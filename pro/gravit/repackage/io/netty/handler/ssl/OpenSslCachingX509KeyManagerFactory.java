/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ import java.security.InvalidAlgorithmParameterException;
/*    */ import java.security.KeyStore;
/*    */ import java.security.KeyStoreException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.security.UnrecoverableKeyException;
/*    */ import javax.net.ssl.KeyManager;
/*    */ import javax.net.ssl.KeyManagerFactory;
/*    */ import javax.net.ssl.KeyManagerFactorySpi;
/*    */ import javax.net.ssl.ManagerFactoryParameters;
/*    */ import javax.net.ssl.X509KeyManager;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*    */ public final class OpenSslCachingX509KeyManagerFactory
/*    */   extends KeyManagerFactory
/*    */ {
/*    */   private final int maxCachedEntries;
/*    */   
/*    */   public OpenSslCachingX509KeyManagerFactory(KeyManagerFactory factory) {
/* 46 */     this(factory, 1024);
/*    */   }
/*    */   
/*    */   public OpenSslCachingX509KeyManagerFactory(KeyManagerFactory factory, int maxCachedEntries) {
/* 50 */     super(new KeyManagerFactorySpi(factory)
/*    */         {
/*    */           protected void engineInit(KeyStore keyStore, char[] chars) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
/*    */           {
/* 54 */             factory.init(keyStore, chars);
/*    */           }
/*    */ 
/*    */ 
/*    */           
/*    */           protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
/* 60 */             factory.init(managerFactoryParameters);
/*    */           }
/*    */ 
/*    */           
/*    */           protected KeyManager[] engineGetKeyManagers() {
/* 65 */             return factory.getKeyManagers();
/*    */           }
/* 67 */         }factory.getProvider(), factory.getAlgorithm());
/* 68 */     this.maxCachedEntries = ObjectUtil.checkPositive(maxCachedEntries, "maxCachedEntries");
/*    */   }
/*    */   
/*    */   OpenSslKeyMaterialProvider newProvider(String password) {
/* 72 */     X509KeyManager keyManager = ReferenceCountedOpenSslContext.chooseX509KeyManager(getKeyManagers());
/* 73 */     if ("sun.security.ssl.X509KeyManagerImpl".equals(keyManager.getClass().getName()))
/*    */     {
/*    */       
/* 76 */       return new OpenSslKeyMaterialProvider(keyManager, password);
/*    */     }
/* 78 */     return new OpenSslCachingKeyMaterialProvider(
/* 79 */         ReferenceCountedOpenSslContext.chooseX509KeyManager(getKeyManagers()), password, this.maxCachedEntries);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslCachingX509KeyManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */