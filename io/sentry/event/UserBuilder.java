/*    */ package io.sentry.event;
/*    */ 
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
/*    */ public class UserBuilder
/*    */ {
/*    */   private String id;
/*    */   private String username;
/*    */   private String ipAddress;
/*    */   private String email;
/*    */   private Map<String, Object> data;
/*    */   
/*    */   public UserBuilder setId(String value) {
/* 23 */     this.id = value;
/* 24 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UserBuilder setUsername(String value) {
/* 34 */     this.username = value;
/* 35 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UserBuilder setIpAddress(String value) {
/* 45 */     this.ipAddress = value;
/* 46 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UserBuilder setEmail(String value) {
/* 56 */     this.email = value;
/* 57 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UserBuilder setData(Map<String, Object> value) {
/* 67 */     this.data = value;
/* 68 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UserBuilder withData(String name, Object value) {
/* 79 */     if (this.data == null) {
/* 80 */       this.data = new HashMap<>();
/*    */     }
/*    */     
/* 83 */     this.data.put(name, value);
/* 84 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public User build() {
/* 93 */     return new User(this.id, this.username, this.ipAddress, this.email, this.data);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\UserBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */