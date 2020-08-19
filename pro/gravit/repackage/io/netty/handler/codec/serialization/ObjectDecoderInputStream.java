/*     */ package pro.gravit.repackage.io.netty.handler.codec.serialization;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.StreamCorruptedException;
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
/*     */ public class ObjectDecoderInputStream
/*     */   extends InputStream
/*     */   implements ObjectInput
/*     */ {
/*     */   private final DataInputStream in;
/*     */   private final int maxObjectSize;
/*     */   private final ClassResolver classResolver;
/*     */   
/*     */   public ObjectDecoderInputStream(InputStream in) {
/*  46 */     this(in, (ClassLoader)null);
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
/*     */   public ObjectDecoderInputStream(InputStream in, ClassLoader classLoader) {
/*  60 */     this(in, classLoader, 1048576);
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
/*     */   public ObjectDecoderInputStream(InputStream in, int maxObjectSize) {
/*  75 */     this(in, null, maxObjectSize);
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
/*     */   public ObjectDecoderInputStream(InputStream in, ClassLoader classLoader, int maxObjectSize) {
/*  93 */     ObjectUtil.checkNotNull(in, "in");
/*  94 */     ObjectUtil.checkPositive(maxObjectSize, "maxObjectSize");
/*     */     
/*  96 */     if (in instanceof DataInputStream) {
/*  97 */       this.in = (DataInputStream)in;
/*     */     } else {
/*  99 */       this.in = new DataInputStream(in);
/*     */     } 
/* 101 */     this.classResolver = ClassResolvers.weakCachingResolver(classLoader);
/* 102 */     this.maxObjectSize = maxObjectSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object readObject() throws ClassNotFoundException, IOException {
/* 107 */     int dataLen = readInt();
/* 108 */     if (dataLen <= 0) {
/* 109 */       throw new StreamCorruptedException("invalid data length: " + dataLen);
/*     */     }
/* 111 */     if (dataLen > this.maxObjectSize) {
/* 112 */       throw new StreamCorruptedException("data length too big: " + dataLen + " (max: " + this.maxObjectSize + ')');
/*     */     }
/*     */ 
/*     */     
/* 116 */     return (new CompactObjectInputStream(this.in, this.classResolver)).readObject();
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 121 */     return this.in.available();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 126 */     this.in.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void mark(int readlimit) {
/* 131 */     this.in.mark(readlimit);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/* 136 */     return this.in.markSupported();
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 141 */     return this.in.read();
/*     */   }
/*     */ 
/*     */   
/*     */   public final int read(byte[] b, int off, int len) throws IOException {
/* 146 */     return this.in.read(b, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int read(byte[] b) throws IOException {
/* 151 */     return this.in.read(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean readBoolean() throws IOException {
/* 156 */     return this.in.readBoolean();
/*     */   }
/*     */ 
/*     */   
/*     */   public final byte readByte() throws IOException {
/* 161 */     return this.in.readByte();
/*     */   }
/*     */ 
/*     */   
/*     */   public final char readChar() throws IOException {
/* 166 */     return this.in.readChar();
/*     */   }
/*     */ 
/*     */   
/*     */   public final double readDouble() throws IOException {
/* 171 */     return this.in.readDouble();
/*     */   }
/*     */ 
/*     */   
/*     */   public final float readFloat() throws IOException {
/* 176 */     return this.in.readFloat();
/*     */   }
/*     */ 
/*     */   
/*     */   public final void readFully(byte[] b, int off, int len) throws IOException {
/* 181 */     this.in.readFully(b, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public final void readFully(byte[] b) throws IOException {
/* 186 */     this.in.readFully(b);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int readInt() throws IOException {
/* 191 */     return this.in.readInt();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public final String readLine() throws IOException {
/* 200 */     return this.in.readLine();
/*     */   }
/*     */ 
/*     */   
/*     */   public final long readLong() throws IOException {
/* 205 */     return this.in.readLong();
/*     */   }
/*     */ 
/*     */   
/*     */   public final short readShort() throws IOException {
/* 210 */     return this.in.readShort();
/*     */   }
/*     */ 
/*     */   
/*     */   public final int readUnsignedByte() throws IOException {
/* 215 */     return this.in.readUnsignedByte();
/*     */   }
/*     */ 
/*     */   
/*     */   public final int readUnsignedShort() throws IOException {
/* 220 */     return this.in.readUnsignedShort();
/*     */   }
/*     */ 
/*     */   
/*     */   public final String readUTF() throws IOException {
/* 225 */     return this.in.readUTF();
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() throws IOException {
/* 230 */     this.in.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 235 */     return this.in.skip(n);
/*     */   }
/*     */ 
/*     */   
/*     */   public final int skipBytes(int n) throws IOException {
/* 240 */     return this.in.skipBytes(n);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\serialization\ObjectDecoderInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */