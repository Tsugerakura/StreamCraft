/*     */ package pro.gravit.repackage.io.netty.handler.stream;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.PushbackInputStream;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
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
/*     */ public class ChunkedStream
/*     */   implements ChunkedInput<ByteBuf>
/*     */ {
/*     */   static final int DEFAULT_CHUNK_SIZE = 8192;
/*     */   private final PushbackInputStream in;
/*     */   private final int chunkSize;
/*     */   private long offset;
/*     */   private boolean closed;
/*     */   
/*     */   public ChunkedStream(InputStream in) {
/*  49 */     this(in, 8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkedStream(InputStream in, int chunkSize) {
/*  59 */     ObjectUtil.checkNotNull(in, "in");
/*  60 */     ObjectUtil.checkPositive(chunkSize, "chunkSize");
/*     */     
/*  62 */     if (in instanceof PushbackInputStream) {
/*  63 */       this.in = (PushbackInputStream)in;
/*     */     } else {
/*  65 */       this.in = new PushbackInputStream(in);
/*     */     } 
/*  67 */     this.chunkSize = chunkSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long transferredBytes() {
/*  74 */     return this.offset;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEndOfInput() throws Exception {
/*  79 */     if (this.closed) {
/*  80 */       return true;
/*     */     }
/*     */     
/*  83 */     int b = this.in.read();
/*  84 */     if (b < 0) {
/*  85 */       return true;
/*     */     }
/*  87 */     this.in.unread(b);
/*  88 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws Exception {
/*  94 */     this.closed = true;
/*  95 */     this.in.close();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
/* 101 */     return readChunk(ctx.alloc());
/*     */   }
/*     */   
/*     */   public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
/*     */     int chunkSize;
/* 106 */     if (isEndOfInput()) {
/* 107 */       return null;
/*     */     }
/*     */     
/* 110 */     int availableBytes = this.in.available();
/*     */     
/* 112 */     if (availableBytes <= 0) {
/* 113 */       chunkSize = this.chunkSize;
/*     */     } else {
/* 115 */       chunkSize = Math.min(this.chunkSize, this.in.available());
/*     */     } 
/*     */     
/* 118 */     boolean release = true;
/* 119 */     ByteBuf buffer = allocator.buffer(chunkSize);
/*     */     
/*     */     try {
/* 122 */       this.offset += buffer.writeBytes(this.in, chunkSize);
/* 123 */       release = false;
/* 124 */       return buffer;
/*     */     } finally {
/* 126 */       if (release) {
/* 127 */         buffer.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long length() {
/* 134 */     return -1L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long progress() {
/* 139 */     return this.offset;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\stream\ChunkedStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */