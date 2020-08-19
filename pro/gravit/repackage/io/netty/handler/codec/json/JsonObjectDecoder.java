/*     */ package pro.gravit.repackage.io.netty.handler.codec.json;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ByteToMessageDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.CorruptedFrameException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.TooLongFrameException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonObjectDecoder
/*     */   extends ByteToMessageDecoder
/*     */ {
/*     */   private static final int ST_CORRUPTED = -1;
/*     */   private static final int ST_INIT = 0;
/*     */   private static final int ST_DECODING_NORMAL = 1;
/*     */   private static final int ST_DECODING_ARRAY_STREAM = 2;
/*     */   private int openBraces;
/*     */   private int idx;
/*     */   private int lastReaderIndex;
/*     */   private int state;
/*     */   private boolean insideString;
/*     */   private final int maxObjectLength;
/*     */   private final boolean streamArrayElements;
/*     */   
/*     */   public JsonObjectDecoder() {
/*  63 */     this(1048576);
/*     */   }
/*     */   
/*     */   public JsonObjectDecoder(int maxObjectLength) {
/*  67 */     this(maxObjectLength, false);
/*     */   }
/*     */   
/*     */   public JsonObjectDecoder(boolean streamArrayElements) {
/*  71 */     this(1048576, streamArrayElements);
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
/*     */   public JsonObjectDecoder(int maxObjectLength, boolean streamArrayElements) {
/*  84 */     if (maxObjectLength < 1) {
/*  85 */       throw new IllegalArgumentException("maxObjectLength must be a positive int");
/*     */     }
/*  87 */     this.maxObjectLength = maxObjectLength;
/*  88 */     this.streamArrayElements = streamArrayElements;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/*  93 */     if (this.state == -1) {
/*  94 */       in.skipBytes(in.readableBytes());
/*     */       
/*     */       return;
/*     */     } 
/*  98 */     if (this.idx > in.readerIndex() && this.lastReaderIndex != in.readerIndex()) {
/*  99 */       this.idx = in.readerIndex() + this.idx - this.lastReaderIndex;
/*     */     }
/*     */ 
/*     */     
/* 103 */     int idx = this.idx;
/* 104 */     int wrtIdx = in.writerIndex();
/*     */     
/* 106 */     if (wrtIdx > this.maxObjectLength) {
/*     */       
/* 108 */       in.skipBytes(in.readableBytes());
/* 109 */       reset();
/* 110 */       throw new TooLongFrameException("object length exceeds " + this.maxObjectLength + ": " + wrtIdx + " bytes discarded");
/*     */     } 
/*     */ 
/*     */     
/* 114 */     for (; idx < wrtIdx; idx++) {
/* 115 */       byte c = in.getByte(idx);
/* 116 */       if (this.state == 1) {
/* 117 */         decodeByte(c, in, idx);
/*     */ 
/*     */ 
/*     */         
/* 121 */         if (this.openBraces == 0) {
/* 122 */           ByteBuf json = extractObject(ctx, in, in.readerIndex(), idx + 1 - in.readerIndex());
/* 123 */           if (json != null) {
/* 124 */             out.add(json);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 129 */           in.readerIndex(idx + 1);
/*     */ 
/*     */           
/* 132 */           reset();
/*     */         } 
/* 134 */       } else if (this.state == 2) {
/* 135 */         decodeByte(c, in, idx);
/*     */         
/* 137 */         if (!this.insideString && ((this.openBraces == 1 && c == 44) || (this.openBraces == 0 && c == 93)))
/*     */         {
/*     */           
/* 140 */           for (int i = in.readerIndex(); Character.isWhitespace(in.getByte(i)); i++) {
/* 141 */             in.skipBytes(1);
/*     */           }
/*     */ 
/*     */           
/* 145 */           int idxNoSpaces = idx - 1;
/* 146 */           while (idxNoSpaces >= in.readerIndex() && Character.isWhitespace(in.getByte(idxNoSpaces))) {
/* 147 */             idxNoSpaces--;
/*     */           }
/*     */           
/* 150 */           ByteBuf json = extractObject(ctx, in, in.readerIndex(), idxNoSpaces + 1 - in.readerIndex());
/* 151 */           if (json != null) {
/* 152 */             out.add(json);
/*     */           }
/*     */           
/* 155 */           in.readerIndex(idx + 1);
/*     */           
/* 157 */           if (c == 93) {
/* 158 */             reset();
/*     */           }
/*     */         }
/*     */       
/* 162 */       } else if (c == 123 || c == 91) {
/* 163 */         initDecoding(c);
/*     */         
/* 165 */         if (this.state == 2)
/*     */         {
/* 167 */           in.skipBytes(1);
/*     */         }
/*     */       }
/* 170 */       else if (Character.isWhitespace(c)) {
/* 171 */         in.skipBytes(1);
/*     */       } else {
/* 173 */         this.state = -1;
/* 174 */         throw new CorruptedFrameException("invalid JSON received at byte position " + idx + ": " + 
/* 175 */             ByteBufUtil.hexDump(in));
/*     */       } 
/*     */     } 
/*     */     
/* 179 */     if (in.readableBytes() == 0) {
/* 180 */       this.idx = 0;
/*     */     } else {
/* 182 */       this.idx = idx;
/*     */     } 
/* 184 */     this.lastReaderIndex = in.readerIndex();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected ByteBuf extractObject(ChannelHandlerContext ctx, ByteBuf buffer, int index, int length) {
/* 192 */     return buffer.retainedSlice(index, length);
/*     */   }
/*     */   
/*     */   private void decodeByte(byte c, ByteBuf in, int idx) {
/* 196 */     if ((c == 123 || c == 91) && !this.insideString) {
/* 197 */       this.openBraces++;
/* 198 */     } else if ((c == 125 || c == 93) && !this.insideString) {
/* 199 */       this.openBraces--;
/* 200 */     } else if (c == 34) {
/*     */ 
/*     */       
/* 203 */       if (!this.insideString) {
/* 204 */         this.insideString = true;
/*     */       } else {
/* 206 */         int backslashCount = 0;
/* 207 */         idx--;
/* 208 */         while (idx >= 0 && 
/* 209 */           in.getByte(idx) == 92) {
/* 210 */           backslashCount++;
/* 211 */           idx--;
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 217 */         if (backslashCount % 2 == 0)
/*     */         {
/* 219 */           this.insideString = false;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void initDecoding(byte openingBrace) {
/* 226 */     this.openBraces = 1;
/* 227 */     if (openingBrace == 91 && this.streamArrayElements) {
/* 228 */       this.state = 2;
/*     */     } else {
/* 230 */       this.state = 1;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void reset() {
/* 235 */     this.insideString = false;
/* 236 */     this.state = 0;
/* 237 */     this.openBraces = 0;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\json\JsonObjectDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */