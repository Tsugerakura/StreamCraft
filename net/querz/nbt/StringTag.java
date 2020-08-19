/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class StringTag
/*    */   extends Tag<String> implements Comparable<StringTag> {
/*    */   public static final String ZERO_VALUE = "";
/*    */   
/*    */   public StringTag() {
/* 12 */     super("");
/*    */   }
/*    */   
/*    */   public StringTag(String value) {
/* 16 */     super(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 21 */     return super.getValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValue(String value) {
/* 26 */     super.setValue(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 31 */     dos.writeUTF(getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 36 */     setValue(dis.readUTF());
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToString(int maxDepth) {
/* 41 */     return escapeString(getValue(), false);
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 46 */     return escapeString(getValue(), true);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 51 */     return (super.equals(other) && getValue().equals(((StringTag)other).getValue()));
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(StringTag o) {
/* 56 */     return getValue().compareTo(o.getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public StringTag clone() {
/* 61 */     return new StringTag(getValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\StringTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */