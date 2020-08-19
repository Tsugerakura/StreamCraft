/*     */ package pro.gravit.repackage.io.netty.handler.codec.serialization;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutput;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.OutputStream;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufOutputStream;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
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
/*     */ public class ObjectEncoderOutputStream
/*     */   extends OutputStream
/*     */   implements ObjectOutput
/*     */ {
/*     */   private final DataOutputStream out;
/*     */   private final int estimatedLength;
/*     */   
/*     */   public ObjectEncoderOutputStream(OutputStream out) {
/*  48 */     this(out, 512);
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
/*     */   public ObjectEncoderOutputStream(OutputStream out, int estimatedLength) {
/*  67 */     ObjectUtil.checkNotNull(out, "out");
/*  68 */     ObjectUtil.checkPositiveOrZero(estimatedLength, "estimatedLength");
/*     */     
/*  70 */     if (out instanceof DataOutputStream) {
/*  71 */       this.out = (DataOutputStream)out;
/*     */     } else {
/*  73 */       this.out = new DataOutputStream(out);
/*     */     } 
/*  75 */     this.estimatedLength = estimatedLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeObject(Object obj) throws IOException {
/*  80 */     ByteBuf buf = Unpooled.buffer(this.estimatedLength);
/*     */     try {
/*  82 */       ObjectOutputStream oout = new CompactObjectOutputStream((OutputStream)new ByteBufOutputStream(buf));
/*     */       try {
/*  84 */         oout.writeObject(obj);
/*  85 */         oout.flush();
/*     */       } finally {
/*  87 */         oout.close();
/*     */       } 
/*     */       
/*  90 */       int objectSize = buf.readableBytes();
/*  91 */       writeInt(objectSize);
/*  92 */       buf.getBytes(0, this, objectSize);
/*     */     } finally {
/*  94 */       buf.release();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int b) throws IOException {
/* 100 */     this.out.write(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 105 */     this.out.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 110 */     this.out.flush();
/*     */   }
/*     */   
/*     */   public final int size() {
/* 114 */     return this.out.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b, int off, int len) throws IOException {
/* 119 */     this.out.write(b, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(byte[] b) throws IOException {
/* 124 */     this.out.write(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeBoolean(boolean v) throws IOException {
/* 129 */     this.out.writeBoolean(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeByte(int v) throws IOException {
/* 134 */     this.out.writeByte(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeBytes(String s) throws IOException {
/* 139 */     this.out.writeBytes(s);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeChar(int v) throws IOException {
/* 144 */     this.out.writeChar(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeChars(String s) throws IOException {
/* 149 */     this.out.writeChars(s);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeDouble(double v) throws IOException {
/* 154 */     this.out.writeDouble(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeFloat(float v) throws IOException {
/* 159 */     this.out.writeFloat(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeInt(int v) throws IOException {
/* 164 */     this.out.writeInt(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeLong(long v) throws IOException {
/* 169 */     this.out.writeLong(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeShort(int v) throws IOException {
/* 174 */     this.out.writeShort(v);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void writeUTF(String str) throws IOException {
/* 179 */     this.out.writeUTF(str);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\serialization\ObjectEncoderOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */