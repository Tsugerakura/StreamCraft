/*    */ package pro.gravit.launcher;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicReference;
/*    */ import javafx.application.Application;
/*    */ 
/*    */ public abstract class JSApplication
/*    */   extends Application {
/*  8 */   private static final AtomicReference<JSApplication> INSTANCE = new AtomicReference<>();
/*    */   
/*    */   @LauncherAPI
/*    */   public static JSApplication getInstance() {
/* 12 */     return INSTANCE.get();
/*    */   }
/*    */   
/*    */   public JSApplication() {
/* 16 */     INSTANCE.set(this);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\JSApplication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */