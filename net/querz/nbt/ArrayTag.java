/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.lang.reflect.Array;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ArrayTag<T>
/*    */   extends Tag<T>
/*    */ {
/*    */   public ArrayTag(T value) {
/* 13 */     super(value);
/* 14 */     if (!value.getClass().isArray()) {
/* 15 */       throw new UnsupportedOperationException("type of array tag must be an array");
/*    */     }
/*    */   }
/*    */   
/*    */   public int length() {
/* 20 */     return Array.getLength(getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public T getValue() {
/* 25 */     return super.getValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValue(T value) {
/* 30 */     super.setValue(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToString(int maxDepth) {
/* 35 */     return arrayToString("", "");
/*    */   }
/*    */   
/*    */   protected String arrayToString(String prefix, String suffix) {
/* 39 */     StringBuilder sb = (new StringBuilder("[")).append(prefix).append("".equals(prefix) ? "" : ";");
/* 40 */     for (int i = 0; i < length(); i++) {
/* 41 */       sb.append((i == 0) ? "" : ",").append(Array.get(getValue(), i)).append(suffix);
/*    */     }
/* 43 */     sb.append("]");
/* 44 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\ArrayTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */