/*    */ package com.fasterxml.jackson.core.io;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonParseException;
/*    */ import com.fasterxml.jackson.core.JsonParser;
/*    */ import com.fasterxml.jackson.core.JsonToken;
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
/*    */ public class JsonEOFException
/*    */   extends JsonParseException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final JsonToken _token;
/*    */   
/*    */   public JsonEOFException(JsonParser p, JsonToken token, String msg) {
/* 26 */     super(p, msg);
/* 27 */     this._token = token;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JsonToken getTokenBeingDecoded() {
/* 35 */     return this._token;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\io\JsonEOFException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */