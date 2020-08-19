/*    */ package pro.gravit.launcher.request.update;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Path;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import pro.gravit.launcher.Launcher;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.LauncherNetworkAPI;
/*    */ import pro.gravit.launcher.downloader.ListDownloader;
/*    */ import pro.gravit.launcher.events.request.LauncherRequestEvent;
/*    */ import pro.gravit.launcher.request.Request;
/*    */ import pro.gravit.launcher.request.WebSocketEvent;
/*    */ import pro.gravit.launcher.request.websockets.StandartClientWebSocketService;
/*    */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.JVMHelper;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ import pro.gravit.utils.helper.SecurityHelper;
/*    */ 
/*    */ public final class LauncherRequest
/*    */   extends Request<LauncherRequestEvent> implements WebSocketRequest {
/*    */   @LauncherNetworkAPI
/* 24 */   public int launcher_type = EXE_BINARY ? 2 : 1; @LauncherNetworkAPI
/*    */   public byte[] digest;
/*    */   @LauncherAPI
/* 27 */   public static final Path BINARY_PATH = IOHelper.getCodeSource(Launcher.class);
/*    */   
/*    */   @LauncherAPI
/* 30 */   public static final boolean EXE_BINARY = IOHelper.hasExtension(BINARY_PATH, "exe");
/*    */   
/*    */   @LauncherAPI
/*    */   public static void update(LauncherRequestEvent result) throws IOException {
/* 34 */     List<String> args = new ArrayList<>(8);
/* 35 */     args.add(IOHelper.resolveJavaBin(null).toString());
/* 36 */     if (LogHelper.isDebugEnabled())
/* 37 */       args.add(JVMHelper.jvmProperty("launcher.debug", Boolean.toString(LogHelper.isDebugEnabled()))); 
/* 38 */     args.add("-jar");
/* 39 */     args.add(BINARY_PATH.toString());
/* 40 */     ProcessBuilder builder = new ProcessBuilder(args.<String>toArray(new String[0]));
/* 41 */     builder.inheritIO();
/*    */ 
/*    */     
/* 44 */     if (result.binary != null) {
/* 45 */       IOHelper.write(BINARY_PATH, result.binary);
/*    */     } else {
/*    */ 
/*    */       
/*    */       try {
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 54 */         ListDownloader downloader = new ListDownloader();
/* 55 */         downloader.downloadOne(result.url, BINARY_PATH);
/* 56 */       } catch (Throwable e) {
/* 57 */         LogHelper.error(e);
/*    */       } 
/*    */     } 
/* 60 */     builder.start();
/*    */ 
/*    */     
/* 63 */     JVMHelper.RUNTIME.exit(255);
/* 64 */     throw new AssertionError("Why Launcher wasn't restarted?!");
/*    */   }
/*    */ 
/*    */   
/*    */   public LauncherRequestEvent requestDo(StandartClientWebSocketService service) throws Exception {
/* 69 */     LauncherRequestEvent result = (LauncherRequestEvent)service.sendRequest(this);
/* 70 */     if (result.needUpdate) update(result); 
/* 71 */     return result;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public LauncherRequest() {
/* 76 */     Path launcherPath = IOHelper.getCodeSource(LauncherRequest.class);
/*    */     try {
/* 78 */       this.digest = SecurityHelper.digest(SecurityHelper.DigestAlgorithm.SHA512, launcherPath);
/* 79 */     } catch (IOException e) {
/* 80 */       LogHelper.error(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String getType() {
/* 86 */     return "launcher";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\reques\\update\LauncherRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */