/*    */ package pro.gravit.launcher.request.uuid;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.events.request.BatchProfileByUsernameRequestEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.VerifyHelper;
/*    */ 
/*    */ public final class BatchProfileByUsernameRequest
/*    */   extends Request<BatchProfileByUsernameRequestEvent>
/*    */   implements WebSocketRequest {
/*    */   @LauncherNetworkAPI
/*    */   private final Entry[] list;
/*    */   
/*    */   class Entry {
/*    */     @LauncherNetworkAPI
/*    */     String username;
/*    */     @LauncherNetworkAPI
/*    */     String client;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public BatchProfileByUsernameRequest(String... usernames) throws IOException {
/* 27 */     this.list = new Entry[usernames.length];
/* 28 */     for (int i = 0; i < usernames.length; i++) {
/* 29 */       (this.list[i]).client = "";
/* 30 */       (this.list[i]).username = usernames[i];
/*    */     } 
/* 32 */     IOHelper.verifyLength(usernames.length, 128);
/* 33 */     for (String username : usernames) {
/* 34 */       VerifyHelper.verifyUsername(username);
/*    */     }
/*    */   }
/*    */   
/*    */   public String getType() {
/* 39 */     return "batchProfileByUsername";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\reques\\uuid\BatchProfileByUsernameRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */