/*    */ package pro.gravit.launcher.console;
/*    */ 
/*    */ import pro.gravit.launcher.managers.SettingsManager;
/*    */ import pro.gravit.utils.command.Command;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class FeatureCommand
/*    */   extends Command {
/*    */   public String getArgsDescription() {
/* 10 */     return "[feature] [true/false]";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getUsageDescription() {
/* 15 */     return "Enable or disable feature";
/*    */   }
/*    */ 
/*    */   
/*    */   public void invoke(String... args) throws Exception {
/* 20 */     verifyArgs(args, 2);
/* 21 */     boolean enabled = Boolean.valueOf(args[1]).booleanValue();
/* 22 */     switch (args[0]) {
/*    */       case "store":
/* 24 */         SettingsManager.settings.featureStore = enabled;
/*    */         break;
/*    */       
/*    */       default:
/* 28 */         LogHelper.info("Features: [store]");
/*    */         return;
/*    */     } 
/*    */     
/* 32 */     LogHelper.info("Feature %s %s", new Object[] { args[0], enabled ? "enabled" : "disabled" });
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\console\FeatureCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */