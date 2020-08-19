/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum SslProvider
/*    */ {
/* 29 */   JDK,
/*    */ 
/*    */ 
/*    */   
/* 33 */   OPENSSL,
/*    */ 
/*    */ 
/*    */   
/* 37 */   OPENSSL_REFCNT;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isAlpnSupported(SslProvider provider) {
/* 45 */     switch (provider) {
/*    */       case JDK:
/* 47 */         return JdkAlpnApplicationProtocolNegotiator.isAlpnSupported();
/*    */       case OPENSSL:
/*    */       case OPENSSL_REFCNT:
/* 50 */         return OpenSsl.isAlpnSupported();
/*    */     } 
/* 52 */     throw new Error("Unknown SslProvider: " + provider);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SslProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */