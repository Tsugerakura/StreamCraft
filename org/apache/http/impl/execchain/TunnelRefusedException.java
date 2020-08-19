/*    */ package org.apache.http.impl.execchain;
/*    */ 
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpResponse;
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
/*    */ public class TunnelRefusedException
/*    */   extends HttpException
/*    */ {
/*    */   private static final long serialVersionUID = -8646722842745617323L;
/*    */   private final HttpResponse response;
/*    */   
/*    */   public TunnelRefusedException(String message, HttpResponse response) {
/* 45 */     super(message);
/* 46 */     this.response = response;
/*    */   }
/*    */   
/*    */   public HttpResponse getResponse() {
/* 50 */     return this.response;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\impl\execchain\TunnelRefusedException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */