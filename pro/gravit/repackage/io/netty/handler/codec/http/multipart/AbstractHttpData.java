/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.regex.Pattern;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpConstants;
/*     */ import pro.gravit.repackage.io.netty.util.AbstractReferenceCounted;
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
/*     */ public abstract class AbstractHttpData
/*     */   extends AbstractReferenceCounted
/*     */   implements HttpData
/*     */ {
/*  33 */   private static final Pattern STRIP_PATTERN = Pattern.compile("(?:^\\s+|\\s+$|\\n)");
/*  34 */   private static final Pattern REPLACE_PATTERN = Pattern.compile("[\\r\\t]");
/*     */   
/*     */   private final String name;
/*     */   protected long definedSize;
/*     */   protected long size;
/*  39 */   private Charset charset = HttpConstants.DEFAULT_CHARSET;
/*     */   private boolean completed;
/*  41 */   private long maxSize = -1L;
/*     */   
/*     */   protected AbstractHttpData(String name, Charset charset, long size) {
/*  44 */     ObjectUtil.checkNotNull(name, "name");
/*     */     
/*  46 */     name = REPLACE_PATTERN.matcher(name).replaceAll(" ");
/*  47 */     name = STRIP_PATTERN.matcher(name).replaceAll("");
/*     */     
/*  49 */     if (name.isEmpty()) {
/*  50 */       throw new IllegalArgumentException("empty name");
/*     */     }
/*     */     
/*  53 */     this.name = name;
/*  54 */     if (charset != null) {
/*  55 */       setCharset(charset);
/*     */     }
/*  57 */     this.definedSize = size;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getMaxSize() {
/*  62 */     return this.maxSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxSize(long maxSize) {
/*  67 */     this.maxSize = maxSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkSize(long newSize) throws IOException {
/*  72 */     if (this.maxSize >= 0L && newSize > this.maxSize) {
/*  73 */       throw new IOException("Size exceed allowed maximum capacity");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  79 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCompleted() {
/*  84 */     return this.completed;
/*     */   }
/*     */   
/*     */   protected void setCompleted() {
/*  88 */     this.completed = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Charset getCharset() {
/*  93 */     return this.charset;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCharset(Charset charset) {
/*  98 */     this.charset = (Charset)ObjectUtil.checkNotNull(charset, "charset");
/*     */   }
/*     */ 
/*     */   
/*     */   public long length() {
/* 103 */     return this.size;
/*     */   }
/*     */ 
/*     */   
/*     */   public long definedLength() {
/* 108 */     return this.definedSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf content() {
/*     */     try {
/* 114 */       return getByteBuf();
/* 115 */     } catch (IOException e) {
/* 116 */       throw new ChannelException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void deallocate() {
/* 122 */     delete();
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpData retain() {
/* 127 */     super.retain();
/* 128 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpData retain(int increment) {
/* 133 */     super.retain(increment);
/* 134 */     return this;
/*     */   }
/*     */   
/*     */   public abstract HttpData touch();
/*     */   
/*     */   public abstract HttpData touch(Object paramObject);
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\AbstractHttpData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */