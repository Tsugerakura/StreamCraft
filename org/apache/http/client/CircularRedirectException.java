/*    */ package org.apache.http.client;
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
/*    */ public class CircularRedirectException
/*    */   extends RedirectException
/*    */ {
/*    */   private static final long serialVersionUID = 6830063487001091803L;
/*    */   
/*    */   public CircularRedirectException() {}
/*    */   
/*    */   public CircularRedirectException(String message) {
/* 52 */     super(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CircularRedirectException(String message, Throwable cause) {
/* 63 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\client\CircularRedirectException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */