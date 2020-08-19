/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class FloatTag
/*    */   extends NumberTag<Float> implements Comparable<FloatTag> {
/*    */   public static final float ZERO_VALUE = 0.0F;
/*    */   
/*    */   public FloatTag() {
/* 12 */     super(Float.valueOf(0.0F));
/*    */   }
/*    */   
/*    */   public FloatTag(float value) {
/* 16 */     super(Float.valueOf(value));
/*    */   }
/*    */   
/*    */   public void setValue(float value) {
/* 20 */     setValue((T)Float.valueOf(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 25 */     dos.writeFloat(getValue().floatValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 30 */     setValue(dis.readFloat());
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 35 */     return getValue() + "f";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 40 */     return (super.equals(other) && getValue().equals(((FloatTag)other).getValue()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(FloatTag other) {
/* 45 */     return getValue().compareTo(other.getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public FloatTag clone() {
/* 50 */     return new FloatTag(getValue().floatValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\FloatTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */