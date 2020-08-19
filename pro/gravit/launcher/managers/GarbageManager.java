/*    */ package pro.gravit.launcher.managers;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import java.util.Timer;
/*    */ import java.util.TimerTask;
/*    */ import pro.gravit.launcher.NeedGarbageCollection;
/*    */ 
/*    */ public class GarbageManager
/*    */ {
/*    */   static class Entry {
/*    */     NeedGarbageCollection invoke;
/*    */     long timer;
/*    */     
/*    */     public Entry(NeedGarbageCollection invoke, long timer) {
/* 16 */       this.invoke = invoke;
/* 17 */       this.timer = timer;
/*    */     }
/*    */   }
/*    */   
/* 21 */   private static final Timer timer = new Timer("GarbageTimer");
/*    */   
/* 23 */   private static final Set<Entry> NEED_GARBARE_COLLECTION = new HashSet<>();
/*    */   
/*    */   public static void gc() {
/* 26 */     for (Entry gc : NEED_GARBARE_COLLECTION)
/* 27 */       gc.invoke.garbageCollection(); 
/*    */   }
/*    */   
/*    */   public static void registerNeedGC(NeedGarbageCollection gc) {
/* 31 */     NEED_GARBARE_COLLECTION.add(new Entry(gc, 0L));
/*    */   }
/*    */   
/*    */   public static void registerNeedGC(final NeedGarbageCollection gc, long time) {
/* 35 */     TimerTask task = new TimerTask()
/*    */       {
/*    */         public void run() {
/* 38 */           gc.garbageCollection();
/*    */         }
/*    */       };
/* 41 */     timer.schedule(task, time);
/* 42 */     NEED_GARBARE_COLLECTION.add(new Entry(gc, time));
/*    */   }
/*    */   
/*    */   public static void unregisterNeedGC(NeedGarbageCollection gc) {
/* 46 */     NEED_GARBARE_COLLECTION.removeIf(e -> (e.invoke == gc));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\managers\GarbageManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */