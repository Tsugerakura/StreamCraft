/*    */ package pro.gravit.launcher.modules;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Path;
/*    */ import java.util.List;
/*    */ import java.util.function.Predicate;
/*    */ import pro.gravit.utils.Version;
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface LauncherModulesManager
/*    */ {
/*    */   LauncherModule loadModule(LauncherModule paramLauncherModule);
/*    */   
/*    */   LauncherModule loadModule(Path paramPath) throws IOException;
/*    */   
/*    */   default boolean containsModule(String name) {
/* 18 */     return (getModule(name) != null);
/*    */   }
/*    */   LauncherModule getModule(String paramString);
/*    */   LauncherModule getCoreModule();
/*    */   default <T extends LauncherModule> boolean containsModule(Class<? extends T> clazz) {
/* 23 */     return (getModule(clazz) != null);
/*    */   }
/*    */   
/*    */   ClassLoader getModuleClassLoader();
/*    */   
/*    */   ModulesConfigManager getConfigManager();
/*    */   
/*    */   <T extends LauncherModule> T getModule(Class<? extends T> paramClass);
/*    */   
/*    */   <T> T getModuleByInterface(Class<T> paramClass);
/*    */   
/*    */   <T> List<T> getModulesByInterface(Class<T> paramClass);
/*    */   
/*    */   <T extends LauncherModule> T findModule(Class<? extends T> paramClass, Predicate<Version> paramPredicate);
/*    */   
/*    */   <T extends LauncherModule.Event> void invokeEvent(T paramT);
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\modules\LauncherModulesManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */