/*    */ package net.querz.nbt.mca;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum CompressionType
/*    */ {
/* 14 */   GZIP(1, java.util.zip.GZIPOutputStream::new, java.util.zip.GZIPInputStream::new),
/* 15 */   ZLIB(2, java.util.zip.DeflaterOutputStream::new, java.util.zip.InflaterInputStream::new), NONE(2, java.util.zip.DeflaterOutputStream::new, java.util.zip.InflaterInputStream::new);
/*    */   
/*    */   static {
/*    */     NONE = new CompressionType("NONE", 0, 0, t -> t, t -> t);
/*    */   }
/*    */   
/*    */   private byte id;
/*    */   
/*    */   CompressionType(int id, ExceptionFunction<OutputStream, ? extends OutputStream, IOException> compressor, ExceptionFunction<InputStream, ? extends InputStream, IOException> decompressor) {
/* 24 */     this.id = (byte)id;
/* 25 */     this.compressor = compressor;
/* 26 */     this.decompressor = decompressor;
/*    */   }
/*    */   private ExceptionFunction<OutputStream, ? extends OutputStream, IOException> compressor; private ExceptionFunction<InputStream, ? extends InputStream, IOException> decompressor;
/*    */   public byte getID() {
/* 30 */     return this.id;
/*    */   }
/*    */   
/*    */   public OutputStream compress(OutputStream out) throws IOException {
/* 34 */     return this.compressor.accept(out);
/*    */   }
/*    */   
/*    */   public InputStream decompress(InputStream in) throws IOException {
/* 38 */     return this.decompressor.accept(in);
/*    */   }
/*    */   
/*    */   public static CompressionType getFromID(byte id) {
/* 42 */     for (CompressionType c : values()) {
/* 43 */       if (c.id == id) {
/* 44 */         return c;
/*    */       }
/*    */     } 
/* 47 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\mca\CompressionType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */