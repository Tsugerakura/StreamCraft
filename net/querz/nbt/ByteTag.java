/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class ByteTag
/*    */   extends NumberTag<Byte> implements Comparable<ByteTag> {
/*    */   public static final byte ZERO_VALUE = 0;
/*    */   
/*    */   public ByteTag() {
/* 12 */     super(Byte.valueOf((byte)0));
/*    */   }
/*    */   
/*    */   public ByteTag(byte value) {
/* 16 */     super(Byte.valueOf(value));
/*    */   }
/*    */   
/*    */   public ByteTag(boolean value) {
/* 20 */     super(Byte.valueOf((byte)(value ? 1 : 0)));
/*    */   }
/*    */   
/*    */   public boolean asBoolean() {
/* 24 */     return (getValue().byteValue() > 0);
/*    */   }
/*    */   
/*    */   public void setValue(byte value) {
/* 28 */     setValue((T)Byte.valueOf(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 33 */     dos.writeByte(getValue().byteValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 38 */     setValue(dis.readByte());
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 43 */     return getValue() + "b";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 48 */     return (super.equals(other) && asByte() == ((ByteTag)other).asByte());
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(ByteTag other) {
/* 53 */     return getValue().compareTo(other.getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public ByteTag clone() {
/* 58 */     return new ByteTag(getValue().byteValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\ByteTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */