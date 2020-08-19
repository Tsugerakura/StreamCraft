/*     */ package pro.gravit.launcher.gui;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import javax.script.Bindings;
/*     */ import javax.script.Invocable;
/*     */ import javax.script.ScriptException;
/*     */ import pro.gravit.launcher.JSApplication;
/*     */ import pro.gravit.launcher.Launcher;
/*     */ import pro.gravit.launcher.LauncherAPI;
/*     */ import pro.gravit.launcher.LauncherConfig;
/*     */ import pro.gravit.launcher.LauncherEngine;
/*     */ import pro.gravit.launcher.NewLauncherSettings;
/*     */ import pro.gravit.launcher.client.ClientLauncher;
/*     */ import pro.gravit.launcher.client.DirBridge;
/*     */ import pro.gravit.launcher.client.UserSettings;
/*     */ import pro.gravit.launcher.client.events.ClientGuiPhase;
/*     */ import pro.gravit.launcher.hasher.FileNameMatcher;
/*     */ import pro.gravit.launcher.hasher.HashedDir;
/*     */ import pro.gravit.launcher.hasher.HashedEntry;
/*     */ import pro.gravit.launcher.hasher.HashedFile;
/*     */ import pro.gravit.launcher.hwid.NoHWID;
/*     */ import pro.gravit.launcher.hwid.OshiHWID;
/*     */ import pro.gravit.launcher.modules.LauncherModule;
/*     */ import pro.gravit.launcher.modules.events.ClosePhase;
/*     */ import pro.gravit.launcher.profiles.ClientProfile;
/*     */ import pro.gravit.launcher.profiles.Texture;
/*     */ import pro.gravit.launcher.profiles.optional.OptionalFile;
/*     */ import pro.gravit.launcher.request.PingRequest;
/*     */ import pro.gravit.launcher.request.Request;
/*     */ import pro.gravit.launcher.request.RequestException;
/*     */ import pro.gravit.launcher.request.RequestType;
/*     */ import pro.gravit.launcher.request.auth.AuthRequest;
/*     */ import pro.gravit.launcher.request.auth.CheckServerRequest;
/*     */ import pro.gravit.launcher.request.auth.GetAvailabilityAuthRequest;
/*     */ import pro.gravit.launcher.request.auth.JoinServerRequest;
/*     */ import pro.gravit.launcher.request.auth.RegisterRequest;
/*     */ import pro.gravit.launcher.request.auth.SetProfileRequest;
/*     */ import pro.gravit.launcher.request.update.LauncherRequest;
/*     */ import pro.gravit.launcher.request.update.ProfilesRequest;
/*     */ import pro.gravit.launcher.request.update.UpdateRequest;
/*     */ import pro.gravit.launcher.request.uuid.BatchProfileByUsernameRequest;
/*     */ import pro.gravit.launcher.request.uuid.ProfileByUUIDRequest;
/*     */ import pro.gravit.launcher.serialize.HOutput;
/*     */ import pro.gravit.launcher.serialize.stream.EnumSerializer;
/*     */ import pro.gravit.launcher.serialize.stream.StreamObject;
/*     */ import pro.gravit.utils.HTTPRequest;
/*     */ import pro.gravit.utils.helper.CommonHelper;
/*     */ import pro.gravit.utils.helper.EnvHelper;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.JVMHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ import pro.gravit.utils.helper.SecurityHelper;
/*     */ import pro.gravit.utils.helper.VerifyHelper;
/*     */ 
/*     */ public class JSRuntimeProvider implements RuntimeProvider {
/*  58 */   public final ScriptEngine engine = CommonHelper.newScriptEngine();
/*     */   private boolean isPreLoaded = false;
/*     */   
/*     */   @LauncherAPI
/*     */   public static void addLauncherClassBindings(Map<String, Object> bindings) {
/*  63 */     bindings.put("LauncherClass", Launcher.class);
/*  64 */     bindings.put("LauncherConfigClass", LauncherConfig.class);
/*  65 */     bindings.put("HTTPRequestClass", HTTPRequest.class);
/*  66 */     bindings.put("SettingsManagerClass", SettingsManager.class);
/*  67 */     bindings.put("NewLauncherSettingsClass", NewLauncherSettings.class);
/*     */ 
/*     */     
/*  70 */     bindings.put("PlayerProfileClass", PlayerProfile.class);
/*  71 */     bindings.put("PlayerProfileTextureClass", Texture.class);
/*  72 */     bindings.put("ClientProfileClass", ClientProfile.class);
/*  73 */     bindings.put("ClientProfileVersionClass", ClientProfile.Version.class);
/*  74 */     bindings.put("ClientLauncherClass", ClientLauncher.class);
/*  75 */     bindings.put("ClientLauncherParamsClass", ClientLauncher.Params.class);
/*  76 */     bindings.put("ServerPingerClass", ServerPinger.class);
/*     */ 
/*     */     
/*  79 */     bindings.put("RequestClass", Request.class);
/*  80 */     bindings.put("RequestTypeClass", RequestType.class);
/*  81 */     bindings.put("RequestExceptionClass", RequestException.class);
/*  82 */     bindings.put("PingRequestClass", PingRequest.class);
/*  83 */     bindings.put("AuthRequestClass", AuthRequest.class);
/*  84 */     bindings.put("RegisterRequestClass", RegisterRequest.class);
/*  85 */     bindings.put("JoinServerRequestClass", JoinServerRequest.class);
/*  86 */     bindings.put("CheckServerRequestClass", CheckServerRequest.class);
/*  87 */     bindings.put("UpdateRequestClass", UpdateRequest.class);
/*  88 */     bindings.put("LauncherRequestClass", LauncherRequest.class);
/*  89 */     bindings.put("SetProfileRequestClass", SetProfileRequest.class);
/*  90 */     bindings.put("ProfilesRequestClass", ProfilesRequest.class);
/*  91 */     bindings.put("ProfileByUsernameRequestClass", ProfileByUsernameRequest.class);
/*  92 */     bindings.put("ProfileByUUIDRequestClass", ProfileByUUIDRequest.class);
/*  93 */     bindings.put("BatchProfileByUsernameRequestClass", BatchProfileByUsernameRequest.class);
/*  94 */     bindings.put("GetAvailabilityAuthRequestClass", GetAvailabilityAuthRequest.class);
/*     */ 
/*     */     
/*  97 */     bindings.put("FileNameMatcherClass", FileNameMatcher.class);
/*  98 */     bindings.put("HashedDirClass", HashedDir.class);
/*  99 */     bindings.put("HashedFileClass", HashedFile.class);
/* 100 */     bindings.put("HashedEntryTypeClass", HashedEntry.Type.class);
/*     */ 
/*     */     
/* 103 */     bindings.put("HInputClass", HInput.class);
/* 104 */     bindings.put("HOutputClass", HOutput.class);
/* 105 */     bindings.put("StreamObjectClass", StreamObject.class);
/* 106 */     bindings.put("StreamObjectAdapterClass", StreamObject.Adapter.class);
/* 107 */     bindings.put("EnumSerializerClass", EnumSerializer.class);
/* 108 */     bindings.put("OptionalFileClass", OptionalFile.class);
/* 109 */     bindings.put("UserSettingsClass", UserSettings.class);
/*     */ 
/*     */     
/* 112 */     bindings.put("CommonHelperClass", CommonHelper.class);
/* 113 */     bindings.put("IOHelperClass", IOHelper.class);
/* 114 */     bindings.put("EnvHelperClass", EnvHelper.class);
/* 115 */     bindings.put("JVMHelperClass", JVMHelper.class);
/* 116 */     bindings.put("JVMHelperOSClass", JVMHelper.OS.class);
/* 117 */     bindings.put("LogHelperClass", LogHelper.class);
/* 118 */     bindings.put("LogHelperOutputClass", LogHelper.Output.class);
/* 119 */     bindings.put("SecurityHelperClass", SecurityHelper.class);
/* 120 */     bindings.put("DigestAlgorithmClass", SecurityHelper.DigestAlgorithm.class);
/* 121 */     bindings.put("VerifyHelperClass", VerifyHelper.class);
/* 122 */     bindings.put("DirBridgeClass", DirBridge.class);
/* 123 */     bindings.put("FunctionalBridgeClass", FunctionalBridge.class);
/*     */     
/* 125 */     bindings.put("NoHWIDClass", NoHWID.class);
/* 126 */     bindings.put("OshiHWIDClass", OshiHWID.class);
/*     */ 
/*     */     
/*     */     try {
/* 130 */       Class.forName("javafx.application.Application");
/* 131 */       bindings.put("JSApplicationClass", JSApplication.class);
/* 132 */     } catch (ClassNotFoundException ignored) {
/* 133 */       LogHelper.warning("JavaFX API isn't available");
/*     */     } 
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public Object loadScript(String path) throws IOException, ScriptException {
/* 139 */     URL url = Launcher.getResourceURL(path);
/* 140 */     LogHelper.debug("Loading script: '%s'", new Object[] { url });
/* 141 */     try (BufferedReader reader = IOHelper.newReader(url)) {
/* 142 */       return this.engine.eval(reader, this.engine.getBindings(100));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void setScriptBindings() {
/* 147 */     LogHelper.info("Setting up script engine bindings");
/* 148 */     Bindings bindings = this.engine.getBindings(100);
/* 149 */     bindings.put("launcher", this);
/*     */ 
/*     */     
/* 152 */     addLauncherClassBindings(bindings);
/*     */   }
/*     */ 
/*     */   
/*     */   public void run(String[] args) throws ScriptException, NoSuchMethodException, IOException {
/* 157 */     preLoad();
/* 158 */     loadScript("init.js");
/* 159 */     LogHelper.info("Invoking start() function");
/* 160 */     LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)new ClientGuiPhase(this));
/* 161 */     ((Invocable)this.engine).invokeFunction("start", new Object[] { args });
/* 162 */     LauncherEngine.modulesManager.invokeEvent((LauncherModule.Event)new ClosePhase());
/*     */   }
/*     */ 
/*     */   
/*     */   public void preLoad() throws IOException, ScriptException {
/* 167 */     if (!this.isPreLoaded) {
/* 168 */       loadScript("engine/api.js");
/* 169 */       loadScript("config.js");
/* 170 */       this.isPreLoaded = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void init(boolean clientInstance) {
/* 176 */     setScriptBindings();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\gui\JSRuntimeProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */