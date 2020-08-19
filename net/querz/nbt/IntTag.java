/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class IntTag
/*    */   extends NumberTag<Integer> implements Comparable<IntTag> {
/*    */   public static final int ZERO_VALUE = 0;
/*    */   
/*    */   public IntTag() {
/* 12 */     super(Integer.valueOf(0));
/*    */   }
/*    */   
/*    */   public IntTag(int value) {
/* 16 */     super(Integer.valueOf(value));
/*    */   }
/*    */   
/*    */   public void setValue(int value) {
/* 20 */     setValue((T)Integer.valueOf(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 25 */     dos.writeInt(getValue().intValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 30 */     setValue(dis.readInt());
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 35 */     return getValue() + "";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 40 */     return (super.equals(other) && asInt() == ((IntTag)other).asInt());
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(IntTag other) {
/* 45 */     return getValue().compareTo(other.getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public IntTag clone() {
/* 50 */     return new IntTag(getValue().intValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\IntTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */