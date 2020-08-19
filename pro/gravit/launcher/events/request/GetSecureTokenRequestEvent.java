/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ 
/*    */ public class GetSecureTokenRequestEvent
/*    */   extends RequestEvent {
/*    */   @LauncherNetworkAPI
/*    */   public String secureToken;
/*    */   
/*    */   public String getType() {
/* 12 */     return "GetSecureToken";
/*    */   }
/*    */   
/*    */   public GetSecureTokenRequestEvent(String secureToken) {
/* 16 */     this.secureToken = secureToken;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\GetSecureTokenRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */