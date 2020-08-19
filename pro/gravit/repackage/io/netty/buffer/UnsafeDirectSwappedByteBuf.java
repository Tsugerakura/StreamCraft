/*    */ package pro.gravit.repackage.io.netty.buffer;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ final class UnsafeDirectSwappedByteBuf
/*    */   extends AbstractUnsafeSwappedByteBuf
/*    */ {
/*    */   UnsafeDirectSwappedByteBuf(AbstractByteBuf buf) {
/* 27 */     super(buf);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static long addr(AbstractByteBuf wrapped, int index) {
/* 35 */     return wrapped.memoryAddress() + index;
/*    */   }
/*    */ 
/*    */   
/*    */   protected long _getLong(AbstractByteBuf wrapped, int index) {
/* 40 */     return PlatformDependent.getLong(addr(wrapped, index));
/*    */   }
/*    */ 
/*    */   
/*    */   protected int _getInt(AbstractByteBuf wrapped, int index) {
/* 45 */     return PlatformDependent.getInt(addr(wrapped, index));
/*    */   }
/*    */ 
/*    */   
/*    */   protected short _getShort(AbstractByteBuf wrapped, int index) {
/* 50 */     return PlatformDependent.getShort(addr(wrapped, index));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void _setShort(AbstractByteBuf wrapped, int index, short value) {
/* 55 */     PlatformDependent.putShort(addr(wrapped, index), value);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void _setInt(AbstractByteBuf wrapped, int index, int value) {
/* 60 */     PlatformDependent.putInt(addr(wrapped, index), value);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void _setLong(AbstractByteBuf wrapped, int index, long value) {
/* 65 */     PlatformDependent.putLong(addr(wrapped, index), value);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnsafeDirectSwappedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */