/*     */ package pro.gravit.repackage.io.netty.handler.stream;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
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
/*     */ public class ChunkedFile
/*     */   implements ChunkedInput<ByteBuf>
/*     */ {
/*     */   private final RandomAccessFile file;
/*     */   private final long startOffset;
/*     */   private final long endOffset;
/*     */   private final int chunkSize;
/*     */   private long offset;
/*     */   
/*     */   public ChunkedFile(File file) throws IOException {
/*  47 */     this(file, 8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkedFile(File file, int chunkSize) throws IOException {
/*  57 */     this(new RandomAccessFile(file, "r"), chunkSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkedFile(RandomAccessFile file) throws IOException {
/*  64 */     this(file, 8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkedFile(RandomAccessFile file, int chunkSize) throws IOException {
/*  74 */     this(file, 0L, file.length(), chunkSize);
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
/*     */   public ChunkedFile(RandomAccessFile file, long offset, long length, int chunkSize) throws IOException {
/*  86 */     ObjectUtil.checkNotNull(file, "file");
/*  87 */     ObjectUtil.checkPositiveOrZero(offset, "offset");
/*  88 */     ObjectUtil.checkPositiveOrZero(length, "length");
/*  89 */     ObjectUtil.checkPositive(chunkSize, "chunkSize");
/*     */     
/*  91 */     this.file = file;
/*  92 */     this.offset = this.startOffset = offset;
/*  93 */     this.endOffset = offset + length;
/*  94 */     this.chunkSize = chunkSize;
/*     */     
/*  96 */     file.seek(offset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long startOffset() {
/* 103 */     return this.startOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long endOffset() {
/* 110 */     return this.endOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long currentOffset() {
/* 117 */     return this.offset;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEndOfInput() throws Exception {
/* 122 */     return (this.offset >= this.endOffset || !this.file.getChannel().isOpen());
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws Exception {
/* 127 */     this.file.close();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
/* 133 */     return readChunk(ctx.alloc());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
/* 138 */     long offset = this.offset;
/* 139 */     if (offset >= this.endOffset) {
/* 140 */       return null;
/*     */     }
/*     */     
/* 143 */     int chunkSize = (int)Math.min(this.chunkSize, this.endOffset - offset);
/*     */ 
/*     */     
/* 146 */     ByteBuf buf = allocator.heapBuffer(chunkSize);
/* 147 */     boolean release = true;
/*     */     try {
/* 149 */       this.file.readFully(buf.array(), buf.arrayOffset(), chunkSize);
/* 150 */       buf.writerIndex(chunkSize);
/* 151 */       this.offset = offset + chunkSize;
/* 152 */       release = false;
/* 153 */       return buf;
/*     */     } finally {
/* 155 */       if (release) {
/* 156 */         buf.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long length() {
/* 163 */     return this.endOffset - this.startOffset;
/*     */   }
/*     */ 
/*     */   
/*     */   public long progress() {
/* 168 */     return this.offset - this.startOffset;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\stream\ChunkedFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */