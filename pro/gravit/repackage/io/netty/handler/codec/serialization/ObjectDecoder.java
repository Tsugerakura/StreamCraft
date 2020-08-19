/*    */ package pro.gravit.repackage.io.netty.handler.codec.serialization;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.io.ObjectInputStream;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufInputStream;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.LengthFieldBasedFrameDecoder;
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
/*    */ public class ObjectDecoder
/*    */   extends LengthFieldBasedFrameDecoder
/*    */ {
/*    */   private final ClassResolver classResolver;
/*    */   
/*    */   public ObjectDecoder(ClassResolver classResolver) {
/* 49 */     this(1048576, classResolver);
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
/*    */   
/*    */   public ObjectDecoder(int maxObjectSize, ClassResolver classResolver) {
/* 63 */     super(maxObjectSize, 0, 4, 0, 4);
/* 64 */     this.classResolver = classResolver;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
/* 69 */     ByteBuf frame = (ByteBuf)super.decode(ctx, in);
/* 70 */     if (frame == null) {
/* 71 */       return null;
/*    */     }
/*    */     
/* 74 */     ObjectInputStream ois = new CompactObjectInputStream((InputStream)new ByteBufInputStream(frame, true), this.classResolver);
/*    */     try {
/* 76 */       return ois.readObject();
/*    */     } finally {
/* 78 */       ois.close();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\serialization\ObjectDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */