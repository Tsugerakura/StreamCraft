/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import java.util.concurrent.atomic.AtomicReferenceArray;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BufferRecycler
/*     */ {
/*     */   public static final int BYTE_READ_IO_BUFFER = 0;
/*     */   public static final int BYTE_WRITE_ENCODING_BUFFER = 1;
/*     */   public static final int BYTE_WRITE_CONCAT_BUFFER = 2;
/*     */   public static final int BYTE_BASE64_CODEC_BUFFER = 3;
/*     */   public static final int CHAR_TOKEN_BUFFER = 0;
/*     */   public static final int CHAR_CONCAT_BUFFER = 1;
/*     */   public static final int CHAR_TEXT_BUFFER = 2;
/*     */   public static final int CHAR_NAME_COPY_BUFFER = 3;
/*  76 */   private static final int[] BYTE_BUFFER_LENGTHS = new int[] { 8000, 8000, 2000, 2000 };
/*  77 */   private static final int[] CHAR_BUFFER_LENGTHS = new int[] { 4000, 4000, 200, 200 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AtomicReferenceArray<byte[]> _byteBuffers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final AtomicReferenceArray<char[]> _charBuffers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferRecycler() {
/*  96 */     this(4, 4);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected BufferRecycler(int bbCount, int cbCount) {
/* 106 */     this._byteBuffers = (AtomicReferenceArray)new AtomicReferenceArray<byte>(bbCount);
/* 107 */     this._charBuffers = (AtomicReferenceArray)new AtomicReferenceArray<char>(cbCount);
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
/*     */   public final byte[] allocByteBuffer(int ix) {
/* 120 */     return allocByteBuffer(ix, 0);
/*     */   }
/*     */   
/*     */   public byte[] allocByteBuffer(int ix, int minSize) {
/* 124 */     int DEF_SIZE = byteBufferLength(ix);
/* 125 */     if (minSize < DEF_SIZE) {
/* 126 */       minSize = DEF_SIZE;
/*     */     }
/* 128 */     byte[] buffer = this._byteBuffers.getAndSet(ix, null);
/* 129 */     if (buffer == null || buffer.length < minSize) {
/* 130 */       buffer = balloc(minSize);
/*     */     }
/* 132 */     return buffer;
/*     */   }
/*     */   
/*     */   public void releaseByteBuffer(int ix, byte[] buffer) {
/* 136 */     this._byteBuffers.set(ix, buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final char[] allocCharBuffer(int ix) {
/* 146 */     return allocCharBuffer(ix, 0);
/*     */   }
/*     */   
/*     */   public char[] allocCharBuffer(int ix, int minSize) {
/* 150 */     int DEF_SIZE = charBufferLength(ix);
/* 151 */     if (minSize < DEF_SIZE) {
/* 152 */       minSize = DEF_SIZE;
/*     */     }
/* 154 */     char[] buffer = this._charBuffers.getAndSet(ix, null);
/* 155 */     if (buffer == null || buffer.length < minSize) {
/* 156 */       buffer = calloc(minSize);
/*     */     }
/* 158 */     return buffer;
/*     */   }
/*     */   
/*     */   public void releaseCharBuffer(int ix, char[] buffer) {
/* 162 */     this._charBuffers.set(ix, buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int byteBufferLength(int ix) {
/* 172 */     return BYTE_BUFFER_LENGTHS[ix];
/*     */   }
/*     */   
/*     */   protected int charBufferLength(int ix) {
/* 176 */     return CHAR_BUFFER_LENGTHS[ix];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] balloc(int size) {
/* 185 */     return new byte[size]; } protected char[] calloc(int size) {
/* 186 */     return new char[size];
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\cor\\util\BufferRecycler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */