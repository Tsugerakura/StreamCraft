/*    */ package pro.gravit.launcher.request.auth;
/*    */ 
/*    */ import pro.gravit.launcher.events.request.GetAvailabilityAuthRequestEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ 
/*    */ public class GetAvailabilityAuthRequest
/*    */   extends Request<GetAvailabilityAuthRequestEvent>
/*    */   implements WebSocketRequest {
/*    */   public String getType() {
/* 11 */     return "getAvailabilityAuth";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\auth\GetAvailabilityAuthRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */