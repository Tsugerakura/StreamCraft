/*     */ package pro.gravit.launcher;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.security.interfaces.RSAPublicKey;
/*     */ import java.security.spec.InvalidKeySpecException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import pro.gravit.launcher.serialize.HInput;
/*     */ import pro.gravit.launcher.serialize.HOutput;
/*     */ import pro.gravit.launcher.serialize.stream.StreamObject;
/*     */ import pro.gravit.utils.helper.SecurityHelper;
/*     */ import pro.gravit.utils.helper.VerifyHelper;
/*     */ 
/*     */ public final class LauncherConfig
/*     */   extends StreamObject {
/*  19 */   private static final AutogenConfig config = new AutogenConfig();
/*     */   public String address;
/*     */   
/*     */   public static AutogenConfig getAutogenConfig() {
/*  23 */     return config;
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public final String projectname;
/*     */   
/*     */   public final int clientPort;
/*     */   
/*     */   public String secretKeyClient;
/*     */   public String oemUnlockKey;
/*     */   @LauncherAPI
/*     */   public final RSAPublicKey publicKey;
/*     */   @LauncherAPI
/*     */   public final Map<String, byte[]> runtime;
/*     */   public final boolean isWarningMissArchJava;
/*     */   public boolean isNettyEnabled;
/*     */   public LauncherEnvironment environment;
/*     */   public final String guardLicenseName;
/*     */   public final String guardLicenseKey;
/*     */   public final String guardLicenseEncryptKey;
/*     */   public final String guardType;
/*     */   
/*     */   @LauncherAPI
/*     */   public LauncherConfig(HInput input) throws IOException, InvalidKeySpecException {
/*     */     LauncherEnvironment env;
/*  49 */     this.publicKey = SecurityHelper.toPublicRSAKey(input.readByteArray(2048));
/*  50 */     this.projectname = config.projectname;
/*  51 */     this.clientPort = config.clientPort;
/*  52 */     this.secretKeyClient = config.secretKeyClient;
/*  53 */     this.oemUnlockKey = config.oemUnlockKey;
/*     */     
/*  55 */     this.isWarningMissArchJava = config.isWarningMissArchJava;
/*  56 */     this.guardLicenseEncryptKey = config.guardLicenseEncryptKey;
/*  57 */     this.guardLicenseKey = config.guardLicenseKey;
/*  58 */     this.guardType = config.guardType;
/*  59 */     this.guardLicenseName = config.guardLicenseName;
/*  60 */     this.address = config.address;
/*     */     
/*  62 */     if (config.env == 0) { env = LauncherEnvironment.DEV; }
/*  63 */     else if (config.env == 1) { env = LauncherEnvironment.DEBUG; }
/*  64 */     else if (config.env == 2) { env = LauncherEnvironment.STD; }
/*  65 */     else if (config.env == 3) { env = LauncherEnvironment.PROD; }
/*  66 */     else { env = LauncherEnvironment.STD; }
/*  67 */      Launcher.applyLauncherEnv(env);
/*  68 */     this.environment = env;
/*     */     
/*  70 */     int count = input.readLength(0);
/*  71 */     Map<String, byte[]> localResources = (Map)new HashMap<>(count);
/*  72 */     for (int i = 0; i < count; i++) {
/*  73 */       String name = input.readString(255);
/*  74 */       VerifyHelper.putIfAbsent(localResources, name, input
/*  75 */           .readByteArray(2048), 
/*  76 */           String.format("Duplicate runtime resource: '%s'", new Object[] { name }));
/*     */     } 
/*  78 */     this.runtime = Collections.unmodifiableMap((Map)localResources);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public LauncherConfig(String address, RSAPublicKey publicKey, Map<String, byte[]> runtime, String projectname) {
/*  83 */     this.address = address;
/*  84 */     this.publicKey = Objects.<RSAPublicKey>requireNonNull(publicKey, "publicKey");
/*  85 */     this.runtime = Collections.unmodifiableMap(new HashMap<>((Map)runtime));
/*  86 */     this.projectname = projectname;
/*  87 */     this.clientPort = 32148;
/*  88 */     this.guardLicenseName = "FREE";
/*  89 */     this.guardLicenseKey = "AAAA-BBBB-CCCC-DDDD";
/*  90 */     this.guardLicenseEncryptKey = "12345";
/*  91 */     this.guardType = "no";
/*  92 */     this.isWarningMissArchJava = true;
/*  93 */     this.isNettyEnabled = false;
/*  94 */     this.environment = LauncherEnvironment.STD;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public LauncherConfig(String address, RSAPublicKey publicKey, Map<String, byte[]> runtime) {
/*  99 */     this.address = address;
/* 100 */     this.publicKey = Objects.<RSAPublicKey>requireNonNull(publicKey, "publicKey");
/* 101 */     this.runtime = Collections.unmodifiableMap(new HashMap<>((Map)runtime));
/* 102 */     this.projectname = "Minecraft";
/* 103 */     this.guardLicenseName = "FREE";
/* 104 */     this.guardLicenseKey = "AAAA-BBBB-CCCC-DDDD";
/* 105 */     this.guardLicenseEncryptKey = "12345";
/* 106 */     this.clientPort = 32148;
/* 107 */     this.guardType = "no";
/* 108 */     this.isWarningMissArchJava = true;
/* 109 */     this.isNettyEnabled = false;
/* 110 */     this.environment = LauncherEnvironment.STD;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(HOutput output) throws IOException {
/* 115 */     output.writeByteArray(this.publicKey.getEncoded(), 2048);
/*     */ 
/*     */     
/* 118 */     Set<Map.Entry<String, byte[]>> entrySet = (Set)this.runtime.entrySet();
/* 119 */     output.writeLength(entrySet.size(), 0);
/* 120 */     for (Map.Entry<String, byte[]> entry : this.runtime.entrySet()) {
/* 121 */       output.writeString(entry.getKey(), 255);
/* 122 */       output.writeByteArray(entry.getValue(), 2048);
/*     */     } 
/*     */   }
/*     */   
/*     */   public enum LauncherEnvironment {
/* 127 */     DEV, DEBUG, STD, PROD;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\LauncherConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */