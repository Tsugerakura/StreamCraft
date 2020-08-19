/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ import java.security.KeyStore;
/*    */ import javax.net.ssl.KeyManager;
/*    */ import javax.net.ssl.ManagerFactoryParameters;
/*    */ import pro.gravit.repackage.io.netty.handler.ssl.util.SimpleKeyManagerFactory;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ final class KeyManagerFactoryWrapper
/*    */   extends SimpleKeyManagerFactory
/*    */ {
/*    */   private final KeyManager km;
/*    */   
/*    */   KeyManagerFactoryWrapper(KeyManager km) {
/* 30 */     this.km = (KeyManager)ObjectUtil.checkNotNull(km, "km");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void engineInit(KeyStore keyStore, char[] var2) throws Exception {}
/*    */ 
/*    */   
/*    */   protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {}
/*    */ 
/*    */   
/*    */   protected KeyManager[] engineGetKeyManagers() {
/* 42 */     return new KeyManager[] { this.km };
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\KeyManagerFactoryWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */