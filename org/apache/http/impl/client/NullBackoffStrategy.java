/*    */ package org.apache.http.impl.client;
/*    */ 
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.client.ConnectionBackoffStrategy;
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
/*    */ public class NullBackoffStrategy
/*    */   implements ConnectionBackoffStrategy
/*    */ {
/*    */   public boolean shouldBackoff(Throwable t) {
/* 42 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean shouldBackoff(HttpResponse resp) {
/* 47 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\impl\client\NullBackoffStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */