/*    */ package pro.gravit.launcher.events;
/*    */ 
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class ControlEvent
/*    */ {
/*    */   public ControlCommand signal;
/*  8 */   private static final UUID uuid = UUID.fromString("f1051a64-0cd0-4ed8-8430-d856a196e91f");
/*    */   
/*    */   public enum ControlCommand {
/* 11 */     STOP, START, PAUSE, CONTINUE, CRASH;
/*    */   }
/*    */   
/*    */   public ControlEvent(ControlCommand signal) {
/* 15 */     this.signal = signal;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\events\ControlEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */