/*     */ package pro.gravit.repackage.io.netty.handler.ssl.util;
/*     */ 
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.Provider;
/*     */ import javax.net.ssl.KeyManager;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.KeyManagerFactorySpi;
/*     */ import javax.net.ssl.ManagerFactoryParameters;
/*     */ import javax.net.ssl.X509KeyManager;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SimpleKeyManagerFactory
/*     */   extends KeyManagerFactory
/*     */ {
/*  40 */   private static final Provider PROVIDER = new Provider("", 0.0D, "")
/*     */     {
/*     */       private static final long serialVersionUID = -2680540247105807895L;
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   private static final FastThreadLocal<SimpleKeyManagerFactorySpi> CURRENT_SPI = new FastThreadLocal<SimpleKeyManagerFactorySpi>()
/*     */     {
/*     */       protected SimpleKeyManagerFactory.SimpleKeyManagerFactorySpi initialValue()
/*     */       {
/*  56 */         return new SimpleKeyManagerFactory.SimpleKeyManagerFactorySpi();
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SimpleKeyManagerFactory() {
/*  64 */     this("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SimpleKeyManagerFactory(String name) {
/*  73 */     super((KeyManagerFactorySpi)CURRENT_SPI.get(), PROVIDER, (String)ObjectUtil.checkNotNull(name, "name"));
/*  74 */     ((SimpleKeyManagerFactorySpi)CURRENT_SPI.get()).init(this);
/*  75 */     CURRENT_SPI.remove();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void engineInit(KeyStore paramKeyStore, char[] paramArrayOfchar) throws Exception;
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void engineInit(ManagerFactoryParameters paramManagerFactoryParameters) throws Exception;
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract KeyManager[] engineGetKeyManagers();
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class SimpleKeyManagerFactorySpi
/*     */     extends KeyManagerFactorySpi
/*     */   {
/*     */     private SimpleKeyManagerFactory parent;
/*     */ 
/*     */     
/*     */     private volatile KeyManager[] keyManagers;
/*     */ 
/*     */     
/*     */     private SimpleKeyManagerFactorySpi() {}
/*     */ 
/*     */     
/*     */     void init(SimpleKeyManagerFactory parent) {
/* 105 */       this.parent = parent;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void engineInit(KeyStore keyStore, char[] pwd) throws KeyStoreException {
/*     */       try {
/* 111 */         this.parent.engineInit(keyStore, pwd);
/* 112 */       } catch (KeyStoreException e) {
/* 113 */         throw e;
/* 114 */       } catch (Exception e) {
/* 115 */         throw new KeyStoreException(e);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
/*     */       try {
/* 123 */         this.parent.engineInit(managerFactoryParameters);
/* 124 */       } catch (InvalidAlgorithmParameterException e) {
/* 125 */         throw e;
/* 126 */       } catch (Exception e) {
/* 127 */         throw new InvalidAlgorithmParameterException(e);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected KeyManager[] engineGetKeyManagers() {
/* 133 */       KeyManager[] keyManagers = this.keyManagers;
/* 134 */       if (keyManagers == null) {
/* 135 */         keyManagers = this.parent.engineGetKeyManagers();
/* 136 */         if (PlatformDependent.javaVersion() >= 7) {
/* 137 */           wrapIfNeeded(keyManagers);
/*     */         }
/* 139 */         this.keyManagers = keyManagers;
/*     */       } 
/* 141 */       return (KeyManager[])keyManagers.clone();
/*     */     }
/*     */     
/*     */     @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */     private static void wrapIfNeeded(KeyManager[] keyManagers) {
/* 146 */       for (int i = 0; i < keyManagers.length; i++) {
/* 147 */         KeyManager tm = keyManagers[i];
/* 148 */         if (tm instanceof X509KeyManager && !(tm instanceof javax.net.ssl.X509ExtendedKeyManager))
/* 149 */           keyManagers[i] = new X509KeyManagerWrapper((X509KeyManager)tm); 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ss\\util\SimpleKeyManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */