/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpConstants;
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
/*     */ public class MixedAttribute
/*     */   implements Attribute
/*     */ {
/*     */   private Attribute attribute;
/*     */   private final long limitSize;
/*  33 */   private long maxSize = -1L;
/*     */   
/*     */   public MixedAttribute(String name, long limitSize) {
/*  36 */     this(name, limitSize, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */   
/*     */   public MixedAttribute(String name, long definedSize, long limitSize) {
/*  40 */     this(name, definedSize, limitSize, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */   
/*     */   public MixedAttribute(String name, long limitSize, Charset charset) {
/*  44 */     this.limitSize = limitSize;
/*  45 */     this.attribute = new MemoryAttribute(name, charset);
/*     */   }
/*     */   
/*     */   public MixedAttribute(String name, long definedSize, long limitSize, Charset charset) {
/*  49 */     this.limitSize = limitSize;
/*  50 */     this.attribute = new MemoryAttribute(name, definedSize, charset);
/*     */   }
/*     */   
/*     */   public MixedAttribute(String name, String value, long limitSize) {
/*  54 */     this(name, value, limitSize, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */   
/*     */   public MixedAttribute(String name, String value, long limitSize, Charset charset) {
/*  58 */     this.limitSize = limitSize;
/*  59 */     if (value.length() > this.limitSize) {
/*     */       try {
/*  61 */         this.attribute = new DiskAttribute(name, value, charset);
/*  62 */       } catch (IOException e) {
/*     */         
/*     */         try {
/*  65 */           this.attribute = new MemoryAttribute(name, value, charset);
/*  66 */         } catch (IOException ignore) {
/*  67 */           throw new IllegalArgumentException(e);
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       try {
/*  72 */         this.attribute = new MemoryAttribute(name, value, charset);
/*  73 */       } catch (IOException e) {
/*  74 */         throw new IllegalArgumentException(e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxSize() {
/*  81 */     return this.maxSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxSize(long maxSize) {
/*  86 */     this.maxSize = maxSize;
/*  87 */     this.attribute.setMaxSize(maxSize);
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkSize(long newSize) throws IOException {
/*  92 */     if (this.maxSize >= 0L && newSize > this.maxSize) {
/*  93 */       throw new IOException("Size exceed allowed maximum capacity");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void addContent(ByteBuf buffer, boolean last) throws IOException {
/*  99 */     if (this.attribute instanceof MemoryAttribute) {
/* 100 */       checkSize(this.attribute.length() + buffer.readableBytes());
/* 101 */       if (this.attribute.length() + buffer.readableBytes() > this.limitSize) {
/*     */         
/* 103 */         DiskAttribute diskAttribute = new DiskAttribute(this.attribute.getName(), this.attribute.definedLength());
/* 104 */         diskAttribute.setMaxSize(this.maxSize);
/* 105 */         if (((MemoryAttribute)this.attribute).getByteBuf() != null) {
/* 106 */           diskAttribute.addContent(((MemoryAttribute)this.attribute)
/* 107 */               .getByteBuf(), false);
/*     */         }
/* 109 */         this.attribute = diskAttribute;
/*     */       } 
/*     */     } 
/* 112 */     this.attribute.addContent(buffer, last);
/*     */   }
/*     */ 
/*     */   
/*     */   public void delete() {
/* 117 */     this.attribute.delete();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] get() throws IOException {
/* 122 */     return this.attribute.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getByteBuf() throws IOException {
/* 127 */     return this.attribute.getByteBuf();
/*     */   }
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/* 132 */     return this.attribute.getCharset();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() throws IOException {
/* 137 */     return this.attribute.getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString(Charset encoding) throws IOException {
/* 142 */     return this.attribute.getString(encoding);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCompleted() {
/* 147 */     return this.attribute.isCompleted();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInMemory() {
/* 152 */     return this.attribute.isInMemory();
/*     */   }
/*     */ 
/*     */   
/*     */   public long length() {
/* 157 */     return this.attribute.length();
/*     */   }
/*     */ 
/*     */   
/*     */   public long definedLength() {
/* 162 */     return this.attribute.definedLength();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean renameTo(File dest) throws IOException {
/* 167 */     return this.attribute.renameTo(dest);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharset(Charset charset) {
/* 172 */     this.attribute.setCharset(charset);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(ByteBuf buffer) throws IOException {
/* 177 */     checkSize(buffer.readableBytes());
/* 178 */     if (buffer.readableBytes() > this.limitSize && 
/* 179 */       this.attribute instanceof MemoryAttribute) {
/*     */       
/* 181 */       this.attribute = new DiskAttribute(this.attribute.getName(), this.attribute.definedLength());
/* 182 */       this.attribute.setMaxSize(this.maxSize);
/*     */     } 
/*     */     
/* 185 */     this.attribute.setContent(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(File file) throws IOException {
/* 190 */     checkSize(file.length());
/* 191 */     if (file.length() > this.limitSize && 
/* 192 */       this.attribute instanceof MemoryAttribute) {
/*     */       
/* 194 */       this.attribute = new DiskAttribute(this.attribute.getName(), this.attribute.definedLength());
/* 195 */       this.attribute.setMaxSize(this.maxSize);
/*     */     } 
/*     */     
/* 198 */     this.attribute.setContent(file);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContent(InputStream inputStream) throws IOException {
/* 203 */     if (this.attribute instanceof MemoryAttribute) {
/*     */       
/* 205 */       this.attribute = new DiskAttribute(this.attribute.getName(), this.attribute.definedLength());
/* 206 */       this.attribute.setMaxSize(this.maxSize);
/*     */     } 
/* 208 */     this.attribute.setContent(inputStream);
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData.HttpDataType getHttpDataType() {
/* 213 */     return this.attribute.getHttpDataType();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 218 */     return this.attribute.getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 223 */     return this.attribute.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 228 */     return this.attribute.equals(obj);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(InterfaceHttpData o) {
/* 233 */     return this.attribute.compareTo(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 238 */     return "Mixed: " + this.attribute;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getValue() throws IOException {
/* 243 */     return this.attribute.getValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(String value) throws IOException {
/* 248 */     if (value != null) {
/* 249 */       checkSize((value.getBytes()).length);
/*     */     }
/* 251 */     this.attribute.setValue(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf getChunk(int length) throws IOException {
/* 256 */     return this.attribute.getChunk(length);
/*     */   }
/*     */ 
/*     */   
/*     */   public File getFile() throws IOException {
/* 261 */     return this.attribute.getFile();
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute copy() {
/* 266 */     return this.attribute.copy();
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute duplicate() {
/* 271 */     return this.attribute.duplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute retainedDuplicate() {
/* 276 */     return this.attribute.retainedDuplicate();
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute replace(ByteBuf content) {
/* 281 */     return this.attribute.replace(content);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf content() {
/* 286 */     return this.attribute.content();
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt() {
/* 291 */     return this.attribute.refCnt();
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute retain() {
/* 296 */     this.attribute.retain();
/* 297 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute retain(int increment) {
/* 302 */     this.attribute.retain(increment);
/* 303 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute touch() {
/* 308 */     this.attribute.touch();
/* 309 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute touch(Object hint) {
/* 314 */     this.attribute.touch(hint);
/* 315 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 320 */     return this.attribute.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 325 */     return this.attribute.release(decrement);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\MixedAttribute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */