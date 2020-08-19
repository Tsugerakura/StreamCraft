/*    */ package org.apache.http.impl.cookie;
/*    */ 
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.cookie.CommonCookieAttributeHandler;
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
/*    */ @Contract(threading = ThreadingBehavior.SAFE)
/*    */ public class RFC6265LaxSpec
/*    */   extends RFC6265CookieSpecBase
/*    */ {
/*    */   public RFC6265LaxSpec() {
/* 46 */     super(new CommonCookieAttributeHandler[] { new BasicPathHandler(), new BasicDomainHandler(), new LaxMaxAgeHandler(), new BasicSecureHandler(), new LaxExpiresHandler() });
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   RFC6265LaxSpec(CommonCookieAttributeHandler... handlers) {
/* 54 */     super(handlers);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 59 */     return "rfc6265-lax";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\impl\cookie\RFC6265LaxSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */