/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.security.AccessController;
/*     */ import java.security.KeyManagementException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */ final class OpenSslX509TrustManagerWrapper
/*     */ {
/*  46 */   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(OpenSslX509TrustManagerWrapper.class);
/*     */   
/*     */   private static final TrustManagerWrapper WRAPPER;
/*     */   
/*     */   static {
/*  51 */     TrustManagerWrapper wrapper = new TrustManagerWrapper()
/*     */       {
/*     */         public X509TrustManager wrapIfNeeded(X509TrustManager manager) {
/*  54 */           return manager;
/*     */         }
/*     */       };
/*     */     
/*  58 */     Throwable cause = null;
/*  59 */     Throwable unsafeCause = PlatformDependent.getUnsafeUnavailabilityCause();
/*  60 */     if (unsafeCause == null) {
/*     */       SSLContext context;
/*     */       try {
/*  63 */         context = newSSLContext();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  71 */         context.init(null, new TrustManager[] { new X509TrustManager()
/*     */               {
/*     */                 
/*     */                 public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
/*     */                 {
/*  76 */                   throw new CertificateException();
/*     */                 }
/*     */ 
/*     */ 
/*     */                 
/*     */                 public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
/*  82 */                   throw new CertificateException();
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public X509Certificate[] getAcceptedIssuers() {
/*  87 */                   return EmptyArrays.EMPTY_X509_CERTIFICATES;
/*     */                 }
/*     */               }, 
/*     */                }, null);
/*  91 */       } catch (Throwable error) {
/*  92 */         context = null;
/*  93 */         cause = error;
/*     */       } 
/*  95 */       if (cause != null) {
/*  96 */         LOGGER.debug("Unable to access wrapped TrustManager", cause);
/*     */       } else {
/*  98 */         final SSLContext finalContext = context;
/*  99 */         Object maybeWrapper = AccessController.doPrivileged(new PrivilegedAction()
/*     */             {
/*     */               public Object run() {
/*     */                 try {
/* 103 */                   Field contextSpiField = SSLContext.class.getDeclaredField("contextSpi");
/* 104 */                   long spiOffset = PlatformDependent.objectFieldOffset(contextSpiField);
/* 105 */                   Object spi = PlatformDependent.getObject(finalContext, spiOffset);
/* 106 */                   if (spi != null) {
/* 107 */                     Class<?> clazz = spi.getClass();
/*     */ 
/*     */ 
/*     */                     
/*     */                     do {
/*     */                       try {
/* 113 */                         Field trustManagerField = clazz.getDeclaredField("trustManager");
/* 114 */                         long tmOffset = PlatformDependent.objectFieldOffset(trustManagerField);
/* 115 */                         Object trustManager = PlatformDependent.getObject(spi, tmOffset);
/* 116 */                         if (trustManager instanceof javax.net.ssl.X509ExtendedTrustManager) {
/* 117 */                           return new OpenSslX509TrustManagerWrapper.UnsafeTrustManagerWrapper(spiOffset, tmOffset);
/*     */                         }
/* 119 */                       } catch (NoSuchFieldException noSuchFieldException) {}
/*     */ 
/*     */                       
/* 122 */                       clazz = clazz.getSuperclass();
/* 123 */                     } while (clazz != null);
/*     */                   } 
/* 125 */                   throw new NoSuchFieldException();
/* 126 */                 } catch (NoSuchFieldException e) {
/* 127 */                   return e;
/* 128 */                 } catch (SecurityException e) {
/* 129 */                   return e;
/*     */                 } 
/*     */               }
/*     */             });
/* 133 */         if (maybeWrapper instanceof Throwable) {
/* 134 */           LOGGER.debug("Unable to access wrapped TrustManager", (Throwable)maybeWrapper);
/*     */         } else {
/* 136 */           wrapper = (TrustManagerWrapper)maybeWrapper;
/*     */         } 
/*     */       } 
/*     */     } else {
/* 140 */       LOGGER.debug("Unable to access wrapped TrustManager", cause);
/*     */     } 
/* 142 */     WRAPPER = wrapper;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static X509TrustManager wrapIfNeeded(X509TrustManager trustManager) {
/* 148 */     return WRAPPER.wrapIfNeeded(trustManager);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static SSLContext newSSLContext() throws NoSuchAlgorithmException {
/* 156 */     return SSLContext.getInstance("TLS");
/*     */   }
/*     */   private static interface TrustManagerWrapper {
/*     */     X509TrustManager wrapIfNeeded(X509TrustManager param1X509TrustManager); }
/*     */   
/*     */   private static final class UnsafeTrustManagerWrapper implements TrustManagerWrapper { private final long spiOffset;
/*     */     
/*     */     UnsafeTrustManagerWrapper(long spiOffset, long tmOffset) {
/* 164 */       this.spiOffset = spiOffset;
/* 165 */       this.tmOffset = tmOffset;
/*     */     }
/*     */     private final long tmOffset;
/*     */     
/*     */     @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */     public X509TrustManager wrapIfNeeded(X509TrustManager manager) {
/* 171 */       if (!(manager instanceof javax.net.ssl.X509ExtendedTrustManager)) {
/*     */         try {
/* 173 */           SSLContext ctx = OpenSslX509TrustManagerWrapper.newSSLContext();
/* 174 */           ctx.init(null, new TrustManager[] { manager }, null);
/* 175 */           Object spi = PlatformDependent.getObject(ctx, this.spiOffset);
/* 176 */           if (spi != null) {
/* 177 */             Object tm = PlatformDependent.getObject(spi, this.tmOffset);
/* 178 */             if (tm instanceof javax.net.ssl.X509ExtendedTrustManager) {
/* 179 */               return (X509TrustManager)tm;
/*     */             }
/*     */           } 
/* 182 */         } catch (NoSuchAlgorithmException e) {
/*     */ 
/*     */           
/* 185 */           PlatformDependent.throwException(e);
/* 186 */         } catch (KeyManagementException e) {
/*     */ 
/*     */           
/* 189 */           PlatformDependent.throwException(e);
/*     */         } 
/*     */       }
/* 192 */       return manager;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslX509TrustManagerWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */