/*    */ package pro.gravit.launcher.request.update;
/*    */ 
/*    */ import pro.gravit.launcher.events.request.ProfilesRequestEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ 
/*    */ public final class ProfilesRequest
/*    */   extends Request<ProfilesRequestEvent>
/*    */   implements WebSocketRequest {
/*    */   public String getType() {
/* 11 */     return "profiles";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\reques\\update\ProfilesRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */