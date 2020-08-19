/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.util.AbstractMap;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.util.UncheckedBooleanSupplier;
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
/*     */ public class DefaultMaxBytesRecvByteBufAllocator
/*     */   implements MaxBytesRecvByteBufAllocator
/*     */ {
/*     */   private volatile int maxBytesPerRead;
/*     */   private volatile int maxBytesPerIndividualRead;
/*     */   
/*     */   private final class HandleImpl
/*     */     implements RecvByteBufAllocator.ExtendedHandle
/*     */   {
/*     */     private int individualReadMax;
/*     */     private int bytesToRead;
/*     */     
/*  40 */     private final UncheckedBooleanSupplier defaultMaybeMoreSupplier = new UncheckedBooleanSupplier()
/*     */       {
/*     */         public boolean get() {
/*  43 */           return (DefaultMaxBytesRecvByteBufAllocator.HandleImpl.this.attemptBytesRead == DefaultMaxBytesRecvByteBufAllocator.HandleImpl.this.lastBytesRead);
/*     */         }
/*     */       };
/*     */     private int lastBytesRead; private int attemptBytesRead;
/*     */     
/*     */     public ByteBuf allocate(ByteBufAllocator alloc) {
/*  49 */       return alloc.ioBuffer(guess());
/*     */     }
/*     */ 
/*     */     
/*     */     public int guess() {
/*  54 */       return Math.min(this.individualReadMax, this.bytesToRead);
/*     */     }
/*     */ 
/*     */     
/*     */     public void reset(ChannelConfig config) {
/*  59 */       this.bytesToRead = DefaultMaxBytesRecvByteBufAllocator.this.maxBytesPerRead();
/*  60 */       this.individualReadMax = DefaultMaxBytesRecvByteBufAllocator.this.maxBytesPerIndividualRead();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void incMessagesRead(int amt) {}
/*     */ 
/*     */     
/*     */     public void lastBytesRead(int bytes) {
/*  69 */       this.lastBytesRead = bytes;
/*     */ 
/*     */       
/*  72 */       this.bytesToRead -= bytes;
/*     */     }
/*     */ 
/*     */     
/*     */     public int lastBytesRead() {
/*  77 */       return this.lastBytesRead;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean continueReading() {
/*  82 */       return continueReading(this.defaultMaybeMoreSupplier);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean continueReading(UncheckedBooleanSupplier maybeMoreDataSupplier) {
/*  88 */       return (this.bytesToRead > 0 && maybeMoreDataSupplier.get());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void readComplete() {}
/*     */ 
/*     */     
/*     */     public void attemptedBytesRead(int bytes) {
/*  97 */       this.attemptBytesRead = bytes;
/*     */     }
/*     */     private HandleImpl() {}
/*     */     
/*     */     public int attemptedBytesRead() {
/* 102 */       return this.attemptBytesRead;
/*     */     }
/*     */   }
/*     */   
/*     */   public DefaultMaxBytesRecvByteBufAllocator() {
/* 107 */     this(65536, 65536);
/*     */   }
/*     */   
/*     */   public DefaultMaxBytesRecvByteBufAllocator(int maxBytesPerRead, int maxBytesPerIndividualRead) {
/* 111 */     checkMaxBytesPerReadPair(maxBytesPerRead, maxBytesPerIndividualRead);
/* 112 */     this.maxBytesPerRead = maxBytesPerRead;
/* 113 */     this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RecvByteBufAllocator.Handle newHandle() {
/* 119 */     return new HandleImpl();
/*     */   }
/*     */ 
/*     */   
/*     */   public int maxBytesPerRead() {
/* 124 */     return this.maxBytesPerRead;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultMaxBytesRecvByteBufAllocator maxBytesPerRead(int maxBytesPerRead) {
/* 129 */     ObjectUtil.checkPositive(maxBytesPerRead, "maxBytesPerRead");
/*     */ 
/*     */     
/* 132 */     synchronized (this) {
/* 133 */       int maxBytesPerIndividualRead = maxBytesPerIndividualRead();
/* 134 */       if (maxBytesPerRead < maxBytesPerIndividualRead) {
/* 135 */         throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + maxBytesPerIndividualRead + "): " + maxBytesPerRead);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 140 */       this.maxBytesPerRead = maxBytesPerRead;
/*     */     } 
/* 142 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int maxBytesPerIndividualRead() {
/* 147 */     return this.maxBytesPerIndividualRead;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultMaxBytesRecvByteBufAllocator maxBytesPerIndividualRead(int maxBytesPerIndividualRead) {
/* 152 */     ObjectUtil.checkPositive(maxBytesPerIndividualRead, "maxBytesPerIndividualRead");
/*     */ 
/*     */     
/* 155 */     synchronized (this) {
/* 156 */       int maxBytesPerRead = maxBytesPerRead();
/* 157 */       if (maxBytesPerIndividualRead > maxBytesPerRead) {
/* 158 */         throw new IllegalArgumentException("maxBytesPerIndividualRead cannot be greater than maxBytesPerRead (" + maxBytesPerRead + "): " + maxBytesPerIndividualRead);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 163 */       this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
/*     */     } 
/* 165 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized Map.Entry<Integer, Integer> maxBytesPerReadPair() {
/* 170 */     return new AbstractMap.SimpleEntry<Integer, Integer>(Integer.valueOf(this.maxBytesPerRead), Integer.valueOf(this.maxBytesPerIndividualRead));
/*     */   }
/*     */   
/*     */   private static void checkMaxBytesPerReadPair(int maxBytesPerRead, int maxBytesPerIndividualRead) {
/* 174 */     ObjectUtil.checkPositive(maxBytesPerRead, "maxBytesPerRead");
/* 175 */     ObjectUtil.checkPositive(maxBytesPerIndividualRead, "maxBytesPerIndividualRead");
/* 176 */     if (maxBytesPerRead < maxBytesPerIndividualRead) {
/* 177 */       throw new IllegalArgumentException("maxBytesPerRead cannot be less than maxBytesPerIndividualRead (" + maxBytesPerIndividualRead + "): " + maxBytesPerRead);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultMaxBytesRecvByteBufAllocator maxBytesPerReadPair(int maxBytesPerRead, int maxBytesPerIndividualRead) {
/* 186 */     checkMaxBytesPerReadPair(maxBytesPerRead, maxBytesPerIndividualRead);
/*     */ 
/*     */     
/* 189 */     synchronized (this) {
/* 190 */       this.maxBytesPerRead = maxBytesPerRead;
/* 191 */       this.maxBytesPerIndividualRead = maxBytesPerIndividualRead;
/*     */     } 
/* 193 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultMaxBytesRecvByteBufAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */