/*    */ package pro.gravit.repackage.io.netty.handler.codec.protobuf;
/*    */ 
/*    */ import com.google.protobuf.nano.CodedOutputByteBufferNano;
/*    */ import com.google.protobuf.nano.MessageNano;
/*    */ import java.util.List;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Sharable
/*    */ public class ProtobufEncoderNano
/*    */   extends MessageToMessageEncoder<MessageNano>
/*    */ {
/*    */   protected void encode(ChannelHandlerContext ctx, MessageNano msg, List<Object> out) throws Exception {
/* 64 */     int size = msg.getSerializedSize();
/* 65 */     ByteBuf buffer = ctx.alloc().heapBuffer(size, size);
/* 66 */     byte[] array = buffer.array();
/* 67 */     CodedOutputByteBufferNano cobbn = CodedOutputByteBufferNano.newInstance(array, buffer
/* 68 */         .arrayOffset(), buffer.capacity());
/* 69 */     msg.writeTo(cobbn);
/* 70 */     buffer.writerIndex(size);
/* 71 */     out.add(buffer);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\protobuf\ProtobufEncoderNano.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */