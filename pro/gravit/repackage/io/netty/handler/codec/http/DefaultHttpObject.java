/*    */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.handler.codec.DecoderResult;
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
/*    */ public class DefaultHttpObject
/*    */   implements HttpObject
/*    */ {
/*    */   private static final int HASH_CODE_PRIME = 31;
/* 24 */   private DecoderResult decoderResult = DecoderResult.SUCCESS;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DecoderResult decoderResult() {
/* 32 */     return this.decoderResult;
/*    */   }
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public DecoderResult getDecoderResult() {
/* 38 */     return decoderResult();
/*    */   }
/*    */ 
/*    */   
/*    */   public void setDecoderResult(DecoderResult decoderResult) {
/* 43 */     this.decoderResult = (DecoderResult)ObjectUtil.checkNotNull(decoderResult, "decoderResult");
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 48 */     int result = 1;
/* 49 */     result = 31 * result + this.decoderResult.hashCode();
/* 50 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 55 */     if (!(o instanceof DefaultHttpObject)) {
/* 56 */       return false;
/*    */     }
/*    */     
/* 59 */     DefaultHttpObject other = (DefaultHttpObject)o;
/*    */     
/* 61 */     return decoderResult().equals(other.decoderResult());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\DefaultHttpObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */