/*    */ package io.sentry.connection;
/*    */ 
/*    */ import java.net.Authenticator;
/*    */ import java.net.PasswordAuthentication;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ProxyAuthenticator
/*    */   extends Authenticator
/*    */ {
/*    */   private String user;
/*    */   private String pass;
/*    */   
/*    */   public ProxyAuthenticator(String user, String pass) {
/* 20 */     this.user = user;
/* 21 */     this.pass = pass;
/*    */   }
/*    */ 
/*    */   
/*    */   protected PasswordAuthentication getPasswordAuthentication() {
/* 26 */     if (getRequestorType() == Authenticator.RequestorType.PROXY) {
/* 27 */       return new PasswordAuthentication(this.user, this.pass.toCharArray());
/*    */     }
/* 29 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\ProxyAuthenticator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */