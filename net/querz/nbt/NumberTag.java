/*    */ package net.querz.nbt;
/*    */ 
/*    */ public abstract class NumberTag<T extends Number & Comparable<T>>
/*    */   extends Tag<T> {
/*    */   public NumberTag(T value) {
/*  6 */     super(value);
/*    */   }
/*    */   
/*    */   public byte asByte() {
/* 10 */     return ((Number)getValue()).byteValue();
/*    */   }
/*    */   
/*    */   public short asShort() {
/* 14 */     return ((Number)getValue()).shortValue();
/*    */   }
/*    */   
/*    */   public int asInt() {
/* 18 */     return ((Number)getValue()).intValue();
/*    */   }
/*    */   
/*    */   public long asLong() {
/* 22 */     return ((Number)getValue()).longValue();
/*    */   }
/*    */   
/*    */   public float asFloat() {
/* 26 */     return ((Number)getValue()).floatValue();
/*    */   }
/*    */   
/*    */   public double asDouble() {
/* 30 */     return ((Number)getValue()).doubleValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToString(int maxDepth) {
/* 35 */     return ((Number)getValue()).toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\NumberTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */