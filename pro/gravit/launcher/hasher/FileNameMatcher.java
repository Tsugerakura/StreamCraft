/*    */ package pro.gravit.launcher.hasher;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ 
/*    */ public final class FileNameMatcher
/*    */ {
/*  8 */   private static final String[] NO_ENTRIES = new String[0];
/*    */   private final String[] update;
/*    */   
/*    */   private static boolean anyMatch(String[] entries, Collection<String> path) {
/* 12 */     String jpath = String.join("/", (Iterable)path);
/* 13 */     for (String e : entries) {
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 27 */       if (jpath.startsWith(e)) return true; 
/*    */     } 
/* 29 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private final String[] verify;
/*    */   
/*    */   private final String[] exclusions;
/*    */ 
/*    */   
/*    */   @LauncherAPI
/*    */   public FileNameMatcher(String[] update, String[] verify, String[] exclusions) {
/* 41 */     this.update = update;
/* 42 */     this.verify = verify;
/* 43 */     this.exclusions = exclusions;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean shouldUpdate(Collection<String> path) {
/* 48 */     return ((anyMatch(this.update, path) || anyMatch(this.verify, path)) && !anyMatch(this.exclusions, path));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean shouldVerify(Collection<String> path) {
/* 53 */     return (anyMatch(this.verify, path) && !anyMatch(this.exclusions, path));
/*    */   }
/*    */   
/*    */   public FileNameMatcher verifyOnly() {
/* 57 */     return new FileNameMatcher(NO_ENTRIES, this.verify, this.exclusions);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\hasher\FileNameMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */