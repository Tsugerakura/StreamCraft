/*     */ package net.querz.nbt;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PushbackInputStream;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import java.util.zip.GZIPOutputStream;
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
/*     */ public final class NBTUtil
/*     */ {
/*     */   public static void writeTag(Tag<?> tag, String file) throws IOException {
/*  32 */     writeTag(tag, "", new File(file), true);
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
/*     */   public static void writeTag(Tag<?> tag, File file) throws IOException {
/*  45 */     writeTag(tag, "", file, true);
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
/*     */   public static void writeTag(Tag<?> tag, String file, boolean compressed) throws IOException {
/*  59 */     writeTag(tag, "", new File(file), compressed);
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
/*     */   public static void writeTag(Tag<?> tag, File file, boolean compressed) throws IOException {
/*  73 */     writeTag(tag, "", file, compressed);
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
/*     */   public static void writeTag(Tag<?> tag, String name, String file) throws IOException {
/*  86 */     writeTag(tag, name, new File(file), true);
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
/*     */   public static void writeTag(Tag<?> tag, String name, File file) throws IOException {
/* 100 */     writeTag(tag, name, file, true);
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
/*     */   public static void writeTag(Tag<?> tag, String name, String file, boolean compressed) throws IOException {
/* 114 */     writeTag(tag, name, new File(file), compressed);
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
/*     */   public static void writeTag(Tag<?> tag, String name, File file, boolean compressed) throws IOException {
/* 130 */     try (DataOutputStream dos = new DataOutputStream(compressed ? new GZIPOutputStream(new FileOutputStream(file)) : new FileOutputStream(file))) {
/*     */ 
/*     */       
/* 133 */       tag.serialize(dos, name, 512);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Tag<?> readTag(String file) throws IOException {
/* 144 */     return readTag(new File(file));
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
/*     */   public static Tag<?> readTag(File file) throws IOException {
/* 157 */     try (DataInputStream dis = new DataInputStream(applyDecompression(new FileInputStream(file)))) {
/* 158 */       return Tag.deserialize(dis, 512);
/*     */     } 
/*     */   }
/*     */   
/*     */   static InputStream applyDecompression(InputStream is) throws IOException {
/* 163 */     PushbackInputStream pbis = new PushbackInputStream(is, 2);
/* 164 */     int sig = (pbis.read() & 0xFF) + (pbis.read() << 8);
/* 165 */     pbis.unread(sig >> 8);
/* 166 */     pbis.unread(sig & 0xFF);
/* 167 */     if (sig == 35615) {
/* 168 */       return new GZIPInputStream(pbis);
/*     */     }
/* 170 */     return pbis;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\NBTUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */