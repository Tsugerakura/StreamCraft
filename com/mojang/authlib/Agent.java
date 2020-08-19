/*    */ package com.mojang.authlib;
/*    */ 
/*    */ public class Agent {
/*  4 */   public static final Agent MINECRAFT = new Agent("Minecraft", 1);
/*  5 */   public static final Agent SCROLLS = new Agent("Scrolls", 1);
/*    */   
/*    */   private final String name;
/*    */   private final int version;
/*    */   
/*    */   public Agent(String name, int version) {
/* 11 */     this.name = name;
/* 12 */     this.version = version;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 16 */     return this.name;
/*    */   }
/*    */   
/*    */   public int getVersion() {
/* 20 */     return this.version;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 25 */     return "Agent{name='" + this.name + '\'' + ", version=" + this.version + '}';
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\Agent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */