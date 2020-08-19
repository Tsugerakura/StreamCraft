/*     */ package pro.gravit.launcher.request.websockets;
/*     */ import com.google.gson.GsonBuilder;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.net.URI;
/*     */ import java.util.HashSet;
/*     */ import javax.net.ssl.SSLException;
/*     */ import pro.gravit.launcher.Launcher;
/*     */ import pro.gravit.launcher.events.request.BatchProfileByUsernameRequestEvent;
/*     */ import pro.gravit.launcher.events.request.ErrorRequestEvent;
/*     */ import pro.gravit.launcher.events.request.ExecCommandRequestEvent;
/*     */ import pro.gravit.launcher.events.request.LauncherRequestEvent;
/*     */ import pro.gravit.launcher.events.request.LogEvent;
/*     */ import pro.gravit.launcher.events.request.RegisterRequestEvent;
/*     */ import pro.gravit.launcher.events.request.RestoreSessionRequestEvent;
/*     */ import pro.gravit.launcher.events.request.SetPasswordRequestEvent;
/*     */ import pro.gravit.launcher.events.request.SetProfileRequestEvent;
/*     */ import pro.gravit.launcher.events.request.VerifySecureTokenRequestEvent;
/*     */ import pro.gravit.launcher.hasher.HashedEntryAdapter;
/*     */ import pro.gravit.launcher.request.WebSocketEvent;
/*     */ import pro.gravit.utils.ProviderMap;
/*     */ import pro.gravit.utils.UniversalJsonAdapter;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ 
/*     */ public class ClientWebSocketService extends ClientJSONPoint {
/*     */   public final Gson gson;
/*     */   public OnCloseCallback onCloseCallback;
/*  28 */   public static ProviderMap<WebSocketEvent> results = new ProviderMap(); public final Boolean onConnect; public ReconnectCallback reconnectCallback;
/*  29 */   public static ProviderMap<WebSocketRequest> requests = new ProviderMap();
/*     */   private HashSet<EventHandler> handlers;
/*     */   
/*     */   public ClientWebSocketService(String address) throws SSLException {
/*  33 */     super(createURL(address));
/*  34 */     this.handlers = new HashSet<>();
/*  35 */     this.gson = Launcher.gsonManager.gson;
/*  36 */     this.onConnect = Boolean.valueOf(true);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void appendTypeAdapters(GsonBuilder builder) {
/*  41 */     builder.registerTypeAdapter(HashedEntry.class, new HashedEntryAdapter());
/*  42 */     builder.registerTypeAdapter(WebSocketEvent.class, new UniversalJsonAdapter(results));
/*  43 */     builder.registerTypeAdapter(WebSocketRequest.class, new UniversalJsonAdapter(requests));
/*     */   }
/*     */   
/*     */   private static URI createURL(String address) {
/*     */     try {
/*  48 */       URI u = new URI(address);
/*  49 */       return u;
/*  50 */     } catch (Throwable e) {
/*  51 */       LogHelper.error(e);
/*  52 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void onMessage(String message) {
/*  58 */     WebSocketEvent result = (WebSocketEvent)this.gson.fromJson(message, WebSocketEvent.class);
/*  59 */     for (EventHandler handler : this.handlers) {
/*  60 */       handler.process(result);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   void onDisconnect() {
/*  66 */     LogHelper.info("WebSocket client disconnect");
/*  67 */     if (this.onCloseCallback != null) this.onCloseCallback.onClose(0, "unsupported param", !this.isClosed);
/*     */   
/*     */   }
/*     */   
/*     */   void onOpen() throws Exception {
/*  72 */     synchronized (this.onConnect) {
/*  73 */       this.onConnect.notifyAll();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerRequests() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void registerResults() {
/*  91 */     results.register("auth", AuthRequestEvent.class);
/*  92 */     results.register("checkServer", CheckServerRequestEvent.class);
/*  93 */     results.register("joinServer", JoinServerRequestEvent.class);
/*  94 */     results.register("launcher", LauncherRequestEvent.class);
/*  95 */     results.register("profileByUsername", ProfileByUsernameRequestEvent.class);
/*  96 */     results.register("profileByUUID", ProfileByUUIDRequestEvent.class);
/*  97 */     results.register("batchProfileByUsername", BatchProfileByUsernameRequestEvent.class);
/*  98 */     results.register("profiles", ProfilesRequestEvent.class);
/*  99 */     results.register("setProfile", SetProfileRequestEvent.class);
/* 100 */     results.register("updateList", UpdateListRequestEvent.class);
/* 101 */     results.register("error", ErrorRequestEvent.class);
/* 102 */     results.register("update", UpdateRequestEvent.class);
/* 103 */     results.register("restoreSession", RestoreSessionRequestEvent.class);
/* 104 */     results.register("getSecureToken", GetSecureTokenRequestEvent.class);
/* 105 */     results.register("verifySecureToken", VerifySecureTokenRequestEvent.class);
/* 106 */     results.register("log", LogEvent.class);
/* 107 */     results.register("cmdExec", ExecCommandRequestEvent.class);
/* 108 */     results.register("getAvailabilityAuth", GetAvailabilityAuthRequestEvent.class);
/* 109 */     results.register("exception", ExceptionEvent.class);
/* 110 */     results.register("register", RegisterRequestEvent.class);
/* 111 */     results.register("setpassword", SetPasswordRequestEvent.class);
/* 112 */     results.register("params", ParamsRequestEvent.class);
/* 113 */     results.register("jvm", JVMRequestEvent.class);
/*     */   }
/*     */   
/*     */   public void registerHandler(EventHandler eventHandler) {
/* 117 */     this.handlers.add(eventHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void waitIfNotConnected() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void sendObject(Object obj) throws IOException {
/* 136 */     waitIfNotConnected();
/* 137 */     if (this.ch == null || !this.ch.isActive()) this.reconnectCallback.onReconnect();
/*     */ 
/*     */     
/* 140 */     send(this.gson.toJson(obj, WebSocketRequest.class));
/*     */   }
/*     */   
/*     */   public void sendObject(Object obj, Type type) throws IOException {
/* 144 */     waitIfNotConnected();
/* 145 */     if (this.ch == null || !this.ch.isActive()) this.reconnectCallback.onReconnect();
/*     */ 
/*     */     
/* 148 */     send(this.gson.toJson(obj, type));
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface EventHandler {
/*     */     void process(WebSocketEvent param1WebSocketEvent);
/*     */   }
/*     */   
/*     */   public static interface ReconnectCallback {
/*     */     void onReconnect() throws IOException;
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface OnCloseCallback {
/*     */     void onClose(int param1Int, String param1String, boolean param1Boolean);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\websockets\ClientWebSocketService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */