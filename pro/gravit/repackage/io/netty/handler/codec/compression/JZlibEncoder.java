/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import com.jcraft.jzlib.Deflater;
/*     */ import com.jcraft.jzlib.JZlib;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromiseNotifier;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public class JZlibEncoder
/*     */   extends ZlibEncoder
/*     */ {
/*     */   private final int wrapperOverhead;
/*  39 */   private final Deflater z = new Deflater();
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean finished;
/*     */ 
/*     */   
/*     */   private volatile ChannelHandlerContext ctx;
/*     */ 
/*     */ 
/*     */   
/*     */   public JZlibEncoder() {
/*  51 */     this(6);
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
/*     */   public JZlibEncoder(int compressionLevel) {
/*  67 */     this(ZlibWrapper.ZLIB, compressionLevel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JZlibEncoder(ZlibWrapper wrapper) {
/*  78 */     this(wrapper, 6);
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
/*     */   public JZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
/*  94 */     this(wrapper, compressionLevel, 15, 8);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JZlibEncoder(ZlibWrapper wrapper, int compressionLevel, int windowBits, int memLevel) {
/* 121 */     if (compressionLevel < 0 || compressionLevel > 9) {
/* 122 */       throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
/*     */     }
/*     */ 
/*     */     
/* 126 */     if (windowBits < 9 || windowBits > 15) {
/* 127 */       throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
/*     */     }
/*     */     
/* 130 */     if (memLevel < 1 || memLevel > 9) {
/* 131 */       throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
/*     */     }
/*     */     
/* 134 */     ObjectUtil.checkNotNull(wrapper, "wrapper");
/* 135 */     if (wrapper == ZlibWrapper.ZLIB_OR_NONE) {
/* 136 */       throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not allowed for compression.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 141 */     int resultCode = this.z.init(compressionLevel, windowBits, memLevel, 
/*     */         
/* 143 */         ZlibUtil.convertWrapperType(wrapper));
/* 144 */     if (resultCode != 0) {
/* 145 */       ZlibUtil.fail(this.z, "initialization failure", resultCode);
/*     */     }
/*     */     
/* 148 */     this.wrapperOverhead = ZlibUtil.wrapperOverhead(wrapper);
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
/*     */   public JZlibEncoder(byte[] dictionary) {
/* 163 */     this(6, dictionary);
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
/*     */   
/*     */   public JZlibEncoder(int compressionLevel, byte[] dictionary) {
/* 182 */     this(compressionLevel, 15, 8, dictionary);
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
/*     */   public JZlibEncoder(int compressionLevel, int windowBits, int memLevel, byte[] dictionary) {
/* 211 */     if (compressionLevel < 0 || compressionLevel > 9) {
/* 212 */       throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
/*     */     }
/* 214 */     if (windowBits < 9 || windowBits > 15) {
/* 215 */       throw new IllegalArgumentException("windowBits: " + windowBits + " (expected: 9-15)");
/*     */     }
/*     */     
/* 218 */     if (memLevel < 1 || memLevel > 9) {
/* 219 */       throw new IllegalArgumentException("memLevel: " + memLevel + " (expected: 1-9)");
/*     */     }
/*     */     
/* 222 */     ObjectUtil.checkNotNull(dictionary, "dictionary");
/*     */ 
/*     */     
/* 225 */     int resultCode = this.z.deflateInit(compressionLevel, windowBits, memLevel, JZlib.W_ZLIB);
/*     */ 
/*     */     
/* 228 */     if (resultCode != 0) {
/* 229 */       ZlibUtil.fail(this.z, "initialization failure", resultCode);
/*     */     } else {
/* 231 */       resultCode = this.z.deflateSetDictionary(dictionary, dictionary.length);
/* 232 */       if (resultCode != 0) {
/* 233 */         ZlibUtil.fail(this.z, "failed to set the dictionary", resultCode);
/*     */       }
/*     */     } 
/*     */     
/* 237 */     this.wrapperOverhead = ZlibUtil.wrapperOverhead(ZlibWrapper.ZLIB);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture close() {
/* 242 */     return close(ctx().channel().newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture close(final ChannelPromise promise) {
/* 247 */     ChannelHandlerContext ctx = ctx();
/* 248 */     EventExecutor executor = ctx.executor();
/* 249 */     if (executor.inEventLoop()) {
/* 250 */       return finishEncode(ctx, promise);
/*     */     }
/* 252 */     final ChannelPromise p = ctx.newPromise();
/* 253 */     executor.execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 256 */             ChannelFuture f = JZlibEncoder.this.finishEncode(JZlibEncoder.this.ctx(), p);
/* 257 */             f.addListener((GenericFutureListener)new ChannelPromiseNotifier(new ChannelPromise[] { this.val$promise }));
/*     */           }
/*     */         });
/* 260 */     return (ChannelFuture)p;
/*     */   }
/*     */ 
/*     */   
/*     */   private ChannelHandlerContext ctx() {
/* 265 */     ChannelHandlerContext ctx = this.ctx;
/* 266 */     if (ctx == null) {
/* 267 */       throw new IllegalStateException("not added to a pipeline");
/*     */     }
/* 269 */     return ctx;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 274 */     return this.finished;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
/* 279 */     if (this.finished) {
/* 280 */       out.writeBytes(in);
/*     */       
/*     */       return;
/*     */     } 
/* 284 */     int inputLength = in.readableBytes();
/* 285 */     if (inputLength == 0) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/*     */       int resultCode;
/* 291 */       boolean inHasArray = in.hasArray();
/* 292 */       this.z.avail_in = inputLength;
/* 293 */       if (inHasArray) {
/* 294 */         this.z.next_in = in.array();
/* 295 */         this.z.next_in_index = in.arrayOffset() + in.readerIndex();
/*     */       } else {
/* 297 */         byte[] array = new byte[inputLength];
/* 298 */         in.getBytes(in.readerIndex(), array);
/* 299 */         this.z.next_in = array;
/* 300 */         this.z.next_in_index = 0;
/*     */       } 
/* 302 */       int oldNextInIndex = this.z.next_in_index;
/*     */ 
/*     */       
/* 305 */       int maxOutputLength = (int)Math.ceil(inputLength * 1.001D) + 12 + this.wrapperOverhead;
/* 306 */       out.ensureWritable(maxOutputLength);
/* 307 */       this.z.avail_out = maxOutputLength;
/* 308 */       this.z.next_out = out.array();
/* 309 */       this.z.next_out_index = out.arrayOffset() + out.writerIndex();
/* 310 */       int oldNextOutIndex = this.z.next_out_index;
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 315 */         resultCode = this.z.deflate(2);
/*     */       } finally {
/* 317 */         in.skipBytes(this.z.next_in_index - oldNextInIndex);
/*     */       } 
/*     */       
/* 320 */       if (resultCode != 0) {
/* 321 */         ZlibUtil.fail(this.z, "compression failure", resultCode);
/*     */       }
/*     */       
/* 324 */       int outputLength = this.z.next_out_index - oldNextOutIndex;
/* 325 */       if (outputLength > 0) {
/* 326 */         out.writerIndex(out.writerIndex() + outputLength);
/*     */       
/*     */       }
/*     */     
/*     */     }
/*     */     finally {
/*     */       
/* 333 */       this.z.next_in = null;
/* 334 */       this.z.next_out = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) {
/* 342 */     ChannelFuture f = finishEncode(ctx, ctx.newPromise());
/* 343 */     f.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture f) throws Exception {
/* 346 */             ctx.close(promise);
/*     */           }
/*     */         });
/*     */     
/* 350 */     if (!f.isDone())
/*     */     {
/* 352 */       ctx.executor().schedule(new Runnable()
/*     */           {
/*     */             public void run() {
/* 355 */               ctx.close(promise);
/*     */             }
/*     */           },  10L, TimeUnit.SECONDS); } 
/*     */   }
/*     */   
/*     */   private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
/*     */     ByteBuf footer;
/* 362 */     if (this.finished) {
/* 363 */       promise.setSuccess();
/* 364 */       return (ChannelFuture)promise;
/*     */     } 
/* 366 */     this.finished = true;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 371 */       this.z.next_in = EmptyArrays.EMPTY_BYTES;
/* 372 */       this.z.next_in_index = 0;
/* 373 */       this.z.avail_in = 0;
/*     */ 
/*     */       
/* 376 */       byte[] out = new byte[32];
/* 377 */       this.z.next_out = out;
/* 378 */       this.z.next_out_index = 0;
/* 379 */       this.z.avail_out = out.length;
/*     */ 
/*     */       
/* 382 */       int resultCode = this.z.deflate(4);
/* 383 */       if (resultCode != 0 && resultCode != 1) {
/* 384 */         promise.setFailure((Throwable)ZlibUtil.deflaterException(this.z, "compression failure", resultCode));
/* 385 */         return (ChannelFuture)promise;
/* 386 */       }  if (this.z.next_out_index != 0) {
/* 387 */         footer = Unpooled.wrappedBuffer(out, 0, this.z.next_out_index);
/*     */       } else {
/* 389 */         footer = Unpooled.EMPTY_BUFFER;
/*     */       } 
/*     */     } finally {
/* 392 */       this.z.deflateEnd();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 398 */       this.z.next_in = null;
/* 399 */       this.z.next_out = null;
/*     */     } 
/* 401 */     return ctx.writeAndFlush(footer, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 406 */     this.ctx = ctx;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\JZlibEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */