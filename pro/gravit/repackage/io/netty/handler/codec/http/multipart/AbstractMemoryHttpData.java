/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.charset.Charset;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.CompositeByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpConstants;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
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
/*     */ public abstract class AbstractMemoryHttpData
/*     */   extends AbstractHttpData
/*     */ {
/*     */   private ByteBuf byteBuf;
/*     */   private int chunkPosition;
/*     */   
/*     */   protected AbstractMemoryHttpData(String name, Charset charset, long size) {
/*  45 */     super(name, charset, size);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(ByteBuf buffer) throws IOException {
/*  50 */     ObjectUtil.checkNotNull(buffer, "buffer");
/*  51 */     long localsize = buffer.readableBytes();
/*  52 */     checkSize(localsize);
/*  53 */     if (this.definedSize > 0L && this.definedSize < localsize) {
/*  54 */       throw new IOException("Out of size: " + localsize + " > " + this.definedSize);
/*     */     }
/*     */     
/*  57 */     if (this.byteBuf != null) {
/*  58 */       this.byteBuf.release();
/*     */     }
/*  60 */     this.byteBuf = buffer;
/*  61 */     this.size = localsize;
/*  62 */     setCompleted();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(InputStream inputStream) throws IOException {
/*  67 */     ObjectUtil.checkNotNull(inputStream, "inputStream");
/*     */     
/*  69 */     ByteBuf buffer = Unpooled.buffer();
/*  70 */     byte[] bytes = new byte[16384];
/*  71 */     int read = inputStream.read(bytes);
/*  72 */     int written = 0;
/*  73 */     while (read > 0) {
/*  74 */       buffer.writeBytes(bytes, 0, read);
/*  75 */       written += read;
/*  76 */       checkSize(written);
/*  77 */       read = inputStream.read(bytes);
/*     */     } 
/*  79 */     this.size = written;
/*  80 */     if (this.definedSize > 0L && this.definedSize < this.size) {
/*  81 */       throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
/*     */     }
/*  83 */     if (this.byteBuf != null) {
/*  84 */       this.byteBuf.release();
/*     */     }
/*  86 */     this.byteBuf = buffer;
/*  87 */     setCompleted();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addContent(ByteBuf buffer, boolean last) throws IOException {
/*  93 */     if (buffer != null) {
/*  94 */       long localsize = buffer.readableBytes();
/*  95 */       checkSize(this.size + localsize);
/*  96 */       if (this.definedSize > 0L && this.definedSize < this.size + localsize) {
/*  97 */         throw new IOException("Out of size: " + (this.size + localsize) + " > " + this.definedSize);
/*     */       }
/*     */       
/* 100 */       this.size += localsize;
/* 101 */       if (this.byteBuf == null) {
/* 102 */         this.byteBuf = buffer;
/* 103 */       } else if (this.byteBuf instanceof CompositeByteBuf) {
/* 104 */         CompositeByteBuf cbb = (CompositeByteBuf)this.byteBuf;
/* 105 */         cbb.addComponent(true, buffer);
/*     */       } else {
/* 107 */         CompositeByteBuf cbb = Unpooled.compositeBuffer(2147483647);
/* 108 */         cbb.addComponents(true, new ByteBuf[] { this.byteBuf, buffer });
/* 109 */         this.byteBuf = (ByteBuf)cbb;
/*     */       } 
/*     */     } 
/* 112 */     if (last) {
/* 113 */       setCompleted();
/*     */     } else {
/* 115 */       ObjectUtil.checkNotNull(buffer, "buffer");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(File file) throws IOException {
/* 121 */     ObjectUtil.checkNotNull(file, "file");
/*     */     
/* 123 */     long newsize = file.length();
/* 124 */     if (newsize > 2147483647L) {
/* 125 */       throw new IllegalArgumentException("File too big to be loaded in memory");
/*     */     }
/* 127 */     checkSize(newsize);
/* 128 */     RandomAccessFile accessFile = new RandomAccessFile(file, "r");
/* 129 */     FileChannel fileChannel = accessFile.getChannel();
/* 130 */     byte[] array = new byte[(int)newsize];
/* 131 */     ByteBuffer byteBuffer = ByteBuffer.wrap(array);
/* 132 */     int read = 0;
/* 133 */     while (read < newsize) {
/* 134 */       read += fileChannel.read(byteBuffer);
/*     */     }
/* 136 */     fileChannel.close();
/* 137 */     accessFile.close();
/* 138 */     byteBuffer.flip();
/* 139 */     if (this.byteBuf != null) {
/* 140 */       this.byteBuf.release();
/*     */     }
/* 142 */     this.byteBuf = Unpooled.wrappedBuffer(2147483647, new ByteBuffer[] { byteBuffer });
/* 143 */     this.size = newsize;
/* 144 */     setCompleted();
/*     */   }
/*     */ 
/*     */   
/*     */   public void delete() {
/* 149 */     if (this.byteBuf != null) {
/* 150 */       this.byteBuf.release();
/* 151 */       this.byteBuf = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] get() {
/* 157 */     if (this.byteBuf == null) {
/* 158 */       return Unpooled.EMPTY_BUFFER.array();
/*     */     }
/* 160 */     byte[] array = new byte[this.byteBuf.readableBytes()];
/* 161 */     this.byteBuf.getBytes(this.byteBuf.readerIndex(), array);
/* 162 */     return array;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 167 */     return getString(HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString(Charset encoding) {
/* 172 */     if (this.byteBuf == null) {
/* 173 */       return "";
/*     */     }
/* 175 */     if (encoding == null) {
/* 176 */       encoding = HttpConstants.DEFAULT_CHARSET;
/*     */     }
/* 178 */     return this.byteBuf.toString(encoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf getByteBuf() {
/* 188 */     return this.byteBuf;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getChunk(int length) throws IOException {
/* 193 */     if (this.byteBuf == null || length == 0 || this.byteBuf.readableBytes() == 0) {
/* 194 */       this.chunkPosition = 0;
/* 195 */       return Unpooled.EMPTY_BUFFER;
/*     */     } 
/* 197 */     int sizeLeft = this.byteBuf.readableBytes() - this.chunkPosition;
/* 198 */     if (sizeLeft == 0) {
/* 199 */       this.chunkPosition = 0;
/* 200 */       return Unpooled.EMPTY_BUFFER;
/*     */     } 
/* 202 */     int sliceLength = length;
/* 203 */     if (sizeLeft < length) {
/* 204 */       sliceLength = sizeLeft;
/*     */     }
/* 206 */     ByteBuf chunk = this.byteBuf.retainedSlice(this.chunkPosition, sliceLength);
/* 207 */     this.chunkPosition += sliceLength;
/* 208 */     return chunk;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInMemory() {
/* 213 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean renameTo(File dest) throws IOException {
/* 218 */     ObjectUtil.checkNotNull(dest, "dest");
/* 219 */     if (this.byteBuf == null) {
/*     */       
/* 221 */       if (!dest.createNewFile()) {
/* 222 */         throw new IOException("file exists already: " + dest);
/*     */       }
/* 224 */       return true;
/*     */     } 
/* 226 */     int length = this.byteBuf.readableBytes();
/* 227 */     RandomAccessFile accessFile = new RandomAccessFile(dest, "rw");
/* 228 */     FileChannel fileChannel = accessFile.getChannel();
/* 229 */     int written = 0;
/* 230 */     if (this.byteBuf.nioBufferCount() == 1) {
/* 231 */       ByteBuffer byteBuffer = this.byteBuf.nioBuffer();
/* 232 */       while (written < length) {
/* 233 */         written += fileChannel.write(byteBuffer);
/*     */       }
/*     */     } else {
/* 236 */       ByteBuffer[] byteBuffers = this.byteBuf.nioBuffers();
/* 237 */       while (written < length) {
/* 238 */         written = (int)(written + fileChannel.write(byteBuffers));
/*     */       }
/*     */     } 
/*     */     
/* 242 */     fileChannel.force(false);
/* 243 */     fileChannel.close();
/* 244 */     accessFile.close();
/* 245 */     return (written == length);
/*     */   }
/*     */ 
/*     */   
/*     */   public File getFile() throws IOException {
/* 250 */     throw new IOException("Not represented by a file");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpData touch() {
/* 255 */     return touch((Object)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpData touch(Object hint) {
/* 260 */     if (this.byteBuf != null) {
/* 261 */       this.byteBuf.touch(hint);
/*     */     }
/* 263 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\AbstractMemoryHttpData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */