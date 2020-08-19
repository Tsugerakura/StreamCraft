/*    */ package pro.gravit.repackage.io.netty.channel.local;
/*    */ 
/*    */ import java.net.SocketAddress;
/*    */ import pro.gravit.repackage.io.netty.channel.Channel;
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
/*    */ 
/*    */ public final class LocalAddress
/*    */   extends SocketAddress
/*    */   implements Comparable<LocalAddress>
/*    */ {
/*    */   private static final long serialVersionUID = 4644331421130916435L;
/* 31 */   public static final LocalAddress ANY = new LocalAddress("ANY");
/*    */ 
/*    */   
/*    */   private final String id;
/*    */ 
/*    */   
/*    */   private final String strVal;
/*    */ 
/*    */ 
/*    */   
/*    */   LocalAddress(Channel channel) {
/* 42 */     StringBuilder buf = new StringBuilder(16);
/* 43 */     buf.append("local:E");
/* 44 */     buf.append(Long.toHexString(channel.hashCode() & 0xFFFFFFFFL | 0x100000000L));
/* 45 */     buf.setCharAt(7, ':');
/* 46 */     this.id = buf.substring(6);
/* 47 */     this.strVal = buf.toString();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public LocalAddress(String id) {
/* 54 */     ObjectUtil.checkNotNull(id, "id");
/* 55 */     id = id.trim().toLowerCase();
/* 56 */     if (id.isEmpty()) {
/* 57 */       throw new IllegalArgumentException("empty id");
/*    */     }
/* 59 */     this.id = id;
/* 60 */     this.strVal = "local:" + id;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String id() {
/* 67 */     return this.id;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 72 */     return this.id.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 77 */     if (!(o instanceof LocalAddress)) {
/* 78 */       return false;
/*    */     }
/*    */     
/* 81 */     return this.id.equals(((LocalAddress)o).id);
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(LocalAddress o) {
/* 86 */     return this.id.compareTo(o.id);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 91 */     return this.strVal;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\local\LocalAddress.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */