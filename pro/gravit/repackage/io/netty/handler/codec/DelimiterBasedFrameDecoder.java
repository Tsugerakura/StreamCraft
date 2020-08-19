/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.util.List;
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
/*     */ public class DelimiterBasedFrameDecoder
/*     */   extends ByteToMessageDecoder
/*     */ {
/*     */   private final ByteBuf[] delimiters;
/*     */   private final int maxFrameLength;
/*     */   private final boolean stripDelimiter;
/*     */   private final boolean failFast;
/*     */   private boolean discardingTooLongFrame;
/*     */   private int tooLongFrameLength;
/*     */   private final LineBasedFrameDecoder lineBasedDecoder;
/*     */   
/*     */   public DelimiterBasedFrameDecoder(int maxFrameLength, ByteBuf delimiter) {
/*  81 */     this(maxFrameLength, true, delimiter);
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
/*     */   public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, ByteBuf delimiter) {
/*  96 */     this(maxFrameLength, stripDelimiter, true, delimiter);
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
/*     */ 
/*     */   
/*     */   public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ByteBuf delimiter) {
/* 119 */     this(maxFrameLength, stripDelimiter, failFast, new ByteBuf[] { delimiter
/* 120 */           .slice(delimiter.readerIndex(), delimiter.readableBytes()) });
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
/*     */   public DelimiterBasedFrameDecoder(int maxFrameLength, ByteBuf... delimiters) {
/* 132 */     this(maxFrameLength, true, delimiters);
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
/*     */   public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, ByteBuf... delimiters) {
/* 147 */     this(maxFrameLength, stripDelimiter, true, delimiters);
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
/*     */   
/*     */   public DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ByteBuf... delimiters) {
/* 169 */     validateMaxFrameLength(maxFrameLength);
/* 170 */     ObjectUtil.checkNonEmpty((Object[])delimiters, "delimiters");
/*     */     
/* 172 */     if (isLineBased(delimiters) && !isSubclass()) {
/* 173 */       this.lineBasedDecoder = new LineBasedFrameDecoder(maxFrameLength, stripDelimiter, failFast);
/* 174 */       this.delimiters = null;
/*     */     } else {
/* 176 */       this.delimiters = new ByteBuf[delimiters.length];
/* 177 */       for (int i = 0; i < delimiters.length; i++) {
/* 178 */         ByteBuf d = delimiters[i];
/* 179 */         validateDelimiter(d);
/* 180 */         this.delimiters[i] = d.slice(d.readerIndex(), d.readableBytes());
/*     */       } 
/* 182 */       this.lineBasedDecoder = null;
/*     */     } 
/* 184 */     this.maxFrameLength = maxFrameLength;
/* 185 */     this.stripDelimiter = stripDelimiter;
/* 186 */     this.failFast = failFast;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isLineBased(ByteBuf[] delimiters) {
/* 191 */     if (delimiters.length != 2) {
/* 192 */       return false;
/*     */     }
/* 194 */     ByteBuf a = delimiters[0];
/* 195 */     ByteBuf b = delimiters[1];
/* 196 */     if (a.capacity() < b.capacity()) {
/* 197 */       a = delimiters[1];
/* 198 */       b = delimiters[0];
/*     */     } 
/* 200 */     return (a.capacity() == 2 && b.capacity() == 1 && a
/* 201 */       .getByte(0) == 13 && a.getByte(1) == 10 && b
/* 202 */       .getByte(0) == 10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isSubclass() {
/* 209 */     return (getClass() != DelimiterBasedFrameDecoder.class);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/* 214 */     Object decoded = decode(ctx, in);
/* 215 */     if (decoded != null) {
/* 216 */       out.add(decoded);
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
/*     */   protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
/* 229 */     if (this.lineBasedDecoder != null) {
/* 230 */       return this.lineBasedDecoder.decode(ctx, buffer);
/*     */     }
/*     */     
/* 233 */     int minFrameLength = Integer.MAX_VALUE;
/* 234 */     ByteBuf minDelim = null;
/* 235 */     for (ByteBuf delim : this.delimiters) {
/* 236 */       int frameLength = indexOf(buffer, delim);
/* 237 */       if (frameLength >= 0 && frameLength < minFrameLength) {
/* 238 */         minFrameLength = frameLength;
/* 239 */         minDelim = delim;
/*     */       } 
/*     */     } 
/*     */     
/* 243 */     if (minDelim != null) {
/* 244 */       ByteBuf frame; int minDelimLength = minDelim.capacity();
/*     */ 
/*     */       
/* 247 */       if (this.discardingTooLongFrame) {
/*     */ 
/*     */         
/* 250 */         this.discardingTooLongFrame = false;
/* 251 */         buffer.skipBytes(minFrameLength + minDelimLength);
/*     */         
/* 253 */         int tooLongFrameLength = this.tooLongFrameLength;
/* 254 */         this.tooLongFrameLength = 0;
/* 255 */         if (!this.failFast) {
/* 256 */           fail(tooLongFrameLength);
/*     */         }
/* 258 */         return null;
/*     */       } 
/*     */       
/* 261 */       if (minFrameLength > this.maxFrameLength) {
/*     */         
/* 263 */         buffer.skipBytes(minFrameLength + minDelimLength);
/* 264 */         fail(minFrameLength);
/* 265 */         return null;
/*     */       } 
/*     */       
/* 268 */       if (this.stripDelimiter) {
/* 269 */         frame = buffer.readRetainedSlice(minFrameLength);
/* 270 */         buffer.skipBytes(minDelimLength);
/*     */       } else {
/* 272 */         frame = buffer.readRetainedSlice(minFrameLength + minDelimLength);
/*     */       } 
/*     */       
/* 275 */       return frame;
/*     */     } 
/* 277 */     if (!this.discardingTooLongFrame) {
/* 278 */       if (buffer.readableBytes() > this.maxFrameLength) {
/*     */         
/* 280 */         this.tooLongFrameLength = buffer.readableBytes();
/* 281 */         buffer.skipBytes(buffer.readableBytes());
/* 282 */         this.discardingTooLongFrame = true;
/* 283 */         if (this.failFast) {
/* 284 */           fail(this.tooLongFrameLength);
/*     */         }
/*     */       } 
/*     */     } else {
/*     */       
/* 289 */       this.tooLongFrameLength += buffer.readableBytes();
/* 290 */       buffer.skipBytes(buffer.readableBytes());
/*     */     } 
/* 292 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private void fail(long frameLength) {
/* 297 */     if (frameLength > 0L) {
/* 298 */       throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + ": " + frameLength + " - discarded");
/*     */     }
/*     */ 
/*     */     
/* 302 */     throw new TooLongFrameException("frame length exceeds " + this.maxFrameLength + " - discarding");
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
/*     */   private static int indexOf(ByteBuf haystack, ByteBuf needle) {
/* 314 */     for (int i = haystack.readerIndex(); i < haystack.writerIndex(); i++) {
/* 315 */       int haystackIndex = i;
/*     */       int needleIndex;
/* 317 */       for (needleIndex = 0; needleIndex < needle.capacity() && 
/* 318 */         haystack.getByte(haystackIndex) == needle.getByte(needleIndex); needleIndex++) {
/*     */ 
/*     */         
/* 321 */         haystackIndex++;
/* 322 */         if (haystackIndex == haystack.writerIndex() && needleIndex != needle
/* 323 */           .capacity() - 1) {
/* 324 */           return -1;
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/* 329 */       if (needleIndex == needle.capacity())
/*     */       {
/* 331 */         return i - haystack.readerIndex();
/*     */       }
/*     */     } 
/* 334 */     return -1;
/*     */   }
/*     */   
/*     */   private static void validateDelimiter(ByteBuf delimiter) {
/* 338 */     ObjectUtil.checkNotNull(delimiter, "delimiter");
/* 339 */     if (!delimiter.isReadable()) {
/* 340 */       throw new IllegalArgumentException("empty delimiter");
/*     */     }
/*     */   }
/*     */   
/*     */   private static void validateMaxFrameLength(int maxFrameLength) {
/* 345 */     ObjectUtil.checkPositive(maxFrameLength, "maxFrameLength");
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\DelimiterBasedFrameDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */