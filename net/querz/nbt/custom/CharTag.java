/*    */ package net.querz.nbt.custom;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.function.Supplier;
/*    */ import net.querz.nbt.Tag;
/*    */ import net.querz.nbt.TagFactory;
/*    */ 
/*    */ public class CharTag extends Tag<Character> implements Comparable<CharTag> {
/*    */   public static final char ZERO_VALUE = '\000';
/*    */   
/*    */   public static void register() {
/* 14 */     TagFactory.registerCustomTag(110, CharTag::new, CharTag.class);
/*    */   }
/*    */   
/*    */   public CharTag() {
/* 18 */     super(Character.valueOf(false));
/*    */   }
/*    */   
/*    */   public CharTag(char value) {
/* 22 */     super(Character.valueOf(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public Character getValue() {
/* 27 */     return (Character)super.getValue();
/*    */   }
/*    */   
/*    */   public void setValue(char value) {
/* 31 */     setValue(Character.valueOf(value));
/*    */   }
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/* 36 */     dos.writeChar(getValue().charValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/* 41 */     setValue(dis.readChar());
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToString(int maxDepth) {
/* 46 */     return escapeString(getValue() + "", false);
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 51 */     return escapeString(getValue() + "", true);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object other) {
/* 56 */     return (super.equals(other) && getValue() == ((CharTag)other).getValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(CharTag o) {
/* 61 */     return Character.compare(getValue().charValue(), o.getValue().charValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public CharTag clone() {
/* 66 */     return new CharTag(getValue().charValue());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\custom\CharTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */