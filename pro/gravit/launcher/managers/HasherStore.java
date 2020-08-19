/*    */ package pro.gravit.launcher.managers;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Files;
/*    */ import java.nio.file.Path;
/*    */ import java.nio.file.attribute.FileAttribute;
/*    */ import java.util.Collection;
/*    */ import java.util.LinkedList;
/*    */ import java.util.Map;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.hasher.FileNameMatcher;
/*    */ import pro.gravit.launcher.hasher.HashedDir;
/*    */ import pro.gravit.launcher.hasher.HashedEntry;
/*    */ import pro.gravit.launcher.profiles.ClientProfile;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class HasherStore {
/*    */   public Map<String, HasherStoreEnity> store;
/*    */   
/*    */   public class HasherStoreEnity {
/*    */     @LauncherAPI
/*    */     public HashedDir hdir;
/*    */     @LauncherAPI
/*    */     public Path dir;
/*    */     @LauncherAPI
/*    */     public Collection<String> shared;
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public void addProfileUpdateDir(ClientProfile profile, Path dir, HashedDir hdir) {
/* 32 */     HasherStoreEnity e = new HasherStoreEnity();
/* 33 */     e.hdir = hdir;
/* 34 */     e.dir = dir;
/* 35 */     e.shared = profile.getShared();
/*    */     
/* 37 */     this.store.put(profile.getTitle(), e);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public void copyCompareFilesTo(String name, Path targetDir, HashedDir targetHDir, String[] shared) {
/* 42 */     this.store.forEach((key, e) -> {
/*    */           if (key.equals(name))
/*    */             return; 
/*    */           FileNameMatcher nm = new FileNameMatcher(shared, null, null);
/*    */           HashedDir compare = targetHDir.sideCompare(e.hdir, nm, new LinkedList(), true);
/*    */           compare.map().forEach(());
/*    */         });
/*    */   }
/*    */   @LauncherAPI
/*    */   public void recurseCopy(String filename, HashedEntry entry, String name, Path targetDir, Path sourceDir) {
/* 52 */     if (!IOHelper.isDir(targetDir)) {
/*    */       try {
/* 54 */         Files.createDirectories(targetDir, (FileAttribute<?>[])new FileAttribute[0]);
/* 55 */       } catch (IOException e1) {
/* 56 */         LogHelper.error(e1);
/*    */       } 
/*    */     }
/* 59 */     if (entry.getType().equals(HashedEntry.Type.DIR)) {
/* 60 */       ((HashedDir)entry).map().forEach((arg1, arg2) -> recurseCopy(arg1, arg2, name, targetDir.resolve(filename), sourceDir.resolve(filename)));
/* 61 */     } else if (entry.getType().equals(HashedEntry.Type.FILE)) {
/*    */       try {
/* 63 */         IOHelper.copy(sourceDir.resolve(filename), targetDir.resolve(filename));
/* 64 */       } catch (IOException e) {
/* 65 */         LogHelper.error(e);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\managers\HasherStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */