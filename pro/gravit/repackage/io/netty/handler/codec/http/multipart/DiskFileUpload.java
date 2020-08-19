/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderValues;
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
/*     */ 
/*     */ public class DiskFileUpload
/*     */   extends AbstractDiskHttpData
/*     */   implements FileUpload
/*     */ {
/*     */   public static String baseDirectory;
/*     */   public static boolean deleteOnExitTemporaryFile = true;
/*     */   public static final String prefix = "FUp_";
/*     */   public static final String postfix = ".tmp";
/*     */   private String filename;
/*     */   private String contentType;
/*     */   private String contentTransferEncoding;
/*     */   
/*     */   public DiskFileUpload(String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size) {
/*  48 */     super(name, charset, size);
/*  49 */     setFilename(filename);
/*  50 */     setContentType(contentType);
/*  51 */     setContentTransferEncoding(contentTransferEncoding);
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData.HttpDataType getHttpDataType() {
/*  56 */     return InterfaceHttpData.HttpDataType.FileUpload;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getFilename() {
/*  61 */     return this.filename;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFilename(String filename) {
/*  66 */     this.filename = (String)ObjectUtil.checkNotNull(filename, "filename");
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  71 */     return FileUploadUtil.hashCode(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  76 */     return (o instanceof FileUpload && FileUploadUtil.equals(this, (FileUpload)o));
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(InterfaceHttpData o) {
/*  81 */     if (!(o instanceof FileUpload)) {
/*  82 */       throw new ClassCastException("Cannot compare " + getHttpDataType() + " with " + o
/*  83 */           .getHttpDataType());
/*     */     }
/*  85 */     return compareTo((FileUpload)o);
/*     */   }
/*     */   
/*     */   public int compareTo(FileUpload o) {
/*  89 */     return FileUploadUtil.compareTo(this, o);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContentType(String contentType) {
/*  94 */     this.contentType = (String)ObjectUtil.checkNotNull(contentType, "contentType");
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContentType() {
/*  99 */     return this.contentType;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContentTransferEncoding() {
/* 104 */     return this.contentTransferEncoding;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setContentTransferEncoding(String contentTransferEncoding) {
/* 109 */     this.contentTransferEncoding = contentTransferEncoding;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 114 */     File file = null;
/*     */     try {
/* 116 */       file = getFile();
/* 117 */     } catch (IOException iOException) {}
/*     */ 
/*     */ 
/*     */     
/* 121 */     return HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + 
/* 122 */       getName() + "\"; " + HttpHeaderValues.FILENAME + "=\"" + this.filename + "\"\r\n" + HttpHeaderNames.CONTENT_TYPE + ": " + this.contentType + (
/*     */ 
/*     */       
/* 125 */       (getCharset() != null) ? ("; " + HttpHeaderValues.CHARSET + '=' + getCharset().name() + "\r\n") : "\r\n") + HttpHeaderNames.CONTENT_LENGTH + ": " + 
/* 126 */       length() + "\r\nCompleted: " + 
/* 127 */       isCompleted() + "\r\nIsInMemory: " + 
/* 128 */       isInMemory() + "\r\nRealFile: " + ((file != null) ? file
/* 129 */       .getAbsolutePath() : "null") + " DefaultDeleteAfter: " + deleteOnExitTemporaryFile;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean deleteOnExit() {
/* 135 */     return deleteOnExitTemporaryFile;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getBaseDirectory() {
/* 140 */     return baseDirectory;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getDiskFilename() {
/* 145 */     return "upload";
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getPostfix() {
/* 150 */     return ".tmp";
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getPrefix() {
/* 155 */     return "FUp_";
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload copy() {
/* 160 */     ByteBuf content = content();
/* 161 */     return replace((content != null) ? content.copy() : null);
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload duplicate() {
/* 166 */     ByteBuf content = content();
/* 167 */     return replace((content != null) ? content.duplicate() : null);
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload retainedDuplicate() {
/* 172 */     ByteBuf content = content();
/* 173 */     if (content != null) {
/* 174 */       content = content.retainedDuplicate();
/* 175 */       boolean success = false;
/*     */       try {
/* 177 */         FileUpload duplicate = replace(content);
/* 178 */         success = true;
/* 179 */         return duplicate;
/*     */       } finally {
/* 181 */         if (!success) {
/* 182 */           content.release();
/*     */         }
/*     */       } 
/*     */     } 
/* 186 */     return replace((ByteBuf)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileUpload replace(ByteBuf content) {
/* 193 */     DiskFileUpload upload = new DiskFileUpload(getName(), getFilename(), getContentType(), getContentTransferEncoding(), getCharset(), this.size);
/* 194 */     if (content != null) {
/*     */       try {
/* 196 */         upload.setContent(content);
/* 197 */       } catch (IOException e) {
/* 198 */         throw new ChannelException(e);
/*     */       } 
/*     */     }
/* 201 */     return upload;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload retain(int increment) {
/* 206 */     super.retain(increment);
/* 207 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload retain() {
/* 212 */     super.retain();
/* 213 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload touch() {
/* 218 */     super.touch();
/* 219 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileUpload touch(Object hint) {
/* 224 */     super.touch(hint);
/* 225 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\DiskFileUpload.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */