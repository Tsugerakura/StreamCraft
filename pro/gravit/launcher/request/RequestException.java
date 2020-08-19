/*    */ package pro.gravit.launcher.request;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ 
/*    */ public final class RequestException
/*    */   extends IOException {
/*    */   private static final long serialVersionUID = 7558237657082664821L;
/*    */   
/*    */   @LauncherAPI
/*    */   public RequestException(String message) {
/* 12 */     super(message);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public RequestException(String message, Throwable exc) {
/* 17 */     super(message, exc);
/*    */   }
/*    */   
/*    */   @LauncherAPI
/*    */   public RequestException(Throwable exc) {
/* 22 */     super(exc);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 27 */     return getMessage();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\RequestException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */