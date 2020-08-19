/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.List;
/*     */ import java.util.function.BiFunction;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLParameters;
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
/*     */ @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */ final class Java9SslUtils
/*     */ {
/*  36 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Java9SslUtils.class);
/*     */   private static final Method SET_APPLICATION_PROTOCOLS;
/*     */   private static final Method GET_APPLICATION_PROTOCOL;
/*     */   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
/*     */   private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
/*     */   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
/*     */   
/*     */   static {
/*  44 */     Method getHandshakeApplicationProtocol = null;
/*  45 */     Method getApplicationProtocol = null;
/*  46 */     Method setApplicationProtocols = null;
/*  47 */     Method setHandshakeApplicationProtocolSelector = null;
/*  48 */     Method getHandshakeApplicationProtocolSelector = null;
/*     */     
/*     */     try {
/*  51 */       SSLContext context = SSLContext.getInstance("TLS");
/*  52 */       context.init(null, null, null);
/*  53 */       SSLEngine engine = context.createSSLEngine();
/*  54 */       getHandshakeApplicationProtocol = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>()
/*     */           {
/*     */             public Method run() throws Exception {
/*  57 */               return SSLEngine.class.getMethod("getHandshakeApplicationProtocol", new Class[0]);
/*     */             }
/*     */           });
/*  60 */       getHandshakeApplicationProtocol.invoke(engine, new Object[0]);
/*  61 */       getApplicationProtocol = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>()
/*     */           {
/*     */             public Method run() throws Exception {
/*  64 */               return SSLEngine.class.getMethod("getApplicationProtocol", new Class[0]);
/*     */             }
/*     */           });
/*  67 */       getApplicationProtocol.invoke(engine, new Object[0]);
/*     */       
/*  69 */       setApplicationProtocols = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>()
/*     */           {
/*     */             public Method run() throws Exception {
/*  72 */               return SSLParameters.class.getMethod("setApplicationProtocols", new Class[] { String[].class });
/*     */             }
/*     */           });
/*  75 */       setApplicationProtocols.invoke(engine.getSSLParameters(), new Object[] { EmptyArrays.EMPTY_STRINGS });
/*     */ 
/*     */       
/*  78 */       setHandshakeApplicationProtocolSelector = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>()
/*     */           {
/*     */             public Method run() throws Exception {
/*  81 */               return SSLEngine.class.getMethod("setHandshakeApplicationProtocolSelector", new Class[] { BiFunction.class });
/*     */             }
/*     */           });
/*  84 */       setHandshakeApplicationProtocolSelector.invoke(engine, new Object[] { new BiFunction<SSLEngine, List<String>, String>()
/*     */             {
/*     */               public String apply(SSLEngine sslEngine, List<String> strings) {
/*  87 */                 return null;
/*     */               }
/*     */             } });
/*     */ 
/*     */       
/*  92 */       getHandshakeApplicationProtocolSelector = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>()
/*     */           {
/*     */             public Method run() throws Exception {
/*  95 */               return SSLEngine.class.getMethod("getHandshakeApplicationProtocolSelector", new Class[0]);
/*     */             }
/*     */           });
/*  98 */       getHandshakeApplicationProtocolSelector.invoke(engine, new Object[0]);
/*  99 */     } catch (Throwable t) {
/* 100 */       logger.error("Unable to initialize Java9SslUtils, but the detected javaVersion was: {}", 
/* 101 */           Integer.valueOf(PlatformDependent.javaVersion()), t);
/* 102 */       getHandshakeApplicationProtocol = null;
/* 103 */       getApplicationProtocol = null;
/* 104 */       setApplicationProtocols = null;
/* 105 */       setHandshakeApplicationProtocolSelector = null;
/* 106 */       getHandshakeApplicationProtocolSelector = null;
/*     */     } 
/* 108 */     GET_HANDSHAKE_APPLICATION_PROTOCOL = getHandshakeApplicationProtocol;
/* 109 */     GET_APPLICATION_PROTOCOL = getApplicationProtocol;
/* 110 */     SET_APPLICATION_PROTOCOLS = setApplicationProtocols;
/* 111 */     SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = setHandshakeApplicationProtocolSelector;
/* 112 */     GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = getHandshakeApplicationProtocolSelector;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean supportsAlpn() {
/* 119 */     return (GET_APPLICATION_PROTOCOL != null);
/*     */   }
/*     */   
/*     */   static String getApplicationProtocol(SSLEngine sslEngine) {
/*     */     try {
/* 124 */       return (String)GET_APPLICATION_PROTOCOL.invoke(sslEngine, new Object[0]);
/* 125 */     } catch (UnsupportedOperationException ex) {
/* 126 */       throw ex;
/* 127 */     } catch (Exception ex) {
/* 128 */       throw new IllegalStateException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   static String getHandshakeApplicationProtocol(SSLEngine sslEngine) {
/*     */     try {
/* 134 */       return (String)GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke(sslEngine, new Object[0]);
/* 135 */     } catch (UnsupportedOperationException ex) {
/* 136 */       throw ex;
/* 137 */     } catch (Exception ex) {
/* 138 */       throw new IllegalStateException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void setApplicationProtocols(SSLEngine engine, List<String> supportedProtocols) {
/* 143 */     SSLParameters parameters = engine.getSSLParameters();
/*     */     
/* 145 */     String[] protocolArray = supportedProtocols.<String>toArray(EmptyArrays.EMPTY_STRINGS);
/*     */     try {
/* 147 */       SET_APPLICATION_PROTOCOLS.invoke(parameters, new Object[] { protocolArray });
/* 148 */     } catch (UnsupportedOperationException ex) {
/* 149 */       throw ex;
/* 150 */     } catch (Exception ex) {
/* 151 */       throw new IllegalStateException(ex);
/*     */     } 
/* 153 */     engine.setSSLParameters(parameters);
/*     */   }
/*     */ 
/*     */   
/*     */   static void setHandshakeApplicationProtocolSelector(SSLEngine engine, BiFunction<SSLEngine, List<String>, String> selector) {
/*     */     try {
/* 159 */       SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, new Object[] { selector });
/* 160 */     } catch (UnsupportedOperationException ex) {
/* 161 */       throw ex;
/* 162 */     } catch (Exception ex) {
/* 163 */       throw new IllegalStateException(ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   static BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector(SSLEngine engine) {
/*     */     try {
/* 169 */       return (BiFunction<SSLEngine, List<String>, String>)GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR
/* 170 */         .invoke(engine, new Object[0]);
/* 171 */     } catch (UnsupportedOperationException ex) {
/* 172 */       throw ex;
/* 173 */     } catch (Exception ex) {
/* 174 */       throw new IllegalStateException(ex);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\Java9SslUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */