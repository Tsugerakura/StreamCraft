/*     */ package pro.gravit.launcher.serialize;
/*     */ 
/*     */ import java.io.Flushable;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Objects;
/*     */ import java.util.UUID;
/*     */ import pro.gravit.launcher.LauncherAPI;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ 
/*     */ public final class HOutput
/*     */   implements AutoCloseable, Flushable {
/*     */   @LauncherAPI
/*     */   public final OutputStream stream;
/*     */   
/*     */   @LauncherAPI
/*     */   public HOutput(OutputStream stream) {
/*  19 */     this.stream = Objects.<OutputStream>requireNonNull(stream, "stream");
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  24 */     this.stream.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/*  29 */     this.stream.flush();
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeASCII(String s, int maxBytes) throws IOException {
/*  34 */     writeByteArray(IOHelper.encodeASCII(s), maxBytes);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeBigInteger(BigInteger bi, int max) throws IOException {
/*  39 */     writeByteArray(bi.toByteArray(), max);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeBoolean(boolean b) throws IOException {
/*  44 */     writeUnsignedByte(b ? 1 : 0);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeByteArray(byte[] bytes, int max) throws IOException {
/*  49 */     writeLength(bytes.length, max);
/*  50 */     this.stream.write(bytes);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeInt(int i) throws IOException {
/*  55 */     writeUnsignedByte(i >>> 24 & 0xFF);
/*  56 */     writeUnsignedByte(i >>> 16 & 0xFF);
/*  57 */     writeUnsignedByte(i >>> 8 & 0xFF);
/*  58 */     writeUnsignedByte(i & 0xFF);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeLength(int length, int max) throws IOException {
/*  63 */     IOHelper.verifyLength(length, max);
/*  64 */     if (max >= 0)
/*  65 */       writeVarInt(length); 
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeLong(long l) throws IOException {
/*  70 */     writeInt((int)(l >> 32L));
/*  71 */     writeInt((int)l);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeShort(short s) throws IOException {
/*  76 */     writeUnsignedByte(s >>> 8 & 0xFF);
/*  77 */     writeUnsignedByte(s & 0xFF);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeString(String s, int maxBytes) throws IOException {
/*  82 */     writeByteArray(IOHelper.encode(s), maxBytes);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeUnsignedByte(int b) throws IOException {
/*  87 */     this.stream.write(b);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeUUID(UUID uuid) throws IOException {
/*  92 */     writeLong(uuid.getMostSignificantBits());
/*  93 */     writeLong(uuid.getLeastSignificantBits());
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeVarInt(int i) throws IOException {
/*  98 */     while ((i & 0xFFFFFFFFFFFFFF80L) != 0L) {
/*  99 */       writeUnsignedByte(i & 0x7F | 0x80);
/* 100 */       i >>>= 7;
/*     */     } 
/* 102 */     writeUnsignedByte(i);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public void writeVarLong(long l) throws IOException {
/* 107 */     while ((l & 0xFFFFFFFFFFFFFF80L) != 0L) {
/* 108 */       writeUnsignedByte((int)l & 0x7F | 0x80);
/* 109 */       l >>>= 7L;
/*     */     } 
/* 111 */     writeUnsignedByte((int)l);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\serialize\HOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */