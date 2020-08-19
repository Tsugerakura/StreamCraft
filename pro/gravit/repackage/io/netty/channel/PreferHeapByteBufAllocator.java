/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.buffer.CompositeByteBuf;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class PreferHeapByteBufAllocator
/*     */   implements ByteBufAllocator
/*     */ {
/*     */   private final ByteBufAllocator allocator;
/*     */   
/*     */   public PreferHeapByteBufAllocator(ByteBufAllocator allocator) {
/*  33 */     this.allocator = (ByteBufAllocator)ObjectUtil.checkNotNull(allocator, "allocator");
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf buffer() {
/*  38 */     return this.allocator.heapBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf buffer(int initialCapacity) {
/*  43 */     return this.allocator.heapBuffer(initialCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf buffer(int initialCapacity, int maxCapacity) {
/*  48 */     return this.allocator.heapBuffer(initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf ioBuffer() {
/*  53 */     return this.allocator.heapBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf ioBuffer(int initialCapacity) {
/*  58 */     return this.allocator.heapBuffer(initialCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf ioBuffer(int initialCapacity, int maxCapacity) {
/*  63 */     return this.allocator.heapBuffer(initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf heapBuffer() {
/*  68 */     return this.allocator.heapBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf heapBuffer(int initialCapacity) {
/*  73 */     return this.allocator.heapBuffer(initialCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf heapBuffer(int initialCapacity, int maxCapacity) {
/*  78 */     return this.allocator.heapBuffer(initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf directBuffer() {
/*  83 */     return this.allocator.directBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf directBuffer(int initialCapacity) {
/*  88 */     return this.allocator.directBuffer(initialCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf directBuffer(int initialCapacity, int maxCapacity) {
/*  93 */     return this.allocator.directBuffer(initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeBuffer() {
/*  98 */     return this.allocator.compositeHeapBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeBuffer(int maxNumComponents) {
/* 103 */     return this.allocator.compositeHeapBuffer(maxNumComponents);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeHeapBuffer() {
/* 108 */     return this.allocator.compositeHeapBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
/* 113 */     return this.allocator.compositeHeapBuffer(maxNumComponents);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeDirectBuffer() {
/* 118 */     return this.allocator.compositeDirectBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeDirectBuffer(int maxNumComponents) {
/* 123 */     return this.allocator.compositeDirectBuffer(maxNumComponents);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectBufferPooled() {
/* 128 */     return this.allocator.isDirectBufferPooled();
/*     */   }
/*     */ 
/*     */   
/*     */   public int calculateNewCapacity(int minNewCapacity, int maxCapacity) {
/* 133 */     return this.allocator.calculateNewCapacity(minNewCapacity, maxCapacity);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\PreferHeapByteBufAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */