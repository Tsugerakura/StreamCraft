/*   */ package cpw.mods.fml;
/*   */ 
/*   */ import pro.gravit.utils.helper.JVMHelper;
/*   */ 
/*   */ 
/*   */ public class SafeExitJVMLegacy
/*   */ {
/*   */   public static void exit(int code) {
/* 9 */     JVMHelper.RUNTIME.halt(code);
/*   */   }
/*   */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\cpw\mods\fml\SafeExitJVMLegacy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */