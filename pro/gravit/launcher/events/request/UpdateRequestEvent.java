/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ import pro.gravit.launcher.hasher.HashedDir;
/*    */ 
/*    */ public class UpdateRequestEvent
/*    */   extends RequestEvent {
/*    */   @LauncherNetworkAPI
/*    */   public HashedDir hdir;
/*    */   @LauncherNetworkAPI
/*    */   public String url;
/*    */   @LauncherNetworkAPI
/*    */   public boolean zip;
/*    */   @LauncherNetworkAPI
/*    */   public boolean fullDownload;
/*    */   
/*    */   public String getType() {
/* 19 */     return "update";
/*    */   }
/*    */   
/*    */   public UpdateRequestEvent(HashedDir hdir) {
/* 23 */     this.hdir = hdir;
/* 24 */     this.zip = false;
/*    */   }
/*    */   
/*    */   public UpdateRequestEvent(HashedDir hdir, String url) {
/* 28 */     this.hdir = hdir;
/* 29 */     this.url = url;
/* 30 */     this.zip = false;
/*    */   }
/*    */   
/*    */   public UpdateRequestEvent(HashedDir hdir, String url, boolean zip) {
/* 34 */     this.hdir = hdir;
/* 35 */     this.url = url;
/* 36 */     this.zip = zip;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\UpdateRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */