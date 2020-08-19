/*    */ package com.mojang.authlib.minecraft;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.EnumSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public final class MinecraftProfileTexture {
/*    */   public enum Type {
/*  9 */     SKIN,
/* 10 */     CAPE,
/* 11 */     ELYTRA;
/*    */   }
/*    */   
/* 14 */   public static final Set<Type> PROFILE_TEXTURE_TYPES = Collections.unmodifiableSet(EnumSet.allOf(Type.class));
/*    */   
/* 16 */   public static final int PROFILE_TEXTURE_COUNT = PROFILE_TEXTURE_TYPES.size();
/*    */   
/*    */   private static String baseName(String url) {
/* 19 */     String name = url.substring(url.lastIndexOf('/') + 1);
/*    */ 
/*    */     
/* 22 */     int extensionIndex = name.lastIndexOf('.');
/* 23 */     if (extensionIndex >= 0) {
/* 24 */       name = name.substring(0, extensionIndex);
/*    */     }
/*    */     
/* 27 */     return name;
/*    */   }
/*    */ 
/*    */   
/*    */   private final String url;
/*    */   
/*    */   private final String hash;
/*    */   
/*    */   public MinecraftProfileTexture(String url) {
/* 36 */     this(url, baseName(url));
/*    */   }
/*    */   
/*    */   public MinecraftProfileTexture(String url, String hash) {
/* 40 */     this.url = url;
/* 41 */     this.hash = hash;
/*    */   }
/*    */   
/*    */   public String getHash() {
/* 45 */     return this.hash;
/*    */   }
/*    */   
/*    */   public String getMetadata(String key) {
/* 49 */     return null;
/*    */   }
/*    */   
/*    */   public String getUrl() {
/* 53 */     return this.url;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 58 */     return String.format("MinecraftProfileTexture{url='%s',hash=%s}", new Object[] { this.url, this.hash });
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\minecraft\MinecraftProfileTexture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */