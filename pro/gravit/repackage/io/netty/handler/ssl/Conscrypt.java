/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import javax.net.ssl.SSLEngine;
/*    */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*    */ final class Conscrypt
/*    */ {
/* 30 */   private static final Method IS_CONSCRYPT_SSLENGINE = loadIsConscryptEngine();
/* 31 */   private static final boolean CAN_INSTANCE_PROVIDER = canInstanceProvider();
/*    */   
/*    */   private static Method loadIsConscryptEngine() {
/*    */     try {
/* 35 */       Class<?> conscryptClass = Class.forName("org.conscrypt.Conscrypt", true, ConscryptAlpnSslEngine.class
/* 36 */           .getClassLoader());
/* 37 */       return conscryptClass.getMethod("isConscrypt", new Class[] { SSLEngine.class });
/* 38 */     } catch (Throwable ignore) {
/*    */       
/* 40 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   private static boolean canInstanceProvider() {
/*    */     try {
/* 46 */       Class<?> providerClass = Class.forName("org.conscrypt.OpenSSLProvider", true, ConscryptAlpnSslEngine.class
/* 47 */           .getClassLoader());
/* 48 */       providerClass.newInstance();
/* 49 */       return true;
/* 50 */     } catch (Throwable ignore) {
/* 51 */       return false;
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static boolean isAvailable() {
/* 59 */     return (CAN_INSTANCE_PROVIDER && IS_CONSCRYPT_SSLENGINE != null && PlatformDependent.javaVersion() >= 8);
/*    */   }
/*    */   
/*    */   static boolean isEngineSupported(SSLEngine engine) {
/* 63 */     return (isAvailable() && isConscryptEngine(engine));
/*    */   }
/*    */   
/*    */   private static boolean isConscryptEngine(SSLEngine engine) {
/*    */     try {
/* 68 */       return ((Boolean)IS_CONSCRYPT_SSLENGINE.invoke(null, new Object[] { engine })).booleanValue();
/* 69 */     } catch (IllegalAccessException ignore) {
/* 70 */       return false;
/* 71 */     } catch (InvocationTargetException ex) {
/* 72 */       throw new RuntimeException(ex);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\Conscrypt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */