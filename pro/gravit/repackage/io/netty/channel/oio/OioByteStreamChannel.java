/*     */ package pro.gravit.repackage.io.netty.channel.oio;
/*     */ 
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.channels.Channels;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.NotYetConnectedException;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.FileRegion;
/*     */ import pro.gravit.repackage.io.netty.channel.RecvByteBufAllocator;
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
/*     */ @Deprecated
/*     */ public abstract class OioByteStreamChannel
/*     */   extends AbstractOioByteChannel
/*     */ {
/*  41 */   private static final InputStream CLOSED_IN = new InputStream()
/*     */     {
/*     */       public int read() {
/*  44 */         return -1;
/*     */       }
/*     */     };
/*     */   
/*  48 */   private static final OutputStream CLOSED_OUT = new OutputStream()
/*     */     {
/*     */       public void write(int b) throws IOException {
/*  51 */         throw new ClosedChannelException();
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */   
/*     */   private InputStream is;
/*     */ 
/*     */   
/*     */   private OutputStream os;
/*     */   
/*     */   private WritableByteChannel outChannel;
/*     */ 
/*     */   
/*     */   protected OioByteStreamChannel(Channel parent) {
/*  66 */     super(parent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void activate(InputStream is, OutputStream os) {
/*  73 */     if (this.is != null) {
/*  74 */       throw new IllegalStateException("input was set already");
/*     */     }
/*  76 */     if (this.os != null) {
/*  77 */       throw new IllegalStateException("output was set already");
/*     */     }
/*  79 */     this.is = (InputStream)ObjectUtil.checkNotNull(is, "is");
/*  80 */     this.os = (OutputStream)ObjectUtil.checkNotNull(os, "os");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isActive() {
/*  85 */     InputStream is = this.is;
/*  86 */     if (is == null || is == CLOSED_IN) {
/*  87 */       return false;
/*     */     }
/*     */     
/*  90 */     OutputStream os = this.os;
/*  91 */     return (os != null && os != CLOSED_OUT);
/*     */   }
/*     */ 
/*     */   
/*     */   protected int available() {
/*     */     try {
/*  97 */       return this.is.available();
/*  98 */     } catch (IOException ignored) {
/*  99 */       return 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected int doReadBytes(ByteBuf buf) throws Exception {
/* 105 */     RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
/* 106 */     allocHandle.attemptedBytesRead(Math.max(1, Math.min(available(), buf.maxWritableBytes())));
/* 107 */     return buf.writeBytes(this.is, allocHandle.attemptedBytesRead());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWriteBytes(ByteBuf buf) throws Exception {
/* 112 */     OutputStream os = this.os;
/* 113 */     if (os == null) {
/* 114 */       throw new NotYetConnectedException();
/*     */     }
/* 116 */     buf.readBytes(os, buf.readableBytes());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doWriteFileRegion(FileRegion region) throws Exception {
/* 121 */     OutputStream os = this.os;
/* 122 */     if (os == null) {
/* 123 */       throw new NotYetConnectedException();
/*     */     }
/* 125 */     if (this.outChannel == null) {
/* 126 */       this.outChannel = Channels.newChannel(os);
/*     */     }
/*     */     
/* 129 */     long written = 0L;
/*     */     do {
/* 131 */       long localWritten = region.transferTo(this.outChannel, written);
/* 132 */       if (localWritten == -1L) {
/* 133 */         checkEOF(region);
/*     */         return;
/*     */       } 
/* 136 */       written += localWritten;
/*     */     }
/* 138 */     while (written < region.count());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void checkEOF(FileRegion region) throws IOException {
/* 145 */     if (region.transferred() < region.count()) {
/* 146 */       throw new EOFException("Expected to be able to write " + region.count() + " bytes, but only wrote " + region
/* 147 */           .transferred());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doClose() throws Exception {
/* 153 */     InputStream is = this.is;
/* 154 */     OutputStream os = this.os;
/* 155 */     this.is = CLOSED_IN;
/* 156 */     this.os = CLOSED_OUT;
/*     */     
/*     */     try {
/* 159 */       if (is != null) {
/* 160 */         is.close();
/*     */       }
/*     */     } finally {
/* 163 */       if (os != null)
/* 164 */         os.close(); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\oio\OioByteStreamChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */