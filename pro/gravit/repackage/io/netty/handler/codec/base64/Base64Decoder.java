/*    */ package pro.gravit.repackage.io.netty.handler.codec.base64;
/*    */ 
/*    */ import java.util.List;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.MessageToMessageDecoder;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Sharable
/*    */ public class Base64Decoder
/*    */   extends MessageToMessageDecoder<ByteBuf>
/*    */ {
/*    */   private final Base64Dialect dialect;
/*    */   
/*    */   public Base64Decoder() {
/* 53 */     this(Base64Dialect.STANDARD);
/*    */   }
/*    */   
/*    */   public Base64Decoder(Base64Dialect dialect) {
/* 57 */     this.dialect = (Base64Dialect)ObjectUtil.checkNotNull(dialect, "dialect");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
/* 62 */     out.add(Base64.decode(msg, msg.readerIndex(), msg.readableBytes(), this.dialect));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\base64\Base64Decoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */