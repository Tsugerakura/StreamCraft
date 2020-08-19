/*     */ package pro.gravit.repackage.io.netty.channel.oio;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
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
/*     */ @Deprecated
/*     */ public abstract class AbstractOioMessageChannel
/*     */   extends AbstractOioChannel
/*     */ {
/*  35 */   private final List<Object> readBuf = new ArrayList();
/*     */   
/*     */   protected AbstractOioMessageChannel(Channel parent) {
/*  38 */     super(parent);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doRead() {
/*  43 */     if (!this.readPending) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  50 */     this.readPending = false;
/*     */     
/*  52 */     ChannelConfig config = config();
/*  53 */     ChannelPipeline pipeline = pipeline();
/*  54 */     RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
/*  55 */     allocHandle.reset(config);
/*     */     
/*  57 */     boolean closed = false;
/*  58 */     Throwable exception = null;
/*     */     
/*     */     try {
/*     */       do {
/*  62 */         int localRead = doReadMessages(this.readBuf);
/*  63 */         if (localRead == 0) {
/*     */           break;
/*     */         }
/*  66 */         if (localRead < 0) {
/*  67 */           closed = true;
/*     */           
/*     */           break;
/*     */         } 
/*  71 */         allocHandle.incMessagesRead(localRead);
/*  72 */       } while (allocHandle.continueReading());
/*  73 */     } catch (Throwable t) {
/*  74 */       exception = t;
/*     */     } 
/*     */     
/*  77 */     boolean readData = false;
/*  78 */     int size = this.readBuf.size();
/*  79 */     if (size > 0) {
/*  80 */       readData = true;
/*  81 */       for (int i = 0; i < size; i++) {
/*  82 */         this.readPending = false;
/*  83 */         pipeline.fireChannelRead(this.readBuf.get(i));
/*     */       } 
/*  85 */       this.readBuf.clear();
/*  86 */       allocHandle.readComplete();
/*  87 */       pipeline.fireChannelReadComplete();
/*     */     } 
/*     */     
/*  90 */     if (exception != null) {
/*  91 */       if (exception instanceof java.io.IOException) {
/*  92 */         closed = true;
/*     */       }
/*     */       
/*  95 */       pipeline.fireExceptionCaught(exception);
/*     */     } 
/*     */     
/*  98 */     if (closed) {
/*  99 */       if (isOpen()) {
/* 100 */         unsafe().close(unsafe().voidPromise());
/*     */       }
/* 102 */     } else if (this.readPending || config.isAutoRead() || (!readData && isActive())) {
/*     */ 
/*     */       
/* 105 */       read();
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract int doReadMessages(List<Object> paramList) throws Exception;
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\oio\AbstractOioMessageChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */