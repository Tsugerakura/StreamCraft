/*     */ package pro.gravit.launcher.modules.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.FileVisitResult;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.SimpleFileVisitor;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.jar.JarFile;
/*     */ import pro.gravit.launcher.managers.SimpleModulesConfigManager;
/*     */ import pro.gravit.launcher.modules.LauncherInitContext;
/*     */ import pro.gravit.launcher.modules.LauncherModule;
/*     */ import pro.gravit.launcher.modules.LauncherModuleInfo;
/*     */ import pro.gravit.launcher.modules.LauncherModulesManager;
/*     */ import pro.gravit.launcher.modules.ModulesConfigManager;
/*     */ import pro.gravit.utils.PublicURLClassLoader;
/*     */ import pro.gravit.utils.Version;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ 
/*     */ public class SimpleModuleManager
/*     */   implements LauncherModulesManager {
/*  28 */   protected final List<LauncherModule> modules = new ArrayList<>();
/*  29 */   protected final List<String> moduleNames = new ArrayList<>();
/*     */   
/*     */   protected final SimpleModuleContext context;
/*     */   protected final ModulesConfigManager modulesConfigManager;
/*     */   protected final Path modulesDir;
/*     */   protected LauncherInitContext initContext;
/*  35 */   protected PublicURLClassLoader classLoader = new PublicURLClassLoader(new java.net.URL[0]);
/*     */   
/*     */   protected final class ModulesVisitor
/*     */     extends SimpleFileVisitor<Path>
/*     */   {
/*     */     private ModulesVisitor() {}
/*     */     
/*     */     public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/*  43 */       if (file.toFile().getName().endsWith(".jar"))
/*  44 */         SimpleModuleManager.this.loadModule(file); 
/*  45 */       return super.visitFile(file, attrs);
/*     */     }
/*     */   }
/*     */   
/*     */   public void autoload() throws IOException {
/*  50 */     autoload(this.modulesDir);
/*     */   }
/*     */   
/*     */   public void autoload(Path dir) throws IOException {
/*  54 */     if (Files.notExists(dir, new java.nio.file.LinkOption[0])) { Files.createDirectory(dir, (FileAttribute<?>[])new FileAttribute[0]); }
/*     */     else
/*  56 */     { IOHelper.walk(dir, new ModulesVisitor(), true); }
/*     */   
/*     */   }
/*     */   
/*     */   public void initModules(LauncherInitContext initContext) {
/*  61 */     boolean isAnyModuleLoad = true;
/*  62 */     this.modules.sort((m1, m2) -> {
/*     */           int priority1 = (m1.getModuleInfo()).priority;
/*     */           int priority2 = (m2.getModuleInfo()).priority;
/*     */           return Integer.compare(priority1, priority2);
/*     */         });
/*  67 */     while (isAnyModuleLoad) {
/*     */       
/*  69 */       isAnyModuleLoad = false;
/*  70 */       for (LauncherModule module : this.modules) {
/*     */         
/*  72 */         if (module.getInitStatus().equals(LauncherModule.InitStatus.INIT_WAIT) && 
/*  73 */           checkDepend(module)) {
/*     */           
/*  75 */           isAnyModuleLoad = true;
/*  76 */           module.setInitStatus(LauncherModule.InitStatus.INIT);
/*  77 */           module.init(initContext);
/*  78 */           module.setInitStatus(LauncherModule.InitStatus.FINISH);
/*     */         } 
/*     */       } 
/*     */     } 
/*  82 */     for (LauncherModule module : this.modules) {
/*     */       
/*  84 */       if (module.getInitStatus().equals(LauncherModule.InitStatus.INIT_WAIT)) {
/*     */         
/*  86 */         LauncherModuleInfo info = module.getModuleInfo();
/*  87 */         LogHelper.warning("Module %s required %s. Cyclic dependencies?", new Object[] { info.name, Arrays.toString((Object[])info.dependencies) });
/*  88 */         module.setInitStatus(LauncherModule.InitStatus.INIT);
/*  89 */         module.init(initContext);
/*  90 */         module.setInitStatus(LauncherModule.InitStatus.FINISH); continue;
/*     */       } 
/*  92 */       if (module.getInitStatus().equals(LauncherModule.InitStatus.PRE_INIT_WAIT)) {
/*     */         
/*  94 */         LauncherModuleInfo info = module.getModuleInfo();
/*  95 */         LogHelper.error("Module %s skip pre-init phase. This module NOT finish loading", new Object[] { info.name, Arrays.toString((Object[])info.dependencies) });
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean checkDepend(LauncherModule module) {
/* 102 */     LauncherModuleInfo info = module.getModuleInfo();
/* 103 */     for (String dep : info.dependencies) {
/*     */       
/* 105 */       LauncherModule depModule = getModule(dep);
/* 106 */       if (depModule == null) throw new RuntimeException(String.format("Module %s required %s. %s not found", new Object[] { info.name, dep, dep })); 
/* 107 */       if (!depModule.getInitStatus().equals(LauncherModule.InitStatus.FINISH)) return false; 
/*     */     } 
/* 109 */     return true;
/*     */   }
/*     */   
/*     */   public SimpleModuleManager(Path modulesDir, Path configDir) {
/* 113 */     this.modulesConfigManager = (ModulesConfigManager)new SimpleModulesConfigManager(configDir);
/* 114 */     this.context = new SimpleModuleContext(this, this.modulesConfigManager);
/* 115 */     this.modulesDir = modulesDir;
/*     */   }
/*     */ 
/*     */   
/*     */   public LauncherModule loadModule(LauncherModule module) {
/* 120 */     if (this.modules.contains(module)) return module; 
/* 121 */     this.modules.add(module);
/* 122 */     LauncherModuleInfo info = module.getModuleInfo();
/* 123 */     this.moduleNames.add(info.name);
/* 124 */     module.setContext(this.context);
/* 125 */     module.preInit();
/* 126 */     if (this.initContext != null) {
/*     */       
/* 128 */       module.setInitStatus(LauncherModule.InitStatus.INIT);
/* 129 */       module.init(this.initContext);
/* 130 */       module.setInitStatus(LauncherModule.InitStatus.FINISH);
/*     */     } 
/* 132 */     return module;
/*     */   }
/*     */ 
/*     */   
/*     */   public LauncherModule loadModule(Path file) throws IOException {
/* 137 */     try (JarFile f = new JarFile(file.toFile())) {
/* 138 */       String moduleClass = f.getManifest().getMainAttributes().getValue("Module-Main-Class");
/* 139 */       if (moduleClass == null) {
/*     */         
/* 141 */         LogHelper.error("In module %s Module-Main-Class not found", new Object[] { file.toString() });
/* 142 */         return null;
/*     */       } 
/* 144 */       this.classLoader.addURL(file.toUri().toURL());
/* 145 */       LauncherModule module = (LauncherModule)Class.forName(moduleClass, true, (ClassLoader)this.classLoader).newInstance();
/* 146 */       loadModule(module);
/* 147 */       return module;
/* 148 */     } catch (ClassNotFoundException|IllegalAccessException|InstantiationException e) {
/* 149 */       LogHelper.error(e);
/* 150 */       LogHelper.error("In module %s Module-Main-Class incorrect", new Object[] { file.toString() });
/* 151 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public LauncherModule getModule(String name) {
/* 157 */     for (LauncherModule module : this.modules) {
/*     */       
/* 159 */       LauncherModuleInfo info = module.getModuleInfo();
/* 160 */       if (info.name.equals(name) || (info.providers.length > 0 && Arrays.<String>asList(info.providers).contains(name))) return module; 
/*     */     } 
/* 162 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public LauncherModule getCoreModule() {
/* 167 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public ClassLoader getModuleClassLoader() {
/* 172 */     return (ClassLoader)this.classLoader;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends LauncherModule> T getModule(Class<? extends T> clazz) {
/* 178 */     for (LauncherModule module : this.modules) {
/*     */       
/* 180 */       if (clazz.isAssignableFrom(module.getClass())) return (T)module; 
/*     */     } 
/* 182 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getModuleByInterface(Class<T> clazz) {
/* 188 */     for (LauncherModule module : this.modules) {
/*     */       
/* 190 */       if (clazz.isAssignableFrom(module.getClass())) return (T)module; 
/*     */     } 
/* 192 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> List<T> getModulesByInterface(Class<T> clazz) {
/* 198 */     List<T> list = new ArrayList<>();
/* 199 */     for (LauncherModule module : this.modules) {
/*     */       
/* 201 */       if (clazz.isAssignableFrom(module.getClass())) list.add((T)module); 
/*     */     } 
/* 203 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends LauncherModule> T findModule(Class<? extends T> clazz, Predicate<Version> versionPredicate) {
/* 209 */     for (LauncherModule module : this.modules) {
/*     */       
/* 211 */       LauncherModuleInfo info = module.getModuleInfo();
/* 212 */       if (versionPredicate.test(info.version) && 
/* 213 */         clazz.isAssignableFrom(module.getClass())) return (T)module; 
/*     */     } 
/* 215 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public <T extends LauncherModule.Event> void invokeEvent(T event) {
/* 220 */     for (LauncherModule module : this.modules) {
/*     */       
/* 222 */       module.callEvent((LauncherModule.Event)event);
/* 223 */       if (event.isCancel())
/*     */         return; 
/*     */     } 
/*     */   }
/*     */   
/*     */   public ModulesConfigManager getConfigManager() {
/* 229 */     return this.modulesConfigManager;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\modules\impl\SimpleModuleManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */