/*     */ package pro.gravit.repackage.io.netty.handler.codec;
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
/*     */ public final class UnsupportedValueConverter<V>
/*     */   implements ValueConverter<V>
/*     */ {
/*  23 */   private static final UnsupportedValueConverter INSTANCE = new UnsupportedValueConverter();
/*     */ 
/*     */ 
/*     */   
/*     */   public static <V> UnsupportedValueConverter<V> instance() {
/*  28 */     return INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   public V convertObject(Object value) {
/*  33 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public V convertBoolean(boolean value) {
/*  38 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean convertToBoolean(V value) {
/*  43 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public V convertByte(byte value) {
/*  48 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte convertToByte(V value) {
/*  53 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public V convertChar(char value) {
/*  58 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public char convertToChar(V value) {
/*  63 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public V convertShort(short value) {
/*  68 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public short convertToShort(V value) {
/*  73 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public V convertInt(int value) {
/*  78 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int convertToInt(V value) {
/*  83 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public V convertLong(long value) {
/*  88 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public long convertToLong(V value) {
/*  93 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public V convertTimeMillis(long value) {
/*  98 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public long convertToTimeMillis(V value) {
/* 103 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public V convertFloat(float value) {
/* 108 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public float convertToFloat(V value) {
/* 113 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public V convertDouble(double value) {
/* 118 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public double convertToDouble(V value) {
/* 123 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\UnsupportedValueConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */