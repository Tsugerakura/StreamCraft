/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ 
/*    */ public final class EndTag
/*    */   extends Tag<Void> {
/*  8 */   static final EndTag INSTANCE = new EndTag();
/*    */   
/*    */   private EndTag() {
/* 11 */     super(null);
/*    */   }
/*    */ 
/*    */   
/*    */   protected Void checkValue(Void value) {
/* 16 */     return value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void serializeValue(DataOutputStream dos, int maxDepth) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void deserializeValue(DataInputStream dis, int maxDepth) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public String valueToString(int maxDepth) {
/* 31 */     return "\"end\"";
/*    */   }
/*    */ 
/*    */   
/*    */   public String valueToTagString(int maxDepth) {
/* 36 */     throw new UnsupportedOperationException("EndTag cannot be turned into a String");
/*    */   }
/*    */ 
/*    */   
/*    */   public EndTag clone() {
/* 41 */     return INSTANCE;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\EndTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */