/*    */ package pro.gravit.repackage.io.netty.handler.codec.string;
/*    */ 
/*    */ import java.nio.charset.Charset;
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
/*    */ public class StringDecoder
/*    */   extends MessageToMessageDecoder<ByteBuf>
/*    */ {
/*    */   private final Charset charset;
/*    */   
/*    */   public StringDecoder() {
/* 65 */     this(Charset.defaultCharset());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StringDecoder(Charset charset) {
/* 72 */     this.charset = (Charset)ObjectUtil.checkNotNull(charset, "charset");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
/* 77 */     out.add(msg.toString(this.charset));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\string\StringDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */