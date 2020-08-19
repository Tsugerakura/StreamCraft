/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Base64;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.base64.Base64;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
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
/*     */ final class WebSocketUtil
/*     */ {
/*  34 */   private static final FastThreadLocal<MessageDigest> MD5 = new FastThreadLocal<MessageDigest>()
/*     */     {
/*     */       protected MessageDigest initialValue() throws Exception
/*     */       {
/*     */         try {
/*  39 */           return MessageDigest.getInstance("MD5");
/*  40 */         } catch (NoSuchAlgorithmException e) {
/*     */           
/*  42 */           throw new InternalError("MD5 not supported on this platform - Outdated?");
/*     */         } 
/*     */       }
/*     */     };
/*     */   
/*  47 */   private static final FastThreadLocal<MessageDigest> SHA1 = new FastThreadLocal<MessageDigest>()
/*     */     {
/*     */       protected MessageDigest initialValue() throws Exception
/*     */       {
/*     */         try {
/*  52 */           return MessageDigest.getInstance("SHA1");
/*  53 */         } catch (NoSuchAlgorithmException e) {
/*     */           
/*  55 */           throw new InternalError("SHA-1 not supported on this platform - Outdated?");
/*     */         } 
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static byte[] md5(byte[] data) {
/*  68 */     return digest(MD5, data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static byte[] sha1(byte[] data) {
/*  79 */     return digest(SHA1, data);
/*     */   }
/*     */   
/*     */   private static byte[] digest(FastThreadLocal<MessageDigest> digestFastThreadLocal, byte[] data) {
/*  83 */     MessageDigest digest = (MessageDigest)digestFastThreadLocal.get();
/*  84 */     digest.reset();
/*  85 */     return digest.digest(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SuppressJava6Requirement(reason = "Guarded with java version check")
/*     */   static String base64(byte[] data) {
/*  96 */     if (PlatformDependent.javaVersion() >= 8) {
/*  97 */       return Base64.getEncoder().encodeToString(data);
/*     */     }
/*  99 */     ByteBuf encodedData = Unpooled.wrappedBuffer(data);
/* 100 */     ByteBuf encoded = Base64.encode(encodedData);
/* 101 */     String encodedString = encoded.toString(CharsetUtil.UTF_8);
/* 102 */     encoded.release();
/* 103 */     return encodedString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static byte[] randomBytes(int size) {
/* 113 */     byte[] bytes = new byte[size];
/* 114 */     PlatformDependent.threadLocalRandom().nextBytes(bytes);
/* 115 */     return bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static int randomNumber(int minimum, int maximum) {
/* 126 */     assert minimum < maximum;
/* 127 */     double fraction = PlatformDependent.threadLocalRandom().nextDouble();
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
/* 148 */     return (int)(minimum + fraction * (maximum - minimum));
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */