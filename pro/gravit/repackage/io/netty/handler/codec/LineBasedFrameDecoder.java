/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
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
/*     */ public class LineBasedFrameDecoder
/*     */   extends ByteToMessageDecoder
/*     */ {
/*     */   private final int maxLength;
/*     */   private final boolean failFast;
/*     */   private final boolean stripDelimiter;
/*     */   private boolean discarding;
/*     */   private int discardedBytes;
/*     */   private int offset;
/*     */   
/*     */   public LineBasedFrameDecoder(int maxLength) {
/*  58 */     this(maxLength, true, false);
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
/*     */   public LineBasedFrameDecoder(int maxLength, boolean stripDelimiter, boolean failFast) {
/*  77 */     this.maxLength = maxLength;
/*  78 */     this.failFast = failFast;
/*  79 */     this.stripDelimiter = stripDelimiter;
/*     */   }
/*     */ 
/*     */   
/*     */   protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/*  84 */     Object decoded = decode(ctx, in);
/*  85 */     if (decoded != null) {
/*  86 */       out.add(decoded);
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
/*  99 */     int eol = findEndOfLine(buffer);
/* 100 */     if (!this.discarding) {
/* 101 */       if (eol >= 0) {
/*     */         ByteBuf frame;
/* 103 */         int i = eol - buffer.readerIndex();
/* 104 */         int delimLength = (buffer.getByte(eol) == 13) ? 2 : 1;
/*     */         
/* 106 */         if (i > this.maxLength) {
/* 107 */           buffer.readerIndex(eol + delimLength);
/* 108 */           fail(ctx, i);
/* 109 */           return null;
/*     */         } 
/*     */         
/* 112 */         if (this.stripDelimiter) {
/* 113 */           frame = buffer.readRetainedSlice(i);
/* 114 */           buffer.skipBytes(delimLength);
/*     */         } else {
/* 116 */           frame = buffer.readRetainedSlice(i + delimLength);
/*     */         } 
/*     */         
/* 119 */         return frame;
/*     */       } 
/* 121 */       int length = buffer.readableBytes();
/* 122 */       if (length > this.maxLength) {
/* 123 */         this.discardedBytes = length;
/* 124 */         buffer.readerIndex(buffer.writerIndex());
/* 125 */         this.discarding = true;
/* 126 */         this.offset = 0;
/* 127 */         if (this.failFast) {
/* 128 */           fail(ctx, "over " + this.discardedBytes);
/*     */         }
/*     */       } 
/* 131 */       return null;
/*     */     } 
/*     */     
/* 134 */     if (eol >= 0) {
/* 135 */       int length = this.discardedBytes + eol - buffer.readerIndex();
/* 136 */       int delimLength = (buffer.getByte(eol) == 13) ? 2 : 1;
/* 137 */       buffer.readerIndex(eol + delimLength);
/* 138 */       this.discardedBytes = 0;
/* 139 */       this.discarding = false;
/* 140 */       if (!this.failFast) {
/* 141 */         fail(ctx, length);
/*     */       }
/*     */     } else {
/* 144 */       this.discardedBytes += buffer.readableBytes();
/* 145 */       buffer.readerIndex(buffer.writerIndex());
/*     */       
/* 147 */       this.offset = 0;
/*     */     } 
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private void fail(ChannelHandlerContext ctx, int length) {
/* 154 */     fail(ctx, String.valueOf(length));
/*     */   }
/*     */   
/*     */   private void fail(ChannelHandlerContext ctx, String length) {
/* 158 */     ctx.fireExceptionCaught(new TooLongFrameException("frame length (" + length + ") exceeds the allowed maximum (" + this.maxLength + ')'));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int findEndOfLine(ByteBuf buffer) {
/* 168 */     int totalLength = buffer.readableBytes();
/* 169 */     int i = buffer.forEachByte(buffer.readerIndex() + this.offset, totalLength - this.offset, ByteProcessor.FIND_LF);
/* 170 */     if (i >= 0) {
/* 171 */       this.offset = 0;
/* 172 */       if (i > 0 && buffer.getByte(i - 1) == 13) {
/* 173 */         i--;
/*     */       }
/*     */     } else {
/* 176 */       this.offset = totalLength;
/*     */     } 
/* 178 */     return i;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\LineBasedFrameDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */