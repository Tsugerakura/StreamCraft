/*     */ package com.fasterxml.jackson.core.format;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import com.fasterxml.jackson.core.JsonParser;
/*     */ import com.fasterxml.jackson.core.io.MergedStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataFormatMatcher
/*     */ {
/*     */   protected final InputStream _originalStream;
/*     */   protected final byte[] _bufferedData;
/*     */   protected final int _bufferedStart;
/*     */   protected final int _bufferedLength;
/*     */   protected final JsonFactory _match;
/*     */   protected final MatchStrength _matchStrength;
/*     */   
/*     */   protected DataFormatMatcher(InputStream in, byte[] buffered, int bufferedStart, int bufferedLength, JsonFactory match, MatchStrength strength) {
/*  46 */     this._originalStream = in;
/*  47 */     this._bufferedData = buffered;
/*  48 */     this._bufferedStart = bufferedStart;
/*  49 */     this._bufferedLength = bufferedLength;
/*  50 */     this._match = match;
/*  51 */     this._matchStrength = strength;
/*     */ 
/*     */     
/*  54 */     if ((bufferedStart | bufferedLength) < 0 || bufferedStart + bufferedLength > buffered.length)
/*     */     {
/*  56 */       throw new IllegalArgumentException(String.format("Illegal start/length (%d/%d) wrt input array of %d bytes", new Object[] {
/*  57 */               Integer.valueOf(bufferedStart), Integer.valueOf(bufferedLength), Integer.valueOf(buffered.length)
/*     */             }));
/*     */     }
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
/*     */   public boolean hasMatch() {
/*  71 */     return (this._match != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MatchStrength getMatchStrength() {
/*  78 */     return (this._matchStrength == null) ? MatchStrength.INCONCLUSIVE : this._matchStrength;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonFactory getMatch() {
/*  84 */     return this._match;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getMatchedFormatName() {
/*  94 */     return this._match.getFormatName();
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
/*     */   public JsonParser createParserWithMatch() throws IOException {
/* 109 */     if (this._match == null) {
/* 110 */       return null;
/*     */     }
/* 112 */     if (this._originalStream == null) {
/* 113 */       return this._match.createParser(this._bufferedData, this._bufferedStart, this._bufferedLength);
/*     */     }
/* 115 */     return this._match.createParser(getDataStream());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getDataStream() {
/* 126 */     if (this._originalStream == null) {
/* 127 */       return new ByteArrayInputStream(this._bufferedData, this._bufferedStart, this._bufferedLength);
/*     */     }
/* 129 */     return (InputStream)new MergedStream(null, this._originalStream, this._bufferedData, this._bufferedStart, this._bufferedLength);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\format\DataFormatMatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */