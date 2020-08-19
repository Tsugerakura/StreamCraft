/*    */ package pro.gravit.repackage.io.netty.buffer;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.ByteOrder;
/*    */ import java.nio.channels.FileChannel;
/*    */ import java.nio.channels.GatheringByteChannel;
/*    */ import java.nio.channels.ScatteringByteChannel;
/*    */ import java.nio.charset.Charset;
/*    */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class SlicedByteBuf
/*    */   extends AbstractUnpooledSlicedByteBuf
/*    */ {
/*    */   private int length;
/*    */   
/*    */   public SlicedByteBuf(ByteBuf buffer, int index, int length) {
/* 32 */     super(buffer, index, length);
/*    */   }
/*    */ 
/*    */   
/*    */   final void initLength(int length) {
/* 37 */     this.length = length;
/*    */   }
/*    */ 
/*    */   
/*    */   final int length() {
/* 42 */     return this.length;
/*    */   }
/*    */ 
/*    */   
/*    */   public int capacity() {
/* 47 */     return this.length;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\SlicedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */