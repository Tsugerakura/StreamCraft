/*     */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*     */ 
/*     */ import com.jcraft.jzlib.Inflater;
/*     */ import com.jcraft.jzlib.JZlib;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
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
/*     */ public class JZlibDecoder
/*     */   extends ZlibDecoder
/*     */ {
/*  28 */   private final Inflater z = new Inflater();
/*     */ 
/*     */   
/*     */   private byte[] dictionary;
/*     */ 
/*     */   
/*     */   private volatile boolean finished;
/*     */ 
/*     */   
/*     */   public JZlibDecoder() {
/*  38 */     this(ZlibWrapper.ZLIB);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JZlibDecoder(ZlibWrapper wrapper) {
/*  47 */     ObjectUtil.checkNotNull(wrapper, "wrapper");
/*     */     
/*  49 */     int resultCode = this.z.init(ZlibUtil.convertWrapperType(wrapper));
/*  50 */     if (resultCode != 0) {
/*  51 */       ZlibUtil.fail(this.z, "initialization failure", resultCode);
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
/*     */   public JZlibDecoder(byte[] dictionary) {
/*  63 */     this.dictionary = (byte[])ObjectUtil.checkNotNull(dictionary, "dictionary");
/*     */     
/*  65 */     int resultCode = this.z.inflateInit(JZlib.W_ZLIB);
/*  66 */     if (resultCode != 0) {
/*  67 */       ZlibUtil.fail(this.z, "initialization failure", resultCode);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/*  77 */     return this.finished;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/*  82 */     if (this.finished) {
/*     */       
/*  84 */       in.skipBytes(in.readableBytes());
/*     */       
/*     */       return;
/*     */     } 
/*  88 */     int inputLength = in.readableBytes();
/*  89 */     if (inputLength == 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/*  95 */       this.z.avail_in = inputLength;
/*  96 */       if (in.hasArray()) {
/*  97 */         this.z.next_in = in.array();
/*  98 */         this.z.next_in_index = in.arrayOffset() + in.readerIndex();
/*     */       } else {
/* 100 */         byte[] array = new byte[inputLength];
/* 101 */         in.getBytes(in.readerIndex(), array);
/* 102 */         this.z.next_in = array;
/* 103 */         this.z.next_in_index = 0;
/*     */       } 
/* 105 */       int oldNextInIndex = this.z.next_in_index;
/*     */ 
/*     */       
/* 108 */       ByteBuf decompressed = ctx.alloc().heapBuffer(inputLength << 1);
/*     */       
/*     */       try {
/*     */         while (true) {
/* 112 */           decompressed.ensureWritable(this.z.avail_in << 1);
/* 113 */           this.z.avail_out = decompressed.writableBytes();
/* 114 */           this.z.next_out = decompressed.array();
/* 115 */           this.z.next_out_index = decompressed.arrayOffset() + decompressed.writerIndex();
/* 116 */           int oldNextOutIndex = this.z.next_out_index;
/*     */ 
/*     */           
/* 119 */           int resultCode = this.z.inflate(2);
/* 120 */           int outputLength = this.z.next_out_index - oldNextOutIndex;
/* 121 */           if (outputLength > 0) {
/* 122 */             decompressed.writerIndex(decompressed.writerIndex() + outputLength);
/*     */           }
/*     */           
/* 125 */           switch (resultCode) {
/*     */             case 2:
/* 127 */               if (this.dictionary == null) {
/* 128 */                 ZlibUtil.fail(this.z, "decompression failure", resultCode); continue;
/*     */               } 
/* 130 */               resultCode = this.z.inflateSetDictionary(this.dictionary, this.dictionary.length);
/* 131 */               if (resultCode != 0) {
/* 132 */                 ZlibUtil.fail(this.z, "failed to set the dictionary", resultCode);
/*     */               }
/*     */               continue;
/*     */             
/*     */             case 1:
/* 137 */               this.finished = true;
/* 138 */               this.z.inflateEnd();
/*     */               break;
/*     */             case 0:
/*     */               continue;
/*     */             case -5:
/* 143 */               if (this.z.avail_in <= 0) {
/*     */                 break;
/*     */               }
/*     */               continue;
/*     */           } 
/* 148 */           ZlibUtil.fail(this.z, "decompression failure", resultCode);
/*     */         } 
/*     */       } finally {
/*     */         
/* 152 */         in.skipBytes(this.z.next_in_index - oldNextInIndex);
/* 153 */         if (decompressed.isReadable()) {
/* 154 */           out.add(decompressed);
/*     */         } else {
/* 156 */           decompressed.release();
/*     */         }
/*     */       
/*     */       }
/*     */     
/*     */     }
/*     */     finally {
/*     */       
/* 164 */       this.z.next_in = null;
/* 165 */       this.z.next_out = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\JZlibDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */