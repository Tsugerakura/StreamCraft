/*    */ package pro.gravit.launcher.serialize.stream;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.serialize.HInput;
/*    */ import pro.gravit.launcher.serialize.HOutput;
/*    */ import pro.gravit.utils.helper.VerifyHelper;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class EnumSerializer<E extends Enum<?> & EnumSerializer.Itf>
/*    */ {
/*    */   @LauncherAPI
/*    */   public static void write(HOutput output, Itf itf) throws IOException {
/* 22 */     output.writeVarInt(itf.getNumber());
/*    */   }
/*    */   
/* 25 */   private final Map<Integer, E> map = new HashMap<>(16);
/*    */   
/*    */   @LauncherAPI
/*    */   public EnumSerializer(Class<E> clazz) {
/* 29 */     for (Enum enum_ : (Enum[])clazz.getEnumConstants())
/* 30 */       VerifyHelper.putIfAbsent(this.map, Integer.valueOf(((Itf)enum_).getNumber()), enum_, "Duplicate number for enum constant " + enum_.name()); 
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public E read(HInput input) throws IOException {
/* 35 */     int n = input.readVarInt();
/* 36 */     return (E)VerifyHelper.getMapValue(this.map, Integer.valueOf(n), "Unknown enum number: " + n);
/*    */   }
/*    */   
/*    */   @FunctionalInterface
/*    */   public static interface Itf {
/*    */     @LauncherAPI
/*    */     int getNumber();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\serialize\stream\EnumSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */