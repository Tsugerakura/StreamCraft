/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class LongArrayTag
/*    */   extends ArrayTag<long[]> implements Comparable<LongArrayTag> {
/* 10 */   public static final long[] ZERO_VALUE = new long[0];
/*    */   
/*    */   public LongArrayTag() {
/* 13 */     super(ZERO_VALUE);
/*    */   }
/*    */   
/*    */   public LongArrayTag(long[] value) {
/* 17 */     super(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 22 */     dos.writeInt(length());
/* 23 */     for (long i : getValue()) {
/* 24 */       dos.writeLong(i);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 30 */     int length = dis.readInt();
/* 31 */     setValue(new long[length]);
/* 32 */     for (int i = 0; i < length; i++) {
/* 33 */       getValue()[i] = dis.readLong();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 39 */     return arrayToString("L", "l");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 44 */     return (super.equals(other) && Arrays.equals(getValue(), ((LongArrayTag)other).getValue()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 49 */     return Arrays.hashCode(getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(LongArrayTag other) {
/* 54 */     return Integer.compare(length(), other.length());
/*    */   }
/*    */ 
/*    */   
/*    */   public LongArrayTag clone() {
/* 59 */     return new LongArrayTag(Arrays.copyOf(getValue(), length()));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\LongArrayTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */