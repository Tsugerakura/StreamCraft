/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.stream.ChunkedInput;
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
/*     */ public final class WebSocketChunkedInput
/*     */   implements ChunkedInput<WebSocketFrame>
/*     */ {
/*     */   private final ChunkedInput<ByteBuf> input;
/*     */   private final int rsv;
/*     */   
/*     */   public WebSocketChunkedInput(ChunkedInput<ByteBuf> input) {
/*  40 */     this(input, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketChunkedInput(ChunkedInput<ByteBuf> input, int rsv) {
/*  51 */     this.input = (ChunkedInput<ByteBuf>)ObjectUtil.checkNotNull(input, "input");
/*  52 */     this.rsv = rsv;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEndOfInput() throws Exception {
/*  61 */     return this.input.isEndOfInput();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws Exception {
/*  69 */     this.input.close();
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
/*     */   @Deprecated
/*     */   public WebSocketFrame readChunk(ChannelHandlerContext ctx) throws Exception {
/*  85 */     return readChunk(ctx.alloc());
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
/*     */   public WebSocketFrame readChunk(ByteBufAllocator allocator) throws Exception {
/*  98 */     ByteBuf buf = (ByteBuf)this.input.readChunk(allocator);
/*  99 */     if (buf == null) {
/* 100 */       return null;
/*     */     }
/* 102 */     return new ContinuationWebSocketFrame(this.input.isEndOfInput(), this.rsv, buf);
/*     */   }
/*     */ 
/*     */   
/*     */   public long length() {
/* 107 */     return this.input.length();
/*     */   }
/*     */ 
/*     */   
/*     */   public long progress() {
/* 112 */     return this.input.progress();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketChunkedInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */