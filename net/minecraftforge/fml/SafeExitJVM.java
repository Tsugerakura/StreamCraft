/*   */ package net.minecraftforge.fml;
/*   */ 
/*   */ import pro.gravit.utils.helper.JVMHelper;
/*   */ 
/*   */ 
/*   */ public class SafeExitJVM
/*   */ {
/*   */   public static void exit(int code) {
/* 9 */     JVMHelper.RUNTIME.halt(code);
/*   */   }
/*   */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\minecraftforge\fml\SafeExitJVM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */