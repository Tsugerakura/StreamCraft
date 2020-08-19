/*    */ package pro.gravit.repackage.io.netty.handler.codec;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ProtocolDetectionResult<T>
/*    */ {
/* 28 */   private static final ProtocolDetectionResult NEEDS_MORE_DATE = new ProtocolDetectionResult(ProtocolDetectionState.NEEDS_MORE_DATA, null);
/*    */ 
/*    */   
/* 31 */   private static final ProtocolDetectionResult INVALID = new ProtocolDetectionResult(ProtocolDetectionState.INVALID, null);
/*    */ 
/*    */   
/*    */   private final ProtocolDetectionState state;
/*    */ 
/*    */   
/*    */   private final T result;
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T> ProtocolDetectionResult<T> needsMoreData() {
/* 42 */     return NEEDS_MORE_DATE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T> ProtocolDetectionResult<T> invalid() {
/* 50 */     return INVALID;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T> ProtocolDetectionResult<T> detected(T protocol) {
/* 58 */     return new ProtocolDetectionResult<T>(ProtocolDetectionState.DETECTED, (T)ObjectUtil.checkNotNull(protocol, "protocol"));
/*    */   }
/*    */   
/*    */   private ProtocolDetectionResult(ProtocolDetectionState state, T result) {
/* 62 */     this.state = state;
/* 63 */     this.result = result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ProtocolDetectionState state() {
/* 71 */     return this.state;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T detectedProtocol() {
/* 78 */     return this.result;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\ProtocolDetectionResult.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */