/*     */ package pro.gravit.launcher.serialize;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Objects;
/*     */ import java.util.UUID;
/*     */ import pro.gravit.launcher.LauncherAPI;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ 
/*     */ public final class HInput
/*     */   implements AutoCloseable {
/*     */   @LauncherAPI
/*     */   public final InputStream stream;
/*     */   
/*     */   @LauncherAPI
/*     */   public HInput(byte[] bytes) {
/*  20 */     this.stream = new ByteArrayInputStream(bytes);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public HInput(InputStream stream) {
/*  25 */     this.stream = Objects.<InputStream>requireNonNull(stream, "stream");
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  30 */     this.stream.close();
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String readASCII(int maxBytes) throws IOException {
/*  35 */     return IOHelper.decodeASCII(readByteArray(maxBytes));
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public BigInteger readBigInteger(int maxBytes) throws IOException {
/*  40 */     return new BigInteger(readByteArray(maxBytes));
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public boolean readBoolean() throws IOException {
/*  45 */     int b = readUnsignedByte();
/*  46 */     switch (b) {
/*     */       case 0:
/*  48 */         return false;
/*     */       case 1:
/*  50 */         return true;
/*     */     } 
/*  52 */     throw new IOException("Invalid boolean state: " + b);
/*     */   }
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public byte[] readByteArray(int max) throws IOException {
/*  58 */     byte[] bytes = new byte[readLength(max)];
/*  59 */     IOHelper.read(this.stream, bytes);
/*  60 */     return bytes;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public int readInt() throws IOException {
/*  65 */     return (readUnsignedByte() << 24) + (readUnsignedByte() << 16) + (readUnsignedByte() << 8) + readUnsignedByte();
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public int readLength(int max) throws IOException {
/*  70 */     if (max < 0)
/*  71 */       return -max; 
/*  72 */     return IOHelper.verifyLength(readVarInt(), max);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public long readLong() throws IOException {
/*  77 */     return readInt() << 32L | readInt() & 0xFFFFFFFFL;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public short readShort() throws IOException {
/*  82 */     return (short)((readUnsignedByte() << 8) + readUnsignedByte());
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public String readString(int maxBytes) throws IOException {
/*  87 */     return IOHelper.decode(readByteArray(maxBytes));
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public int readUnsignedByte() throws IOException {
/*  92 */     int b = this.stream.read();
/*  93 */     if (b < 0)
/*  94 */       throw new EOFException("readUnsignedByte"); 
/*  95 */     return b;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public int readUnsignedShort() throws IOException {
/* 100 */     return Short.toUnsignedInt(readShort());
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public UUID readUUID() throws IOException {
/* 105 */     return new UUID(readLong(), readLong());
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public int readVarInt() throws IOException {
/* 110 */     int shift = 0;
/* 111 */     int result = 0;
/* 112 */     while (shift < 32) {
/* 113 */       int b = readUnsignedByte();
/* 114 */       result |= (b & 0x7F) << shift;
/* 115 */       if ((b & 0x80) == 0)
/* 116 */         return result; 
/* 117 */       shift += 7;
/*     */     } 
/* 119 */     throw new IOException("VarInt too big");
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public long readVarLong() throws IOException {
/* 124 */     int shift = 0;
/* 125 */     long result = 0L;
/* 126 */     while (shift < 64) {
/* 127 */       int b = readUnsignedByte();
/* 128 */       result |= (b & 0x7F) << shift;
/* 129 */       if ((b & 0x80) == 0)
/* 130 */         return result; 
/* 131 */       shift += 7;
/*     */     } 
/* 133 */     throw new IOException("VarLong too big");
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\serialize\HInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */