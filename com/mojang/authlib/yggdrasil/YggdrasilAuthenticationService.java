/*    */ package com.mojang.authlib.yggdrasil;
/*    */ 
/*    */ import com.mojang.authlib.Agent;
/*    */ import com.mojang.authlib.AuthenticationService;
/*    */ import com.mojang.authlib.GameProfileRepository;
/*    */ import com.mojang.authlib.UserAuthentication;
/*    */ import com.mojang.authlib.minecraft.MinecraftSessionService;
/*    */ import java.net.Proxy;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public final class YggdrasilAuthenticationService
/*    */   implements AuthenticationService
/*    */ {
/*    */   public YggdrasilAuthenticationService(Proxy proxy, String clientToken) {
/* 15 */     LogHelper.debug("Patched AuthenticationService created: '%s'", new Object[] { clientToken });
/*    */   }
/*    */ 
/*    */   
/*    */   public MinecraftSessionService createMinecraftSessionService() {
/* 20 */     return (MinecraftSessionService)new YggdrasilMinecraftSessionService(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public GameProfileRepository createProfileRepository() {
/* 25 */     return new YggdrasilGameProfileRepository();
/*    */   }
/*    */ 
/*    */   
/*    */   public UserAuthentication createUserAuthentication(Agent agent) {
/* 30 */     throw new UnsupportedOperationException("createUserAuthentication is used only by Mojang Launcher");
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\yggdrasil\YggdrasilAuthenticationService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */