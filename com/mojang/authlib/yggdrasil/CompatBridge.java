/*    */ package com.mojang.authlib.yggdrasil;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.events.request.BatchProfileByUsernameRequestEvent;
/*    */ import pro.gravit.launcher.events.request.CheckServerRequestEvent;
/*    */ import pro.gravit.launcher.events.request.JoinServerRequestEvent;
/*    */ import pro.gravit.launcher.events.request.ProfileByUUIDRequestEvent;
/*    */ import pro.gravit.launcher.events.request.ProfileByUsernameRequestEvent;
/*    */ import pro.gravit.launcher.profiles.PlayerProfile;
/*    */ import pro.gravit.launcher.request.auth.CheckServerRequest;
/*    */ import pro.gravit.launcher.request.auth.JoinServerRequest;
/*    */ import pro.gravit.launcher.request.uuid.BatchProfileByUsernameRequest;
/*    */ import pro.gravit.launcher.request.uuid.ProfileByUUIDRequest;
/*    */ import pro.gravit.launcher.request.uuid.ProfileByUsernameRequest;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ @LauncherAPI
/*    */ public final class CompatBridge {
/*    */   public static CompatProfile checkServer(String username, String serverID) throws Exception {
/* 21 */     LogHelper.debug("CompatBridge.checkServer, Username: '%s', Server ID: %s", new Object[] { username, serverID });
/* 22 */     return CompatProfile.fromPlayerProfile(((CheckServerRequestEvent)(new CheckServerRequest(username, serverID)).request()).playerProfile);
/*    */   }
/*    */   
/*    */   public static final int PROFILES_MAX_BATCH_SIZE = 128;
/*    */   
/*    */   public static boolean joinServer(String username, String accessToken, String serverID) throws Exception {
/* 28 */     LogHelper.debug("LegacyBridge.joinServer, Username: '%s', Access token: %s, Server ID: %s", new Object[] { username, accessToken, serverID });
/* 29 */     return ((JoinServerRequestEvent)(new JoinServerRequest(username, accessToken, serverID)).request()).allow;
/*    */   }
/*    */   
/*    */   public static CompatProfile profileByUsername(String username) throws Exception {
/* 33 */     return CompatProfile.fromPlayerProfile(((ProfileByUsernameRequestEvent)(new ProfileByUsernameRequest(username)).request()).playerProfile);
/*    */   }
/*    */   
/*    */   public static CompatProfile profileByUUID(UUID uuid) throws Exception {
/* 37 */     return CompatProfile.fromPlayerProfile(((ProfileByUUIDRequestEvent)(new ProfileByUUIDRequest(uuid)).request()).playerProfile);
/*    */   }
/*    */   
/*    */   public static CompatProfile[] profilesByUsername(String... usernames) throws Exception {
/* 41 */     PlayerProfile[] profiles = ((BatchProfileByUsernameRequestEvent)(new BatchProfileByUsernameRequest(usernames)).request()).playerProfiles;
/*    */ 
/*    */     
/* 44 */     CompatProfile[] resultProfiles = new CompatProfile[profiles.length];
/* 45 */     for (int i = 0; i < profiles.length; i++) {
/* 46 */       resultProfiles[i] = CompatProfile.fromPlayerProfile(profiles[i]);
/*    */     }
/*    */     
/* 49 */     return resultProfiles;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\yggdrasil\CompatBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */