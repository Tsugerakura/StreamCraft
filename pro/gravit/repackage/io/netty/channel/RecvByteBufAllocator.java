/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
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
/*     */ 
/*     */ public interface RecvByteBufAllocator
/*     */ {
/*     */   Handle newHandle();
/*     */   
/*     */   public static class DelegatingHandle
/*     */     implements Handle
/*     */   {
/*     */     private final RecvByteBufAllocator.Handle delegate;
/*     */     
/*     */     public DelegatingHandle(RecvByteBufAllocator.Handle delegate) {
/* 127 */       this.delegate = (RecvByteBufAllocator.Handle)ObjectUtil.checkNotNull(delegate, "delegate");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected final RecvByteBufAllocator.Handle delegate() {
/* 135 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuf allocate(ByteBufAllocator alloc) {
/* 140 */       return this.delegate.allocate(alloc);
/*     */     }
/*     */ 
/*     */     
/*     */     public int guess() {
/* 145 */       return this.delegate.guess();
/*     */     }
/*     */ 
/*     */     
/*     */     public void reset(ChannelConfig config) {
/* 150 */       this.delegate.reset(config);
/*     */     }
/*     */ 
/*     */     
/*     */     public void incMessagesRead(int numMessages) {
/* 155 */       this.delegate.incMessagesRead(numMessages);
/*     */     }
/*     */ 
/*     */     
/*     */     public void lastBytesRead(int bytes) {
/* 160 */       this.delegate.lastBytesRead(bytes);
/*     */     }
/*     */ 
/*     */     
/*     */     public int lastBytesRead() {
/* 165 */       return this.delegate.lastBytesRead();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean continueReading() {
/* 170 */       return this.delegate.continueReading();
/*     */     }
/*     */ 
/*     */     
/*     */     public int attemptedBytesRead() {
/* 175 */       return this.delegate.attemptedBytesRead();
/*     */     }
/*     */ 
/*     */     
/*     */     public void attemptedBytesRead(int bytes) {
/* 180 */       this.delegate.attemptedBytesRead(bytes);
/*     */     }
/*     */ 
/*     */     
/*     */     public void readComplete() {
/* 185 */       this.delegate.readComplete();
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface ExtendedHandle extends Handle {
/*     */     boolean continueReading(UncheckedBooleanSupplier param1UncheckedBooleanSupplier);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public static interface Handle {
/*     */     ByteBuf allocate(ByteBufAllocator param1ByteBufAllocator);
/*     */     
/*     */     int guess();
/*     */     
/*     */     void reset(ChannelConfig param1ChannelConfig);
/*     */     
/*     */     void incMessagesRead(int param1Int);
/*     */     
/*     */     void lastBytesRead(int param1Int);
/*     */     
/*     */     int lastBytesRead();
/*     */     
/*     */     void attemptedBytesRead(int param1Int);
/*     */     
/*     */     int attemptedBytesRead();
/*     */     
/*     */     boolean continueReading();
/*     */     
/*     */     void readComplete();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\RecvByteBufAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */