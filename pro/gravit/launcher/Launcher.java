/*     */ package pro.gravit.launcher;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.nio.file.NoSuchFileException;
/*     */ import java.util.Arrays;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.regex.Pattern;
/*     */ import javafx.scene.text.Font;
/*     */ import pro.gravit.launcher.managers.GsonManager;
/*     */ import pro.gravit.launcher.profiles.ClientProfile;
/*     */ import pro.gravit.launcher.serialize.HInput;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ import pro.gravit.utils.helper.SecurityHelper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Launcher
/*     */ {
/*     */   @LauncherAPI
/*     */   public static final String SKIN_URL_PROPERTY = "skinURL";
/*     */   @LauncherAPI
/*     */   public static final String SKIN_DIGEST_PROPERTY = "skinDigest";
/*     */   @LauncherAPI
/*     */   public static final String CLOAK_URL_PROPERTY = "cloakURL";
/*     */   @LauncherAPI
/*     */   public static final String CLOAK_DIGEST_PROPERTY = "cloakDigest";
/*  33 */   public static final AtomicBoolean LAUNCHED = new AtomicBoolean(false);
/*     */   
/*  35 */   private static final AtomicReference<LauncherConfig> CONFIG = new AtomicReference<>();
/*     */   
/*     */   @LauncherAPI
/*     */   public static final int PROTOCOL_MAGIC_LEGACY = 1917264920;
/*     */   
/*     */   @LauncherAPI
/*     */   public static final int PROTOCOL_MAGIC = -1576685468;
/*     */   
/*     */   @LauncherAPI
/*     */   public static final String RUNTIME_DIR = "runtime";
/*     */   @LauncherAPI
/*     */   public static final String GUARD_DIR = "guard";
/*     */   @LauncherAPI
/*     */   public static final String CONFIG_FILE = "config.bin";
/*     */   @LauncherAPI
/*     */   public static ClientProfile profile;
/*     */   @LauncherAPI
/*     */   public static final String INIT_SCRIPT_FILE = "init.js";
/*     */   @LauncherAPI
/*     */   public static final String API_SCRIPT_FILE = "engine/api.js";
/*     */   public static final String CONFIG_SCRIPT_FILE = "config.js";
/*  56 */   private static final Pattern UUID_PATTERN = Pattern.compile("-", 16);
/*     */   public static GsonManager gsonManager;
/*     */   
/*     */   @LauncherAPI
/*     */   public static LauncherConfig getConfig() {
/*  61 */     LauncherConfig config = CONFIG.get();
/*  62 */     if (config == null) {
/*  63 */       try (HInput input = new HInput(IOHelper.newInput(IOHelper.getResourceURL("config.bin")))) {
/*  64 */         config = new LauncherConfig(input);
/*  65 */       } catch (IOException|java.security.spec.InvalidKeySpecException e) {
/*  66 */         throw new SecurityException(e);
/*     */       } 
/*  68 */       CONFIG.set(config);
/*     */     } 
/*  70 */     return config;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static void setConfig(LauncherConfig cfg) {
/*  75 */     CONFIG.set(cfg);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static URL getResourceURL(String name) throws IOException {
/*  80 */     LauncherConfig config = getConfig();
/*  81 */     byte[] validDigest = config.runtime.get(name);
/*  82 */     if (validDigest == null) {
/*  83 */       throw new NoSuchFileException(name);
/*     */     }
/*     */     
/*  86 */     URL url = IOHelper.getResourceURL("runtime/" + name);
/*  87 */     if (!Arrays.equals(validDigest, SecurityHelper.digest(SecurityHelper.DigestAlgorithm.MD5, url))) {
/*  88 */       throw new NoSuchFileException(name);
/*     */     }
/*     */     
/*  91 */     return url;
/*     */   }
/*     */   
/*     */   public static URL getResourceURL(String name, String prefix) throws IOException {
/*  95 */     LauncherConfig config = getConfig();
/*  96 */     byte[] validDigest = config.runtime.get(name);
/*  97 */     if (validDigest == null) {
/*  98 */       throw new NoSuchFileException(name);
/*     */     }
/*     */     
/* 101 */     URL url = IOHelper.getResourceURL(prefix + '/' + name);
/* 102 */     if (!Arrays.equals(validDigest, SecurityHelper.digest(SecurityHelper.DigestAlgorithm.MD5, url))) {
/* 103 */       throw new NoSuchFileException(name);
/*     */     }
/*     */     
/* 106 */     return url;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static String toHash(UUID uuid) {
/* 111 */     return UUID_PATTERN.matcher(uuid.toString()).replaceAll("");
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public static Font loadFont(String name, int size) {
/*     */     try {
/* 118 */       LogHelper.debug("Loading font: " + name);
/*     */       
/* 120 */       Font font = Font.loadFont(IOHelper.newInput(IOHelper.getResourceURL("runtime/dialog/" + name)), size);
/*     */       
/* 122 */       LogHelper.debug("Loaded font: " + font.toString());
/*     */       
/* 124 */       return font;
/*     */     }
/* 126 */     catch (Exception e) {
/*     */       
/* 128 */       LogHelper.error("Failed load font: " + name);
/* 129 */       LogHelper.error(e);
/*     */ 
/*     */       
/* 132 */       return null;
/*     */     } 
/*     */   }
/*     */   public static void applyLauncherEnv(LauncherConfig.LauncherEnvironment env) {
/* 136 */     switch (env) {
/*     */       case DEV:
/* 138 */         LogHelper.setDevEnabled(true);
/* 139 */         LogHelper.setStacktraceEnabled(true);
/* 140 */         LogHelper.setDebugEnabled(true);
/*     */         break;
/*     */       case DEBUG:
/* 143 */         LogHelper.setDebugEnabled(true);
/* 144 */         LogHelper.setStacktraceEnabled(true);
/*     */         break;
/*     */ 
/*     */       
/*     */       case PROD:
/* 149 */         LogHelper.setStacktraceEnabled(false);
/* 150 */         LogHelper.setDebugEnabled(false);
/* 151 */         LogHelper.setDevEnabled(false);
/*     */         break;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\Launcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */