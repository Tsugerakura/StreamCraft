/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Set;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SupportedCipherSuiteFilter
/*    */   implements CipherSuiteFilter
/*    */ {
/* 29 */   public static final SupportedCipherSuiteFilter INSTANCE = new SupportedCipherSuiteFilter();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String[] filterCipherSuites(Iterable<String> ciphers, List<String> defaultCiphers, Set<String> supportedCiphers) {
/*    */     List<String> newCiphers;
/* 36 */     ObjectUtil.checkNotNull(defaultCiphers, "defaultCiphers");
/* 37 */     ObjectUtil.checkNotNull(supportedCiphers, "supportedCiphers");
/*    */ 
/*    */     
/* 40 */     if (ciphers == null) {
/* 41 */       newCiphers = new ArrayList<String>(defaultCiphers.size());
/* 42 */       ciphers = defaultCiphers;
/*    */     } else {
/* 44 */       newCiphers = new ArrayList<String>(supportedCiphers.size());
/*    */     } 
/* 46 */     for (String c : ciphers) {
/* 47 */       if (c == null) {
/*    */         break;
/*    */       }
/* 50 */       if (supportedCiphers.contains(c)) {
/* 51 */         newCiphers.add(c);
/*    */       }
/*    */     } 
/* 54 */     return newCiphers.<String>toArray(new String[0]);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SupportedCipherSuiteFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */