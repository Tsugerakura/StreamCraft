/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class DoubleTag
/*    */   extends NumberTag<Double> implements Comparable<DoubleTag> {
/*    */   public static final double ZERO_VALUE = 0.0D;
/*    */   
/*    */   public DoubleTag() {
/* 12 */     super(Double.valueOf(0.0D));
/*    */   }
/*    */   
/*    */   public DoubleTag(double value) {
/* 16 */     super(Double.valueOf(value));
/*    */   }
/*    */   
/*    */   public void setValue(double value) {
/* 20 */     setValue((T)Double.valueOf(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 25 */     dos.writeDouble(getValue().doubleValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 30 */     setValue(dis.readDouble());
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 35 */     return getValue() + "d";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 40 */     return (super.equals(other) && getValue().equals(((DoubleTag)other).getValue()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(DoubleTag other) {
/* 45 */     return getValue().compareTo(other.getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public DoubleTag clone() {
/* 50 */     return new DoubleTag(getValue().doubleValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\DoubleTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */