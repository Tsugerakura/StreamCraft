/*    */ package pro.gravit.launcher.request.auth;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.RestoreSessionRequestEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ 
/*    */ public class RestoreSessionRequest extends Request<RestoreSessionRequestEvent> implements WebSocketRequest {
/*    */   @LauncherNetworkAPI
/*    */   public long session;
/*    */   
/*    */   public RestoreSessionRequest(long session) {
/* 13 */     this.session = session;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 18 */     return "restoreSession";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\auth\RestoreSessionRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */