/*    */ package io.sentry.connection;
/*    */ 
/*    */ import io.sentry.event.Event;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NoopConnection
/*    */   extends AbstractConnection
/*    */ {
/*    */   public NoopConnection() {
/* 17 */     super(null, null);
/*    */   }
/*    */   
/*    */   protected void doSend(Event event) throws ConnectionException {}
/*    */   
/*    */   public void close() throws IOException {}
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\NoopConnection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */