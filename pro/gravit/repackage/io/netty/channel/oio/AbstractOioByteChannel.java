/*     */ package pro.gravit.repackage.io.netty.channel.oio;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelMetadata;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOption;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.FileRegion;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ChannelInputShutdownEvent;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ChannelInputShutdownReadComplete;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractOioByteChannel
/*     */   extends AbstractOioChannel
/*     */ {
/*  42 */   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
/*  43 */   private static final String EXPECTED_TYPES = " (expected: " + 
/*  44 */     StringUtil.simpleClassName(ByteBuf.class) + ", " + 
/*  45 */     StringUtil.simpleClassName(FileRegion.class) + ')';
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractOioByteChannel(Channel parent) {
/*  51 */     super(parent);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelMetadata metadata() {
/*  56 */     return METADATA;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean isInputShutdown();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract ChannelFuture shutdownInput();
/*     */ 
/*     */ 
/*     */   
/*     */   private void closeOnRead(ChannelPipeline pipeline) {
/*  72 */     if (isOpen()) {
/*  73 */       if (Boolean.TRUE.equals(config().getOption(ChannelOption.ALLOW_HALF_CLOSURE))) {
/*  74 */         shutdownInput();
/*  75 */         pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
/*     */       } else {
/*  77 */         unsafe().close(unsafe().voidPromise());
/*     */       } 
/*  79 */       pipeline.fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close, RecvByteBufAllocator.Handle allocHandle) {
/*  85 */     if (byteBuf != null) {
/*  86 */       if (byteBuf.isReadable()) {
/*  87 */         this.readPending = false;
/*  88 */         pipeline.fireChannelRead(byteBuf);
/*     */       } else {
/*  90 */         byteBuf.release();
/*     */       } 
/*     */     }
/*  93 */     allocHandle.readComplete();
/*  94 */     pipeline.fireChannelReadComplete();
/*  95 */     pipeline.fireExceptionCaught(cause);
/*  96 */     if (close || cause instanceof java.io.IOException) {
/*  97 */       closeOnRead(pipeline);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doRead() {
/* 103 */     ChannelConfig config = config();
/* 104 */     if (isInputShutdown() || !this.readPending) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 111 */     this.readPending = false;
/*     */     
/* 113 */     ChannelPipeline pipeline = pipeline();
/* 114 */     ByteBufAllocator allocator = config.getAllocator();
/* 115 */     RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
/* 116 */     allocHandle.reset(config);
/*     */     
/* 118 */     ByteBuf byteBuf = null;
/* 119 */     boolean close = false;
/* 120 */     boolean readData = false;
/*     */     try {
/* 122 */       byteBuf = allocHandle.allocate(allocator);
/*     */       do {
/* 124 */         allocHandle.lastBytesRead(doReadBytes(byteBuf));
/* 125 */         if (allocHandle.lastBytesRead() <= 0) {
/* 126 */           if (!byteBuf.isReadable()) {
/* 127 */             byteBuf.release();
/* 128 */             byteBuf = null;
/* 129 */             close = (allocHandle.lastBytesRead() < 0);
/* 130 */             if (close)
/*     */             {
/* 132 */               this.readPending = false;
/*     */             }
/*     */           } 
/*     */           break;
/*     */         } 
/* 137 */         readData = true;
/*     */ 
/*     */         
/* 140 */         int available = available();
/* 141 */         if (available <= 0) {
/*     */           break;
/*     */         }
/*     */ 
/*     */         
/* 146 */         if (byteBuf.isWritable())
/* 147 */           continue;  int capacity = byteBuf.capacity();
/* 148 */         int maxCapacity = byteBuf.maxCapacity();
/* 149 */         if (capacity == maxCapacity) {
/* 150 */           allocHandle.incMessagesRead(1);
/* 151 */           this.readPending = false;
/* 152 */           pipeline.fireChannelRead(byteBuf);
/* 153 */           byteBuf = allocHandle.allocate(allocator);
/*     */         } else {
/* 155 */           int writerIndex = byteBuf.writerIndex();
/* 156 */           if (writerIndex + available > maxCapacity) {
/* 157 */             byteBuf.capacity(maxCapacity);
/*     */           } else {
/* 159 */             byteBuf.ensureWritable(available);
/*     */           }
/*     */         
/*     */         } 
/* 163 */       } while (allocHandle.continueReading());
/*     */       
/* 165 */       if (byteBuf != null) {
/*     */ 
/*     */         
/* 168 */         if (byteBuf.isReadable()) {
/* 169 */           this.readPending = false;
/* 170 */           pipeline.fireChannelRead(byteBuf);
/*     */         } else {
/* 172 */           byteBuf.release();
/*     */         } 
/* 174 */         byteBuf = null;
/*     */       } 
/*     */       
/* 177 */       if (readData) {
/* 178 */         allocHandle.readComplete();
/* 179 */         pipeline.fireChannelReadComplete();
/*     */       } 
/*     */       
/* 182 */       if (close) {
/* 183 */         closeOnRead(pipeline);
/*     */       }
/* 185 */     } catch (Throwable t) {
/* 186 */       handleReadException(pipeline, byteBuf, t, close, allocHandle);
/*     */     } finally {
/* 188 */       if (this.readPending || config.isAutoRead() || (!readData && isActive()))
/*     */       {
/*     */         
/* 191 */         read();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
/*     */     while (true) {
/* 199 */       Object msg = in.current();
/* 200 */       if (msg == null) {
/*     */         break;
/*     */       }
/*     */       
/* 204 */       if (msg instanceof ByteBuf) {
/* 205 */         ByteBuf buf = (ByteBuf)msg;
/* 206 */         int readableBytes = buf.readableBytes();
/* 207 */         while (readableBytes > 0) {
/* 208 */           doWriteBytes(buf);
/* 209 */           int newReadableBytes = buf.readableBytes();
/* 210 */           in.progress((readableBytes - newReadableBytes));
/* 211 */           readableBytes = newReadableBytes;
/*     */         } 
/* 213 */         in.remove(); continue;
/* 214 */       }  if (msg instanceof FileRegion) {
/* 215 */         FileRegion region = (FileRegion)msg;
/* 216 */         long transferred = region.transferred();
/* 217 */         doWriteFileRegion(region);
/* 218 */         in.progress(region.transferred() - transferred);
/* 219 */         in.remove(); continue;
/*     */       } 
/* 221 */       in.remove(new UnsupportedOperationException("unsupported message type: " + 
/* 222 */             StringUtil.simpleClassName(msg)));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final Object filterOutboundMessage(Object msg) throws Exception {
/* 229 */     if (msg instanceof ByteBuf || msg instanceof FileRegion) {
/* 230 */       return msg;
/*     */     }
/*     */     
/* 233 */     throw new UnsupportedOperationException("unsupported message type: " + 
/* 234 */         StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
/*     */   }
/*     */   
/*     */   protected abstract int available();
/*     */   
/*     */   protected abstract int doReadBytes(ByteBuf paramByteBuf) throws Exception;
/*     */   
/*     */   protected abstract void doWriteBytes(ByteBuf paramByteBuf) throws Exception;
/*     */   
/*     */   protected abstract void doWriteFileRegion(FileRegion paramFileRegion) throws Exception;
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\oio\AbstractOioByteChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */