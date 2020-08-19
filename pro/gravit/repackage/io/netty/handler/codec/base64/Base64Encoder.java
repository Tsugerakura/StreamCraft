/*    */ package pro.gravit.repackage.io.netty.handler.codec.base64;
/*    */ 
/*    */ import java.util.List;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.MessageToMessageEncoder;
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
/*    */ @Sharable
/*    */ public class Base64Encoder
/*    */   extends MessageToMessageEncoder<ByteBuf>
/*    */ {
/*    */   private final boolean breakLines;
/*    */   private final Base64Dialect dialect;
/*    */   
/*    */   public Base64Encoder() {
/* 50 */     this(true);
/*    */   }
/*    */   
/*    */   public Base64Encoder(boolean breakLines) {
/* 54 */     this(breakLines, Base64Dialect.STANDARD);
/*    */   }
/*    */   
/*    */   public Base64Encoder(boolean breakLines, Base64Dialect dialect) {
/* 58 */     this.dialect = (Base64Dialect)ObjectUtil.checkNotNull(dialect, "dialect");
/* 59 */     this.breakLines = breakLines;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
/* 64 */     out.add(Base64.encode(msg, msg.readerIndex(), msg.readableBytes(), this.breakLines, this.dialect));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\base64\Base64Encoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */