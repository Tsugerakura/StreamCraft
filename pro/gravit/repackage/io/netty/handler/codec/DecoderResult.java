/*    */ package pro.gravit.repackage.io.netty.handler.codec;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.util.Signal;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*    */ public class DecoderResult
/*    */ {
/* 23 */   protected static final Signal SIGNAL_UNFINISHED = Signal.valueOf(DecoderResult.class, "UNFINISHED");
/* 24 */   protected static final Signal SIGNAL_SUCCESS = Signal.valueOf(DecoderResult.class, "SUCCESS");
/*    */   
/* 26 */   public static final DecoderResult UNFINISHED = new DecoderResult((Throwable)SIGNAL_UNFINISHED);
/* 27 */   public static final DecoderResult SUCCESS = new DecoderResult((Throwable)SIGNAL_SUCCESS);
/*    */   
/*    */   public static DecoderResult failure(Throwable cause) {
/* 30 */     return new DecoderResult((Throwable)ObjectUtil.checkNotNull(cause, "cause"));
/*    */   }
/*    */   
/*    */   private final Throwable cause;
/*    */   
/*    */   protected DecoderResult(Throwable cause) {
/* 36 */     this.cause = (Throwable)ObjectUtil.checkNotNull(cause, "cause");
/*    */   }
/*    */   
/*    */   public boolean isFinished() {
/* 40 */     return (this.cause != SIGNAL_UNFINISHED);
/*    */   }
/*    */   
/*    */   public boolean isSuccess() {
/* 44 */     return (this.cause == SIGNAL_SUCCESS);
/*    */   }
/*    */   
/*    */   public boolean isFailure() {
/* 48 */     return (this.cause != SIGNAL_SUCCESS && this.cause != SIGNAL_UNFINISHED);
/*    */   }
/*    */   
/*    */   public Throwable cause() {
/* 52 */     if (isFailure()) {
/* 53 */       return this.cause;
/*    */     }
/* 55 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 61 */     if (isFinished()) {
/* 62 */       if (isSuccess()) {
/* 63 */         return "success";
/*    */       }
/*    */       
/* 66 */       String cause = cause().toString();
/* 67 */       return (new StringBuilder(cause.length() + 17))
/* 68 */         .append("failure(")
/* 69 */         .append(cause)
/* 70 */         .append(')')
/* 71 */         .toString();
/*    */     } 
/* 73 */     return "unfinished";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\DecoderResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */