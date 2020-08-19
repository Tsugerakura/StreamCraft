/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.zip.Checksum;
/*     */ import net.jpountz.lz4.LZ4Factory;
/*     */ import net.jpountz.lz4.LZ4FastDecompressor;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ByteToMessageDecoder;
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
/*     */ public class Lz4FrameDecoder
/*     */   extends ByteToMessageDecoder
/*     */ {
/*     */   private enum State
/*     */   {
/*  52 */     INIT_BLOCK,
/*  53 */     DECOMPRESS_DATA,
/*  54 */     FINISHED,
/*  55 */     CORRUPTED;
/*     */   }
/*     */   
/*  58 */   private State currentState = State.INIT_BLOCK;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private LZ4FastDecompressor decompressor;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ByteBufChecksum checksum;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int blockType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int compressedLength;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int decompressedLength;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int currentChecksum;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Lz4FrameDecoder() {
/* 100 */     this(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Lz4FrameDecoder(boolean validateChecksums) {
/* 111 */     this(LZ4Factory.fastestInstance(), validateChecksums);
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
/*     */   public Lz4FrameDecoder(LZ4Factory factory, boolean validateChecksums) {
/* 127 */     this(factory, validateChecksums ? new Lz4XXHash32(-1756908916) : null);
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
/*     */   public Lz4FrameDecoder(LZ4Factory factory, Checksum checksum) {
/* 140 */     this.decompressor = ((LZ4Factory)ObjectUtil.checkNotNull(factory, "factory")).fastDecompressor();
/* 141 */     this.checksum = (checksum == null) ? null : ByteBufChecksum.wrapChecksum(checksum); } protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/*     */     try {
/*     */       int blockType;
/*     */       int compressedLength;
/*     */       int decompressedLength;
/*     */       int currentChecksum;
/* 147 */       switch (this.currentState) {
/*     */         case INIT_BLOCK:
/* 149 */           if (in.readableBytes() >= 21) {
/*     */ 
/*     */             
/* 152 */             long magic = in.readLong();
/* 153 */             if (magic != 5501767354678207339L) {
/* 154 */               throw new DecompressionException("unexpected block identifier");
/*     */             }
/*     */             
/* 157 */             int token = in.readByte();
/* 158 */             int compressionLevel = (token & 0xF) + 10;
/* 159 */             int i = token & 0xF0;
/*     */             
/* 161 */             int j = Integer.reverseBytes(in.readInt());
/* 162 */             if (j < 0 || j > 33554432) {
/* 163 */               throw new DecompressionException(String.format("invalid compressedLength: %d (expected: 0-%d)", new Object[] {
/*     */                       
/* 165 */                       Integer.valueOf(j), Integer.valueOf(33554432)
/*     */                     }));
/*     */             }
/* 168 */             int k = Integer.reverseBytes(in.readInt());
/* 169 */             int maxDecompressedLength = 1 << compressionLevel;
/* 170 */             if (k < 0 || k > maxDecompressedLength)
/* 171 */               throw new DecompressionException(String.format("invalid decompressedLength: %d (expected: 0-%d)", new Object[] {
/*     */                       
/* 173 */                       Integer.valueOf(k), Integer.valueOf(maxDecompressedLength)
/*     */                     })); 
/* 175 */             if ((k == 0 && j != 0) || (k != 0 && j == 0) || (i == 16 && k != j))
/*     */             {
/*     */               
/* 178 */               throw new DecompressionException(String.format("stream corrupted: compressedLength(%d) and decompressedLength(%d) mismatch", new Object[] {
/*     */                       
/* 180 */                       Integer.valueOf(j), Integer.valueOf(k)
/*     */                     }));
/*     */             }
/* 183 */             int m = Integer.reverseBytes(in.readInt());
/* 184 */             if (k == 0 && j == 0)
/* 185 */             { if (m != 0) {
/* 186 */                 throw new DecompressionException("stream corrupted: checksum error");
/*     */               }
/* 188 */               this.currentState = State.FINISHED;
/* 189 */               this.decompressor = null;
/* 190 */               this.checksum = null; }
/*     */             
/*     */             else
/*     */             
/* 194 */             { this.blockType = i;
/* 195 */               this.compressedLength = j;
/* 196 */               this.decompressedLength = k;
/* 197 */               this.currentChecksum = m;
/*     */               
/* 199 */               this.currentState = State.DECOMPRESS_DATA; } 
/*     */           }  return;
/*     */         case DECOMPRESS_DATA:
/* 202 */           blockType = this.blockType;
/* 203 */           compressedLength = this.compressedLength;
/* 204 */           decompressedLength = this.decompressedLength;
/* 205 */           currentChecksum = this.currentChecksum;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         case FINISHED:
/*     */         case CORRUPTED:
/* 253 */           in.skipBytes(in.readableBytes());
/*     */           return;
/*     */       } 
/* 256 */       throw new IllegalStateException();
/*     */     }
/* 258 */     catch (Exception e) {
/* 259 */       this.currentState = State.CORRUPTED;
/* 260 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 269 */     return (this.currentState == State.FINISHED);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\Lz4FrameDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */