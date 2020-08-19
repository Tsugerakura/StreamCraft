/*    */ package pro.gravit.launcher.request;
/*    */ import java.io.IOException;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ import pro.gravit.launcher.serialize.HInput;
/*    */ import pro.gravit.launcher.serialize.stream.EnumSerializer;
/*    */ 
/*    */ public enum RequestType implements EnumSerializer.Itf {
/*    */   private static final EnumSerializer<RequestType> SERIALIZER;
/*    */   private final int n;
/* 10 */   PING(0),
/* 11 */   LEGACYLAUNCHER(1), UPDATE(2), UPDATE_LIST(3),
/* 12 */   AUTH(4), JOIN_SERVER(5), CHECK_SERVER(6),
/* 13 */   PROFILE_BY_USERNAME(7), PROFILE_BY_UUID(8), BATCH_PROFILE_BY_USERNAME(9),
/* 14 */   PROFILES(10), SERVERAUTH(11), SETPROFILE(12), LAUNCHER(13), CHANGESERVER(14), EXECCOMMAND(15),
/* 15 */   CUSTOM(255); static {
/* 16 */     SERIALIZER = new EnumSerializer(RequestType.class);
/*    */   }
/*    */   @LauncherAPI
/*    */   public static RequestType read(HInput input) throws IOException {
/* 20 */     return (RequestType)SERIALIZER.read(input);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   RequestType(int n) {
/* 26 */     this.n = n;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getNumber() {
/* 31 */     return this.n;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\RequestType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */