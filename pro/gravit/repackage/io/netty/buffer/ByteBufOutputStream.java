/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.io.DataOutput;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
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
/*     */ public class ByteBufOutputStream
/*     */   extends OutputStream
/*     */   implements DataOutput
/*     */ {
/*     */   private final ByteBuf buffer;
/*     */   private final int startIndex;
/*  43 */   private final DataOutputStream utf8out = new DataOutputStream(this);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBufOutputStream(ByteBuf buffer) {
/*  49 */     this.buffer = (ByteBuf)ObjectUtil.checkNotNull(buffer, "buffer");
/*  50 */     this.startIndex = buffer.writerIndex();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int writtenBytes() {
/*  57 */     return this.buffer.writerIndex() - this.startIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/*  62 */     if (len == 0) {
/*     */       return;
/*     */     }
/*     */     
/*  66 */     this.buffer.writeBytes(b, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/*  71 */     this.buffer.writeBytes(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/*  76 */     this.buffer.writeByte(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBoolean(boolean v) throws IOException {
/*  81 */     this.buffer.writeBoolean(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeByte(int v) throws IOException {
/*  86 */     this.buffer.writeByte(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeBytes(String s) throws IOException {
/*  91 */     this.buffer.writeCharSequence(s, CharsetUtil.US_ASCII);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeChar(int v) throws IOException {
/*  96 */     this.buffer.writeChar(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeChars(String s) throws IOException {
/* 101 */     int len = s.length();
/* 102 */     for (int i = 0; i < len; i++) {
/* 103 */       this.buffer.writeChar(s.charAt(i));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeDouble(double v) throws IOException {
/* 109 */     this.buffer.writeDouble(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeFloat(float v) throws IOException {
/* 114 */     this.buffer.writeFloat(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeInt(int v) throws IOException {
/* 119 */     this.buffer.writeInt(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeLong(long v) throws IOException {
/* 124 */     this.buffer.writeLong(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeShort(int v) throws IOException {
/* 129 */     this.buffer.writeShort((short)v);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeUTF(String s) throws IOException {
/* 134 */     this.utf8out.writeUTF(s);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuf buffer() {
/* 141 */     return this.buffer;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\ByteBufOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */