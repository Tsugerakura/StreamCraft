/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class LongTag
/*    */   extends NumberTag<Long> implements Comparable<LongTag> {
/*    */   public static final long ZERO_VALUE = 0L;
/*    */   
/*    */   public LongTag() {
/* 12 */     super(Long.valueOf(0L));
/*    */   }
/*    */   
/*    */   public LongTag(long value) {
/* 16 */     super(Long.valueOf(value));
/*    */   }
/*    */   
/*    */   public void setValue(long value) {
/* 20 */     setValue((T)Long.valueOf(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 25 */     dos.writeLong(getValue().longValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 30 */     setValue(dis.readLong());
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 35 */     return getValue() + "l";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 40 */     return (super.equals(other) && asLong() == ((LongTag)other).asLong());
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(LongTag other) {
/* 45 */     return getValue().compareTo(other.getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public LongTag clone() {
/* 50 */     return new LongTag(getValue().longValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\LongTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */