/*    */ package pro.gravit.repackage.io.netty.handler.codec.serialization;
/*    */ 
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.io.Serializable;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufOutputStream;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CompatibleObjectEncoder
/*    */   extends MessageToByteEncoder<Serializable>
/*    */ {
/*    */   private final int resetInterval;
/*    */   private int writtenObjects;
/*    */   
/*    */   public CompatibleObjectEncoder() {
/* 43 */     this(16);
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
/*    */   public CompatibleObjectEncoder(int resetInterval) {
/* 56 */     if (resetInterval < 0) {
/* 57 */       throw new IllegalArgumentException("resetInterval: " + resetInterval);
/*    */     }
/*    */     
/* 60 */     this.resetInterval = resetInterval;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected ObjectOutputStream newObjectOutputStream(OutputStream out) throws Exception {
/* 69 */     return new ObjectOutputStream(out);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
/* 74 */     ObjectOutputStream oos = newObjectOutputStream((OutputStream)new ByteBufOutputStream(out));
/*    */     try {
/* 76 */       if (this.resetInterval != 0) {
/*    */         
/* 78 */         this.writtenObjects++;
/* 79 */         if (this.writtenObjects % this.resetInterval == 0) {
/* 80 */           oos.reset();
/*    */         }
/*    */       } 
/*    */       
/* 84 */       oos.writeObject(msg);
/* 85 */       oos.flush();
/*    */     } finally {
/* 87 */       oos.close();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\serialization\CompatibleObjectEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */