/*    */ package pro.gravit.launcher;
/*    */ 
/*    */ import io.sentry.Sentry;
/*    */ import java.util.function.Consumer;
/*    */ import pro.gravit.launcher.request.auth.JVMRequest;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class NativeConsumer
/*    */   implements Consumer<byte[]>
/*    */ {
/*    */   public void accept(byte[] bytes) {
/*    */     try {
/* 13 */       LogHelper.debug("NativeConsumer accept " + bytes.length);
/*    */       
/* 15 */       JVMRequest request = new JVMRequest(bytes);
/* 16 */       request.request();
/* 17 */     } catch (Exception e) {
/* 18 */       Sentry.capture(e);
/* 19 */       LogHelper.error(e);
/* 20 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\NativeConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */