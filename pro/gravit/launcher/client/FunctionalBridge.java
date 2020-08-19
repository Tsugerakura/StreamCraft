/*     */ package pro.gravit.launcher.client;
/*     */ 
/*     */ import com.sun.management.OperatingSystemMXBean;
/*     */ import io.sentry.Sentry;
/*     */ import io.sentry.event.BreadcrumbBuilder;
/*     */ import io.sentry.event.UserBuilder;
/*     */ import java.nio.file.Path;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import pro.gravit.launcher.LauncherAPI;
/*     */ import pro.gravit.launcher.events.request.AuthRequestEvent;
/*     */ import pro.gravit.launcher.guard.LauncherGuardManager;
/*     */ import pro.gravit.launcher.hasher.FileNameMatcher;
/*     */ import pro.gravit.launcher.hasher.HashedDir;
/*     */ import pro.gravit.launcher.hwid.HWID;
/*     */ import pro.gravit.launcher.managers.ConsoleManager;
/*     */ import pro.gravit.launcher.managers.HasherManager;
/*     */ import pro.gravit.launcher.managers.HasherStore;
/*     */ import pro.gravit.launcher.request.Request;
/*     */ import pro.gravit.utils.helper.JVMHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ import wtf.nano.paradox.NativeUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FunctionalBridge
/*     */ {
/*     */   @LauncherAPI
/*  34 */   public static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(0);
/*     */   @LauncherAPI
/*  36 */   public static AtomicReference<HWID> hwid = new AtomicReference<>();
/*     */   @LauncherAPI
/*  38 */   public static Thread getHWID = null;
/*     */   
/*  40 */   private static long cachedMemorySize = -1L;
/*     */   
/*     */   @LauncherAPI
/*     */   public static void processJVMPacket(byte[] packet) {
/*     */     try {
/*  45 */       LogHelper.debug("processJVMPacket " + packet.length);
/*     */       
/*  47 */       Sentry.getContext().recordBreadcrumb((new BreadcrumbBuilder())
/*  48 */           .setMessage("processJVMPacket").build());
/*     */ 
/*     */       
/*  51 */       NativeUtils.handleNative(packet);
/*  52 */     } catch (Exception e) {
/*  53 */       LogHelper.error(e);
/*  54 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static HashedDirRunnable offlineUpdateRequest(String dirName, Path dir, HashedDir hdir, FileNameMatcher matcher, boolean digest) {
/*  60 */     return () -> {
/*     */         if (hdir == null) {
/*     */           Request.requestError(String.format("Директории '%s' нет в кэше", new Object[] { dirName }));
/*     */         }
/*     */         ClientLauncher.verifyHDir(dir, hdir, matcher, digest);
/*     */         return hdir;
/*     */       };
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static void startTask(Runnable task) {
/*  71 */     threadPool.execute(task);
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public static long getTotalMemory() {
/*  77 */     return ((OperatingSystemMXBean)JVMHelper.OPERATING_SYSTEM_MXBEAN).getTotalPhysicalMemorySize() / 1024L / 1024L;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static int getClientJVMBits() {
/*  82 */     return LauncherGuardManager.guard.getClientJVMBits();
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static long getJVMTotalMemory() {
/*  87 */     if (getClientJVMBits() == 32) {
/*  88 */       return Math.min(getTotalMemory(), 1536L);
/*     */     }
/*  90 */     return getTotalMemory();
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public static HasherStore getDefaultHasherStore() {
/*  96 */     return HasherManager.getDefaultStore();
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static void registerUserSettings(String typename, Class<? extends UserSettings> clazz) {
/* 101 */     UserSettings.providers.register(typename, clazz);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static void close() throws Exception {
/* 106 */     threadPool.awaitTermination(2L, TimeUnit.SECONDS);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static void setAuthParams(AuthRequestEvent event) {
/* 111 */     if (event.session != 0L) {
/* 112 */       Request.setSession(event.session);
/*     */     }
/* 114 */     if (event.playerProfile != null) {
/* 115 */       Sentry.getContext().setUser((new UserBuilder()).setId(event.playerProfile.uuid.toString()).setUsername(event.playerProfile.username).build());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public static void evalCommand(String cmd) {
/* 125 */     ConsoleManager.handler.eval(cmd, false);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static void addPlainOutput(LogHelper.Output output) {
/* 130 */     LogHelper.addOutput(output, LogHelper.OutputTypes.PLAIN);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static String getLauncherVersion() {
/* 135 */     return String.format("GravitLauncher v%d.%d.%d build %d", new Object[] {
/* 136 */           Integer.valueOf(5), 
/* 137 */           Integer.valueOf(0), 
/* 138 */           Integer.valueOf(8), 
/* 139 */           Integer.valueOf(1)
/*     */         });
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface HashedDirRunnable {
/*     */     HashedDir run() throws Exception;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\client\FunctionalBridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */