/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ public class JsonProcessingException
/*     */   extends IOException
/*     */ {
/*     */   static final long serialVersionUID = 123L;
/*     */   protected JsonLocation _location;
/*     */   
/*     */   protected JsonProcessingException(String msg, JsonLocation loc, Throwable rootCause) {
/*  22 */     super(msg);
/*  23 */     if (rootCause != null) {
/*  24 */       initCause(rootCause);
/*     */     }
/*  26 */     this._location = loc;
/*     */   }
/*     */   
/*     */   protected JsonProcessingException(String msg) {
/*  30 */     super(msg);
/*     */   }
/*     */   
/*     */   protected JsonProcessingException(String msg, JsonLocation loc) {
/*  34 */     this(msg, loc, (Throwable)null);
/*     */   }
/*     */   
/*     */   protected JsonProcessingException(String msg, Throwable rootCause) {
/*  38 */     this(msg, (JsonLocation)null, rootCause);
/*     */   }
/*     */   
/*     */   protected JsonProcessingException(Throwable rootCause) {
/*  42 */     this((String)null, (JsonLocation)null, rootCause);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonLocation getLocation() {
/*  51 */     return this._location;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearLocation() {
/*  60 */     this._location = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getOriginalMessage() {
/*  69 */     return super.getMessage();
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
/*     */   public Object getProcessor() {
/*  85 */     return null;
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
/*     */   protected String getMessageSuffix() {
/*  98 */     return null;
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
/*     */   public String getMessage() {
/* 110 */     String msg = super.getMessage();
/* 111 */     if (msg == null) {
/* 112 */       msg = "N/A";
/*     */     }
/* 114 */     JsonLocation loc = getLocation();
/* 115 */     String suffix = getMessageSuffix();
/*     */     
/* 117 */     if (loc != null || suffix != null) {
/* 118 */       StringBuilder sb = new StringBuilder(100);
/* 119 */       sb.append(msg);
/* 120 */       if (suffix != null) {
/* 121 */         sb.append(suffix);
/*     */       }
/* 123 */       if (loc != null) {
/* 124 */         sb.append('\n');
/* 125 */         sb.append(" at ");
/* 126 */         sb.append(loc.toString());
/*     */       } 
/* 128 */       msg = sb.toString();
/*     */     } 
/* 130 */     return msg;
/*     */   }
/*     */   public String toString() {
/* 133 */     return getClass().getName() + ": " + getMessage();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\JsonProcessingException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */