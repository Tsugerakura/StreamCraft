/*    */ package pro.gravit.launcher.profiles;
/*    */ import java.io.IOException;
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.serialize.HInput;
/*    */ import pro.gravit.launcher.serialize.HOutput;
/*    */ import pro.gravit.utils.helper.VerifyHelper;
/*    */ 
/*    */ public final class PlayerProfile extends StreamObject {
/*    */   @LauncherAPI
/*    */   public final UUID uuid;
/*    */   @LauncherAPI
/*    */   public final String username;
/*    */   
/*    */   @LauncherAPI
/*    */   public static PlayerProfile newOfflineProfile(String username) {
/* 17 */     return new PlayerProfile(offlineUUID(username), username, null, null);
/*    */   } @LauncherAPI
/*    */   public final Texture skin; @LauncherAPI
/*    */   public final Texture cloak; @LauncherAPI
/*    */   public static UUID offlineUUID(String username) {
/* 22 */     return UUID.nameUUIDFromBytes(IOHelper.encodeASCII("OfflinePlayer:" + username));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @LauncherAPI
/*    */   public PlayerProfile(HInput input) throws IOException {
/* 36 */     this.uuid = input.readUUID();
/* 37 */     this.username = VerifyHelper.verifyUsername(input.readString(64));
/* 38 */     this.skin = input.readBoolean() ? new Texture(input) : null;
/* 39 */     this.cloak = input.readBoolean() ? new Texture(input) : null;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public PlayerProfile(UUID uuid, String username, Texture skin, Texture cloak) {
/* 44 */     this.uuid = Objects.<UUID>requireNonNull(uuid, "uuid");
/* 45 */     this.username = VerifyHelper.verifyUsername(username);
/* 46 */     this.skin = skin;
/* 47 */     this.cloak = cloak;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(HOutput output) throws IOException {
/* 52 */     output.writeUUID(this.uuid);
/* 53 */     output.writeString(this.username, 64);
/*    */ 
/*    */     
/* 56 */     output.writeBoolean((this.skin != null));
/* 57 */     if (this.skin != null)
/* 58 */       this.skin.write(output); 
/* 59 */     output.writeBoolean((this.cloak != null));
/* 60 */     if (this.cloak != null)
/* 61 */       this.cloak.write(output); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\profiles\PlayerProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */