/*    */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.cookie.Cookie;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.cookie.ServerCookieEncoder;
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
/*    */ @Deprecated
/*    */ public final class ServerCookieEncoder
/*    */ {
/*    */   @Deprecated
/*    */   public static String encode(String name, String value) {
/* 53 */     return ServerCookieEncoder.LAX.encode(name, value);
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
/* 64 */     return ServerCookieEncoder.LAX.encode(cookie);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public static List<String> encode(Cookie... cookies) {
/* 75 */     return ServerCookieEncoder.LAX.encode((Cookie[])cookies);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public static List<String> encode(Collection<Cookie> cookies) {
/* 86 */     return ServerCookieEncoder.LAX.encode(cookies);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public static List<String> encode(Iterable<Cookie> cookies) {
/* 97 */     return ServerCookieEncoder.LAX.encode(cookies);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\ServerCookieEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */