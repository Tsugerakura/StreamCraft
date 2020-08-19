/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.Arrays;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
/*     */ import pro.gravit.repackage.io.netty.util.internal.InternalThreadLocalMap;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectPool;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PromiseNotificationUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ChannelOutboundBuffer
/*     */ {
/*  63 */   static final int CHANNEL_OUTBOUND_BUFFER_ENTRY_OVERHEAD = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.transport.outboundBufferEntrySizeOverhead", 96);
/*     */   
/*  65 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelOutboundBuffer.class);
/*     */   
/*  67 */   private static final FastThreadLocal<ByteBuffer[]> NIO_BUFFERS = new FastThreadLocal<ByteBuffer[]>()
/*     */     {
/*     */       protected ByteBuffer[] initialValue() throws Exception {
/*  70 */         return new ByteBuffer[1024];
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */   
/*     */   private final Channel channel;
/*     */ 
/*     */   
/*     */   private Entry flushedEntry;
/*     */   
/*     */   private Entry unflushedEntry;
/*     */   
/*     */   private Entry tailEntry;
/*     */   
/*     */   private int flushed;
/*     */   
/*     */   private int nioBufferCount;
/*     */   
/*     */   private long nioBufferSize;
/*     */   
/*     */   private boolean inFail;
/*     */   
/*  93 */   private static final AtomicLongFieldUpdater<ChannelOutboundBuffer> TOTAL_PENDING_SIZE_UPDATER = AtomicLongFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "totalPendingSize");
/*     */ 
/*     */   
/*     */   private volatile long totalPendingSize;
/*     */ 
/*     */   
/*  99 */   private static final AtomicIntegerFieldUpdater<ChannelOutboundBuffer> UNWRITABLE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ChannelOutboundBuffer.class, "unwritable");
/*     */   
/*     */   private volatile int unwritable;
/*     */   
/*     */   private volatile Runnable fireChannelWritabilityChangedTask;
/*     */ 
/*     */   
/*     */   ChannelOutboundBuffer(AbstractChannel channel) {
/* 107 */     this.channel = channel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addMessage(Object msg, int size, ChannelPromise promise) {
/* 115 */     Entry entry = Entry.newInstance(msg, size, total(msg), promise);
/* 116 */     if (this.tailEntry == null) {
/* 117 */       this.flushedEntry = null;
/*     */     } else {
/* 119 */       Entry tail = this.tailEntry;
/* 120 */       tail.next = entry;
/*     */     } 
/* 122 */     this.tailEntry = entry;
/* 123 */     if (this.unflushedEntry == null) {
/* 124 */       this.unflushedEntry = entry;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 129 */     incrementPendingOutboundBytes(entry.pendingSize, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addFlush() {
/* 141 */     Entry entry = this.unflushedEntry;
/* 142 */     if (entry != null) {
/* 143 */       if (this.flushedEntry == null)
/*     */       {
/* 145 */         this.flushedEntry = entry;
/*     */       }
/*     */       do {
/* 148 */         this.flushed++;
/* 149 */         if (!entry.promise.setUncancellable()) {
/*     */           
/* 151 */           int pending = entry.cancel();
/* 152 */           decrementPendingOutboundBytes(pending, false, true);
/*     */         } 
/* 154 */         entry = entry.next;
/* 155 */       } while (entry != null);
/*     */ 
/*     */       
/* 158 */       this.unflushedEntry = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void incrementPendingOutboundBytes(long size) {
/* 167 */     incrementPendingOutboundBytes(size, true);
/*     */   }
/*     */   
/*     */   private void incrementPendingOutboundBytes(long size, boolean invokeLater) {
/* 171 */     if (size == 0L) {
/*     */       return;
/*     */     }
/*     */     
/* 175 */     long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, size);
/* 176 */     if (newWriteBufferSize > this.channel.config().getWriteBufferHighWaterMark()) {
/* 177 */       setUnwritable(invokeLater);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void decrementPendingOutboundBytes(long size) {
/* 186 */     decrementPendingOutboundBytes(size, true, true);
/*     */   }
/*     */   
/*     */   private void decrementPendingOutboundBytes(long size, boolean invokeLater, boolean notifyWritability) {
/* 190 */     if (size == 0L) {
/*     */       return;
/*     */     }
/*     */     
/* 194 */     long newWriteBufferSize = TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
/* 195 */     if (notifyWritability && newWriteBufferSize < this.channel.config().getWriteBufferLowWaterMark()) {
/* 196 */       setWritable(invokeLater);
/*     */     }
/*     */   }
/*     */   
/*     */   private static long total(Object msg) {
/* 201 */     if (msg instanceof ByteBuf) {
/* 202 */       return ((ByteBuf)msg).readableBytes();
/*     */     }
/* 204 */     if (msg instanceof FileRegion) {
/* 205 */       return ((FileRegion)msg).count();
/*     */     }
/* 207 */     if (msg instanceof ByteBufHolder) {
/* 208 */       return ((ByteBufHolder)msg).content().readableBytes();
/*     */     }
/* 210 */     return -1L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object current() {
/* 217 */     Entry entry = this.flushedEntry;
/* 218 */     if (entry == null) {
/* 219 */       return null;
/*     */     }
/*     */     
/* 222 */     return entry.msg;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long currentProgress() {
/* 230 */     Entry entry = this.flushedEntry;
/* 231 */     if (entry == null) {
/* 232 */       return 0L;
/*     */     }
/* 234 */     return entry.progress;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void progress(long amount) {
/* 241 */     Entry e = this.flushedEntry;
/* 242 */     assert e != null;
/* 243 */     ChannelPromise p = e.promise;
/* 244 */     long progress = e.progress + amount;
/* 245 */     e.progress = progress;
/* 246 */     if (p instanceof ChannelProgressivePromise) {
/* 247 */       ((ChannelProgressivePromise)p).tryProgress(progress, e.total);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean remove() {
/* 257 */     Entry e = this.flushedEntry;
/* 258 */     if (e == null) {
/* 259 */       clearNioBuffers();
/* 260 */       return false;
/*     */     } 
/* 262 */     Object msg = e.msg;
/*     */     
/* 264 */     ChannelPromise promise = e.promise;
/* 265 */     int size = e.pendingSize;
/*     */     
/* 267 */     removeEntry(e);
/*     */     
/* 269 */     if (!e.cancelled) {
/*     */       
/* 271 */       ReferenceCountUtil.safeRelease(msg);
/* 272 */       safeSuccess(promise);
/* 273 */       decrementPendingOutboundBytes(size, false, true);
/*     */     } 
/*     */ 
/*     */     
/* 277 */     e.recycle();
/*     */     
/* 279 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean remove(Throwable cause) {
/* 288 */     return remove0(cause, true);
/*     */   }
/*     */   
/*     */   private boolean remove0(Throwable cause, boolean notifyWritability) {
/* 292 */     Entry e = this.flushedEntry;
/* 293 */     if (e == null) {
/* 294 */       clearNioBuffers();
/* 295 */       return false;
/*     */     } 
/* 297 */     Object msg = e.msg;
/*     */     
/* 299 */     ChannelPromise promise = e.promise;
/* 300 */     int size = e.pendingSize;
/*     */     
/* 302 */     removeEntry(e);
/*     */     
/* 304 */     if (!e.cancelled) {
/*     */       
/* 306 */       ReferenceCountUtil.safeRelease(msg);
/*     */       
/* 308 */       safeFail(promise, cause);
/* 309 */       decrementPendingOutboundBytes(size, false, notifyWritability);
/*     */     } 
/*     */ 
/*     */     
/* 313 */     e.recycle();
/*     */     
/* 315 */     return true;
/*     */   }
/*     */   
/*     */   private void removeEntry(Entry e) {
/* 319 */     if (--this.flushed == 0) {
/*     */       
/* 321 */       this.flushedEntry = null;
/* 322 */       if (e == this.tailEntry) {
/* 323 */         this.tailEntry = null;
/* 324 */         this.unflushedEntry = null;
/*     */       } 
/*     */     } else {
/* 327 */       this.flushedEntry = e.next;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeBytes(long writtenBytes) {
/*     */     while (true) {
/* 337 */       Object msg = current();
/* 338 */       if (!(msg instanceof ByteBuf)) {
/* 339 */         assert writtenBytes == 0L;
/*     */         
/*     */         break;
/*     */       } 
/* 343 */       ByteBuf buf = (ByteBuf)msg;
/* 344 */       int readerIndex = buf.readerIndex();
/* 345 */       int readableBytes = buf.writerIndex() - readerIndex;
/*     */       
/* 347 */       if (readableBytes <= writtenBytes) {
/* 348 */         if (writtenBytes != 0L) {
/* 349 */           progress(readableBytes);
/* 350 */           writtenBytes -= readableBytes;
/*     */         } 
/* 352 */         remove(); continue;
/*     */       } 
/* 354 */       if (writtenBytes != 0L) {
/* 355 */         buf.readerIndex(readerIndex + (int)writtenBytes);
/* 356 */         progress(writtenBytes);
/*     */       } 
/*     */       
/*     */       break;
/*     */     } 
/* 361 */     clearNioBuffers();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void clearNioBuffers() {
/* 367 */     int count = this.nioBufferCount;
/* 368 */     if (count > 0) {
/* 369 */       this.nioBufferCount = 0;
/* 370 */       Arrays.fill((Object[])NIO_BUFFERS.get(), 0, count, (Object)null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers() {
/* 385 */     return nioBuffers(2147483647, 2147483647L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer[] nioBuffers(int maxCount, long maxBytes) {
/* 403 */     assert maxCount > 0;
/* 404 */     assert maxBytes > 0L;
/* 405 */     long nioBufferSize = 0L;
/* 406 */     int nioBufferCount = 0;
/* 407 */     InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
/* 408 */     ByteBuffer[] nioBuffers = (ByteBuffer[])NIO_BUFFERS.get(threadLocalMap);
/* 409 */     Entry entry = this.flushedEntry;
/* 410 */     while (isFlushedEntry(entry) && entry.msg instanceof ByteBuf) {
/* 411 */       if (!entry.cancelled) {
/* 412 */         ByteBuf buf = (ByteBuf)entry.msg;
/* 413 */         int readerIndex = buf.readerIndex();
/* 414 */         int readableBytes = buf.writerIndex() - readerIndex;
/*     */         
/* 416 */         if (readableBytes > 0) {
/* 417 */           if (maxBytes - readableBytes < nioBufferSize && nioBufferCount != 0) {
/*     */             break;
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 431 */           nioBufferSize += readableBytes;
/* 432 */           int count = entry.count;
/* 433 */           if (count == -1)
/*     */           {
/* 435 */             entry.count = count = buf.nioBufferCount();
/*     */           }
/* 437 */           int neededSpace = Math.min(maxCount, nioBufferCount + count);
/* 438 */           if (neededSpace > nioBuffers.length) {
/* 439 */             nioBuffers = expandNioBufferArray(nioBuffers, neededSpace, nioBufferCount);
/* 440 */             NIO_BUFFERS.set(threadLocalMap, nioBuffers);
/*     */           } 
/* 442 */           if (count == 1) {
/* 443 */             ByteBuffer nioBuf = entry.buf;
/* 444 */             if (nioBuf == null)
/*     */             {
/*     */               
/* 447 */               entry.buf = nioBuf = buf.internalNioBuffer(readerIndex, readableBytes);
/*     */             }
/* 449 */             nioBuffers[nioBufferCount++] = nioBuf;
/*     */           }
/*     */           else {
/*     */             
/* 453 */             nioBufferCount = nioBuffers(entry, buf, nioBuffers, nioBufferCount, maxCount);
/*     */           } 
/* 455 */           if (nioBufferCount == maxCount) {
/*     */             break;
/*     */           }
/*     */         } 
/*     */       } 
/* 460 */       entry = entry.next;
/*     */     } 
/* 462 */     this.nioBufferCount = nioBufferCount;
/* 463 */     this.nioBufferSize = nioBufferSize;
/*     */     
/* 465 */     return nioBuffers;
/*     */   }
/*     */   
/*     */   private static int nioBuffers(Entry entry, ByteBuf buf, ByteBuffer[] nioBuffers, int nioBufferCount, int maxCount) {
/* 469 */     ByteBuffer[] nioBufs = entry.bufs;
/* 470 */     if (nioBufs == null)
/*     */     {
/*     */       
/* 473 */       entry.bufs = nioBufs = buf.nioBuffers();
/*     */     }
/* 475 */     for (int i = 0; i < nioBufs.length && nioBufferCount < maxCount; i++) {
/* 476 */       ByteBuffer nioBuf = nioBufs[i];
/* 477 */       if (nioBuf == null)
/*     */         break; 
/* 479 */       if (nioBuf.hasRemaining())
/*     */       {
/*     */         
/* 482 */         nioBuffers[nioBufferCount++] = nioBuf; } 
/*     */     } 
/* 484 */     return nioBufferCount;
/*     */   }
/*     */   
/*     */   private static ByteBuffer[] expandNioBufferArray(ByteBuffer[] array, int neededSpace, int size) {
/* 488 */     int newCapacity = array.length;
/*     */ 
/*     */     
/*     */     do {
/* 492 */       newCapacity <<= 1;
/*     */       
/* 494 */       if (newCapacity < 0) {
/* 495 */         throw new IllegalStateException();
/*     */       }
/*     */     }
/* 498 */     while (neededSpace > newCapacity);
/*     */     
/* 500 */     ByteBuffer[] newArray = new ByteBuffer[newCapacity];
/* 501 */     System.arraycopy(array, 0, newArray, 0, size);
/*     */     
/* 503 */     return newArray;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int nioBufferCount() {
/* 512 */     return this.nioBufferCount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long nioBufferSize() {
/* 521 */     return this.nioBufferSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWritable() {
/* 531 */     return (this.unwritable == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getUserDefinedWritability(int index) {
/* 539 */     return ((this.unwritable & writabilityMask(index)) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setUserDefinedWritability(int index, boolean writable) {
/* 546 */     if (writable) {
/* 547 */       setUserDefinedWritability(index);
/*     */     } else {
/* 549 */       clearUserDefinedWritability(index);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void setUserDefinedWritability(int index) {
/* 554 */     int mask = writabilityMask(index) ^ 0xFFFFFFFF;
/*     */     while (true) {
/* 556 */       int oldValue = this.unwritable;
/* 557 */       int newValue = oldValue & mask;
/* 558 */       if (UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue)) {
/* 559 */         if (oldValue != 0 && newValue == 0) {
/* 560 */           fireChannelWritabilityChanged(true);
/*     */         }
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void clearUserDefinedWritability(int index) {
/* 568 */     int mask = writabilityMask(index);
/*     */     while (true) {
/* 570 */       int oldValue = this.unwritable;
/* 571 */       int newValue = oldValue | mask;
/* 572 */       if (UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue)) {
/* 573 */         if (oldValue == 0 && newValue != 0) {
/* 574 */           fireChannelWritabilityChanged(true);
/*     */         }
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int writabilityMask(int index) {
/* 582 */     if (index < 1 || index > 31) {
/* 583 */       throw new IllegalArgumentException("index: " + index + " (expected: 1~31)");
/*     */     }
/* 585 */     return 1 << index;
/*     */   }
/*     */   
/*     */   private void setWritable(boolean invokeLater) {
/*     */     while (true) {
/* 590 */       int oldValue = this.unwritable;
/* 591 */       int newValue = oldValue & 0xFFFFFFFE;
/* 592 */       if (UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue)) {
/* 593 */         if (oldValue != 0 && newValue == 0) {
/* 594 */           fireChannelWritabilityChanged(invokeLater);
/*     */         }
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void setUnwritable(boolean invokeLater) {
/*     */     while (true) {
/* 603 */       int oldValue = this.unwritable;
/* 604 */       int newValue = oldValue | 0x1;
/* 605 */       if (UNWRITABLE_UPDATER.compareAndSet(this, oldValue, newValue)) {
/* 606 */         if (oldValue == 0 && newValue != 0) {
/* 607 */           fireChannelWritabilityChanged(invokeLater);
/*     */         }
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void fireChannelWritabilityChanged(boolean invokeLater) {
/* 615 */     final ChannelPipeline pipeline = this.channel.pipeline();
/* 616 */     if (invokeLater) {
/* 617 */       Runnable task = this.fireChannelWritabilityChangedTask;
/* 618 */       if (task == null) {
/* 619 */         this.fireChannelWritabilityChangedTask = task = new Runnable()
/*     */           {
/*     */             public void run() {
/* 622 */               pipeline.fireChannelWritabilityChanged();
/*     */             }
/*     */           };
/*     */       }
/* 626 */       this.channel.eventLoop().execute(task);
/*     */     } else {
/* 628 */       pipeline.fireChannelWritabilityChanged();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 636 */     return this.flushed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 644 */     return (this.flushed == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void failFlushed(Throwable cause, boolean notify) {
/* 653 */     if (this.inFail) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/* 658 */       this.inFail = true; do {
/*     */       
/* 660 */       } while (remove0(cause, notify));
/*     */     
/*     */     }
/*     */     finally {
/*     */       
/* 665 */       this.inFail = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   void close(final Throwable cause, final boolean allowChannelOpen) {
/* 670 */     if (this.inFail) {
/* 671 */       this.channel.eventLoop().execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 674 */               ChannelOutboundBuffer.this.close(cause, allowChannelOpen);
/*     */             }
/*     */           });
/*     */       
/*     */       return;
/*     */     } 
/* 680 */     this.inFail = true;
/*     */     
/* 682 */     if (!allowChannelOpen && this.channel.isOpen()) {
/* 683 */       throw new IllegalStateException("close() must be invoked after the channel is closed.");
/*     */     }
/*     */     
/* 686 */     if (!isEmpty()) {
/* 687 */       throw new IllegalStateException("close() must be invoked after all flushed writes are handled.");
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 692 */       Entry e = this.unflushedEntry;
/* 693 */       while (e != null) {
/*     */         
/* 695 */         int size = e.pendingSize;
/* 696 */         TOTAL_PENDING_SIZE_UPDATER.addAndGet(this, -size);
/*     */         
/* 698 */         if (!e.cancelled) {
/* 699 */           ReferenceCountUtil.safeRelease(e.msg);
/* 700 */           safeFail(e.promise, cause);
/*     */         } 
/* 702 */         e = e.recycleAndGetNext();
/*     */       } 
/*     */     } finally {
/* 705 */       this.inFail = false;
/*     */     } 
/* 707 */     clearNioBuffers();
/*     */   }
/*     */   
/*     */   void close(ClosedChannelException cause) {
/* 711 */     close(cause, false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void safeSuccess(ChannelPromise promise) {
/* 717 */     PromiseNotificationUtil.trySuccess(promise, null, (promise instanceof VoidChannelPromise) ? null : logger);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void safeFail(ChannelPromise promise, Throwable cause) {
/* 723 */     PromiseNotificationUtil.tryFailure(promise, cause, (promise instanceof VoidChannelPromise) ? null : logger);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void recycle() {}
/*     */ 
/*     */   
/*     */   public long totalPendingWriteBytes() {
/* 732 */     return this.totalPendingSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long bytesBeforeUnwritable() {
/* 740 */     long bytes = this.channel.config().getWriteBufferHighWaterMark() - this.totalPendingSize;
/*     */ 
/*     */ 
/*     */     
/* 744 */     if (bytes > 0L) {
/* 745 */       return isWritable() ? bytes : 0L;
/*     */     }
/* 747 */     return 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long bytesBeforeWritable() {
/* 755 */     long bytes = this.totalPendingSize - this.channel.config().getWriteBufferLowWaterMark();
/*     */ 
/*     */ 
/*     */     
/* 759 */     if (bytes > 0L) {
/* 760 */       return isWritable() ? 0L : bytes;
/*     */     }
/* 762 */     return 0L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void forEachFlushedMessage(MessageProcessor processor) throws Exception {
/* 771 */     ObjectUtil.checkNotNull(processor, "processor");
/*     */     
/* 773 */     Entry entry = this.flushedEntry;
/* 774 */     if (entry == null) {
/*     */       return;
/*     */     }
/*     */     
/*     */     do {
/* 779 */       if (!entry.cancelled && 
/* 780 */         !processor.processMessage(entry.msg)) {
/*     */         return;
/*     */       }
/*     */       
/* 784 */       entry = entry.next;
/* 785 */     } while (isFlushedEntry(entry));
/*     */   }
/*     */   
/*     */   private boolean isFlushedEntry(Entry e) {
/* 789 */     return (e != null && e != this.unflushedEntry);
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface MessageProcessor
/*     */   {
/*     */     boolean processMessage(Object param1Object) throws Exception;
/*     */   }
/*     */ 
/*     */   
/*     */   static final class Entry
/*     */   {
/* 801 */     private static final ObjectPool<Entry> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<Entry>()
/*     */         {
/*     */           public ChannelOutboundBuffer.Entry newObject(ObjectPool.Handle<ChannelOutboundBuffer.Entry> handle) {
/* 804 */             return new ChannelOutboundBuffer.Entry(handle);
/*     */           }
/*     */         });
/*     */     
/*     */     private final ObjectPool.Handle<Entry> handle;
/*     */     Entry next;
/*     */     Object msg;
/*     */     ByteBuffer[] bufs;
/*     */     ByteBuffer buf;
/*     */     ChannelPromise promise;
/*     */     long progress;
/*     */     long total;
/*     */     int pendingSize;
/* 817 */     int count = -1;
/*     */     boolean cancelled;
/*     */     
/*     */     private Entry(ObjectPool.Handle<Entry> handle) {
/* 821 */       this.handle = handle;
/*     */     }
/*     */     
/*     */     static Entry newInstance(Object msg, int size, long total, ChannelPromise promise) {
/* 825 */       Entry entry = (Entry)RECYCLER.get();
/* 826 */       entry.msg = msg;
/* 827 */       entry.pendingSize = size + ChannelOutboundBuffer.CHANNEL_OUTBOUND_BUFFER_ENTRY_OVERHEAD;
/* 828 */       entry.total = total;
/* 829 */       entry.promise = promise;
/* 830 */       return entry;
/*     */     }
/*     */     
/*     */     int cancel() {
/* 834 */       if (!this.cancelled) {
/* 835 */         this.cancelled = true;
/* 836 */         int pSize = this.pendingSize;
/*     */ 
/*     */         
/* 839 */         ReferenceCountUtil.safeRelease(this.msg);
/* 840 */         this.msg = Unpooled.EMPTY_BUFFER;
/*     */         
/* 842 */         this.pendingSize = 0;
/* 843 */         this.total = 0L;
/* 844 */         this.progress = 0L;
/* 845 */         this.bufs = null;
/* 846 */         this.buf = null;
/* 847 */         return pSize;
/*     */       } 
/* 849 */       return 0;
/*     */     }
/*     */     
/*     */     void recycle() {
/* 853 */       this.next = null;
/* 854 */       this.bufs = null;
/* 855 */       this.buf = null;
/* 856 */       this.msg = null;
/* 857 */       this.promise = null;
/* 858 */       this.progress = 0L;
/* 859 */       this.total = 0L;
/* 860 */       this.pendingSize = 0;
/* 861 */       this.count = -1;
/* 862 */       this.cancelled = false;
/* 863 */       this.handle.recycle(this);
/*     */     }
/*     */     
/*     */     Entry recycleAndGetNext() {
/* 867 */       Entry next = this.next;
/* 868 */       recycle();
/* 869 */       return next;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelOutboundBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */