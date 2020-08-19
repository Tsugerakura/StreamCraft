/*    */ package net.querz.nbt.custom;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Arrays;
/*    */ import java.util.function.Supplier;
/*    */ import net.querz.nbt.ArrayTag;
/*    */ import net.querz.nbt.Tag;
/*    */ import net.querz.nbt.TagFactory;
/*    */ 
/*    */ public class ShortArrayTag extends ArrayTag<short[]> implements Comparable<ShortArrayTag> {
/* 12 */   public static final short[] ZERO_VALUE = new short[0];
/*    */   
/*    */   public static void register() {
/* 15 */     TagFactory.registerCustomTag(100, ShortArrayTag::new, ShortArrayTag.class);
/*    */   }
/*    */   
/*    */   public ShortArrayTag() {
/* 19 */     super(ZERO_VALUE);
/*    */   }
/*    */   
/*    */   public ShortArrayTag(short[] value) {
/* 23 */     super(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 28 */     dos.writeInt(length());
/* 29 */     for (int i : (short[])getValue()) {
/* 30 */       dos.writeShort(i);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 36 */     int length = dis.readInt();
/* 37 */     setValue(new short[length]);
/* 38 */     for (int i = 0; i < length; i++) {
/* 39 */       ((short[])getValue())[i] = dis.readShort();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 45 */     return arrayToString("S", "s");
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 50 */     return (super.equals(other) && (
/* 51 */       getValue() == ((ShortArrayTag)other).getValue() || (((short[])
/* 52 */       getValue()).length == ((ShortArrayTag)other).length() && 
/* 53 */       Arrays.equals((short[])getValue(), (short[])((ShortArrayTag)other).getValue()))));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 58 */     return Arrays.hashCode((short[])getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(ShortArrayTag other) {
/* 63 */     return Integer.compare(length(), other.length());
/*    */   }
/*    */ 
/*    */   
/*    */   public ShortArrayTag clone() {
/* 68 */     return new ShortArrayTag(Arrays.copyOf((short[])getValue(), length()));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\custom\ShortArrayTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */