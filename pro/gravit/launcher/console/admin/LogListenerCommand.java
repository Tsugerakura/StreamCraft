/*    */ package pro.gravit.launcher.console.admin;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.LogEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.WebSocketEvent;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ import pro.gravit.utils.command.Command;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class LogListenerCommand
/*    */   extends Command {
/*    */   public class LogListenerRequest
/*    */     implements WebSocketRequest {
/*    */     public LogListenerRequest(LogHelper.OutputTypes outputType) {
/* 16 */       this.outputType = outputType;
/*    */     }
/*    */     @LauncherNetworkAPI
/*    */     public LogHelper.OutputTypes outputType;
/*    */     public String getType() {
/* 21 */       return "addLogListener";
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String getArgsDescription() {
/* 27 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsageDescription() {
/* 32 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void invoke(String... args) throws Exception {
/* 37 */     LogHelper.info("Send log listener request");
/* 38 */     Request.service.sendObject(new LogListenerRequest(LogHelper.JANSI ? LogHelper.OutputTypes.JANSI : LogHelper.OutputTypes.PLAIN));
/* 39 */     LogHelper.info("Add log handler");
/* 40 */     Request.service.registerHandler(result -> {
/*    */           if (result instanceof LogEvent)
/*    */             LogHelper.rawLog((), (), ()); 
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\console\admin\LogListenerCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */