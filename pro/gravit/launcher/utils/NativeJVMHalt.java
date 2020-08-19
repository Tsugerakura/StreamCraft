/*    */ package pro.gravit.launcher.utils;
/*    */ 
/*    */ import cpw.mods.fml.SafeExitJVMLegacy;
/*    */ import net.minecraftforge.fml.SafeExitJVM;
/*    */ 
/*    */ public final class NativeJVMHalt {
/*    */   public NativeJVMHalt(int haltCode) {
/*  8 */     this.haltCode = haltCode;
/*  9 */     System.out.println("JVM exit code " + haltCode);
/*    */   }
/*    */   
/*    */   public int haltCode;
/*    */   
/*    */   public static void haltA(int code) {
/* 15 */     NativeJVMHalt halt = new NativeJVMHalt(code);
/*    */     try {
/* 17 */       SafeExitJVMLegacy.exit(code);
/* 18 */     } catch (Throwable throwable) {}
/*    */     
/*    */     try {
/* 21 */       SafeExitJVM.exit(code);
/* 22 */     } catch (Throwable throwable) {}
/*    */   }
/*    */ 
/*    */   
/*    */   public static boolean initFunc() {
/* 27 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launche\\utils\NativeJVMHalt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */