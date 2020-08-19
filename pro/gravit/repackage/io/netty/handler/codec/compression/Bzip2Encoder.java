/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromiseNotifier;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.MessageToByteEncoder;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Bzip2Encoder
/*     */   extends MessageToByteEncoder<ByteBuf>
/*     */ {
/*     */   private enum State
/*     */   {
/*  42 */     INIT,
/*  43 */     INIT_BLOCK,
/*  44 */     WRITE_DATA,
/*  45 */     CLOSE_BLOCK;
/*     */   }
/*     */   
/*  48 */   private State currentState = State.INIT;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  53 */   private final Bzip2BitWriter writer = new Bzip2BitWriter();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int streamBlockSize;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int streamCRC;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Bzip2BlockCompressor blockCompressor;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean finished;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile ChannelHandlerContext ctx;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Bzip2Encoder() {
/*  84 */     this(9);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Bzip2Encoder(int blockSizeMultiplier) {
/*  95 */     if (blockSizeMultiplier < 1 || blockSizeMultiplier > 9) {
/*  96 */       throw new IllegalArgumentException("blockSizeMultiplier: " + blockSizeMultiplier + " (expected: 1-9)");
/*     */     }
/*     */     
/*  99 */     this.streamBlockSize = blockSizeMultiplier * 100000;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
/* 104 */     if (this.finished) {
/* 105 */       out.writeBytes(in); return;
/*     */     }  while (true) {
/*     */       Bzip2BlockCompressor blockCompressor;
/*     */       int length;
/*     */       int bytesWritten;
/* 110 */       switch (this.currentState) {
/*     */         case INIT:
/* 112 */           out.ensureWritable(4);
/* 113 */           out.writeMedium(4348520);
/* 114 */           out.writeByte(48 + this.streamBlockSize / 100000);
/* 115 */           this.currentState = State.INIT_BLOCK;
/*     */         
/*     */         case INIT_BLOCK:
/* 118 */           this.blockCompressor = new Bzip2BlockCompressor(this.writer, this.streamBlockSize);
/* 119 */           this.currentState = State.WRITE_DATA;
/*     */         
/*     */         case WRITE_DATA:
/* 122 */           if (!in.isReadable()) {
/*     */             return;
/*     */           }
/* 125 */           blockCompressor = this.blockCompressor;
/* 126 */           length = Math.min(in.readableBytes(), blockCompressor.availableSize());
/* 127 */           bytesWritten = blockCompressor.write(in, in.readerIndex(), length);
/* 128 */           in.skipBytes(bytesWritten);
/* 129 */           if (!blockCompressor.isFull()) {
/* 130 */             if (in.isReadable()) {
/*     */               continue;
/*     */             }
/*     */             
/*     */             return;
/*     */           } 
/* 136 */           this.currentState = State.CLOSE_BLOCK;
/*     */         
/*     */         case CLOSE_BLOCK:
/* 139 */           closeBlock(out);
/* 140 */           this.currentState = State.INIT_BLOCK; continue;
/*     */       }  break;
/*     */     } 
/* 143 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void closeBlock(ByteBuf out) {
/* 152 */     Bzip2BlockCompressor blockCompressor = this.blockCompressor;
/* 153 */     if (!blockCompressor.isEmpty()) {
/* 154 */       blockCompressor.close(out);
/* 155 */       int blockCRC = blockCompressor.crc();
/* 156 */       this.streamCRC = (this.streamCRC << 1 | this.streamCRC >>> 31) ^ blockCRC;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 164 */     return this.finished;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture close() {
/* 173 */     return close(ctx().newPromise());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelFuture close(final ChannelPromise promise) {
/* 182 */     ChannelHandlerContext ctx = ctx();
/* 183 */     EventExecutor executor = ctx.executor();
/* 184 */     if (executor.inEventLoop()) {
/* 185 */       return finishEncode(ctx, promise);
/*     */     }
/* 187 */     executor.execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 190 */             ChannelFuture f = Bzip2Encoder.this.finishEncode(Bzip2Encoder.this.ctx(), promise);
/* 191 */             f.addListener((GenericFutureListener)new ChannelPromiseNotifier(new ChannelPromise[] { this.val$promise }));
/*     */           }
/*     */         });
/* 194 */     return (ChannelFuture)promise;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
/* 200 */     ChannelFuture f = finishEncode(ctx, ctx.newPromise());
/* 201 */     f.addListener((GenericFutureListener)new ChannelFutureListener()
/*     */         {
/*     */           public void operationComplete(ChannelFuture f) throws Exception {
/* 204 */             ctx.close(promise);
/*     */           }
/*     */         });
/*     */     
/* 208 */     if (!f.isDone())
/*     */     {
/* 210 */       ctx.executor().schedule(new Runnable()
/*     */           {
/*     */             public void run() {
/* 213 */               ctx.close(promise);
/*     */             }
/*     */           },  10L, TimeUnit.SECONDS);
/*     */     }
/*     */   }
/*     */   
/*     */   private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
/* 220 */     if (this.finished) {
/* 221 */       promise.setSuccess();
/* 222 */       return (ChannelFuture)promise;
/*     */     } 
/* 224 */     this.finished = true;
/*     */     
/* 226 */     ByteBuf footer = ctx.alloc().buffer();
/* 227 */     closeBlock(footer);
/*     */     
/* 229 */     int streamCRC = this.streamCRC;
/* 230 */     Bzip2BitWriter writer = this.writer;
/*     */     try {
/* 232 */       writer.writeBits(footer, 24, 1536581L);
/* 233 */       writer.writeBits(footer, 24, 3690640L);
/* 234 */       writer.writeInt(footer, streamCRC);
/* 235 */       writer.flush(footer);
/*     */     } finally {
/* 237 */       this.blockCompressor = null;
/*     */     } 
/* 239 */     return ctx.writeAndFlush(footer, promise);
/*     */   }
/*     */   
/*     */   private ChannelHandlerContext ctx() {
/* 243 */     ChannelHandlerContext ctx = this.ctx;
/* 244 */     if (ctx == null) {
/* 245 */       throw new IllegalStateException("not added to a pipeline");
/*     */     }
/* 247 */     return ctx;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
/* 252 */     this.ctx = ctx;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\Bzip2Encoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */