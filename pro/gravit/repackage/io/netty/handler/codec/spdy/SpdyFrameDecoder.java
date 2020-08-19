/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
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
/*     */ public class SpdyFrameDecoder
/*     */ {
/*     */   private final int spdyVersion;
/*     */   private final int maxChunkSize;
/*     */   private final SpdyFrameDecoderDelegate delegate;
/*     */   private State state;
/*     */   private byte flags;
/*     */   private int length;
/*     */   private int streamId;
/*     */   private int numSettings;
/*     */   
/*     */   private enum State
/*     */   {
/*  66 */     READ_COMMON_HEADER,
/*  67 */     READ_DATA_FRAME,
/*  68 */     READ_SYN_STREAM_FRAME,
/*  69 */     READ_SYN_REPLY_FRAME,
/*  70 */     READ_RST_STREAM_FRAME,
/*  71 */     READ_SETTINGS_FRAME,
/*  72 */     READ_SETTING,
/*  73 */     READ_PING_FRAME,
/*  74 */     READ_GOAWAY_FRAME,
/*  75 */     READ_HEADERS_FRAME,
/*  76 */     READ_WINDOW_UPDATE_FRAME,
/*  77 */     READ_HEADER_BLOCK,
/*  78 */     DISCARD_FRAME,
/*  79 */     FRAME_ERROR;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SpdyFrameDecoder(SpdyVersion spdyVersion, SpdyFrameDecoderDelegate delegate) {
/*  87 */     this(spdyVersion, delegate, 8192);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SpdyFrameDecoder(SpdyVersion spdyVersion, SpdyFrameDecoderDelegate delegate, int maxChunkSize) {
/*  94 */     this.spdyVersion = ((SpdyVersion)ObjectUtil.checkNotNull(spdyVersion, "spdyVersion")).getVersion();
/*  95 */     this.delegate = (SpdyFrameDecoderDelegate)ObjectUtil.checkNotNull(delegate, "delegate");
/*  96 */     this.maxChunkSize = ObjectUtil.checkPositive(maxChunkSize, "maxChunkSize");
/*  97 */     this.state = State.READ_COMMON_HEADER; } public void decode(ByteBuf buffer) { while (true) {
/*     */       boolean last; int statusCode; int frameOffset; int flagsOffset; int lengthOffset; boolean control; int version; int type; int dataLength; ByteBuf data; int offset; int associatedToStreamId; byte priority; boolean unidirectional; boolean clear; byte settingsFlags; int id; int value; boolean persistValue; boolean persisted;
/*     */       int pingId;
/*     */       int lastGoodStreamId;
/*     */       int deltaWindowSize;
/*     */       int compressedBytes;
/*     */       ByteBuf headerBlock;
/*     */       int numBytes;
/* 105 */       switch (this.state) {
/*     */         case READ_COMMON_HEADER:
/* 107 */           if (buffer.readableBytes() < 8) {
/*     */             return;
/*     */           }
/*     */           
/* 111 */           frameOffset = buffer.readerIndex();
/* 112 */           flagsOffset = frameOffset + 4;
/* 113 */           lengthOffset = frameOffset + 5;
/* 114 */           buffer.skipBytes(8);
/*     */           
/* 116 */           control = ((buffer.getByte(frameOffset) & 0x80) != 0);
/*     */ 
/*     */ 
/*     */           
/* 120 */           if (control) {
/*     */             
/* 122 */             version = SpdyCodecUtil.getUnsignedShort(buffer, frameOffset) & 0x7FFF;
/* 123 */             type = SpdyCodecUtil.getUnsignedShort(buffer, frameOffset + 2);
/* 124 */             this.streamId = 0;
/*     */           } else {
/*     */             
/* 127 */             version = this.spdyVersion;
/* 128 */             type = 0;
/* 129 */             this.streamId = SpdyCodecUtil.getUnsignedInt(buffer, frameOffset);
/*     */           } 
/*     */           
/* 132 */           this.flags = buffer.getByte(flagsOffset);
/* 133 */           this.length = SpdyCodecUtil.getUnsignedMedium(buffer, lengthOffset);
/*     */ 
/*     */           
/* 136 */           if (version != this.spdyVersion) {
/* 137 */             this.state = State.FRAME_ERROR;
/* 138 */             this.delegate.readFrameError("Invalid SPDY Version"); continue;
/* 139 */           }  if (!isValidFrameHeader(this.streamId, type, this.flags, this.length)) {
/* 140 */             this.state = State.FRAME_ERROR;
/* 141 */             this.delegate.readFrameError("Invalid Frame Error"); continue;
/*     */           } 
/* 143 */           this.state = getNextState(type, this.length);
/*     */           continue;
/*     */ 
/*     */         
/*     */         case READ_DATA_FRAME:
/* 148 */           if (this.length == 0) {
/* 149 */             this.state = State.READ_COMMON_HEADER;
/* 150 */             this.delegate.readDataFrame(this.streamId, hasFlag(this.flags, (byte)1), Unpooled.buffer(0));
/*     */             
/*     */             continue;
/*     */           } 
/*     */           
/* 155 */           dataLength = Math.min(this.maxChunkSize, this.length);
/*     */ 
/*     */           
/* 158 */           if (buffer.readableBytes() < dataLength) {
/*     */             return;
/*     */           }
/*     */           
/* 162 */           data = buffer.alloc().buffer(dataLength);
/* 163 */           data.writeBytes(buffer, dataLength);
/* 164 */           this.length -= dataLength;
/*     */           
/* 166 */           if (this.length == 0) {
/* 167 */             this.state = State.READ_COMMON_HEADER;
/*     */           }
/*     */           
/* 170 */           last = (this.length == 0 && hasFlag(this.flags, (byte)1));
/*     */           
/* 172 */           this.delegate.readDataFrame(this.streamId, last, data);
/*     */           continue;
/*     */         
/*     */         case READ_SYN_STREAM_FRAME:
/* 176 */           if (buffer.readableBytes() < 10) {
/*     */             return;
/*     */           }
/*     */           
/* 180 */           offset = buffer.readerIndex();
/* 181 */           this.streamId = SpdyCodecUtil.getUnsignedInt(buffer, offset);
/* 182 */           associatedToStreamId = SpdyCodecUtil.getUnsignedInt(buffer, offset + 4);
/* 183 */           priority = (byte)(buffer.getByte(offset + 8) >> 5 & 0x7);
/* 184 */           last = hasFlag(this.flags, (byte)1);
/* 185 */           unidirectional = hasFlag(this.flags, (byte)2);
/* 186 */           buffer.skipBytes(10);
/* 187 */           this.length -= 10;
/*     */           
/* 189 */           if (this.streamId == 0) {
/* 190 */             this.state = State.FRAME_ERROR;
/* 191 */             this.delegate.readFrameError("Invalid SYN_STREAM Frame"); continue;
/*     */           } 
/* 193 */           this.state = State.READ_HEADER_BLOCK;
/* 194 */           this.delegate.readSynStreamFrame(this.streamId, associatedToStreamId, priority, last, unidirectional);
/*     */           continue;
/*     */ 
/*     */         
/*     */         case READ_SYN_REPLY_FRAME:
/* 199 */           if (buffer.readableBytes() < 4) {
/*     */             return;
/*     */           }
/*     */           
/* 203 */           this.streamId = SpdyCodecUtil.getUnsignedInt(buffer, buffer.readerIndex());
/* 204 */           last = hasFlag(this.flags, (byte)1);
/*     */           
/* 206 */           buffer.skipBytes(4);
/* 207 */           this.length -= 4;
/*     */           
/* 209 */           if (this.streamId == 0) {
/* 210 */             this.state = State.FRAME_ERROR;
/* 211 */             this.delegate.readFrameError("Invalid SYN_REPLY Frame"); continue;
/*     */           } 
/* 213 */           this.state = State.READ_HEADER_BLOCK;
/* 214 */           this.delegate.readSynReplyFrame(this.streamId, last);
/*     */           continue;
/*     */ 
/*     */         
/*     */         case READ_RST_STREAM_FRAME:
/* 219 */           if (buffer.readableBytes() < 8) {
/*     */             return;
/*     */           }
/*     */           
/* 223 */           this.streamId = SpdyCodecUtil.getUnsignedInt(buffer, buffer.readerIndex());
/* 224 */           statusCode = SpdyCodecUtil.getSignedInt(buffer, buffer.readerIndex() + 4);
/* 225 */           buffer.skipBytes(8);
/*     */           
/* 227 */           if (this.streamId == 0 || statusCode == 0) {
/* 228 */             this.state = State.FRAME_ERROR;
/* 229 */             this.delegate.readFrameError("Invalid RST_STREAM Frame"); continue;
/*     */           } 
/* 231 */           this.state = State.READ_COMMON_HEADER;
/* 232 */           this.delegate.readRstStreamFrame(this.streamId, statusCode);
/*     */           continue;
/*     */ 
/*     */         
/*     */         case READ_SETTINGS_FRAME:
/* 237 */           if (buffer.readableBytes() < 4) {
/*     */             return;
/*     */           }
/*     */           
/* 241 */           clear = hasFlag(this.flags, (byte)1);
/*     */           
/* 243 */           this.numSettings = SpdyCodecUtil.getUnsignedInt(buffer, buffer.readerIndex());
/* 244 */           buffer.skipBytes(4);
/* 245 */           this.length -= 4;
/*     */ 
/*     */           
/* 248 */           if ((this.length & 0x7) != 0 || this.length >> 3 != this.numSettings) {
/* 249 */             this.state = State.FRAME_ERROR;
/* 250 */             this.delegate.readFrameError("Invalid SETTINGS Frame"); continue;
/*     */           } 
/* 252 */           this.state = State.READ_SETTING;
/* 253 */           this.delegate.readSettingsFrame(clear);
/*     */           continue;
/*     */ 
/*     */         
/*     */         case READ_SETTING:
/* 258 */           if (this.numSettings == 0) {
/* 259 */             this.state = State.READ_COMMON_HEADER;
/* 260 */             this.delegate.readSettingsEnd();
/*     */             
/*     */             continue;
/*     */           } 
/* 264 */           if (buffer.readableBytes() < 8) {
/*     */             return;
/*     */           }
/*     */           
/* 268 */           settingsFlags = buffer.getByte(buffer.readerIndex());
/* 269 */           id = SpdyCodecUtil.getUnsignedMedium(buffer, buffer.readerIndex() + 1);
/* 270 */           value = SpdyCodecUtil.getSignedInt(buffer, buffer.readerIndex() + 4);
/* 271 */           persistValue = hasFlag(settingsFlags, (byte)1);
/* 272 */           persisted = hasFlag(settingsFlags, (byte)2);
/* 273 */           buffer.skipBytes(8);
/*     */           
/* 275 */           this.numSettings--;
/*     */           
/* 277 */           this.delegate.readSetting(id, value, persistValue, persisted);
/*     */           continue;
/*     */         
/*     */         case READ_PING_FRAME:
/* 281 */           if (buffer.readableBytes() < 4) {
/*     */             return;
/*     */           }
/*     */           
/* 285 */           pingId = SpdyCodecUtil.getSignedInt(buffer, buffer.readerIndex());
/* 286 */           buffer.skipBytes(4);
/*     */           
/* 288 */           this.state = State.READ_COMMON_HEADER;
/* 289 */           this.delegate.readPingFrame(pingId);
/*     */           continue;
/*     */         
/*     */         case READ_GOAWAY_FRAME:
/* 293 */           if (buffer.readableBytes() < 8) {
/*     */             return;
/*     */           }
/*     */           
/* 297 */           lastGoodStreamId = SpdyCodecUtil.getUnsignedInt(buffer, buffer.readerIndex());
/* 298 */           statusCode = SpdyCodecUtil.getSignedInt(buffer, buffer.readerIndex() + 4);
/* 299 */           buffer.skipBytes(8);
/*     */           
/* 301 */           this.state = State.READ_COMMON_HEADER;
/* 302 */           this.delegate.readGoAwayFrame(lastGoodStreamId, statusCode);
/*     */           continue;
/*     */         
/*     */         case READ_HEADERS_FRAME:
/* 306 */           if (buffer.readableBytes() < 4) {
/*     */             return;
/*     */           }
/*     */           
/* 310 */           this.streamId = SpdyCodecUtil.getUnsignedInt(buffer, buffer.readerIndex());
/* 311 */           last = hasFlag(this.flags, (byte)1);
/*     */           
/* 313 */           buffer.skipBytes(4);
/* 314 */           this.length -= 4;
/*     */           
/* 316 */           if (this.streamId == 0) {
/* 317 */             this.state = State.FRAME_ERROR;
/* 318 */             this.delegate.readFrameError("Invalid HEADERS Frame"); continue;
/*     */           } 
/* 320 */           this.state = State.READ_HEADER_BLOCK;
/* 321 */           this.delegate.readHeadersFrame(this.streamId, last);
/*     */           continue;
/*     */ 
/*     */         
/*     */         case READ_WINDOW_UPDATE_FRAME:
/* 326 */           if (buffer.readableBytes() < 8) {
/*     */             return;
/*     */           }
/*     */           
/* 330 */           this.streamId = SpdyCodecUtil.getUnsignedInt(buffer, buffer.readerIndex());
/* 331 */           deltaWindowSize = SpdyCodecUtil.getUnsignedInt(buffer, buffer.readerIndex() + 4);
/* 332 */           buffer.skipBytes(8);
/*     */           
/* 334 */           if (deltaWindowSize == 0) {
/* 335 */             this.state = State.FRAME_ERROR;
/* 336 */             this.delegate.readFrameError("Invalid WINDOW_UPDATE Frame"); continue;
/*     */           } 
/* 338 */           this.state = State.READ_COMMON_HEADER;
/* 339 */           this.delegate.readWindowUpdateFrame(this.streamId, deltaWindowSize);
/*     */           continue;
/*     */ 
/*     */         
/*     */         case READ_HEADER_BLOCK:
/* 344 */           if (this.length == 0) {
/* 345 */             this.state = State.READ_COMMON_HEADER;
/* 346 */             this.delegate.readHeaderBlockEnd();
/*     */             
/*     */             continue;
/*     */           } 
/* 350 */           if (!buffer.isReadable()) {
/*     */             return;
/*     */           }
/*     */           
/* 354 */           compressedBytes = Math.min(buffer.readableBytes(), this.length);
/* 355 */           headerBlock = buffer.alloc().buffer(compressedBytes);
/* 356 */           headerBlock.writeBytes(buffer, compressedBytes);
/* 357 */           this.length -= compressedBytes;
/*     */           
/* 359 */           this.delegate.readHeaderBlock(headerBlock);
/*     */           continue;
/*     */         
/*     */         case DISCARD_FRAME:
/* 363 */           numBytes = Math.min(buffer.readableBytes(), this.length);
/* 364 */           buffer.skipBytes(numBytes);
/* 365 */           this.length -= numBytes;
/* 366 */           if (this.length == 0) {
/* 367 */             this.state = State.READ_COMMON_HEADER;
/*     */             continue;
/*     */           } 
/*     */           return;
/*     */         
/*     */         case FRAME_ERROR:
/* 373 */           buffer.skipBytes(buffer.readableBytes()); return;
/*     */       } 
/*     */       break;
/*     */     } 
/* 377 */     throw new Error("Shouldn't reach here."); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean hasFlag(byte flags, byte flag) {
/* 383 */     return ((flags & flag) != 0);
/*     */   }
/*     */   
/*     */   private static State getNextState(int type, int length) {
/* 387 */     switch (type) {
/*     */       case 0:
/* 389 */         return State.READ_DATA_FRAME;
/*     */       
/*     */       case 1:
/* 392 */         return State.READ_SYN_STREAM_FRAME;
/*     */       
/*     */       case 2:
/* 395 */         return State.READ_SYN_REPLY_FRAME;
/*     */       
/*     */       case 3:
/* 398 */         return State.READ_RST_STREAM_FRAME;
/*     */       
/*     */       case 4:
/* 401 */         return State.READ_SETTINGS_FRAME;
/*     */       
/*     */       case 6:
/* 404 */         return State.READ_PING_FRAME;
/*     */       
/*     */       case 7:
/* 407 */         return State.READ_GOAWAY_FRAME;
/*     */       
/*     */       case 8:
/* 410 */         return State.READ_HEADERS_FRAME;
/*     */       
/*     */       case 9:
/* 413 */         return State.READ_WINDOW_UPDATE_FRAME;
/*     */     } 
/*     */     
/* 416 */     if (length != 0) {
/* 417 */       return State.DISCARD_FRAME;
/*     */     }
/* 419 */     return State.READ_COMMON_HEADER;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isValidFrameHeader(int streamId, int type, byte flags, int length) {
/* 425 */     switch (type) {
/*     */       case 0:
/* 427 */         return (streamId != 0);
/*     */       
/*     */       case 1:
/* 430 */         return (length >= 10);
/*     */       
/*     */       case 2:
/* 433 */         return (length >= 4);
/*     */       
/*     */       case 3:
/* 436 */         return (flags == 0 && length == 8);
/*     */       
/*     */       case 4:
/* 439 */         return (length >= 4);
/*     */       
/*     */       case 6:
/* 442 */         return (length == 4);
/*     */       
/*     */       case 7:
/* 445 */         return (length == 8);
/*     */       
/*     */       case 8:
/* 448 */         return (length >= 4);
/*     */       
/*     */       case 9:
/* 451 */         return (length == 8);
/*     */     } 
/*     */     
/* 454 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyFrameDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */