/*    */ package pro.gravit.launcher.request.admin;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.events.request.ExecCommandRequestEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ 
/*    */ public class ExecCommandRequest extends Request<ExecCommandRequestEvent> implements WebSocketRequest {
/*    */   @LauncherAPI
/*    */   public String cmd;
/*    */   
/*    */   public ExecCommandRequest(String cmd) {
/* 13 */     this.cmd = cmd;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 18 */     return "cmdExec";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\admin\ExecCommandRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */