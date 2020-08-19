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
/*     */ public class SpdySessionStatus
/*     */   implements Comparable<SpdySessionStatus>
/*     */ {
/*  28 */   public static final SpdySessionStatus OK = new SpdySessionStatus(0, "OK");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   public static final SpdySessionStatus PROTOCOL_ERROR = new SpdySessionStatus(1, "PROTOCOL_ERROR");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  40 */   public static final SpdySessionStatus INTERNAL_ERROR = new SpdySessionStatus(2, "INTERNAL_ERROR");
/*     */ 
/*     */   
/*     */   private final int code;
/*     */   
/*     */   private final String statusPhrase;
/*     */ 
/*     */   
/*     */   public static SpdySessionStatus valueOf(int code) {
/*  49 */     switch (code) {
/*     */       case 0:
/*  51 */         return OK;
/*     */       case 1:
/*  53 */         return PROTOCOL_ERROR;
/*     */       case 2:
/*  55 */         return INTERNAL_ERROR;
/*     */     } 
/*     */     
/*  58 */     return new SpdySessionStatus(code, "UNKNOWN (" + code + ')');
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
/*     */   public SpdySessionStatus(int code, String statusPhrase) {
/*  70 */     this.statusPhrase = (String)ObjectUtil.checkNotNull(statusPhrase, "statusPhrase");
/*  71 */     this.code = code;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int code() {
/*  78 */     return this.code;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String statusPhrase() {
/*  85 */     return this.statusPhrase;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  90 */     return code();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  95 */     if (!(o instanceof SpdySessionStatus)) {
/*  96 */       return false;
/*     */     }
/*     */     
/*  99 */     return (code() == ((SpdySessionStatus)o).code());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 104 */     return statusPhrase();
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(SpdySessionStatus o) {
/* 109 */     return code() - o.code();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdySessionStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */