/*    */ package pro.gravit.repackage.io.netty.handler.codec;
/*    */ 
/*    */ import java.util.List;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
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
/*    */ public class FixedLengthFrameDecoder
/*    */   extends ByteToMessageDecoder
/*    */ {
/*    */   private final int frameLength;
/*    */   
/*    */   public FixedLengthFrameDecoder(int frameLength) {
/* 51 */     ObjectUtil.checkPositive(frameLength, "frameLength");
/* 52 */     this.frameLength = frameLength;
/*    */   }
/*    */ 
/*    */   
/*    */   protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/* 57 */     Object decoded = decode(ctx, in);
/* 58 */     if (decoded != null) {
/* 59 */       out.add(decoded);
/*    */     }
/*    */   }
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
/*    */   protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
/* 73 */     if (in.readableBytes() < this.frameLength) {
/* 74 */       return null;
/*    */     }
/* 76 */     return in.readRetainedSlice(this.frameLength);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\FixedLengthFrameDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */