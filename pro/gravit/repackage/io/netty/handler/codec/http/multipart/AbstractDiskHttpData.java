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
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpConstants;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public abstract class AbstractDiskHttpData
/*     */   extends AbstractHttpData
/*     */ {
/*  40 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractDiskHttpData.class);
/*     */   
/*     */   private File file;
/*     */   private boolean isRenamed;
/*     */   private FileChannel fileChannel;
/*     */   
/*     */   protected AbstractDiskHttpData(String name, Charset charset, long size) {
/*  47 */     super(name, charset, size);
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
/*     */ 
/*     */ 
/*     */   
/*     */   private File tempFile() throws IOException {
/*     */     String newpostfix;
/*     */     File tmpFile;
/*  81 */     String diskFilename = getDiskFilename();
/*  82 */     if (diskFilename != null) {
/*  83 */       newpostfix = '_' + diskFilename;
/*     */     } else {
/*  85 */       newpostfix = getPostfix();
/*     */     } 
/*     */     
/*  88 */     if (getBaseDirectory() == null) {
/*     */       
/*  90 */       tmpFile = File.createTempFile(getPrefix(), newpostfix);
/*     */     } else {
/*  92 */       tmpFile = File.createTempFile(getPrefix(), newpostfix, new File(
/*  93 */             getBaseDirectory()));
/*     */     } 
/*  95 */     if (deleteOnExit()) {
/*  96 */       tmpFile.deleteOnExit();
/*     */     }
/*  98 */     return tmpFile;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(ByteBuf buffer) throws IOException {
/* 103 */     ObjectUtil.checkNotNull(buffer, "buffer");
/*     */     try {
/* 105 */       this.size = buffer.readableBytes();
/* 106 */       checkSize(this.size);
/* 107 */       if (this.definedSize > 0L && this.definedSize < this.size) {
/* 108 */         throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
/*     */       }
/* 110 */       if (this.file == null) {
/* 111 */         this.file = tempFile();
/*     */       }
/* 113 */       if (buffer.readableBytes() == 0) {
/*     */         
/* 115 */         if (!this.file.createNewFile()) {
/* 116 */           if (this.file.length() == 0L) {
/*     */             return;
/*     */           }
/* 119 */           if (!this.file.delete() || !this.file.createNewFile()) {
/* 120 */             throw new IOException("file exists already: " + this.file);
/*     */           }
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/* 126 */       RandomAccessFile accessFile = new RandomAccessFile(this.file, "rw");
/* 127 */       accessFile.setLength(0L);
/*     */       try {
/* 129 */         FileChannel localfileChannel = accessFile.getChannel();
/* 130 */         ByteBuffer byteBuffer = buffer.nioBuffer();
/* 131 */         int written = 0;
/* 132 */         while (written < this.size) {
/* 133 */           written += localfileChannel.write(byteBuffer);
/*     */         }
/* 135 */         buffer.readerIndex(buffer.readerIndex() + written);
/* 136 */         localfileChannel.force(false);
/*     */       } finally {
/* 138 */         accessFile.close();
/*     */       } 
/* 140 */       setCompleted();
/*     */     }
/*     */     finally {
/*     */       
/* 144 */       buffer.release();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addContent(ByteBuf buffer, boolean last) throws IOException {
/* 151 */     if (buffer != null) {
/*     */       try {
/* 153 */         int localsize = buffer.readableBytes();
/* 154 */         checkSize(this.size + localsize);
/* 155 */         if (this.definedSize > 0L && this.definedSize < this.size + localsize) {
/* 156 */           throw new IOException("Out of size: " + (this.size + localsize) + " > " + this.definedSize);
/*     */         }
/*     */         
/* 159 */         ByteBuffer byteBuffer = (buffer.nioBufferCount() == 1) ? buffer.nioBuffer() : buffer.copy().nioBuffer();
/* 160 */         int written = 0;
/* 161 */         if (this.file == null) {
/* 162 */           this.file = tempFile();
/*     */         }
/* 164 */         if (this.fileChannel == null) {
/* 165 */           RandomAccessFile accessFile = new RandomAccessFile(this.file, "rw");
/* 166 */           this.fileChannel = accessFile.getChannel();
/*     */         } 
/* 168 */         while (written < localsize) {
/* 169 */           written += this.fileChannel.write(byteBuffer);
/*     */         }
/* 171 */         this.size += localsize;
/* 172 */         buffer.readerIndex(buffer.readerIndex() + written);
/*     */       }
/*     */       finally {
/*     */         
/* 176 */         buffer.release();
/*     */       } 
/*     */     }
/* 179 */     if (last) {
/* 180 */       if (this.file == null) {
/* 181 */         this.file = tempFile();
/*     */       }
/* 183 */       if (this.fileChannel == null) {
/* 184 */         RandomAccessFile accessFile = new RandomAccessFile(this.file, "rw");
/* 185 */         this.fileChannel = accessFile.getChannel();
/*     */       } 
/* 187 */       this.fileChannel.force(false);
/* 188 */       this.fileChannel.close();
/* 189 */       this.fileChannel = null;
/* 190 */       setCompleted();
/*     */     } else {
/* 192 */       ObjectUtil.checkNotNull(buffer, "buffer");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(File file) throws IOException {
/* 198 */     if (this.file != null) {
/* 199 */       delete();
/*     */     }
/* 201 */     this.file = file;
/* 202 */     this.size = file.length();
/* 203 */     checkSize(this.size);
/* 204 */     this.isRenamed = true;
/* 205 */     setCompleted();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(InputStream inputStream) throws IOException {
/* 210 */     ObjectUtil.checkNotNull(inputStream, "inputStream");
/* 211 */     if (this.file != null) {
/* 212 */       delete();
/*     */     }
/* 214 */     this.file = tempFile();
/* 215 */     RandomAccessFile accessFile = new RandomAccessFile(this.file, "rw");
/* 216 */     accessFile.setLength(0L);
/* 217 */     int written = 0;
/*     */     try {
/* 219 */       FileChannel localfileChannel = accessFile.getChannel();
/* 220 */       byte[] bytes = new byte[16384];
/* 221 */       ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
/* 222 */       int read = inputStream.read(bytes);
/* 223 */       while (read > 0) {
/* 224 */         byteBuffer.position(read).flip();
/* 225 */         written += localfileChannel.write(byteBuffer);
/* 226 */         checkSize(written);
/* 227 */         read = inputStream.read(bytes);
/*     */       } 
/* 229 */       localfileChannel.force(false);
/*     */     } finally {
/* 231 */       accessFile.close();
/*     */     } 
/* 233 */     this.size = written;
/* 234 */     if (this.definedSize > 0L && this.definedSize < this.size) {
/* 235 */       if (!this.file.delete()) {
/* 236 */         logger.warn("Failed to delete: {}", this.file);
/*     */       }
/* 238 */       this.file = null;
/* 239 */       throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
/*     */     } 
/* 241 */     this.isRenamed = true;
/* 242 */     setCompleted();
/*     */   }
/*     */ 
/*     */   
/*     */   public void delete() {
/* 247 */     if (this.fileChannel != null) {
/*     */       try {
/* 249 */         this.fileChannel.force(false);
/* 250 */         this.fileChannel.close();
/* 251 */       } catch (IOException e) {
/* 252 */         logger.warn("Failed to close a file.", e);
/*     */       } 
/* 254 */       this.fileChannel = null;
/*     */     } 
/* 256 */     if (!this.isRenamed) {
/* 257 */       if (this.file != null && this.file.exists() && 
/* 258 */         !this.file.delete()) {
/* 259 */         logger.warn("Failed to delete: {}", this.file);
/*     */       }
/*     */       
/* 262 */       this.file = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] get() throws IOException {
/* 268 */     if (this.file == null) {
/* 269 */       return EmptyArrays.EMPTY_BYTES;
/*     */     }
/* 271 */     return readFrom(this.file);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getByteBuf() throws IOException {
/* 276 */     if (this.file == null) {
/* 277 */       return Unpooled.EMPTY_BUFFER;
/*     */     }
/* 279 */     byte[] array = readFrom(this.file);
/* 280 */     return Unpooled.wrappedBuffer(array);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getChunk(int length) throws IOException {
/* 285 */     if (this.file == null || length == 0) {
/* 286 */       return Unpooled.EMPTY_BUFFER;
/*     */     }
/* 288 */     if (this.fileChannel == null) {
/* 289 */       RandomAccessFile accessFile = new RandomAccessFile(this.file, "r");
/* 290 */       this.fileChannel = accessFile.getChannel();
/*     */     } 
/* 292 */     int read = 0;
/* 293 */     ByteBuffer byteBuffer = ByteBuffer.allocate(length);
/* 294 */     while (read < length) {
/* 295 */       int readnow = this.fileChannel.read(byteBuffer);
/* 296 */       if (readnow == -1) {
/* 297 */         this.fileChannel.close();
/* 298 */         this.fileChannel = null;
/*     */         break;
/*     */       } 
/* 301 */       read += readnow;
/*     */     } 
/*     */     
/* 304 */     if (read == 0) {
/* 305 */       return Unpooled.EMPTY_BUFFER;
/*     */     }
/* 307 */     byteBuffer.flip();
/* 308 */     ByteBuf buffer = Unpooled.wrappedBuffer(byteBuffer);
/* 309 */     buffer.readerIndex(0);
/* 310 */     buffer.writerIndex(read);
/* 311 */     return buffer;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() throws IOException {
/* 316 */     return getString(HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString(Charset encoding) throws IOException {
/* 321 */     if (this.file == null) {
/* 322 */       return "";
/*     */     }
/* 324 */     if (encoding == null) {
/* 325 */       byte[] arrayOfByte = readFrom(this.file);
/* 326 */       return new String(arrayOfByte, HttpConstants.DEFAULT_CHARSET.name());
/*     */     } 
/* 328 */     byte[] array = readFrom(this.file);
/* 329 */     return new String(array, encoding.name());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInMemory() {
/* 334 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean renameTo(File dest) throws IOException {
/* 339 */     ObjectUtil.checkNotNull(dest, "dest");
/* 340 */     if (this.file == null) {
/* 341 */       throw new IOException("No file defined so cannot be renamed");
/*     */     }
/* 343 */     if (!this.file.renameTo(dest)) {
/*     */       
/* 345 */       IOException exception = null;
/* 346 */       RandomAccessFile inputAccessFile = null;
/* 347 */       RandomAccessFile outputAccessFile = null;
/* 348 */       long chunkSize = 8196L;
/* 349 */       long position = 0L;
/*     */       try {
/* 351 */         inputAccessFile = new RandomAccessFile(this.file, "r");
/* 352 */         outputAccessFile = new RandomAccessFile(dest, "rw");
/* 353 */         FileChannel in = inputAccessFile.getChannel();
/* 354 */         FileChannel out = outputAccessFile.getChannel();
/* 355 */         while (position < this.size) {
/* 356 */           if (chunkSize < this.size - position) {
/* 357 */             chunkSize = this.size - position;
/*     */           }
/* 359 */           position += in.transferTo(position, chunkSize, out);
/*     */         } 
/* 361 */       } catch (IOException e) {
/* 362 */         exception = e;
/*     */       } finally {
/* 364 */         if (inputAccessFile != null) {
/*     */           try {
/* 366 */             inputAccessFile.close();
/* 367 */           } catch (IOException e) {
/* 368 */             if (exception == null) {
/* 369 */               exception = e;
/*     */             } else {
/* 371 */               logger.warn("Multiple exceptions detected, the following will be suppressed {}", e);
/*     */             } 
/*     */           } 
/*     */         }
/* 375 */         if (outputAccessFile != null) {
/*     */           try {
/* 377 */             outputAccessFile.close();
/* 378 */           } catch (IOException e) {
/* 379 */             if (exception == null) {
/* 380 */               exception = e;
/*     */             } else {
/* 382 */               logger.warn("Multiple exceptions detected, the following will be suppressed {}", e);
/*     */             } 
/*     */           } 
/*     */         }
/*     */       } 
/* 387 */       if (exception != null) {
/* 388 */         throw exception;
/*     */       }
/* 390 */       if (position == this.size) {
/* 391 */         if (!this.file.delete()) {
/* 392 */           logger.warn("Failed to delete: {}", this.file);
/*     */         }
/* 394 */         this.file = dest;
/* 395 */         this.isRenamed = true;
/* 396 */         return true;
/*     */       } 
/* 398 */       if (!dest.delete()) {
/* 399 */         logger.warn("Failed to delete: {}", dest);
/*     */       }
/* 401 */       return false;
/*     */     } 
/*     */     
/* 404 */     this.file = dest;
/* 405 */     this.isRenamed = true;
/* 406 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[] readFrom(File src) throws IOException {
/* 414 */     long srcsize = src.length();
/* 415 */     if (srcsize > 2147483647L) {
/* 416 */       throw new IllegalArgumentException("File too big to be loaded in memory");
/*     */     }
/*     */     
/* 419 */     RandomAccessFile accessFile = new RandomAccessFile(src, "r");
/* 420 */     byte[] array = new byte[(int)srcsize];
/*     */     try {
/* 422 */       FileChannel fileChannel = accessFile.getChannel();
/* 423 */       ByteBuffer byteBuffer = ByteBuffer.wrap(array);
/* 424 */       int read = 0;
/* 425 */       while (read < srcsize) {
/* 426 */         read += fileChannel.read(byteBuffer);
/*     */       }
/*     */     } finally {
/* 429 */       accessFile.close();
/*     */     } 
/* 431 */     return array;
/*     */   }
/*     */ 
/*     */   
/*     */   public File getFile() throws IOException {
/* 436 */     return this.file;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpData touch() {
/* 441 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpData touch(Object hint) {
/* 446 */     return this;
/*     */   }
/*     */   
/*     */   protected abstract String getDiskFilename();
/*     */   
/*     */   protected abstract String getPrefix();
/*     */   
/*     */   protected abstract String getBaseDirectory();
/*     */   
/*     */   protected abstract String getPostfix();
/*     */   
/*     */   protected abstract boolean deleteOnExit();
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\AbstractDiskHttpData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */