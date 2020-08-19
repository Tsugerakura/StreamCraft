/*     */ package pro.gravit.repackage.io.netty.channel.nio;
/*     */ 
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.AbstractChannel;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelConfig;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelMetadata;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundBuffer;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPipeline;
/*     */ import pro.gravit.repackage.io.netty.channel.FileRegion;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ChannelInputShutdownEvent;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.ChannelInputShutdownReadComplete;
/*     */ import pro.gravit.repackage.io.netty.channel.socket.SocketChannelConfig;
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
/*     */ public abstract class AbstractNioByteChannel
/*     */   extends AbstractNioChannel
/*     */ {
/*  44 */   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
/*  45 */   private static final String EXPECTED_TYPES = " (expected: " + 
/*  46 */     StringUtil.simpleClassName(ByteBuf.class) + ", " + 
/*  47 */     StringUtil.simpleClassName(FileRegion.class) + ')';
/*     */   
/*  49 */   private final Runnable flushTask = new Runnable()
/*     */     {
/*     */       
/*     */       public void run()
/*     */       {
/*  54 */         ((AbstractNioChannel.AbstractNioUnsafe)AbstractNioByteChannel.this.unsafe()).flush0();
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean inputClosedSeenErrorOnRead;
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractNioByteChannel(Channel parent, SelectableChannel ch) {
/*  66 */     super(parent, ch, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isInputShutdown0() {
/*  75 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
/*  80 */     return new NioByteUnsafe();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelMetadata metadata() {
/*  85 */     return METADATA;
/*     */   }
/*     */   
/*     */   final boolean shouldBreakReadReady(ChannelConfig config) {
/*  89 */     return (isInputShutdown0() && (this.inputClosedSeenErrorOnRead || !isAllowHalfClosure(config)));
/*     */   }
/*     */   
/*     */   private static boolean isAllowHalfClosure(ChannelConfig config) {
/*  93 */     return (config instanceof SocketChannelConfig && ((SocketChannelConfig)config)
/*  94 */       .isAllowHalfClosure());
/*     */   }
/*     */   
/*     */   protected class NioByteUnsafe
/*     */     extends AbstractNioChannel.AbstractNioUnsafe {
/*     */     private void closeOnRead(ChannelPipeline pipeline) {
/* 100 */       if (!AbstractNioByteChannel.this.isInputShutdown0()) {
/* 101 */         if (AbstractNioByteChannel.isAllowHalfClosure(AbstractNioByteChannel.this.config())) {
/* 102 */           AbstractNioByteChannel.this.shutdownInput();
/* 103 */           pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
/*     */         } else {
/* 105 */           close(voidPromise());
/*     */         } 
/*     */       } else {
/* 108 */         AbstractNioByteChannel.this.inputClosedSeenErrorOnRead = true;
/* 109 */         pipeline.fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private void handleReadException(ChannelPipeline pipeline, ByteBuf byteBuf, Throwable cause, boolean close, RecvByteBufAllocator.Handle allocHandle) {
/* 115 */       if (byteBuf != null) {
/* 116 */         if (byteBuf.isReadable()) {
/* 117 */           AbstractNioByteChannel.this.readPending = false;
/* 118 */           pipeline.fireChannelRead(byteBuf);
/*     */         } else {
/* 120 */           byteBuf.release();
/*     */         } 
/*     */       }
/* 123 */       allocHandle.readComplete();
/* 124 */       pipeline.fireChannelReadComplete();
/* 125 */       pipeline.fireExceptionCaught(cause);
/* 126 */       if (close || cause instanceof java.io.IOException) {
/* 127 */         closeOnRead(pipeline);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public final void read() {
/* 133 */       ChannelConfig config = AbstractNioByteChannel.this.config();
/* 134 */       if (AbstractNioByteChannel.this.shouldBreakReadReady(config)) {
/* 135 */         AbstractNioByteChannel.this.clearReadPending();
/*     */         return;
/*     */       } 
/* 138 */       ChannelPipeline pipeline = AbstractNioByteChannel.this.pipeline();
/* 139 */       ByteBufAllocator allocator = config.getAllocator();
/* 140 */       RecvByteBufAllocator.Handle allocHandle = recvBufAllocHandle();
/* 141 */       allocHandle.reset(config);
/*     */       
/* 143 */       ByteBuf byteBuf = null;
/* 144 */       boolean close = false;
/*     */       try {
/*     */         do {
/* 147 */           byteBuf = allocHandle.allocate(allocator);
/* 148 */           allocHandle.lastBytesRead(AbstractNioByteChannel.this.doReadBytes(byteBuf));
/* 149 */           if (allocHandle.lastBytesRead() <= 0) {
/*     */             
/* 151 */             byteBuf.release();
/* 152 */             byteBuf = null;
/* 153 */             close = (allocHandle.lastBytesRead() < 0);
/* 154 */             if (close)
/*     */             {
/* 156 */               AbstractNioByteChannel.this.readPending = false;
/*     */             }
/*     */             
/*     */             break;
/*     */           } 
/* 161 */           allocHandle.incMessagesRead(1);
/* 162 */           AbstractNioByteChannel.this.readPending = false;
/* 163 */           pipeline.fireChannelRead(byteBuf);
/* 164 */           byteBuf = null;
/* 165 */         } while (allocHandle.continueReading());
/*     */         
/* 167 */         allocHandle.readComplete();
/* 168 */         pipeline.fireChannelReadComplete();
/*     */         
/* 170 */         if (close) {
/* 171 */           closeOnRead(pipeline);
/*     */         }
/* 173 */       } catch (Throwable t) {
/* 174 */         handleReadException(pipeline, byteBuf, t, close, allocHandle);
/*     */ 
/*     */       
/*     */       }
/*     */       finally {
/*     */ 
/*     */ 
/*     */         
/* 182 */         if (!AbstractNioByteChannel.this.readPending && !config.isAutoRead()) {
/* 183 */           removeReadOp();
/*     */         }
/*     */       } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int doWrite0(ChannelOutboundBuffer in) throws Exception {
/* 204 */     Object msg = in.current();
/* 205 */     if (msg == null)
/*     */     {
/* 207 */       return 0;
/*     */     }
/* 209 */     return doWriteInternal(in, in.current());
/*     */   }
/*     */   
/*     */   private int doWriteInternal(ChannelOutboundBuffer in, Object msg) throws Exception {
/* 213 */     if (msg instanceof ByteBuf) {
/* 214 */       ByteBuf buf = (ByteBuf)msg;
/* 215 */       if (!buf.isReadable()) {
/* 216 */         in.remove();
/* 217 */         return 0;
/*     */       } 
/*     */       
/* 220 */       int localFlushedAmount = doWriteBytes(buf);
/* 221 */       if (localFlushedAmount > 0) {
/* 222 */         in.progress(localFlushedAmount);
/* 223 */         if (!buf.isReadable()) {
/* 224 */           in.remove();
/*     */         }
/* 226 */         return 1;
/*     */       } 
/* 228 */     } else if (msg instanceof FileRegion) {
/* 229 */       FileRegion region = (FileRegion)msg;
/* 230 */       if (region.transferred() >= region.count()) {
/* 231 */         in.remove();
/* 232 */         return 0;
/*     */       } 
/*     */       
/* 235 */       long localFlushedAmount = doWriteFileRegion(region);
/* 236 */       if (localFlushedAmount > 0L) {
/* 237 */         in.progress(localFlushedAmount);
/* 238 */         if (region.transferred() >= region.count()) {
/* 239 */           in.remove();
/*     */         }
/* 241 */         return 1;
/*     */       } 
/*     */     } else {
/*     */       
/* 245 */       throw new Error();
/*     */     } 
/* 247 */     return Integer.MAX_VALUE;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
/* 252 */     int writeSpinCount = config().getWriteSpinCount();
/*     */     do {
/* 254 */       Object msg = in.current();
/* 255 */       if (msg == null) {
/*     */         
/* 257 */         clearOpWrite();
/*     */         
/*     */         return;
/*     */       } 
/* 261 */       writeSpinCount -= doWriteInternal(in, msg);
/* 262 */     } while (writeSpinCount > 0);
/*     */     
/* 264 */     incompleteWrite((writeSpinCount < 0));
/*     */   }
/*     */ 
/*     */   
/*     */   protected final Object filterOutboundMessage(Object msg) {
/* 269 */     if (msg instanceof ByteBuf) {
/* 270 */       ByteBuf buf = (ByteBuf)msg;
/* 271 */       if (buf.isDirect()) {
/* 272 */         return msg;
/*     */       }
/*     */       
/* 275 */       return newDirectBuffer(buf);
/*     */     } 
/*     */     
/* 278 */     if (msg instanceof FileRegion) {
/* 279 */       return msg;
/*     */     }
/*     */     
/* 282 */     throw new UnsupportedOperationException("unsupported message type: " + 
/* 283 */         StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
/*     */   }
/*     */ 
/*     */   
/*     */   protected final void incompleteWrite(boolean setOpWrite) {
/* 288 */     if (setOpWrite) {
/* 289 */       setOpWrite();
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 295 */       clearOpWrite();
/*     */ 
/*     */       
/* 298 */       eventLoop().execute(this.flushTask);
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
/*     */   protected final void setOpWrite() {
/* 323 */     SelectionKey key = selectionKey();
/*     */ 
/*     */ 
/*     */     
/* 327 */     if (!key.isValid()) {
/*     */       return;
/*     */     }
/* 330 */     int interestOps = key.interestOps();
/* 331 */     if ((interestOps & 0x4) == 0) {
/* 332 */       key.interestOps(interestOps | 0x4);
/*     */     }
/*     */   }
/*     */   
/*     */   protected final void clearOpWrite() {
/* 337 */     SelectionKey key = selectionKey();
/*     */ 
/*     */ 
/*     */     
/* 341 */     if (!key.isValid()) {
/*     */       return;
/*     */     }
/* 344 */     int interestOps = key.interestOps();
/* 345 */     if ((interestOps & 0x4) != 0)
/* 346 */       key.interestOps(interestOps & 0xFFFFFFFB); 
/*     */   }
/*     */   
/*     */   protected abstract ChannelFuture shutdownInput();
/*     */   
/*     */   protected abstract long doWriteFileRegion(FileRegion paramFileRegion) throws Exception;
/*     */   
/*     */   protected abstract int doReadBytes(ByteBuf paramByteBuf) throws Exception;
/*     */   
/*     */   protected abstract int doWriteBytes(ByteBuf paramByteBuf) throws Exception;
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\nio\AbstractNioByteChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */