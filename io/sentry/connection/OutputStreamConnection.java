/*    */ package io.sentry.connection;
/*    */ 
/*    */ import io.sentry.event.Event;
/*    */ import io.sentry.marshaller.Marshaller;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.nio.charset.Charset;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class OutputStreamConnection
/*    */   extends AbstractConnection
/*    */ {
/* 14 */   private static final Charset UTF_8 = Charset.forName("UTF-8");
/*    */ 
/*    */   
/*    */   private final OutputStream outputStream;
/*    */ 
/*    */   
/*    */   private Marshaller marshaller;
/*    */ 
/*    */   
/*    */   public OutputStreamConnection(OutputStream outputStream) {
/* 24 */     super(null, null);
/* 25 */     this.outputStream = outputStream;
/*    */   }
/*    */ 
/*    */   
/*    */   protected synchronized void doSend(Event event) throws ConnectionException {
/*    */     try {
/* 31 */       this.outputStream.write("Sentry event:\n".getBytes(UTF_8));
/* 32 */       this.marshaller.marshall(event, this.outputStream);
/* 33 */       this.outputStream.write("\n".getBytes(UTF_8));
/* 34 */       this.outputStream.flush();
/* 35 */     } catch (IOException e) {
/* 36 */       throw new ConnectionException("Couldn't sent the event properly", e);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 42 */     this.outputStream.close();
/*    */   }
/*    */   
/*    */   public void setMarshaller(Marshaller marshaller) {
/* 46 */     this.marshaller = marshaller;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\OutputStreamConnection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */