/*    */ package io.sentry.event.interfaces;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ExceptionMechanism
/*    */   implements Serializable
/*    */ {
/*    */   private final String type;
/*    */   private final boolean handled;
/*    */   
/*    */   public ExceptionMechanism(String type, boolean handled) {
/* 19 */     this.type = type;
/* 20 */     this.handled = handled;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getType() {
/* 28 */     return this.type;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isHandled() {
/* 36 */     return this.handled;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 41 */     return "ExceptionMechanism{type='" + this.type + '\'' + ", handled=" + this.handled + '}';
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 49 */     if (this == o) {
/* 50 */       return true;
/*    */     }
/* 52 */     if (o == null || getClass() != o.getClass()) {
/* 53 */       return false;
/*    */     }
/*    */     
/* 56 */     ExceptionMechanism that = (ExceptionMechanism)o;
/*    */     
/* 58 */     if ((this.type != null) ? !this.type.equals(that.type) : (that.type != null)) {
/* 59 */       return false;
/*    */     }
/*    */     
/* 62 */     return (this.handled == that.handled);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 67 */     int result = (this.type != null) ? this.type.hashCode() : 0;
/* 68 */     result = 31 * result + (this.handled ? 1231 : 1237);
/* 69 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\interfaces\ExceptionMechanism.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */