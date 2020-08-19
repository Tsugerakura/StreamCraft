/*    */ package io.sentry.jvmti;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Arrays;
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
/*    */ public final class Frame
/*    */ {
/*    */   private Method method;
/*    */   private final LocalVariable[] locals;
/*    */   
/*    */   public Frame(Method method, LocalVariable[] locals) {
/* 29 */     this.method = method;
/* 30 */     this.locals = locals;
/*    */   }
/*    */   
/*    */   public Method getMethod() {
/* 34 */     return this.method;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<String, Object> getLocals() {
/* 43 */     if (this.locals == null || this.locals.length == 0) {
/* 44 */       return Collections.emptyMap();
/*    */     }
/*    */     
/* 47 */     Map<String, Object> localsMap = new HashMap<>();
/* 48 */     for (LocalVariable localVariable : this.locals) {
/* 49 */       if (localVariable != null) {
/* 50 */         localsMap.put(localVariable.getName(), localVariable.getValue());
/*    */       }
/*    */     } 
/*    */     
/* 54 */     return localsMap;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 59 */     return "Frame{, locals=" + 
/* 60 */       Arrays.toString((Object[])this.locals) + '}';
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static final class LocalVariable
/*    */   {
/*    */     final String name;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     final Object value;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public LocalVariable(String name, Object value) {
/* 84 */       this.name = name;
/* 85 */       this.value = value;
/*    */     }
/*    */     
/*    */     public String getName() {
/* 89 */       return this.name;
/*    */     }
/*    */     
/*    */     public Object getValue() {
/* 93 */       return this.value;
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 98 */       return "LocalVariable{name='" + this.name + '\'' + ", value=" + this.value + '}';
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\jvmti\Frame.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */