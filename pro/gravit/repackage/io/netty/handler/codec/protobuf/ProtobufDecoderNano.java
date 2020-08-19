/*    */ package pro.gravit.repackage.io.netty.handler.codec.protobuf;
/*    */ 
/*    */ import com.google.protobuf.nano.MessageNano;
/*    */ import java.util.List;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @Sharable
/*    */ public class ProtobufDecoderNano
/*    */   extends MessageToMessageDecoder<ByteBuf>
/*    */ {
/*    */   private final Class<? extends MessageNano> clazz;
/*    */   
/*    */   public ProtobufDecoderNano(Class<? extends MessageNano> clazz) {
/* 69 */     this.clazz = (Class<? extends MessageNano>)ObjectUtil.checkNotNull(clazz, "You must provide a Class");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
/*    */     byte[] array;
/* 77 */     int offset, length = msg.readableBytes();
/* 78 */     if (msg.hasArray()) {
/* 79 */       array = msg.array();
/* 80 */       offset = msg.arrayOffset() + msg.readerIndex();
/*    */     } else {
/* 82 */       array = ByteBufUtil.getBytes(msg, msg.readerIndex(), length, false);
/* 83 */       offset = 0;
/*    */     } 
/* 85 */     MessageNano prototype = this.clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
/* 86 */     out.add(MessageNano.mergeFrom(prototype, array, offset, length));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\protobuf\ProtobufDecoderNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */