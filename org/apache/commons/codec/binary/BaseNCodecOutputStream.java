/*     */ package org.apache.commons.codec.binary;
/*     */ 
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BaseNCodecOutputStream
/*     */   extends FilterOutputStream
/*     */ {
/*     */   private final boolean doEncode;
/*     */   private final BaseNCodec baseNCodec;
/*  46 */   private final byte[] singleByte = new byte[1];
/*     */   
/*  48 */   private final BaseNCodec.Context context = new BaseNCodec.Context();
/*     */ 
/*     */   
/*     */   public BaseNCodecOutputStream(OutputStream out, BaseNCodec basedCodec, boolean doEncode) {
/*  52 */     super(out);
/*  53 */     this.baseNCodec = basedCodec;
/*  54 */     this.doEncode = doEncode;
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
/*     */   public void write(int i) throws IOException {
/*  67 */     this.singleByte[0] = (byte)i;
/*  68 */     write(this.singleByte, 0, 1);
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
/*     */   public void write(byte[] b, int offset, int len) throws IOException {
/*  91 */     if (b == null)
/*  92 */       throw new NullPointerException(); 
/*  93 */     if (offset < 0 || len < 0)
/*  94 */       throw new IndexOutOfBoundsException(); 
/*  95 */     if (offset > b.length || offset + len > b.length)
/*  96 */       throw new IndexOutOfBoundsException(); 
/*  97 */     if (len > 0) {
/*  98 */       if (this.doEncode) {
/*  99 */         this.baseNCodec.encode(b, offset, len, this.context);
/*     */       } else {
/* 101 */         this.baseNCodec.decode(b, offset, len, this.context);
/*     */       } 
/* 103 */       flush(false);
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
/*     */   private void flush(boolean propagate) throws IOException {
/* 117 */     int avail = this.baseNCodec.available(this.context);
/* 118 */     if (avail > 0) {
/* 119 */       byte[] buf = new byte[avail];
/* 120 */       int c = this.baseNCodec.readResults(buf, 0, avail, this.context);
/* 121 */       if (c > 0) {
/* 122 */         this.out.write(buf, 0, c);
/*     */       }
/*     */     } 
/* 125 */     if (propagate) {
/* 126 */       this.out.flush();
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
/*     */   public void flush() throws IOException {
/* 138 */     flush(true);
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
/*     */   public void close() throws IOException {
/* 155 */     eof();
/* 156 */     flush();
/* 157 */     this.out.close();
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
/*     */   public void eof() throws IOException {
/* 169 */     if (this.doEncode) {
/* 170 */       this.baseNCodec.encode(this.singleByte, 0, -1, this.context);
/*     */     } else {
/* 172 */       this.baseNCodec.decode(this.singleByte, 0, -1, this.context);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\binary\BaseNCodecOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */