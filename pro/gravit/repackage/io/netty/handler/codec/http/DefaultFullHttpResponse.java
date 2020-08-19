/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.util.IllegalReferenceCountException;
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
/*     */ public class DefaultFullHttpResponse
/*     */   extends DefaultHttpResponse
/*     */   implements FullHttpResponse
/*     */ {
/*     */   private final ByteBuf content;
/*     */   private final HttpHeaders trailingHeaders;
/*     */   private int hash;
/*     */   
/*     */   public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status) {
/*  38 */     this(version, status, Unpooled.buffer(0));
/*     */   }
/*     */   
/*     */   public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content) {
/*  42 */     this(version, status, content, true);
/*     */   }
/*     */   
/*     */   public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders) {
/*  46 */     this(version, status, Unpooled.buffer(0), validateHeaders, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders, boolean singleFieldHeaders) {
/*  51 */     this(version, status, Unpooled.buffer(0), validateHeaders, singleFieldHeaders);
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders) {
/*  56 */     this(version, status, content, validateHeaders, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders, boolean singleFieldHeaders) {
/*  61 */     super(version, status, validateHeaders, singleFieldHeaders);
/*  62 */     this.content = (ByteBuf)ObjectUtil.checkNotNull(content, "content");
/*  63 */     this.trailingHeaders = singleFieldHeaders ? new CombinedHttpHeaders(validateHeaders) : new DefaultHttpHeaders(validateHeaders);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, HttpHeaders headers, HttpHeaders trailingHeaders) {
/*  69 */     super(version, status, headers);
/*  70 */     this.content = (ByteBuf)ObjectUtil.checkNotNull(content, "content");
/*  71 */     this.trailingHeaders = (HttpHeaders)ObjectUtil.checkNotNull(trailingHeaders, "trailingHeaders");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders trailingHeaders() {
/*  76 */     return this.trailingHeaders;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf content() {
/*  81 */     return this.content;
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt() {
/*  86 */     return this.content.refCnt();
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpResponse retain() {
/*  91 */     this.content.retain();
/*  92 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpResponse retain(int increment) {
/*  97 */     this.content.retain(increment);
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpResponse touch() {
/* 103 */     this.content.touch();
/* 104 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpResponse touch(Object hint) {
/* 109 */     this.content.touch(hint);
/* 110 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 115 */     return this.content.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 120 */     return this.content.release(decrement);
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpResponse setProtocolVersion(HttpVersion version) {
/* 125 */     super.setProtocolVersion(version);
/* 126 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpResponse setStatus(HttpResponseStatus status) {
/* 131 */     super.setStatus(status);
/* 132 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpResponse copy() {
/* 137 */     return replace(content().copy());
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpResponse duplicate() {
/* 142 */     return replace(content().duplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpResponse retainedDuplicate() {
/* 147 */     return replace(content().retainedDuplicate());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public FullHttpResponse replace(ByteBuf content) {
/* 153 */     FullHttpResponse response = new DefaultFullHttpResponse(protocolVersion(), status(), content, headers().copy(), trailingHeaders().copy());
/* 154 */     response.setDecoderResult(decoderResult());
/* 155 */     return response;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 160 */     int hash = this.hash;
/* 161 */     if (hash == 0) {
/* 162 */       if (content().refCnt() != 0) {
/*     */         try {
/* 164 */           hash = 31 + content().hashCode();
/* 165 */         } catch (IllegalReferenceCountException ignored) {
/*     */           
/* 167 */           hash = 31;
/*     */         } 
/*     */       } else {
/* 170 */         hash = 31;
/*     */       } 
/* 172 */       hash = 31 * hash + trailingHeaders().hashCode();
/* 173 */       hash = 31 * hash + super.hashCode();
/* 174 */       this.hash = hash;
/*     */     } 
/* 176 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 181 */     if (!(o instanceof DefaultFullHttpResponse)) {
/* 182 */       return false;
/*     */     }
/*     */     
/* 185 */     DefaultFullHttpResponse other = (DefaultFullHttpResponse)o;
/*     */     
/* 187 */     return (super.equals(other) && 
/* 188 */       content().equals(other.content()) && 
/* 189 */       trailingHeaders().equals(other.trailingHeaders()));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 194 */     return HttpMessageUtil.appendFullResponse(new StringBuilder(256), this).toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\DefaultFullHttpResponse.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */