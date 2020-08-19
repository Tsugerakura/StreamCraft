/*    */ package pro.gravit.launcher.request.auth;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.ParamsRequestEvent;
/*    */ import pro.gravit.launcher.profiles.ClientProfile;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ 
/*    */ public class ParamsRequest
/*    */   extends Request<ParamsRequestEvent>
/*    */   implements WebSocketRequest {
/*    */   @LauncherNetworkAPI
/*    */   private final ClientProfile profile;
/*    */   @LauncherNetworkAPI
/*    */   private final byte[] data;
/*    */   
/*    */   public ParamsRequest(byte[] data, ClientProfile profile) {
/* 18 */     this.data = data;
/* 19 */     this.profile = profile;
/*    */   }
/*    */   
/*    */   public ParamsRequest() {
/* 23 */     this.profile = null;
/* 24 */     this.data = null;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 29 */     return "params";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\auth\ParamsRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */