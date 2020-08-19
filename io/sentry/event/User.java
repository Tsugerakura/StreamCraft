/*    */ package io.sentry.event;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public class User
/*    */   implements Serializable
/*    */ {
/*    */   private final String id;
/*    */   private final String username;
/*    */   private final String ipAddress;
/*    */   private final String email;
/*    */   private final Map<String, Object> data;
/*    */   
/*    */   public User(String id, String username, String ipAddress, String email, Map<String, Object> data) {
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
/*    */   public User(String id, String username, String ipAddress, String email) {
/* 46 */     this(id, username, ipAddress, email, null);
/*    */   }
/*    */   
/*    */   public String getId() {
/* 50 */     return this.id;
/*    */   }
/*    */   
/*    */   public String getUsername() {
/* 54 */     return this.username;
/*    */   }
/*    */   
/*    */   public String getIpAddress() {
/* 58 */     return this.ipAddress;
/*    */   }
/*    */   
/*    */   public String getEmail() {
/* 62 */     return this.email;
/*    */   }
/*    */   
/*    */   public Map<String, Object> getData() {
/* 66 */     return this.data;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\User.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */