/*    */ package pro.gravit.launcher.request.auth;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.JVMRequestEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ 
/*    */ public class JVMRequest extends Request<JVMRequestEvent> implements WebSocketRequest {
/*    */   @LauncherNetworkAPI
/*    */   private byte[] packet;
/*    */   
/*    */   public JVMRequest(byte[] packet) {
/* 13 */     this.packet = packet;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 18 */     return "jvm";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\auth\JVMRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */