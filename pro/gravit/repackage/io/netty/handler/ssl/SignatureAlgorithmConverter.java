/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class SignatureAlgorithmConverter
/*    */ {
/* 42 */   private static final Pattern PATTERN = Pattern.compile("(?:(^[a-zA-Z].+)With(.+)Encryption$)|(?:(^[a-zA-Z].+)(?:_with_|-with-|_pkcs1_|_pss_rsae_)(.+$))|(?:(^[a-zA-Z].+)_(.+$))");
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
/*    */   static String toJavaName(String opensslName) {
/* 55 */     if (opensslName == null) {
/* 56 */       return null;
/*    */     }
/* 58 */     Matcher matcher = PATTERN.matcher(opensslName);
/* 59 */     if (matcher.matches()) {
/* 60 */       String group1 = matcher.group(1);
/* 61 */       if (group1 != null) {
/* 62 */         return group1.toUpperCase(Locale.ROOT) + "with" + matcher.group(2).toUpperCase(Locale.ROOT);
/*    */       }
/* 64 */       if (matcher.group(3) != null) {
/* 65 */         return matcher.group(4).toUpperCase(Locale.ROOT) + "with" + matcher.group(3).toUpperCase(Locale.ROOT);
/*    */       }
/*    */       
/* 68 */       if (matcher.group(5) != null) {
/* 69 */         return matcher.group(6).toUpperCase(Locale.ROOT) + "with" + matcher.group(5).toUpperCase(Locale.ROOT);
/*    */       }
/*    */     } 
/* 72 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\SignatureAlgorithmConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */