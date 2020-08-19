/*     */ package pro.gravit.launcher.modules;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public abstract class LauncherModule
/*     */ {
/*     */   private LauncherModulesContext context;
/*   9 */   private Map<Class<? extends Event>, EventHandler> eventMap = new HashMap<>();
/*     */   
/*     */   protected LauncherModulesManager modulesManager;
/*     */   protected final LauncherModuleInfo moduleInfo;
/*     */   protected ModulesConfigManager modulesConfigManager;
/*  14 */   protected InitStatus initStatus = InitStatus.CREATED;
/*     */   
/*     */   protected LauncherModule() {
/*  17 */     this.moduleInfo = new LauncherModuleInfo("UnknownModule");
/*     */   }
/*     */   
/*     */   protected LauncherModule(LauncherModuleInfo info) {
/*  21 */     this.moduleInfo = info;
/*     */   }
/*     */   
/*     */   public LauncherModuleInfo getModuleInfo() {
/*  25 */     return this.moduleInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum InitStatus
/*     */   {
/*  37 */     CREATED(false),
/*     */ 
/*     */ 
/*     */     
/*  41 */     PRE_INIT_WAIT(true),
/*     */ 
/*     */ 
/*     */     
/*  45 */     PRE_INIT(false),
/*     */ 
/*     */ 
/*     */     
/*  49 */     INIT_WAIT(true),
/*     */ 
/*     */ 
/*     */     
/*  53 */     INIT(false),
/*  54 */     FINISH(true);
/*     */     
/*     */     InitStatus(boolean b) {
/*  57 */       this.isAvailable = b;
/*     */     }
/*     */     private final boolean isAvailable;
/*     */     public boolean isAvailable() {
/*  61 */       return this.isAvailable;
/*     */     }
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface EventHandler<T extends Event>
/*     */   {
/*     */     void event(T param1T);
/*     */   }
/*     */   
/*     */   public static class Event
/*     */   {
/*     */     public boolean isCancel() {
/*  74 */       return this.cancel;
/*     */     }
/*     */     
/*     */     public Event cancel() {
/*  78 */       this.cancel = true;
/*  79 */       return this;
/*     */     }
/*     */     
/*     */     protected boolean cancel = false;
/*     */   }
/*     */   
/*     */   public InitStatus getInitStatus() {
/*  86 */     return this.initStatus;
/*     */   }
/*     */   
/*     */   public LauncherModule setInitStatus(InitStatus initStatus) {
/*  90 */     this.initStatus = initStatus;
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContext(LauncherModulesContext context) {
/* 101 */     if (this.context != null) throw new IllegalStateException("Module already set context"); 
/* 102 */     this.context = context;
/* 103 */     this.modulesManager = context.getModulesManager();
/* 104 */     this.modulesConfigManager = context.getModulesConfigManager();
/* 105 */     setInitStatus(InitStatus.PRE_INIT_WAIT);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void preInitAction() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LauncherModule preInit() {
/* 124 */     if (!this.initStatus.equals(InitStatus.PRE_INIT_WAIT)) throw new IllegalStateException("PreInit not allowed in current state"); 
/* 125 */     this.initStatus = InitStatus.PRE_INIT;
/* 126 */     preInitAction();
/* 127 */     this.initStatus = InitStatus.INIT_WAIT;
/* 128 */     return this;
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
/*     */   public abstract void init(LauncherInitContext paramLauncherInitContext);
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
/*     */   protected <T extends Event> boolean registerEvent(EventHandler<T> handle, Class<T> tClass) {
/* 157 */     this.eventMap.put(tClass, handle);
/* 158 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final <T extends Event> void callEvent(T event) {
/* 169 */     Class<? extends Event> tClass = (Class)event.getClass();
/* 170 */     for (Map.Entry<Class<? extends Event>, EventHandler> e : this.eventMap.entrySet()) {
/*     */ 
/*     */       
/* 173 */       if (((Class)e.getKey()).isAssignableFrom(tClass)) {
/*     */         
/* 175 */         ((EventHandler<T>)e.getValue()).event(event);
/* 176 */         if (event.isCancel())
/*     */           return; 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\modules\LauncherModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */