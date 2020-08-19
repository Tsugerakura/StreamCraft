/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class AdaptiveRecvByteBufAllocator
/*     */   extends DefaultMaxMessagesRecvByteBufAllocator
/*     */ {
/*     */   static final int DEFAULT_MINIMUM = 64;
/*     */   static final int DEFAULT_INITIAL = 1024;
/*     */   static final int DEFAULT_MAXIMUM = 65536;
/*     */   private static final int INDEX_INCREMENT = 4;
/*     */   private static final int INDEX_DECREMENT = 1;
/*     */   private static final int[] SIZE_TABLE;
/*     */   
/*     */   static {
/*  47 */     List<Integer> sizeTable = new ArrayList<Integer>(); int i;
/*  48 */     for (i = 16; i < 512; i += 16) {
/*  49 */       sizeTable.add(Integer.valueOf(i));
/*     */     }
/*     */     
/*  52 */     for (i = 512; i > 0; i <<= 1) {
/*  53 */       sizeTable.add(Integer.valueOf(i));
/*     */     }
/*     */     
/*  56 */     SIZE_TABLE = new int[sizeTable.size()];
/*  57 */     for (i = 0; i < SIZE_TABLE.length; i++) {
/*  58 */       SIZE_TABLE[i] = ((Integer)sizeTable.get(i)).intValue();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  66 */   public static final AdaptiveRecvByteBufAllocator DEFAULT = new AdaptiveRecvByteBufAllocator(); private final int minIndex; private final int maxIndex; private final int initial;
/*     */   
/*     */   private static int getSizeTableIndex(int size) {
/*  69 */     int mid, a, low = 0, high = SIZE_TABLE.length - 1; while (true) {
/*  70 */       if (high < low) {
/*  71 */         return low;
/*     */       }
/*  73 */       if (high == low) {
/*  74 */         return high;
/*     */       }
/*     */       
/*  77 */       mid = low + high >>> 1;
/*  78 */       a = SIZE_TABLE[mid];
/*  79 */       int b = SIZE_TABLE[mid + 1];
/*  80 */       if (size > b) {
/*  81 */         low = mid + 1; continue;
/*  82 */       }  if (size < a)
/*  83 */       { high = mid - 1; continue; }  break;
/*  84 */     }  if (size == a) {
/*  85 */       return mid;
/*     */     }
/*  87 */     return mid + 1;
/*     */   }
/*     */   
/*     */   private final class HandleImpl
/*     */     extends DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle
/*     */   {
/*     */     private final int minIndex;
/*     */     private final int maxIndex;
/*     */     private int index;
/*     */     private int nextReceiveBufferSize;
/*     */     private boolean decreaseNow;
/*     */     
/*     */     HandleImpl(int minIndex, int maxIndex, int initial) {
/* 100 */       this.minIndex = minIndex;
/* 101 */       this.maxIndex = maxIndex;
/*     */       
/* 103 */       this.index = AdaptiveRecvByteBufAllocator.getSizeTableIndex(initial);
/* 104 */       this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.SIZE_TABLE[this.index];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void lastBytesRead(int bytes) {
/* 113 */       if (bytes == attemptedBytesRead()) {
/* 114 */         record(bytes);
/*     */       }
/* 116 */       super.lastBytesRead(bytes);
/*     */     }
/*     */ 
/*     */     
/*     */     public int guess() {
/* 121 */       return this.nextReceiveBufferSize;
/*     */     }
/*     */     
/*     */     private void record(int actualReadBytes) {
/* 125 */       if (actualReadBytes <= AdaptiveRecvByteBufAllocator.SIZE_TABLE[Math.max(0, this.index - 1)]) {
/* 126 */         if (this.decreaseNow) {
/* 127 */           this.index = Math.max(this.index - 1, this.minIndex);
/* 128 */           this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.SIZE_TABLE[this.index];
/* 129 */           this.decreaseNow = false;
/*     */         } else {
/* 131 */           this.decreaseNow = true;
/*     */         } 
/* 133 */       } else if (actualReadBytes >= this.nextReceiveBufferSize) {
/* 134 */         this.index = Math.min(this.index + 4, this.maxIndex);
/* 135 */         this.nextReceiveBufferSize = AdaptiveRecvByteBufAllocator.SIZE_TABLE[this.index];
/* 136 */         this.decreaseNow = false;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void readComplete() {
/* 142 */       record(totalBytesRead());
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
/*     */   public AdaptiveRecvByteBufAllocator() {
/* 156 */     this(64, 1024, 65536);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AdaptiveRecvByteBufAllocator(int minimum, int initial, int maximum) {
/* 167 */     ObjectUtil.checkPositive(minimum, "minimum");
/* 168 */     if (initial < minimum) {
/* 169 */       throw new IllegalArgumentException("initial: " + initial);
/*     */     }
/* 171 */     if (maximum < initial) {
/* 172 */       throw new IllegalArgumentException("maximum: " + maximum);
/*     */     }
/*     */     
/* 175 */     int minIndex = getSizeTableIndex(minimum);
/* 176 */     if (SIZE_TABLE[minIndex] < minimum) {
/* 177 */       this.minIndex = minIndex + 1;
/*     */     } else {
/* 179 */       this.minIndex = minIndex;
/*     */     } 
/*     */     
/* 182 */     int maxIndex = getSizeTableIndex(maximum);
/* 183 */     if (SIZE_TABLE[maxIndex] > maximum) {
/* 184 */       this.maxIndex = maxIndex - 1;
/*     */     } else {
/* 186 */       this.maxIndex = maxIndex;
/*     */     } 
/*     */     
/* 189 */     this.initial = initial;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RecvByteBufAllocator.Handle newHandle() {
/* 195 */     return new HandleImpl(this.minIndex, this.maxIndex, this.initial);
/*     */   }
/*     */ 
/*     */   
/*     */   public AdaptiveRecvByteBufAllocator respectMaybeMoreData(boolean respectMaybeMoreData) {
/* 200 */     super.respectMaybeMoreData(respectMaybeMoreData);
/* 201 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\AdaptiveRecvByteBufAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */