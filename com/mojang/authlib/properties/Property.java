/*    */ package com.mojang.authlib.properties;
/*    */ 
/*    */ import java.security.InvalidKeyException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.security.PublicKey;
/*    */ import java.security.Signature;
/*    */ import java.security.SignatureException;
/*    */ import org.apache.commons.codec.binary.Base64;
/*    */ 
/*    */ public class Property
/*    */ {
/*    */   private final String name;
/*    */   private final String value;
/*    */   private final String signature;
/*    */   
/*    */   public Property(String value, String name) {
/* 17 */     this(value, name, null);
/*    */   }
/*    */   
/*    */   public Property(String name, String value, String signature) {
/* 21 */     this.name = name;
/* 22 */     this.value = value;
/* 23 */     this.signature = signature;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 27 */     return this.name;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 31 */     return this.value;
/*    */   }
/*    */   
/*    */   public String getSignature() {
/* 35 */     return this.signature;
/*    */   }
/*    */   
/*    */   public boolean hasSignature() {
/* 39 */     return (this.signature != null);
/*    */   }
/*    */   
/*    */   public boolean isSignatureValid(PublicKey publicKey) {
/*    */     try {
/* 44 */       Signature signature = Signature.getInstance("SHA1withRSA");
/* 45 */       signature.initVerify(publicKey);
/* 46 */       signature.update(this.value.getBytes());
/* 47 */       return signature.verify(Base64.decodeBase64(this.signature));
/* 48 */     } catch (NoSuchAlgorithmException e) {
/* 49 */       e.printStackTrace();
/* 50 */     } catch (InvalidKeyException e) {
/* 51 */       e.printStackTrace();
/* 52 */     } catch (SignatureException e) {
/* 53 */       e.printStackTrace();
/*    */     } 
/* 55 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\properties\Property.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */