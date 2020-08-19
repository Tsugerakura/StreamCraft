/*    */ package io.sentry.event.helper;
/*    */ 
/*    */ import io.sentry.util.Util;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ForwardedAddressResolver
/*    */   implements RemoteAddressResolver
/*    */ {
/* 22 */   private BasicRemoteAddressResolver basicRemoteAddressResolver = new BasicRemoteAddressResolver();
/*    */ 
/*    */   
/*    */   private static String firstAddress(String csvAddrs) {
/* 26 */     List<String> ips = Arrays.asList(csvAddrs.split(","));
/* 27 */     return ((String)ips.get(0)).trim();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getRemoteAddress(HttpServletRequest request) {
/* 32 */     String forwarded = request.getHeader("X-FORWARDED-FOR");
/* 33 */     if (!Util.isNullOrEmpty(forwarded)) {
/* 34 */       return firstAddress(forwarded);
/*    */     }
/* 36 */     return this.basicRemoteAddressResolver.getRemoteAddress(request);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\helper\ForwardedAddressResolver.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */