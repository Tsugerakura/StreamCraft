/*    */ package pro.gravit.launcher.managers;
/*    */ 
/*    */ import com.google.gson.GsonBuilder;
/*    */ import pro.gravit.launcher.client.ClientModuleManager;
/*    */ import pro.gravit.launcher.client.UserSettings;
/*    */ import pro.gravit.launcher.hwid.HWID;
/*    */ import pro.gravit.launcher.hwid.HWIDProvider;
/*    */ import pro.gravit.launcher.request.websockets.ClientWebSocketService;
/*    */ import pro.gravit.utils.UniversalJsonAdapter;
/*    */ 
/*    */ public class ClientGsonManager
/*    */   extends GsonManager
/*    */ {
/*    */   private final ClientModuleManager moduleManager;
/*    */   
/*    */   public ClientGsonManager(ClientModuleManager moduleManager) {
/* 17 */     this.moduleManager = moduleManager;
/*    */   }
/*    */ 
/*    */   
/*    */   public void registerAdapters(GsonBuilder builder) {
/* 22 */     super.registerAdapters(builder);
/* 23 */     builder.registerTypeAdapter(UserSettings.class, new UniversalJsonAdapter(UserSettings.providers));
/* 24 */     builder.registerTypeAdapter(HWID.class, new UniversalJsonAdapter(HWIDProvider.hwids));
/* 25 */     ClientWebSocketService.appendTypeAdapters(builder);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\managers\ClientGsonManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */