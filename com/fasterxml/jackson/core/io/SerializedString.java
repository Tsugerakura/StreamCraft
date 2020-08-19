/*     */ package com.fasterxml.jackson.core.io;
/*     */ 
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SerializedString
/*     */   implements SerializableString, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  22 */   private static final JsonStringEncoder JSON_ENCODER = JsonStringEncoder.getInstance();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String _value;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] _quotedUTF8Ref;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] _unquotedUTF8Ref;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected char[] _quotedChars;
/*     */ 
/*     */ 
/*     */   
/*     */   protected transient String _jdkSerializeValue;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SerializedString(String v) {
/*  52 */     if (v == null) {
/*  53 */       throw new IllegalStateException("Null String illegal for SerializedString");
/*     */     }
/*  55 */     this._value = v;
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
/*     */   private void readObject(ObjectInputStream in) throws IOException {
/*  73 */     this._jdkSerializeValue = in.readUTF();
/*     */   }
/*     */   
/*     */   private void writeObject(ObjectOutputStream out) throws IOException {
/*  77 */     out.writeUTF(this._value);
/*     */   }
/*     */   
/*     */   protected Object readResolve() {
/*  81 */     return new SerializedString(this._jdkSerializeValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String getValue() {
/*  91 */     return this._value;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final int charLength() {
/*  97 */     return this._value.length();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final char[] asQuotedChars() {
/* 105 */     char[] result = this._quotedChars;
/* 106 */     if (result == null) {
/* 107 */       this._quotedChars = result = JSON_ENCODER.quoteAsString(this._value);
/*     */     }
/* 109 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final byte[] asQuotedUTF8() {
/* 119 */     byte[] result = this._quotedUTF8Ref;
/* 120 */     if (result == null) {
/* 121 */       this._quotedUTF8Ref = result = JSON_ENCODER.quoteAsUTF8(this._value);
/*     */     }
/* 123 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final byte[] asUnquotedUTF8() {
/* 132 */     byte[] result = this._unquotedUTF8Ref;
/* 133 */     if (result == null) {
/* 134 */       this._unquotedUTF8Ref = result = JSON_ENCODER.encodeAsUTF8(this._value);
/*     */     }
/* 136 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int appendQuoted(char[] buffer, int offset) {
/* 147 */     char[] result = this._quotedChars;
/* 148 */     if (result == null) {
/* 149 */       this._quotedChars = result = JSON_ENCODER.quoteAsString(this._value);
/*     */     }
/* 151 */     int length = result.length;
/* 152 */     if (offset + length > buffer.length) {
/* 153 */       return -1;
/*     */     }
/* 155 */     System.arraycopy(result, 0, buffer, offset, length);
/* 156 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int appendQuotedUTF8(byte[] buffer, int offset) {
/* 161 */     byte[] result = this._quotedUTF8Ref;
/* 162 */     if (result == null) {
/* 163 */       this._quotedUTF8Ref = result = JSON_ENCODER.quoteAsUTF8(this._value);
/*     */     }
/* 165 */     int length = result.length;
/* 166 */     if (offset + length > buffer.length) {
/* 167 */       return -1;
/*     */     }
/* 169 */     System.arraycopy(result, 0, buffer, offset, length);
/* 170 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int appendUnquoted(char[] buffer, int offset) {
/* 175 */     String str = this._value;
/* 176 */     int length = str.length();
/* 177 */     if (offset + length > buffer.length) {
/* 178 */       return -1;
/*     */     }
/* 180 */     str.getChars(0, length, buffer, offset);
/* 181 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int appendUnquotedUTF8(byte[] buffer, int offset) {
/* 186 */     byte[] result = this._unquotedUTF8Ref;
/* 187 */     if (result == null) {
/* 188 */       this._unquotedUTF8Ref = result = JSON_ENCODER.encodeAsUTF8(this._value);
/*     */     }
/* 190 */     int length = result.length;
/* 191 */     if (offset + length > buffer.length) {
/* 192 */       return -1;
/*     */     }
/* 194 */     System.arraycopy(result, 0, buffer, offset, length);
/* 195 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeQuotedUTF8(OutputStream out) throws IOException {
/* 200 */     byte[] result = this._quotedUTF8Ref;
/* 201 */     if (result == null) {
/* 202 */       this._quotedUTF8Ref = result = JSON_ENCODER.quoteAsUTF8(this._value);
/*     */     }
/* 204 */     int length = result.length;
/* 205 */     out.write(result, 0, length);
/* 206 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeUnquotedUTF8(OutputStream out) throws IOException {
/* 211 */     byte[] result = this._unquotedUTF8Ref;
/* 212 */     if (result == null) {
/* 213 */       this._unquotedUTF8Ref = result = JSON_ENCODER.encodeAsUTF8(this._value);
/*     */     }
/* 215 */     int length = result.length;
/* 216 */     out.write(result, 0, length);
/* 217 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int putQuotedUTF8(ByteBuffer buffer) {
/* 222 */     byte[] result = this._quotedUTF8Ref;
/* 223 */     if (result == null) {
/* 224 */       this._quotedUTF8Ref = result = JSON_ENCODER.quoteAsUTF8(this._value);
/*     */     }
/* 226 */     int length = result.length;
/* 227 */     if (length > buffer.remaining()) {
/* 228 */       return -1;
/*     */     }
/* 230 */     buffer.put(result, 0, length);
/* 231 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   public int putUnquotedUTF8(ByteBuffer buffer) {
/* 236 */     byte[] result = this._unquotedUTF8Ref;
/* 237 */     if (result == null) {
/* 238 */       this._unquotedUTF8Ref = result = JSON_ENCODER.encodeAsUTF8(this._value);
/*     */     }
/* 240 */     int length = result.length;
/* 241 */     if (length > buffer.remaining()) {
/* 242 */       return -1;
/*     */     }
/* 244 */     buffer.put(result, 0, length);
/* 245 */     return length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 255 */     return this._value;
/*     */   }
/*     */   public final int hashCode() {
/* 258 */     return this._value.hashCode();
/*     */   }
/*     */   
/*     */   public final boolean equals(Object o) {
/* 262 */     if (o == this) return true; 
/* 263 */     if (o == null || o.getClass() != getClass()) return false; 
/* 264 */     SerializedString other = (SerializedString)o;
/* 265 */     return this._value.equals(other._value);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\io\SerializedString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */