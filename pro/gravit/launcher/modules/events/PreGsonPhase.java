/*    */ package pro.gravit.launcher.modules.events;
/*    */ 
/*    */ import com.google.gson.GsonBuilder;
/*    */ import pro.gravit.launcher.modules.LauncherModule;
/*    */ 
/*    */ public class PreGsonPhase
/*    */   extends LauncherModule.Event {
/*    */   public GsonBuilder gsonBuilder;
/*    */   
/*    */   public PreGsonPhase(GsonBuilder gsonBuilder) {
/* 11 */     this.gsonBuilder = gsonBuilder;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\modules\events\PreGsonPhase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */