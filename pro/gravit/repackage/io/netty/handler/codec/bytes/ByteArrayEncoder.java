/*    */ package pro.gravit.repackage.io.netty.handler.codec.bytes;
/*    */ 
/*    */ import java.util.List;
/*    */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.MessageToMessageEncoder;
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
/*    */ public class ByteArrayEncoder
/*    */   extends MessageToMessageEncoder<byte[]>
/*    */ {
/*    */   protected void encode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
/* 57 */     out.add(Unpooled.wrappedBuffer(msg));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\bytes\ByteArrayEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */