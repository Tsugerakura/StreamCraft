/*    */ package com.mojang.authlib.yggdrasil;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.Launcher;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.profiles.PlayerProfile;
/*    */ import pro.gravit.utils.helper.SecurityHelper;
/*    */ 
/*    */ @LauncherAPI
/*    */ public final class CompatProfile {
/*    */   public static final String SKIN_URL_PROPERTY = "skinURL";
/*    */   public static final String SKIN_DIGEST_PROPERTY = "skinDigest";
/*    */   public static final String CLOAK_URL_PROPERTY = "cloakURL";
/*    */   public static final String CLOAK_DIGEST_PROPERTY = "cloakDigest";
/*    */   public final UUID uuid;
/*    */   
/*    */   public static CompatProfile fromPlayerProfile(PlayerProfile profile) {
/* 18 */     return (profile == null) ? null : new CompatProfile(profile.uuid, profile.username, (profile.skin == null) ? null : profile.skin.url, (profile.skin == null) ? null : 
/*    */         
/* 20 */         SecurityHelper.toHex(profile.skin.digest), (profile.cloak == null) ? null : profile.cloak.url, (profile.cloak == null) ? null : 
/*    */         
/* 22 */         SecurityHelper.toHex(profile.cloak.digest));
/*    */   }
/*    */ 
/*    */   
/*    */   public final String uuidHash;
/*    */   public final String username;
/*    */   public final String skinURL;
/*    */   public final String skinDigest;
/*    */   public final String cloakURL;
/*    */   public final String cloakDigest;
/*    */   
/*    */   public CompatProfile(UUID uuid, String username, String skinURL, String skinDigest, String cloakURL, String cloakDigest) {
/* 34 */     this.uuid = uuid;
/* 35 */     this.uuidHash = Launcher.toHash(uuid);
/* 36 */     this.username = username;
/* 37 */     this.skinURL = skinURL;
/* 38 */     this.skinDigest = skinDigest;
/* 39 */     this.cloakURL = cloakURL;
/* 40 */     this.cloakDigest = cloakDigest;
/*    */   }
/*    */   
/*    */   public int countProperties() {
/* 44 */     int count = 0;
/* 45 */     if (this.skinURL != null)
/* 46 */       count++; 
/* 47 */     if (this.skinDigest != null)
/* 48 */       count++; 
/* 49 */     if (this.cloakURL != null)
/* 50 */       count++; 
/* 51 */     if (this.cloakDigest != null)
/* 52 */       count++; 
/* 53 */     return count;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\yggdrasil\CompatProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */