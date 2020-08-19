/*    */ package pro.gravit.launcher.request.uuid;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.ProfileByUUIDRequestEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ 
/*    */ public final class ProfileByUUIDRequest
/*    */   extends Request<ProfileByUUIDRequestEvent> implements WebSocketRequest {
/*    */   @LauncherNetworkAPI
/*    */   private final UUID uuid;
/*    */   
/*    */   @LauncherAPI
/*    */   public ProfileByUUIDRequest(UUID uuid) {
/* 18 */     this.uuid = Objects.<UUID>requireNonNull(uuid, "uuid");
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 23 */     return "profileByUUID";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\reques\\uuid\ProfileByUUIDRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */