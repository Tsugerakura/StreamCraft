/*     */ package pro.gravit.launcher.client;
/*     */ 
/*     */ import io.sentry.Sentry;
/*     */ import io.sentry.event.BreadcrumbBuilder;
/*     */ import io.sentry.event.UserBuilder;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.lang.invoke.MethodHandle;
/*     */ import java.lang.invoke.MethodHandles;
/*     */ import java.lang.invoke.MethodType;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.file.FileVisitResult;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.SimpleFileVisitor;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.nio.file.attribute.PosixFilePermission;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Base64;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.Stream;
/*     */ import javax.swing.JOptionPane;
/*     */ import net.querz.nbt.CompoundTag;
/*     */ import net.querz.nbt.ListTag;
/*     */ import net.querz.nbt.NBTUtil;
/*     */ import net.querz.nbt.Tag;
/*     */ import pro.gravit.launcher.Launcher;
/*     */ import pro.gravit.launcher.LauncherAPI;
/*     */ import pro.gravit.launcher.LauncherEngine;
/*     */ import pro.gravit.launcher.events.request.ParamsRequestEvent;
/*     */ import pro.gravit.launcher.guard.LauncherGuardManager;
/*     */ import pro.gravit.launcher.hasher.FileNameMatcher;
/*     */ import pro.gravit.launcher.hasher.HashedDir;
/*     */ import pro.gravit.launcher.hasher.HashedEntry;
/*     */ import pro.gravit.launcher.managers.ClientGsonManager;
/*     */ import pro.gravit.launcher.managers.ClientHookManager;
/*     */ import pro.gravit.launcher.managers.GarbageManager;
/*     */ import pro.gravit.launcher.managers.GsonManager;
/*     */ import pro.gravit.launcher.profiles.ClientProfile;
/*     */ import pro.gravit.launcher.profiles.PlayerProfile;
/*     */ import pro.gravit.launcher.request.Request;
/*     */ import pro.gravit.launcher.request.RequestException;
/*     */ import pro.gravit.launcher.request.auth.ParamsRequest;
/*     */ import pro.gravit.launcher.request.auth.RestoreSessionRequest;
/*     */ import pro.gravit.launcher.serialize.HInput;
/*     */ import pro.gravit.launcher.serialize.HOutput;
/*     */ import pro.gravit.launcher.serialize.stream.StreamObject;
/*     */ import pro.gravit.launcher.utils.NativeJVMHalt;
/*     */ import pro.gravit.utils.PublicURLClassLoader;
/*     */ import pro.gravit.utils.Version;
/*     */ import pro.gravit.utils.helper.EnvHelper;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.JVMHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ import pro.gravit.utils.helper.SecurityHelper;
/*     */ 
/*     */ public final class ClientLauncher {
/*     */   private static final String MAGICAL_INTEL_OPTION = "-XX:HeapDumpPath=ThisTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump";
/*     */   private static Path JavaBinPath;
/*     */   
/*     */   private static final class ClassPathFileVisitor
/*     */     extends SimpleFileVisitor<Path> {
/*     */     private ClassPathFileVisitor(Stream.Builder<Path> result) {
/*  75 */       this.result = result;
/*     */     }
/*     */     private final Stream.Builder<Path> result;
/*     */     
/*     */     public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/*  80 */       if (IOHelper.hasExtension(file, "jar") || IOHelper.hasExtension(file, "zip"))
/*  81 */         this.result.accept(file); 
/*  82 */       return super.visitFile(file, attrs);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class Params
/*     */     extends StreamObject
/*     */   {
/*     */     @LauncherAPI
/*     */     public final Path assetDir;
/*     */     
/*     */     @LauncherAPI
/*     */     public final Path clientDir;
/*     */     
/*     */     @LauncherAPI
/*     */     public final PlayerProfile pp;
/*     */     @LauncherAPI
/*     */     public final String accessToken;
/*     */     @LauncherAPI
/*     */     public final boolean autoEnter;
/*     */     @LauncherAPI
/*     */     public final boolean fullScreen;
/*     */     @LauncherAPI
/*     */     public final int ram;
/*     */     @LauncherAPI
/*     */     public final int width;
/*     */     @LauncherAPI
/*     */     public final int height;
/*     */     @LauncherAPI
/*     */     public final long session;
/*     */     
/*     */     @LauncherAPI
/*     */     public Params(byte[] launcherDigest, Path assetDir, Path clientDir, PlayerProfile pp, String accessToken, boolean autoEnter, boolean fullScreen, int ram, int width, int height) {
/* 115 */       this.assetDir = assetDir;
/* 116 */       this.clientDir = clientDir;
/*     */       
/* 118 */       this.pp = pp;
/* 119 */       this.accessToken = SecurityHelper.verifyToken(accessToken);
/* 120 */       this.autoEnter = autoEnter;
/* 121 */       this.fullScreen = fullScreen;
/* 122 */       this.ram = ram;
/* 123 */       this.width = width;
/* 124 */       this.height = height;
/* 125 */       this.session = Request.getSession();
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public Params(HInput input) throws Exception {
/* 130 */       this.session = input.readLong();
/*     */       
/* 132 */       this.assetDir = IOHelper.toPath(input.readString(0));
/* 133 */       this.clientDir = IOHelper.toPath(input.readString(0));
/*     */       
/* 135 */       this.pp = new PlayerProfile(input);
/* 136 */       byte[] encryptedAccessToken = input.readByteArray(2048);
/* 137 */       String accessTokenD = new String(SecurityHelper.decrypt((Launcher.getConfig()).secretKeyClient.getBytes(), encryptedAccessToken));
/* 138 */       this.accessToken = SecurityHelper.verifyToken(accessTokenD);
/* 139 */       this.autoEnter = input.readBoolean();
/* 140 */       this.fullScreen = input.readBoolean();
/* 141 */       this.ram = input.readVarInt();
/* 142 */       this.width = input.readVarInt();
/* 143 */       this.height = input.readVarInt();
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(HOutput output) throws IOException {
/* 148 */       output.writeLong(this.session);
/*     */       
/* 150 */       output.writeString(this.assetDir.toString(), 0);
/* 151 */       output.writeString(this.clientDir.toString(), 0);
/* 152 */       this.pp.write(output);
/*     */       try {
/* 154 */         output.writeByteArray(SecurityHelper.encrypt((Launcher.getConfig()).secretKeyClient.getBytes(), this.accessToken.getBytes()), 2048);
/* 155 */       } catch (Exception e) {
/* 156 */         Sentry.capture(e);
/* 157 */         LogHelper.error(e);
/*     */       } 
/* 159 */       output.writeBoolean(this.autoEnter);
/* 160 */       output.writeBoolean(this.fullScreen);
/* 161 */       output.writeVarInt(this.ram);
/* 162 */       output.writeVarInt(this.width);
/* 163 */       output.writeVarInt(this.height);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 171 */   private static final Set<PosixFilePermission> BIN_POSIX_PERMISSIONS = Collections.unmodifiableSet(EnumSet.of(PosixFilePermission.OWNER_READ, new PosixFilePermission[] { PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_EXECUTE }));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 177 */   private static final Path NATIVES_DIR = IOHelper.toPath("natives");
/* 178 */   private static final Path RESOURCEPACKS_DIR = IOHelper.toPath("resourcepacks");
/*     */   private static PublicURLClassLoader classLoader;
/*     */   
/*     */   public static class ClientUserProperties {
/*     */     @LauncherAPI
/*     */     String[] skinURL;
/*     */     @LauncherAPI
/*     */     String[] skinDigest;
/*     */     @LauncherAPI
/*     */     String[] cloakURL;
/*     */     @LauncherAPI
/*     */     String[] cloakDigest;
/*     */   }
/*     */   
/*     */   public static Path getJavaBinPath() {
/* 193 */     return JavaBinPath;
/*     */   }
/*     */   
/*     */   private static void addClientArgs(Collection<String> args, ClientProfile profile, Params params) {
/* 197 */     PlayerProfile pp = params.pp;
/*     */ 
/*     */     
/* 200 */     ClientProfile.Version version = profile.getVersion();
/* 201 */     Collections.addAll(args, new String[] { "--username", pp.username });
/* 202 */     if (version.compareTo((Enum)ClientProfile.Version.MC172) >= 0) {
/* 203 */       Collections.addAll(args, new String[] { "--uuid", Launcher.toHash(pp.uuid) });
/* 204 */       Collections.addAll(args, new String[] { "--accessToken", params.accessToken });
/*     */ 
/*     */       
/* 207 */       if (version.compareTo((Enum)ClientProfile.Version.MC1710) >= 0) {
/*     */         
/* 209 */         Collections.addAll(args, new String[] { "--userType", "mojang" });
/* 210 */         ClientUserProperties properties = new ClientUserProperties();
/* 211 */         if (pp.skin != null) {
/* 212 */           properties.skinURL = new String[] { pp.skin.url };
/* 213 */           properties.skinDigest = new String[] { SecurityHelper.toHex(pp.skin.digest) };
/*     */         } 
/* 215 */         if (pp.cloak != null) {
/* 216 */           properties.cloakURL = new String[] { pp.cloak.url };
/* 217 */           properties.cloakDigest = new String[] { SecurityHelper.toHex(pp.cloak.digest) };
/*     */         } 
/* 219 */         Collections.addAll(args, new String[] { "--userProperties", Launcher.gsonManager.gson.toJson(properties) });
/*     */ 
/*     */         
/* 222 */         Collections.addAll(args, new String[] { "--assetIndex", profile.getAssetIndex() });
/*     */       } 
/*     */     } else {
/* 225 */       Collections.addAll(args, new String[] { "--session", params.accessToken });
/*     */     } 
/*     */     
/* 228 */     Collections.addAll(args, new String[] { "--version", (profile.getVersion()).name });
/* 229 */     Collections.addAll(args, new String[] { "--gameDir", params.clientDir.toString() });
/* 230 */     Collections.addAll(args, new String[] { "--assetsDir", params.assetDir.toString() });
/* 231 */     Collections.addAll(args, new String[] { "--resourcePackDir", params.clientDir.resolve(RESOURCEPACKS_DIR).toString() });
/* 232 */     if (version.compareTo((Enum)ClientProfile.Version.MC194) >= 0) {
/* 233 */       Collections.addAll(args, new String[] { "--versionType", "Launcher v" + Version.getVersion().getVersionString() });
/*     */     }
/*     */     
/* 236 */     if (params.autoEnter);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 241 */     profile.pushOptionalClientArgs(args);
/*     */     
/* 243 */     if (params.fullScreen)
/* 244 */       Collections.addAll(args, new String[] { "--fullscreen", Boolean.toString(true) }); 
/* 245 */     if (params.width > 0 && params.height > 0) {
/* 246 */       Collections.addAll(args, new String[] { "--width", Integer.toString(params.width) });
/* 247 */       Collections.addAll(args, new String[] { "--height", Integer.toString(params.height) });
/*     */     } 
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static void setJavaBinPath(Path javaBinPath) {
/* 253 */     JavaBinPath = javaBinPath;
/*     */   }
/*     */   
/*     */   private static void addClientLegacyArgs(Collection<String> args, ClientProfile profile, Params params) {
/* 257 */     args.add(params.pp.username);
/* 258 */     args.add(params.accessToken);
/*     */ 
/*     */     
/* 261 */     Collections.addAll(args, new String[] { "--version", (profile.getVersion()).name });
/* 262 */     Collections.addAll(args, new String[] { "--gameDir", params.clientDir.toString() });
/* 263 */     Collections.addAll(args, new String[] { "--assetsDir", params.assetDir.toString() });
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static void checkJVMBitsAndVersion() {
/* 268 */     if (JVMHelper.JVM_BITS != JVMHelper.OS_BITS) {
/* 269 */       String error = String.format("У Вас установлена Java %d, но Ваша система определена как %d. Установите Java правильной разрядности", new Object[] { Integer.valueOf(JVMHelper.JVM_BITS), Integer.valueOf(JVMHelper.OS_BITS) });
/* 270 */       LogHelper.error(error);
/* 271 */       if ((Launcher.getConfig()).isWarningMissArchJava)
/* 272 */         JOptionPane.showMessageDialog(null, error); 
/*     */     } 
/* 274 */     String jvmVersion = JVMHelper.RUNTIME_MXBEAN.getVmVersion();
/* 275 */     LogHelper.info(jvmVersion);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static boolean isLaunched() {
/* 280 */     return Launcher.LAUNCHED.get();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void launch(ClientProfile profile, Params params) throws Throwable {
/* 285 */     Collection<String> args = new LinkedList<>();
/* 286 */     if (profile.getVersion().compareTo((Enum)ClientProfile.Version.MC164) >= 0) {
/* 287 */       addClientArgs(args, profile, params);
/*     */     } else {
/* 289 */       addClientLegacyArgs(args, profile, params);
/* 290 */       System.setProperty("minecraft.applet.TargetDirectory", params.clientDir.toString());
/*     */     } 
/* 292 */     Collections.addAll(args, profile.getClientArgs());
/* 293 */     List<String> copy = new ArrayList<>(args);
/* 294 */     for (int i = 0, l = copy.size(); i < l; i++) {
/* 295 */       String s = copy.get(i);
/* 296 */       if (i + 1 < l && ("--accessToken".equals(s) || "--session".equals(s))) {
/* 297 */         copy.set(i + 1, "censored");
/*     */       }
/*     */     } 
/* 300 */     LogHelper.debug("Args: " + copy);
/*     */     
/* 302 */     LogHelper.debug("Perform full GC...");
/*     */     
/* 304 */     JVMHelper.fullGC();
/* 305 */     GarbageManager.gc();
/*     */ 
/*     */     
/* 308 */     Class<?> mainClass = classLoader.loadClass(profile.getMainClass());
/* 309 */     MethodHandle mainMethod = MethodHandles.publicLookup().findStatic(mainClass, "main", MethodType.methodType(void.class, String[].class));
/* 310 */     Launcher.LAUNCHED.set(true);
/*     */     
/* 312 */     mainMethod.invoke(args.toArray(new String[0]));
/*     */   }
/*     */   
/* 315 */   private static Process process = null;
/*     */   
/*     */   private static boolean clientStarted = false;
/*     */   public static PlayerProfile playerProfile;
/*     */   
/*     */   public static void genServersDat(File dir, ClientProfile profile) {
/* 321 */     File serversDat = new File(dir, "servers.dat");
/*     */     
/* 323 */     if (serversDat.exists()) {
/* 324 */       serversDat.delete();
/*     */     }
/* 326 */     CompoundTag ct = new CompoundTag();
/*     */     
/* 328 */     ListTag<CompoundTag> servers = new ListTag(CompoundTag.class);
/*     */     
/* 330 */     int i = 1;
/* 331 */     for (String s : profile.getServers()) {
/* 332 */       CompoundTag serverTag = new CompoundTag();
/*     */       
/* 334 */       serverTag.putString("name", profile.getTitle());
/* 335 */       serverTag.putString("ip", s);
/*     */       
/* 337 */       servers.add((Tag)serverTag);
/*     */       
/* 339 */       i++;
/*     */     } 
/*     */     
/* 342 */     ct.put("servers", (Tag)servers);
/*     */     
/*     */     try {
/* 345 */       NBTUtil.writeTag((Tag)ct, serversDat, false);
/* 346 */     } catch (IOException e) {
/* 347 */       Sentry.capture(e);
/* 348 */       LogHelper.error(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public static Process launch(HashedDir assetHDir, HashedDir clientHDir, ClientProfile profile, Params params, boolean pipeOutput) throws Throwable {
/* 356 */     Sentry.getContext().recordBreadcrumb((new BreadcrumbBuilder())
/* 357 */         .setMessage("Launch client").build());
/*     */ 
/*     */     
/*     */     try {
/* 361 */       Sentry.getContext().setUser((new UserBuilder()).setUsername(params.pp.username).build());
/*     */       
/* 363 */       Sentry.getContext().addTag("version", Version.getVersion().toString());
/* 364 */       Sentry.getContext().addTag("os", JVMHelper.OS_TYPE.name());
/* 365 */       Sentry.getContext().addTag("os_version", JVMHelper.OS_VERSION);
/* 366 */       Sentry.getContext().addTag("os_bits", String.valueOf(JVMHelper.OS_BITS));
/* 367 */       Sentry.getContext().addTag("jvm_bits", String.valueOf(JVMHelper.JVM_BITS));
/* 368 */       Sentry.getContext().addTag("profile", profile.getTitle());
/*     */       
/* 370 */       Sentry.getContext().addExtra("clientDir", params.clientDir.toAbsolutePath().toString());
/* 371 */       Sentry.getContext().addExtra("assetDir", params.assetDir.toAbsolutePath().toString());
/* 372 */       Sentry.getContext().addExtra("ram", Integer.valueOf(params.ram));
/* 373 */       Sentry.getContext().addExtra("accessToken", params.accessToken);
/* 374 */       Sentry.getContext().addExtra("session", Long.valueOf(params.session));
/*     */       
/* 376 */       LogHelper.debug("Writing ClientLauncher params");
/* 377 */       ClientLauncherContext context = new ClientLauncherContext();
/* 378 */       clientStarted = false;
/*     */       
/* 380 */       try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
/* 381 */           HOutput paramsOut = new HOutput(outputStream)) {
/* 382 */         params.write(paramsOut);
/* 383 */         clientHDir.write(paramsOut);
/*     */         
/* 385 */         ParamsRequest paramsRequest = new ParamsRequest(outputStream.toByteArray(), profile);
/* 386 */         paramsRequest.request();
/*     */       } 
/*     */ 
/*     */       
/* 390 */       checkJVMBitsAndVersion();
/* 391 */       LogHelper.debug("Resolving JVM binary");
/* 392 */       Path javaBin = LauncherGuardManager.getGuardJavaBinPath();
/* 393 */       context.javaBin = javaBin;
/* 394 */       context.clientProfile = profile;
/* 395 */       context.playerProfile = params.pp;
/*     */       
/* 397 */       context.args.add(javaBin.toString());
/* 398 */       context.args.add("-XX:HeapDumpPath=ThisTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump");
/*     */       
/* 400 */       if (params.ram > 0 && params.ram <= FunctionalBridge.getJVMTotalMemory()) {
/* 401 */         context.args.add("-Xms" + params.ram + 'M');
/* 402 */         context.args.add("-Xmx" + params.ram + 'M');
/*     */       } 
/*     */       
/* 405 */       context.args.add(JVMHelper.jvmProperty("launcher.debug", Boolean.toString(LogHelper.isDebugEnabled())));
/* 406 */       context.args.add(JVMHelper.jvmProperty("launcher.stacktrace", Boolean.toString(LogHelper.isStacktraceEnabled())));
/* 407 */       context.args.add(JVMHelper.jvmProperty("launcher.dev", Boolean.toString(LogHelper.isDevEnabled())));
/* 408 */       context.args.add(JVMHelper.jvmProperty("launcher.noJAnsi", "true"));
/*     */       
/* 410 */       JVMHelper.addSystemPropertyToArgs(context.args, "launcher.customdir");
/* 411 */       JVMHelper.addSystemPropertyToArgs(context.args, "launcher.usecustomdir");
/* 412 */       JVMHelper.addSystemPropertyToArgs(context.args, "launcher.useoptdir");
/*     */       
/* 414 */       if (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE && 
/* 415 */         JVMHelper.OS_VERSION.startsWith("10.")) {
/* 416 */         LogHelper.debug("MustDie 10 fix is applied");
/* 417 */         context.args.add(JVMHelper.jvmProperty("os.name", "Windows 10"));
/* 418 */         context.args.add(JVMHelper.jvmProperty("os.version", "10.0"));
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 423 */       String pathLauncher = IOHelper.getCodeSource(ClientLauncher.class).toString();
/* 424 */       context.pathLauncher = pathLauncher;
/*     */ 
/*     */       
/* 427 */       Collections.addAll(context.args, profile.getJvmArgs());
/*     */       
/* 429 */       profile.pushOptionalJvmArgs(context.args);
/* 430 */       Collections.addAll(context.args, new String[] { "-Djava.library.path=".concat(params.clientDir.resolve(NATIVES_DIR).toString().concat(File.separator)) });
/*     */       
/* 432 */       ClientHookManager.clientLaunchHook.hook(context);
/*     */ 
/*     */       
/* 435 */       if (LogHelper.isDebugEnabled()) {
/* 436 */         Collections.addAll(context.args, new String[] { "-debug" });
/*     */       }
/*     */       
/* 439 */       Collections.addAll(context.args, new String[] { "-jar" });
/* 440 */       Collections.addAll(context.args, new String[] { context.pathLauncher });
/*     */       
/* 442 */       Collections.addAll(context.args, new String[] { "-main" });
/* 443 */       Collections.addAll(context.args, new String[] { ClientLauncher.class.getName().replace('.', '/') });
/*     */       
/* 445 */       Collections.addAll(context.args, new String[] { Base64.getEncoder().encodeToString(longToBytes(Request.getSession())) });
/*     */ 
/*     */ 
/*     */       
/* 449 */       ClientHookManager.clientLaunchFinallyHook.hook(context);
/*     */ 
/*     */       
/* 452 */       LogHelper.debug("Commandline: " + context.args);
/*     */ 
/*     */       
/* 455 */       genServersDat(params.clientDir.toFile(), profile);
/*     */ 
/*     */       
/* 458 */       LogHelper.debug("Launching client instance");
/*     */       
/* 460 */       ProcessBuilder builder = new ProcessBuilder(context.args);
/* 461 */       context.builder = builder;
/* 462 */       LauncherGuardManager.guard.addCustomEnv(context);
/*     */ 
/*     */       
/* 465 */       EnvHelper.addEnv(builder);
/* 466 */       builder.directory(params.clientDir.toFile());
/* 467 */       builder.inheritIO();
/* 468 */       if (pipeOutput) {
/* 469 */         builder.redirectErrorStream(true);
/* 470 */         builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
/*     */       } 
/*     */       
/* 473 */       List<String> command = builder.command();
/*     */       
/* 475 */       ClientHookManager.preStartHook.hook(context, builder);
/*     */       
/* 477 */       LogHelper.debug("Perform full GC...");
/*     */       
/* 479 */       JVMHelper.fullGC();
/* 480 */       GarbageManager.gc();
/*     */       
/* 482 */       process = builder.start();
/*     */       
/* 484 */       if (builder.command() != command) {
/* 485 */         LogHelper.error("Something strange cheating...");
/* 486 */         NativeJVMHalt.haltA(0);
/* 487 */         System.exit(100);
/* 488 */         clientStarted = false;
/* 489 */         return null;
/*     */       } 
/*     */       
/* 492 */       if (ClientHookManager.postStartHook.hook(context, builder)) return process; 
/* 493 */       if (!pipeOutput) {
/* 494 */         for (int i = 0; i < 100; i++) {
/* 495 */           if (!process.isAlive()) {
/* 496 */             int exitCode = process.exitValue();
/* 497 */             LogHelper.error("Process exit code %d", new Object[] { Integer.valueOf(exitCode) });
/*     */             break;
/*     */           } 
/* 500 */           if (clientStarted) {
/*     */             break;
/*     */           }
/* 503 */           Thread.sleep(200L);
/*     */         } 
/* 505 */         if (!clientStarted) {
/* 506 */           LogHelper.error("Client did not start properly. Enable debug mode for more information");
/*     */         }
/*     */       } 
/* 509 */       clientStarted = false;
/*     */       
/* 511 */       return process;
/* 512 */     } catch (Throwable e) {
/* 513 */       LogHelper.error(e);
/* 514 */       Sentry.capture(e);
/*     */ 
/*     */       
/* 517 */       return null;
/*     */     } 
/*     */   }
/*     */   public static byte[] longToBytes(long x) {
/* 521 */     ByteBuffer buffer = ByteBuffer.allocate(8);
/* 522 */     buffer.putLong(x);
/* 523 */     return buffer.array();
/*     */   }
/*     */   
/*     */   public static long bytesToLong(byte[] bytes) {
/* 527 */     ByteBuffer buffer = ByteBuffer.allocate(8);
/* 528 */     buffer.put(bytes);
/* 529 */     buffer.flip();
/* 530 */     return buffer.getLong();
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static void main(String... args) throws Throwable {
/* 535 */     Sentry.init("https://e21b06e00b9840dbb0658a716fae21b3@sentry.streamcraft.net/3");
/*     */     try {
/*     */       Params params;
/* 538 */       Sentry.getContext().recordBreadcrumb((new BreadcrumbBuilder())
/* 539 */           .setMessage("Starting client").build());
/*     */ 
/*     */       
/* 542 */       LauncherEngine.setJVMOpts();
/*     */       
/* 544 */       Sentry.getContext().addTag("version", Version.getVersion().toString());
/* 545 */       Sentry.getContext().addTag("os", JVMHelper.OS_TYPE.name());
/* 546 */       Sentry.getContext().addTag("os_version", JVMHelper.OS_VERSION);
/* 547 */       Sentry.getContext().addTag("os_bits", String.valueOf(JVMHelper.OS_BITS));
/*     */       
/* 549 */       Sentry.getContext().addTag("jvm_bits", String.valueOf(JVMHelper.JVM_BITS));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 557 */       initGson(null);
/*     */       
/* 559 */       JVMHelper.verifySystemProperties(ClientLauncher.class, true);
/* 560 */       EnvHelper.checkDangerousParams();
/*     */       
/* 562 */       LogHelper.printVersion("Client Launcher");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 569 */       LauncherGuardManager.initGuard(true);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 574 */       Sentry.getContext().recordBreadcrumb((new BreadcrumbBuilder())
/* 575 */           .setMessage("Restore sessions").build());
/*     */       
/* 577 */       LogHelper.debug("Restore sessions");
/*     */       
/* 579 */       Request.setSession(bytesToLong(Base64.getDecoder().decode(args[0])));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 585 */       RestoreSessionRequest request = new RestoreSessionRequest(Request.getSession());
/* 586 */       request.request();
/* 587 */       Request.service.reconnectCallback = (() -> {
/*     */           LogHelper.debug("WebSocket connect closed. Try reconnect");
/*     */           
/*     */           try {
/*     */             Request.service.open();
/*     */             LogHelper.debug("Connect to %s", new Object[] { (Launcher.getConfig()).address });
/* 593 */           } catch (Exception e) {
/*     */             LogHelper.error(e);
/*     */             throw new RequestException(String.format("Connect error: %s", new Object[] { (e.getMessage() != null) ? e.getMessage() : "null" }));
/*     */           } 
/*     */           try {
/*     */             RestoreSessionRequest request1 = new RestoreSessionRequest(Request.getSession());
/*     */             request1.request();
/* 600 */           } catch (Exception e) {
/*     */             LogHelper.error(e);
/*     */           } 
/*     */         });
/*     */       
/* 605 */       Sentry.getContext().recordBreadcrumb((new BreadcrumbBuilder())
/* 606 */           .setMessage("Reading ClientLauncher params").build());
/*     */ 
/*     */       
/* 609 */       LogHelper.debug("Reading ClientLauncher params");
/*     */       
/* 611 */       ParamsRequest paramsRequest = new ParamsRequest();
/* 612 */       ParamsRequestEvent event = (ParamsRequestEvent)paramsRequest.request();
/*     */       
/* 614 */       FunctionalBridge.processJVMPacket(event.packet);
/*     */       
/* 616 */       ClientProfile profile = event.profile;
/* 617 */       Sentry.getContext().addTag("profile", profile.getTitle());
/*     */       
/* 619 */       try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(event.data); 
/* 620 */           HInput input = new HInput(byteArrayInputStream)) {
/* 621 */         params = new Params(input);
/* 622 */         HashedDir clientHDir = new HashedDir(input);
/*     */       } 
/*     */ 
/*     */       
/* 626 */       Sentry.getContext().setUser((new UserBuilder())
/* 627 */           .setUsername(params.pp.username)
/* 628 */           .setId(params.pp.uuid.toString())
/* 629 */           .build());
/*     */ 
/*     */       
/* 632 */       Sentry.getContext().addExtra("clientDir", params.clientDir.toAbsolutePath().toString());
/* 633 */       Sentry.getContext().addExtra("assetDir", params.assetDir.toAbsolutePath().toString());
/* 634 */       Sentry.getContext().addExtra("ram", Integer.valueOf(params.ram));
/* 635 */       Sentry.getContext().addExtra("accessToken", params.accessToken);
/* 636 */       Sentry.getContext().addExtra("session", Long.valueOf(params.session));
/*     */       
/* 638 */       Launcher.profile = profile;
/* 639 */       playerProfile = params.pp;
/* 640 */       checkJVMBitsAndVersion();
/*     */ 
/*     */       
/* 643 */       Sentry.getContext().recordBreadcrumb((new BreadcrumbBuilder())
/* 644 */           .setMessage("Verifying ClientLauncher sign and classpath").build());
/*     */       
/* 646 */       LogHelper.debug("Verifying ClientLauncher sign and classpath");
/* 647 */       URL[] classpathurls = resolveClassPath(params.clientDir, profile.getClassPath());
/* 648 */       ClientLauncher.classLoader = new PublicURLClassLoader(classpathurls, ClassLoader.getSystemClassLoader());
/*     */       
/* 650 */       for (URL classpathURL : classpathurls) {
/*     */ 
/*     */         
/* 653 */         ClientLauncher.classLoader.addURL(classpathURL);
/*     */         
/* 655 */         URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
/* 656 */         Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
/* 657 */         method.setAccessible(true);
/* 658 */         method.invoke(classLoader, new Object[] { classpathURL });
/*     */       } 
/* 660 */       profile.pushOptionalClassPath(cp -> {
/*     */             URL[] optionalClassPath = resolveClassPath(params.clientDir, cp);
/*     */             
/*     */             for (URL classpathURL : optionalClassPath) {
/*     */               ClientLauncher.classLoader.addURL(classpathURL);
/*     */               
/*     */               try {
/*     */                 URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
/*     */                 
/*     */                 Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
/*     */                 method.setAccessible(true);
/*     */                 method.invoke(classLoader, new Object[] { classpathURL });
/* 672 */               } catch (Exception e) {
/*     */                 Sentry.capture(e);
/*     */                 
/*     */                 e.printStackTrace();
/*     */               } 
/*     */             } 
/*     */           });
/* 679 */       Thread.currentThread().setContextClassLoader((ClassLoader)ClientLauncher.classLoader);
/* 680 */       ClientLauncher.classLoader.nativePath = params.clientDir.resolve(NATIVES_DIR).toString().concat(File.separator);
/* 681 */       PublicURLClassLoader.systemclassloader = (ClassLoader)ClientLauncher.classLoader;
/*     */ 
/*     */ 
/*     */       
/* 685 */       Sentry.getContext().recordBreadcrumb((new BreadcrumbBuilder())
/* 686 */           .setMessage("Starting JVM and run launch").build());
/*     */       
/* 688 */       LogHelper.debug("Starting JVM and client WatchService");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 709 */       launch(profile, params);
/*     */     }
/* 711 */     catch (Throwable e) {
/* 712 */       Params params; LogHelper.error((Throwable)params);
/* 713 */       Sentry.capture((Throwable)params);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static URL[] resolveClassPath(Path clientDir, String... classPath) throws IOException {
/* 718 */     return (URL[])resolveClassPathStream(clientDir, classPath).map(IOHelper::toURL).toArray(x$0 -> new URL[x$0]);
/*     */   }
/*     */   
/*     */   private static LinkedList<Path> resolveClassPathList(Path clientDir, String... classPath) throws IOException {
/* 722 */     return resolveClassPathStream(clientDir, classPath).collect(Collectors.toCollection(LinkedList::new));
/*     */   }
/*     */   
/*     */   private static Stream<Path> resolveClassPathStream(Path clientDir, String... classPath) throws IOException {
/* 726 */     Stream.Builder<Path> builder = Stream.builder();
/* 727 */     for (String classPathEntry : classPath) {
/* 728 */       Path path = clientDir.resolve(IOHelper.toPath(classPathEntry));
/* 729 */       if (IOHelper.isDir(path)) {
/* 730 */         IOHelper.walk(path, new ClassPathFileVisitor(builder), false);
/*     */       } else {
/*     */         
/* 733 */         builder.accept(path);
/*     */       } 
/* 735 */     }  return builder.build();
/*     */   }
/*     */   
/*     */   private static void initGson(ClientModuleManager moduleManager) {
/* 739 */     Launcher.gsonManager = (GsonManager)new ClientGsonManager(moduleManager);
/* 740 */     Launcher.gsonManager.initGson();
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public static void setProfile(ClientProfile profile) {
/* 745 */     Launcher.profile = profile;
/* 746 */     LogHelper.debug("New Profile name: %s", new Object[] { profile.getTitle() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void verifyHDir(Path dir, HashedDir hdir, FileNameMatcher matcher, boolean digest) throws IOException {
/* 754 */     HashedDir currentHDir = new HashedDir(dir, matcher, true, digest);
/* 755 */     HashedDir.Diff diff = hdir.diff(currentHDir, matcher);
/* 756 */     if (!diff.isSame()) {
/* 757 */       ArrayList<String> extra_files = new ArrayList<>();
/* 758 */       ArrayList<String> mismatch_files = new ArrayList<>();
/*     */       
/* 760 */       diff.extra.walk(File.separator, (e, k, v) -> {
/*     */             extra_files.add(e); if (v.getType().equals(HashedEntry.Type.FILE)) {
/*     */               LogHelper.error("Extra file %s", new Object[] { e });
/*     */             } else {
/*     */               LogHelper.error("Extra %s", new Object[] { e });
/*     */             } 
/*     */             return HashedDir.WalkAction.CONTINUE;
/*     */           });
/* 768 */       diff.mismatch.walk(File.separator, (e, k, v) -> {
/*     */             mismatch_files.add(e);
/*     */             if (v.getType().equals(HashedEntry.Type.FILE)) {
/*     */               LogHelper.error("Mismatch file %s", new Object[] { e });
/*     */             } else {
/*     */               LogHelper.error("Mismatch %s", new Object[] { e });
/*     */             } 
/*     */             return HashedDir.WalkAction.CONTINUE;
/*     */           });
/* 777 */       StringBuilder builder = new StringBuilder("В папке клиента обнаружены лишние файлы:");
/* 778 */       extra_files.forEach(file -> builder.append("\n[+] " + file));
/* 779 */       mismatch_files.forEach(file -> builder.append("\n[-] " + file));
/* 780 */       builder.append("\nПопробуйте удалить папку клиента или обратитесь в поддержку");
/*     */       
/* 782 */       JOptionPane.showMessageDialog(null, builder.toString(), "StreamCraft | Launcher", 0);
/*     */       
/* 784 */       throw new SecurityException(String.format("Forbidden modification: '%s'", new Object[] { IOHelper.getFileName(dir) }));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\client\ClientLauncher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */