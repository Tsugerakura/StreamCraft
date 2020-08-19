/*    */ package io.sentry.event.interfaces;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DebugMetaInterface
/*    */   implements SentryInterface
/*    */ {
/*    */   public static final String DEBUG_META_INTERFACE = "debug_meta";
/* 14 */   private ArrayList<DebugImage> debugImages = new ArrayList<>();
/*    */   
/*    */   public ArrayList<DebugImage> getDebugImages() {
/* 17 */     return this.debugImages;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void addDebugImage(DebugImage debugImage) {
/* 26 */     this.debugImages.add(debugImage);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getInterfaceName() {
/* 31 */     return "debug_meta";
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 36 */     return this.debugImages.hashCode();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 41 */     return "DebugMetaInterface{debugImages=" + this.debugImages + '}';
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static class DebugImage
/*    */     implements Serializable
/*    */   {
/*    */     private static final String DEFAULT_TYPE = "proguard";
/*    */ 
/*    */     
/*    */     private final String uuid;
/*    */ 
/*    */     
/*    */     private final String type;
/*    */ 
/*    */ 
/*    */     
/*    */     public DebugImage(String uuid) {
/* 60 */       this(uuid, "proguard");
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public DebugImage(String uuid, String type) {
/* 70 */       this.uuid = uuid;
/* 71 */       this.type = type;
/*    */     }
/*    */     
/*    */     public String getUuid() {
/* 75 */       return this.uuid;
/*    */     }
/*    */     
/*    */     public String getType() {
/* 79 */       return this.type;
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 84 */       return "DebugImage{uuid='" + this.uuid + '\'' + ", type='" + this.type + '\'' + '}';
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\interfaces\DebugMetaInterface.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */