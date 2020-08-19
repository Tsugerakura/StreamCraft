/*    */ package io.sentry.context;
/*    */ 
/*    */ 
/*    */ public class ThreadLocalContextManager
/*    */   implements ContextManager
/*    */ {
/*  7 */   private final ThreadLocal<Context> context = new ThreadLocal<Context>()
/*    */     {
/*    */       protected Context initialValue() {
/* 10 */         return new Context();
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Context getContext() {
/* 21 */     return this.context.get();
/*    */   }
/*    */ 
/*    */   
/*    */   public void clear() {
/* 26 */     this.context.remove();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\context\ThreadLocalContextManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */