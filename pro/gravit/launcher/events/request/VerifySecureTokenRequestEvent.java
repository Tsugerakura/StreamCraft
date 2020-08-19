/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ 
/*    */ public class VerifySecureTokenRequestEvent
/*    */   extends RequestEvent {
/*    */   @LauncherAPI
/*    */   public boolean success;
/*    */   
/*    */   public String getType() {
/* 12 */     return "verifySecureToken";
/*    */   }
/*    */   
/*    */   public VerifySecureTokenRequestEvent(boolean success) {
/* 16 */     this.success = success;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\VerifySecureTokenRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */