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
/*    */ class UnpooledUnsafeNoCleanerDirectByteBuf
/*    */   extends UnpooledUnsafeDirectByteBuf
/*    */ {
/*    */   UnpooledUnsafeNoCleanerDirectByteBuf(ByteBufAllocator alloc, int initialCapacity, int maxCapacity) {
/* 25 */     super(alloc, initialCapacity, maxCapacity);
/*    */   }
/*    */ 
/*    */   
/*    */   protected ByteBuffer allocateDirect(int initialCapacity) {
/* 30 */     return PlatformDependent.allocateDirectNoCleaner(initialCapacity);
/*    */   }
/*    */   
/*    */   ByteBuffer reallocateDirect(ByteBuffer oldBuffer, int initialCapacity) {
/* 34 */     return PlatformDependent.reallocateDirectNoCleaner(oldBuffer, initialCapacity);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void freeDirect(ByteBuffer buffer) {
/* 39 */     PlatformDependent.freeDirectNoCleaner(buffer);
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteBuf capacity(int newCapacity) {
/* 44 */     checkNewCapacity(newCapacity);
/*    */     
/* 46 */     int oldCapacity = capacity();
/* 47 */     if (newCapacity == oldCapacity) {
/* 48 */       return this;
/*    */     }
/*    */     
/* 51 */     trimIndicesToCapacity(newCapacity);
/* 52 */     setByteBuffer(reallocateDirect(this.buffer, newCapacity), false);
/* 53 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnpooledUnsafeNoCleanerDirectByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */