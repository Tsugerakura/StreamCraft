/*    */ package io.sentry.event.interfaces;
/*    */ 
/*    */ import java.util.Deque;
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
/*    */ public class ExceptionInterface
/*    */   implements SentryInterface
/*    */ {
/*    */   public static final String EXCEPTION_INTERFACE = "sentry.interfaces.Exception";
/*    */   private final Deque<SentryException> exceptions;
/*    */   
/*    */   public ExceptionInterface(Throwable throwable) {
/* 21 */     this(SentryException.extractExceptionQueue(throwable));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ExceptionInterface(Deque<SentryException> exceptions) {
/* 30 */     this.exceptions = exceptions;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getInterfaceName() {
/* 35 */     return "sentry.interfaces.Exception";
/*    */   }
/*    */   
/*    */   public Deque<SentryException> getExceptions() {
/* 39 */     return this.exceptions;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 44 */     return "ExceptionInterface{exceptions=" + this.exceptions + '}';
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 51 */     if (this == o) {
/* 52 */       return true;
/*    */     }
/* 54 */     if (o == null || getClass() != o.getClass()) {
/* 55 */       return false;
/*    */     }
/*    */     
/* 58 */     ExceptionInterface that = (ExceptionInterface)o;
/*    */     
/* 60 */     return this.exceptions.equals(that.exceptions);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 66 */     return this.exceptions.hashCode();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\interfaces\ExceptionInterface.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */