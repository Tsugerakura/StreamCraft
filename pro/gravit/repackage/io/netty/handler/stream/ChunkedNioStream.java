/*     */ package pro.gravit.repackage.io.netty.handler.stream;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ReadableByteChannel;
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
/*     */ public class ChunkedNioStream
/*     */   implements ChunkedInput<ByteBuf>
/*     */ {
/*     */   private final ReadableByteChannel in;
/*     */   private final int chunkSize;
/*     */   private long offset;
/*     */   private final ByteBuffer byteBuffer;
/*     */   
/*     */   public ChunkedNioStream(ReadableByteChannel in) {
/*  47 */     this(in, 8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkedNioStream(ReadableByteChannel in, int chunkSize) {
/*  57 */     ObjectUtil.checkNotNull(in, "in");
/*  58 */     if (chunkSize <= 0) {
/*  59 */       throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
/*     */     }
/*     */     
/*  62 */     this.in = in;
/*  63 */     this.offset = 0L;
/*  64 */     this.chunkSize = chunkSize;
/*  65 */     this.byteBuffer = ByteBuffer.allocate(chunkSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long transferredBytes() {
/*  72 */     return this.offset;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEndOfInput() throws Exception {
/*  77 */     if (this.byteBuffer.position() > 0)
/*     */     {
/*  79 */       return false;
/*     */     }
/*  81 */     if (this.in.isOpen()) {
/*     */       
/*  83 */       int b = this.in.read(this.byteBuffer);
/*  84 */       if (b < 0) {
/*  85 */         return true;
/*     */       }
/*  87 */       this.offset += b;
/*  88 */       return false;
/*     */     } 
/*     */     
/*  91 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws Exception {
/*  96 */     this.in.close();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
/* 102 */     return readChunk(ctx.alloc());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
/* 107 */     if (isEndOfInput()) {
/* 108 */       return null;
/*     */     }
/*     */     
/* 111 */     int readBytes = this.byteBuffer.position();
/*     */     do {
/* 113 */       int localReadBytes = this.in.read(this.byteBuffer);
/* 114 */       if (localReadBytes < 0) {
/*     */         break;
/*     */       }
/* 117 */       readBytes += localReadBytes;
/* 118 */       this.offset += localReadBytes;
/* 119 */     } while (readBytes != this.chunkSize);
/*     */ 
/*     */ 
/*     */     
/* 123 */     this.byteBuffer.flip();
/* 124 */     boolean release = true;
/* 125 */     ByteBuf buffer = allocator.buffer(this.byteBuffer.remaining());
/*     */     try {
/* 127 */       buffer.writeBytes(this.byteBuffer);
/* 128 */       this.byteBuffer.clear();
/* 129 */       release = false;
/* 130 */       return buffer;
/*     */     } finally {
/* 132 */       if (release) {
/* 133 */         buffer.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long length() {
/* 140 */     return -1L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long progress() {
/* 145 */     return this.offset;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\stream\ChunkedNioStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */