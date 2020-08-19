/*    */ package io.sentry.event;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Set;
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
/*    */ public class Sdk
/*    */   implements Serializable
/*    */ {
/*    */   private String name;
/*    */   private String version;
/*    */   private Set<String> integrations;
/*    */   
/*    */   public Sdk(String name, String version, Set<String> integrations) {
/* 31 */     this.name = name;
/* 32 */     this.version = version;
/* 33 */     this.integrations = integrations;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 37 */     return this.name;
/*    */   }
/*    */   
/*    */   public String getVersion() {
/* 41 */     return this.version;
/*    */   }
/*    */   
/*    */   public Set<String> getIntegrations() {
/* 45 */     return this.integrations;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\Sdk.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */