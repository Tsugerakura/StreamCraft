/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RegisterRequestEvent
/*    */   extends RequestEvent
/*    */ {
/*    */   @LauncherNetworkAPI
/*    */   public byte[] captcha;
/*    */   
/*    */   public String getType() {
/* 15 */     return "register";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\RegisterRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */