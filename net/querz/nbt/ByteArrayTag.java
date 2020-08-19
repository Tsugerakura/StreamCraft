/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class ByteArrayTag
/*    */   extends ArrayTag<byte[]> implements Comparable<ByteArrayTag> {
/* 10 */   public static final byte[] ZERO_VALUE = new byte[0];
/*    */   
/*    */   public ByteArrayTag() {
/* 13 */     super(ZERO_VALUE);
/*    */   }
/*    */   
/*    */   public ByteArrayTag(byte[] value) {
/* 17 */     super(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 22 */     dos.writeInt(length());
/* 23 */     dos.write(getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 28 */     int length = dis.readInt();
/* 29 */     setValue(new byte[length]);
/* 30 */     dis.readFully(getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 35 */     return arrayToString("B", "b");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 40 */     return (super.equals(other) && Arrays.equals(getValue(), ((ByteArrayTag)other).getValue()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 45 */     return Arrays.hashCode(getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(ByteArrayTag other) {
/* 50 */     return Integer.compare(length(), other.length());
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteArrayTag clone() {
/* 55 */     return new ByteArrayTag(Arrays.copyOf(getValue(), length()));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\ByteArrayTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */