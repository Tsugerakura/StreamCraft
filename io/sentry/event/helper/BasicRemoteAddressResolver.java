/*    */ package io.sentry.event.helper;
/*    */ 
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BasicRemoteAddressResolver
/*    */   implements RemoteAddressResolver
/*    */ {
/*    */   public String getRemoteAddress(HttpServletRequest request) {
/* 17 */     return request.getRemoteAddr();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\helper\BasicRemoteAddressResolver.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */