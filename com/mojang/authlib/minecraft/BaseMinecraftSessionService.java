/*    */ package com.mojang.authlib.minecraft;
/*    */ 
/*    */ import com.mojang.authlib.AuthenticationService;
/*    */ 
/*    */ public abstract class BaseMinecraftSessionService implements MinecraftSessionService {
/*    */   private final AuthenticationService authenticationService;
/*    */   
/*    */   protected BaseMinecraftSessionService(AuthenticationService authenticationService) {
/*  9 */     this.authenticationService = authenticationService;
/*    */   }
/*    */   
/*    */   public AuthenticationService getAuthenticationService() {
/* 13 */     return this.authenticationService;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\minecraft\BaseMinecraftSessionService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */