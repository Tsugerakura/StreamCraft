/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ 
/*     */ 
/*     */ public class DefaultSpdySynStreamFrame
/*     */   extends DefaultSpdyHeadersFrame
/*     */   implements SpdySynStreamFrame
/*     */ {
/*     */   private int associatedStreamId;
/*     */   private byte priority;
/*     */   private boolean unidirectional;
/*     */   
/*     */   public DefaultSpdySynStreamFrame(int streamId, int associatedStreamId, byte priority) {
/*  40 */     this(streamId, associatedStreamId, priority, true);
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
/*     */   public DefaultSpdySynStreamFrame(int streamId, int associatedStreamId, byte priority, boolean validateHeaders) {
/*  52 */     super(streamId, validateHeaders);
/*  53 */     setAssociatedStreamId(associatedStreamId);
/*  54 */     setPriority(priority);
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySynStreamFrame setStreamId(int streamId) {
/*  59 */     super.setStreamId(streamId);
/*  60 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySynStreamFrame setLast(boolean last) {
/*  65 */     super.setLast(last);
/*  66 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySynStreamFrame setInvalid() {
/*  71 */     super.setInvalid();
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int associatedStreamId() {
/*  77 */     return this.associatedStreamId;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySynStreamFrame setAssociatedStreamId(int associatedStreamId) {
/*  82 */     ObjectUtil.checkPositiveOrZero(associatedStreamId, "associatedStreamId");
/*  83 */     this.associatedStreamId = associatedStreamId;
/*  84 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte priority() {
/*  89 */     return this.priority;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySynStreamFrame setPriority(byte priority) {
/*  94 */     if (priority < 0 || priority > 7) {
/*  95 */       throw new IllegalArgumentException("Priority must be between 0 and 7 inclusive: " + priority);
/*     */     }
/*     */     
/*  98 */     this.priority = priority;
/*  99 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isUnidirectional() {
/* 104 */     return this.unidirectional;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySynStreamFrame setUnidirectional(boolean unidirectional) {
/* 109 */     this.unidirectional = unidirectional;
/* 110 */     return this;
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
/* 125 */     StringBuilder buf = (new StringBuilder()).append(StringUtil.simpleClassName(this)).append("(last: ").append(isLast()).append("; unidirectional: ").append(isUnidirectional()).append(')').append(StringUtil.NEWLINE).append("--> Stream-ID = ").append(streamId()).append(StringUtil.NEWLINE);
/* 126 */     if (this.associatedStreamId != 0) {
/* 127 */       buf.append("--> Associated-To-Stream-ID = ")
/* 128 */         .append(associatedStreamId())
/* 129 */         .append(StringUtil.NEWLINE);
/*     */     }
/* 131 */     buf.append("--> Priority = ")
/* 132 */       .append(priority())
/* 133 */       .append(StringUtil.NEWLINE)
/* 134 */       .append("--> Headers:")
/* 135 */       .append(StringUtil.NEWLINE);
/* 136 */     appendHeaders(buf);
/*     */ 
/*     */     
/* 139 */     buf.setLength(buf.length() - StringUtil.NEWLINE.length());
/* 140 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\DefaultSpdySynStreamFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */