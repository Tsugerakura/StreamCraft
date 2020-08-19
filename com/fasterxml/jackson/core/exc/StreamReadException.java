/*     */ package com.fasterxml.jackson.core.exc;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonLocation;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.core.util.RequestPayload;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class StreamReadException
/*     */   extends JsonProcessingException
/*     */ {
/*     */   static final long serialVersionUID = 1L;
/*     */   protected transient JsonParser _processor;
/*     */   protected RequestPayload _requestPayload;
/*     */   
/*     */   public StreamReadException(JsonParser p, String msg) {
/*  28 */     super(msg, (p == null) ? null : p.getCurrentLocation());
/*  29 */     this._processor = p;
/*     */   }
/*     */   
/*     */   public StreamReadException(JsonParser p, String msg, Throwable root) {
/*  33 */     super(msg, (p == null) ? null : p.getCurrentLocation(), root);
/*  34 */     this._processor = p;
/*     */   }
/*     */   
/*     */   public StreamReadException(JsonParser p, String msg, JsonLocation loc) {
/*  38 */     super(msg, loc, null);
/*  39 */     this._processor = p;
/*     */   }
/*     */   
/*     */   protected StreamReadException(String msg, JsonLocation loc, Throwable rootCause) {
/*  43 */     super(msg);
/*  44 */     if (rootCause != null) {
/*  45 */       initCause(rootCause);
/*     */     }
/*  47 */     this._location = loc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParser getProcessor() {
/*  68 */     return this._processor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestPayload getRequestPayload() {
/*  78 */     return this._requestPayload;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRequestPayloadAsString() {
/*  88 */     return (this._requestPayload != null) ? this._requestPayload.toString() : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessage() {
/*  96 */     String msg = super.getMessage();
/*  97 */     if (this._requestPayload != null) {
/*  98 */       msg = msg + "\nRequest payload : " + this._requestPayload.toString();
/*     */     }
/* 100 */     return msg;
/*     */   }
/*     */   
/*     */   public abstract StreamReadException withParser(JsonParser paramJsonParser);
/*     */   
/*     */   public abstract StreamReadException withRequestPayload(RequestPayload paramRequestPayload);
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\exc\StreamReadException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */