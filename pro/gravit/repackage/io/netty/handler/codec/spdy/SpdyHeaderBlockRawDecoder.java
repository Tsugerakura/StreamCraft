/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public class SpdyHeaderBlockRawDecoder
/*     */   extends SpdyHeaderBlockDecoder
/*     */ {
/*     */   private static final int LENGTH_FIELD_SIZE = 4;
/*     */   private final int maxHeaderSize;
/*     */   private State state;
/*     */   private ByteBuf cumulation;
/*     */   private int headerSize;
/*     */   private int numHeaders;
/*     */   private int length;
/*     */   private String name;
/*     */   
/*     */   private enum State
/*     */   {
/*  40 */     READ_NUM_HEADERS,
/*  41 */     READ_NAME_LENGTH,
/*  42 */     READ_NAME,
/*  43 */     SKIP_NAME,
/*  44 */     READ_VALUE_LENGTH,
/*  45 */     READ_VALUE,
/*  46 */     SKIP_VALUE,
/*  47 */     END_HEADER_BLOCK,
/*  48 */     ERROR;
/*     */   }
/*     */   
/*     */   public SpdyHeaderBlockRawDecoder(SpdyVersion spdyVersion, int maxHeaderSize) {
/*  52 */     ObjectUtil.checkNotNull(spdyVersion, "spdyVersion");
/*  53 */     this.maxHeaderSize = maxHeaderSize;
/*  54 */     this.state = State.READ_NUM_HEADERS;
/*     */   }
/*     */   
/*     */   private static int readLengthField(ByteBuf buffer) {
/*  58 */     int length = SpdyCodecUtil.getSignedInt(buffer, buffer.readerIndex());
/*  59 */     buffer.skipBytes(4);
/*  60 */     return length;
/*     */   }
/*     */ 
/*     */   
/*     */   void decode(ByteBufAllocator alloc, ByteBuf headerBlock, SpdyHeadersFrame frame) throws Exception {
/*  65 */     ObjectUtil.checkNotNull(headerBlock, "headerBlock");
/*  66 */     ObjectUtil.checkNotNull(frame, "frame");
/*     */     
/*  68 */     if (this.cumulation == null) {
/*  69 */       decodeHeaderBlock(headerBlock, frame);
/*  70 */       if (headerBlock.isReadable()) {
/*  71 */         this.cumulation = alloc.buffer(headerBlock.readableBytes());
/*  72 */         this.cumulation.writeBytes(headerBlock);
/*     */       } 
/*     */     } else {
/*  75 */       this.cumulation.writeBytes(headerBlock);
/*  76 */       decodeHeaderBlock(this.cumulation, frame);
/*  77 */       if (this.cumulation.isReadable()) {
/*  78 */         this.cumulation.discardReadBytes();
/*     */       } else {
/*  80 */         releaseBuffer();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decodeHeaderBlock(ByteBuf headerBlock, SpdyHeadersFrame frame) throws Exception {
/*  87 */     while (headerBlock.isReadable()) {
/*  88 */       int skipLength; byte[] nameBytes; byte[] valueBytes; int index; int offset; switch (this.state) {
/*     */         case READ_NUM_HEADERS:
/*  90 */           if (headerBlock.readableBytes() < 4) {
/*     */             return;
/*     */           }
/*     */           
/*  94 */           this.numHeaders = readLengthField(headerBlock);
/*     */           
/*  96 */           if (this.numHeaders < 0) {
/*  97 */             this.state = State.ERROR;
/*  98 */             frame.setInvalid(); continue;
/*  99 */           }  if (this.numHeaders == 0) {
/* 100 */             this.state = State.END_HEADER_BLOCK; continue;
/*     */           } 
/* 102 */           this.state = State.READ_NAME_LENGTH;
/*     */           continue;
/*     */ 
/*     */         
/*     */         case READ_NAME_LENGTH:
/* 107 */           if (headerBlock.readableBytes() < 4) {
/*     */             return;
/*     */           }
/*     */           
/* 111 */           this.length = readLengthField(headerBlock);
/*     */ 
/*     */           
/* 114 */           if (this.length <= 0) {
/* 115 */             this.state = State.ERROR;
/* 116 */             frame.setInvalid(); continue;
/* 117 */           }  if (this.length > this.maxHeaderSize || this.headerSize > this.maxHeaderSize - this.length) {
/* 118 */             this.headerSize = this.maxHeaderSize + 1;
/* 119 */             this.state = State.SKIP_NAME;
/* 120 */             frame.setTruncated(); continue;
/*     */           } 
/* 122 */           this.headerSize += this.length;
/* 123 */           this.state = State.READ_NAME;
/*     */           continue;
/*     */ 
/*     */         
/*     */         case READ_NAME:
/* 128 */           if (headerBlock.readableBytes() < this.length) {
/*     */             return;
/*     */           }
/*     */           
/* 132 */           nameBytes = new byte[this.length];
/* 133 */           headerBlock.readBytes(nameBytes);
/* 134 */           this.name = new String(nameBytes, "UTF-8");
/*     */ 
/*     */           
/* 137 */           if (frame.headers().contains(this.name)) {
/* 138 */             this.state = State.ERROR;
/* 139 */             frame.setInvalid(); continue;
/*     */           } 
/* 141 */           this.state = State.READ_VALUE_LENGTH;
/*     */           continue;
/*     */ 
/*     */         
/*     */         case SKIP_NAME:
/* 146 */           skipLength = Math.min(headerBlock.readableBytes(), this.length);
/* 147 */           headerBlock.skipBytes(skipLength);
/* 148 */           this.length -= skipLength;
/*     */           
/* 150 */           if (this.length == 0) {
/* 151 */             this.state = State.READ_VALUE_LENGTH;
/*     */           }
/*     */           continue;
/*     */         
/*     */         case READ_VALUE_LENGTH:
/* 156 */           if (headerBlock.readableBytes() < 4) {
/*     */             return;
/*     */           }
/*     */           
/* 160 */           this.length = readLengthField(headerBlock);
/*     */ 
/*     */           
/* 163 */           if (this.length < 0) {
/* 164 */             this.state = State.ERROR;
/* 165 */             frame.setInvalid(); continue;
/* 166 */           }  if (this.length == 0) {
/* 167 */             if (!frame.isTruncated())
/*     */             {
/* 169 */               frame.headers().add(this.name, "");
/*     */             }
/*     */             
/* 172 */             this.name = null;
/* 173 */             if (--this.numHeaders == 0) {
/* 174 */               this.state = State.END_HEADER_BLOCK; continue;
/*     */             } 
/* 176 */             this.state = State.READ_NAME_LENGTH;
/*     */             continue;
/*     */           } 
/* 179 */           if (this.length > this.maxHeaderSize || this.headerSize > this.maxHeaderSize - this.length) {
/* 180 */             this.headerSize = this.maxHeaderSize + 1;
/* 181 */             this.name = null;
/* 182 */             this.state = State.SKIP_VALUE;
/* 183 */             frame.setTruncated(); continue;
/*     */           } 
/* 185 */           this.headerSize += this.length;
/* 186 */           this.state = State.READ_VALUE;
/*     */           continue;
/*     */ 
/*     */         
/*     */         case READ_VALUE:
/* 191 */           if (headerBlock.readableBytes() < this.length) {
/*     */             return;
/*     */           }
/*     */           
/* 195 */           valueBytes = new byte[this.length];
/* 196 */           headerBlock.readBytes(valueBytes);
/*     */ 
/*     */           
/* 199 */           index = 0;
/* 200 */           offset = 0;
/*     */ 
/*     */           
/* 203 */           if (valueBytes[0] == 0) {
/* 204 */             this.state = State.ERROR;
/* 205 */             frame.setInvalid();
/*     */             
/*     */             continue;
/*     */           } 
/* 209 */           while (index < this.length) {
/* 210 */             while (index < valueBytes.length && valueBytes[index] != 0) {
/* 211 */               index++;
/*     */             }
/* 213 */             if (index < valueBytes.length)
/*     */             {
/* 215 */               if (index + 1 == valueBytes.length || valueBytes[index + 1] == 0) {
/*     */ 
/*     */ 
/*     */                 
/* 219 */                 this.state = State.ERROR;
/* 220 */                 frame.setInvalid();
/*     */                 break;
/*     */               } 
/*     */             }
/* 224 */             String value = new String(valueBytes, offset, index - offset, "UTF-8");
/*     */             
/*     */             try {
/* 227 */               frame.headers().add(this.name, value);
/* 228 */             } catch (IllegalArgumentException e) {
/*     */               
/* 230 */               this.state = State.ERROR;
/* 231 */               frame.setInvalid();
/*     */               
/*     */               break;
/*     */             } 
/* 235 */             offset = ++index;
/*     */           } 
/*     */           
/* 238 */           this.name = null;
/*     */ 
/*     */           
/* 241 */           if (this.state == State.ERROR) {
/*     */             continue;
/*     */           }
/*     */           
/* 245 */           if (--this.numHeaders == 0) {
/* 246 */             this.state = State.END_HEADER_BLOCK; continue;
/*     */           } 
/* 248 */           this.state = State.READ_NAME_LENGTH;
/*     */           continue;
/*     */ 
/*     */         
/*     */         case SKIP_VALUE:
/* 253 */           skipLength = Math.min(headerBlock.readableBytes(), this.length);
/* 254 */           headerBlock.skipBytes(skipLength);
/* 255 */           this.length -= skipLength;
/*     */           
/* 257 */           if (this.length == 0) {
/* 258 */             if (--this.numHeaders == 0) {
/* 259 */               this.state = State.END_HEADER_BLOCK; continue;
/*     */             } 
/* 261 */             this.state = State.READ_NAME_LENGTH;
/*     */           } 
/*     */           continue;
/*     */ 
/*     */         
/*     */         case END_HEADER_BLOCK:
/* 267 */           this.state = State.ERROR;
/* 268 */           frame.setInvalid();
/*     */           continue;
/*     */         
/*     */         case ERROR:
/* 272 */           headerBlock.skipBytes(headerBlock.readableBytes());
/*     */           return;
/*     */       } 
/*     */       
/* 276 */       throw new Error("Shouldn't reach here.");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void endHeaderBlock(SpdyHeadersFrame frame) throws Exception {
/* 283 */     if (this.state != State.END_HEADER_BLOCK) {
/* 284 */       frame.setInvalid();
/*     */     }
/*     */     
/* 287 */     releaseBuffer();
/*     */ 
/*     */     
/* 290 */     this.headerSize = 0;
/* 291 */     this.name = null;
/* 292 */     this.state = State.READ_NUM_HEADERS;
/*     */   }
/*     */ 
/*     */   
/*     */   void end() {
/* 297 */     releaseBuffer();
/*     */   }
/*     */   
/*     */   private void releaseBuffer() {
/* 301 */     if (this.cumulation != null) {
/* 302 */       this.cumulation.release();
/* 303 */       this.cumulation = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyHeaderBlockRawDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */