/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultSpdyHeadersFrame
/*     */   extends DefaultSpdyStreamFrame
/*     */   implements SpdyHeadersFrame
/*     */ {
/*     */   private boolean invalid;
/*     */   private boolean truncated;
/*     */   private final SpdyHeaders headers;
/*     */   
/*     */   public DefaultSpdyHeadersFrame(int streamId) {
/*  38 */     this(streamId, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultSpdyHeadersFrame(int streamId, boolean validate) {
/*  48 */     super(streamId);
/*  49 */     this.headers = new DefaultSpdyHeaders(validate);
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyHeadersFrame setStreamId(int streamId) {
/*  54 */     super.setStreamId(streamId);
/*  55 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyHeadersFrame setLast(boolean last) {
/*  60 */     super.setLast(last);
/*  61 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isInvalid() {
/*  66 */     return this.invalid;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyHeadersFrame setInvalid() {
/*  71 */     this.invalid = true;
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTruncated() {
/*  77 */     return this.truncated;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyHeadersFrame setTruncated() {
/*  82 */     this.truncated = true;
/*  83 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyHeaders headers() {
/*  88 */     return this.headers;
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
/*     */   public String toString() {
/* 103 */     StringBuilder buf = (new StringBuilder()).append(StringUtil.simpleClassName(this)).append("(last: ").append(isLast()).append(')').append(StringUtil.NEWLINE).append("--> Stream-ID = ").append(streamId()).append(StringUtil.NEWLINE).append("--> Headers:").append(StringUtil.NEWLINE);
/* 104 */     appendHeaders(buf);
/*     */ 
/*     */     
/* 107 */     buf.setLength(buf.length() - StringUtil.NEWLINE.length());
/* 108 */     return buf.toString();
/*     */   }
/*     */   
/*     */   protected void appendHeaders(StringBuilder buf) {
/* 112 */     for (Map.Entry<CharSequence, CharSequence> e : (Iterable<Map.Entry<CharSequence, CharSequence>>)headers()) {
/* 113 */       buf.append("    ");
/* 114 */       buf.append(e.getKey());
/* 115 */       buf.append(": ");
/* 116 */       buf.append(e.getValue());
/* 117 */       buf.append(StringUtil.NEWLINE);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\DefaultSpdyHeadersFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */