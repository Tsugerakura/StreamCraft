/*     */ package pro.gravit.repackage.io.netty.channel.nio;
/*     */ 
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.channel.AbstractChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
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
/*     */ 
/*     */ public abstract class AbstractNioMessageChannel
/*     */   extends AbstractNioChannel
/*     */ {
/*     */   boolean inputShutdown;
/*     */   
/*     */   protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
/*  42 */     super(parent, ch, readInterestOp);
/*     */   }
/*     */ 
/*     */   
/*     */   protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
/*  47 */     return new NioMessageUnsafe();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doBeginRead() throws Exception {
/*  52 */     if (this.inputShutdown) {
/*     */       return;
/*     */     }
/*  55 */     super.doBeginRead();
/*     */   }
/*     */   
/*     */   private final class NioMessageUnsafe extends AbstractNioChannel.AbstractNioUnsafe {
/*     */     private NioMessageUnsafe() {
/*  60 */       this.readBuf = new ArrayList();
/*     */     }
/*     */     private final List<Object> readBuf;
/*     */     public void read() {
/*  64 */       assert AbstractNioMessageChannel.this.eventLoop().inEventLoop();
/*  65 */       ChannelConfig config = AbstractNioMessageChannel.this.config();
/*  66 */       ChannelPipeline pipeline = AbstractNioMessageChannel.this.pipeline();
/*  67 */       RecvByteBufAllocator.Handle allocHandle = AbstractNioMessageChannel.this.unsafe().recvBufAllocHandle();
/*  68 */       allocHandle.reset(config);
/*     */       
/*  70 */       boolean closed = false;
/*  71 */       Throwable exception = null;
/*     */       try {
/*     */         while (true) {
/*     */           
/*  75 */           try { int localRead = AbstractNioMessageChannel.this.doReadMessages(this.readBuf);
/*  76 */             if (localRead == 0) {
/*     */               break;
/*     */             }
/*  79 */             if (localRead < 0) {
/*  80 */               closed = true;
/*     */               
/*     */               break;
/*     */             } 
/*  84 */             allocHandle.incMessagesRead(localRead);
/*  85 */             if (!allocHandle.continueReading())
/*  86 */               break;  } catch (Throwable t)
/*  87 */           { exception = t; break; }
/*     */         
/*     */         } 
/*  90 */         int size = this.readBuf.size();
/*  91 */         for (int i = 0; i < size; i++) {
/*  92 */           AbstractNioMessageChannel.this.readPending = false;
/*  93 */           pipeline.fireChannelRead(this.readBuf.get(i));
/*     */         } 
/*  95 */         this.readBuf.clear();
/*  96 */         allocHandle.readComplete();
/*  97 */         pipeline.fireChannelReadComplete();
/*     */         
/*  99 */         if (exception != null) {
/* 100 */           closed = AbstractNioMessageChannel.this.closeOnReadError(exception);
/*     */           
/* 102 */           pipeline.fireExceptionCaught(exception);
/*     */         } 
/*     */         
/* 105 */         if (closed) {
/* 106 */           AbstractNioMessageChannel.this.inputShutdown = true;
/* 107 */           if (AbstractNioMessageChannel.this.isOpen()) {
/* 108 */             close(voidPromise());
/*     */           
/*     */           }
/*     */         
/*     */         }
/*     */ 
/*     */       
/*     */       }
/*     */       finally {
/*     */         
/* 118 */         if (!AbstractNioMessageChannel.this.readPending && !config.isAutoRead()) {
/* 119 */           removeReadOp();
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
/* 127 */     SelectionKey key = selectionKey();
/* 128 */     int interestOps = key.interestOps();
/*     */     
/*     */     while (true) {
/* 131 */       Object msg = in.current();
/* 132 */       if (msg == null) {
/*     */         
/* 134 */         if ((interestOps & 0x4) != 0) {
/* 135 */           key.interestOps(interestOps & 0xFFFFFFFB);
/*     */         }
/*     */         break;
/*     */       } 
/*     */       try {
/* 140 */         boolean done = false;
/* 141 */         for (int i = config().getWriteSpinCount() - 1; i >= 0; i--) {
/* 142 */           if (doWriteMessage(msg, in)) {
/* 143 */             done = true;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/* 148 */         if (done) {
/* 149 */           in.remove();
/*     */           continue;
/*     */         } 
/* 152 */         if ((interestOps & 0x4) == 0) {
/* 153 */           key.interestOps(interestOps | 0x4);
/*     */         
/*     */         }
/*     */       }
/* 157 */       catch (Exception e) {
/* 158 */         if (continueOnWriteError()) {
/* 159 */           in.remove(e); continue;
/*     */         } 
/* 161 */         throw e;
/*     */       } 
/*     */       break;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean continueOnWriteError() {
/* 171 */     return false;
/*     */   }
/*     */   
/*     */   protected boolean closeOnReadError(Throwable cause) {
/* 175 */     if (!isActive())
/*     */     {
/* 177 */       return true;
/*     */     }
/* 179 */     if (cause instanceof java.net.PortUnreachableException) {
/* 180 */       return false;
/*     */     }
/* 182 */     if (cause instanceof java.io.IOException)
/*     */     {
/*     */       
/* 185 */       return !(this instanceof pro.gravit.repackage.io.netty.channel.ServerChannel);
/*     */     }
/* 187 */     return true;
/*     */   }
/*     */   
/*     */   protected abstract int doReadMessages(List<Object> paramList) throws Exception;
/*     */   
/*     */   protected abstract boolean doWriteMessage(Object paramObject, ChannelOutboundBuffer paramChannelOutboundBuffer) throws Exception;
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\nio\AbstractNioMessageChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */