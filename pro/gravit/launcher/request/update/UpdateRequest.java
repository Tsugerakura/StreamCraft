/*     */ package pro.gravit.launcher.request.update;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import pro.gravit.launcher.Launcher;
/*     */ import pro.gravit.launcher.LauncherAPI;
/*     */ import pro.gravit.launcher.LauncherNetworkAPI;
/*     */ import pro.gravit.launcher.downloader.ListDownloader;
/*     */ import pro.gravit.launcher.events.request.UpdateRequestEvent;
/*     */ import pro.gravit.launcher.hasher.FileNameMatcher;
/*     */ import pro.gravit.launcher.hasher.HashedDir;
/*     */ import pro.gravit.launcher.hasher.HashedEntry;
/*     */ import pro.gravit.launcher.hasher.HashedFile;
/*     */ import pro.gravit.launcher.request.Request;
/*     */ import pro.gravit.launcher.request.WebSocketEvent;
/*     */ import pro.gravit.launcher.request.websockets.StandartClientWebSocketService;
/*     */ import pro.gravit.launcher.request.websockets.WebSocketRequest;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ 
/*     */ 
/*     */ public final class UpdateRequest
/*     */   extends Request<UpdateRequestEvent>
/*     */   implements WebSocketRequest
/*     */ {
/*     */   private static UpdateController controller;
/*     */   @LauncherNetworkAPI
/*     */   private final String dirName;
/*     */   private final transient Path dir;
/*     */   private final transient FileNameMatcher matcher;
/*     */   private final transient boolean digest;
/*     */   private volatile transient State.Callback stateCallback;
/*     */   private transient HashedDir localDir;
/*     */   private transient long totalDownloaded;
/*     */   private transient long totalSize;
/*     */   private transient Instant startTime;
/*     */   
/*     */   public static void setController(UpdateController controller) {
/*  47 */     UpdateRequest.controller = controller;
/*     */   }
/*     */   
/*     */   public static UpdateController getController() {
/*  51 */     return controller;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getType() {
/*  56 */     return "update";
/*     */   }
/*     */   @FunctionalInterface
/*     */   public static interface Callback {
/*     */     @LauncherAPI
/*     */     void call(UpdateRequest.State param1State); }
/*     */   public static interface UpdateController {
/*     */     void preUpdate(UpdateRequest param1UpdateRequest, UpdateRequestEvent param1UpdateRequestEvent) throws IOException;
/*     */     void preDiff(UpdateRequest param1UpdateRequest, UpdateRequestEvent param1UpdateRequestEvent) throws IOException;
/*     */     void postDiff(UpdateRequest param1UpdateRequest, UpdateRequestEvent param1UpdateRequestEvent, HashedDir.Diff param1Diff) throws IOException;
/*     */     
/*     */     void preDownload(UpdateRequest param1UpdateRequest, UpdateRequestEvent param1UpdateRequestEvent, List<ListDownloader.DownloadTask> param1List) throws IOException;
/*     */     
/*     */     void postDownload(UpdateRequest param1UpdateRequest, UpdateRequestEvent param1UpdateRequestEvent) throws IOException;
/*     */     
/*     */     void postUpdate(UpdateRequest param1UpdateRequest, UpdateRequestEvent param1UpdateRequestEvent) throws IOException; }
/*     */   
/*     */   public static final class State {
/*     */     @LauncherAPI
/*     */     public final long fileDownloaded;
/*     */     @LauncherAPI
/*     */     public final long fileSize;
/*     */     @LauncherAPI
/*     */     public final long totalDownloaded;
/*     */     
/*  81 */     public State(String filePath, long fileDownloaded, long fileSize, long totalDownloaded, long totalSize, Duration duration) { this.filePath = filePath;
/*  82 */       this.fileDownloaded = fileDownloaded;
/*  83 */       this.fileSize = fileSize;
/*  84 */       this.totalDownloaded = totalDownloaded;
/*  85 */       this.totalSize = totalSize;
/*     */ 
/*     */       
/*  88 */       this.duration = duration; } @LauncherAPI
/*     */     public final long totalSize; @LauncherAPI
/*     */     public final String filePath; @LauncherAPI
/*     */     public final Duration duration; @LauncherAPI
/*     */     public double getBps() {
/*  93 */       long seconds = this.duration.getSeconds();
/*  94 */       if (seconds == 0L)
/*  95 */         return -1.0D; 
/*  96 */       return this.totalDownloaded / seconds;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public Duration getEstimatedTime() {
/* 101 */       double bps = getBps();
/* 102 */       if (bps <= 0.0D)
/* 103 */         return null; 
/* 104 */       return Duration.ofSeconds((long)(getTotalRemaining() / bps));
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getFileDownloadedKiB() {
/* 109 */       return this.fileDownloaded / 1024.0D;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getFileDownloadedMiB() {
/* 114 */       return getFileDownloadedKiB() / 1024.0D;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getFileDownloadedPart() {
/* 119 */       if (this.fileSize == 0L)
/* 120 */         return 0.0D; 
/* 121 */       return this.fileDownloaded / this.fileSize;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public long getFileRemaining() {
/* 126 */       return this.fileSize - this.fileDownloaded;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getFileRemainingKiB() {
/* 131 */       return getFileRemaining() / 1024.0D;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getFileRemainingMiB() {
/* 136 */       return getFileRemainingKiB() / 1024.0D;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getFileSizeKiB() {
/* 141 */       return this.fileSize / 1024.0D;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getFileSizeMiB() {
/* 146 */       return getFileSizeKiB() / 1024.0D;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getTotalDownloadedKiB() {
/* 151 */       return this.totalDownloaded / 1024.0D;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getTotalDownloadedMiB() {
/* 156 */       return getTotalDownloadedKiB() / 1024.0D;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getTotalDownloadedPart() {
/* 161 */       if (this.totalSize == 0L)
/* 162 */         return 0.0D; 
/* 163 */       return this.totalDownloaded / this.totalSize;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public long getTotalRemaining() {
/* 168 */       return this.totalSize - this.totalDownloaded;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getTotalRemainingKiB() {
/* 173 */       return getTotalRemaining() / 1024.0D;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getTotalRemainingMiB() {
/* 178 */       return getTotalRemainingKiB() / 1024.0D;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getTotalSizeKiB() {
/* 183 */       return this.totalSize / 1024.0D;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public double getTotalSizeMiB() {
/* 188 */       return getTotalSizeKiB() / 1024.0D;
/*     */     } @FunctionalInterface
/*     */     public static interface Callback { @LauncherAPI
/*     */       void call(UpdateRequest.State param2State); }
/*     */   }
/*     */   public UpdateRequestEvent requestDo(StandartClientWebSocketService service) throws Exception {
/* 194 */     LogHelper.debug("Start update request");
/* 195 */     UpdateRequestEvent e = (UpdateRequestEvent)service.sendRequest(this);
/* 196 */     if (controller != null) controller.preUpdate(this, e); 
/* 197 */     LogHelper.debug("Start update");
/* 198 */     Launcher.profile.pushOptionalFile(e.hdir, !Launcher.profile.isUpdateFastCheck());
/* 199 */     if (controller != null) controller.preDiff(this, e); 
/* 200 */     HashedDir.Diff diff = e.hdir.diff(this.localDir, this.matcher);
/* 201 */     if (controller != null) controller.postDiff(this, e, diff); 
/* 202 */     List<ListDownloader.DownloadTask> adds = new ArrayList<>();
/* 203 */     if (controller != null) controller.preDownload(this, e, adds); 
/* 204 */     diff.mismatch.walk("/", (path, name, entry) -> {
/*     */           if (entry.getType().equals(HashedEntry.Type.FILE)) {
/*     */             if (!entry.flag) {
/*     */               HashedFile file = (HashedFile)entry;
/*     */               this.totalSize += file.size;
/*     */               adds.add(new ListDownloader.DownloadTask(path, file.size));
/*     */             } 
/*     */           } else if (entry.getType().equals(HashedEntry.Type.DIR)) {
/*     */             try {
/*     */               Files.createDirectories(this.dir.resolve(path), (FileAttribute<?>[])new FileAttribute[0]);
/* 214 */             } catch (IOException ex) {
/*     */               LogHelper.error(ex);
/*     */             } 
/*     */           } 
/*     */           return HashedDir.WalkAction.CONTINUE;
/*     */         });
/* 220 */     this.totalSize = diff.mismatch.size();
/* 221 */     this.startTime = Instant.now();
/* 222 */     updateState("UnknownFile", 0L, 100L);
/* 223 */     ListDownloader listDownloader = new ListDownloader();
/* 224 */     LogHelper.info("Download %s to %s", new Object[] { this.dirName, this.dir.toAbsolutePath().toString() });
/* 225 */     if (e.zip && !adds.isEmpty()) {
/* 226 */       listDownloader.downloadZip(e.url, adds, this.dir, this::updateState, add -> this.totalDownloaded += add, e.fullDownload);
/*     */     } else {
/* 228 */       listDownloader.download(e.url, adds, this.dir, this::updateState, add -> this.totalDownloaded += add);
/*     */     } 
/* 230 */     if (controller != null) controller.postDownload(this, e); 
/* 231 */     deleteExtraDir(this.dir, diff.extra, diff.extra.flag);
/* 232 */     if (controller != null) controller.postUpdate(this, e); 
/* 233 */     LogHelper.debug("Update success");
/* 234 */     return e;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Path getDir() {
/* 243 */     return this.dir;
/*     */   }
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
/*     */   @LauncherAPI
/*     */   public UpdateRequest(String dirName, Path dir, FileNameMatcher matcher, boolean digest) {
/* 260 */     this.dirName = IOHelper.verifyFileName(dirName);
/* 261 */     this.dir = Objects.<Path>requireNonNull(dir, "dir");
/* 262 */     this.matcher = matcher;
/* 263 */     this.digest = digest;
/*     */   }
/*     */   
/*     */   private void deleteExtraDir(Path subDir, HashedDir subHDir, boolean flag) throws IOException {
/* 267 */     for (Map.Entry<String, HashedEntry> mapEntry : (Iterable<Map.Entry<String, HashedEntry>>)subHDir.map().entrySet()) {
/* 268 */       String name = mapEntry.getKey();
/* 269 */       Path path = subDir.resolve(name);
/*     */ 
/*     */       
/* 272 */       HashedEntry entry = mapEntry.getValue();
/* 273 */       HashedEntry.Type entryType = entry.getType();
/* 274 */       switch (entryType) {
/*     */         case FILE:
/* 276 */           updateState(IOHelper.toString(path), 0L, 0L);
/* 277 */           Files.delete(path);
/*     */           continue;
/*     */         case DIR:
/* 280 */           deleteExtraDir(path, (HashedDir)entry, (flag || entry.flag));
/*     */           continue;
/*     */       } 
/* 283 */       throw new AssertionError("Unsupported hashed entry type: " + entryType.name());
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 288 */     if (flag) {
/* 289 */       updateState(IOHelper.toString(subDir), 0L, 0L);
/* 290 */       Files.delete(subDir);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public UpdateRequestEvent request() throws Exception {
/* 296 */     Files.createDirectories(this.dir, (FileAttribute<?>[])new FileAttribute[0]);
/* 297 */     this.localDir = new HashedDir(this.dir, this.matcher, false, this.digest);
/*     */ 
/*     */     
/* 300 */     return (UpdateRequestEvent)super.request();
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void setStateCallback(State.Callback callback) {
/* 305 */     this.stateCallback = callback;
/*     */   }
/*     */   
/*     */   private void updateState(String filePath, long fileDownloaded, long fileSize) {
/* 309 */     if (this.stateCallback != null)
/* 310 */       this.stateCallback.call(new State(filePath, fileDownloaded, fileSize, this.totalDownloaded, this.totalSize, 
/* 311 */             Duration.between(this.startTime, Instant.now()))); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\reques\\update\UpdateRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */