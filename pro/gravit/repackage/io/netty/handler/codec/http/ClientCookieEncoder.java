/*    */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.cookie.ClientCookieEncoder;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.cookie.Cookie;
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
/*    */ @Deprecated
/*    */ public final class ClientCookieEncoder
/*    */ {
/*    */   @Deprecated
/*    */   public static String encode(String name, String value) {
/* 49 */     return ClientCookieEncoder.LAX.encode(name, value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public static String encode(Cookie cookie) {
/* 60 */     return ClientCookieEncoder.LAX.encode(cookie);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public static String encode(Cookie... cookies) {
/* 71 */     return ClientCookieEncoder.LAX.encode((Cookie[])cookies);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public static String encode(Iterable<Cookie> cookies) {
/* 82 */     return ClientCookieEncoder.LAX.encode(cookies);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\ClientCookieEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */