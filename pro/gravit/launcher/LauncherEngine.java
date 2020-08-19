/*     */ package pro.gravit.launcher;
/*     */ 
/*     */ import io.sentry.Sentry;
/*     */ import io.sentry.event.BreadcrumbBuilder;
/*     */ import java.io.IOException;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import pro.gravit.launcher.client.ClientModuleManager;
/*     */ import pro.gravit.launcher.client.DirBridge;
/*     */ import pro.gravit.launcher.client.LauncherUpdateController;
/*     */ import pro.gravit.launcher.client.events.ClientEngineInitPhase;
/*     */ import pro.gravit.launcher.client.events.ClientPreGuiPhase;
/*     */ import pro.gravit.launcher.guard.LauncherGuardManager;
/*     */ import pro.gravit.launcher.gui.JSRuntimeProvider;
/*     */ import pro.gravit.launcher.gui.RuntimeProvider;
/*     */ import pro.gravit.launcher.hwid.HWIDProvider;
/*     */ import pro.gravit.launcher.managers.ClientGsonManager;
/*     */ import pro.gravit.launcher.managers.ClientHookManager;
/*     */ import pro.gravit.launcher.managers.ConsoleManager;
/*     */ import pro.gravit.launcher.managers.GsonManager;
/*     */ import pro.gravit.launcher.modules.LauncherModule;
/*     */ import pro.gravit.launcher.modules.events.PreConfigPhase;
/*     */ import pro.gravit.launcher.request.Request;
/*     */ import pro.gravit.launcher.request.RequestException;
/*     */ import pro.gravit.launcher.request.auth.RestoreSessionRequest;
/*     */ import pro.gravit.launcher.request.update.UpdateRequest;
/*     */ import pro.gravit.launcher.request.websockets.StandartClientWebSocketService;
/*     */ import pro.gravit.utils.Version;
/*     */ import pro.gravit.utils.helper.JVMHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ import wtf.nano.paradox.NativeUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LauncherEngine
/*     */ {
/*     */   public static void setJVMOpts() {
/*  38 */     System.setProperty("java.net.preferIPv4Stack", "true");
/*  39 */     System.setProperty("java.net.preferIPv4Addresses", "true");
/*     */   }
/*     */   
/*     */   public static void main(String... args) throws Throwable {
/*     */     try {
/*  44 */       setJVMOpts();
/*     */       
/*  46 */       Sentry.init("https://e21b06e00b9840dbb0658a716fae21b3@sentry.streamcraft.net/3");
/*     */       
/*  48 */       Sentry.getContext().recordBreadcrumb((new BreadcrumbBuilder())
/*  49 */           .setMessage("Started").build());
/*     */ 
/*     */       
/*  52 */       Sentry.getContext().addTag("os", JVMHelper.OS_TYPE.name());
/*  53 */       Sentry.getContext().addTag("os_version", JVMHelper.OS_VERSION);
/*  54 */       Sentry.getContext().addTag("os_bits", String.valueOf(JVMHelper.OS_BITS));
/*     */       
/*  56 */       Sentry.getContext().addTag("jvm_bits", String.valueOf(JVMHelper.JVM_BITS));
/*     */       
/*  58 */       Sentry.getContext().recordBreadcrumb((new BreadcrumbBuilder())
/*  59 */           .setMessage("Register native consumer").build());
/*     */       
/*  61 */       NativeUtils.registerEventConsumer(new NativeConsumer());
/*     */       
/*  63 */       JVMHelper.checkStackTrace(LauncherEngine.class);
/*  64 */       JVMHelper.verifySystemProperties(Launcher.class, true);
/*     */       
/*  66 */       LogHelper.printVersion("Launcher");
/*  67 */       LogHelper.printLicense("Launcher");
/*     */       
/*  69 */       modulesManager = new ClientModuleManager();
/*  70 */       LauncherConfig.getAutogenConfig().initModules();
/*  71 */       modulesManager.initModules(null);
/*     */       
/*  73 */       initGson(modulesManager);
/*  74 */       ConsoleManager.initConsole();
/*  75 */       HWIDProvider.registerHWIDs();
/*  76 */       modulesManager.invokeEvent((LauncherModule.Event)new PreConfigPhase());
/*  77 */       LauncherConfig config = Launcher.getConfig();
/*     */       
/*  79 */       Sentry.getContext().addTag("version", Version.getVersion().toString());
/*  80 */       Sentry.getContext().addTag("project", config.projectname);
/*  81 */       Sentry.getContext().addTag("address", config.address);
/*  82 */       Sentry.getContext().addTag("environment", config.environment.name());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  87 */       long startTime = System.currentTimeMillis();
/*     */       try {
/*  89 */         Sentry.getContext().recordBreadcrumb((new BreadcrumbBuilder())
/*  90 */             .setMessage("LauncherEngine start").build());
/*     */         
/*  92 */         (new LauncherEngine()).start(args);
/*  93 */       } catch (Exception e) {
/*  94 */         Sentry.capture(e);
/*  95 */         LogHelper.error(e);
/*     */         return;
/*     */       } 
/*  98 */       long endTime = System.currentTimeMillis();
/*  99 */       LogHelper.debug("Launcher started in %dms", new Object[] { Long.valueOf(endTime - startTime) });
/*     */ 
/*     */       
/* 102 */       System.exit(0);
/* 103 */     } catch (Throwable e) {
/* 104 */       Sentry.capture(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void initGson(ClientModuleManager modulesManager) {
/* 109 */     Launcher.gsonManager = (GsonManager)new ClientGsonManager(modulesManager);
/* 110 */     Launcher.gsonManager.initGson();
/*     */   }
/*     */ 
/*     */   
/* 114 */   private final AtomicBoolean started = new AtomicBoolean(false);
/*     */ 
/*     */   
/*     */   public RuntimeProvider runtimeProvider;
/*     */ 
/*     */   
/*     */   public static ClientModuleManager modulesManager;
/*     */ 
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public void start(String... args) throws Throwable {
/* 126 */     Launcher.loadFont("FSElliotPro.otf", 14);
/* 127 */     Launcher.loadFont("FSElliotPro.otf", 15);
/* 128 */     Launcher.loadFont("FSElliotPro.otf", 16);
/* 129 */     Launcher.loadFont("FSElliotPro.otf", 18);
/* 130 */     Launcher.loadFont("FSElliotPro.otf", 44);
/* 131 */     Launcher.loadFont("FSElliotProBold.otf", 14);
/* 132 */     Launcher.loadFont("FSElliotProBold.otf", 15);
/* 133 */     Launcher.loadFont("FSElliotProBold.otf", 16);
/* 134 */     Launcher.loadFont("FSElliotProBold.otf", 18);
/* 135 */     Launcher.loadFont("FSElliotProBold.otf", 44);
/*     */ 
/*     */     
/* 138 */     ClientPreGuiPhase event = new ClientPreGuiPhase(null);
/* 139 */     modulesManager.invokeEvent((LauncherModule.Event)event);
/* 140 */     this.runtimeProvider = event.runtimeProvider;
/* 141 */     if (this.runtimeProvider == null) this.runtimeProvider = (RuntimeProvider)new JSRuntimeProvider(); 
/* 142 */     ClientHookManager.initGuiHook.hook(this.runtimeProvider);
/* 143 */     this.runtimeProvider.init(false);
/*     */     
/* 145 */     if (Request.service == null) {
/* 146 */       String address = (Launcher.getConfig()).address;
/* 147 */       LogHelper.debug("Start async connection to %s", new Object[] { address });
/* 148 */       Request.service = StandartClientWebSocketService.initWebSockets(address, true);
/* 149 */       Request.service.reconnectCallback = (() -> {
/*     */           LogHelper.debug("WebSocket connect closed. Try reconnect");
/*     */           
/*     */           try {
/*     */             Request.service.open();
/*     */             LogHelper.debug("Connect to %s", new Object[] { (Launcher.getConfig()).address });
/* 155 */           } catch (Exception e) {
/*     */             LogHelper.error(e);
/*     */             throw new RequestException(String.format("Connect error: %s", new Object[] { (e.getMessage() != null) ? e.getMessage() : "null" }));
/*     */           } 
/*     */           try {
/*     */             RestoreSessionRequest request1 = new RestoreSessionRequest(Request.getSession());
/*     */             request1.request();
/* 162 */           } catch (Exception e) {
/*     */             LogHelper.error(e);
/*     */           } 
/*     */         });
/*     */     } 
/* 167 */     if (UpdateRequest.getController() == null) UpdateRequest.setController((UpdateRequest.UpdateController)new LauncherUpdateController()); 
/* 168 */     Objects.requireNonNull(args, "args");
/* 169 */     if (this.started.getAndSet(true))
/* 170 */       throw new IllegalStateException("Launcher has been already started"); 
/* 171 */     modulesManager.invokeEvent((LauncherModule.Event)new ClientEngineInitPhase(this));
/* 172 */     this.runtimeProvider.preLoad();
/* 173 */     LauncherGuardManager.initGuard(false);
/* 174 */     LogHelper.debug("Dir: %s", new Object[] { DirBridge.dir });
/* 175 */     this.runtimeProvider.run(args);
/*     */   }
/*     */   
/*     */   public static LauncherEngine clientInstance() {
/* 179 */     return new LauncherEngine();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\LauncherEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */