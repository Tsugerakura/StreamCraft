/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import javax.crypto.SecretKey;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelInboundHandlerAdapter;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ReflectionUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
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
/*     */ public abstract class SslMasterKeyHandler
/*     */   extends ChannelInboundHandlerAdapter
/*     */ {
/*  40 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslMasterKeyHandler.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Class<?> SSL_SESSIONIMPL_CLASS;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Field SSL_SESSIONIMPL_MASTER_SECRET_FIELD;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SYSTEM_PROP_KEY = "pro.gravit.repackage.io.netty.ssl.masterKeyHandler";
/*     */ 
/*     */ 
/*     */   
/*     */   private static final Throwable UNAVAILABILITY_CAUSE;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  65 */     Throwable cause = null;
/*  66 */     Class<?> clazz = null;
/*  67 */     Field field = null;
/*     */     try {
/*  69 */       clazz = Class.forName("sun.security.ssl.SSLSessionImpl");
/*  70 */       field = clazz.getDeclaredField("masterSecret");
/*  71 */       cause = ReflectionUtil.trySetAccessible(field, true);
/*  72 */     } catch (Throwable e) {
/*  73 */       cause = e;
/*  74 */       logger.debug("sun.security.ssl.SSLSessionImpl is unavailable.", e);
/*     */     } 
/*  76 */     UNAVAILABILITY_CAUSE = cause;
/*  77 */     SSL_SESSIONIMPL_CLASS = clazz;
/*  78 */     SSL_SESSIONIMPL_MASTER_SECRET_FIELD = field;
/*     */   }
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
/*     */   public static void ensureSunSslEngineAvailability() {
/*  92 */     if (UNAVAILABILITY_CAUSE != null) {
/*  93 */       throw new IllegalStateException("Failed to find SSLSessionImpl on classpath", UNAVAILABILITY_CAUSE);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Throwable sunSslEngineUnavailabilityCause() {
/* 104 */     return UNAVAILABILITY_CAUSE;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isSunSslEngineAvailable() {
/* 110 */     return (UNAVAILABILITY_CAUSE == null);
/*     */   }
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
/*     */   public final void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
/* 123 */     if (evt == SslHandshakeCompletionEvent.SUCCESS) {
/* 124 */       boolean shouldHandle = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.ssl.masterKeyHandler", false);
/*     */       
/* 126 */       if (shouldHandle) {
/* 127 */         SslHandler handler = (SslHandler)ctx.pipeline().get(SslHandler.class);
/* 128 */         SSLEngine engine = handler.engine();
/* 129 */         SSLSession sslSession = engine.getSession();
/*     */ 
/*     */         
/* 132 */         if (isSunSslEngineAvailable() && sslSession.getClass().equals(SSL_SESSIONIMPL_CLASS)) {
/*     */           SecretKey secretKey;
/*     */           try {
/* 135 */             secretKey = (SecretKey)SSL_SESSIONIMPL_MASTER_SECRET_FIELD.get(sslSession);
/* 136 */           } catch (IllegalAccessException e) {
/* 137 */             throw new IllegalArgumentException("Failed to access the field 'masterSecret' via reflection.", e);
/*     */           } 
/*     */           
/* 140 */           accept(secretKey, sslSession);
/* 141 */         } else if (OpenSsl.isAvailable() && engine instanceof ReferenceCountedOpenSslEngine) {
/* 142 */           SecretKeySpec secretKey = ((ReferenceCountedOpenSslEngine)engine).masterKey();
/* 143 */           accept(secretKey, sslSession);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 148 */     ctx.fireUserEventTriggered(evt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SslMasterKeyHandler newWireSharkSslMasterKeyHandler() {
/* 159 */     return new WiresharkSslMasterKeyHandler();
/*     */   }
/*     */ 
/*     */   
/*     */   protected abstract void accept(SecretKey paramSecretKey, SSLSession paramSSLSession);
/*     */ 
/*     */   
/*     */   private static final class WiresharkSslMasterKeyHandler
/*     */     extends SslMasterKeyHandler
/*     */   {
/*     */     private WiresharkSslMasterKeyHandler() {}
/*     */ 
/*     */     
/* 172 */     private static final InternalLogger wireshark_logger = InternalLoggerFactory.getInstance("pro.gravit.repackage.io.netty.wireshark");
/*     */     
/* 174 */     private static final char[] hexCode = "0123456789ABCDEF".toCharArray();
/*     */ 
/*     */     
/*     */     protected void accept(SecretKey masterKey, SSLSession session) {
/* 178 */       if ((masterKey.getEncoded()).length != 48) {
/* 179 */         throw new IllegalArgumentException("An invalid length master key was provided.");
/*     */       }
/* 181 */       byte[] sessionId = session.getId();
/* 182 */       wireshark_logger.warn("RSA Session-ID:{} Master-Key:{}", 
/* 183 */           ByteBufUtil.hexDump(sessionId).toLowerCase(), 
/* 184 */           ByteBufUtil.hexDump(masterKey.getEncoded()).toLowerCase());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SslMasterKeyHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */