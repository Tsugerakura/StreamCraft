/*    */ package pro.gravit.repackage.io.netty.buffer;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class WrappedUnpooledUnsafeDirectByteBuf
/*    */   extends UnpooledUnsafeDirectByteBuf
/*    */ {
/*    */   WrappedUnpooledUnsafeDirectByteBuf(ByteBufAllocator alloc, long memoryAddress, int size, boolean doFree) {
/* 25 */     super(alloc, PlatformDependent.directBuffer(memoryAddress, size), size, doFree);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void freeDirect(ByteBuffer buffer) {
/* 30 */     PlatformDependent.freeMemory(this.memoryAddress);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\WrappedUnpooledUnsafeDirectByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */