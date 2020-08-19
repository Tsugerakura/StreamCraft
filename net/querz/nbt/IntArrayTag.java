/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class IntArrayTag
/*    */   extends ArrayTag<int[]> implements Comparable<IntArrayTag> {
/* 10 */   public static final int[] ZERO_VALUE = new int[0];
/*    */   
/*    */   public IntArrayTag() {
/* 13 */     super(ZERO_VALUE);
/*    */   }
/*    */   
/*    */   public IntArrayTag(int[] value) {
/* 17 */     super(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 22 */     dos.writeInt(length());
/* 23 */     for (int i : getValue()) {
/* 24 */       dos.writeInt(i);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 30 */     int length = dis.readInt();
/* 31 */     setValue(new int[length]);
/* 32 */     for (int i = 0; i < length; i++) {
/* 33 */       getValue()[i] = dis.readInt();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 39 */     return arrayToString("I", "");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 44 */     return (super.equals(other) && Arrays.equals(getValue(), ((IntArrayTag)other).getValue()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 49 */     return Arrays.hashCode(getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(IntArrayTag other) {
/* 54 */     return Integer.compare(length(), other.length());
/*    */   }
/*    */ 
/*    */   
/*    */   public IntArrayTag clone() {
/* 59 */     return new IntArrayTag(Arrays.copyOf(getValue(), length()));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\IntArrayTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */