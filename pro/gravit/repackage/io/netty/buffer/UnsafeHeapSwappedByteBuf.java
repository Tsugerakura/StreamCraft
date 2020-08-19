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
/*    */ final class UnsafeHeapSwappedByteBuf
/*    */   extends AbstractUnsafeSwappedByteBuf
/*    */ {
/*    */   UnsafeHeapSwappedByteBuf(AbstractByteBuf buf) {
/* 27 */     super(buf);
/*    */   }
/*    */   
/*    */   private static int idx(ByteBuf wrapped, int index) {
/* 31 */     return wrapped.arrayOffset() + index;
/*    */   }
/*    */ 
/*    */   
/*    */   protected long _getLong(AbstractByteBuf wrapped, int index) {
/* 36 */     return PlatformDependent.getLong(wrapped.array(), idx(wrapped, index));
/*    */   }
/*    */ 
/*    */   
/*    */   protected int _getInt(AbstractByteBuf wrapped, int index) {
/* 41 */     return PlatformDependent.getInt(wrapped.array(), idx(wrapped, index));
/*    */   }
/*    */ 
/*    */   
/*    */   protected short _getShort(AbstractByteBuf wrapped, int index) {
/* 46 */     return PlatformDependent.getShort(wrapped.array(), idx(wrapped, index));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void _setShort(AbstractByteBuf wrapped, int index, short value) {
/* 51 */     PlatformDependent.putShort(wrapped.array(), idx(wrapped, index), value);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void _setInt(AbstractByteBuf wrapped, int index, int value) {
/* 56 */     PlatformDependent.putInt(wrapped.array(), idx(wrapped, index), value);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void _setLong(AbstractByteBuf wrapped, int index, long value) {
/* 61 */     PlatformDependent.putLong(wrapped.array(), idx(wrapped, index), value);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\UnsafeHeapSwappedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */