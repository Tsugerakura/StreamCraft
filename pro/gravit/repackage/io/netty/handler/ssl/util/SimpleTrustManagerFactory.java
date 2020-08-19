/*     */ package pro.gravit.repackage.io.netty.handler.ssl.util;
/*     */ 
/*     */ import java.security.InvalidAlgorithmParameterException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.KeyStoreException;
/*     */ import java.security.Provider;
/*     */ import javax.net.ssl.ManagerFactoryParameters;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.TrustManagerFactorySpi;
/*     */ import javax.net.ssl.X509TrustManager;
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
/*     */ public abstract class SimpleTrustManagerFactory
/*     */   extends TrustManagerFactory
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
/*  52 */   private static final FastThreadLocal<SimpleTrustManagerFactorySpi> CURRENT_SPI = new FastThreadLocal<SimpleTrustManagerFactorySpi>()
/*     */     {
/*     */       protected SimpleTrustManagerFactory.SimpleTrustManagerFactorySpi initialValue()
/*     */       {
/*  56 */         return new SimpleTrustManagerFactory.SimpleTrustManagerFactorySpi();
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SimpleTrustManagerFactory() {
/*  64 */     this("");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SimpleTrustManagerFactory(String name) {
/*  73 */     super((TrustManagerFactorySpi)CURRENT_SPI.get(), PROVIDER, name);
/*  74 */     ((SimpleTrustManagerFactorySpi)CURRENT_SPI.get()).init(this);
/*  75 */     CURRENT_SPI.remove();
/*     */     
/*  77 */     ObjectUtil.checkNotNull(name, "name");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void engineInit(KeyStore paramKeyStore) throws Exception;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void engineInit(ManagerFactoryParameters paramManagerFactoryParameters) throws Exception;
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract TrustManager[] engineGetTrustManagers();
/*     */ 
/*     */ 
/*     */   
/*     */   static final class SimpleTrustManagerFactorySpi
/*     */     extends TrustManagerFactorySpi
/*     */   {
/*     */     private SimpleTrustManagerFactory parent;
/*     */ 
/*     */     
/*     */     private volatile TrustManager[] trustManagers;
/*     */ 
/*     */ 
/*     */     
/*     */     void init(SimpleTrustManagerFactory parent) {
/* 107 */       this.parent = parent;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void engineInit(KeyStore keyStore) throws KeyStoreException {
/*     */       try {
/* 113 */         this.parent.engineInit(keyStore);
/* 114 */       } catch (KeyStoreException e) {
/* 115 */         throw e;
/* 116 */       } catch (Exception e) {
/* 117 */         throw new KeyStoreException(e);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
/*     */       try {
/* 125 */         this.parent.engineInit(managerFactoryParameters);
/* 126 */       } catch (InvalidAlgorithmParameterException e) {
/* 127 */         throw e;
/* 128 */       } catch (Exception e) {
/* 129 */         throw new InvalidAlgorithmParameterException(e);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected TrustManager[] engineGetTrustManagers() {
/* 135 */       TrustManager[] trustManagers = this.trustManagers;
/* 136 */       if (trustManagers == null) {
/* 137 */         trustManagers = this.parent.engineGetTrustManagers();
/* 138 */         if (PlatformDependent.javaVersion() >= 7) {
/* 139 */           wrapIfNeeded(trustManagers);
/*     */         }
/* 141 */         this.trustManagers = trustManagers;
/*     */       } 
/* 143 */       return (TrustManager[])trustManagers.clone();
/*     */     }
/*     */     
/*     */     @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */     private static void wrapIfNeeded(TrustManager[] trustManagers) {
/* 148 */       for (int i = 0; i < trustManagers.length; i++) {
/* 149 */         TrustManager tm = trustManagers[i];
/* 150 */         if (tm instanceof X509TrustManager && !(tm instanceof javax.net.ssl.X509ExtendedTrustManager))
/* 151 */           trustManagers[i] = new X509TrustManagerWrapper((X509TrustManager)tm); 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ss\\util\SimpleTrustManagerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */