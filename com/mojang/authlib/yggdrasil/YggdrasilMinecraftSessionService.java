/*     */ package com.mojang.authlib.yggdrasil;
/*     */ 
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonParser;
/*     */ import com.mojang.authlib.AuthenticationService;
/*     */ import com.mojang.authlib.GameProfile;
/*     */ import com.mojang.authlib.exceptions.AuthenticationException;
/*     */ import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
/*     */ import com.mojang.authlib.minecraft.BaseMinecraftSessionService;
/*     */ import com.mojang.authlib.minecraft.MinecraftProfileTexture;
/*     */ import com.mojang.authlib.properties.Property;
/*     */ import com.mojang.authlib.properties.PropertyMap;
/*     */ import java.net.InetAddress;
/*     */ import java.util.Base64;
/*     */ import java.util.EnumMap;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import pro.gravit.launcher.events.request.CheckServerRequestEvent;
/*     */ import pro.gravit.launcher.events.request.JoinServerRequestEvent;
/*     */ import pro.gravit.launcher.events.request.ProfileByUUIDRequestEvent;
/*     */ import pro.gravit.launcher.profiles.PlayerProfile;
/*     */ import pro.gravit.launcher.request.auth.CheckServerRequest;
/*     */ import pro.gravit.launcher.request.auth.JoinServerRequest;
/*     */ import pro.gravit.launcher.request.uuid.ProfileByUUIDRequest;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ import pro.gravit.utils.helper.SecurityHelper;
/*     */ 
/*     */ public final class YggdrasilMinecraftSessionService extends BaseMinecraftSessionService {
/*  32 */   public static final JsonParser JSON_PARSER = new JsonParser();
/*  33 */   public static final boolean NO_TEXTURES = Boolean.parseBoolean("launcher.com.mojang.authlib.noTextures");
/*     */   
/*     */   public static void fillTextureProperties(GameProfile profile, PlayerProfile pp) {
/*  36 */     boolean debug = LogHelper.isDebugEnabled();
/*  37 */     if (debug) {
/*  38 */       LogHelper.debug("fillTextureProperties, Username: '%s'", new Object[] { profile.getName() });
/*     */     }
/*  40 */     if (NO_TEXTURES) {
/*     */       return;
/*     */     }
/*     */     
/*  44 */     PropertyMap properties = profile.getProperties();
/*  45 */     if (pp.skin != null) {
/*  46 */       properties.put("skinURL", new Property("skinURL", pp.skin.url, ""));
/*  47 */       properties.put("skinDigest", new Property("skinDigest", SecurityHelper.toHex(pp.skin.digest), ""));
/*  48 */       if (debug) {
/*  49 */         LogHelper.debug("fillTextureProperties, Has skin texture for username '%s'", new Object[] { profile.getName() });
/*     */       }
/*     */     } 
/*  52 */     if (pp.cloak != null) {
/*  53 */       properties.put("cloakURL", new Property("cloakURL", pp.cloak.url, ""));
/*  54 */       properties.put("cloakDigest", new Property("cloakDigest", SecurityHelper.toHex(pp.cloak.digest), ""));
/*  55 */       if (debug) {
/*  56 */         LogHelper.debug("fillTextureProperties, Has cloak texture for username '%s'", new Object[] { profile.getName() });
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void getTexturesMojang(Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures, String texturesBase64, GameProfile profile) {
/*     */     try {
/*     */       JsonObject texturesJSON;
/*     */       try {
/*  66 */         byte[] decoded = Base64.getDecoder().decode(texturesBase64);
/*  67 */         texturesJSON = JSON_PARSER.parse(new String(decoded, IOHelper.UNICODE_CHARSET)).getAsJsonObject().getAsJsonObject("textures");
/*  68 */       } catch (Throwable ignored) {
/*  69 */         LogHelper.error("Could not decode textures payload, Username: '%s', UUID: '%s'", new Object[] { profile.getName(), profile.getUUID() });
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/*  74 */       for (MinecraftProfileTexture.Type type : MinecraftProfileTexture.PROFILE_TEXTURE_TYPES) {
/*  75 */         if (textures.containsKey(type)) {
/*     */           continue;
/*     */         }
/*     */         
/*  79 */         JsonElement textureJSON = texturesJSON.get(type.name());
/*  80 */         if (textureJSON != null && textureJSON.isJsonObject()) {
/*  81 */           JsonElement urlValue = textureJSON.getAsJsonObject().get("url");
/*  82 */           if (urlValue.isJsonPrimitive())
/*  83 */             textures.put(type, new MinecraftProfileTexture(urlValue.getAsString())); 
/*     */         } 
/*     */       } 
/*  86 */     } catch (Throwable e) {
/*  87 */       JsonObject texturesJSON; LogHelper.error("Could not getTexturesMojang");
/*  88 */       LogHelper.error((Throwable)texturesJSON);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static GameProfile toGameProfile(PlayerProfile pp) {
/*  93 */     GameProfile profile = new GameProfile(pp.uuid, pp.username);
/*  94 */     fillTextureProperties(profile, pp);
/*  95 */     return profile;
/*     */   }
/*     */   
/*     */   public YggdrasilMinecraftSessionService(AuthenticationService service) {
/*  99 */     super(service);
/* 100 */     LogHelper.debug("Patched MinecraftSessionService created");
/*     */   }
/*     */ 
/*     */   
/*     */   public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
/*     */     PlayerProfile pp;
/* 106 */     UUID uuid = profile.getUUID();
/* 107 */     boolean debug = LogHelper.isDebugEnabled();
/* 108 */     if (debug) {
/* 109 */       LogHelper.debug("fillProfileProperties, UUID: %s", new Object[] { uuid });
/*     */     }
/* 111 */     if (uuid == null) {
/* 112 */       return profile;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 117 */       pp = ((ProfileByUUIDRequestEvent)(new ProfileByUUIDRequest(uuid)).request()).playerProfile;
/* 118 */     } catch (Exception e) {
/* 119 */       if (debug) {
/* 120 */         LogHelper.debug("Couldn't fetch profile properties for '%s': %s", new Object[] { profile, e });
/*     */       }
/* 122 */       return profile;
/*     */     } 
/*     */ 
/*     */     
/* 126 */     if (pp == null) {
/* 127 */       if (debug) {
/* 128 */         LogHelper.debug("Couldn't fetch profile properties for '%s' as the profile does not exist", new Object[] { profile });
/*     */       }
/* 130 */       return profile;
/*     */     } 
/*     */ 
/*     */     
/* 134 */     if (debug) {
/* 135 */       LogHelper.debug("Successfully fetched profile properties for '%s'", new Object[] { profile });
/*     */     }
/* 137 */     fillTextureProperties(profile, pp);
/* 138 */     return toGameProfile(pp);
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
/* 143 */     if (LogHelper.isDebugEnabled()) {
/* 144 */       LogHelper.debug("getTextures, Username: '%s', UUID: '%s'", new Object[] { profile.getName(), profile.getUUID() });
/*     */     }
/* 146 */     Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = new EnumMap<>(MinecraftProfileTexture.Type.class);
/*     */     
/* 148 */     PropertyMap.Serializer serializer = new PropertyMap.Serializer();
/*     */ 
/*     */     
/* 151 */     if (!NO_TEXTURES) {
/*     */       
/* 153 */       Property skinURL = (Property)Iterables.getFirst(profile.getProperties().get("skinURL"), null);
/* 154 */       Property skinDigest = (Property)Iterables.getFirst(profile.getProperties().get("skinDigest"), null);
/*     */       
/* 156 */       LogHelper.debug("trySkinTextures " + profile.getName() + " " + ((skinURL != null) ? 1 : 0) + " " + ((skinDigest != null) ? 1 : 0));
/* 157 */       LogHelper.debug("jsonProperties " + profile.getName() + " " + serializer.serialize(profile.getProperties(), null, null).toString());
/*     */       
/* 159 */       if (skinURL != null && skinDigest != null) {
/* 160 */         LogHelper.debug("putSkinTextures " + profile.getName() + " " + skinURL.getValue() + " " + skinDigest.getValue());
/*     */         
/* 162 */         textures.put(MinecraftProfileTexture.Type.SKIN, new MinecraftProfileTexture(skinURL.getValue(), skinDigest.getValue()));
/*     */       } 
/*     */ 
/*     */       
/* 166 */       Property cloakURL = (Property)Iterables.getFirst(profile.getProperties().get("cloakURL"), null);
/* 167 */       Property cloakDigest = (Property)Iterables.getFirst(profile.getProperties().get("cloakDigest"), null);
/*     */       
/* 169 */       LogHelper.debug("tryCloakTextures " + profile.getName() + " " + ((skinURL != null) ? 1 : 0) + " " + ((skinDigest != null) ? 1 : 0));
/*     */       
/* 171 */       if (cloakURL != null && cloakDigest != null) {
/* 172 */         LogHelper.debug("putCloakTextures " + profile.getName() + " " + cloakURL.getValue() + " " + cloakDigest.getValue());
/*     */         
/* 174 */         textures.put(MinecraftProfileTexture.Type.CAPE, new MinecraftProfileTexture(cloakURL.getValue(), cloakDigest.getValue()));
/*     */       } 
/*     */ 
/*     */       
/* 178 */       if (textures.size() != MinecraftProfileTexture.PROFILE_TEXTURE_COUNT) {
/* 179 */         Property texturesMojang = (Property)Iterables.getFirst(profile.getProperties().get("textures"), null);
/* 180 */         if (texturesMojang != null) {
/* 181 */           getTexturesMojang(textures, texturesMojang.getValue(), profile);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 186 */     return textures;
/*     */   }
/*     */   
/*     */   public GameProfile hasJoinedServer(GameProfile profile, String serverID) throws AuthenticationUnavailableException {
/*     */     PlayerProfile pp;
/* 191 */     String username = profile.getName();
/* 192 */     if (LogHelper.isDebugEnabled()) {
/* 193 */       LogHelper.debug("checkServer, Username: '%s', Server ID: %s", new Object[] { username, serverID });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 199 */       pp = ((CheckServerRequestEvent)(new CheckServerRequest(username, serverID)).request()).playerProfile;
/* 200 */     } catch (Exception e) {
/* 201 */       LogHelper.error(e);
/* 202 */       throw new AuthenticationUnavailableException(e);
/*     */     } 
/*     */ 
/*     */     
/* 206 */     return (pp == null) ? null : toGameProfile(pp);
/*     */   }
/*     */ 
/*     */   
/*     */   public GameProfile hasJoinedServer(GameProfile profile, String serverID, InetAddress address) throws AuthenticationUnavailableException {
/* 211 */     return hasJoinedServer(profile, serverID);
/*     */   }
/*     */   
/*     */   public YggdrasilAuthenticationService getAuthenticationService() {
/* 215 */     return (YggdrasilAuthenticationService)super.getAuthenticationService();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void joinServer(GameProfile profile, String accessToken, String serverID) throws AuthenticationException {
/*     */     boolean success;
/* 222 */     String username = profile.getName();
/* 223 */     if (LogHelper.isDebugEnabled()) {
/* 224 */       LogHelper.debug("joinServer, Username: '%s', Access token: %s, Server ID: %s", new Object[] { username, accessToken, serverID });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 230 */       success = ((JoinServerRequestEvent)(new JoinServerRequest(username, accessToken, serverID)).request()).allow;
/* 231 */     } catch (Exception e) {
/* 232 */       throw new AuthenticationUnavailableException(e);
/*     */     } 
/*     */ 
/*     */     
/* 236 */     if (!success)
/* 237 */       throw new AuthenticationException("Bad Login (Clientside)"); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\yggdrasil\YggdrasilMinecraftSessionService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */