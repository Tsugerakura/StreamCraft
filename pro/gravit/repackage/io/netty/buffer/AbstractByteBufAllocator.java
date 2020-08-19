/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.ResourceLeakDetector;
/*     */ import pro.gravit.repackage.io.netty.util.ResourceLeakTracker;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ public abstract class AbstractByteBufAllocator
/*     */   implements ByteBufAllocator
/*     */ {
/*     */   static final int DEFAULT_INITIAL_CAPACITY = 256;
/*     */   static final int DEFAULT_MAX_CAPACITY = 2147483647;
/*     */   static final int DEFAULT_MAX_COMPONENTS = 16;
/*     */   static final int CALCULATE_THRESHOLD = 4194304;
/*     */   private final boolean directByDefault;
/*     */   private final ByteBuf emptyBuf;
/*     */   
/*     */   static {
/*  36 */     ResourceLeakDetector.addExclusions(AbstractByteBufAllocator.class, new String[] { "toLeakAwareBuffer" });
/*     */   }
/*     */   
/*     */   protected static ByteBuf toLeakAwareBuffer(ByteBuf buf) {
/*     */     ResourceLeakTracker<ByteBuf> leak;
/*  41 */     switch (ResourceLeakDetector.getLevel()) {
/*     */       case SIMPLE:
/*  43 */         leak = AbstractByteBuf.leakDetector.track(buf);
/*  44 */         if (leak != null) {
/*  45 */           buf = new SimpleLeakAwareByteBuf(buf, leak);
/*     */         }
/*     */         break;
/*     */       case ADVANCED:
/*     */       case PARANOID:
/*  50 */         leak = AbstractByteBuf.leakDetector.track(buf);
/*  51 */         if (leak != null) {
/*  52 */           buf = new AdvancedLeakAwareByteBuf(buf, leak);
/*     */         }
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/*  58 */     return buf;
/*     */   }
/*     */   
/*     */   protected static CompositeByteBuf toLeakAwareBuffer(CompositeByteBuf buf) {
/*     */     ResourceLeakTracker<ByteBuf> leak;
/*  63 */     switch (ResourceLeakDetector.getLevel()) {
/*     */       case SIMPLE:
/*  65 */         leak = AbstractByteBuf.leakDetector.track(buf);
/*  66 */         if (leak != null) {
/*  67 */           buf = new SimpleLeakAwareCompositeByteBuf(buf, leak);
/*     */         }
/*     */         break;
/*     */       case ADVANCED:
/*     */       case PARANOID:
/*  72 */         leak = AbstractByteBuf.leakDetector.track(buf);
/*  73 */         if (leak != null) {
/*  74 */           buf = new AdvancedLeakAwareCompositeByteBuf(buf, leak);
/*     */         }
/*     */         break;
/*     */     } 
/*     */ 
/*     */     
/*  80 */     return buf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractByteBufAllocator() {
/*  90 */     this(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractByteBufAllocator(boolean preferDirect) {
/* 100 */     this.directByDefault = (preferDirect && PlatformDependent.hasUnsafe());
/* 101 */     this.emptyBuf = new EmptyByteBuf(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf buffer() {
/* 106 */     if (this.directByDefault) {
/* 107 */       return directBuffer();
/*     */     }
/* 109 */     return heapBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf buffer(int initialCapacity) {
/* 114 */     if (this.directByDefault) {
/* 115 */       return directBuffer(initialCapacity);
/*     */     }
/* 117 */     return heapBuffer(initialCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf buffer(int initialCapacity, int maxCapacity) {
/* 122 */     if (this.directByDefault) {
/* 123 */       return directBuffer(initialCapacity, maxCapacity);
/*     */     }
/* 125 */     return heapBuffer(initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf ioBuffer() {
/* 130 */     if (PlatformDependent.hasUnsafe() || isDirectBufferPooled()) {
/* 131 */       return directBuffer(256);
/*     */     }
/* 133 */     return heapBuffer(256);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf ioBuffer(int initialCapacity) {
/* 138 */     if (PlatformDependent.hasUnsafe() || isDirectBufferPooled()) {
/* 139 */       return directBuffer(initialCapacity);
/*     */     }
/* 141 */     return heapBuffer(initialCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf ioBuffer(int initialCapacity, int maxCapacity) {
/* 146 */     if (PlatformDependent.hasUnsafe() || isDirectBufferPooled()) {
/* 147 */       return directBuffer(initialCapacity, maxCapacity);
/*     */     }
/* 149 */     return heapBuffer(initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf heapBuffer() {
/* 154 */     return heapBuffer(256, 2147483647);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf heapBuffer(int initialCapacity) {
/* 159 */     return heapBuffer(initialCapacity, 2147483647);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf heapBuffer(int initialCapacity, int maxCapacity) {
/* 164 */     if (initialCapacity == 0 && maxCapacity == 0) {
/* 165 */       return this.emptyBuf;
/*     */     }
/* 167 */     validate(initialCapacity, maxCapacity);
/* 168 */     return newHeapBuffer(initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf directBuffer() {
/* 173 */     return directBuffer(256, 2147483647);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf directBuffer(int initialCapacity) {
/* 178 */     return directBuffer(initialCapacity, 2147483647);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf directBuffer(int initialCapacity, int maxCapacity) {
/* 183 */     if (initialCapacity == 0 && maxCapacity == 0) {
/* 184 */       return this.emptyBuf;
/*     */     }
/* 186 */     validate(initialCapacity, maxCapacity);
/* 187 */     return newDirectBuffer(initialCapacity, maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeBuffer() {
/* 192 */     if (this.directByDefault) {
/* 193 */       return compositeDirectBuffer();
/*     */     }
/* 195 */     return compositeHeapBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeBuffer(int maxNumComponents) {
/* 200 */     if (this.directByDefault) {
/* 201 */       return compositeDirectBuffer(maxNumComponents);
/*     */     }
/* 203 */     return compositeHeapBuffer(maxNumComponents);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeHeapBuffer() {
/* 208 */     return compositeHeapBuffer(16);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeHeapBuffer(int maxNumComponents) {
/* 213 */     return toLeakAwareBuffer(new CompositeByteBuf(this, false, maxNumComponents));
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeDirectBuffer() {
/* 218 */     return compositeDirectBuffer(16);
/*     */   }
/*     */ 
/*     */   
/*     */   public CompositeByteBuf compositeDirectBuffer(int maxNumComponents) {
/* 223 */     return toLeakAwareBuffer(new CompositeByteBuf(this, true, maxNumComponents));
/*     */   }
/*     */   
/*     */   private static void validate(int initialCapacity, int maxCapacity) {
/* 227 */     ObjectUtil.checkPositiveOrZero(initialCapacity, "initialCapacity");
/* 228 */     if (initialCapacity > maxCapacity) {
/* 229 */       throw new IllegalArgumentException(String.format("initialCapacity: %d (expected: not greater than maxCapacity(%d)", new Object[] {
/*     */               
/* 231 */               Integer.valueOf(initialCapacity), Integer.valueOf(maxCapacity)
/*     */             }));
/*     */     }
/*     */   }
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
/*     */   public String toString() {
/* 247 */     return StringUtil.simpleClassName(this) + "(directByDefault: " + this.directByDefault + ')';
/*     */   }
/*     */ 
/*     */   
/*     */   public int calculateNewCapacity(int minNewCapacity, int maxCapacity) {
/* 252 */     ObjectUtil.checkPositiveOrZero(minNewCapacity, "minNewCapacity");
/* 253 */     if (minNewCapacity > maxCapacity)
/* 254 */       throw new IllegalArgumentException(String.format("minNewCapacity: %d (expected: not greater than maxCapacity(%d)", new Object[] {
/*     */               
/* 256 */               Integer.valueOf(minNewCapacity), Integer.valueOf(maxCapacity)
/*     */             })); 
/* 258 */     int threshold = 4194304;
/*     */     
/* 260 */     if (minNewCapacity == 4194304) {
/* 261 */       return 4194304;
/*     */     }
/*     */ 
/*     */     
/* 265 */     if (minNewCapacity > 4194304) {
/* 266 */       int i = minNewCapacity / 4194304 * 4194304;
/* 267 */       if (i > maxCapacity - 4194304) {
/* 268 */         i = maxCapacity;
/*     */       } else {
/* 270 */         i += 4194304;
/*     */       } 
/* 272 */       return i;
/*     */     } 
/*     */ 
/*     */     
/* 276 */     int newCapacity = 64;
/* 277 */     while (newCapacity < minNewCapacity) {
/* 278 */       newCapacity <<= 1;
/*     */     }
/*     */     
/* 281 */     return Math.min(newCapacity, maxCapacity);
/*     */   }
/*     */   
/*     */   protected abstract ByteBuf newHeapBuffer(int paramInt1, int paramInt2);
/*     */   
/*     */   protected abstract ByteBuf newDirectBuffer(int paramInt1, int paramInt2);
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\AbstractByteBufAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */