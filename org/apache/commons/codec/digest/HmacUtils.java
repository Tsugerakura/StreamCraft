/*      */ package org.apache.commons.codec.digest;
/*      */ 
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.security.InvalidKeyException;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import javax.crypto.Mac;
/*      */ import javax.crypto.spec.SecretKeySpec;
/*      */ import org.apache.commons.codec.binary.Hex;
/*      */ import org.apache.commons.codec.binary.StringUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public final class HmacUtils
/*      */ {
/*      */   private static final int STREAM_BUFFER_LENGTH = 1024;
/*      */   private final Mac mac;
/*      */   
/*      */   public static boolean isAvailable(String name) {
/*      */     try {
/*   70 */       Mac.getInstance(name);
/*   71 */       return true;
/*   72 */     } catch (NoSuchAlgorithmException e) {
/*   73 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAvailable(HmacAlgorithms name) {
/*      */     try {
/*   86 */       Mac.getInstance(name.getName());
/*   87 */       return true;
/*   88 */     } catch (NoSuchAlgorithmException e) {
/*   89 */       return false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Mac getHmacMd5(byte[] key) {
/*  110 */     return getInitializedMac(HmacAlgorithms.HMAC_MD5, key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Mac getHmacSha1(byte[] key) {
/*  130 */     return getInitializedMac(HmacAlgorithms.HMAC_SHA_1, key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Mac getHmacSha256(byte[] key) {
/*  150 */     return getInitializedMac(HmacAlgorithms.HMAC_SHA_256, key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Mac getHmacSha384(byte[] key) {
/*  170 */     return getInitializedMac(HmacAlgorithms.HMAC_SHA_384, key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static Mac getHmacSha512(byte[] key) {
/*  190 */     return getInitializedMac(HmacAlgorithms.HMAC_SHA_512, key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Mac getInitializedMac(HmacAlgorithms algorithm, byte[] key) {
/*  210 */     return getInitializedMac(algorithm.getName(), key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Mac getInitializedMac(String algorithm, byte[] key) {
/*  231 */     if (key == null) {
/*  232 */       throw new IllegalArgumentException("Null key");
/*      */     }
/*      */     
/*      */     try {
/*  236 */       SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
/*  237 */       Mac mac = Mac.getInstance(algorithm);
/*  238 */       mac.init(keySpec);
/*  239 */       return mac;
/*  240 */     } catch (NoSuchAlgorithmException e) {
/*  241 */       throw new IllegalArgumentException(e);
/*  242 */     } catch (InvalidKeyException e) {
/*  243 */       throw new IllegalArgumentException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacMd5(byte[] key, byte[] valueToDigest) {
/*  263 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacMd5(byte[] key, InputStream valueToDigest) throws IOException {
/*  285 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacMd5(String key, String valueToDigest) {
/*  302 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacMd5Hex(byte[] key, byte[] valueToDigest) {
/*  319 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacMd5Hex(byte[] key, InputStream valueToDigest) throws IOException {
/*  341 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacMd5Hex(String key, String valueToDigest) {
/*  358 */     return (new HmacUtils(HmacAlgorithms.HMAC_MD5, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha1(byte[] key, byte[] valueToDigest) {
/*  377 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha1(byte[] key, InputStream valueToDigest) throws IOException {
/*  399 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha1(String key, String valueToDigest) {
/*  416 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha1Hex(byte[] key, byte[] valueToDigest) {
/*  433 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha1Hex(byte[] key, InputStream valueToDigest) throws IOException {
/*  455 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha1Hex(String key, String valueToDigest) {
/*  472 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha256(byte[] key, byte[] valueToDigest) {
/*  491 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha256(byte[] key, InputStream valueToDigest) throws IOException {
/*  513 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha256(String key, String valueToDigest) {
/*  530 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha256Hex(byte[] key, byte[] valueToDigest) {
/*  547 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha256Hex(byte[] key, InputStream valueToDigest) throws IOException {
/*  569 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha256Hex(String key, String valueToDigest) {
/*  586 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha384(byte[] key, byte[] valueToDigest) {
/*  605 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha384(byte[] key, InputStream valueToDigest) throws IOException {
/*  627 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha384(String key, String valueToDigest) {
/*  644 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha384Hex(byte[] key, byte[] valueToDigest) {
/*  661 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha384Hex(byte[] key, InputStream valueToDigest) throws IOException {
/*  683 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha384Hex(String key, String valueToDigest) {
/*  700 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_384, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha512(byte[] key, byte[] valueToDigest) {
/*  719 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha512(byte[] key, InputStream valueToDigest) throws IOException {
/*  741 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static byte[] hmacSha512(String key, String valueToDigest) {
/*  758 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmac(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha512Hex(byte[] key, byte[] valueToDigest) {
/*  775 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha512Hex(byte[] key, InputStream valueToDigest) throws IOException {
/*  797 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static String hmacSha512Hex(String key, String valueToDigest) {
/*  814 */     return (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, key)).hmacHex(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Mac updateHmac(Mac mac, byte[] valueToDigest) {
/*  831 */     mac.reset();
/*  832 */     mac.update(valueToDigest);
/*  833 */     return mac;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Mac updateHmac(Mac mac, InputStream valueToDigest) throws IOException {
/*  853 */     mac.reset();
/*  854 */     byte[] buffer = new byte[1024];
/*  855 */     int read = valueToDigest.read(buffer, 0, 1024);
/*      */     
/*  857 */     while (read > -1) {
/*  858 */       mac.update(buffer, 0, read);
/*  859 */       read = valueToDigest.read(buffer, 0, 1024);
/*      */     } 
/*      */     
/*  862 */     return mac;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Mac updateHmac(Mac mac, String valueToDigest) {
/*  877 */     mac.reset();
/*  878 */     mac.update(StringUtils.getBytesUtf8(valueToDigest));
/*  879 */     return mac;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public HmacUtils() {
/*  889 */     this(null);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private HmacUtils(Mac mac) {
/*  895 */     this.mac = mac;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HmacUtils(String algorithm, byte[] key) {
/*  908 */     this(getInitializedMac(algorithm, key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HmacUtils(String algorithm, String key) {
/*  921 */     this(algorithm, StringUtils.getBytesUtf8(key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HmacUtils(HmacAlgorithms algorithm, String key) {
/*  934 */     this(algorithm.getName(), StringUtils.getBytesUtf8(key));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HmacUtils(HmacAlgorithms algorithm, byte[] key) {
/*  947 */     this(algorithm.getName(), key);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] hmac(byte[] valueToDigest) {
/*  958 */     return this.mac.doFinal(valueToDigest);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String hmacHex(byte[] valueToDigest) {
/*  969 */     return Hex.encodeHexString(hmac(valueToDigest));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] hmac(String valueToDigest) {
/*  980 */     return this.mac.doFinal(StringUtils.getBytesUtf8(valueToDigest));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String hmacHex(String valueToDigest) {
/*  991 */     return Hex.encodeHexString(hmac(valueToDigest));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] hmac(ByteBuffer valueToDigest) {
/* 1002 */     this.mac.update(valueToDigest);
/* 1003 */     return this.mac.doFinal();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String hmacHex(ByteBuffer valueToDigest) {
/* 1014 */     return Hex.encodeHexString(hmac(valueToDigest));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] hmac(InputStream valueToDigest) throws IOException {
/* 1031 */     byte[] buffer = new byte[1024];
/*      */     
/*      */     int read;
/* 1034 */     while ((read = valueToDigest.read(buffer, 0, 1024)) > -1) {
/* 1035 */       this.mac.update(buffer, 0, read);
/*      */     }
/* 1037 */     return this.mac.doFinal();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String hmacHex(InputStream valueToDigest) throws IOException {
/* 1054 */     return Hex.encodeHexString(hmac(valueToDigest));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public byte[] hmac(File valueToDigest) throws IOException {
/* 1067 */     BufferedInputStream stream = new BufferedInputStream(new FileInputStream(valueToDigest));
/*      */     try {
/* 1069 */       return hmac(stream);
/*      */     } finally {
/* 1071 */       stream.close();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String hmacHex(File valueToDigest) throws IOException {
/* 1085 */     return Hex.encodeHexString(hmac(valueToDigest));
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\digest\HmacUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */