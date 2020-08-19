/*     */ package pro.gravit.launcher.client;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import pro.gravit.launcher.NewLauncherSettings;
/*     */ import pro.gravit.launcher.downloader.ListDownloader;
/*     */ import pro.gravit.launcher.events.request.UpdateRequestEvent;
/*     */ import pro.gravit.launcher.hasher.HashedDir;
/*     */ import pro.gravit.launcher.hasher.HashedEntry;
/*     */ import pro.gravit.launcher.hasher.HashedFile;
/*     */ import pro.gravit.launcher.managers.SettingsManager;
/*     */ import pro.gravit.launcher.request.update.UpdateRequest;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LauncherUpdateController
/*     */   implements UpdateRequest.UpdateController
/*     */ {
/*     */   public void preUpdate(UpdateRequest request, UpdateRequestEvent e) {}
/*     */   
/*     */   public void preDiff(UpdateRequest request, UpdateRequestEvent e) {}
/*     */   
/*     */   public void postDiff(UpdateRequest request, UpdateRequestEvent e, HashedDir.Diff diff) throws IOException {
/*  36 */     if (e.zip && e.fullDownload)
/*  37 */       return;  if (SettingsManager.settings.featureStore) {
/*  38 */       LogHelper.info("Enabled HStore feature. Find");
/*  39 */       AtomicReference<NewLauncherSettings.HashedStoreEntry> lastEn = new AtomicReference<>(null);
/*     */       
/*  41 */       diff.mismatch.walk(File.separator, (path, name, entry) -> {
/*     */             if (entry.getType() == HashedEntry.Type.DIR) {
/*     */               Files.createDirectories(request.getDir().resolve(path), (FileAttribute<?>[])new FileAttribute[0]);
/*     */               return HashedDir.WalkAction.CONTINUE;
/*     */             } 
/*     */             HashedFile file = (HashedFile)entry;
/*     */             Path ret = null;
/*     */             if (lastEn.get() == null) {
/*     */               for (NewLauncherSettings.HashedStoreEntry en : SettingsManager.settings.lastHDirs) {
/*     */                 ret = tryFind(en, file);
/*     */                 if (ret != null) {
/*     */                   lastEn.set(en);
/*     */                   break;
/*     */                 } 
/*     */               } 
/*     */             } else {
/*     */               ret = tryFind(lastEn.get(), file);
/*     */             } 
/*     */             if (ret == null) {
/*     */               for (NewLauncherSettings.HashedStoreEntry en : SettingsManager.settings.lastHDirs) {
/*     */                 ret = tryFind(en, file);
/*     */                 if (ret != null) {
/*     */                   lastEn.set(en);
/*     */                   break;
/*     */                 } 
/*     */               } 
/*     */             }
/*     */             if (ret != null) {
/*     */               Path source = request.getDir().resolve(path);
/*     */               if (LogHelper.isDebugEnabled()) {
/*     */                 LogHelper.debug("Copy file %s to %s", new Object[] { ret.toAbsolutePath().toString(), source.toAbsolutePath().toString() });
/*     */               }
/*     */               Files.deleteIfExists(source);
/*     */               Files.copy(ret, source, new java.nio.file.CopyOption[0]);
/*     */               try (InputStream input = IOHelper.newInput(ret)) {
/*     */                 IOHelper.transfer(input, source);
/*     */               } 
/*     */               entry.flag = true;
/*     */             } 
/*     */             return HashedDir.WalkAction.CONTINUE;
/*     */           });
/*     */     } 
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
/*     */   public Path tryFind(NewLauncherSettings.HashedStoreEntry en, HashedFile file) throws IOException {
/*  94 */     AtomicReference<Path> ret = new AtomicReference<>(null);
/*  95 */     en.hdir.walk(File.separator, (path, name, entry) -> {
/*     */           if (entry.getType() == HashedEntry.Type.DIR) {
/*     */             return HashedDir.WalkAction.CONTINUE;
/*     */           }
/*     */           HashedFile tfile = (HashedFile)entry;
/*     */           if (tfile.isSame(file)) {
/*     */             if (LogHelper.isDevEnabled())
/*     */               LogHelper.dev("[DIR:%s] Found file %s in %s", new Object[] { en.name, name, path }); 
/*     */             Path tdir = Paths.get(en.fullPath, new String[0]).resolve(path);
/*     */             try {
/*     */               if (tfile.isSame(tdir, true)) {
/*     */                 if (LogHelper.isDevEnabled())
/*     */                   LogHelper.dev("[DIR:%s] Confirmed file %s in %s", new Object[] { en.name, name, path }); 
/*     */                 ret.set(tdir);
/*     */                 return HashedDir.WalkAction.STOP;
/*     */               } 
/* 111 */             } catch (IOException e) {
/*     */               LogHelper.error("Check file error %s %s", new Object[] { e.getClass().getName(), e.getMessage() });
/*     */             } 
/*     */           } 
/*     */           return HashedDir.WalkAction.CONTINUE;
/*     */         });
/* 117 */     return ret.get();
/*     */   }
/*     */   
/*     */   public void preDownload(UpdateRequest request, UpdateRequestEvent e, List<ListDownloader.DownloadTask> adds) {}
/*     */   
/*     */   public void postDownload(UpdateRequest request, UpdateRequestEvent e) {}
/*     */   
/*     */   public void postUpdate(UpdateRequest request, UpdateRequestEvent e) {}
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\client\LauncherUpdateController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */