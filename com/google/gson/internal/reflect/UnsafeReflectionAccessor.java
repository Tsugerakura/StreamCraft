/*    */ package com.google.gson.internal.reflect;
/*    */ 
/*    */ import com.google.gson.JsonIOException;
/*    */ import java.lang.reflect.AccessibleObject;
/*    */ import java.lang.reflect.Field;
/*    */ import java.lang.reflect.Method;
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
/*    */ final class UnsafeReflectionAccessor
/*    */   extends ReflectionAccessor
/*    */ {
/*    */   private static Class unsafeClass;
/* 34 */   private final Object theUnsafe = getUnsafeInstance();
/* 35 */   private final Field overrideField = getOverrideField();
/*    */ 
/*    */ 
/*    */   
/*    */   public void makeAccessible(AccessibleObject ao) {
/* 40 */     boolean success = makeAccessibleWithUnsafe(ao);
/* 41 */     if (!success) {
/*    */       
/*    */       try {
/* 44 */         ao.setAccessible(true);
/* 45 */       } catch (SecurityException e) {
/* 46 */         throw new JsonIOException("Gson couldn't modify fields for " + ao + "\nand sun.misc.Unsafe not found.\nEither write a custom type adapter, or make fields accessible, or include sun.misc.Unsafe.", e);
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   boolean makeAccessibleWithUnsafe(AccessibleObject ao) {
/* 55 */     if (this.theUnsafe != null && this.overrideField != null) {
/*    */       try {
/* 57 */         Method method = unsafeClass.getMethod("objectFieldOffset", new Class[] { Field.class });
/* 58 */         long overrideOffset = ((Long)method.invoke(this.theUnsafe, new Object[] { this.overrideField })).longValue();
/* 59 */         Method putBooleanMethod = unsafeClass.getMethod("putBoolean", new Class[] { Object.class, long.class, boolean.class });
/* 60 */         putBooleanMethod.invoke(this.theUnsafe, new Object[] { ao, Long.valueOf(overrideOffset), Boolean.valueOf(true) });
/* 61 */         return true;
/* 62 */       } catch (Exception exception) {}
/*    */     }
/*    */     
/* 65 */     return false;
/*    */   }
/*    */   
/*    */   private static Object getUnsafeInstance() {
/*    */     try {
/* 70 */       unsafeClass = Class.forName("sun.misc.Unsafe");
/* 71 */       Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
/* 72 */       unsafeField.setAccessible(true);
/* 73 */       return unsafeField.get((Object)null);
/* 74 */     } catch (Exception e) {
/* 75 */       return null;
/*    */     } 
/*    */   }
/*    */   
/*    */   private static Field getOverrideField() {
/*    */     try {
/* 81 */       return AccessibleObject.class.getDeclaredField("override");
/* 82 */     } catch (NoSuchFieldException e) {
/* 83 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\reflect\UnsafeReflectionAccessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */