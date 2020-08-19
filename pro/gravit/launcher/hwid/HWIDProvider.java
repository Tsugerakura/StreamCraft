/*    */ package pro.gravit.launcher.hwid;
/*    */ 
/*    */ import pro.gravit.utils.ProviderMap;
/*    */ 
/*    */ public class HWIDProvider {
/*  6 */   public static ProviderMap<HWID> hwids = new ProviderMap();
/*    */   
/*    */   public static void registerHWIDs() {
/*  9 */     hwids.register("oshi", OshiHWID.class);
/* 10 */     hwids.register("no", NoHWID.class);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\hwid\HWIDProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */