/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SNIHostName;
/*     */ import javax.net.ssl.SNIMatcher;
/*     */ import javax.net.ssl.SNIServerName;
/*     */ import javax.net.ssl.SSLParameters;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SuppressJava6Requirement;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @SuppressJava6Requirement(reason = "Usage guarded by java version check")
/*     */ final class Java8SslUtils
/*     */ {
/*     */   static List<String> getSniHostNames(SSLParameters sslParameters) {
/*  36 */     List<SNIServerName> names = sslParameters.getServerNames();
/*  37 */     if (names == null || names.isEmpty()) {
/*  38 */       return Collections.emptyList();
/*     */     }
/*  40 */     List<String> strings = new ArrayList<String>(names.size());
/*     */     
/*  42 */     for (SNIServerName serverName : names) {
/*  43 */       if (serverName instanceof SNIHostName) {
/*  44 */         strings.add(((SNIHostName)serverName).getAsciiName()); continue;
/*     */       } 
/*  46 */       throw new IllegalArgumentException("Only " + SNIHostName.class.getName() + " instances are supported, but found: " + serverName);
/*     */     } 
/*     */ 
/*     */     
/*  50 */     return strings;
/*     */   }
/*     */   
/*     */   static void setSniHostNames(SSLParameters sslParameters, List<String> names) {
/*  54 */     sslParameters.setServerNames(getSniHostNames(names));
/*     */   }
/*     */   
/*     */   static List getSniHostNames(List<String> names) {
/*  58 */     if (names == null || names.isEmpty()) {
/*  59 */       return Collections.emptyList();
/*     */     }
/*  61 */     List<SNIServerName> sniServerNames = new ArrayList<SNIServerName>(names.size());
/*  62 */     for (String name : names) {
/*  63 */       sniServerNames.add(new SNIHostName(name));
/*     */     }
/*  65 */     return sniServerNames;
/*     */   }
/*     */   
/*     */   static List getSniHostName(byte[] hostname) {
/*  69 */     if (hostname == null || hostname.length == 0) {
/*  70 */       return Collections.emptyList();
/*     */     }
/*  72 */     return Collections.singletonList(new SNIHostName(hostname));
/*     */   }
/*     */   
/*     */   static boolean getUseCipherSuitesOrder(SSLParameters sslParameters) {
/*  76 */     return sslParameters.getUseCipherSuitesOrder();
/*     */   }
/*     */   
/*     */   static void setUseCipherSuitesOrder(SSLParameters sslParameters, boolean useOrder) {
/*  80 */     sslParameters.setUseCipherSuitesOrder(useOrder);
/*     */   }
/*     */ 
/*     */   
/*     */   static void setSNIMatchers(SSLParameters sslParameters, Collection<?> matchers) {
/*  85 */     sslParameters.setSNIMatchers((Collection)matchers);
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean checkSniHostnameMatch(Collection<?> matchers, byte[] hostname) {
/*  90 */     if (matchers != null && !matchers.isEmpty()) {
/*  91 */       SNIHostName name = new SNIHostName(hostname);
/*  92 */       Iterator<SNIMatcher> matcherIt = (Iterator)matchers.iterator();
/*  93 */       while (matcherIt.hasNext()) {
/*  94 */         SNIMatcher matcher = matcherIt.next();
/*     */         
/*  96 */         if (matcher.getType() == 0 && matcher.matches(name)) {
/*  97 */           return true;
/*     */         }
/*     */       } 
/* 100 */       return false;
/*     */     } 
/* 102 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\Java8SslUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */