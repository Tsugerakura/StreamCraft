/*    */ package pro.gravit.repackage.io.netty.buffer;
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
/*    */ public interface ByteBufAllocator
/*    */ {
/* 24 */   public static final ByteBufAllocator DEFAULT = ByteBufUtil.DEFAULT_ALLOCATOR;
/*    */   
/*    */   ByteBuf buffer();
/*    */   
/*    */   ByteBuf buffer(int paramInt);
/*    */   
/*    */   ByteBuf buffer(int paramInt1, int paramInt2);
/*    */   
/*    */   ByteBuf ioBuffer();
/*    */   
/*    */   ByteBuf ioBuffer(int paramInt);
/*    */   
/*    */   ByteBuf ioBuffer(int paramInt1, int paramInt2);
/*    */   
/*    */   ByteBuf heapBuffer();
/*    */   
/*    */   ByteBuf heapBuffer(int paramInt);
/*    */   
/*    */   ByteBuf heapBuffer(int paramInt1, int paramInt2);
/*    */   
/*    */   ByteBuf directBuffer();
/*    */   
/*    */   ByteBuf directBuffer(int paramInt);
/*    */   
/*    */   ByteBuf directBuffer(int paramInt1, int paramInt2);
/*    */   
/*    */   CompositeByteBuf compositeBuffer();
/*    */   
/*    */   CompositeByteBuf compositeBuffer(int paramInt);
/*    */   
/*    */   CompositeByteBuf compositeHeapBuffer();
/*    */   
/*    */   CompositeByteBuf compositeHeapBuffer(int paramInt);
/*    */   
/*    */   CompositeByteBuf compositeDirectBuffer();
/*    */   
/*    */   CompositeByteBuf compositeDirectBuffer(int paramInt);
/*    */   
/*    */   boolean isDirectBufferPooled();
/*    */   
/*    */   int calculateNewCapacity(int paramInt1, int paramInt2);
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\ByteBufAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */