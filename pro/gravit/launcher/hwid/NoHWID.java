/*    */ package pro.gravit.launcher.hwid;
/*    */ 
/*    */ public class NoHWID
/*    */   implements HWID {
/*    */   public String getSerializeString() {
/*  6 */     return "";
/*    */   }
/*    */ 
/*    */   
/*    */   public int getLevel() {
/* 11 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compare(HWID hwid) {
/* 16 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isNull() {
/* 21 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\hwid\NoHWID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */