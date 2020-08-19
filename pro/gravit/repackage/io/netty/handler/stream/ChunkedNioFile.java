/*     */ package pro.gravit.repackage.io.netty.handler.stream;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
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
/*     */ public class ChunkedNioFile
/*     */   implements ChunkedInput<ByteBuf>
/*     */ {
/*     */   private final FileChannel in;
/*     */   private final long startOffset;
/*     */   private final long endOffset;
/*     */   private final int chunkSize;
/*     */   private long offset;
/*     */   
/*     */   public ChunkedNioFile(File in) throws IOException {
/*  50 */     this((new RandomAccessFile(in, "r")).getChannel());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkedNioFile(File in, int chunkSize) throws IOException {
/*  60 */     this((new RandomAccessFile(in, "r")).getChannel(), chunkSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkedNioFile(FileChannel in) throws IOException {
/*  67 */     this(in, 8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkedNioFile(FileChannel in, int chunkSize) throws IOException {
/*  77 */     this(in, 0L, in.size(), chunkSize);
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
/*     */   public ChunkedNioFile(FileChannel in, long offset, long length, int chunkSize) throws IOException {
/*  90 */     ObjectUtil.checkNotNull(in, "in");
/*  91 */     ObjectUtil.checkPositiveOrZero(offset, "offset");
/*  92 */     ObjectUtil.checkPositiveOrZero(length, "length");
/*  93 */     ObjectUtil.checkPositive(chunkSize, "chunkSize");
/*  94 */     if (!in.isOpen()) {
/*  95 */       throw new ClosedChannelException();
/*     */     }
/*  97 */     this.in = in;
/*  98 */     this.chunkSize = chunkSize;
/*  99 */     this.offset = this.startOffset = offset;
/* 100 */     this.endOffset = offset + length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long startOffset() {
/* 107 */     return this.startOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long endOffset() {
/* 114 */     return this.endOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long currentOffset() {
/* 121 */     return this.offset;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEndOfInput() throws Exception {
/* 126 */     return (this.offset >= this.endOffset || !this.in.isOpen());
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws Exception {
/* 131 */     this.in.close();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
/* 137 */     return readChunk(ctx.alloc());
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf readChunk(ByteBufAllocator allocator) throws Exception {
/* 142 */     long offset = this.offset;
/* 143 */     if (offset >= this.endOffset) {
/* 144 */       return null;
/*     */     }
/*     */     
/* 147 */     int chunkSize = (int)Math.min(this.chunkSize, this.endOffset - offset);
/* 148 */     ByteBuf buffer = allocator.buffer(chunkSize);
/* 149 */     boolean release = true;
/*     */     try {
/* 151 */       int readBytes = 0;
/*     */       do {
/* 153 */         int localReadBytes = buffer.writeBytes(this.in, offset + readBytes, chunkSize - readBytes);
/* 154 */         if (localReadBytes < 0) {
/*     */           break;
/*     */         }
/* 157 */         readBytes += localReadBytes;
/* 158 */       } while (readBytes != chunkSize);
/*     */ 
/*     */ 
/*     */       
/* 162 */       this.offset += readBytes;
/* 163 */       release = false;
/* 164 */       return buffer;
/*     */     } finally {
/* 166 */       if (release) {
/* 167 */         buffer.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long length() {
/* 174 */     return this.endOffset - this.startOffset;
/*     */   }
/*     */ 
/*     */   
/*     */   public long progress() {
/* 179 */     return this.offset - this.startOffset;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\stream\ChunkedNioFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */