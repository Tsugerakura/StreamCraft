/*      */ package pro.gravit.repackage.io.netty.buffer;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.GatheringByteChannel;
/*      */ import java.nio.channels.ScatteringByteChannel;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.ConcurrentModificationException;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.NoSuchElementException;
/*      */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*      */ import pro.gravit.repackage.io.netty.util.IllegalReferenceCountException;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*      */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.RecyclableArrayList;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CompositeByteBuf
/*      */   extends AbstractReferenceCountedByteBuf
/*      */   implements Iterable<ByteBuf>
/*      */ {
/*   51 */   private static final ByteBuffer EMPTY_NIO_BUFFER = Unpooled.EMPTY_BUFFER.nioBuffer();
/*   52 */   private static final Iterator<ByteBuf> EMPTY_ITERATOR = Collections.<ByteBuf>emptyList().iterator();
/*      */   
/*      */   private final ByteBufAllocator alloc;
/*      */   
/*      */   private final boolean direct;
/*      */   
/*      */   private final int maxNumComponents;
/*      */   private int componentCount;
/*      */   private Component[] components;
/*      */   private boolean freed;
/*      */   
/*      */   private CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, int initSize) {
/*   64 */     super(2147483647);
/*      */     
/*   66 */     this.alloc = (ByteBufAllocator)ObjectUtil.checkNotNull(alloc, "alloc");
/*   67 */     if (maxNumComponents < 1) {
/*   68 */       throw new IllegalArgumentException("maxNumComponents: " + maxNumComponents + " (expected: >= 1)");
/*      */     }
/*      */ 
/*      */     
/*   72 */     this.direct = direct;
/*   73 */     this.maxNumComponents = maxNumComponents;
/*   74 */     this.components = newCompArray(initSize, maxNumComponents);
/*      */   }
/*      */   
/*      */   public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents) {
/*   78 */     this(alloc, direct, maxNumComponents, 0);
/*      */   }
/*      */   
/*      */   public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteBuf... buffers) {
/*   82 */     this(alloc, direct, maxNumComponents, buffers, 0);
/*      */   }
/*      */ 
/*      */   
/*      */   CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteBuf[] buffers, int offset) {
/*   87 */     this(alloc, direct, maxNumComponents, buffers.length - offset);
/*      */     
/*   89 */     addComponents0(false, 0, buffers, offset);
/*   90 */     consolidateIfNeeded();
/*   91 */     setIndex0(0, capacity());
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, Iterable<ByteBuf> buffers) {
/*   96 */     this(alloc, direct, maxNumComponents, (buffers instanceof Collection) ? ((Collection)buffers)
/*   97 */         .size() : 0);
/*      */     
/*   99 */     addComponents(false, 0, buffers);
/*  100 */     setIndex(0, capacity());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  109 */   static final ByteWrapper<byte[]> BYTE_ARRAY_WRAPPER = new ByteWrapper<byte[]>()
/*      */     {
/*      */       public ByteBuf wrap(byte[] bytes) {
/*  112 */         return Unpooled.wrappedBuffer(bytes);
/*      */       }
/*      */       
/*      */       public boolean isEmpty(byte[] bytes) {
/*  116 */         return (bytes.length == 0);
/*      */       }
/*      */     };
/*      */   
/*  120 */   static final ByteWrapper<ByteBuffer> BYTE_BUFFER_WRAPPER = new ByteWrapper<ByteBuffer>()
/*      */     {
/*      */       public ByteBuf wrap(ByteBuffer bytes) {
/*  123 */         return Unpooled.wrappedBuffer(bytes);
/*      */       }
/*      */       
/*      */       public boolean isEmpty(ByteBuffer bytes) {
/*  127 */         return !bytes.hasRemaining();
/*      */       }
/*      */     };
/*      */   private Component lastAccessed;
/*      */   
/*      */   <T> CompositeByteBuf(ByteBufAllocator alloc, boolean direct, int maxNumComponents, ByteWrapper<T> wrapper, T[] buffers, int offset) {
/*  133 */     this(alloc, direct, maxNumComponents, buffers.length - offset);
/*      */     
/*  135 */     addComponents0(false, 0, wrapper, buffers, offset);
/*  136 */     consolidateIfNeeded();
/*  137 */     setIndex(0, capacity());
/*      */   }
/*      */   
/*      */   private static Component[] newCompArray(int initComponents, int maxNumComponents) {
/*  141 */     int capacityGuess = Math.min(16, maxNumComponents);
/*  142 */     return new Component[Math.max(initComponents, capacityGuess)];
/*      */   }
/*      */ 
/*      */   
/*      */   CompositeByteBuf(ByteBufAllocator alloc) {
/*  147 */     super(2147483647);
/*  148 */     this.alloc = alloc;
/*  149 */     this.direct = false;
/*  150 */     this.maxNumComponents = 0;
/*  151 */     this.components = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(ByteBuf buffer) {
/*  165 */     return addComponent(false, buffer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(ByteBuf... buffers) {
/*  180 */     return addComponents(false, buffers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(Iterable<ByteBuf> buffers) {
/*  195 */     return addComponents(false, buffers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(int cIndex, ByteBuf buffer) {
/*  210 */     return addComponent(false, cIndex, buffer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(boolean increaseWriterIndex, ByteBuf buffer) {
/*  222 */     return addComponent(increaseWriterIndex, this.componentCount, buffer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(boolean increaseWriterIndex, ByteBuf... buffers) {
/*  235 */     ObjectUtil.checkNotNull(buffers, "buffers");
/*  236 */     addComponents0(increaseWriterIndex, this.componentCount, buffers, 0);
/*  237 */     consolidateIfNeeded();
/*  238 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(boolean increaseWriterIndex, Iterable<ByteBuf> buffers) {
/*  251 */     return addComponents(increaseWriterIndex, this.componentCount, buffers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponent(boolean increaseWriterIndex, int cIndex, ByteBuf buffer) {
/*  264 */     ObjectUtil.checkNotNull(buffer, "buffer");
/*  265 */     addComponent0(increaseWriterIndex, cIndex, buffer);
/*  266 */     consolidateIfNeeded();
/*  267 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int addComponent0(boolean increaseWriterIndex, int cIndex, ByteBuf buffer) {
/*  274 */     assert buffer != null;
/*  275 */     boolean wasAdded = false;
/*      */     try {
/*  277 */       checkComponentIndex(cIndex);
/*      */ 
/*      */       
/*  280 */       Component c = newComponent(ensureAccessible(buffer), 0);
/*  281 */       int readableBytes = c.length();
/*      */       
/*  283 */       addComp(cIndex, c);
/*  284 */       wasAdded = true;
/*  285 */       if (readableBytes > 0 && cIndex < this.componentCount - 1) {
/*  286 */         updateComponentOffsets(cIndex);
/*  287 */       } else if (cIndex > 0) {
/*  288 */         c.reposition((this.components[cIndex - 1]).endOffset);
/*      */       } 
/*  290 */       if (increaseWriterIndex) {
/*  291 */         this.writerIndex += readableBytes;
/*      */       }
/*  293 */       return cIndex;
/*      */     } finally {
/*  295 */       if (!wasAdded) {
/*  296 */         buffer.release();
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   private static ByteBuf ensureAccessible(ByteBuf buf) {
/*  302 */     if (checkAccessible && !buf.isAccessible()) {
/*  303 */       throw new IllegalReferenceCountException(0);
/*      */     }
/*  305 */     return buf;
/*      */   }
/*      */ 
/*      */   
/*      */   private Component newComponent(ByteBuf buf, int offset) {
/*  310 */     int srcIndex = buf.readerIndex();
/*  311 */     int len = buf.readableBytes();
/*      */ 
/*      */     
/*  314 */     ByteBuf unwrapped = buf;
/*  315 */     int unwrappedIndex = srcIndex;
/*  316 */     while (unwrapped instanceof WrappedByteBuf || unwrapped instanceof SwappedByteBuf) {
/*  317 */       unwrapped = unwrapped.unwrap();
/*      */     }
/*      */ 
/*      */     
/*  321 */     if (unwrapped instanceof AbstractUnpooledSlicedByteBuf) {
/*  322 */       unwrappedIndex += ((AbstractUnpooledSlicedByteBuf)unwrapped).idx(0);
/*  323 */       unwrapped = unwrapped.unwrap();
/*  324 */     } else if (unwrapped instanceof PooledSlicedByteBuf) {
/*  325 */       unwrappedIndex += ((PooledSlicedByteBuf)unwrapped).adjustment;
/*  326 */       unwrapped = unwrapped.unwrap();
/*  327 */     } else if (unwrapped instanceof DuplicatedByteBuf || unwrapped instanceof PooledDuplicatedByteBuf) {
/*  328 */       unwrapped = unwrapped.unwrap();
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  333 */     ByteBuf slice = (buf.capacity() == len) ? buf : null;
/*      */     
/*  335 */     return new Component(buf.order(ByteOrder.BIG_ENDIAN), srcIndex, unwrapped
/*  336 */         .order(ByteOrder.BIG_ENDIAN), unwrappedIndex, offset, len, slice);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(int cIndex, ByteBuf... buffers) {
/*  354 */     ObjectUtil.checkNotNull(buffers, "buffers");
/*  355 */     addComponents0(false, cIndex, buffers, 0);
/*  356 */     consolidateIfNeeded();
/*  357 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   private CompositeByteBuf addComponents0(boolean increaseWriterIndex, int cIndex, ByteBuf[] buffers, int arrOffset) {
/*  362 */     int len = buffers.length, count = len - arrOffset;
/*      */     
/*  364 */     int ci = Integer.MAX_VALUE;
/*      */     try {
/*  366 */       checkComponentIndex(cIndex);
/*  367 */       shiftComps(cIndex, count);
/*  368 */       int nextOffset = (cIndex > 0) ? (this.components[cIndex - 1]).endOffset : 0;
/*  369 */       for (ci = cIndex; arrOffset < len; arrOffset++, ci++) {
/*  370 */         ByteBuf b = buffers[arrOffset];
/*  371 */         if (b == null) {
/*      */           break;
/*      */         }
/*  374 */         Component c = newComponent(ensureAccessible(b), nextOffset);
/*  375 */         this.components[ci] = c;
/*  376 */         nextOffset = c.endOffset;
/*      */       } 
/*  378 */       return this;
/*      */     } finally {
/*      */       
/*  381 */       if (ci < this.componentCount) {
/*  382 */         if (ci < cIndex + count) {
/*      */           
/*  384 */           removeCompRange(ci, cIndex + count);
/*  385 */           for (; arrOffset < len; arrOffset++) {
/*  386 */             ReferenceCountUtil.safeRelease(buffers[arrOffset]);
/*      */           }
/*      */         } 
/*  389 */         updateComponentOffsets(ci);
/*      */       } 
/*  391 */       if (increaseWriterIndex && ci > cIndex && ci <= this.componentCount) {
/*  392 */         this.writerIndex += (this.components[ci - 1]).endOffset - (this.components[cIndex]).offset;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private <T> int addComponents0(boolean increaseWriterIndex, int cIndex, ByteWrapper<T> wrapper, T[] buffers, int offset) {
/*  399 */     checkComponentIndex(cIndex);
/*      */ 
/*      */     
/*  402 */     for (int i = offset, len = buffers.length; i < len; i++) {
/*  403 */       T b = buffers[i];
/*  404 */       if (b == null) {
/*      */         break;
/*      */       }
/*  407 */       if (!wrapper.isEmpty(b)) {
/*  408 */         cIndex = addComponent0(increaseWriterIndex, cIndex, wrapper.wrap(b)) + 1;
/*  409 */         int size = this.componentCount;
/*  410 */         if (cIndex > size) {
/*  411 */           cIndex = size;
/*      */         }
/*      */       } 
/*      */     } 
/*  415 */     return cIndex;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addComponents(int cIndex, Iterable<ByteBuf> buffers) {
/*  432 */     return addComponents(false, cIndex, buffers);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf addFlattenedComponents(boolean increaseWriterIndex, ByteBuf buffer) {
/*  446 */     ObjectUtil.checkNotNull(buffer, "buffer");
/*  447 */     int ridx = buffer.readerIndex();
/*  448 */     int widx = buffer.writerIndex();
/*  449 */     if (ridx == widx) {
/*  450 */       buffer.release();
/*  451 */       return this;
/*      */     } 
/*  453 */     if (!(buffer instanceof CompositeByteBuf)) {
/*  454 */       addComponent0(increaseWriterIndex, this.componentCount, buffer);
/*  455 */       consolidateIfNeeded();
/*  456 */       return this;
/*      */     } 
/*  458 */     CompositeByteBuf from = (CompositeByteBuf)buffer;
/*  459 */     from.checkIndex(ridx, widx - ridx);
/*  460 */     Component[] fromComponents = from.components;
/*  461 */     int compCountBefore = this.componentCount;
/*  462 */     int writerIndexBefore = this.writerIndex;
/*      */     try {
/*  464 */       for (int cidx = from.toComponentIndex0(ridx), newOffset = capacity();; cidx++) {
/*  465 */         Component component = fromComponents[cidx];
/*  466 */         int compOffset = component.offset;
/*  467 */         int fromIdx = Math.max(ridx, compOffset);
/*  468 */         int toIdx = Math.min(widx, component.endOffset);
/*  469 */         int len = toIdx - fromIdx;
/*  470 */         if (len > 0) {
/*  471 */           addComp(this.componentCount, new Component(component.srcBuf
/*  472 */                 .retain(), component.srcIdx(fromIdx), component.buf, component
/*  473 */                 .idx(fromIdx), newOffset, len, null));
/*      */         }
/*  475 */         if (widx == toIdx) {
/*      */           break;
/*      */         }
/*  478 */         newOffset += len;
/*      */       } 
/*  480 */       if (increaseWriterIndex) {
/*  481 */         this.writerIndex = writerIndexBefore + widx - ridx;
/*      */       }
/*  483 */       consolidateIfNeeded();
/*  484 */       buffer.release();
/*  485 */       buffer = null;
/*  486 */       return this;
/*      */     } finally {
/*  488 */       if (buffer != null) {
/*      */         
/*  490 */         if (increaseWriterIndex) {
/*  491 */           this.writerIndex = writerIndexBefore;
/*      */         }
/*  493 */         for (int cidx = this.componentCount - 1; cidx >= compCountBefore; cidx--) {
/*  494 */           this.components[cidx].free();
/*  495 */           removeComp(cidx);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private CompositeByteBuf addComponents(boolean increaseIndex, int cIndex, Iterable<ByteBuf> buffers) {
/*  505 */     if (buffers instanceof ByteBuf)
/*      */     {
/*  507 */       return addComponent(increaseIndex, cIndex, (ByteBuf)buffers);
/*      */     }
/*  509 */     ObjectUtil.checkNotNull(buffers, "buffers");
/*  510 */     Iterator<ByteBuf> it = buffers.iterator();
/*      */     try {
/*  512 */       checkComponentIndex(cIndex);
/*      */ 
/*      */       
/*  515 */       while (it.hasNext()) {
/*  516 */         ByteBuf b = it.next();
/*  517 */         if (b == null) {
/*      */           break;
/*      */         }
/*  520 */         cIndex = addComponent0(increaseIndex, cIndex, b) + 1;
/*  521 */         cIndex = Math.min(cIndex, this.componentCount);
/*      */       } 
/*      */     } finally {
/*  524 */       while (it.hasNext()) {
/*  525 */         ReferenceCountUtil.safeRelease(it.next());
/*      */       }
/*      */     } 
/*  528 */     consolidateIfNeeded();
/*  529 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void consolidateIfNeeded() {
/*  539 */     int size = this.componentCount;
/*  540 */     if (size > this.maxNumComponents) {
/*  541 */       consolidate0(0, size);
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkComponentIndex(int cIndex) {
/*  546 */     ensureAccessible();
/*  547 */     if (cIndex < 0 || cIndex > this.componentCount)
/*  548 */       throw new IndexOutOfBoundsException(String.format("cIndex: %d (expected: >= 0 && <= numComponents(%d))", new Object[] {
/*      */               
/*  550 */               Integer.valueOf(cIndex), Integer.valueOf(this.componentCount)
/*      */             })); 
/*      */   }
/*      */   
/*      */   private void checkComponentIndex(int cIndex, int numComponents) {
/*  555 */     ensureAccessible();
/*  556 */     if (cIndex < 0 || cIndex + numComponents > this.componentCount)
/*  557 */       throw new IndexOutOfBoundsException(String.format("cIndex: %d, numComponents: %d (expected: cIndex >= 0 && cIndex + numComponents <= totalNumComponents(%d))", new Object[] {
/*      */ 
/*      */               
/*  560 */               Integer.valueOf(cIndex), Integer.valueOf(numComponents), Integer.valueOf(this.componentCount)
/*      */             })); 
/*      */   }
/*      */   
/*      */   private void updateComponentOffsets(int cIndex) {
/*  565 */     int size = this.componentCount;
/*  566 */     if (size <= cIndex) {
/*      */       return;
/*      */     }
/*      */     
/*  570 */     int nextIndex = (cIndex > 0) ? (this.components[cIndex - 1]).endOffset : 0;
/*  571 */     for (; cIndex < size; cIndex++) {
/*  572 */       Component c = this.components[cIndex];
/*  573 */       c.reposition(nextIndex);
/*  574 */       nextIndex = c.endOffset;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf removeComponent(int cIndex) {
/*  584 */     checkComponentIndex(cIndex);
/*  585 */     Component comp = this.components[cIndex];
/*  586 */     if (this.lastAccessed == comp) {
/*  587 */       this.lastAccessed = null;
/*      */     }
/*  589 */     comp.free();
/*  590 */     removeComp(cIndex);
/*  591 */     if (comp.length() > 0)
/*      */     {
/*  593 */       updateComponentOffsets(cIndex);
/*      */     }
/*  595 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf removeComponents(int cIndex, int numComponents) {
/*  605 */     checkComponentIndex(cIndex, numComponents);
/*      */     
/*  607 */     if (numComponents == 0) {
/*  608 */       return this;
/*      */     }
/*  610 */     int endIndex = cIndex + numComponents;
/*  611 */     boolean needsUpdate = false;
/*  612 */     for (int i = cIndex; i < endIndex; i++) {
/*  613 */       Component c = this.components[i];
/*  614 */       if (c.length() > 0) {
/*  615 */         needsUpdate = true;
/*      */       }
/*  617 */       if (this.lastAccessed == c) {
/*  618 */         this.lastAccessed = null;
/*      */       }
/*  620 */       c.free();
/*      */     } 
/*  622 */     removeCompRange(cIndex, endIndex);
/*      */     
/*  624 */     if (needsUpdate)
/*      */     {
/*  626 */       updateComponentOffsets(cIndex);
/*      */     }
/*  628 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public Iterator<ByteBuf> iterator() {
/*  633 */     ensureAccessible();
/*  634 */     return (this.componentCount == 0) ? EMPTY_ITERATOR : new CompositeByteBufIterator();
/*      */   }
/*      */ 
/*      */   
/*      */   protected int forEachByteAsc0(int start, int end, ByteProcessor processor) throws Exception {
/*  639 */     if (end <= start) {
/*  640 */       return -1;
/*      */     }
/*  642 */     for (int i = toComponentIndex0(start), length = end - start; length > 0; i++) {
/*  643 */       Component c = this.components[i];
/*  644 */       if (c.offset != c.endOffset) {
/*      */ 
/*      */         
/*  647 */         ByteBuf s = c.buf;
/*  648 */         int localStart = c.idx(start);
/*  649 */         int localLength = Math.min(length, c.endOffset - start);
/*      */ 
/*      */ 
/*      */         
/*  653 */         int result = (s instanceof AbstractByteBuf) ? ((AbstractByteBuf)s).forEachByteAsc0(localStart, localStart + localLength, processor) : s.forEachByte(localStart, localLength, processor);
/*  654 */         if (result != -1) {
/*  655 */           return result - c.adjustment;
/*      */         }
/*  657 */         start += localLength;
/*  658 */         length -= localLength;
/*      */       } 
/*  660 */     }  return -1;
/*      */   }
/*      */ 
/*      */   
/*      */   protected int forEachByteDesc0(int rStart, int rEnd, ByteProcessor processor) throws Exception {
/*  665 */     if (rEnd > rStart) {
/*  666 */       return -1;
/*      */     }
/*  668 */     for (int i = toComponentIndex0(rStart), length = 1 + rStart - rEnd; length > 0; i--) {
/*  669 */       Component c = this.components[i];
/*  670 */       if (c.offset != c.endOffset) {
/*      */ 
/*      */         
/*  673 */         ByteBuf s = c.buf;
/*  674 */         int localRStart = c.idx(length + rEnd);
/*  675 */         int localLength = Math.min(length, localRStart), localIndex = localRStart - localLength;
/*      */ 
/*      */ 
/*      */         
/*  679 */         int result = (s instanceof AbstractByteBuf) ? ((AbstractByteBuf)s).forEachByteDesc0(localRStart - 1, localIndex, processor) : s.forEachByteDesc(localIndex, localLength, processor);
/*      */         
/*  681 */         if (result != -1) {
/*  682 */           return result - c.adjustment;
/*      */         }
/*  684 */         length -= localLength;
/*      */       } 
/*  686 */     }  return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<ByteBuf> decompose(int offset, int length) {
/*  693 */     checkIndex(offset, length);
/*  694 */     if (length == 0) {
/*  695 */       return Collections.emptyList();
/*      */     }
/*      */     
/*  698 */     int componentId = toComponentIndex0(offset);
/*  699 */     int bytesToSlice = length;
/*      */     
/*  701 */     Component firstC = this.components[componentId];
/*      */     
/*  703 */     ByteBuf slice = firstC.buf.slice(firstC.idx(offset), Math.min(firstC.endOffset - offset, bytesToSlice));
/*  704 */     bytesToSlice -= slice.readableBytes();
/*      */     
/*  706 */     if (bytesToSlice == 0) {
/*  707 */       return Collections.singletonList(slice);
/*      */     }
/*      */     
/*  710 */     List<ByteBuf> sliceList = new ArrayList<ByteBuf>(this.componentCount - componentId);
/*  711 */     sliceList.add(slice);
/*      */ 
/*      */     
/*      */     do {
/*  715 */       Component component = this.components[++componentId];
/*  716 */       slice = component.buf.slice(component.idx(component.offset), Math.min(component.length(), bytesToSlice));
/*  717 */       bytesToSlice -= slice.readableBytes();
/*  718 */       sliceList.add(slice);
/*  719 */     } while (bytesToSlice > 0);
/*      */     
/*  721 */     return sliceList;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isDirect() {
/*  726 */     int size = this.componentCount;
/*  727 */     if (size == 0) {
/*  728 */       return false;
/*      */     }
/*  730 */     for (int i = 0; i < size; i++) {
/*  731 */       if (!(this.components[i]).buf.isDirect()) {
/*  732 */         return false;
/*      */       }
/*      */     } 
/*  735 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasArray() {
/*  740 */     switch (this.componentCount) {
/*      */       case 0:
/*  742 */         return true;
/*      */       case 1:
/*  744 */         return (this.components[0]).buf.hasArray();
/*      */     } 
/*  746 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] array() {
/*  752 */     switch (this.componentCount) {
/*      */       case 0:
/*  754 */         return EmptyArrays.EMPTY_BYTES;
/*      */       case 1:
/*  756 */         return (this.components[0]).buf.array();
/*      */     } 
/*  758 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */   
/*      */   public int arrayOffset() {
/*      */     Component c;
/*  764 */     switch (this.componentCount) {
/*      */       case 0:
/*  766 */         return 0;
/*      */       case 1:
/*  768 */         c = this.components[0];
/*  769 */         return c.idx(c.buf.arrayOffset());
/*      */     } 
/*  771 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasMemoryAddress() {
/*  777 */     switch (this.componentCount) {
/*      */       case 0:
/*  779 */         return Unpooled.EMPTY_BUFFER.hasMemoryAddress();
/*      */       case 1:
/*  781 */         return (this.components[0]).buf.hasMemoryAddress();
/*      */     } 
/*  783 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public long memoryAddress() {
/*      */     Component c;
/*  789 */     switch (this.componentCount) {
/*      */       case 0:
/*  791 */         return Unpooled.EMPTY_BUFFER.memoryAddress();
/*      */       case 1:
/*  793 */         c = this.components[0];
/*  794 */         return c.buf.memoryAddress() + c.adjustment;
/*      */     } 
/*  796 */     throw new UnsupportedOperationException();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int capacity() {
/*  802 */     int size = this.componentCount;
/*  803 */     return (size > 0) ? (this.components[size - 1]).endOffset : 0;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf capacity(int newCapacity) {
/*  808 */     checkNewCapacity(newCapacity);
/*      */     
/*  810 */     int size = this.componentCount, oldCapacity = capacity();
/*  811 */     if (newCapacity > oldCapacity) {
/*  812 */       int paddingLength = newCapacity - oldCapacity;
/*  813 */       ByteBuf padding = allocBuffer(paddingLength).setIndex(0, paddingLength);
/*  814 */       addComponent0(false, size, padding);
/*  815 */       if (this.componentCount >= this.maxNumComponents)
/*      */       {
/*      */         
/*  818 */         consolidateIfNeeded();
/*      */       }
/*  820 */     } else if (newCapacity < oldCapacity) {
/*  821 */       this.lastAccessed = null;
/*  822 */       int i = size - 1;
/*  823 */       for (int bytesToTrim = oldCapacity - newCapacity; i >= 0; i--) {
/*  824 */         Component c = this.components[i];
/*  825 */         int cLength = c.length();
/*  826 */         if (bytesToTrim < cLength) {
/*      */           
/*  828 */           c.endOffset -= bytesToTrim;
/*  829 */           ByteBuf slice = c.slice;
/*  830 */           if (slice != null)
/*      */           {
/*      */             
/*  833 */             c.slice = slice.slice(0, c.length());
/*      */           }
/*      */           break;
/*      */         } 
/*  837 */         c.free();
/*  838 */         bytesToTrim -= cLength;
/*      */       } 
/*  840 */       removeCompRange(i + 1, size);
/*      */       
/*  842 */       if (readerIndex() > newCapacity) {
/*  843 */         setIndex0(newCapacity, newCapacity);
/*  844 */       } else if (this.writerIndex > newCapacity) {
/*  845 */         this.writerIndex = newCapacity;
/*      */       } 
/*      */     } 
/*  848 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBufAllocator alloc() {
/*  853 */     return this.alloc;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteOrder order() {
/*  858 */     return ByteOrder.BIG_ENDIAN;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int numComponents() {
/*  865 */     return this.componentCount;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int maxNumComponents() {
/*  872 */     return this.maxNumComponents;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int toComponentIndex(int offset) {
/*  879 */     checkIndex(offset);
/*  880 */     return toComponentIndex0(offset);
/*      */   }
/*      */   
/*      */   private int toComponentIndex0(int offset) {
/*  884 */     int size = this.componentCount;
/*  885 */     if (offset == 0) {
/*  886 */       for (int i = 0; i < size; i++) {
/*  887 */         if ((this.components[i]).endOffset > 0) {
/*  888 */           return i;
/*      */         }
/*      */       } 
/*      */     }
/*  892 */     if (size <= 2) {
/*  893 */       return (size == 1 || offset < (this.components[0]).endOffset) ? 0 : 1;
/*      */     }
/*  895 */     for (int low = 0, high = size; low <= high; ) {
/*  896 */       int mid = low + high >>> 1;
/*  897 */       Component c = this.components[mid];
/*  898 */       if (offset >= c.endOffset) {
/*  899 */         low = mid + 1; continue;
/*  900 */       }  if (offset < c.offset) {
/*  901 */         high = mid - 1; continue;
/*      */       } 
/*  903 */       return mid;
/*      */     } 
/*      */ 
/*      */     
/*  907 */     throw new Error("should not reach here");
/*      */   }
/*      */   
/*      */   public int toByteIndex(int cIndex) {
/*  911 */     checkComponentIndex(cIndex);
/*  912 */     return (this.components[cIndex]).offset;
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getByte(int index) {
/*  917 */     Component c = findComponent(index);
/*  918 */     return c.buf.getByte(c.idx(index));
/*      */   }
/*      */ 
/*      */   
/*      */   protected byte _getByte(int index) {
/*  923 */     Component c = findComponent0(index);
/*  924 */     return c.buf.getByte(c.idx(index));
/*      */   }
/*      */ 
/*      */   
/*      */   protected short _getShort(int index) {
/*  929 */     Component c = findComponent0(index);
/*  930 */     if (index + 2 <= c.endOffset)
/*  931 */       return c.buf.getShort(c.idx(index)); 
/*  932 */     if (order() == ByteOrder.BIG_ENDIAN) {
/*  933 */       return (short)((_getByte(index) & 0xFF) << 8 | _getByte(index + 1) & 0xFF);
/*      */     }
/*  935 */     return (short)(_getByte(index) & 0xFF | (_getByte(index + 1) & 0xFF) << 8);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected short _getShortLE(int index) {
/*  941 */     Component c = findComponent0(index);
/*  942 */     if (index + 2 <= c.endOffset)
/*  943 */       return c.buf.getShortLE(c.idx(index)); 
/*  944 */     if (order() == ByteOrder.BIG_ENDIAN) {
/*  945 */       return (short)(_getByte(index) & 0xFF | (_getByte(index + 1) & 0xFF) << 8);
/*      */     }
/*  947 */     return (short)((_getByte(index) & 0xFF) << 8 | _getByte(index + 1) & 0xFF);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _getUnsignedMedium(int index) {
/*  953 */     Component c = findComponent0(index);
/*  954 */     if (index + 3 <= c.endOffset)
/*  955 */       return c.buf.getUnsignedMedium(c.idx(index)); 
/*  956 */     if (order() == ByteOrder.BIG_ENDIAN) {
/*  957 */       return (_getShort(index) & 0xFFFF) << 8 | _getByte(index + 2) & 0xFF;
/*      */     }
/*  959 */     return _getShort(index) & 0xFFFF | (_getByte(index + 2) & 0xFF) << 16;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _getUnsignedMediumLE(int index) {
/*  965 */     Component c = findComponent0(index);
/*  966 */     if (index + 3 <= c.endOffset)
/*  967 */       return c.buf.getUnsignedMediumLE(c.idx(index)); 
/*  968 */     if (order() == ByteOrder.BIG_ENDIAN) {
/*  969 */       return _getShortLE(index) & 0xFFFF | (_getByte(index + 2) & 0xFF) << 16;
/*      */     }
/*  971 */     return (_getShortLE(index) & 0xFFFF) << 8 | _getByte(index + 2) & 0xFF;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _getInt(int index) {
/*  977 */     Component c = findComponent0(index);
/*  978 */     if (index + 4 <= c.endOffset)
/*  979 */       return c.buf.getInt(c.idx(index)); 
/*  980 */     if (order() == ByteOrder.BIG_ENDIAN) {
/*  981 */       return (_getShort(index) & 0xFFFF) << 16 | _getShort(index + 2) & 0xFFFF;
/*      */     }
/*  983 */     return _getShort(index) & 0xFFFF | (_getShort(index + 2) & 0xFFFF) << 16;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected int _getIntLE(int index) {
/*  989 */     Component c = findComponent0(index);
/*  990 */     if (index + 4 <= c.endOffset)
/*  991 */       return c.buf.getIntLE(c.idx(index)); 
/*  992 */     if (order() == ByteOrder.BIG_ENDIAN) {
/*  993 */       return _getShortLE(index) & 0xFFFF | (_getShortLE(index + 2) & 0xFFFF) << 16;
/*      */     }
/*  995 */     return (_getShortLE(index) & 0xFFFF) << 16 | _getShortLE(index + 2) & 0xFFFF;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected long _getLong(int index) {
/* 1001 */     Component c = findComponent0(index);
/* 1002 */     if (index + 8 <= c.endOffset)
/* 1003 */       return c.buf.getLong(c.idx(index)); 
/* 1004 */     if (order() == ByteOrder.BIG_ENDIAN) {
/* 1005 */       return (_getInt(index) & 0xFFFFFFFFL) << 32L | _getInt(index + 4) & 0xFFFFFFFFL;
/*      */     }
/* 1007 */     return _getInt(index) & 0xFFFFFFFFL | (_getInt(index + 4) & 0xFFFFFFFFL) << 32L;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected long _getLongLE(int index) {
/* 1013 */     Component c = findComponent0(index);
/* 1014 */     if (index + 8 <= c.endOffset)
/* 1015 */       return c.buf.getLongLE(c.idx(index)); 
/* 1016 */     if (order() == ByteOrder.BIG_ENDIAN) {
/* 1017 */       return _getIntLE(index) & 0xFFFFFFFFL | (_getIntLE(index + 4) & 0xFFFFFFFFL) << 32L;
/*      */     }
/* 1019 */     return (_getIntLE(index) & 0xFFFFFFFFL) << 32L | _getIntLE(index + 4) & 0xFFFFFFFFL;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
/* 1025 */     checkDstIndex(index, length, dstIndex, dst.length);
/* 1026 */     if (length == 0) {
/* 1027 */       return this;
/*      */     }
/*      */     
/* 1030 */     int i = toComponentIndex0(index);
/* 1031 */     while (length > 0) {
/* 1032 */       Component c = this.components[i];
/* 1033 */       int localLength = Math.min(length, c.endOffset - index);
/* 1034 */       c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
/* 1035 */       index += localLength;
/* 1036 */       dstIndex += localLength;
/* 1037 */       length -= localLength;
/* 1038 */       i++;
/*      */     } 
/* 1040 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuffer dst) {
/* 1045 */     int limit = dst.limit();
/* 1046 */     int length = dst.remaining();
/*      */     
/* 1048 */     checkIndex(index, length);
/* 1049 */     if (length == 0) {
/* 1050 */       return this;
/*      */     }
/*      */     
/* 1053 */     int i = toComponentIndex0(index);
/*      */     try {
/* 1055 */       while (length > 0) {
/* 1056 */         Component c = this.components[i];
/* 1057 */         int localLength = Math.min(length, c.endOffset - index);
/* 1058 */         dst.limit(dst.position() + localLength);
/* 1059 */         c.buf.getBytes(c.idx(index), dst);
/* 1060 */         index += localLength;
/* 1061 */         length -= localLength;
/* 1062 */         i++;
/*      */       } 
/*      */     } finally {
/* 1065 */       dst.limit(limit);
/*      */     } 
/* 1067 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
/* 1072 */     checkDstIndex(index, length, dstIndex, dst.capacity());
/* 1073 */     if (length == 0) {
/* 1074 */       return this;
/*      */     }
/*      */     
/* 1077 */     int i = toComponentIndex0(index);
/* 1078 */     while (length > 0) {
/* 1079 */       Component c = this.components[i];
/* 1080 */       int localLength = Math.min(length, c.endOffset - index);
/* 1081 */       c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
/* 1082 */       index += localLength;
/* 1083 */       dstIndex += localLength;
/* 1084 */       length -= localLength;
/* 1085 */       i++;
/*      */     } 
/* 1087 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
/* 1093 */     int count = nioBufferCount();
/* 1094 */     if (count == 1) {
/* 1095 */       return out.write(internalNioBuffer(index, length));
/*      */     }
/* 1097 */     long writtenBytes = out.write(nioBuffers(index, length));
/* 1098 */     if (writtenBytes > 2147483647L) {
/* 1099 */       return Integer.MAX_VALUE;
/*      */     }
/* 1101 */     return (int)writtenBytes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
/* 1109 */     int count = nioBufferCount();
/* 1110 */     if (count == 1) {
/* 1111 */       return out.write(internalNioBuffer(index, length), position);
/*      */     }
/* 1113 */     long writtenBytes = 0L;
/* 1114 */     for (ByteBuffer buf : nioBuffers(index, length)) {
/* 1115 */       writtenBytes += out.write(buf, position + writtenBytes);
/*      */     }
/* 1117 */     if (writtenBytes > 2147483647L) {
/* 1118 */       return Integer.MAX_VALUE;
/*      */     }
/* 1120 */     return (int)writtenBytes;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
/* 1126 */     checkIndex(index, length);
/* 1127 */     if (length == 0) {
/* 1128 */       return this;
/*      */     }
/*      */     
/* 1131 */     int i = toComponentIndex0(index);
/* 1132 */     while (length > 0) {
/* 1133 */       Component c = this.components[i];
/* 1134 */       int localLength = Math.min(length, c.endOffset - index);
/* 1135 */       c.buf.getBytes(c.idx(index), out, localLength);
/* 1136 */       index += localLength;
/* 1137 */       length -= localLength;
/* 1138 */       i++;
/*      */     } 
/* 1140 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setByte(int index, int value) {
/* 1145 */     Component c = findComponent(index);
/* 1146 */     c.buf.setByte(c.idx(index), value);
/* 1147 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _setByte(int index, int value) {
/* 1152 */     Component c = findComponent0(index);
/* 1153 */     c.buf.setByte(c.idx(index), value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setShort(int index, int value) {
/* 1158 */     checkIndex(index, 2);
/* 1159 */     _setShort(index, value);
/* 1160 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _setShort(int index, int value) {
/* 1165 */     Component c = findComponent0(index);
/* 1166 */     if (index + 2 <= c.endOffset) {
/* 1167 */       c.buf.setShort(c.idx(index), value);
/* 1168 */     } else if (order() == ByteOrder.BIG_ENDIAN) {
/* 1169 */       _setByte(index, (byte)(value >>> 8));
/* 1170 */       _setByte(index + 1, (byte)value);
/*      */     } else {
/* 1172 */       _setByte(index, (byte)value);
/* 1173 */       _setByte(index + 1, (byte)(value >>> 8));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _setShortLE(int index, int value) {
/* 1179 */     Component c = findComponent0(index);
/* 1180 */     if (index + 2 <= c.endOffset) {
/* 1181 */       c.buf.setShortLE(c.idx(index), value);
/* 1182 */     } else if (order() == ByteOrder.BIG_ENDIAN) {
/* 1183 */       _setByte(index, (byte)value);
/* 1184 */       _setByte(index + 1, (byte)(value >>> 8));
/*      */     } else {
/* 1186 */       _setByte(index, (byte)(value >>> 8));
/* 1187 */       _setByte(index + 1, (byte)value);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setMedium(int index, int value) {
/* 1193 */     checkIndex(index, 3);
/* 1194 */     _setMedium(index, value);
/* 1195 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _setMedium(int index, int value) {
/* 1200 */     Component c = findComponent0(index);
/* 1201 */     if (index + 3 <= c.endOffset) {
/* 1202 */       c.buf.setMedium(c.idx(index), value);
/* 1203 */     } else if (order() == ByteOrder.BIG_ENDIAN) {
/* 1204 */       _setShort(index, (short)(value >> 8));
/* 1205 */       _setByte(index + 2, (byte)value);
/*      */     } else {
/* 1207 */       _setShort(index, (short)value);
/* 1208 */       _setByte(index + 2, (byte)(value >>> 16));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _setMediumLE(int index, int value) {
/* 1214 */     Component c = findComponent0(index);
/* 1215 */     if (index + 3 <= c.endOffset) {
/* 1216 */       c.buf.setMediumLE(c.idx(index), value);
/* 1217 */     } else if (order() == ByteOrder.BIG_ENDIAN) {
/* 1218 */       _setShortLE(index, (short)value);
/* 1219 */       _setByte(index + 2, (byte)(value >>> 16));
/*      */     } else {
/* 1221 */       _setShortLE(index, (short)(value >> 8));
/* 1222 */       _setByte(index + 2, (byte)value);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setInt(int index, int value) {
/* 1228 */     checkIndex(index, 4);
/* 1229 */     _setInt(index, value);
/* 1230 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _setInt(int index, int value) {
/* 1235 */     Component c = findComponent0(index);
/* 1236 */     if (index + 4 <= c.endOffset) {
/* 1237 */       c.buf.setInt(c.idx(index), value);
/* 1238 */     } else if (order() == ByteOrder.BIG_ENDIAN) {
/* 1239 */       _setShort(index, (short)(value >>> 16));
/* 1240 */       _setShort(index + 2, (short)value);
/*      */     } else {
/* 1242 */       _setShort(index, (short)value);
/* 1243 */       _setShort(index + 2, (short)(value >>> 16));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _setIntLE(int index, int value) {
/* 1249 */     Component c = findComponent0(index);
/* 1250 */     if (index + 4 <= c.endOffset) {
/* 1251 */       c.buf.setIntLE(c.idx(index), value);
/* 1252 */     } else if (order() == ByteOrder.BIG_ENDIAN) {
/* 1253 */       _setShortLE(index, (short)value);
/* 1254 */       _setShortLE(index + 2, (short)(value >>> 16));
/*      */     } else {
/* 1256 */       _setShortLE(index, (short)(value >>> 16));
/* 1257 */       _setShortLE(index + 2, (short)value);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setLong(int index, long value) {
/* 1263 */     checkIndex(index, 8);
/* 1264 */     _setLong(index, value);
/* 1265 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _setLong(int index, long value) {
/* 1270 */     Component c = findComponent0(index);
/* 1271 */     if (index + 8 <= c.endOffset) {
/* 1272 */       c.buf.setLong(c.idx(index), value);
/* 1273 */     } else if (order() == ByteOrder.BIG_ENDIAN) {
/* 1274 */       _setInt(index, (int)(value >>> 32L));
/* 1275 */       _setInt(index + 4, (int)value);
/*      */     } else {
/* 1277 */       _setInt(index, (int)value);
/* 1278 */       _setInt(index + 4, (int)(value >>> 32L));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected void _setLongLE(int index, long value) {
/* 1284 */     Component c = findComponent0(index);
/* 1285 */     if (index + 8 <= c.endOffset) {
/* 1286 */       c.buf.setLongLE(c.idx(index), value);
/* 1287 */     } else if (order() == ByteOrder.BIG_ENDIAN) {
/* 1288 */       _setIntLE(index, (int)value);
/* 1289 */       _setIntLE(index + 4, (int)(value >>> 32L));
/*      */     } else {
/* 1291 */       _setIntLE(index, (int)(value >>> 32L));
/* 1292 */       _setIntLE(index + 4, (int)value);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
/* 1298 */     checkSrcIndex(index, length, srcIndex, src.length);
/* 1299 */     if (length == 0) {
/* 1300 */       return this;
/*      */     }
/*      */     
/* 1303 */     int i = toComponentIndex0(index);
/* 1304 */     while (length > 0) {
/* 1305 */       Component c = this.components[i];
/* 1306 */       int localLength = Math.min(length, c.endOffset - index);
/* 1307 */       c.buf.setBytes(c.idx(index), src, srcIndex, localLength);
/* 1308 */       index += localLength;
/* 1309 */       srcIndex += localLength;
/* 1310 */       length -= localLength;
/* 1311 */       i++;
/*      */     } 
/* 1313 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuffer src) {
/* 1318 */     int limit = src.limit();
/* 1319 */     int length = src.remaining();
/*      */     
/* 1321 */     checkIndex(index, length);
/* 1322 */     if (length == 0) {
/* 1323 */       return this;
/*      */     }
/*      */     
/* 1326 */     int i = toComponentIndex0(index);
/*      */     try {
/* 1328 */       while (length > 0) {
/* 1329 */         Component c = this.components[i];
/* 1330 */         int localLength = Math.min(length, c.endOffset - index);
/* 1331 */         src.limit(src.position() + localLength);
/* 1332 */         c.buf.setBytes(c.idx(index), src);
/* 1333 */         index += localLength;
/* 1334 */         length -= localLength;
/* 1335 */         i++;
/*      */       } 
/*      */     } finally {
/* 1338 */       src.limit(limit);
/*      */     } 
/* 1340 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
/* 1345 */     checkSrcIndex(index, length, srcIndex, src.capacity());
/* 1346 */     if (length == 0) {
/* 1347 */       return this;
/*      */     }
/*      */     
/* 1350 */     int i = toComponentIndex0(index);
/* 1351 */     while (length > 0) {
/* 1352 */       Component c = this.components[i];
/* 1353 */       int localLength = Math.min(length, c.endOffset - index);
/* 1354 */       c.buf.setBytes(c.idx(index), src, srcIndex, localLength);
/* 1355 */       index += localLength;
/* 1356 */       srcIndex += localLength;
/* 1357 */       length -= localLength;
/* 1358 */       i++;
/*      */     } 
/* 1360 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, InputStream in, int length) throws IOException {
/* 1365 */     checkIndex(index, length);
/* 1366 */     if (length == 0) {
/* 1367 */       return in.read(EmptyArrays.EMPTY_BYTES);
/*      */     }
/*      */     
/* 1370 */     int i = toComponentIndex0(index);
/* 1371 */     int readBytes = 0;
/*      */     do {
/* 1373 */       Component c = this.components[i];
/* 1374 */       int localLength = Math.min(length, c.endOffset - index);
/* 1375 */       if (localLength == 0) {
/*      */         
/* 1377 */         i++;
/*      */       } else {
/*      */         
/* 1380 */         int localReadBytes = c.buf.setBytes(c.idx(index), in, localLength);
/* 1381 */         if (localReadBytes < 0) {
/* 1382 */           if (readBytes == 0) {
/* 1383 */             return -1;
/*      */           }
/*      */           
/*      */           break;
/*      */         } 
/*      */         
/* 1389 */         index += localReadBytes;
/* 1390 */         length -= localReadBytes;
/* 1391 */         readBytes += localReadBytes;
/* 1392 */         if (localReadBytes == localLength)
/* 1393 */           i++; 
/*      */       } 
/* 1395 */     } while (length > 0);
/*      */     
/* 1397 */     return readBytes;
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
/* 1402 */     checkIndex(index, length);
/* 1403 */     if (length == 0) {
/* 1404 */       return in.read(EMPTY_NIO_BUFFER);
/*      */     }
/*      */     
/* 1407 */     int i = toComponentIndex0(index);
/* 1408 */     int readBytes = 0;
/*      */     do {
/* 1410 */       Component c = this.components[i];
/* 1411 */       int localLength = Math.min(length, c.endOffset - index);
/* 1412 */       if (localLength == 0) {
/*      */         
/* 1414 */         i++;
/*      */       } else {
/*      */         
/* 1417 */         int localReadBytes = c.buf.setBytes(c.idx(index), in, localLength);
/*      */         
/* 1419 */         if (localReadBytes == 0) {
/*      */           break;
/*      */         }
/*      */         
/* 1423 */         if (localReadBytes < 0) {
/* 1424 */           if (readBytes == 0) {
/* 1425 */             return -1;
/*      */           }
/*      */           
/*      */           break;
/*      */         } 
/*      */         
/* 1431 */         index += localReadBytes;
/* 1432 */         length -= localReadBytes;
/* 1433 */         readBytes += localReadBytes;
/* 1434 */         if (localReadBytes == localLength)
/* 1435 */           i++; 
/*      */       } 
/* 1437 */     } while (length > 0);
/*      */     
/* 1439 */     return readBytes;
/*      */   }
/*      */ 
/*      */   
/*      */   public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
/* 1444 */     checkIndex(index, length);
/* 1445 */     if (length == 0) {
/* 1446 */       return in.read(EMPTY_NIO_BUFFER, position);
/*      */     }
/*      */     
/* 1449 */     int i = toComponentIndex0(index);
/* 1450 */     int readBytes = 0;
/*      */     do {
/* 1452 */       Component c = this.components[i];
/* 1453 */       int localLength = Math.min(length, c.endOffset - index);
/* 1454 */       if (localLength == 0) {
/*      */         
/* 1456 */         i++;
/*      */       } else {
/*      */         
/* 1459 */         int localReadBytes = c.buf.setBytes(c.idx(index), in, position + readBytes, localLength);
/*      */         
/* 1461 */         if (localReadBytes == 0) {
/*      */           break;
/*      */         }
/*      */         
/* 1465 */         if (localReadBytes < 0) {
/* 1466 */           if (readBytes == 0) {
/* 1467 */             return -1;
/*      */           }
/*      */           
/*      */           break;
/*      */         } 
/*      */         
/* 1473 */         index += localReadBytes;
/* 1474 */         length -= localReadBytes;
/* 1475 */         readBytes += localReadBytes;
/* 1476 */         if (localReadBytes == localLength)
/* 1477 */           i++; 
/*      */       } 
/* 1479 */     } while (length > 0);
/*      */     
/* 1481 */     return readBytes;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf copy(int index, int length) {
/* 1486 */     checkIndex(index, length);
/* 1487 */     ByteBuf dst = allocBuffer(length);
/* 1488 */     if (length != 0) {
/* 1489 */       copyTo(index, length, toComponentIndex0(index), dst);
/*      */     }
/* 1491 */     return dst;
/*      */   }
/*      */   
/*      */   private void copyTo(int index, int length, int componentId, ByteBuf dst) {
/* 1495 */     int dstIndex = 0;
/* 1496 */     int i = componentId;
/*      */     
/* 1498 */     while (length > 0) {
/* 1499 */       Component c = this.components[i];
/* 1500 */       int localLength = Math.min(length, c.endOffset - index);
/* 1501 */       c.buf.getBytes(c.idx(index), dst, dstIndex, localLength);
/* 1502 */       index += localLength;
/* 1503 */       dstIndex += localLength;
/* 1504 */       length -= localLength;
/* 1505 */       i++;
/*      */     } 
/*      */     
/* 1508 */     dst.writerIndex(dst.capacity());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf component(int cIndex) {
/* 1518 */     checkComponentIndex(cIndex);
/* 1519 */     return this.components[cIndex].duplicate();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf componentAtOffset(int offset) {
/* 1529 */     return findComponent(offset).duplicate();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf internalComponent(int cIndex) {
/* 1539 */     checkComponentIndex(cIndex);
/* 1540 */     return this.components[cIndex].slice();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf internalComponentAtOffset(int offset) {
/* 1550 */     return findComponent(offset).slice();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Component findComponent(int offset) {
/* 1557 */     Component la = this.lastAccessed;
/* 1558 */     if (la != null && offset >= la.offset && offset < la.endOffset) {
/* 1559 */       ensureAccessible();
/* 1560 */       return la;
/*      */     } 
/* 1562 */     checkIndex(offset);
/* 1563 */     return findIt(offset);
/*      */   }
/*      */   
/*      */   private Component findComponent0(int offset) {
/* 1567 */     Component la = this.lastAccessed;
/* 1568 */     if (la != null && offset >= la.offset && offset < la.endOffset) {
/* 1569 */       return la;
/*      */     }
/* 1571 */     return findIt(offset);
/*      */   }
/*      */   
/*      */   private Component findIt(int offset) {
/* 1575 */     for (int low = 0, high = this.componentCount; low <= high; ) {
/* 1576 */       int mid = low + high >>> 1;
/* 1577 */       Component c = this.components[mid];
/* 1578 */       if (offset >= c.endOffset) {
/* 1579 */         low = mid + 1; continue;
/* 1580 */       }  if (offset < c.offset) {
/* 1581 */         high = mid - 1; continue;
/*      */       } 
/* 1583 */       this.lastAccessed = c;
/* 1584 */       return c;
/*      */     } 
/*      */ 
/*      */     
/* 1588 */     throw new Error("should not reach here");
/*      */   }
/*      */ 
/*      */   
/*      */   public int nioBufferCount() {
/* 1593 */     int size = this.componentCount;
/* 1594 */     switch (size) {
/*      */       case 0:
/* 1596 */         return 1;
/*      */       case 1:
/* 1598 */         return (this.components[0]).buf.nioBufferCount();
/*      */     } 
/* 1600 */     int count = 0;
/* 1601 */     for (int i = 0; i < size; i++) {
/* 1602 */       count += (this.components[i]).buf.nioBufferCount();
/*      */     }
/* 1604 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuffer internalNioBuffer(int index, int length) {
/* 1610 */     switch (this.componentCount) {
/*      */       case 0:
/* 1612 */         return EMPTY_NIO_BUFFER;
/*      */       case 1:
/* 1614 */         return this.components[0].internalNioBuffer(index, length);
/*      */     } 
/* 1616 */     throw new UnsupportedOperationException();
/*      */   }
/*      */   
/*      */   public ByteBuffer nioBuffer(int index, int length) {
/*      */     Component c;
/*      */     ByteBuf buf;
/* 1622 */     checkIndex(index, length);
/*      */     
/* 1624 */     switch (this.componentCount) {
/*      */       case 0:
/* 1626 */         return EMPTY_NIO_BUFFER;
/*      */       case 1:
/* 1628 */         c = this.components[0];
/* 1629 */         buf = c.buf;
/* 1630 */         if (buf.nioBufferCount() == 1) {
/* 1631 */           return buf.nioBuffer(c.idx(index), length);
/*      */         }
/*      */         break;
/*      */     } 
/* 1635 */     ByteBuffer[] buffers = nioBuffers(index, length);
/*      */     
/* 1637 */     if (buffers.length == 1) {
/* 1638 */       return buffers[0];
/*      */     }
/*      */     
/* 1641 */     ByteBuffer merged = ByteBuffer.allocate(length).order(order());
/* 1642 */     for (ByteBuffer byteBuffer : buffers) {
/* 1643 */       merged.put(byteBuffer);
/*      */     }
/*      */     
/* 1646 */     merged.flip();
/* 1647 */     return merged;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer[] nioBuffers(int index, int length) {
/* 1652 */     checkIndex(index, length);
/* 1653 */     if (length == 0) {
/* 1654 */       return new ByteBuffer[] { EMPTY_NIO_BUFFER };
/*      */     }
/*      */     
/* 1657 */     RecyclableArrayList buffers = RecyclableArrayList.newInstance(this.componentCount);
/*      */     try {
/* 1659 */       int i = toComponentIndex0(index);
/* 1660 */       while (length > 0) {
/* 1661 */         Component c = this.components[i];
/* 1662 */         ByteBuf s = c.buf;
/* 1663 */         int localLength = Math.min(length, c.endOffset - index);
/* 1664 */         switch (s.nioBufferCount()) {
/*      */           case 0:
/* 1666 */             throw new UnsupportedOperationException();
/*      */           case 1:
/* 1668 */             buffers.add(s.nioBuffer(c.idx(index), localLength));
/*      */             break;
/*      */           default:
/* 1671 */             Collections.addAll((Collection<? super ByteBuffer>)buffers, s.nioBuffers(c.idx(index), localLength));
/*      */             break;
/*      */         } 
/* 1674 */         index += localLength;
/* 1675 */         length -= localLength;
/* 1676 */         i++;
/*      */       } 
/*      */       
/* 1679 */       return (ByteBuffer[])buffers.toArray((Object[])new ByteBuffer[0]);
/*      */     } finally {
/* 1681 */       buffers.recycle();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf consolidate() {
/* 1689 */     ensureAccessible();
/* 1690 */     consolidate0(0, this.componentCount);
/* 1691 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf consolidate(int cIndex, int numComponents) {
/* 1701 */     checkComponentIndex(cIndex, numComponents);
/* 1702 */     consolidate0(cIndex, numComponents);
/* 1703 */     return this;
/*      */   }
/*      */   
/*      */   private void consolidate0(int cIndex, int numComponents) {
/* 1707 */     if (numComponents <= 1) {
/*      */       return;
/*      */     }
/*      */     
/* 1711 */     int endCIndex = cIndex + numComponents;
/* 1712 */     int startOffset = (cIndex != 0) ? (this.components[cIndex]).offset : 0;
/* 1713 */     int capacity = (this.components[endCIndex - 1]).endOffset - startOffset;
/* 1714 */     ByteBuf consolidated = allocBuffer(capacity);
/*      */     
/* 1716 */     for (int i = cIndex; i < endCIndex; i++) {
/* 1717 */       this.components[i].transferTo(consolidated);
/*      */     }
/* 1719 */     this.lastAccessed = null;
/* 1720 */     removeCompRange(cIndex + 1, endCIndex);
/* 1721 */     this.components[cIndex] = newComponent(consolidated, 0);
/* 1722 */     if (cIndex != 0 || numComponents != this.componentCount) {
/* 1723 */       updateComponentOffsets(cIndex);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf discardReadComponents() {
/* 1731 */     ensureAccessible();
/* 1732 */     int readerIndex = readerIndex();
/* 1733 */     if (readerIndex == 0) {
/* 1734 */       return this;
/*      */     }
/*      */ 
/*      */     
/* 1738 */     int writerIndex = writerIndex();
/* 1739 */     if (readerIndex == writerIndex && writerIndex == capacity()) {
/* 1740 */       for (int i = 0, j = this.componentCount; i < j; i++) {
/* 1741 */         this.components[i].free();
/*      */       }
/* 1743 */       this.lastAccessed = null;
/* 1744 */       clearComps();
/* 1745 */       setIndex(0, 0);
/* 1746 */       adjustMarkers(readerIndex);
/* 1747 */       return this;
/*      */     } 
/*      */ 
/*      */     
/* 1751 */     int firstComponentId = 0;
/* 1752 */     Component c = null;
/* 1753 */     for (int size = this.componentCount; firstComponentId < size; firstComponentId++) {
/* 1754 */       c = this.components[firstComponentId];
/* 1755 */       if (c.endOffset > readerIndex) {
/*      */         break;
/*      */       }
/* 1758 */       c.free();
/*      */     } 
/* 1760 */     if (firstComponentId == 0) {
/* 1761 */       return this;
/*      */     }
/* 1763 */     Component la = this.lastAccessed;
/* 1764 */     if (la != null && la.endOffset <= readerIndex) {
/* 1765 */       this.lastAccessed = null;
/*      */     }
/* 1767 */     removeCompRange(0, firstComponentId);
/*      */ 
/*      */     
/* 1770 */     int offset = c.offset;
/* 1771 */     updateComponentOffsets(0);
/* 1772 */     setIndex(readerIndex - offset, writerIndex - offset);
/* 1773 */     adjustMarkers(offset);
/* 1774 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf discardReadBytes() {
/* 1779 */     ensureAccessible();
/* 1780 */     int readerIndex = readerIndex();
/* 1781 */     if (readerIndex == 0) {
/* 1782 */       return this;
/*      */     }
/*      */ 
/*      */     
/* 1786 */     int writerIndex = writerIndex();
/* 1787 */     if (readerIndex == writerIndex && writerIndex == capacity()) {
/* 1788 */       for (int i = 0, j = this.componentCount; i < j; i++) {
/* 1789 */         this.components[i].free();
/*      */       }
/* 1791 */       this.lastAccessed = null;
/* 1792 */       clearComps();
/* 1793 */       setIndex(0, 0);
/* 1794 */       adjustMarkers(readerIndex);
/* 1795 */       return this;
/*      */     } 
/*      */     
/* 1798 */     int firstComponentId = 0;
/* 1799 */     Component c = null;
/* 1800 */     for (int size = this.componentCount; firstComponentId < size; firstComponentId++) {
/* 1801 */       c = this.components[firstComponentId];
/* 1802 */       if (c.endOffset > readerIndex) {
/*      */         break;
/*      */       }
/* 1805 */       c.free();
/*      */     } 
/*      */ 
/*      */     
/* 1809 */     int trimmedBytes = readerIndex - c.offset;
/* 1810 */     c.offset = 0;
/* 1811 */     c.endOffset -= readerIndex;
/* 1812 */     c.srcAdjustment += readerIndex;
/* 1813 */     c.adjustment += readerIndex;
/* 1814 */     ByteBuf slice = c.slice;
/* 1815 */     if (slice != null)
/*      */     {
/*      */       
/* 1818 */       c.slice = slice.slice(trimmedBytes, c.length());
/*      */     }
/* 1820 */     Component la = this.lastAccessed;
/* 1821 */     if (la != null && la.endOffset <= readerIndex) {
/* 1822 */       this.lastAccessed = null;
/*      */     }
/*      */     
/* 1825 */     removeCompRange(0, firstComponentId);
/*      */ 
/*      */     
/* 1828 */     updateComponentOffsets(0);
/* 1829 */     setIndex(0, writerIndex - readerIndex);
/* 1830 */     adjustMarkers(readerIndex);
/* 1831 */     return this;
/*      */   }
/*      */   
/*      */   private ByteBuf allocBuffer(int capacity) {
/* 1835 */     return this.direct ? alloc().directBuffer(capacity) : alloc().heapBuffer(capacity);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1840 */     String result = super.toString();
/* 1841 */     result = result.substring(0, result.length() - 1);
/* 1842 */     return result + ", components=" + this.componentCount + ')';
/*      */   }
/*      */   
/*      */   static interface ByteWrapper<T> {
/*      */     ByteBuf wrap(T param1T);
/*      */     
/*      */     boolean isEmpty(T param1T); }
/*      */   
/*      */   private static final class Component { final ByteBuf srcBuf;
/*      */     final ByteBuf buf;
/*      */     int srcAdjustment;
/*      */     int adjustment;
/*      */     int offset;
/*      */     int endOffset;
/*      */     private ByteBuf slice;
/*      */     
/*      */     Component(ByteBuf srcBuf, int srcOffset, ByteBuf buf, int bufOffset, int offset, int len, ByteBuf slice) {
/* 1859 */       this.srcBuf = srcBuf;
/* 1860 */       this.srcAdjustment = srcOffset - offset;
/* 1861 */       this.buf = buf;
/* 1862 */       this.adjustment = bufOffset - offset;
/* 1863 */       this.offset = offset;
/* 1864 */       this.endOffset = offset + len;
/* 1865 */       this.slice = slice;
/*      */     }
/*      */     
/*      */     int srcIdx(int index) {
/* 1869 */       return index + this.srcAdjustment;
/*      */     }
/*      */     
/*      */     int idx(int index) {
/* 1873 */       return index + this.adjustment;
/*      */     }
/*      */     
/*      */     int length() {
/* 1877 */       return this.endOffset - this.offset;
/*      */     }
/*      */     
/*      */     void reposition(int newOffset) {
/* 1881 */       int move = newOffset - this.offset;
/* 1882 */       this.endOffset += move;
/* 1883 */       this.srcAdjustment -= move;
/* 1884 */       this.adjustment -= move;
/* 1885 */       this.offset = newOffset;
/*      */     }
/*      */ 
/*      */     
/*      */     void transferTo(ByteBuf dst) {
/* 1890 */       dst.writeBytes(this.buf, idx(this.offset), length());
/* 1891 */       free();
/*      */     }
/*      */     
/*      */     ByteBuf slice() {
/* 1895 */       ByteBuf s = this.slice;
/* 1896 */       if (s == null) {
/* 1897 */         this.slice = s = this.srcBuf.slice(srcIdx(this.offset), length());
/*      */       }
/* 1899 */       return s;
/*      */     }
/*      */     
/*      */     ByteBuf duplicate() {
/* 1903 */       return this.srcBuf.duplicate();
/*      */     }
/*      */ 
/*      */     
/*      */     ByteBuffer internalNioBuffer(int index, int length) {
/* 1908 */       return this.srcBuf.internalNioBuffer(srcIdx(index), length);
/*      */     }
/*      */     
/*      */     void free() {
/* 1912 */       this.slice = null;
/*      */ 
/*      */       
/* 1915 */       this.srcBuf.release();
/*      */     } }
/*      */ 
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readerIndex(int readerIndex) {
/* 1921 */     super.readerIndex(readerIndex);
/* 1922 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writerIndex(int writerIndex) {
/* 1927 */     super.writerIndex(writerIndex);
/* 1928 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setIndex(int readerIndex, int writerIndex) {
/* 1933 */     super.setIndex(readerIndex, writerIndex);
/* 1934 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf clear() {
/* 1939 */     super.clear();
/* 1940 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf markReaderIndex() {
/* 1945 */     super.markReaderIndex();
/* 1946 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf resetReaderIndex() {
/* 1951 */     super.resetReaderIndex();
/* 1952 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf markWriterIndex() {
/* 1957 */     super.markWriterIndex();
/* 1958 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf resetWriterIndex() {
/* 1963 */     super.resetWriterIndex();
/* 1964 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf ensureWritable(int minWritableBytes) {
/* 1969 */     super.ensureWritable(minWritableBytes);
/* 1970 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuf dst) {
/* 1975 */     return getBytes(index, dst, dst.writableBytes());
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, ByteBuf dst, int length) {
/* 1980 */     getBytes(index, dst, dst.writerIndex(), length);
/* 1981 */     dst.writerIndex(dst.writerIndex() + length);
/* 1982 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf getBytes(int index, byte[] dst) {
/* 1987 */     return getBytes(index, dst, 0, dst.length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBoolean(int index, boolean value) {
/* 1992 */     return setByte(index, value ? 1 : 0);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setChar(int index, int value) {
/* 1997 */     return setShort(index, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setFloat(int index, float value) {
/* 2002 */     return setInt(index, Float.floatToRawIntBits(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setDouble(int index, double value) {
/* 2007 */     return setLong(index, Double.doubleToRawLongBits(value));
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuf src) {
/* 2012 */     super.setBytes(index, src, src.readableBytes());
/* 2013 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, ByteBuf src, int length) {
/* 2018 */     super.setBytes(index, src, length);
/* 2019 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setBytes(int index, byte[] src) {
/* 2024 */     return setBytes(index, src, 0, src.length);
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf setZero(int index, int length) {
/* 2029 */     super.setZero(index, length);
/* 2030 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuf dst) {
/* 2035 */     super.readBytes(dst, dst.writableBytes());
/* 2036 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuf dst, int length) {
/* 2041 */     super.readBytes(dst, length);
/* 2042 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
/* 2047 */     super.readBytes(dst, dstIndex, length);
/* 2048 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(byte[] dst) {
/* 2053 */     super.readBytes(dst, 0, dst.length);
/* 2054 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(byte[] dst, int dstIndex, int length) {
/* 2059 */     super.readBytes(dst, dstIndex, length);
/* 2060 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(ByteBuffer dst) {
/* 2065 */     super.readBytes(dst);
/* 2066 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf readBytes(OutputStream out, int length) throws IOException {
/* 2071 */     super.readBytes(out, length);
/* 2072 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf skipBytes(int length) {
/* 2077 */     super.skipBytes(length);
/* 2078 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBoolean(boolean value) {
/* 2083 */     writeByte(value ? 1 : 0);
/* 2084 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeByte(int value) {
/* 2089 */     ensureWritable0(1);
/* 2090 */     _setByte(this.writerIndex++, value);
/* 2091 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeShort(int value) {
/* 2096 */     super.writeShort(value);
/* 2097 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeMedium(int value) {
/* 2102 */     super.writeMedium(value);
/* 2103 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeInt(int value) {
/* 2108 */     super.writeInt(value);
/* 2109 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeLong(long value) {
/* 2114 */     super.writeLong(value);
/* 2115 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeChar(int value) {
/* 2120 */     super.writeShort(value);
/* 2121 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeFloat(float value) {
/* 2126 */     super.writeInt(Float.floatToRawIntBits(value));
/* 2127 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeDouble(double value) {
/* 2132 */     super.writeLong(Double.doubleToRawLongBits(value));
/* 2133 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuf src) {
/* 2138 */     super.writeBytes(src, src.readableBytes());
/* 2139 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuf src, int length) {
/* 2144 */     super.writeBytes(src, length);
/* 2145 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
/* 2150 */     super.writeBytes(src, srcIndex, length);
/* 2151 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(byte[] src) {
/* 2156 */     super.writeBytes(src, 0, src.length);
/* 2157 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(byte[] src, int srcIndex, int length) {
/* 2162 */     super.writeBytes(src, srcIndex, length);
/* 2163 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeBytes(ByteBuffer src) {
/* 2168 */     super.writeBytes(src);
/* 2169 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf writeZero(int length) {
/* 2174 */     super.writeZero(length);
/* 2175 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf retain(int increment) {
/* 2180 */     super.retain(increment);
/* 2181 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf retain() {
/* 2186 */     super.retain();
/* 2187 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf touch() {
/* 2192 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf touch(Object hint) {
/* 2197 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer[] nioBuffers() {
/* 2202 */     return nioBuffers(readerIndex(), readableBytes());
/*      */   }
/*      */ 
/*      */   
/*      */   public CompositeByteBuf discardSomeReadBytes() {
/* 2207 */     return discardReadComponents();
/*      */   }
/*      */ 
/*      */   
/*      */   protected void deallocate() {
/* 2212 */     if (this.freed) {
/*      */       return;
/*      */     }
/*      */     
/* 2216 */     this.freed = true;
/*      */ 
/*      */     
/* 2219 */     for (int i = 0, size = this.componentCount; i < size; i++) {
/* 2220 */       this.components[i].free();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   boolean isAccessible() {
/* 2226 */     return !this.freed;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf unwrap() {
/* 2231 */     return null;
/*      */   }
/*      */   
/*      */   private final class CompositeByteBufIterator implements Iterator<ByteBuf> {
/* 2235 */     private final int size = CompositeByteBuf.this.numComponents();
/*      */     
/*      */     private int index;
/*      */     
/*      */     public boolean hasNext() {
/* 2240 */       return (this.size > this.index);
/*      */     }
/*      */ 
/*      */     
/*      */     public ByteBuf next() {
/* 2245 */       if (this.size != CompositeByteBuf.this.numComponents()) {
/* 2246 */         throw new ConcurrentModificationException();
/*      */       }
/* 2248 */       if (!hasNext()) {
/* 2249 */         throw new NoSuchElementException();
/*      */       }
/*      */       try {
/* 2252 */         return CompositeByteBuf.this.components[this.index++].slice();
/* 2253 */       } catch (IndexOutOfBoundsException e) {
/* 2254 */         throw new ConcurrentModificationException();
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void remove() {
/* 2260 */       throw new UnsupportedOperationException("Read-Only");
/*      */     }
/*      */     
/*      */     private CompositeByteBufIterator() {}
/*      */   }
/*      */   
/*      */   private void clearComps() {
/* 2267 */     removeCompRange(0, this.componentCount);
/*      */   }
/*      */   
/*      */   private void removeComp(int i) {
/* 2271 */     removeCompRange(i, i + 1);
/*      */   }
/*      */   
/*      */   private void removeCompRange(int from, int to) {
/* 2275 */     if (from >= to) {
/*      */       return;
/*      */     }
/* 2278 */     int size = this.componentCount;
/* 2279 */     assert from >= 0 && to <= size;
/* 2280 */     if (to < size) {
/* 2281 */       System.arraycopy(this.components, to, this.components, from, size - to);
/*      */     }
/* 2283 */     int newSize = size - to + from;
/* 2284 */     for (int i = newSize; i < size; i++) {
/* 2285 */       this.components[i] = null;
/*      */     }
/* 2287 */     this.componentCount = newSize;
/*      */   }
/*      */   
/*      */   private void addComp(int i, Component c) {
/* 2291 */     shiftComps(i, 1);
/* 2292 */     this.components[i] = c;
/*      */   }
/*      */   
/*      */   private void shiftComps(int i, int count) {
/* 2296 */     int size = this.componentCount, newSize = size + count;
/* 2297 */     assert i >= 0 && i <= size && count > 0;
/* 2298 */     if (newSize > this.components.length) {
/*      */       Component[] newArr;
/* 2300 */       int newArrSize = Math.max(size + (size >> 1), newSize);
/*      */       
/* 2302 */       if (i == size) {
/* 2303 */         newArr = Arrays.<Component, Component>copyOf(this.components, newArrSize, Component[].class);
/*      */       } else {
/* 2305 */         newArr = new Component[newArrSize];
/* 2306 */         if (i > 0) {
/* 2307 */           System.arraycopy(this.components, 0, newArr, 0, i);
/*      */         }
/* 2309 */         if (i < size) {
/* 2310 */           System.arraycopy(this.components, i, newArr, i + count, size - i);
/*      */         }
/*      */       } 
/* 2313 */       this.components = newArr;
/* 2314 */     } else if (i < size) {
/* 2315 */       System.arraycopy(this.components, i, this.components, i + count, size - i);
/*      */     } 
/* 2317 */     this.componentCount = newSize;
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\CompositeByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */