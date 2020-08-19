/*    */ package pro.gravit.repackage.io.netty.resolver;
/*    */ 
/*    */ import java.net.Inet4Address;
/*    */ import java.net.Inet6Address;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
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
/*    */ public final class HostsFileEntries
/*    */ {
/* 35 */   static final HostsFileEntries EMPTY = new HostsFileEntries(
/*    */       
/* 37 */       Collections.emptyMap(), 
/* 38 */       Collections.emptyMap());
/*    */   
/*    */   private final Map<String, Inet4Address> inet4Entries;
/*    */   private final Map<String, Inet6Address> inet6Entries;
/*    */   
/*    */   public HostsFileEntries(Map<String, Inet4Address> inet4Entries, Map<String, Inet6Address> inet6Entries) {
/* 44 */     this.inet4Entries = Collections.unmodifiableMap(new HashMap<String, Inet4Address>(inet4Entries));
/* 45 */     this.inet6Entries = Collections.unmodifiableMap(new HashMap<String, Inet6Address>(inet6Entries));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<String, Inet4Address> inet4Entries() {
/* 53 */     return this.inet4Entries;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<String, Inet6Address> inet6Entries() {
/* 61 */     return this.inet6Entries;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\resolver\HostsFileEntries.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */