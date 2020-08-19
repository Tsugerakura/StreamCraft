/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DefaultHeaders;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ public class DefaultLastHttpContent
/*     */   extends DefaultHttpContent
/*     */   implements LastHttpContent
/*     */ {
/*     */   private final HttpHeaders trailingHeaders;
/*     */   private final boolean validateHeaders;
/*     */   
/*     */   public DefaultLastHttpContent() {
/*  33 */     this(Unpooled.buffer(0));
/*     */   }
/*     */   
/*     */   public DefaultLastHttpContent(ByteBuf content) {
/*  37 */     this(content, true);
/*     */   }
/*     */   
/*     */   public DefaultLastHttpContent(ByteBuf content, boolean validateHeaders) {
/*  41 */     super(content);
/*  42 */     this.trailingHeaders = new TrailingHttpHeaders(validateHeaders);
/*  43 */     this.validateHeaders = validateHeaders;
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent copy() {
/*  48 */     return replace(content().copy());
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent duplicate() {
/*  53 */     return replace(content().duplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent retainedDuplicate() {
/*  58 */     return replace(content().retainedDuplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent replace(ByteBuf content) {
/*  63 */     DefaultLastHttpContent dup = new DefaultLastHttpContent(content, this.validateHeaders);
/*  64 */     dup.trailingHeaders().set(trailingHeaders());
/*  65 */     return dup;
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent retain(int increment) {
/*  70 */     super.retain(increment);
/*  71 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent retain() {
/*  76 */     super.retain();
/*  77 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent touch() {
/*  82 */     super.touch();
/*  83 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public LastHttpContent touch(Object hint) {
/*  88 */     super.touch(hint);
/*  89 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders trailingHeaders() {
/*  94 */     return this.trailingHeaders;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  99 */     StringBuilder buf = new StringBuilder(super.toString());
/* 100 */     buf.append(StringUtil.NEWLINE);
/* 101 */     appendHeaders(buf);
/*     */ 
/*     */     
/* 104 */     buf.setLength(buf.length() - StringUtil.NEWLINE.length());
/* 105 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private void appendHeaders(StringBuilder buf) {
/* 109 */     for (Map.Entry<String, String> e : (Iterable<Map.Entry<String, String>>)trailingHeaders()) {
/* 110 */       buf.append(e.getKey());
/* 111 */       buf.append(": ");
/* 112 */       buf.append(e.getValue());
/* 113 */       buf.append(StringUtil.NEWLINE);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class TrailingHttpHeaders extends DefaultHttpHeaders {
/* 118 */     private static final DefaultHeaders.NameValidator<CharSequence> TrailerNameValidator = new DefaultHeaders.NameValidator<CharSequence>()
/*     */       {
/*     */         public void validateName(CharSequence name) {
/* 121 */           DefaultHttpHeaders.HttpNameValidator.validateName(name);
/* 122 */           if (HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(name) || HttpHeaderNames.TRANSFER_ENCODING
/* 123 */             .contentEqualsIgnoreCase(name) || HttpHeaderNames.TRAILER
/* 124 */             .contentEqualsIgnoreCase(name)) {
/* 125 */             throw new IllegalArgumentException("prohibited trailing header: " + name);
/*     */           }
/*     */         }
/*     */       };
/*     */ 
/*     */     
/*     */     TrailingHttpHeaders(boolean validate) {
/* 132 */       super(validate, validate ? TrailerNameValidator : DefaultHeaders.NameValidator.NOT_NULL);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\DefaultLastHttpContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */