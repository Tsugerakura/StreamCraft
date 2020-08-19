/*    */ package org.apache.http.impl.conn;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import java.net.UnknownHostException;
/*    */ import org.apache.http.conn.DnsResolver;
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
/*    */ public class SystemDefaultDnsResolver
/*    */   implements DnsResolver
/*    */ {
/* 41 */   public static final SystemDefaultDnsResolver INSTANCE = new SystemDefaultDnsResolver();
/*    */ 
/*    */   
/*    */   public InetAddress[] resolve(String host) throws UnknownHostException {
/* 45 */     return InetAddress.getAllByName(host);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\impl\conn\SystemDefaultDnsResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */