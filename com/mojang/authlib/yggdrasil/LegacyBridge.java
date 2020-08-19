/*    */ package com.mojang.authlib.yggdrasil;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.events.request.JoinServerRequestEvent;
/*    */ import pro.gravit.launcher.request.auth.CheckServerRequest;
/*    */ import pro.gravit.launcher.request.auth.JoinServerRequest;
/*    */ import pro.gravit.utils.helper.CommonHelper;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ @LauncherAPI
/*    */ public final class LegacyBridge {
/*    */   public static boolean checkServer(String username, String serverID) throws Exception {
/* 14 */     LogHelper.debug("LegacyBridge.checkServer, Username: '%s', Server ID: %s", new Object[] { username, serverID }); return 
/* 15 */       ((new CheckServerRequest(username, serverID)).request() != null);
/*    */   }
/*    */   
/*    */   public static String getCloakURL(String username) {
/* 19 */     LogHelper.debug("LegacyBridge.getCloakURL: '%s'", new Object[] { username });
/* 20 */     return CommonHelper.replace(System.getProperty("launcher.legacy.cloaksURL", "http://skins.minecraft.net/MinecraftCloaks/%username%.png"), new String[] { "username", 
/* 21 */           IOHelper.urlEncode(username) });
/*    */   }
/*    */   
/*    */   public static String getSkinURL(String username) {
/* 25 */     LogHelper.debug("LegacyBridge.getSkinURL: '%s'", new Object[] { username });
/* 26 */     return CommonHelper.replace(System.getProperty("launcher.legacy.skinsURL", "http://skins.minecraft.net/MinecraftSkins/%username%.png"), new String[] { "username", 
/* 27 */           IOHelper.urlEncode(username) });
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static String joinServer(String username, String accessToken, String serverID) {
/* 33 */     LogHelper.debug("LegacyBridge.joinServer, Username: '%s', Access token: %s, Server ID: %s", new Object[] { username, accessToken, serverID });
/*    */     try {
/* 35 */       return ((JoinServerRequestEvent)(new JoinServerRequest(username, accessToken, serverID)).request()).allow ? "OK" : "Bad Login (Clientside)";
/* 36 */     } catch (Exception e) {
/* 37 */       return e.toString();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\yggdrasil\LegacyBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */