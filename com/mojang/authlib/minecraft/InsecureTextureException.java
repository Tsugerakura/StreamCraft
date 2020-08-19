/*    */ package com.mojang.authlib.minecraft;
/*    */ 
/*    */ import com.mojang.authlib.GameProfile;
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class InsecureTextureException
/*    */   extends RuntimeException {
/*    */   public InsecureTextureException(String message) {
/* 11 */     super(message);
/*    */   }
/*    */   
/*    */   public static class OutdatedTextureException extends InsecureTextureException {
/*    */     private final Date validFrom;
/*    */     private final Calendar limit;
/*    */     
/*    */     public OutdatedTextureException(Date validFrom, Calendar limit) {
/* 19 */       super("Decrypted textures payload is too old (" + validFrom + ", but we need it to be at least " + limit + ")");
/* 20 */       this.validFrom = validFrom;
/* 21 */       this.limit = limit;
/*    */     }
/*    */   }
/*    */   
/*    */   public static class WrongTextureOwnerException extends InsecureTextureException {
/*    */     private final GameProfile expected;
/*    */     private final UUID resultId;
/*    */     private final String resultName;
/*    */     
/*    */     public WrongTextureOwnerException(GameProfile expected, UUID resultId, String resultName) {
/* 31 */       super("Decrypted textures payload was for another user (expected " + expected.getId() + "/" + expected.getName() + " but was for " + resultId + "/" + resultName + ")");
/* 32 */       this.expected = expected;
/* 33 */       this.resultId = resultId;
/* 34 */       this.resultName = resultName;
/*    */     }
/*    */   }
/*    */   
/*    */   public static class MissingTextureException extends InsecureTextureException {
/*    */     public MissingTextureException() {
/* 40 */       super("No texture information found");
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\minecraft\InsecureTextureException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */