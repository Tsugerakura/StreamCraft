/*    */ package pro.gravit.launcher.hwid;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ 
/*    */ public class SecurityHWID
/*    */   implements HWID {
/*    */   @LauncherAPI
/*    */   public static String data;
/*    */   
/*    */   public String getSerializeString() {
/* 11 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getLevel() {
/* 16 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compare(HWID hwid) {
/* 21 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isNull() {
/* 26 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\hwid\SecurityHWID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */