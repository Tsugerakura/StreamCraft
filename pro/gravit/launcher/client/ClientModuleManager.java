/*    */ package pro.gravit.launcher.client;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.file.Path;
/*    */ import pro.gravit.launcher.modules.LauncherModule;
/*    */ import pro.gravit.launcher.modules.impl.SimpleModuleManager;
/*    */ 
/*    */ public class ClientModuleManager
/*    */   extends SimpleModuleManager {
/*    */   public ClientModuleManager() {
/* 11 */     super(null, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public void autoload() throws IOException {
/* 16 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public void autoload(Path dir) throws IOException {
/* 21 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   
/*    */   public LauncherModule loadModule(Path file) throws IOException {
/* 26 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\client\ClientModuleManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */