/*     */ package pro.gravit.repackage.io.netty.handler.stream;
/*     */ 
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Queue;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelDuplexHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelProgressivePromise;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChunkedWriteHandler
/*     */   extends ChannelDuplexHandler
/*     */ {
/*  71 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChunkedWriteHandler.class);
/*     */   
/*  73 */   private final Queue<PendingWrite> queue = new ArrayDeque<PendingWrite>();
/*     */ 
/*     */   
/*     */   private volatile ChannelHandlerContext ctx;
/*     */ 
/*     */   
/*     */   private PendingWrite currentWrite;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ChunkedWriteHandler(int maxPendingWrites) {
/*  85 */     if (maxPendingWrites <= 0) {
/*  86 */       throw new IllegalArgumentException("maxPendingWrites: " + maxPendingWrites + " (expected: > 0)");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/*  93 */     this.ctx = ctx;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resumeTransfer() {
/* 100 */     final ChannelHandlerContext ctx = this.ctx;
/* 101 */     if (ctx == null) {
/*     */       return;
/*     */     }
/* 104 */     if (ctx.executor().inEventLoop()) {
/* 105 */       resumeTransfer0(ctx);
/*     */     } else {
/*     */       
/* 108 */       ctx.executor().execute(new Runnable()
/*     */           {
/*     */             public void run()
/*     */             {
/* 112 */               ChunkedWriteHandler.this.resumeTransfer0(ctx);
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */   
/*     */   private void resumeTransfer0(ChannelHandlerContext ctx) {
/*     */     try {
/* 120 */       doFlush(ctx);
/* 121 */     } catch (Exception e) {
/* 122 */       logger.warn("Unexpected exception while sending chunks.", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 128 */     this.queue.add(new PendingWrite(msg, promise));
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush(ChannelHandlerContext ctx) throws Exception {
/* 133 */     doFlush(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 138 */     doFlush(ctx);
/* 139 */     ctx.fireChannelInactive();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
/* 144 */     if (ctx.channel().isWritable())
/*     */     {
/* 146 */       doFlush(ctx);
/*     */     }
/* 148 */     ctx.fireChannelWritabilityChanged();
/*     */   }
/*     */   
/*     */   private void discard(Throwable cause) {
/*     */     while (true) {
/* 153 */       PendingWrite currentWrite = this.currentWrite;
/*     */       
/* 155 */       if (this.currentWrite == null) {
/* 156 */         currentWrite = this.queue.poll();
/*     */       } else {
/* 158 */         this.currentWrite = null;
/*     */       } 
/*     */       
/* 161 */       if (currentWrite == null) {
/*     */         break;
/*     */       }
/* 164 */       Object message = currentWrite.msg;
/* 165 */       if (message instanceof ChunkedInput) {
/* 166 */         boolean endOfInput; long inputLength; ChunkedInput<?> in = (ChunkedInput)message;
/*     */ 
/*     */         
/*     */         try {
/* 170 */           endOfInput = in.isEndOfInput();
/* 171 */           inputLength = in.length();
/* 172 */           closeInput(in);
/* 173 */         } catch (Exception e) {
/* 174 */           closeInput(in);
/* 175 */           currentWrite.fail(e);
/* 176 */           if (logger.isWarnEnabled()) {
/* 177 */             logger.warn(ChunkedInput.class.getSimpleName() + " failed", e);
/*     */           }
/*     */           
/*     */           continue;
/*     */         } 
/* 182 */         if (!endOfInput) {
/* 183 */           if (cause == null) {
/* 184 */             cause = new ClosedChannelException();
/*     */           }
/* 186 */           currentWrite.fail(cause); continue;
/*     */         } 
/* 188 */         currentWrite.success(inputLength);
/*     */         continue;
/*     */       } 
/* 191 */       if (cause == null) {
/* 192 */         cause = new ClosedChannelException();
/*     */       }
/* 194 */       currentWrite.fail(cause);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void doFlush(ChannelHandlerContext ctx) {
/* 200 */     final Channel channel = ctx.channel();
/* 201 */     if (!channel.isActive()) {
/* 202 */       discard(null);
/*     */       
/*     */       return;
/*     */     } 
/* 206 */     boolean requiresFlush = true;
/* 207 */     ByteBufAllocator allocator = ctx.alloc();
/* 208 */     while (channel.isWritable()) {
/* 209 */       if (this.currentWrite == null) {
/* 210 */         this.currentWrite = this.queue.poll();
/*     */       }
/*     */       
/* 213 */       if (this.currentWrite == null) {
/*     */         break;
/*     */       }
/*     */       
/* 217 */       if (this.currentWrite.promise.isDone()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 227 */         this.currentWrite = null;
/*     */         
/*     */         continue;
/*     */       } 
/* 231 */       final PendingWrite currentWrite = this.currentWrite;
/* 232 */       Object pendingMessage = currentWrite.msg;
/*     */       
/* 234 */       if (pendingMessage instanceof ChunkedInput) {
/* 235 */         boolean endOfInput, suspend; final ChunkedInput<?> chunks = (ChunkedInput)pendingMessage;
/*     */ 
/*     */         
/* 238 */         Object message = null;
/*     */         try {
/* 240 */           message = chunks.readChunk(allocator);
/* 241 */           endOfInput = chunks.isEndOfInput();
/*     */           
/* 243 */           if (message == null) {
/*     */             
/* 245 */             suspend = !endOfInput;
/*     */           } else {
/* 247 */             suspend = false;
/*     */           } 
/* 249 */         } catch (Throwable t) {
/* 250 */           this.currentWrite = null;
/*     */           
/* 252 */           if (message != null) {
/* 253 */             ReferenceCountUtil.release(message);
/*     */           }
/*     */           
/* 256 */           closeInput(chunks);
/* 257 */           currentWrite.fail(t);
/*     */           
/*     */           break;
/*     */         } 
/* 261 */         if (suspend) {
/*     */           break;
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 268 */         if (message == null)
/*     */         {
/*     */           
/* 271 */           message = Unpooled.EMPTY_BUFFER;
/*     */         }
/*     */         
/* 274 */         ChannelFuture f = ctx.write(message);
/* 275 */         if (endOfInput) {
/* 276 */           this.currentWrite = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 283 */           f.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */               {
/*     */                 public void operationComplete(ChannelFuture future) throws Exception {
/* 286 */                   if (!future.isSuccess()) {
/* 287 */                     ChunkedWriteHandler.closeInput(chunks);
/* 288 */                     currentWrite.fail(future.cause());
/*     */                   } else {
/*     */                     
/* 291 */                     long inputProgress = chunks.progress();
/* 292 */                     long inputLength = chunks.length();
/* 293 */                     ChunkedWriteHandler.closeInput(chunks);
/* 294 */                     currentWrite.progress(inputProgress, inputLength);
/* 295 */                     currentWrite.success(inputLength);
/*     */                   } 
/*     */                 }
/*     */               });
/* 299 */         } else if (channel.isWritable()) {
/* 300 */           f.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */               {
/*     */                 public void operationComplete(ChannelFuture future) throws Exception {
/* 303 */                   if (!future.isSuccess()) {
/* 304 */                     ChunkedWriteHandler.closeInput(chunks);
/* 305 */                     currentWrite.fail(future.cause());
/*     */                   } else {
/* 307 */                     currentWrite.progress(chunks.progress(), chunks.length());
/*     */                   } 
/*     */                 }
/*     */               });
/*     */         } else {
/* 312 */           f.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */               {
/*     */                 public void operationComplete(ChannelFuture future) throws Exception {
/* 315 */                   if (!future.isSuccess()) {
/* 316 */                     ChunkedWriteHandler.closeInput(chunks);
/* 317 */                     currentWrite.fail(future.cause());
/*     */                   } else {
/* 319 */                     currentWrite.progress(chunks.progress(), chunks.length());
/* 320 */                     if (channel.isWritable()) {
/* 321 */                       ChunkedWriteHandler.this.resumeTransfer();
/*     */                     }
/*     */                   } 
/*     */                 }
/*     */               });
/*     */         } 
/*     */         
/* 328 */         ctx.flush();
/* 329 */         requiresFlush = false;
/*     */       } else {
/* 331 */         this.currentWrite = null;
/* 332 */         ctx.write(pendingMessage, currentWrite.promise);
/* 333 */         requiresFlush = true;
/*     */       } 
/*     */       
/* 336 */       if (!channel.isActive()) {
/* 337 */         discard(new ClosedChannelException());
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 342 */     if (requiresFlush) {
/* 343 */       ctx.flush();
/*     */     }
/*     */   }
/*     */   
/*     */   private static void closeInput(ChunkedInput<?> chunks) {
/*     */     try {
/* 349 */       chunks.close();
/* 350 */     } catch (Throwable t) {
/* 351 */       if (logger.isWarnEnabled())
/* 352 */         logger.warn("Failed to close a chunked input.", t); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public ChunkedWriteHandler() {}
/*     */   
/*     */   private static final class PendingWrite { final Object msg;
/*     */     final ChannelPromise promise;
/*     */     
/*     */     PendingWrite(Object msg, ChannelPromise promise) {
/* 362 */       this.msg = msg;
/* 363 */       this.promise = promise;
/*     */     }
/*     */     
/*     */     void fail(Throwable cause) {
/* 367 */       ReferenceCountUtil.release(this.msg);
/* 368 */       this.promise.tryFailure(cause);
/*     */     }
/*     */     
/*     */     void success(long total) {
/* 372 */       if (this.promise.isDone()) {
/*     */         return;
/*     */       }
/*     */       
/* 376 */       progress(total, total);
/* 377 */       this.promise.trySuccess();
/*     */     }
/*     */     
/*     */     void progress(long progress, long total) {
/* 381 */       if (this.promise instanceof ChannelProgressivePromise)
/* 382 */         ((ChannelProgressivePromise)this.promise).tryProgress(progress, total); 
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\stream\ChunkedWriteHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */