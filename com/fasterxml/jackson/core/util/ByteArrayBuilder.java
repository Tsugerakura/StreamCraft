/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import java.io.OutputStream;
/*     */ import java.util.LinkedList;
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
/*     */ public final class ByteArrayBuilder
/*     */   extends OutputStream
/*     */ {
/*  31 */   public static final byte[] NO_BYTES = new byte[0];
/*     */ 
/*     */   
/*     */   private static final int INITIAL_BLOCK_SIZE = 500;
/*     */ 
/*     */   
/*     */   private static final int MAX_BLOCK_SIZE = 131072;
/*     */ 
/*     */   
/*     */   static final int DEFAULT_BLOCK_ARRAY_SIZE = 40;
/*     */   
/*     */   private final BufferRecycler _bufferRecycler;
/*     */   
/*  44 */   private final LinkedList<byte[]> _pastBlocks = (LinkedList)new LinkedList<byte>();
/*     */   
/*     */   private int _pastLen;
/*     */   private byte[] _currBlock;
/*     */   private int _currBlockPtr;
/*     */   
/*     */   public ByteArrayBuilder() {
/*  51 */     this((BufferRecycler)null); }
/*  52 */   public ByteArrayBuilder(BufferRecycler br) { this(br, 500); } public ByteArrayBuilder(int firstBlockSize) {
/*  53 */     this(null, firstBlockSize);
/*     */   }
/*     */   public ByteArrayBuilder(BufferRecycler br, int firstBlockSize) {
/*  56 */     this._bufferRecycler = br;
/*  57 */     this._currBlock = (br == null) ? new byte[firstBlockSize] : br.allocByteBuffer(2);
/*     */   }
/*     */   
/*     */   private ByteArrayBuilder(BufferRecycler br, byte[] initialBlock, int initialLen) {
/*  61 */     this._bufferRecycler = null;
/*  62 */     this._currBlock = initialBlock;
/*  63 */     this._currBlockPtr = initialLen;
/*     */   }
/*     */   
/*     */   public static ByteArrayBuilder fromInitial(byte[] initialBlock, int length) {
/*  67 */     return new ByteArrayBuilder(null, initialBlock, length);
/*     */   }
/*     */   
/*     */   public void reset() {
/*  71 */     this._pastLen = 0;
/*  72 */     this._currBlockPtr = 0;
/*     */     
/*  74 */     if (!this._pastBlocks.isEmpty()) {
/*  75 */       this._pastBlocks.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/*  83 */     return this._pastLen + this._currBlockPtr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {
/*  92 */     reset();
/*  93 */     if (this._bufferRecycler != null && this._currBlock != null) {
/*  94 */       this._bufferRecycler.releaseByteBuffer(2, this._currBlock);
/*  95 */       this._currBlock = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void append(int i) {
/* 100 */     if (this._currBlockPtr >= this._currBlock.length) {
/* 101 */       _allocMore();
/*     */     }
/* 103 */     this._currBlock[this._currBlockPtr++] = (byte)i;
/*     */   }
/*     */   
/*     */   public void appendTwoBytes(int b16) {
/* 107 */     if (this._currBlockPtr + 1 < this._currBlock.length) {
/* 108 */       this._currBlock[this._currBlockPtr++] = (byte)(b16 >> 8);
/* 109 */       this._currBlock[this._currBlockPtr++] = (byte)b16;
/*     */     } else {
/* 111 */       append(b16 >> 8);
/* 112 */       append(b16);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void appendThreeBytes(int b24) {
/* 117 */     if (this._currBlockPtr + 2 < this._currBlock.length) {
/* 118 */       this._currBlock[this._currBlockPtr++] = (byte)(b24 >> 16);
/* 119 */       this._currBlock[this._currBlockPtr++] = (byte)(b24 >> 8);
/* 120 */       this._currBlock[this._currBlockPtr++] = (byte)b24;
/*     */     } else {
/* 122 */       append(b24 >> 16);
/* 123 */       append(b24 >> 8);
/* 124 */       append(b24);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void appendFourBytes(int b32) {
/* 132 */     if (this._currBlockPtr + 3 < this._currBlock.length) {
/* 133 */       this._currBlock[this._currBlockPtr++] = (byte)(b32 >> 24);
/* 134 */       this._currBlock[this._currBlockPtr++] = (byte)(b32 >> 16);
/* 135 */       this._currBlock[this._currBlockPtr++] = (byte)(b32 >> 8);
/* 136 */       this._currBlock[this._currBlockPtr++] = (byte)b32;
/*     */     } else {
/* 138 */       append(b32 >> 24);
/* 139 */       append(b32 >> 16);
/* 140 */       append(b32 >> 8);
/* 141 */       append(b32);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] toByteArray() {
/* 151 */     int totalLen = this._pastLen + this._currBlockPtr;
/*     */     
/* 153 */     if (totalLen == 0) {
/* 154 */       return NO_BYTES;
/*     */     }
/* 156 */     byte[] result = new byte[totalLen];
/* 157 */     int offset = 0;
/*     */     
/* 159 */     for (byte[] block : this._pastBlocks) {
/* 160 */       int len = block.length;
/* 161 */       System.arraycopy(block, 0, result, offset, len);
/* 162 */       offset += len;
/*     */     } 
/* 164 */     System.arraycopy(this._currBlock, 0, result, offset, this._currBlockPtr);
/* 165 */     offset += this._currBlockPtr;
/* 166 */     if (offset != totalLen) {
/* 167 */       throw new RuntimeException("Internal error: total len assumed to be " + totalLen + ", copied " + offset + " bytes");
/*     */     }
/*     */     
/* 170 */     if (!this._pastBlocks.isEmpty()) {
/* 171 */       reset();
/*     */     }
/* 173 */     return result;
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
/*     */   public byte[] resetAndGetFirstSegment() {
/* 187 */     reset();
/* 188 */     return this._currBlock;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] finishCurrentSegment() {
/* 197 */     _allocMore();
/* 198 */     return this._currBlock;
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
/*     */   public byte[] completeAndCoalesce(int lastBlockLength) {
/* 211 */     this._currBlockPtr = lastBlockLength;
/* 212 */     return toByteArray();
/*     */   }
/*     */   
/* 215 */   public byte[] getCurrentSegment() { return this._currBlock; }
/* 216 */   public void setCurrentSegmentLength(int len) { this._currBlockPtr = len; } public int getCurrentSegmentLength() {
/* 217 */     return this._currBlockPtr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b) {
/* 227 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) {
/*     */     while (true) {
/* 234 */       int max = this._currBlock.length - this._currBlockPtr;
/* 235 */       int toCopy = Math.min(max, len);
/* 236 */       if (toCopy > 0) {
/* 237 */         System.arraycopy(b, off, this._currBlock, this._currBlockPtr, toCopy);
/* 238 */         off += toCopy;
/* 239 */         this._currBlockPtr += toCopy;
/* 240 */         len -= toCopy;
/*     */       } 
/* 242 */       if (len <= 0)
/* 243 */         break;  _allocMore();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) {
/* 249 */     append(b);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() {}
/*     */ 
/*     */ 
/*     */   
/*     */   private void _allocMore() {
/* 263 */     int newPastLen = this._pastLen + this._currBlock.length;
/*     */ 
/*     */ 
/*     */     
/* 267 */     if (newPastLen < 0) {
/* 268 */       throw new IllegalStateException("Maximum Java array size (2GB) exceeded by `ByteArrayBuilder`");
/*     */     }
/*     */     
/* 271 */     this._pastLen = newPastLen;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 279 */     int newSize = Math.max(this._pastLen >> 1, 1000);
/*     */     
/* 281 */     if (newSize > 131072) {
/* 282 */       newSize = 131072;
/*     */     }
/* 284 */     this._pastBlocks.add(this._currBlock);
/* 285 */     this._currBlock = new byte[newSize];
/* 286 */     this._currBlockPtr = 0;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\cor\\util\ByteArrayBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */