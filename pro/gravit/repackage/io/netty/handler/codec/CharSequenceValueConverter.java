/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ import java.util.Date;
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ public class CharSequenceValueConverter
/*     */   implements ValueConverter<CharSequence>
/*     */ {
/*  27 */   public static final CharSequenceValueConverter INSTANCE = new CharSequenceValueConverter();
/*  28 */   private static final AsciiString TRUE_ASCII = new AsciiString("true");
/*     */ 
/*     */   
/*     */   public CharSequence convertObject(Object value) {
/*  32 */     if (value instanceof CharSequence) {
/*  33 */       return (CharSequence)value;
/*     */     }
/*  35 */     return value.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence convertInt(int value) {
/*  40 */     return String.valueOf(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence convertLong(long value) {
/*  45 */     return String.valueOf(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence convertDouble(double value) {
/*  50 */     return String.valueOf(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence convertChar(char value) {
/*  55 */     return String.valueOf(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence convertBoolean(boolean value) {
/*  60 */     return String.valueOf(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence convertFloat(float value) {
/*  65 */     return String.valueOf(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean convertToBoolean(CharSequence value) {
/*  70 */     return AsciiString.contentEqualsIgnoreCase(value, (CharSequence)TRUE_ASCII);
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence convertByte(byte value) {
/*  75 */     return String.valueOf(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte convertToByte(CharSequence value) {
/*  80 */     if (value instanceof AsciiString && value.length() == 1) {
/*  81 */       return ((AsciiString)value).byteAt(0);
/*     */     }
/*  83 */     return Byte.parseByte(value.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public char convertToChar(CharSequence value) {
/*  88 */     return value.charAt(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence convertShort(short value) {
/*  93 */     return String.valueOf(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public short convertToShort(CharSequence value) {
/*  98 */     if (value instanceof AsciiString) {
/*  99 */       return ((AsciiString)value).parseShort();
/*     */     }
/* 101 */     return Short.parseShort(value.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public int convertToInt(CharSequence value) {
/* 106 */     if (value instanceof AsciiString) {
/* 107 */       return ((AsciiString)value).parseInt();
/*     */     }
/* 109 */     return Integer.parseInt(value.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public long convertToLong(CharSequence value) {
/* 114 */     if (value instanceof AsciiString) {
/* 115 */       return ((AsciiString)value).parseLong();
/*     */     }
/* 117 */     return Long.parseLong(value.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence convertTimeMillis(long value) {
/* 122 */     return DateFormatter.format(new Date(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public long convertToTimeMillis(CharSequence value) {
/* 127 */     Date date = DateFormatter.parseHttpDate(value);
/* 128 */     if (date == null) {
/* 129 */       PlatformDependent.throwException(new ParseException("header can't be parsed into a Date: " + value, 0));
/* 130 */       return 0L;
/*     */     } 
/* 132 */     return date.getTime();
/*     */   }
/*     */ 
/*     */   
/*     */   public float convertToFloat(CharSequence value) {
/* 137 */     if (value instanceof AsciiString) {
/* 138 */       return ((AsciiString)value).parseFloat();
/*     */     }
/* 140 */     return Float.parseFloat(value.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public double convertToDouble(CharSequence value) {
/* 145 */     if (value instanceof AsciiString) {
/* 146 */       return ((AsciiString)value).parseDouble();
/*     */     }
/* 148 */     return Double.parseDouble(value.toString());
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\CharSequenceValueConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */