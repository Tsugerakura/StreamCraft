/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.zip.CRC32;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Inflater;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
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
/*     */ 
/*     */ public class JdkZlibDecoder
/*     */   extends ZlibDecoder
/*     */ {
/*     */   private static final int FHCRC = 2;
/*     */   private static final int FEXTRA = 4;
/*     */   private static final int FNAME = 8;
/*     */   private static final int FCOMMENT = 16;
/*     */   private static final int FRESERVED = 224;
/*     */   private Inflater inflater;
/*     */   private final byte[] dictionary;
/*     */   private final ByteBufChecksum crc;
/*     */   private final boolean decompressConcatenated;
/*     */   
/*     */   private enum GzipState
/*     */   {
/*  46 */     HEADER_START,
/*  47 */     HEADER_END,
/*  48 */     FLG_READ,
/*  49 */     XLEN_READ,
/*  50 */     SKIP_FNAME,
/*  51 */     SKIP_COMMENT,
/*  52 */     PROCESS_FHCRC,
/*  53 */     FOOTER_START;
/*     */   }
/*     */   
/*  56 */   private GzipState gzipState = GzipState.HEADER_START;
/*  57 */   private int flags = -1;
/*  58 */   private int xlen = -1;
/*     */ 
/*     */   
/*     */   private volatile boolean finished;
/*     */ 
/*     */   
/*     */   private boolean decideZlibOrNone;
/*     */ 
/*     */   
/*     */   public JdkZlibDecoder() {
/*  68 */     this(ZlibWrapper.ZLIB, null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdkZlibDecoder(byte[] dictionary) {
/*  77 */     this(ZlibWrapper.ZLIB, dictionary, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JdkZlibDecoder(ZlibWrapper wrapper) {
/*  86 */     this(wrapper, null, false);
/*     */   }
/*     */   
/*     */   public JdkZlibDecoder(ZlibWrapper wrapper, boolean decompressConcatenated) {
/*  90 */     this(wrapper, null, decompressConcatenated);
/*     */   }
/*     */   
/*     */   public JdkZlibDecoder(boolean decompressConcatenated) {
/*  94 */     this(ZlibWrapper.GZIP, null, decompressConcatenated);
/*     */   }
/*     */   
/*     */   private JdkZlibDecoder(ZlibWrapper wrapper, byte[] dictionary, boolean decompressConcatenated) {
/*  98 */     ObjectUtil.checkNotNull(wrapper, "wrapper");
/*     */     
/* 100 */     this.decompressConcatenated = decompressConcatenated;
/* 101 */     switch (wrapper) {
/*     */       case FOOTER_START:
/* 103 */         this.inflater = new Inflater(true);
/* 104 */         this.crc = ByteBufChecksum.wrapChecksum(new CRC32());
/*     */         break;
/*     */       case HEADER_START:
/* 107 */         this.inflater = new Inflater(true);
/* 108 */         this.crc = null;
/*     */         break;
/*     */       case FLG_READ:
/* 111 */         this.inflater = new Inflater();
/* 112 */         this.crc = null;
/*     */         break;
/*     */       
/*     */       case XLEN_READ:
/* 116 */         this.decideZlibOrNone = true;
/* 117 */         this.crc = null;
/*     */         break;
/*     */       default:
/* 120 */         throw new IllegalArgumentException("Only GZIP or ZLIB is supported, but you used " + wrapper);
/*     */     } 
/* 122 */     this.dictionary = dictionary;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 127 */     return this.finished;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/* 132 */     if (this.finished) {
/*     */       
/* 134 */       in.skipBytes(in.readableBytes());
/*     */       
/*     */       return;
/*     */     } 
/* 138 */     int readableBytes = in.readableBytes();
/* 139 */     if (readableBytes == 0) {
/*     */       return;
/*     */     }
/*     */     
/* 143 */     if (this.decideZlibOrNone) {
/*     */       
/* 145 */       if (readableBytes < 2) {
/*     */         return;
/*     */       }
/*     */       
/* 149 */       boolean nowrap = !looksLikeZlib(in.getShort(in.readerIndex()));
/* 150 */       this.inflater = new Inflater(nowrap);
/* 151 */       this.decideZlibOrNone = false;
/*     */     } 
/*     */     
/* 154 */     if (this.crc != null) {
/* 155 */       switch (this.gzipState) {
/*     */         case FOOTER_START:
/* 157 */           if (readGZIPFooter(in)) {
/* 158 */             this.finished = true;
/*     */           }
/*     */           return;
/*     */       } 
/* 162 */       if (this.gzipState != GzipState.HEADER_END && 
/* 163 */         !readGZIPHeader(in)) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 169 */       readableBytes = in.readableBytes();
/*     */     } 
/*     */     
/* 172 */     if (in.hasArray()) {
/* 173 */       this.inflater.setInput(in.array(), in.arrayOffset() + in.readerIndex(), readableBytes);
/*     */     } else {
/* 175 */       byte[] array = new byte[readableBytes];
/* 176 */       in.getBytes(in.readerIndex(), array);
/* 177 */       this.inflater.setInput(array);
/*     */     } 
/*     */     
/* 180 */     ByteBuf decompressed = ctx.alloc().heapBuffer(this.inflater.getRemaining() << 1);
/*     */     try {
/* 182 */       boolean readFooter = false;
/* 183 */       while (!this.inflater.needsInput()) {
/* 184 */         byte[] outArray = decompressed.array();
/* 185 */         int writerIndex = decompressed.writerIndex();
/* 186 */         int outIndex = decompressed.arrayOffset() + writerIndex;
/* 187 */         int outputLength = this.inflater.inflate(outArray, outIndex, decompressed.writableBytes());
/* 188 */         if (outputLength > 0) {
/* 189 */           decompressed.writerIndex(writerIndex + outputLength);
/* 190 */           if (this.crc != null) {
/* 191 */             this.crc.update(outArray, outIndex, outputLength);
/*     */           }
/*     */         }
/* 194 */         else if (this.inflater.needsDictionary()) {
/* 195 */           if (this.dictionary == null) {
/* 196 */             throw new DecompressionException("decompression failure, unable to set dictionary as non was specified");
/*     */           }
/*     */           
/* 199 */           this.inflater.setDictionary(this.dictionary);
/*     */         } 
/*     */ 
/*     */         
/* 203 */         if (this.inflater.finished()) {
/* 204 */           if (this.crc == null) {
/* 205 */             this.finished = true; break;
/*     */           } 
/* 207 */           readFooter = true;
/*     */           
/*     */           break;
/*     */         } 
/* 211 */         decompressed.ensureWritable(this.inflater.getRemaining() << 1);
/*     */       } 
/*     */ 
/*     */       
/* 215 */       in.skipBytes(readableBytes - this.inflater.getRemaining());
/*     */       
/* 217 */       if (readFooter) {
/* 218 */         this.gzipState = GzipState.FOOTER_START;
/* 219 */         if (readGZIPFooter(in)) {
/* 220 */           this.finished = !this.decompressConcatenated;
/*     */           
/* 222 */           if (!this.finished) {
/* 223 */             this.inflater.reset();
/* 224 */             this.crc.reset();
/* 225 */             this.gzipState = GzipState.HEADER_START;
/*     */           } 
/*     */         } 
/*     */       } 
/* 229 */     } catch (DataFormatException e) {
/* 230 */       throw new DecompressionException("decompression failure", e);
/*     */     } finally {
/*     */       
/* 233 */       if (decompressed.isReadable()) {
/* 234 */         out.add(decompressed);
/*     */       } else {
/* 236 */         decompressed.release();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
/* 243 */     super.handlerRemoved0(ctx);
/* 244 */     if (this.inflater != null)
/* 245 */       this.inflater.end(); 
/*     */   } private boolean readGZIPHeader(ByteBuf in) {
/*     */     int magic0;
/*     */     int magic1;
/*     */     int method;
/* 250 */     switch (this.gzipState) {
/*     */       case HEADER_START:
/* 252 */         if (in.readableBytes() < 10) {
/* 253 */           return false;
/*     */         }
/*     */         
/* 256 */         magic0 = in.readByte();
/* 257 */         magic1 = in.readByte();
/*     */         
/* 259 */         if (magic0 != 31) {
/* 260 */           throw new DecompressionException("Input is not in the GZIP format");
/*     */         }
/* 262 */         this.crc.update(magic0);
/* 263 */         this.crc.update(magic1);
/*     */         
/* 265 */         method = in.readUnsignedByte();
/* 266 */         if (method != 8) {
/* 267 */           throw new DecompressionException("Unsupported compression method " + method + " in the GZIP header");
/*     */         }
/*     */         
/* 270 */         this.crc.update(method);
/*     */         
/* 272 */         this.flags = in.readUnsignedByte();
/* 273 */         this.crc.update(this.flags);
/*     */         
/* 275 */         if ((this.flags & 0xE0) != 0) {
/* 276 */           throw new DecompressionException("Reserved flags are set in the GZIP header");
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 281 */         this.crc.update(in, in.readerIndex(), 4);
/* 282 */         in.skipBytes(4);
/*     */         
/* 284 */         this.crc.update(in.readUnsignedByte());
/* 285 */         this.crc.update(in.readUnsignedByte());
/*     */         
/* 287 */         this.gzipState = GzipState.FLG_READ;
/*     */       
/*     */       case FLG_READ:
/* 290 */         if ((this.flags & 0x4) != 0) {
/* 291 */           if (in.readableBytes() < 2) {
/* 292 */             return false;
/*     */           }
/* 294 */           int xlen1 = in.readUnsignedByte();
/* 295 */           int xlen2 = in.readUnsignedByte();
/* 296 */           this.crc.update(xlen1);
/* 297 */           this.crc.update(xlen2);
/*     */           
/* 299 */           this.xlen |= xlen1 << 8 | xlen2;
/*     */         } 
/* 301 */         this.gzipState = GzipState.XLEN_READ;
/*     */       
/*     */       case XLEN_READ:
/* 304 */         if (this.xlen != -1) {
/* 305 */           if (in.readableBytes() < this.xlen) {
/* 306 */             return false;
/*     */           }
/* 308 */           this.crc.update(in, in.readerIndex(), this.xlen);
/* 309 */           in.skipBytes(this.xlen);
/*     */         } 
/* 311 */         this.gzipState = GzipState.SKIP_FNAME;
/*     */       
/*     */       case SKIP_FNAME:
/* 314 */         if ((this.flags & 0x8) != 0) {
/* 315 */           if (!in.isReadable()) {
/* 316 */             return false;
/*     */           }
/*     */           do {
/* 319 */             int b = in.readUnsignedByte();
/* 320 */             this.crc.update(b);
/* 321 */             if (b == 0) {
/*     */               break;
/*     */             }
/* 324 */           } while (in.isReadable());
/*     */         } 
/* 326 */         this.gzipState = GzipState.SKIP_COMMENT;
/*     */       
/*     */       case SKIP_COMMENT:
/* 329 */         if ((this.flags & 0x10) != 0) {
/* 330 */           if (!in.isReadable()) {
/* 331 */             return false;
/*     */           }
/*     */           do {
/* 334 */             int b = in.readUnsignedByte();
/* 335 */             this.crc.update(b);
/* 336 */             if (b == 0) {
/*     */               break;
/*     */             }
/* 339 */           } while (in.isReadable());
/*     */         } 
/* 341 */         this.gzipState = GzipState.PROCESS_FHCRC;
/*     */       
/*     */       case PROCESS_FHCRC:
/* 344 */         if ((this.flags & 0x2) != 0) {
/* 345 */           if (in.readableBytes() < 4) {
/* 346 */             return false;
/*     */           }
/* 348 */           verifyCrc(in);
/*     */         } 
/* 350 */         this.crc.reset();
/* 351 */         this.gzipState = GzipState.HEADER_END;
/*     */       
/*     */       case HEADER_END:
/* 354 */         return true;
/*     */     } 
/* 356 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean readGZIPFooter(ByteBuf buf) {
/* 361 */     if (buf.readableBytes() < 8) {
/* 362 */       return false;
/*     */     }
/*     */     
/* 365 */     verifyCrc(buf);
/*     */ 
/*     */     
/* 368 */     int dataLength = 0;
/* 369 */     for (int i = 0; i < 4; i++) {
/* 370 */       dataLength |= buf.readUnsignedByte() << i * 8;
/*     */     }
/* 372 */     int readLength = this.inflater.getTotalOut();
/* 373 */     if (dataLength != readLength) {
/* 374 */       throw new DecompressionException("Number of bytes mismatch. Expected: " + dataLength + ", Got: " + readLength);
/*     */     }
/*     */     
/* 377 */     return true;
/*     */   }
/*     */   
/*     */   private void verifyCrc(ByteBuf in) {
/* 381 */     long crcValue = 0L;
/* 382 */     for (int i = 0; i < 4; i++) {
/* 383 */       crcValue |= in.readUnsignedByte() << i * 8;
/*     */     }
/* 385 */     long readCrc = this.crc.getValue();
/* 386 */     if (crcValue != readCrc) {
/* 387 */       throw new DecompressionException("CRC value mismatch. Expected: " + crcValue + ", Got: " + readCrc);
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
/*     */   private static boolean looksLikeZlib(short cmf_flg) {
/* 400 */     return ((cmf_flg & 0x7800) == 30720 && cmf_flg % 31 == 0);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\JdkZlibDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */