/*    */ package org.slf4j.event;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum Level
/*    */ {
/* 16 */   ERROR(40, "ERROR"), WARN(30, "WARN"), INFO(20, "INFO"), DEBUG(10, "DEBUG"), TRACE(0, "TRACE");
/*    */   
/*    */   private String levelStr;
/*    */   private int levelInt;
/*    */   
/*    */   Level(int i, String s) {
/* 22 */     this.levelInt = i;
/* 23 */     this.levelStr = s;
/*    */   }
/*    */   
/*    */   public int toInt() {
/* 27 */     return this.levelInt;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 34 */     return this.levelStr;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\slf4j\event\Level.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */