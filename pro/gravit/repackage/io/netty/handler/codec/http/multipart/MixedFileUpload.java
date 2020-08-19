/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MixedFileUpload
/*     */   implements FileUpload
/*     */ {
/*     */   private FileUpload fileUpload;
/*     */   private final long limitSize;
/*     */   private final long definedSize;
/*  35 */   private long maxSize = -1L;
/*     */ 
/*     */ 
/*     */   
/*     */   public MixedFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size, long limitSize) {
/*  40 */     this.limitSize = limitSize;
/*  41 */     if (size > this.limitSize) {
/*  42 */       this.fileUpload = new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
/*     */     } else {
/*     */       
/*  45 */       this.fileUpload = new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
/*     */     } 
/*     */     
/*  48 */     this.definedSize = size;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxSize() {
/*  53 */     return this.maxSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxSize(long maxSize) {
/*  58 */     this.maxSize = maxSize;
/*  59 */     this.fileUpload.setMaxSize(maxSize);
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkSize(long newSize) throws IOException {
/*  64 */     if (this.maxSize >= 0L && newSize > this.maxSize) {
/*  65 */       throw new IOException("Size exceed allowed maximum capacity");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addContent(ByteBuf buffer, boolean last) throws IOException {
/*  72 */     if (this.fileUpload instanceof MemoryFileUpload) {
/*  73 */       checkSize(this.fileUpload.length() + buffer.readableBytes());
/*  74 */       if (this.fileUpload.length() + buffer.readableBytes() > this.limitSize) {
/*     */ 
/*     */ 
/*     */         
/*  78 */         DiskFileUpload diskFileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize);
/*     */         
/*  80 */         diskFileUpload.setMaxSize(this.maxSize);
/*  81 */         ByteBuf data = this.fileUpload.getByteBuf();
/*  82 */         if (data != null && data.isReadable()) {
/*  83 */           diskFileUpload.addContent(data.retain(), false);
/*     */         }
/*     */         
/*  86 */         this.fileUpload.release();
/*     */         
/*  88 */         this.fileUpload = diskFileUpload;
/*     */       } 
/*     */     } 
/*  91 */     this.fileUpload.addContent(buffer, last);
/*     */   }
/*     */ 
/*     */   
/*     */   public void delete() {
/*  96 */     this.fileUpload.delete();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] get() throws IOException {
/* 101 */     return this.fileUpload.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getByteBuf() throws IOException {
/* 106 */     return this.fileUpload.getByteBuf();
/*     */   }
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/* 111 */     return this.fileUpload.getCharset();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContentType() {
/* 116 */     return this.fileUpload.getContentType();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContentTransferEncoding() {
/* 121 */     return this.fileUpload.getContentTransferEncoding();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getFilename() {
/* 126 */     return this.fileUpload.getFilename();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() throws IOException {
/* 131 */     return this.fileUpload.getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString(Charset encoding) throws IOException {
/* 136 */     return this.fileUpload.getString(encoding);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCompleted() {
/* 141 */     return this.fileUpload.isCompleted();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInMemory() {
/* 146 */     return this.fileUpload.isInMemory();
/*     */   }
/*     */ 
/*     */   
/*     */   public long length() {
/* 151 */     return this.fileUpload.length();
/*     */   }
/*     */ 
/*     */   
/*     */   public long definedLength() {
/* 156 */     return this.fileUpload.definedLength();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean renameTo(File dest) throws IOException {
/* 161 */     return this.fileUpload.renameTo(dest);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharset(Charset charset) {
/* 166 */     this.fileUpload.setCharset(charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(ByteBuf buffer) throws IOException {
/* 171 */     checkSize(buffer.readableBytes());
/* 172 */     if (buffer.readableBytes() > this.limitSize && 
/* 173 */       this.fileUpload instanceof MemoryFileUpload) {
/* 174 */       FileUpload memoryUpload = this.fileUpload;
/*     */       
/* 176 */       this
/*     */ 
/*     */         
/* 179 */         .fileUpload = new DiskFileUpload(memoryUpload.getName(), memoryUpload.getFilename(), memoryUpload.getContentType(), memoryUpload.getContentTransferEncoding(), memoryUpload.getCharset(), this.definedSize);
/*     */       
/* 181 */       this.fileUpload.setMaxSize(this.maxSize);
/*     */ 
/*     */       
/* 184 */       memoryUpload.release();
/*     */     } 
/*     */     
/* 187 */     this.fileUpload.setContent(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(File file) throws IOException {
/* 192 */     checkSize(file.length());
/* 193 */     if (file.length() > this.limitSize && 
/* 194 */       this.fileUpload instanceof MemoryFileUpload) {
/* 195 */       FileUpload memoryUpload = this.fileUpload;
/*     */ 
/*     */       
/* 198 */       this
/*     */ 
/*     */         
/* 201 */         .fileUpload = new DiskFileUpload(memoryUpload.getName(), memoryUpload.getFilename(), memoryUpload.getContentType(), memoryUpload.getContentTransferEncoding(), memoryUpload.getCharset(), this.definedSize);
/*     */       
/* 203 */       this.fileUpload.setMaxSize(this.maxSize);
/*     */ 
/*     */       
/* 206 */       memoryUpload.release();
/*     */     } 
/*     */     
/* 209 */     this.fileUpload.setContent(file);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(InputStream inputStream) throws IOException {
/* 214 */     if (this.fileUpload instanceof MemoryFileUpload) {
/* 215 */       FileUpload memoryUpload = this.fileUpload;
/*     */ 
/*     */       
/* 218 */       this
/*     */ 
/*     */         
/* 221 */         .fileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize);
/*     */       
/* 223 */       this.fileUpload.setMaxSize(this.maxSize);
/*     */ 
/*     */       
/* 226 */       memoryUpload.release();
/*     */     } 
/* 228 */     this.fileUpload.setContent(inputStream);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContentType(String contentType) {
/* 233 */     this.fileUpload.setContentType(contentType);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContentTransferEncoding(String contentTransferEncoding) {
/* 238 */     this.fileUpload.setContentTransferEncoding(contentTransferEncoding);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFilename(String filename) {
/* 243 */     this.fileUpload.setFilename(filename);
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData.HttpDataType getHttpDataType() {
/* 248 */     return this.fileUpload.getHttpDataType();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 253 */     return this.fileUpload.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 258 */     return this.fileUpload.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 263 */     return this.fileUpload.equals(obj);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(InterfaceHttpData o) {
/* 268 */     return this.fileUpload.compareTo(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 273 */     return "Mixed: " + this.fileUpload;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getChunk(int length) throws IOException {
/* 278 */     return this.fileUpload.getChunk(length);
/*     */   }
/*     */ 
/*     */   
/*     */   public File getFile() throws IOException {
/* 283 */     return this.fileUpload.getFile();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload copy() {
/* 288 */     return this.fileUpload.copy();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload duplicate() {
/* 293 */     return this.fileUpload.duplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload retainedDuplicate() {
/* 298 */     return this.fileUpload.retainedDuplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload replace(ByteBuf content) {
/* 303 */     return this.fileUpload.replace(content);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf content() {
/* 308 */     return this.fileUpload.content();
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt() {
/* 313 */     return this.fileUpload.refCnt();
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload retain() {
/* 318 */     this.fileUpload.retain();
/* 319 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload retain(int increment) {
/* 324 */     this.fileUpload.retain(increment);
/* 325 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload touch() {
/* 330 */     this.fileUpload.touch();
/* 331 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload touch(Object hint) {
/* 336 */     this.fileUpload.touch(hint);
/* 337 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 342 */     return this.fileUpload.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 347 */     return this.fileUpload.release(decrement);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\MixedFileUpload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */