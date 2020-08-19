/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.zip.Checksum;
/*     */ import net.jpountz.lz4.LZ4Compressor;
/*     */ import net.jpountz.lz4.LZ4Exception;
/*     */ import net.jpountz.lz4.LZ4Factory;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromiseNotifier;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.EncoderException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.MessageToByteEncoder;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
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
/*     */ public class Lz4FrameEncoder
/*     */   extends MessageToByteEncoder<ByteBuf>
/*     */ {
/*     */   static final int DEFAULT_MAX_ENCODE_SIZE = 2147483647;
/*     */   private final int blockSize;
/*     */   private final LZ4Compressor compressor;
/*     */   private final ByteBufChecksum checksum;
/*     */   private final int compressionLevel;
/*     */   private ByteBuf buffer;
/*     */   private final int maxEncodeSize;
/*     */   private volatile boolean finished;
/*     */   private volatile ChannelHandlerContext ctx;
/*     */   
/*     */   public Lz4FrameEncoder() {
/* 115 */     this(false);
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
/*     */   public Lz4FrameEncoder(boolean highCompressor) {
/* 127 */     this(LZ4Factory.fastestInstance(), highCompressor, 65536, new Lz4XXHash32(-1756908916));
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
/*     */   public Lz4FrameEncoder(LZ4Factory factory, boolean highCompressor, int blockSize, Checksum checksum) {
/* 143 */     this(factory, highCompressor, blockSize, checksum, 2147483647);
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
/*     */   public Lz4FrameEncoder(LZ4Factory factory, boolean highCompressor, int blockSize, Checksum checksum, int maxEncodeSize) {
/* 161 */     ObjectUtil.checkNotNull(factory, "factory");
/* 162 */     ObjectUtil.checkNotNull(checksum, "checksum");
/*     */     
/* 164 */     this.compressor = highCompressor ? factory.highCompressor() : factory.fastCompressor();
/* 165 */     this.checksum = ByteBufChecksum.wrapChecksum(checksum);
/*     */     
/* 167 */     this.compressionLevel = compressionLevel(blockSize);
/* 168 */     this.blockSize = blockSize;
/* 169 */     this.maxEncodeSize = ObjectUtil.checkPositive(maxEncodeSize, "maxEncodeSize");
/* 170 */     this.finished = false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int compressionLevel(int blockSize) {
/* 177 */     if (blockSize < 64 || blockSize > 33554432)
/* 178 */       throw new IllegalArgumentException(String.format("blockSize: %d (expected: %d-%d)", new Object[] {
/* 179 */               Integer.valueOf(blockSize), Integer.valueOf(64), Integer.valueOf(33554432)
/*     */             })); 
/* 181 */     int compressionLevel = 32 - Integer.numberOfLeadingZeros(blockSize - 1);
/* 182 */     compressionLevel = Math.max(0, compressionLevel - 10);
/* 183 */     return compressionLevel;
/*     */   }
/*     */ 
/*     */   
/*     */   protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) {
/* 188 */     return allocateBuffer(ctx, msg, preferDirect, true);
/*     */   }
/*     */ 
/*     */   
/*     */   private ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect, boolean allowEmptyReturn) {
/* 193 */     int targetBufSize = 0;
/* 194 */     int remaining = msg.readableBytes() + this.buffer.readableBytes();
/*     */ 
/*     */     
/* 197 */     if (remaining < 0) {
/* 198 */       throw new EncoderException("too much data to allocate a buffer for compression");
/*     */     }
/*     */     
/* 201 */     while (remaining > 0) {
/* 202 */       int curSize = Math.min(this.blockSize, remaining);
/* 203 */       remaining -= curSize;
/*     */       
/* 205 */       targetBufSize += this.compressor.maxCompressedLength(curSize) + 21;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 211 */     if (targetBufSize > this.maxEncodeSize || 0 > targetBufSize) {
/* 212 */       throw new EncoderException(String.format("requested encode buffer size (%d bytes) exceeds the maximum allowable size (%d bytes)", new Object[] {
/* 213 */               Integer.valueOf(targetBufSize), Integer.valueOf(this.maxEncodeSize)
/*     */             }));
/*     */     }
/* 216 */     if (allowEmptyReturn && targetBufSize < this.blockSize) {
/* 217 */       return Unpooled.EMPTY_BUFFER;
/*     */     }
/*     */     
/* 220 */     if (preferDirect) {
/* 221 */       return ctx.alloc().ioBuffer(targetBufSize, targetBufSize);
/*     */     }
/* 223 */     return ctx.alloc().heapBuffer(targetBufSize, targetBufSize);
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
/*     */   protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
/* 236 */     if (this.finished) {
/* 237 */       if (!out.isWritable(in.readableBytes()))
/*     */       {
/* 239 */         throw new IllegalStateException("encode finished and not enough space to write remaining data");
/*     */       }
/* 241 */       out.writeBytes(in);
/*     */       
/*     */       return;
/*     */     } 
/* 245 */     ByteBuf buffer = this.buffer;
/*     */     int length;
/* 247 */     while ((length = in.readableBytes()) > 0) {
/* 248 */       int nextChunkSize = Math.min(length, buffer.writableBytes());
/* 249 */       in.readBytes(buffer, nextChunkSize);
/*     */       
/* 251 */       if (!buffer.isWritable()) {
/* 252 */         flushBufferedData(out);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void flushBufferedData(ByteBuf out) {
/* 258 */     int compressedLength, blockType, flushableBytes = this.buffer.readableBytes();
/* 259 */     if (flushableBytes == 0) {
/*     */       return;
/*     */     }
/* 262 */     this.checksum.reset();
/* 263 */     this.checksum.update(this.buffer, this.buffer.readerIndex(), flushableBytes);
/* 264 */     int check = (int)this.checksum.getValue();
/*     */     
/* 266 */     int bufSize = this.compressor.maxCompressedLength(flushableBytes) + 21;
/* 267 */     out.ensureWritable(bufSize);
/* 268 */     int idx = out.writerIndex();
/*     */     
/*     */     try {
/* 271 */       ByteBuffer outNioBuffer = out.internalNioBuffer(idx + 21, out.writableBytes() - 21);
/* 272 */       int pos = outNioBuffer.position();
/*     */       
/* 274 */       this.compressor.compress(this.buffer.internalNioBuffer(this.buffer.readerIndex(), flushableBytes), outNioBuffer);
/* 275 */       compressedLength = outNioBuffer.position() - pos;
/* 276 */     } catch (LZ4Exception e) {
/* 277 */       throw new CompressionException(e);
/*     */     } 
/*     */     
/* 280 */     if (compressedLength >= flushableBytes) {
/* 281 */       blockType = 16;
/* 282 */       compressedLength = flushableBytes;
/* 283 */       out.setBytes(idx + 21, this.buffer, 0, flushableBytes);
/*     */     } else {
/* 285 */       blockType = 32;
/*     */     } 
/*     */     
/* 288 */     out.setLong(idx, 5501767354678207339L);
/* 289 */     out.setByte(idx + 8, (byte)(blockType | this.compressionLevel));
/* 290 */     out.setIntLE(idx + 9, compressedLength);
/* 291 */     out.setIntLE(idx + 13, flushableBytes);
/* 292 */     out.setIntLE(idx + 17, check);
/* 293 */     out.writerIndex(idx + 21 + compressedLength);
/* 294 */     this.buffer.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush(ChannelHandlerContext ctx) throws Exception {
/* 299 */     if (this.buffer != null && this.buffer.isReadable()) {
/* 300 */       ByteBuf buf = allocateBuffer(ctx, Unpooled.EMPTY_BUFFER, isPreferDirect(), false);
/* 301 */       flushBufferedData(buf);
/* 302 */       ctx.write(buf);
/*     */     } 
/* 304 */     ctx.flush();
/*     */   }
/*     */   
/*     */   private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
/* 308 */     if (this.finished) {
/* 309 */       promise.setSuccess();
/* 310 */       return (ChannelFuture)promise;
/*     */     } 
/* 312 */     this.finished = true;
/*     */     
/* 314 */     ByteBuf footer = ctx.alloc().heapBuffer(this.compressor
/* 315 */         .maxCompressedLength(this.buffer.readableBytes()) + 21);
/* 316 */     flushBufferedData(footer);
/*     */     
/* 318 */     int idx = footer.writerIndex();
/* 319 */     footer.setLong(idx, 5501767354678207339L);
/* 320 */     footer.setByte(idx + 8, (byte)(0x10 | this.compressionLevel));
/* 321 */     footer.setInt(idx + 9, 0);
/* 322 */     footer.setInt(idx + 13, 0);
/* 323 */     footer.setInt(idx + 17, 0);
/*     */     
/* 325 */     footer.writerIndex(idx + 21);
/*     */     
/* 327 */     return ctx.writeAndFlush(footer, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 334 */     return this.finished;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture close() {
/* 343 */     return close(ctx().newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture close(final ChannelPromise promise) {
/* 352 */     ChannelHandlerContext ctx = ctx();
/* 353 */     EventExecutor executor = ctx.executor();
/* 354 */     if (executor.inEventLoop()) {
/* 355 */       return finishEncode(ctx, promise);
/*     */     }
/* 357 */     executor.execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 360 */             ChannelFuture f = Lz4FrameEncoder.this.finishEncode(Lz4FrameEncoder.this.ctx(), promise);
/* 361 */             f.addListener((GenericFutureListener)new ChannelPromiseNotifier(new ChannelPromise[] { this.val$promise }));
/*     */           }
/*     */         });
/* 364 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
/* 370 */     ChannelFuture f = finishEncode(ctx, ctx.newPromise());
/* 371 */     f.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture f) throws Exception {
/* 374 */             ctx.close(promise);
/*     */           }
/*     */         });
/*     */     
/* 378 */     if (!f.isDone())
/*     */     {
/* 380 */       ctx.executor().schedule(new Runnable()
/*     */           {
/*     */             public void run() {
/* 383 */               ctx.close(promise);
/*     */             }
/*     */           },  10L, TimeUnit.SECONDS);
/*     */     }
/*     */   }
/*     */   
/*     */   private ChannelHandlerContext ctx() {
/* 390 */     ChannelHandlerContext ctx = this.ctx;
/* 391 */     if (ctx == null) {
/* 392 */       throw new IllegalStateException("not added to a pipeline");
/*     */     }
/* 394 */     return ctx;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) {
/* 399 */     this.ctx = ctx;
/*     */     
/* 401 */     this.buffer = Unpooled.wrappedBuffer(new byte[this.blockSize]);
/* 402 */     this.buffer.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
/* 407 */     super.handlerRemoved(ctx);
/* 408 */     if (this.buffer != null) {
/* 409 */       this.buffer.release();
/* 410 */       this.buffer = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   final ByteBuf getBackingBuffer() {
/* 415 */     return this.buffer;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\Lz4FrameEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */