/*    */ package pro.gravit.repackage.io.netty.handler.codec;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Delimiters
/*    */ {
/*    */   public static ByteBuf[] nulDelimiter() {
/* 31 */     return new ByteBuf[] {
/* 32 */         Unpooled.wrappedBuffer(new byte[] { 0 })
/*    */       };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static ByteBuf[] lineDelimiter() {
/* 40 */     return new ByteBuf[] {
/* 41 */         Unpooled.wrappedBuffer(new byte[] { 13, 10
/* 42 */           }), Unpooled.wrappedBuffer(new byte[] { 10 })
/*    */       };
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\Delimiters.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */