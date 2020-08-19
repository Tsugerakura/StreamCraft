/*    */ package com.google.gson.internal.reflect;
/*    */ 
/*    */ import java.lang.reflect.AccessibleObject;
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
/*    */ final class PreJava9ReflectionAccessor
/*    */   extends ReflectionAccessor
/*    */ {
/*    */   public void makeAccessible(AccessibleObject ao) {
/* 31 */     ao.setAccessible(true);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\reflect\PreJava9ReflectionAccessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */