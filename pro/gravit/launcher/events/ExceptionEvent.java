/*    */ package pro.gravit.launcher.events;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ 
/*    */ public class ExceptionEvent extends RequestEvent {
/*    */   public ExceptionEvent(Exception e) {
/*  7 */     this.message = e.getMessage();
/*  8 */     this.clazz = e.getClass().getName();
/*    */   }
/*    */   
/*    */   @LauncherNetworkAPI
/*    */   public final String message;
/*    */   @LauncherNetworkAPI
/*    */   public final String clazz;
/*    */   
/*    */   public String getType() {
/* 17 */     return "exception";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\ExceptionEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */