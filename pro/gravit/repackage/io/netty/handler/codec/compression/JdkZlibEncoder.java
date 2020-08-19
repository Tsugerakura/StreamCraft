/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.Deflater;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromiseNotifier;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
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
/*     */ public class JdkZlibEncoder
/*     */   extends ZlibEncoder
/*     */ {
/*     */   private final ZlibWrapper wrapper;
/*     */   private final Deflater deflater;
/*     */   private volatile boolean finished;
/*     */   private volatile ChannelHandlerContext ctx;
/*  46 */   private final CRC32 crc = new CRC32();
/*  47 */   private static final byte[] gzipHeader = new byte[] { 31, -117, 8, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean writeHeader = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdkZlibEncoder() {
/*  57 */     this(6);
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
/*     */   public JdkZlibEncoder(int compressionLevel) {
/*  72 */     this(ZlibWrapper.ZLIB, compressionLevel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdkZlibEncoder(ZlibWrapper wrapper) {
/*  82 */     this(wrapper, 6);
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
/*     */   public JdkZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
/*  97 */     if (compressionLevel < 0 || compressionLevel > 9) {
/*  98 */       throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
/*     */     }
/*     */     
/* 101 */     ObjectUtil.checkNotNull(wrapper, "wrapper");
/* 102 */     if (wrapper == ZlibWrapper.ZLIB_OR_NONE) {
/* 103 */       throw new IllegalArgumentException("wrapper '" + ZlibWrapper.ZLIB_OR_NONE + "' is not allowed for compression.");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 108 */     this.wrapper = wrapper;
/* 109 */     this.deflater = new Deflater(compressionLevel, (wrapper != ZlibWrapper.ZLIB));
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
/*     */   public JdkZlibEncoder(byte[] dictionary) {
/* 123 */     this(6, dictionary);
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
/*     */   public JdkZlibEncoder(int compressionLevel, byte[] dictionary) {
/* 141 */     if (compressionLevel < 0 || compressionLevel > 9) {
/* 142 */       throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
/*     */     }
/*     */     
/* 145 */     ObjectUtil.checkNotNull(dictionary, "dictionary");
/*     */     
/* 147 */     this.wrapper = ZlibWrapper.ZLIB;
/* 148 */     this.deflater = new Deflater(compressionLevel);
/* 149 */     this.deflater.setDictionary(dictionary);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture close() {
/* 154 */     return close(ctx().newPromise());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture close(final ChannelPromise promise) {
/* 159 */     ChannelHandlerContext ctx = ctx();
/* 160 */     EventExecutor executor = ctx.executor();
/* 161 */     if (executor.inEventLoop()) {
/* 162 */       return finishEncode(ctx, promise);
/*     */     }
/* 164 */     final ChannelPromise p = ctx.newPromise();
/* 165 */     executor.execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 168 */             ChannelFuture f = JdkZlibEncoder.this.finishEncode(JdkZlibEncoder.this.ctx(), p);
/* 169 */             f.addListener((GenericFutureListener)new ChannelPromiseNotifier(new ChannelPromise[] { this.val$promise }));
/*     */           }
/*     */         });
/* 172 */     return (ChannelFuture)p;
/*     */   }
/*     */ 
/*     */   
/*     */   private ChannelHandlerContext ctx() {
/* 177 */     ChannelHandlerContext ctx = this.ctx;
/* 178 */     if (ctx == null) {
/* 179 */       throw new IllegalStateException("not added to a pipeline");
/*     */     }
/* 181 */     return ctx;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 186 */     return this.finished;
/*     */   }
/*     */   protected void encode(ChannelHandlerContext ctx, ByteBuf uncompressed, ByteBuf out) throws Exception {
/*     */     int offset;
/*     */     byte[] inAry;
/* 191 */     if (this.finished) {
/* 192 */       out.writeBytes(uncompressed);
/*     */       
/*     */       return;
/*     */     } 
/* 196 */     int len = uncompressed.readableBytes();
/* 197 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 203 */     if (uncompressed.hasArray()) {
/*     */       
/* 205 */       inAry = uncompressed.array();
/* 206 */       offset = uncompressed.arrayOffset() + uncompressed.readerIndex();
/*     */       
/* 208 */       uncompressed.skipBytes(len);
/*     */     } else {
/* 210 */       inAry = new byte[len];
/* 211 */       uncompressed.readBytes(inAry);
/* 212 */       offset = 0;
/*     */     } 
/*     */     
/* 215 */     if (this.writeHeader) {
/* 216 */       this.writeHeader = false;
/* 217 */       if (this.wrapper == ZlibWrapper.GZIP) {
/* 218 */         out.writeBytes(gzipHeader);
/*     */       }
/*     */     } 
/*     */     
/* 222 */     if (this.wrapper == ZlibWrapper.GZIP) {
/* 223 */       this.crc.update(inAry, offset, len);
/*     */     }
/*     */     
/* 226 */     this.deflater.setInput(inAry, offset, len);
/*     */     while (true) {
/* 228 */       deflate(out);
/* 229 */       if (this.deflater.needsInput()) {
/*     */         break;
/*     */       }
/*     */       
/* 233 */       if (!out.isWritable())
/*     */       {
/*     */         
/* 236 */         out.ensureWritable(out.writerIndex());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
/* 245 */     int sizeEstimate = (int)Math.ceil(msg.readableBytes() * 1.001D) + 12;
/* 246 */     if (this.writeHeader) {
/* 247 */       switch (this.wrapper) {
/*     */         case GZIP:
/* 249 */           sizeEstimate += gzipHeader.length;
/*     */           break;
/*     */         case ZLIB:
/* 252 */           sizeEstimate += 2;
/*     */           break;
/*     */       } 
/*     */ 
/*     */     
/*     */     }
/* 258 */     return ctx.alloc().heapBuffer(sizeEstimate);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
/* 263 */     ChannelFuture f = finishEncode(ctx, ctx.newPromise());
/* 264 */     f.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture f) throws Exception {
/* 267 */             ctx.close(promise);
/*     */           }
/*     */         });
/*     */     
/* 271 */     if (!f.isDone())
/*     */     {
/* 273 */       ctx.executor().schedule(new Runnable()
/*     */           {
/*     */             public void run() {
/* 276 */               ctx.close(promise);
/*     */             }
/*     */           },  10L, TimeUnit.SECONDS);
/*     */     }
/*     */   }
/*     */   
/*     */   private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
/* 283 */     if (this.finished) {
/* 284 */       promise.setSuccess();
/* 285 */       return (ChannelFuture)promise;
/*     */     } 
/*     */     
/* 288 */     this.finished = true;
/* 289 */     ByteBuf footer = ctx.alloc().heapBuffer();
/* 290 */     if (this.writeHeader && this.wrapper == ZlibWrapper.GZIP) {
/*     */       
/* 292 */       this.writeHeader = false;
/* 293 */       footer.writeBytes(gzipHeader);
/*     */     } 
/*     */     
/* 296 */     this.deflater.finish();
/*     */     
/* 298 */     while (!this.deflater.finished()) {
/* 299 */       deflate(footer);
/* 300 */       if (!footer.isWritable()) {
/*     */         
/* 302 */         ctx.write(footer);
/* 303 */         footer = ctx.alloc().heapBuffer();
/*     */       } 
/*     */     } 
/* 306 */     if (this.wrapper == ZlibWrapper.GZIP) {
/* 307 */       int crcValue = (int)this.crc.getValue();
/* 308 */       int uncBytes = this.deflater.getTotalIn();
/* 309 */       footer.writeByte(crcValue);
/* 310 */       footer.writeByte(crcValue >>> 8);
/* 311 */       footer.writeByte(crcValue >>> 16);
/* 312 */       footer.writeByte(crcValue >>> 24);
/* 313 */       footer.writeByte(uncBytes);
/* 314 */       footer.writeByte(uncBytes >>> 8);
/* 315 */       footer.writeByte(uncBytes >>> 16);
/* 316 */       footer.writeByte(uncBytes >>> 24);
/*     */     } 
/* 318 */     this.deflater.end();
/* 319 */     return ctx.writeAndFlush(footer, promise);
/*     */   }
/*     */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */   private void deflate(ByteBuf out) {
/*     */     int numBytes;
/* 324 */     if (PlatformDependent.javaVersion() < 7) {
/* 325 */       deflateJdk6(out);
/*     */     }
/*     */     
/*     */     do {
/* 329 */       int writerIndex = out.writerIndex();
/* 330 */       numBytes = this.deflater.deflate(out
/* 331 */           .array(), out.arrayOffset() + writerIndex, out.writableBytes(), 2);
/* 332 */       out.writerIndex(writerIndex + numBytes);
/* 333 */     } while (numBytes > 0);
/*     */   }
/*     */   
/*     */   private void deflateJdk6(ByteBuf out) {
/*     */     int numBytes;
/*     */     do {
/* 339 */       int writerIndex = out.writerIndex();
/* 340 */       numBytes = this.deflater.deflate(out
/* 341 */           .array(), out.arrayOffset() + writerIndex, out.writableBytes());
/* 342 */       out.writerIndex(writerIndex + numBytes);
/* 343 */     } while (numBytes > 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 348 */     this.ctx = ctx;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\JdkZlibEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */