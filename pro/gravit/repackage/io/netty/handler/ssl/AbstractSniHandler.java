/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelOutboundHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ByteToMessageDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DecoderException;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
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
/*     */ public abstract class AbstractSniHandler<T>
/*     */   extends ByteToMessageDecoder
/*     */   implements ChannelOutboundHandler
/*     */ {
/*  45 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractSniHandler.class);
/*     */   
/*     */   private boolean handshakeFailed;
/*     */   
/*     */   private boolean suppressRead;
/*     */   private boolean readPending;
/*     */   private ByteBuf handshakeBuffer;
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/*  54 */     if (!this.suppressRead && !this.handshakeFailed) {
/*     */       try {
/*  56 */         int readerIndex = in.readerIndex();
/*  57 */         int readableBytes = in.readableBytes();
/*  58 */         int handshakeLength = -1;
/*     */ 
/*     */         
/*  61 */         while (readableBytes >= 5) {
/*  62 */           int len, majorVersion, contentType = in.getUnsignedByte(readerIndex);
/*  63 */           switch (contentType) {
/*     */             
/*     */             case 20:
/*     */             case 21:
/*  67 */               len = SslUtils.getEncryptedPacketLength(in, readerIndex);
/*     */ 
/*     */               
/*  70 */               if (len == -2) {
/*  71 */                 this.handshakeFailed = true;
/*     */                 
/*  73 */                 NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
/*  74 */                 in.skipBytes(in.readableBytes());
/*  75 */                 ctx.fireUserEventTriggered(new SniCompletionEvent(e));
/*  76 */                 SslUtils.handleHandshakeFailure(ctx, e, true);
/*  77 */                 throw e;
/*     */               } 
/*  79 */               if (len == -1) {
/*     */                 return;
/*     */               }
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*  86 */               select(ctx, null);
/*     */               return;
/*     */             case 22:
/*  89 */               majorVersion = in.getUnsignedByte(readerIndex + 1);
/*     */               
/*  91 */               if (majorVersion == 3) {
/*  92 */                 int packetLength = in.getUnsignedShort(readerIndex + 3) + 5;
/*     */ 
/*     */                 
/*  95 */                 if (readableBytes < packetLength) {
/*     */                   return;
/*     */                 }
/*  98 */                 if (packetLength == 5) {
/*  99 */                   select(ctx, null);
/*     */                   
/*     */                   return;
/*     */                 } 
/* 103 */                 int endOffset = readerIndex + packetLength;
/*     */ 
/*     */                 
/* 106 */                 if (handshakeLength == -1) {
/* 107 */                   if (readerIndex + 4 > endOffset) {
/*     */                     return;
/*     */                   }
/*     */ 
/*     */                   
/* 112 */                   int handshakeType = in.getUnsignedByte(readerIndex + 5);
/*     */ 
/*     */ 
/*     */ 
/*     */                   
/* 117 */                   if (handshakeType != 1) {
/* 118 */                     select(ctx, null);
/*     */ 
/*     */                     
/*     */                     return;
/*     */                   } 
/*     */                   
/* 124 */                   handshakeLength = in.getUnsignedMedium(readerIndex + 5 + 1);
/*     */ 
/*     */ 
/*     */                   
/* 128 */                   readerIndex += 4;
/* 129 */                   packetLength -= 4;
/*     */                   
/* 131 */                   if (handshakeLength + 4 + 5 <= packetLength) {
/*     */ 
/*     */                     
/* 134 */                     readerIndex += 5;
/* 135 */                     select(ctx, extractSniHostname(in, readerIndex, readerIndex + handshakeLength));
/*     */                     return;
/*     */                   } 
/* 138 */                   if (this.handshakeBuffer == null) {
/* 139 */                     this.handshakeBuffer = ctx.alloc().buffer(handshakeLength);
/*     */                   } else {
/*     */                     
/* 142 */                     this.handshakeBuffer.clear();
/*     */                   } 
/*     */                 } 
/*     */ 
/*     */ 
/*     */                 
/* 148 */                 this.handshakeBuffer.writeBytes(in, readerIndex + 5, packetLength - 5);
/*     */                 
/* 150 */                 readerIndex += packetLength;
/* 151 */                 readableBytes -= packetLength;
/* 152 */                 if (handshakeLength <= this.handshakeBuffer.readableBytes()) {
/* 153 */                   select(ctx, extractSniHostname(this.handshakeBuffer, 0, handshakeLength));
/*     */                   return;
/*     */                 } 
/*     */                 continue;
/*     */               } 
/*     */               break;
/*     */           } 
/*     */           
/* 161 */           select(ctx, null);
/*     */           
/*     */           return;
/*     */         } 
/* 165 */       } catch (NotSslRecordException e) {
/*     */         
/* 167 */         throw e;
/* 168 */       } catch (Exception e) {
/*     */         
/* 170 */         if (logger.isDebugEnabled()) {
/* 171 */           logger.debug("Unexpected client hello packet: " + ByteBufUtil.hexDump(in), e);
/*     */         }
/* 173 */         select(ctx, null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String extractSniHostname(ByteBuf in, int offset, int endOffset) {
/* 199 */     offset += 34;
/*     */     
/* 201 */     if (endOffset - offset >= 6) {
/* 202 */       int sessionIdLength = in.getUnsignedByte(offset);
/* 203 */       offset += sessionIdLength + 1;
/*     */       
/* 205 */       int cipherSuitesLength = in.getUnsignedShort(offset);
/* 206 */       offset += cipherSuitesLength + 2;
/*     */       
/* 208 */       int compressionMethodLength = in.getUnsignedByte(offset);
/* 209 */       offset += compressionMethodLength + 1;
/*     */       
/* 211 */       int extensionsLength = in.getUnsignedShort(offset);
/* 212 */       offset += 2;
/* 213 */       int extensionsLimit = offset + extensionsLength;
/*     */ 
/*     */       
/* 216 */       if (extensionsLimit <= endOffset) {
/* 217 */         while (extensionsLimit - offset >= 4) {
/* 218 */           int extensionType = in.getUnsignedShort(offset);
/* 219 */           offset += 2;
/*     */           
/* 221 */           int extensionLength = in.getUnsignedShort(offset);
/* 222 */           offset += 2;
/*     */           
/* 224 */           if (extensionsLimit - offset < extensionLength) {
/*     */             break;
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 230 */           if (extensionType == 0) {
/* 231 */             offset += 2;
/* 232 */             if (extensionsLimit - offset < 3) {
/*     */               break;
/*     */             }
/*     */             
/* 236 */             int serverNameType = in.getUnsignedByte(offset);
/* 237 */             offset++;
/*     */             
/* 239 */             if (serverNameType == 0) {
/* 240 */               int serverNameLength = in.getUnsignedShort(offset);
/* 241 */               offset += 2;
/*     */               
/* 243 */               if (extensionsLimit - offset < serverNameLength) {
/*     */                 break;
/*     */               }
/*     */               
/* 247 */               String hostname = in.toString(offset, serverNameLength, CharsetUtil.US_ASCII);
/* 248 */               return hostname.toLowerCase(Locale.US);
/*     */             } 
/*     */ 
/*     */             
/*     */             break;
/*     */           } 
/*     */           
/* 255 */           offset += extensionLength;
/*     */         } 
/*     */       }
/*     */     } 
/* 259 */     return null;
/*     */   }
/*     */   
/*     */   private void releaseHandshakeBuffer() {
/* 263 */     if (this.handshakeBuffer != null) {
/* 264 */       this.handshakeBuffer.release();
/* 265 */       this.handshakeBuffer = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void select(final ChannelHandlerContext ctx, final String hostname) throws Exception {
/* 270 */     releaseHandshakeBuffer();
/*     */     
/* 272 */     Future<T> future = lookup(ctx, hostname);
/* 273 */     if (future.isDone()) {
/* 274 */       fireSniCompletionEvent(ctx, hostname, future);
/* 275 */       onLookupComplete(ctx, hostname, future);
/*     */     } else {
/* 277 */       this.suppressRead = true;
/* 278 */       future.addListener((GenericFutureListener)new FutureListener<T>()
/*     */           {
/*     */             public void operationComplete(Future<T> future) {
/*     */               try {
/* 282 */                 AbstractSniHandler.this.suppressRead = false;
/*     */                 try {
/* 284 */                   AbstractSniHandler.this.fireSniCompletionEvent(ctx, hostname, future);
/* 285 */                   AbstractSniHandler.this.onLookupComplete(ctx, hostname, future);
/* 286 */                 } catch (DecoderException err) {
/* 287 */                   ctx.fireExceptionCaught((Throwable)err);
/* 288 */                 } catch (Exception cause) {
/* 289 */                   ctx.fireExceptionCaught((Throwable)new DecoderException(cause));
/* 290 */                 } catch (Throwable cause) {
/* 291 */                   ctx.fireExceptionCaught(cause);
/*     */                 } 
/*     */               } finally {
/* 294 */                 if (AbstractSniHandler.this.readPending) {
/* 295 */                   AbstractSniHandler.this.readPending = false;
/* 296 */                   ctx.read();
/*     */                 } 
/*     */               } 
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
/* 306 */     releaseHandshakeBuffer();
/*     */     
/* 308 */     super.handlerRemoved0(ctx);
/*     */   }
/*     */   
/*     */   private void fireSniCompletionEvent(ChannelHandlerContext ctx, String hostname, Future<T> future) {
/* 312 */     Throwable cause = future.cause();
/* 313 */     if (cause == null) {
/* 314 */       ctx.fireUserEventTriggered(new SniCompletionEvent(hostname));
/*     */     } else {
/* 316 */       ctx.fireUserEventTriggered(new SniCompletionEvent(hostname, cause));
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
/*     */   public void read(ChannelHandlerContext ctx) throws Exception {
/* 338 */     if (this.suppressRead) {
/* 339 */       this.readPending = true;
/*     */     } else {
/* 341 */       ctx.read();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/* 347 */     ctx.bind(localAddress, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/* 353 */     ctx.connect(remoteAddress, localAddress, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 358 */     ctx.disconnect(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 363 */     ctx.close(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 368 */     ctx.deregister(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 373 */     ctx.write(msg, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush(ChannelHandlerContext ctx) throws Exception {
/* 378 */     ctx.flush();
/*     */   }
/*     */   
/*     */   protected abstract Future<T> lookup(ChannelHandlerContext paramChannelHandlerContext, String paramString) throws Exception;
/*     */   
/*     */   protected abstract void onLookupComplete(ChannelHandlerContext paramChannelHandlerContext, String paramString, Future<T> paramFuture) throws Exception;
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\AbstractSniHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */