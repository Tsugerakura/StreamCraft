/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.stream.ChunkedInput;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpChunkedInput
/*     */   implements ChunkedInput<HttpContent>
/*     */ {
/*     */   private final ChunkedInput<ByteBuf> input;
/*     */   private final LastHttpContent lastHttpContent;
/*     */   private boolean sentLastChunk;
/*     */   
/*     */   public HttpChunkedInput(ChunkedInput<ByteBuf> input) {
/*  54 */     this.input = input;
/*  55 */     this.lastHttpContent = LastHttpContent.EMPTY_LAST_CONTENT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpChunkedInput(ChunkedInput<ByteBuf> input, LastHttpContent lastHttpContent) {
/*  66 */     this.input = input;
/*  67 */     this.lastHttpContent = lastHttpContent;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEndOfInput() throws Exception {
/*  72 */     if (this.input.isEndOfInput())
/*     */     {
/*  74 */       return this.sentLastChunk;
/*     */     }
/*  76 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws Exception {
/*  82 */     this.input.close();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public HttpContent readChunk(ChannelHandlerContext ctx) throws Exception {
/*  88 */     return readChunk(ctx.alloc());
/*     */   }
/*     */ 
/*     */   
/*     */   public HttpContent readChunk(ByteBufAllocator allocator) throws Exception {
/*  93 */     if (this.input.isEndOfInput()) {
/*  94 */       if (this.sentLastChunk) {
/*  95 */         return null;
/*     */       }
/*     */       
/*  98 */       this.sentLastChunk = true;
/*  99 */       return this.lastHttpContent;
/*     */     } 
/*     */     
/* 102 */     ByteBuf buf = (ByteBuf)this.input.readChunk(allocator);
/* 103 */     if (buf == null) {
/* 104 */       return null;
/*     */     }
/* 106 */     return new DefaultHttpContent(buf);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long length() {
/* 112 */     return this.input.length();
/*     */   }
/*     */ 
/*     */   
/*     */   public long progress() {
/* 117 */     return this.input.progress();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpChunkedInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */