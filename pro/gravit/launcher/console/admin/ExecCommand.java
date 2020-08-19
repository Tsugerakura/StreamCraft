/*    */ package pro.gravit.launcher.console.admin;
/*    */ 
/*    */ import pro.gravit.launcher.events.request.ExecCommandRequestEvent;
/*    */ import pro.gravit.launcher.request.admin.ExecCommandRequest;
/*    */ import pro.gravit.utils.command.Command;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class ExecCommand
/*    */   extends Command {
/*    */   public String getArgsDescription() {
/* 11 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsageDescription() {
/* 16 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void invoke(String... args) throws Exception {
/* 21 */     ExecCommandRequestEvent request = (ExecCommandRequestEvent)(new ExecCommandRequest(String.join(" ", (CharSequence[])args))).request();
/* 22 */     if (!request.success) LogHelper.error("Error executing command"); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\console\admin\ExecCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */