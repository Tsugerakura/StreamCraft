/*    */ package pro.gravit.launcher.events.request;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ 
/*    */ 
/*    */ public class LauncherRequestEvent
/*    */   extends RequestEvent
/*    */ {
/* 11 */   private static final UUID uuid = UUID.fromString("d54cc12a-4f59-4f23-9b10-f527fdd2e38f");
/*    */ 
/*    */   
/*    */   @LauncherNetworkAPI
/*    */   public String url;
/*    */   
/*    */   @LauncherNetworkAPI
/*    */   public byte[] digest;
/*    */ 
/*    */   
/*    */   public LauncherRequestEvent(boolean needUpdate, String url) {
/* 22 */     this.needUpdate = needUpdate;
/* 23 */     this.url = url; } @LauncherNetworkAPI
/*    */   public byte[] binary; @LauncherNetworkAPI
/*    */   public byte[] key; @LauncherNetworkAPI
/*    */   public boolean needUpdate; public LauncherRequestEvent(boolean needUpdate, String url, byte[] key) {
/* 27 */     this.needUpdate = needUpdate;
/* 28 */     this.url = url;
/* 29 */     this.key = key;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LauncherRequestEvent(boolean b, byte[] digest) {
/* 36 */     this.needUpdate = b;
/* 37 */     this.digest = digest;
/*    */   }
/*    */   
/*    */   public LauncherRequestEvent(byte[] binary, byte[] digest) {
/* 41 */     this.binary = binary;
/* 42 */     this.digest = digest;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 47 */     return "launcher";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\request\LauncherRequestEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */