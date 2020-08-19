/*     */ package pro.gravit.launcher.profiles.optional;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import pro.gravit.launcher.LauncherAPI;
/*     */ import pro.gravit.launcher.serialize.HInput;
/*     */ import pro.gravit.launcher.serialize.HOutput;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ 
/*     */ public class OptionalFile {
/*     */   @LauncherAPI
/*     */   public String[] list;
/*     */   @LauncherAPI
/*     */   public OptionalType type;
/*     */   @LauncherAPI
/*     */   public boolean mark;
/*     */   @LauncherAPI
/*     */   public boolean visible = true;
/*     */   @LauncherAPI
/*     */   public String name;
/*     */   @LauncherAPI
/*     */   public String info;
/*     */   @LauncherAPI
/*     */   public OptionalDepend[] dependenciesFile;
/*     */   @LauncherAPI
/*     */   public OptionalDepend[] conflictFile;
/*     */   @LauncherAPI
/*     */   public transient OptionalFile[] dependencies;
/*     */   @LauncherAPI
/*     */   public transient OptionalFile[] conflict;
/*     */   @LauncherAPI
/*  33 */   public int subTreeLevel = 1;
/*     */   @LauncherAPI
/*     */   public boolean isPreset;
/*     */   @LauncherAPI
/*  37 */   public long permissions = 0L;
/*     */   
/*     */   @LauncherAPI
/*     */   public transient Set<OptionalFile> dependenciesCount;
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  44 */     if (this == o) return true; 
/*  45 */     if (o == null || getClass() != o.getClass()) return false; 
/*  46 */     OptionalFile that = (OptionalFile)o;
/*  47 */     return Objects.equals(this.name, that.name);
/*     */   }
/*     */   
/*     */   public int hashCode() {
/*  51 */     return Objects.hash(new Object[] { this.name });
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public OptionalType getType() {
/*  56 */     return OptionalType.FILE;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String getName() {
/*  61 */     return this.name;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public boolean isVisible() {
/*  66 */     return this.visible;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public boolean isMark() {
/*  71 */     return this.mark;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public long getPermissions() {
/*  76 */     return this.permissions;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeType(HOutput output) throws IOException {
/*  81 */     switch (this.type) {
/*     */       
/*     */       case FILE:
/*  84 */         output.writeInt(1);
/*     */         return;
/*     */       case CLASSPATH:
/*  87 */         output.writeInt(2);
/*     */         return;
/*     */       case JVMARGS:
/*  90 */         output.writeInt(3);
/*     */         return;
/*     */       case CLIENTARGS:
/*  93 */         output.writeInt(4);
/*     */         return;
/*     */     } 
/*  96 */     output.writeInt(5);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public static OptionalType readType(HInput input) throws IOException {
/* 103 */     int t = input.readInt();
/*     */     
/* 105 */     switch (t)
/*     */     { case 1:
/* 107 */         type = OptionalType.FILE;
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
/* 123 */         return type;case 2: type = OptionalType.CLASSPATH; return type;case 3: type = OptionalType.JVMARGS; return type;case 4: type = OptionalType.CLIENTARGS; return type; }  LogHelper.error("readType failed. Read int %d", new Object[] { Integer.valueOf(t) }); OptionalType type = OptionalType.FILE; return type;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\profiles\optional\OptionalFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */