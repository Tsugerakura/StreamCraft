/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
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
/*     */ public enum HttpStatusClass
/*     */ {
/*  28 */   INFORMATIONAL(100, 200, "Informational"),
/*     */ 
/*     */ 
/*     */   
/*  32 */   SUCCESS(200, 300, "Success"),
/*     */ 
/*     */ 
/*     */   
/*  36 */   REDIRECTION(300, 400, "Redirection"),
/*     */ 
/*     */ 
/*     */   
/*  40 */   CLIENT_ERROR(400, 500, "Client Error"),
/*     */ 
/*     */ 
/*     */   
/*  44 */   SERVER_ERROR(500, 600, "Server Error"),
/*     */ 
/*     */ 
/*     */   
/*  48 */   UNKNOWN(0, 0, "Unknown Status")
/*     */   {
/*     */     public boolean contains(int code) {
/*  51 */       return (code < 100 || code >= 600);
/*     */     }
/*     */   };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int min;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int max;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final AsciiString defaultReasonPhrase;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int digit(char c) {
/*  91 */     return c - 48;
/*     */   }
/*     */   
/*     */   private static boolean isDigit(char c) {
/*  95 */     return (c >= '0' && c <= '9');
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   HttpStatusClass(int min, int max, String defaultReasonPhrase) {
/* 103 */     this.min = min;
/* 104 */     this.max = max;
/* 105 */     this.defaultReasonPhrase = AsciiString.cached(defaultReasonPhrase);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(int code) {
/* 112 */     return (code >= this.min && code < this.max);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AsciiString defaultReasonPhrase() {
/* 119 */     return this.defaultReasonPhrase;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpStatusClass.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */