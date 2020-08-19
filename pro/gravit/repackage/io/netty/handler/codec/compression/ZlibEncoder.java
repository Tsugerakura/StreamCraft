/*    */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.MessageToByteEncoder;
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
/*    */ public abstract class ZlibEncoder
/*    */   extends MessageToByteEncoder<ByteBuf>
/*    */ {
/*    */   protected ZlibEncoder() {
/* 29 */     super(false);
/*    */   }
/*    */   
/*    */   public abstract boolean isClosed();
/*    */   
/*    */   public abstract ChannelFuture close();
/*    */   
/*    */   public abstract ChannelFuture close(ChannelPromise paramChannelPromise);
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\ZlibEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */