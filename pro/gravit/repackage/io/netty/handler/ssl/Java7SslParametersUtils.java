/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ import java.security.AlgorithmConstraints;
/*    */ import javax.net.ssl.SSLParameters;
/*    */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
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
/*    */ final class Java7SslParametersUtils
/*    */ {
/*    */   @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*    */   static void setAlgorithmConstraints(SSLParameters sslParameters, Object algorithmConstraints) {
/* 36 */     sslParameters.setAlgorithmConstraints((AlgorithmConstraints)algorithmConstraints);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\Java7SslParametersUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */