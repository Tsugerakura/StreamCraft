/*    */ package org.apache.http.impl.client;
/*    */ 
/*    */ import org.apache.http.conn.HttpClientConnectionManager;
/*    */ import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpClients
/*    */ {
/*    */   public static HttpClientBuilder custom() {
/* 48 */     return HttpClientBuilder.create();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CloseableHttpClient createDefault() {
/* 56 */     return HttpClientBuilder.create().build();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CloseableHttpClient createSystem() {
/* 64 */     return HttpClientBuilder.create().useSystemProperties().build();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CloseableHttpClient createMinimal() {
/* 72 */     return new MinimalHttpClient((HttpClientConnectionManager)new PoolingHttpClientConnectionManager());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static CloseableHttpClient createMinimal(HttpClientConnectionManager connManager) {
/* 80 */     return new MinimalHttpClient(connManager);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\impl\client\HttpClients.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */