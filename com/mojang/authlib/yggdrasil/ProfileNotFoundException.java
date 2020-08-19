/*    */ package com.mojang.authlib.yggdrasil;
/*    */ 
/*    */ public class ProfileNotFoundException
/*    */   extends RuntimeException {
/*    */   public ProfileNotFoundException() {}
/*    */   
/*    */   public ProfileNotFoundException(String message) {
/*  8 */     super(message);
/*    */   }
/*    */   
/*    */   public ProfileNotFoundException(String message, Throwable cause) {
/* 12 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public ProfileNotFoundException(Throwable cause) {
/* 16 */     super(cause);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\yggdrasil\ProfileNotFoundException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */