/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.util.ArrayDeque;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.buffer.CompositeByteBuf;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ public abstract class AbstractCoalescingBufferQueue
/*     */ {
/*  33 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractCoalescingBufferQueue.class);
/*     */ 
/*     */   
/*     */   private final ArrayDeque<Object> bufAndListenerPairs;
/*     */ 
/*     */   
/*     */   private final PendingBytesTracker tracker;
/*     */ 
/*     */   
/*     */   private int readableBytes;
/*     */ 
/*     */   
/*     */   protected AbstractCoalescingBufferQueue(Channel channel, int initSize) {
/*  46 */     this.bufAndListenerPairs = new ArrayDeque(initSize);
/*  47 */     this.tracker = (channel == null) ? null : PendingBytesTracker.newTracker(channel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void addFirst(ByteBuf buf, ChannelPromise promise) {
/*  57 */     addFirst(buf, toChannelFutureListener(promise));
/*     */   }
/*     */   
/*     */   private void addFirst(ByteBuf buf, ChannelFutureListener listener) {
/*  61 */     if (listener != null) {
/*  62 */       this.bufAndListenerPairs.addFirst(listener);
/*     */     }
/*  64 */     this.bufAndListenerPairs.addFirst(buf);
/*  65 */     incrementReadableBytes(buf.readableBytes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void add(ByteBuf buf) {
/*  72 */     add(buf, (ChannelFutureListener)null);
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
/*     */   public final void add(ByteBuf buf, ChannelPromise promise) {
/*  84 */     add(buf, toChannelFutureListener(promise));
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
/*     */   public final void add(ByteBuf buf, ChannelFutureListener listener) {
/*  96 */     this.bufAndListenerPairs.add(buf);
/*  97 */     if (listener != null) {
/*  98 */       this.bufAndListenerPairs.add(listener);
/*     */     }
/* 100 */     incrementReadableBytes(buf.readableBytes());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final ByteBuf removeFirst(ChannelPromise aggregatePromise) {
/* 109 */     Object entry = this.bufAndListenerPairs.poll();
/* 110 */     if (entry == null) {
/* 111 */       return null;
/*     */     }
/* 113 */     assert entry instanceof ByteBuf;
/* 114 */     ByteBuf result = (ByteBuf)entry;
/*     */     
/* 116 */     decrementReadableBytes(result.readableBytes());
/*     */     
/* 118 */     entry = this.bufAndListenerPairs.peek();
/* 119 */     if (entry instanceof ChannelFutureListener) {
/* 120 */       aggregatePromise.addListener((ChannelFutureListener)entry);
/* 121 */       this.bufAndListenerPairs.poll();
/*     */     } 
/* 123 */     return result;
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
/*     */   public final ByteBuf remove(ByteBufAllocator alloc, int bytes, ChannelPromise aggregatePromise) {
/* 138 */     ObjectUtil.checkPositiveOrZero(bytes, "bytes");
/* 139 */     ObjectUtil.checkNotNull(aggregatePromise, "aggregatePromise");
/*     */ 
/*     */     
/* 142 */     if (this.bufAndListenerPairs.isEmpty()) {
/* 143 */       return removeEmptyValue();
/*     */     }
/* 145 */     bytes = Math.min(bytes, this.readableBytes);
/*     */     
/* 147 */     ByteBuf toReturn = null;
/* 148 */     ByteBuf entryBuffer = null;
/* 149 */     int originalBytes = bytes;
/*     */     try {
/*     */       while (true) {
/* 152 */         Object entry = this.bufAndListenerPairs.poll();
/* 153 */         if (entry == null) {
/*     */           break;
/*     */         }
/* 156 */         if (entry instanceof ChannelFutureListener) {
/* 157 */           aggregatePromise.addListener((ChannelFutureListener)entry);
/*     */           continue;
/*     */         } 
/* 160 */         entryBuffer = (ByteBuf)entry;
/* 161 */         if (entryBuffer.readableBytes() > bytes) {
/*     */           
/* 163 */           this.bufAndListenerPairs.addFirst(entryBuffer);
/* 164 */           if (bytes > 0) {
/*     */             
/* 166 */             entryBuffer = entryBuffer.readRetainedSlice(bytes);
/*     */             
/* 168 */             toReturn = (toReturn == null) ? composeFirst(alloc, entryBuffer) : compose(alloc, toReturn, entryBuffer);
/* 169 */             bytes = 0;
/*     */           } 
/*     */           break;
/*     */         } 
/* 173 */         bytes -= entryBuffer.readableBytes();
/*     */         
/* 175 */         toReturn = (toReturn == null) ? composeFirst(alloc, entryBuffer) : compose(alloc, toReturn, entryBuffer);
/*     */         
/* 177 */         entryBuffer = null;
/*     */       } 
/* 179 */     } catch (Throwable cause) {
/* 180 */       ReferenceCountUtil.safeRelease(entryBuffer);
/* 181 */       ReferenceCountUtil.safeRelease(toReturn);
/* 182 */       aggregatePromise.setFailure(cause);
/* 183 */       PlatformDependent.throwException(cause);
/*     */     } 
/* 185 */     decrementReadableBytes(originalBytes - bytes);
/* 186 */     return toReturn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int readableBytes() {
/* 193 */     return this.readableBytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isEmpty() {
/* 200 */     return this.bufAndListenerPairs.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void releaseAndFailAll(ChannelOutboundInvoker invoker, Throwable cause) {
/* 207 */     releaseAndCompleteAll(invoker.newFailedFuture(cause));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void copyTo(AbstractCoalescingBufferQueue dest) {
/* 215 */     dest.bufAndListenerPairs.addAll(this.bufAndListenerPairs);
/* 216 */     dest.incrementReadableBytes(this.readableBytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void writeAndRemoveAll(ChannelHandlerContext ctx) {
/* 224 */     decrementReadableBytes(this.readableBytes);
/* 225 */     Throwable pending = null;
/* 226 */     ByteBuf previousBuf = null;
/*     */     while (true) {
/* 228 */       Object entry = this.bufAndListenerPairs.poll();
/*     */       try {
/* 230 */         if (entry == null) {
/* 231 */           if (previousBuf != null) {
/* 232 */             ctx.write(previousBuf, ctx.voidPromise());
/*     */           }
/*     */           
/*     */           break;
/*     */         } 
/* 237 */         if (entry instanceof ByteBuf) {
/* 238 */           if (previousBuf != null) {
/* 239 */             ctx.write(previousBuf, ctx.voidPromise());
/*     */           }
/* 241 */           previousBuf = (ByteBuf)entry; continue;
/* 242 */         }  if (entry instanceof ChannelPromise) {
/* 243 */           ctx.write(previousBuf, (ChannelPromise)entry);
/* 244 */           previousBuf = null; continue;
/*     */         } 
/* 246 */         ctx.write(previousBuf).addListener((ChannelFutureListener)entry);
/* 247 */         previousBuf = null;
/*     */       }
/* 249 */       catch (Throwable t) {
/* 250 */         if (pending == null) {
/* 251 */           pending = t; continue;
/*     */         } 
/* 253 */         logger.info("Throwable being suppressed because Throwable {} is already pending", pending, t);
/*     */       } 
/*     */     } 
/*     */     
/* 257 */     if (pending != null) {
/* 258 */       throw new IllegalStateException(pending);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract ByteBuf compose(ByteBufAllocator paramByteBufAllocator, ByteBuf paramByteBuf1, ByteBuf paramByteBuf2);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ByteBuf composeIntoComposite(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
/* 273 */     CompositeByteBuf composite = alloc.compositeBuffer(size() + 2);
/*     */     try {
/* 275 */       composite.addComponent(true, cumulation);
/* 276 */       composite.addComponent(true, next);
/* 277 */     } catch (Throwable cause) {
/* 278 */       composite.release();
/* 279 */       ReferenceCountUtil.safeRelease(next);
/* 280 */       PlatformDependent.throwException(cause);
/*     */     } 
/* 282 */     return (ByteBuf)composite;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ByteBuf copyAndCompose(ByteBufAllocator alloc, ByteBuf cumulation, ByteBuf next) {
/* 293 */     ByteBuf newCumulation = alloc.ioBuffer(cumulation.readableBytes() + next.readableBytes());
/*     */     try {
/* 295 */       newCumulation.writeBytes(cumulation).writeBytes(next);
/* 296 */     } catch (Throwable cause) {
/* 297 */       newCumulation.release();
/* 298 */       ReferenceCountUtil.safeRelease(next);
/* 299 */       PlatformDependent.throwException(cause);
/*     */     } 
/* 301 */     cumulation.release();
/* 302 */     next.release();
/* 303 */     return newCumulation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ByteBuf composeFirst(ByteBufAllocator allocator, ByteBuf first) {
/* 311 */     return first;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract ByteBuf removeEmptyValue();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int size() {
/* 325 */     return this.bufAndListenerPairs.size();
/*     */   }
/*     */   
/*     */   private void releaseAndCompleteAll(ChannelFuture future) {
/* 329 */     decrementReadableBytes(this.readableBytes);
/* 330 */     Throwable pending = null;
/*     */     while (true) {
/* 332 */       Object entry = this.bufAndListenerPairs.poll();
/* 333 */       if (entry == null) {
/*     */         break;
/*     */       }
/*     */       try {
/* 337 */         if (entry instanceof ByteBuf) {
/* 338 */           ReferenceCountUtil.safeRelease(entry); continue;
/*     */         } 
/* 340 */         ((ChannelFutureListener)entry).operationComplete(future);
/*     */       }
/* 342 */       catch (Throwable t) {
/* 343 */         if (pending == null) {
/* 344 */           pending = t; continue;
/*     */         } 
/* 346 */         logger.info("Throwable being suppressed because Throwable {} is already pending", pending, t);
/*     */       } 
/*     */     } 
/*     */     
/* 350 */     if (pending != null) {
/* 351 */       throw new IllegalStateException(pending);
/*     */     }
/*     */   }
/*     */   
/*     */   private void incrementReadableBytes(int increment) {
/* 356 */     int nextReadableBytes = this.readableBytes + increment;
/* 357 */     if (nextReadableBytes < this.readableBytes) {
/* 358 */       throw new IllegalStateException("buffer queue length overflow: " + this.readableBytes + " + " + increment);
/*     */     }
/* 360 */     this.readableBytes = nextReadableBytes;
/* 361 */     if (this.tracker != null) {
/* 362 */       this.tracker.incrementPendingOutboundBytes(increment);
/*     */     }
/*     */   }
/*     */   
/*     */   private void decrementReadableBytes(int decrement) {
/* 367 */     this.readableBytes -= decrement;
/* 368 */     assert this.readableBytes >= 0;
/* 369 */     if (this.tracker != null) {
/* 370 */       this.tracker.decrementPendingOutboundBytes(decrement);
/*     */     }
/*     */   }
/*     */   
/*     */   private static ChannelFutureListener toChannelFutureListener(ChannelPromise promise) {
/* 375 */     return promise.isVoid() ? null : new DelegatingChannelPromiseNotifier(promise);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\AbstractCoalescingBufferQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */