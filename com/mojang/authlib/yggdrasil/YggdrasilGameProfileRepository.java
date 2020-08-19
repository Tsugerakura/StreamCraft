/*    */ package com.mojang.authlib.yggdrasil;
/*    */ 
/*    */ import com.mojang.authlib.Agent;
/*    */ import com.mojang.authlib.GameProfile;
/*    */ import com.mojang.authlib.GameProfileRepository;
/*    */ import com.mojang.authlib.ProfileLookupCallback;
/*    */ import java.util.Arrays;
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.events.request.BatchProfileByUsernameRequestEvent;
/*    */ import pro.gravit.launcher.profiles.PlayerProfile;
/*    */ import pro.gravit.launcher.request.uuid.BatchProfileByUsernameRequest;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ import pro.gravit.utils.helper.VerifyHelper;
/*    */ 
/*    */ public final class YggdrasilGameProfileRepository
/*    */   implements GameProfileRepository
/*    */ {
/* 18 */   private static final long BUSY_WAIT_MS = VerifyHelper.verifyLong(
/* 19 */       Long.parseLong(System.getProperty("launcher.com.mojang.authlib.busyWait", Long.toString(100L))), VerifyHelper.L_NOT_NEGATIVE, "launcher.com.mojang.authlib.busyWait can't be < 0");
/*    */   
/* 21 */   private static final long ERROR_BUSY_WAIT_MS = VerifyHelper.verifyLong(
/* 22 */       Long.parseLong(System.getProperty("launcher.com.mojang.authlib.errorBusyWait", Long.toString(500L))), VerifyHelper.L_NOT_NEGATIVE, "launcher.com.mojang.authlib.errorBusyWait can't be < 0");
/*    */ 
/*    */   
/*    */   private static void busyWait(long ms) {
/*    */     try {
/* 27 */       Thread.sleep(ms);
/* 28 */     } catch (InterruptedException e) {
/* 29 */       LogHelper.error(e);
/*    */     } 
/*    */   }
/*    */   
/*    */   public YggdrasilGameProfileRepository() {
/* 34 */     LogHelper.debug("Patched GameProfileRepository created");
/*    */   }
/*    */ 
/*    */   
/*    */   public void findProfilesByNames(String[] usernames, Agent agent, ProfileLookupCallback callback) {
/* 39 */     int offset = 0;
/* 40 */     while (offset < usernames.length) {
/* 41 */       PlayerProfile[] sliceProfiles; String[] sliceUsernames = Arrays.<String>copyOfRange(usernames, offset, Math.min(offset + 128, usernames.length));
/* 42 */       offset += 128;
/*    */ 
/*    */ 
/*    */       
/*    */       try {
/* 47 */         sliceProfiles = ((BatchProfileByUsernameRequestEvent)(new BatchProfileByUsernameRequest(sliceUsernames)).request()).playerProfiles;
/* 48 */       } catch (Exception e) {
/* 49 */         boolean bool = LogHelper.isDebugEnabled();
/* 50 */         for (String username : sliceUsernames) {
/* 51 */           if (bool) {
/* 52 */             LogHelper.debug("Couldn't find profile '%s': %s", new Object[] { username, e });
/*    */           }
/* 54 */           callback.onProfileLookupFailed(new GameProfile((UUID)null, username), e);
/*    */         } 
/*    */ 
/*    */         
/* 58 */         busyWait(ERROR_BUSY_WAIT_MS);
/*    */         
/*    */         continue;
/*    */       } 
/*    */       
/* 63 */       int len = sliceProfiles.length;
/* 64 */       boolean debug = (len > 0 && LogHelper.isDebugEnabled());
/* 65 */       for (int i = 0; i < len; i++) {
/* 66 */         PlayerProfile pp = sliceProfiles[i];
/* 67 */         if (pp == null) {
/* 68 */           String username = sliceUsernames[i];
/* 69 */           if (debug) {
/* 70 */             LogHelper.debug("Couldn't find profile '%s'", new Object[] { username });
/*    */           }
/* 72 */           callback.onProfileLookupFailed(new GameProfile((UUID)null, username), new ProfileNotFoundException("Server did not find the requested profile"));
/*    */         
/*    */         }
/*    */         else {
/*    */           
/* 77 */           if (debug) {
/* 78 */             LogHelper.debug("Successfully looked up profile '%s'", new Object[] { pp.username });
/*    */           }
/* 80 */           callback.onProfileLookupSucceeded(YggdrasilMinecraftSessionService.toGameProfile(pp));
/*    */         } 
/*    */       } 
/*    */       
/* 84 */       busyWait(BUSY_WAIT_MS);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\yggdrasil\YggdrasilGameProfileRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */