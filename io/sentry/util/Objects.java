/*    */ package io.sentry.util;
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
/*    */ public final class Objects
/*    */ {
/*    */   public static <T> T requireNonNull(T obj, String message) {
/* 19 */     if (obj == null) {
/* 20 */       throw new NullPointerException(message);
/*    */     }
/* 22 */     return obj;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T> T requireNonNull(T obj) {
/* 32 */     if (obj == null) {
/* 33 */       throw new NullPointerException();
/*    */     }
/* 35 */     return obj;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentr\\util\Objects.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */