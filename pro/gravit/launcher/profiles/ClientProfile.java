/*     */ package pro.gravit.launcher.profiles;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import pro.gravit.launcher.LauncherAPI;
/*     */ import pro.gravit.launcher.ServerPinger;
/*     */ import pro.gravit.launcher.hasher.FileNameMatcher;
/*     */ import pro.gravit.launcher.hasher.HashedDir;
/*     */ import pro.gravit.launcher.profiles.optional.OptionalDepend;
/*     */ import pro.gravit.launcher.profiles.optional.OptionalFile;
/*     */ import pro.gravit.launcher.profiles.optional.OptionalType;
/*     */ import pro.gravit.utils.helper.VerifyHelper;
/*     */ 
/*     */ public final class ClientProfile implements Comparable<ClientProfile> {
/*     */   @FunctionalInterface
/*     */   public static interface pushOptionalClassPathCallback {
/*     */     void run(String[] param1ArrayOfString) throws IOException;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public enum Version {
/*  27 */     MC125("1.2.5", 29),
/*  28 */     MC147("1.4.7", 51),
/*  29 */     MC152("1.5.2", 61),
/*  30 */     MC164("1.6.4", 78),
/*  31 */     MC172("1.7.2", 4),
/*  32 */     MC1710("1.7.10", 5),
/*  33 */     MC189("1.8.9", 47),
/*  34 */     MC19("1.9", 107),
/*  35 */     MC192("1.9.2", 109),
/*  36 */     MC194("1.9.4", 110),
/*  37 */     MC1102("1.10.2", 210),
/*  38 */     MC111("1.11", 315),
/*  39 */     MC1112("1.11.2", 316),
/*  40 */     MC112("1.12", 335),
/*  41 */     MC1121("1.12.1", 338),
/*  42 */     MC1122("1.12.2", 340),
/*  43 */     MC113("1.13", 393),
/*  44 */     MC1131("1.13.1", 401),
/*  45 */     MC1132("1.13.2", 402),
/*  46 */     MC114("1.14", 477),
/*  47 */     MC1141("1.14.1", 480),
/*  48 */     MC1142("1.14.2", 485),
/*  49 */     MC1143("1.14.3", 490),
/*  50 */     MC1144("1.14.4", 498),
/*  51 */     MC1152("1.15.2", 578); private static final Map<String, Version> VERSIONS;
/*     */     public final String name;
/*     */     public final int protocol;
/*     */     
/*     */     static {
/*  56 */       Version[] versionsValues = values();
/*  57 */       VERSIONS = new HashMap<>(versionsValues.length);
/*  58 */       for (Version version : versionsValues)
/*  59 */         VERSIONS.put(version.name, version); 
/*     */     }
/*     */     
/*     */     public static Version byName(String name) {
/*  63 */       return (Version)VerifyHelper.getMapValue(VERSIONS, name, String.format("Unknown client version: '%s'", new Object[] { name }));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Version(String name, int protocol) {
/*  71 */       this.name = name;
/*  72 */       this.protocol = protocol;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/*  77 */       return "Minecraft " + this.name;
/*     */     }
/*     */   }
/*     */   
/*  81 */   public static final boolean profileCaseSensitive = Boolean.getBoolean("launcher.clientProfile.caseSensitive");
/*     */   
/*  83 */   private static final FileNameMatcher ASSET_MATCHER = new FileNameMatcher(new String[0], new String[] { "indexes", "objects" }, new String[0]);
/*     */   
/*     */   @LauncherAPI
/*     */   private String version;
/*     */   
/*     */   @LauncherAPI
/*     */   private String assetIndex;
/*     */   
/*     */   @LauncherAPI
/*     */   private String dir;
/*     */   
/*     */   @LauncherAPI
/*     */   private String assetDir;
/*     */   @LauncherAPI
/*     */   private int sortIndex;
/*     */   @LauncherAPI
/*     */   private String title;
/*     */   @LauncherAPI
/*     */   private String info;
/*     */   @LauncherAPI
/*     */   private String serverAddress;
/*     */   @LauncherAPI
/*     */   private int serverPort;
/*     */   @LauncherAPI
/* 107 */   private final List<String> servers = new ArrayList<>();
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public List<String> getServers() {
/* 112 */     return this.servers;
/*     */   }
/*     */   @LauncherAPI
/* 115 */   public ServerPinger.Result pingResult = new ServerPinger.Result(0, 0, "");
/*     */ 
/*     */   
/*     */   @LauncherAPI
/* 119 */   private final List<String> update = new ArrayList<>();
/*     */   @LauncherAPI
/* 121 */   private final List<String> updateExclusions = new ArrayList<>();
/*     */   @LauncherAPI
/* 123 */   private final List<String> updateShared = new ArrayList<>();
/*     */   @LauncherAPI
/* 125 */   private final List<String> updateVerify = new ArrayList<>();
/*     */   @LauncherAPI
/* 127 */   private final Set<OptionalFile> updateOptional = new HashSet<>();
/*     */   
/*     */   @LauncherAPI
/*     */   private boolean updateFastCheck;
/*     */   @LauncherAPI
/*     */   private boolean useWhitelist;
/*     */   @LauncherAPI
/*     */   private String mainClass;
/*     */   @LauncherAPI
/* 136 */   private final List<String> jvmArgs = new ArrayList<>();
/*     */   @LauncherAPI
/* 138 */   private final List<String> classPath = new ArrayList<>();
/*     */   @LauncherAPI
/* 140 */   private final List<String> clientArgs = new ArrayList<>();
/*     */   @LauncherAPI
/* 142 */   private final List<String> whitelist = new ArrayList<>();
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(ClientProfile o) {
/* 147 */     return Integer.compare(getSortIndex(), o.getSortIndex());
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String getAssetIndex() {
/* 152 */     return this.assetIndex;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public FileNameMatcher getAssetUpdateMatcher() {
/* 157 */     return (getVersion().compareTo(Version.MC1710) >= 0) ? ASSET_MATCHER : null;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String[] getClassPath() {
/* 162 */     return this.classPath.<String>toArray(new String[0]);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String[] getClientArgs() {
/* 167 */     return this.clientArgs.<String>toArray(new String[0]);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String getDir() {
/* 172 */     return this.dir;
/*     */   }
/*     */   
/*     */   public void setDir(String dir) {
/* 176 */     this.dir = dir;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String getAssetDir() {
/* 181 */     return this.assetDir;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public FileNameMatcher getClientUpdateMatcher() {
/* 186 */     String[] updateArray = this.update.<String>toArray(new String[0]);
/* 187 */     String[] verifyArray = this.updateVerify.<String>toArray(new String[0]);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 196 */     List<String> excludeList = this.updateExclusions;
/* 197 */     String[] exclusionsArray = excludeList.<String>toArray(new String[0]);
/* 198 */     return new FileNameMatcher(updateArray, verifyArray, exclusionsArray);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String[] getJvmArgs() {
/* 203 */     return this.jvmArgs.<String>toArray(new String[0]);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String getMainClass() {
/* 208 */     return this.mainClass;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String getServerAddress() {
/* 213 */     return this.serverAddress;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public Set<OptionalFile> getOptional() {
/* 218 */     return this.updateOptional;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void updateOptionalGraph() {
/* 223 */     for (OptionalFile file : this.updateOptional) {
/* 224 */       if (file.dependenciesFile != null) {
/* 225 */         file.dependencies = new OptionalFile[file.dependenciesFile.length];
/* 226 */         for (int i = 0; i < file.dependenciesFile.length; i++) {
/* 227 */           file.dependencies[i] = getOptionalFile((file.dependenciesFile[i]).name, (file.dependenciesFile[i]).type);
/*     */         }
/*     */       } 
/* 230 */       if (file.conflictFile != null) {
/* 231 */         file.conflict = new OptionalFile[file.conflictFile.length];
/* 232 */         for (int i = 0; i < file.conflictFile.length; i++) {
/* 233 */           file.conflict[i] = getOptionalFile((file.conflictFile[i]).name, (file.conflictFile[i]).type);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public OptionalFile getOptionalFile(String file, OptionalType type) {
/* 241 */     for (OptionalFile f : this.updateOptional) {
/* 242 */       if (f.type.equals(type) && f.name.equals(file)) return f; 
/* 243 */     }  return null;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public Collection<String> getShared() {
/* 248 */     return this.updateShared;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void markOptional(String name, OptionalType type) {
/* 253 */     OptionalFile file = getOptionalFile(name, type);
/* 254 */     if (file != null) {
/* 255 */       markOptional(file);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public void markOptional(OptionalFile file) {
/* 262 */     if (file.mark)
/* 263 */       return;  file.mark = true;
/* 264 */     if (file.dependencies != null) {
/* 265 */       for (OptionalFile dep : file.dependencies) {
/* 266 */         if (dep.dependenciesCount == null) dep.dependenciesCount = new HashSet(); 
/* 267 */         dep.dependenciesCount.add(file);
/* 268 */         markOptional(dep);
/*     */       } 
/*     */     }
/* 271 */     if (file.conflict != null) {
/* 272 */       for (OptionalFile conflict : file.conflict) {
/* 273 */         unmarkOptional(conflict);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void unmarkOptional(String name, OptionalType type) {
/* 280 */     OptionalFile file = getOptionalFile(name, type);
/* 281 */     if (file != null) {
/* 282 */       unmarkOptional(file);
/*     */     }
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void unmarkOptional(OptionalFile file) {
/* 288 */     if (!file.mark)
/* 289 */       return;  file.mark = false;
/* 290 */     if (file.dependenciesCount != null) {
/* 291 */       for (OptionalFile f : file.dependenciesCount) {
/* 292 */         if (f.isPreset)
/* 293 */           continue;  unmarkOptional(f);
/*     */       } 
/* 295 */       file.dependenciesCount.clear();
/* 296 */       file.dependenciesCount = null;
/*     */     } 
/* 298 */     if (file.dependencies != null)
/* 299 */       for (OptionalFile f : file.dependencies) {
/* 300 */         if (f.mark) {
/* 301 */           if (f.dependenciesCount == null) {
/* 302 */             unmarkOptional(f);
/* 303 */           } else if (f.dependenciesCount.size() <= 1) {
/* 304 */             f.dependenciesCount.clear();
/* 305 */             f.dependenciesCount = null;
/* 306 */             unmarkOptional(f);
/*     */           } 
/*     */         }
/*     */       }  
/*     */   }
/*     */   
/*     */   public void pushOptionalFile(HashedDir dir, boolean digest) {
/* 313 */     for (OptionalFile opt : this.updateOptional) {
/* 314 */       if (opt.type.equals(OptionalType.FILE) && !opt.mark)
/* 315 */         for (String file : opt.list) {
/* 316 */           dir.removeR(file);
/*     */         } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void pushOptionalJvmArgs(Collection<String> jvmArgs1) {
/* 322 */     for (OptionalFile opt : this.updateOptional) {
/* 323 */       if (opt.type.equals(OptionalType.JVMARGS) && opt.mark) {
/* 324 */         jvmArgs1.addAll(Arrays.asList(opt.list));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void pushOptionalClientArgs(Collection<String> clientArgs1) {
/* 330 */     for (OptionalFile opt : this.updateOptional) {
/* 331 */       if (opt.type.equals(OptionalType.CLIENTARGS) && opt.mark) {
/* 332 */         clientArgs1.addAll(Arrays.asList(opt.list));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void pushOptionalClassPath(pushOptionalClassPathCallback callback) throws IOException {
/* 338 */     for (OptionalFile opt : this.updateOptional) {
/* 339 */       if (opt.type.equals(OptionalType.CLASSPATH) && opt.mark) {
/* 340 */         callback.run(opt.list);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public int getServerPort() {
/* 352 */     return this.serverPort;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public InetSocketAddress getServerSocketAddress() {
/* 357 */     return InetSocketAddress.createUnresolved(getServerAddress(), getServerPort());
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public int getSortIndex() {
/* 362 */     return this.sortIndex;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String getTitle() {
/* 367 */     return this.title;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String getInfo() {
/* 372 */     return this.info;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public Version getVersion() {
/* 377 */     return Version.byName(this.version);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public boolean isUpdateFastCheck() {
/* 382 */     return this.updateFastCheck;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public boolean isWhitelistContains(String username) {
/* 387 */     if (!this.useWhitelist) return true; 
/* 388 */     return this.whitelist.stream().anyMatch(profileCaseSensitive ? (e -> e.equals(username)) : (e -> e.equalsIgnoreCase(username)));
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void setTitle(String title) {
/* 393 */     this.title = title;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void setInfo(String info) {
/* 398 */     this.info = info;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void setVersion(Version version) {
/* 403 */     this.version = version.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 408 */     return this.title;
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public void verify() {
/* 414 */     getVersion();
/* 415 */     IOHelper.verifyFileName(getAssetIndex());
/*     */ 
/*     */     
/* 418 */     VerifyHelper.verify(getTitle(), VerifyHelper.NOT_EMPTY, "Profile title can't be empty");
/* 419 */     VerifyHelper.verify(getInfo(), VerifyHelper.NOT_EMPTY, "Profile info can't be empty");
/* 420 */     VerifyHelper.verify(getServerAddress(), VerifyHelper.NOT_EMPTY, "Server address can't be empty");
/* 421 */     VerifyHelper.verifyInt(getServerPort(), VerifyHelper.range(0, 65535), "Illegal server port: " + getServerPort());
/*     */ 
/*     */     
/* 424 */     VerifyHelper.verify(getTitle(), VerifyHelper.NOT_EMPTY, "Main class can't be empty");
/* 425 */     for (String s : this.update) {
/* 426 */       if (s == null) throw new IllegalArgumentException("Found null entry in update"); 
/*     */     } 
/* 428 */     for (String s : this.updateVerify) {
/* 429 */       if (s == null) throw new IllegalArgumentException("Found null entry in updateVerify"); 
/*     */     } 
/* 431 */     for (String s : this.updateExclusions) {
/* 432 */       if (s == null) throw new IllegalArgumentException("Found null entry in updateExclusions");
/*     */     
/*     */     } 
/* 435 */     for (String s : this.classPath) {
/* 436 */       if (s == null) throw new IllegalArgumentException("Found null entry in classPath"); 
/*     */     } 
/* 438 */     for (String s : this.jvmArgs) {
/* 439 */       if (s == null) throw new IllegalArgumentException("Found null entry in jvmArgs"); 
/*     */     } 
/* 441 */     for (String s : this.clientArgs) {
/* 442 */       if (s == null) throw new IllegalArgumentException("Found null entry in clientArgs"); 
/*     */     } 
/* 444 */     for (OptionalFile f : this.updateOptional) {
/* 445 */       if (f == null) throw new IllegalArgumentException("Found null entry in updateOptional"); 
/* 446 */       if (f.name == null) throw new IllegalArgumentException("Optional: name must not be null"); 
/* 447 */       if (f.list == null) throw new IllegalArgumentException("Optional: list must not be null"); 
/* 448 */       for (String s : f.list) {
/* 449 */         if (s == null) throw new IllegalArgumentException(String.format("Found null entry in updateOptional.%s.list", new Object[] { f.name })); 
/*     */       } 
/* 451 */       if (f.conflictFile != null) for (OptionalDepend s : f.conflictFile) {
/* 452 */           if (s == null) throw new IllegalArgumentException(String.format("Found null entry in updateOptional.%s.conflictFile", new Object[] { f.name })); 
/*     */         }  
/* 454 */       if (f.dependenciesFile != null) for (OptionalDepend s : f.dependenciesFile) {
/* 455 */           if (s == null) throw new IllegalArgumentException(String.format("Found null entry in updateOptional.%s.dependenciesFile", new Object[] { f.name }));
/*     */         
/*     */         }  
/*     */     } 
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 462 */     int prime = 31;
/* 463 */     int result = 1;
/* 464 */     result = 31 * result + ((this.assetDir == null) ? 0 : this.assetDir.hashCode());
/* 465 */     result = 31 * result + ((this.assetIndex == null) ? 0 : this.assetIndex.hashCode());
/* 466 */     result = 31 * result + ((this.classPath == null) ? 0 : this.classPath.hashCode());
/* 467 */     result = 31 * result + ((this.clientArgs == null) ? 0 : this.clientArgs.hashCode());
/* 468 */     result = 31 * result + ((this.dir == null) ? 0 : this.dir.hashCode());
/* 469 */     result = 31 * result + ((this.jvmArgs == null) ? 0 : this.jvmArgs.hashCode());
/* 470 */     result = 31 * result + ((this.mainClass == null) ? 0 : this.mainClass.hashCode());
/* 471 */     result = 31 * result + ((this.serverAddress == null) ? 0 : this.serverAddress.hashCode());
/* 472 */     result = 31 * result + this.serverPort;
/* 473 */     result = 31 * result + this.sortIndex;
/* 474 */     result = 31 * result + ((this.title == null) ? 0 : this.title.hashCode());
/* 475 */     result = 31 * result + ((this.info == null) ? 0 : this.info.hashCode());
/* 476 */     result = 31 * result + ((this.update == null) ? 0 : this.update.hashCode());
/* 477 */     result = 31 * result + ((this.updateExclusions == null) ? 0 : this.updateExclusions.hashCode());
/* 478 */     result = 31 * result + (this.updateFastCheck ? 1231 : 1237);
/* 479 */     result = 31 * result + ((this.updateOptional == null) ? 0 : this.updateOptional.hashCode());
/* 480 */     result = 31 * result + ((this.updateShared == null) ? 0 : this.updateShared.hashCode());
/* 481 */     result = 31 * result + ((this.updateVerify == null) ? 0 : this.updateVerify.hashCode());
/* 482 */     result = 31 * result + (this.useWhitelist ? 1231 : 1237);
/* 483 */     result = 31 * result + ((this.version == null) ? 0 : this.version.hashCode());
/* 484 */     result = 31 * result + ((this.whitelist == null) ? 0 : this.whitelist.hashCode());
/* 485 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 490 */     if (this == obj)
/* 491 */       return true; 
/* 492 */     if (obj == null)
/* 493 */       return false; 
/* 494 */     if (getClass() != obj.getClass())
/* 495 */       return false; 
/* 496 */     ClientProfile other = (ClientProfile)obj;
/* 497 */     if (this.assetDir == null) {
/* 498 */       if (other.assetDir != null)
/* 499 */         return false; 
/* 500 */     } else if (!this.assetDir.equals(other.assetDir)) {
/* 501 */       return false;
/* 502 */     }  if (this.assetIndex == null) {
/* 503 */       if (other.assetIndex != null)
/* 504 */         return false; 
/* 505 */     } else if (!this.assetIndex.equals(other.assetIndex)) {
/* 506 */       return false;
/* 507 */     }  if (this.classPath == null) {
/* 508 */       if (other.classPath != null)
/* 509 */         return false; 
/* 510 */     } else if (!this.classPath.equals(other.classPath)) {
/* 511 */       return false;
/* 512 */     }  if (this.clientArgs == null) {
/* 513 */       if (other.clientArgs != null)
/* 514 */         return false; 
/* 515 */     } else if (!this.clientArgs.equals(other.clientArgs)) {
/* 516 */       return false;
/* 517 */     }  if (this.dir == null) {
/* 518 */       if (other.dir != null)
/* 519 */         return false; 
/* 520 */     } else if (!this.dir.equals(other.dir)) {
/* 521 */       return false;
/* 522 */     }  if (this.jvmArgs == null) {
/* 523 */       if (other.jvmArgs != null)
/* 524 */         return false; 
/* 525 */     } else if (!this.jvmArgs.equals(other.jvmArgs)) {
/* 526 */       return false;
/* 527 */     }  if (this.mainClass == null) {
/* 528 */       if (other.mainClass != null)
/* 529 */         return false; 
/* 530 */     } else if (!this.mainClass.equals(other.mainClass)) {
/* 531 */       return false;
/* 532 */     }  if (this.serverAddress == null) {
/* 533 */       if (other.serverAddress != null)
/* 534 */         return false; 
/* 535 */     } else if (!this.serverAddress.equals(other.serverAddress)) {
/* 536 */       return false;
/* 537 */     }  if (this.serverPort != other.serverPort)
/* 538 */       return false; 
/* 539 */     if (this.sortIndex != other.sortIndex)
/* 540 */       return false; 
/* 541 */     if (this.title == null) {
/* 542 */       if (other.title != null)
/* 543 */         return false; 
/* 544 */     } else if (!this.title.equals(other.title)) {
/* 545 */       return false;
/* 546 */     }  if (this.info == null) {
/* 547 */       if (other.info != null)
/* 548 */         return false; 
/* 549 */     } else if (!this.info.equals(other.info)) {
/* 550 */       return false;
/* 551 */     }  if (this.update == null) {
/* 552 */       if (other.update != null)
/* 553 */         return false; 
/* 554 */     } else if (!this.update.equals(other.update)) {
/* 555 */       return false;
/* 556 */     }  if (this.updateExclusions == null) {
/* 557 */       if (other.updateExclusions != null)
/* 558 */         return false; 
/* 559 */     } else if (!this.updateExclusions.equals(other.updateExclusions)) {
/* 560 */       return false;
/* 561 */     }  if (this.updateFastCheck != other.updateFastCheck)
/* 562 */       return false; 
/* 563 */     if (this.updateOptional == null) {
/* 564 */       if (other.updateOptional != null)
/* 565 */         return false; 
/* 566 */     } else if (!this.updateOptional.equals(other.updateOptional)) {
/* 567 */       return false;
/* 568 */     }  if (this.updateShared == null) {
/* 569 */       if (other.updateShared != null)
/* 570 */         return false; 
/* 571 */     } else if (!this.updateShared.equals(other.updateShared)) {
/* 572 */       return false;
/* 573 */     }  if (this.updateVerify == null) {
/* 574 */       if (other.updateVerify != null)
/* 575 */         return false; 
/* 576 */     } else if (!this.updateVerify.equals(other.updateVerify)) {
/* 577 */       return false;
/* 578 */     }  if (this.useWhitelist != other.useWhitelist)
/* 579 */       return false; 
/* 580 */     if (this.version == null) {
/* 581 */       if (other.version != null)
/* 582 */         return false; 
/* 583 */     } else if (!this.version.equals(other.version)) {
/* 584 */       return false;
/* 585 */     }  if (this.whitelist == null)
/* 586 */       return (other.whitelist == null); 
/* 587 */     return this.whitelist.equals(other.whitelist);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\profiles\ClientProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */