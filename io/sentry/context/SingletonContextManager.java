/*    */ package io.sentry.context;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SingletonContextManager
/*    */   implements ContextManager
/*    */ {
/*  8 */   private final Context context = new Context();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Context getContext() {
/* 17 */     return this.context;
/*    */   }
/*    */ 
/*    */   
/*    */   public void clear() {
/* 22 */     this.context.clear();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\context\SingletonContextManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */