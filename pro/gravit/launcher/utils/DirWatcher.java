/*     */ package pro.gravit.launcher.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.FileVisitResult;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.SimpleFileVisitor;
/*     */ import java.nio.file.StandardWatchEventKinds;
/*     */ import java.nio.file.WatchEvent;
/*     */ import java.nio.file.WatchKey;
/*     */ import java.nio.file.WatchService;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.util.Deque;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Objects;
/*     */ import pro.gravit.launcher.LauncherAPI;
/*     */ import pro.gravit.launcher.hasher.FileNameMatcher;
/*     */ import pro.gravit.launcher.hasher.HashedDir;
/*     */ import pro.gravit.launcher.hasher.HashedEntry;
/*     */ import pro.gravit.launcher.hasher.HashedFile;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.JVMHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ 
/*     */ public final class DirWatcher
/*     */   implements Runnable, AutoCloseable
/*     */ {
/*     */   private final class RegisterFileVisitor
/*     */     extends SimpleFileVisitor<Path>
/*     */   {
/*     */     private RegisterFileVisitor() {}
/*     */     
/*     */     public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
/*  33 */       FileVisitResult result = super.preVisitDirectory(dir, attrs);
/*  34 */       if (DirWatcher.this.dir.equals(dir)) {
/*  35 */         dir.register(DirWatcher.this.service, DirWatcher.KINDS);
/*  36 */         return result;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  45 */       dir.register(DirWatcher.this.service, DirWatcher.KINDS);
/*  46 */       return result;
/*     */     }
/*     */   }
/*     */   
/*  50 */   public static final boolean FILE_TREE_SUPPORTED = (JVMHelper.OS_TYPE == JVMHelper.OS.MUSTDIE);
/*     */ 
/*     */   
/*  53 */   private static final WatchEvent.Kind<?>[] KINDS = new WatchEvent.Kind[] { StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE };
/*     */   private final Path dir;
/*     */   private final HashedDir hdir;
/*     */   
/*     */   private static void handleError(Throwable e) {
/*  58 */     LogHelper.error(e);
/*  59 */     NativeJVMHalt.haltA(-123);
/*     */   }
/*     */   private final FileNameMatcher matcher; private final WatchService service; private final boolean digest;
/*     */   private static Deque<String> toPath(Iterable<Path> path) {
/*  63 */     Deque<String> result = new LinkedList<>();
/*  64 */     for (Path pe : path)
/*  65 */       result.add(pe.toString()); 
/*  66 */     return result;
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
/*     */   @LauncherAPI
/*     */   public DirWatcher(Path dir, HashedDir hdir, FileNameMatcher matcher, boolean digest) throws IOException {
/*  81 */     this.dir = Objects.<Path>requireNonNull(dir, "dir");
/*  82 */     this.hdir = Objects.<HashedDir>requireNonNull(hdir, "hdir");
/*  83 */     this.matcher = matcher;
/*  84 */     this.digest = digest;
/*  85 */     this.service = dir.getFileSystem().newWatchService();
/*     */ 
/*     */     
/*  88 */     IOHelper.walk(dir, new RegisterFileVisitor(), true);
/*  89 */     LogHelper.subInfo("DirWatcher %s", new Object[] { dir.toString() });
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public void close() throws IOException {
/*  95 */     this.service.close();
/*     */   }
/*     */   
/*     */   private void processKey(WatchKey key) throws IOException {
/*  99 */     Path watchDir = (Path)key.watchable();
/* 100 */     for (WatchEvent<?> event : key.pollEvents()) {
/* 101 */       WatchEvent.Kind<?> kind = event.kind();
/* 102 */       if (kind.equals(StandardWatchEventKinds.OVERFLOW)) {
/* 103 */         if (Boolean.getBoolean("launcher.dirwatcher.ignoreOverflows"))
/*     */           continue; 
/* 105 */         throw new IOException("Overflow");
/*     */       } 
/*     */ 
/*     */       
/* 109 */       Path path = watchDir.resolve((Path)event.context());
/* 110 */       Deque<String> stringPath = toPath(this.dir.relativize(path));
/* 111 */       if (this.matcher != null && !this.matcher.shouldVerify(stringPath)) {
/*     */         continue;
/*     */       }
/* 114 */       if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
/* 115 */         HashedEntry entry = this.hdir.resolve(stringPath);
/* 116 */         if (entry != null && (entry.getType() != HashedEntry.Type.FILE || ((HashedFile)entry).isSame(path, this.digest))) {
/*     */           continue;
/*     */         }
/*     */       } 
/*     */       
/* 121 */       throw new SecurityException(String.format("Forbidden modification (%s, %d times): '%s'", new Object[] { kind, Integer.valueOf(event.count()), path }));
/*     */     } 
/* 123 */     key.reset();
/*     */   }
/*     */   
/*     */   private void processLoop() throws IOException, InterruptedException {
/* 127 */     LogHelper.debug("WatchService start processing");
/* 128 */     while (!Thread.interrupted())
/* 129 */       processKey(this.service.take()); 
/* 130 */     LogHelper.debug("WatchService closed");
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public void run() {
/*     */     try {
/* 137 */       processLoop();
/* 138 */     } catch (InterruptedException|java.nio.file.ClosedWatchServiceException ignored) {
/* 139 */       LogHelper.debug("WatchService closed 2");
/*     */     }
/* 141 */     catch (Throwable exc) {
/* 142 */       handleError(exc);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launche\\utils\DirWatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */