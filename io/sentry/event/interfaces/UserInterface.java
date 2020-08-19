/*    */ package io.sentry.event.interfaces;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.Objects;
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
/*    */ public class UserInterface
/*    */   implements SentryInterface
/*    */ {
/*    */   public static final String USER_INTERFACE = "sentry.interfaces.User";
/*    */   private final String id;
/*    */   private final String username;
/*    */   private final String ipAddress;
/*    */   private final String email;
/*    */   private final Map<String, Object> data;
/*    */   
/*    */   public UserInterface(String id, String username, String ipAddress, String email, Map<String, Object> data) {
/* 30 */     this.id = id;
/* 31 */     this.username = username;
/* 32 */     this.ipAddress = ipAddress;
/* 33 */     this.email = email;
/* 34 */     this.data = data;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UserInterface(String id, String username, String ipAddress, String email) {
/* 46 */     this(id, username, ipAddress, email, null);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getInterfaceName() {
/* 51 */     return "sentry.interfaces.User";
/*    */   }
/*    */   
/*    */   public String getId() {
/* 55 */     return this.id;
/*    */   }
/*    */   
/*    */   public String getUsername() {
/* 59 */     return this.username;
/*    */   }
/*    */   
/*    */   public String getIpAddress() {
/* 63 */     return this.ipAddress;
/*    */   }
/*    */   
/*    */   public String getEmail() {
/* 67 */     return this.email;
/*    */   }
/*    */   
/*    */   public Map<String, Object> getData() {
/* 71 */     return this.data;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 76 */     if (this == o) {
/* 77 */       return true;
/*    */     }
/* 79 */     if (o == null || getClass() != o.getClass()) {
/* 80 */       return false;
/*    */     }
/* 82 */     UserInterface that = (UserInterface)o;
/* 83 */     return (Objects.equals(this.id, that.id) && 
/* 84 */       Objects.equals(this.username, that.username) && 
/* 85 */       Objects.equals(this.ipAddress, that.ipAddress) && 
/* 86 */       Objects.equals(this.email, that.email) && 
/* 87 */       Objects.equals(this.data, that.data));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 92 */     return Objects.hash(new Object[] { this.id, this.username, this.ipAddress, this.email, this.data });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 97 */     return "UserInterface{id='" + this.id + '\'' + ", username='" + this.username + '\'' + ", ipAddress='" + this.ipAddress + '\'' + ", email='" + this.email + '\'' + ", data=" + this.data + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\interfaces\UserInterface.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */