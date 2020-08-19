/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.exc.StreamReadException;
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
/*     */ public class JsonParseException
/*     */   extends StreamReadException
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*     */   
/*     */   @Deprecated
/*     */   public JsonParseException(String msg, JsonLocation loc) {
/*  22 */     super(msg, loc, null);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public JsonParseException(String msg, JsonLocation loc, Throwable root) {
/*  27 */     super(msg, loc, root);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParseException(JsonParser p, String msg) {
/*  38 */     super(p, msg);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParseException(JsonParser p, String msg, Throwable root) {
/*  45 */     super(p, msg, root);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParseException(JsonParser p, String msg, JsonLocation loc) {
/*  52 */     super(p, msg, loc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParseException(JsonParser p, String msg, JsonLocation loc, Throwable root) {
/*  59 */     super(msg, loc, root);
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
/*     */   public JsonParseException withParser(JsonParser p) {
/*  72 */     this._processor = p;
/*  73 */     return this;
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
/*     */   public JsonParseException withRequestPayload(RequestPayload p) {
/*  86 */     this._requestPayload = p;
/*  87 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonParser getProcessor() {
/*  93 */     return super.getProcessor();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RequestPayload getRequestPayload() {
/*  99 */     return super.getRequestPayload();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRequestPayloadAsString() {
/* 105 */     return super.getRequestPayloadAsString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMessage() {
/* 111 */     return super.getMessage();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\JsonParseException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */