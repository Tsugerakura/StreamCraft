/*    */ package pro.gravit.launcher.serialize.stream;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.serialize.HInput;
/*    */ import pro.gravit.launcher.serialize.HOutput;
/*    */ import pro.gravit.utils.helper.IOHelper;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class StreamObject
/*    */ {
/*    */   @LauncherAPI
/*    */   public final byte[] write() throws IOException {
/* 22 */     try (ByteArrayOutputStream array = IOHelper.newByteArrayOutput()) {
/* 23 */       try (HOutput output = new HOutput(array)) {
/* 24 */         write(output);
/*    */       } 
/* 26 */       return array.toByteArray();
/*    */     } 
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public abstract void write(HOutput paramHOutput) throws IOException;
/*    */   
/*    */   @FunctionalInterface
/*    */   public static interface Adapter<O extends StreamObject> {
/*    */     @LauncherAPI
/*    */     O convert(HInput param1HInput) throws IOException;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\serialize\stream\StreamObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */