/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal.;
/*     */ import com.google.gson.internal.LazilyParsedNumber;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
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
/*     */ public final class JsonPrimitive
/*     */   extends JsonElement
/*     */ {
/*  35 */   private static final Class<?>[] PRIMITIVE_TYPES = new Class[] { int.class, long.class, short.class, float.class, double.class, byte.class, boolean.class, char.class, Integer.class, Long.class, Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPrimitive(Boolean bool) {
/*  47 */     setValue(bool);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPrimitive(Number number) {
/*  56 */     setValue(number);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPrimitive(String string) {
/*  65 */     setValue(string);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPrimitive(Character c) {
/*  75 */     setValue(c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   JsonPrimitive(Object primitive) {
/*  85 */     setValue(primitive);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPrimitive deepCopy() {
/*  94 */     return this;
/*     */   }
/*     */   
/*     */   void setValue(Object primitive) {
/*  98 */     if (primitive instanceof Character) {
/*     */ 
/*     */       
/* 101 */       char c = ((Character)primitive).charValue();
/* 102 */       this.value = String.valueOf(c);
/*     */     } else {
/* 104 */       .Gson.Preconditions.checkArgument((primitive instanceof Number || 
/* 105 */           isPrimitiveOrString(primitive)));
/* 106 */       this.value = primitive;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBoolean() {
/* 116 */     return this.value instanceof Boolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Boolean getAsBooleanWrapper() {
/* 126 */     return (Boolean)this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getAsBoolean() {
/* 136 */     if (isBoolean()) {
/* 137 */       return getAsBooleanWrapper().booleanValue();
/*     */     }
/*     */     
/* 140 */     return Boolean.parseBoolean(getAsString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isNumber() {
/* 150 */     return this.value instanceof Number;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Number getAsNumber() {
/* 161 */     return (this.value instanceof String) ? (Number)new LazilyParsedNumber((String)this.value) : (Number)this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isString() {
/* 170 */     return this.value instanceof String;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAsString() {
/* 180 */     if (isNumber())
/* 181 */       return getAsNumber().toString(); 
/* 182 */     if (isBoolean()) {
/* 183 */       return getAsBooleanWrapper().toString();
/*     */     }
/* 185 */     return (String)this.value;
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
/*     */   public double getAsDouble() {
/* 197 */     return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigDecimal getAsBigDecimal() {
/* 208 */     return (this.value instanceof BigDecimal) ? (BigDecimal)this.value : new BigDecimal(this.value.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigInteger getAsBigInteger() {
/* 219 */     return (this.value instanceof BigInteger) ? (BigInteger)this.value : new BigInteger(this.value
/* 220 */         .toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getAsFloat() {
/* 231 */     return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getAsLong() {
/* 242 */     return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public short getAsShort() {
/* 253 */     return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAsInt() {
/* 264 */     return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getAsByte() {
/* 269 */     return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
/*     */   }
/*     */ 
/*     */   
/*     */   public char getAsCharacter() {
/* 274 */     return getAsString().charAt(0);
/*     */   }
/*     */   
/*     */   private static boolean isPrimitiveOrString(Object target) {
/* 278 */     if (target instanceof String) {
/* 279 */       return true;
/*     */     }
/*     */     
/* 282 */     Class<?> classOfPrimitive = target.getClass();
/* 283 */     for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
/* 284 */       if (standardPrimitive.isAssignableFrom(classOfPrimitive)) {
/* 285 */         return true;
/*     */       }
/*     */     } 
/* 288 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 293 */     if (this.value == null) {
/* 294 */       return 31;
/*     */     }
/*     */     
/* 297 */     if (isIntegral(this)) {
/* 298 */       long value = getAsNumber().longValue();
/* 299 */       return (int)(value ^ value >>> 32L);
/*     */     } 
/* 301 */     if (this.value instanceof Number) {
/* 302 */       long value = Double.doubleToLongBits(getAsNumber().doubleValue());
/* 303 */       return (int)(value ^ value >>> 32L);
/*     */     } 
/* 305 */     return this.value.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 310 */     if (this == obj) {
/* 311 */       return true;
/*     */     }
/* 313 */     if (obj == null || getClass() != obj.getClass()) {
/* 314 */       return false;
/*     */     }
/* 316 */     JsonPrimitive other = (JsonPrimitive)obj;
/* 317 */     if (this.value == null) {
/* 318 */       return (other.value == null);
/*     */     }
/* 320 */     if (isIntegral(this) && isIntegral(other)) {
/* 321 */       return (getAsNumber().longValue() == other.getAsNumber().longValue());
/*     */     }
/* 323 */     if (this.value instanceof Number && other.value instanceof Number) {
/* 324 */       double a = getAsNumber().doubleValue();
/*     */ 
/*     */       
/* 327 */       double b = other.getAsNumber().doubleValue();
/* 328 */       return (a == b || (Double.isNaN(a) && Double.isNaN(b)));
/*     */     } 
/* 330 */     return this.value.equals(other.value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isIntegral(JsonPrimitive primitive) {
/* 338 */     if (primitive.value instanceof Number) {
/* 339 */       Number number = (Number)primitive.value;
/* 340 */       return (number instanceof BigInteger || number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte);
/*     */     } 
/*     */     
/* 343 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\JsonPrimitive.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */