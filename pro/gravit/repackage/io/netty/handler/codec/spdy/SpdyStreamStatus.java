/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
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
/*     */ public class SpdyStreamStatus
/*     */   implements Comparable<SpdyStreamStatus>
/*     */ {
/*  28 */   public static final SpdyStreamStatus PROTOCOL_ERROR = new SpdyStreamStatus(1, "PROTOCOL_ERROR");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   public static final SpdyStreamStatus INVALID_STREAM = new SpdyStreamStatus(2, "INVALID_STREAM");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  40 */   public static final SpdyStreamStatus REFUSED_STREAM = new SpdyStreamStatus(3, "REFUSED_STREAM");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   public static final SpdyStreamStatus UNSUPPORTED_VERSION = new SpdyStreamStatus(4, "UNSUPPORTED_VERSION");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   public static final SpdyStreamStatus CANCEL = new SpdyStreamStatus(5, "CANCEL");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   public static final SpdyStreamStatus INTERNAL_ERROR = new SpdyStreamStatus(6, "INTERNAL_ERROR");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   public static final SpdyStreamStatus FLOW_CONTROL_ERROR = new SpdyStreamStatus(7, "FLOW_CONTROL_ERROR");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   public static final SpdyStreamStatus STREAM_IN_USE = new SpdyStreamStatus(8, "STREAM_IN_USE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   public static final SpdyStreamStatus STREAM_ALREADY_CLOSED = new SpdyStreamStatus(9, "STREAM_ALREADY_CLOSED");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   public static final SpdyStreamStatus INVALID_CREDENTIALS = new SpdyStreamStatus(10, "INVALID_CREDENTIALS");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   public static final SpdyStreamStatus FRAME_TOO_LARGE = new SpdyStreamStatus(11, "FRAME_TOO_LARGE");
/*     */ 
/*     */   
/*     */   private final int code;
/*     */   
/*     */   private final String statusPhrase;
/*     */ 
/*     */   
/*     */   public static SpdyStreamStatus valueOf(int code) {
/*  97 */     if (code == 0) {
/*  98 */       throw new IllegalArgumentException("0 is not a valid status code for a RST_STREAM");
/*     */     }
/*     */ 
/*     */     
/* 102 */     switch (code) {
/*     */       case 1:
/* 104 */         return PROTOCOL_ERROR;
/*     */       case 2:
/* 106 */         return INVALID_STREAM;
/*     */       case 3:
/* 108 */         return REFUSED_STREAM;
/*     */       case 4:
/* 110 */         return UNSUPPORTED_VERSION;
/*     */       case 5:
/* 112 */         return CANCEL;
/*     */       case 6:
/* 114 */         return INTERNAL_ERROR;
/*     */       case 7:
/* 116 */         return FLOW_CONTROL_ERROR;
/*     */       case 8:
/* 118 */         return STREAM_IN_USE;
/*     */       case 9:
/* 120 */         return STREAM_ALREADY_CLOSED;
/*     */       case 10:
/* 122 */         return INVALID_CREDENTIALS;
/*     */       case 11:
/* 124 */         return FRAME_TOO_LARGE;
/*     */     } 
/*     */     
/* 127 */     return new SpdyStreamStatus(code, "UNKNOWN (" + code + ')');
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
/*     */   public SpdyStreamStatus(int code, String statusPhrase) {
/* 139 */     if (code == 0) {
/* 140 */       throw new IllegalArgumentException("0 is not a valid status code for a RST_STREAM");
/*     */     }
/*     */ 
/*     */     
/* 144 */     this.statusPhrase = (String)ObjectUtil.checkNotNull(statusPhrase, "statusPhrase");
/* 145 */     this.code = code;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int code() {
/* 152 */     return this.code;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String statusPhrase() {
/* 159 */     return this.statusPhrase;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 164 */     return code();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 169 */     if (!(o instanceof SpdyStreamStatus)) {
/* 170 */       return false;
/*     */     }
/*     */     
/* 173 */     return (code() == ((SpdyStreamStatus)o).code());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 178 */     return statusPhrase();
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(SpdyStreamStatus o) {
/* 183 */     return code() - o.code();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyStreamStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */