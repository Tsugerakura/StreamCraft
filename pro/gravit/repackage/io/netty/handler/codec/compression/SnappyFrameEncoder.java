/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.MessageToByteEncoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SnappyFrameEncoder
/*     */   extends MessageToByteEncoder<ByteBuf>
/*     */ {
/*     */   private static final int MIN_COMPRESSIBLE_LENGTH = 18;
/*  41 */   private static final byte[] STREAM_START = new byte[] { -1, 6, 0, 0, 115, 78, 97, 80, 112, 89 };
/*     */ 
/*     */ 
/*     */   
/*  45 */   private final Snappy snappy = new Snappy();
/*     */   
/*     */   private boolean started;
/*     */   
/*     */   protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
/*  50 */     if (!in.isReadable()) {
/*     */       return;
/*     */     }
/*     */     
/*  54 */     if (!this.started) {
/*  55 */       this.started = true;
/*  56 */       out.writeBytes(STREAM_START);
/*     */     } 
/*     */     
/*  59 */     int dataLength = in.readableBytes();
/*  60 */     if (dataLength > 18) {
/*     */       while (true) {
/*  62 */         int lengthIdx = out.writerIndex() + 1;
/*  63 */         if (dataLength < 18) {
/*  64 */           ByteBuf byteBuf = in.readSlice(dataLength);
/*  65 */           writeUnencodedChunk(byteBuf, out, dataLength);
/*     */           
/*     */           break;
/*     */         } 
/*  69 */         out.writeInt(0);
/*  70 */         if (dataLength > 32767) {
/*  71 */           ByteBuf byteBuf = in.readSlice(32767);
/*  72 */           calculateAndWriteChecksum(byteBuf, out);
/*  73 */           this.snappy.encode(byteBuf, out, 32767);
/*  74 */           setChunkLength(out, lengthIdx);
/*  75 */           dataLength -= 32767; continue;
/*     */         } 
/*  77 */         ByteBuf slice = in.readSlice(dataLength);
/*  78 */         calculateAndWriteChecksum(slice, out);
/*  79 */         this.snappy.encode(slice, out, dataLength);
/*  80 */         setChunkLength(out, lengthIdx);
/*     */         
/*     */         break;
/*     */       } 
/*     */     } else {
/*  85 */       writeUnencodedChunk(in, out, dataLength);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void writeUnencodedChunk(ByteBuf in, ByteBuf out, int dataLength) {
/*  90 */     out.writeByte(1);
/*  91 */     writeChunkLength(out, dataLength + 4);
/*  92 */     calculateAndWriteChecksum(in, out);
/*  93 */     out.writeBytes(in, dataLength);
/*     */   }
/*     */   
/*     */   private static void setChunkLength(ByteBuf out, int lengthIdx) {
/*  97 */     int chunkLength = out.writerIndex() - lengthIdx - 3;
/*  98 */     if (chunkLength >>> 24 != 0) {
/*  99 */       throw new CompressionException("compressed data too large: " + chunkLength);
/*     */     }
/* 101 */     out.setMediumLE(lengthIdx, chunkLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void writeChunkLength(ByteBuf out, int chunkLength) {
/* 111 */     out.writeMediumLE(chunkLength);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void calculateAndWriteChecksum(ByteBuf slice, ByteBuf out) {
/* 121 */     out.writeIntLE(Snappy.calculateChecksum(slice));
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\SnappyFrameEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */