/*    */ package pro.gravit.launcher.hasher;public abstract class HashedEntry extends StreamObject {
/*    */   @LauncherAPI
/*    */   public boolean flag;
/*    */   
/*    */   @LauncherAPI
/*    */   public abstract Type getType();
/*    */   
/*    */   @LauncherAPI
/*    */   public abstract long size();
/*    */   
/*    */   @LauncherAPI
/*    */   public enum Type implements EnumSerializer.Itf {
/* 13 */     DIR(1), FILE(2); private final int n;
/* 14 */     private static final EnumSerializer<Type> SERIALIZER = new EnumSerializer(Type.class); static {
/*    */     
/*    */     } public static Type read(HInput input) throws IOException {
/* 17 */       return (Type)SERIALIZER.read(input);
/*    */     }
/*    */ 
/*    */ 
/*    */     
/*    */     Type(int n) {
/* 23 */       this.n = n;
/*    */     }
/*    */ 
/*    */     
/*    */     public int getNumber() {
/* 28 */       return this.n;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\hasher\HashedEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */