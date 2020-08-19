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
/*     */ public class DefaultFullHttpRequest
/*     */   extends DefaultHttpRequest
/*     */   implements FullHttpRequest
/*     */ {
/*     */   private final ByteBuf content;
/*     */   private final HttpHeaders trailingHeader;
/*     */   private int hash;
/*     */   
/*     */   public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
/*  36 */     this(httpVersion, method, uri, Unpooled.buffer(0));
/*     */   }
/*     */   
/*     */   public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content) {
/*  40 */     this(httpVersion, method, uri, content, true);
/*     */   }
/*     */   
/*     */   public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, boolean validateHeaders) {
/*  44 */     this(httpVersion, method, uri, Unpooled.buffer(0), validateHeaders);
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content, boolean validateHeaders) {
/*  49 */     super(httpVersion, method, uri, validateHeaders);
/*  50 */     this.content = (ByteBuf)ObjectUtil.checkNotNull(content, "content");
/*  51 */     this.trailingHeader = new DefaultHttpHeaders(validateHeaders);
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content, HttpHeaders headers, HttpHeaders trailingHeader) {
/*  56 */     super(httpVersion, method, uri, headers);
/*  57 */     this.content = (ByteBuf)ObjectUtil.checkNotNull(content, "content");
/*  58 */     this.trailingHeader = (HttpHeaders)ObjectUtil.checkNotNull(trailingHeader, "trailingHeader");
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpHeaders trailingHeaders() {
/*  63 */     return this.trailingHeader;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf content() {
/*  68 */     return this.content;
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt() {
/*  73 */     return this.content.refCnt();
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpRequest retain() {
/*  78 */     this.content.retain();
/*  79 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpRequest retain(int increment) {
/*  84 */     this.content.retain(increment);
/*  85 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpRequest touch() {
/*  90 */     this.content.touch();
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpRequest touch(Object hint) {
/*  96 */     this.content.touch(hint);
/*  97 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 102 */     return this.content.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 107 */     return this.content.release(decrement);
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpRequest setProtocolVersion(HttpVersion version) {
/* 112 */     super.setProtocolVersion(version);
/* 113 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpRequest setMethod(HttpMethod method) {
/* 118 */     super.setMethod(method);
/* 119 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpRequest setUri(String uri) {
/* 124 */     super.setUri(uri);
/* 125 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpRequest copy() {
/* 130 */     return replace(content().copy());
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpRequest duplicate() {
/* 135 */     return replace(content().duplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public FullHttpRequest retainedDuplicate() {
/* 140 */     return replace(content().retainedDuplicate());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public FullHttpRequest replace(ByteBuf content) {
/* 146 */     FullHttpRequest request = new DefaultFullHttpRequest(protocolVersion(), method(), uri(), content, headers().copy(), trailingHeaders().copy());
/* 147 */     request.setDecoderResult(decoderResult());
/* 148 */     return request;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 153 */     int hash = this.hash;
/* 154 */     if (hash == 0) {
/* 155 */       if (content().refCnt() != 0) {
/*     */         try {
/* 157 */           hash = 31 + content().hashCode();
/* 158 */         } catch (IllegalReferenceCountException ignored) {
/*     */           
/* 160 */           hash = 31;
/*     */         } 
/*     */       } else {
/* 163 */         hash = 31;
/*     */       } 
/* 165 */       hash = 31 * hash + trailingHeaders().hashCode();
/* 166 */       hash = 31 * hash + super.hashCode();
/* 167 */       this.hash = hash;
/*     */     } 
/* 169 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 174 */     if (!(o instanceof DefaultFullHttpRequest)) {
/* 175 */       return false;
/*     */     }
/*     */     
/* 178 */     DefaultFullHttpRequest other = (DefaultFullHttpRequest)o;
/*     */     
/* 180 */     return (super.equals(other) && 
/* 181 */       content().equals(other.content()) && 
/* 182 */       trailingHeaders().equals(other.trailingHeaders()));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 187 */     return HttpMessageUtil.appendFullRequest(new StringBuilder(256), this).toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\DefaultFullHttpRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */