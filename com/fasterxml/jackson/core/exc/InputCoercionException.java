/*    */ package com.fasterxml.jackson.core.exc;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.core.JsonToken;
/*    */ import com.fasterxml.jackson.core.util.RequestPayload;
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
/*    */ public class InputCoercionException
/*    */   extends StreamReadException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final JsonToken _inputType;
/*    */   protected final Class<?> _targetType;
/*    */   
/*    */   public InputCoercionException(JsonParser p, String msg, JsonToken inputType, Class<?> targetType) {
/* 35 */     super(p, msg);
/* 36 */     this._inputType = inputType;
/* 37 */     this._targetType = targetType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public InputCoercionException withParser(JsonParser p) {
/* 48 */     this._processor = p;
/* 49 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public InputCoercionException withRequestPayload(RequestPayload p) {
/* 54 */     this._requestPayload = p;
/* 55 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JsonToken getInputType() {
/* 63 */     return this._inputType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Class<?> getTargetType() {
/* 71 */     return this._targetType;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\exc\InputCoercionException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */