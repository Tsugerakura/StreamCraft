/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class ShortTag
/*    */   extends NumberTag<Short> implements Comparable<ShortTag> {
/*    */   public static final short ZERO_VALUE = 0;
/*    */   
/*    */   public ShortTag() {
/* 12 */     super(Short.valueOf((short)0));
/*    */   }
/*    */   
/*    */   public ShortTag(short value) {
/* 16 */     super(Short.valueOf(value));
/*    */   }
/*    */   
/*    */   public void setValue(short value) {
/* 20 */     setValue((T)Short.valueOf(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 25 */     dos.writeShort(getValue().shortValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 30 */     setValue(dis.readShort());
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 35 */     return getValue() + "s";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 40 */     return (super.equals(other) && asShort() == ((ShortTag)other).asShort());
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(ShortTag other) {
/* 45 */     return getValue().compareTo(other.getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public ShortTag clone() {
/* 50 */     return new ShortTag(getValue().shortValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\ShortTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */