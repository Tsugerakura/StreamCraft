/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.io.DataInput;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ByteBufInputStream
/*     */   extends InputStream
/*     */   implements DataInput
/*     */ {
/*     */   private final ByteBuf buffer;
/*     */   private final int startIndex;
/*     */   private final int endIndex;
/*     */   private boolean closed;
/*     */   private final boolean releaseOnClose;
/*     */   private StringBuilder lineBuf;
/*     */   
/*     */   public ByteBufInputStream(ByteBuf buffer) {
/*  64 */     this(buffer, buffer.readableBytes());
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
/*     */   public ByteBufInputStream(ByteBuf buffer, int length) {
/*  78 */     this(buffer, length, false);
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
/*     */   public ByteBufInputStream(ByteBuf buffer, boolean releaseOnClose) {
/*  90 */     this(buffer, buffer.readableBytes(), releaseOnClose);
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
/*     */   public ByteBufInputStream(ByteBuf buffer, int length, boolean releaseOnClose) {
/* 106 */     ObjectUtil.checkNotNull(buffer, "buffer");
/* 107 */     if (length < 0) {
/* 108 */       if (releaseOnClose) {
/* 109 */         buffer.release();
/*     */       }
/* 111 */       throw new IllegalArgumentException("length: " + length);
/*     */     } 
/* 113 */     if (length > buffer.readableBytes()) {
/* 114 */       if (releaseOnClose) {
/* 115 */         buffer.release();
/*     */       }
/* 117 */       throw new IndexOutOfBoundsException("Too many bytes to be read - Needs " + length + ", maximum is " + buffer
/* 118 */           .readableBytes());
/*     */     } 
/*     */     
/* 121 */     this.releaseOnClose = releaseOnClose;
/* 122 */     this.buffer = buffer;
/* 123 */     this.startIndex = buffer.readerIndex();
/* 124 */     this.endIndex = this.startIndex + length;
/* 125 */     buffer.markReaderIndex();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int readBytes() {
/* 132 */     return this.buffer.readerIndex() - this.startIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 138 */       super.close();
/*     */     } finally {
/*     */       
/* 141 */       if (this.releaseOnClose && !this.closed) {
/* 142 */         this.closed = true;
/* 143 */         this.buffer.release();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int available() throws IOException {
/* 150 */     return this.endIndex - this.buffer.readerIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public void mark(int readlimit) {
/* 155 */     this.buffer.markReaderIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean markSupported() {
/* 160 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read() throws IOException {
/* 165 */     int available = available();
/* 166 */     if (available == 0) {
/* 167 */       return -1;
/*     */     }
/* 169 */     return this.buffer.readByte() & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/* 174 */     int available = available();
/* 175 */     if (available == 0) {
/* 176 */       return -1;
/*     */     }
/*     */     
/* 179 */     len = Math.min(available, len);
/* 180 */     this.buffer.readBytes(b, off, len);
/* 181 */     return len;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() throws IOException {
/* 186 */     this.buffer.resetReaderIndex();
/*     */   }
/*     */ 
/*     */   
/*     */   public long skip(long n) throws IOException {
/* 191 */     if (n > 2147483647L) {
/* 192 */       return skipBytes(2147483647);
/*     */     }
/* 194 */     return skipBytes((int)n);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean readBoolean() throws IOException {
/* 200 */     checkAvailable(1);
/* 201 */     return (read() != 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte readByte() throws IOException {
/* 206 */     int available = available();
/* 207 */     if (available == 0) {
/* 208 */       throw new EOFException();
/*     */     }
/* 210 */     return this.buffer.readByte();
/*     */   }
/*     */ 
/*     */   
/*     */   public char readChar() throws IOException {
/* 215 */     return (char)readShort();
/*     */   }
/*     */ 
/*     */   
/*     */   public double readDouble() throws IOException {
/* 220 */     return Double.longBitsToDouble(readLong());
/*     */   }
/*     */ 
/*     */   
/*     */   public float readFloat() throws IOException {
/* 225 */     return Float.intBitsToFloat(readInt());
/*     */   }
/*     */ 
/*     */   
/*     */   public void readFully(byte[] b) throws IOException {
/* 230 */     readFully(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public void readFully(byte[] b, int off, int len) throws IOException {
/* 235 */     checkAvailable(len);
/* 236 */     this.buffer.readBytes(b, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public int readInt() throws IOException {
/* 241 */     checkAvailable(4);
/* 242 */     return this.buffer.readInt();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String readLine() throws IOException {
/* 249 */     int available = available();
/* 250 */     if (available == 0) {
/* 251 */       return null;
/*     */     }
/*     */     
/* 254 */     if (this.lineBuf != null) {
/* 255 */       this.lineBuf.setLength(0);
/*     */     }
/*     */     
/*     */     do {
/* 259 */       int c = this.buffer.readUnsignedByte();
/* 260 */       available--;
/* 261 */       switch (c) {
/*     */         case 10:
/*     */           break;
/*     */         
/*     */         case 13:
/* 266 */           if (available > 0 && (char)this.buffer.getUnsignedByte(this.buffer.readerIndex()) == '\n') {
/* 267 */             this.buffer.skipBytes(1);
/* 268 */             available--;
/*     */           } 
/*     */           break;
/*     */       } 
/*     */       
/* 273 */       if (this.lineBuf == null) {
/* 274 */         this.lineBuf = new StringBuilder();
/*     */       }
/* 276 */       this.lineBuf.append((char)c);
/*     */     }
/* 278 */     while (available > 0);
/*     */     
/* 280 */     return (this.lineBuf != null && this.lineBuf.length() > 0) ? this.lineBuf.toString() : "";
/*     */   }
/*     */ 
/*     */   
/*     */   public long readLong() throws IOException {
/* 285 */     checkAvailable(8);
/* 286 */     return this.buffer.readLong();
/*     */   }
/*     */ 
/*     */   
/*     */   public short readShort() throws IOException {
/* 291 */     checkAvailable(2);
/* 292 */     return this.buffer.readShort();
/*     */   }
/*     */ 
/*     */   
/*     */   public String readUTF() throws IOException {
/* 297 */     return DataInputStream.readUTF(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int readUnsignedByte() throws IOException {
/* 302 */     return readByte() & 0xFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int readUnsignedShort() throws IOException {
/* 307 */     return readShort() & 0xFFFF;
/*     */   }
/*     */ 
/*     */   
/*     */   public int skipBytes(int n) throws IOException {
/* 312 */     int nBytes = Math.min(available(), n);
/* 313 */     this.buffer.skipBytes(nBytes);
/* 314 */     return nBytes;
/*     */   }
/*     */   
/*     */   private void checkAvailable(int fieldSize) throws IOException {
/* 318 */     if (fieldSize < 0) {
/* 319 */       throw new IndexOutOfBoundsException("fieldSize cannot be a negative number");
/*     */     }
/* 321 */     if (fieldSize > available())
/* 322 */       throw new EOFException("fieldSize is too long! Length is " + fieldSize + ", but maximum is " + 
/* 323 */           available()); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\ByteBufInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */