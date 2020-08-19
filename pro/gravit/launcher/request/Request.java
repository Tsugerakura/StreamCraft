/*    */ package pro.gravit.launcher.request;
/*    */ 
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ import pro.gravit.launcher.Launcher;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.request.websockets.StandartClientWebSocketService;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ import pro.gravit.utils.helper.SecurityHelper;
/*    */ 
/*    */ public abstract class Request<R extends WebSocketEvent>
/*    */   implements WebSocketRequest {
/* 14 */   private static long session = SecurityHelper.secureRandom.nextLong();
/*    */   @LauncherNetworkAPI
/* 16 */   public UUID requestUUID = UUID.randomUUID();
/*    */   public static StandartClientWebSocketService service;
/*    */   
/*    */   public static void setSession(long session) {
/* 20 */     Request.session = session;
/*    */   }
/*    */   
/*    */   public static long getSession() {
/* 24 */     return session;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public static void requestError(String message) throws RequestException {
/* 29 */     throw new RequestException(message);
/*    */   }
/*    */   
/* 32 */   private final transient AtomicBoolean started = new AtomicBoolean(false);
/*    */   
/*    */   @LauncherAPI
/*    */   public R request() throws Exception {
/* 36 */     if (!this.started.compareAndSet(false, true))
/* 37 */       throw new IllegalStateException("Request already started"); 
/* 38 */     if (service == null)
/* 39 */       service = StandartClientWebSocketService.initWebSockets((Launcher.getConfig()).address, false); 
/* 40 */     return requestDo(service);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public R request(StandartClientWebSocketService service) throws Exception {
/* 45 */     if (!this.started.compareAndSet(false, true))
/* 46 */       throw new IllegalStateException("Request already started"); 
/* 47 */     return requestDo(service);
/*    */   }
/*    */ 
/*    */   
/*    */   protected R requestDo(StandartClientWebSocketService service) throws Exception {
/* 52 */     return (R)service.sendRequest(this);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\Request.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */