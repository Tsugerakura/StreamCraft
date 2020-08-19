/*    */ package io.sentry.marshaller;
/*    */ 
/*    */ import io.sentry.event.Event;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface Marshaller
/*    */ {
/*    */   void marshall(Event paramEvent, OutputStream paramOutputStream) throws IOException;
/*    */   
/*    */   String getContentType();
/*    */   
/*    */   String getContentEncoding();
/*    */   
/*    */   public static final class UncloseableOutputStream
/*    */     extends OutputStream
/*    */   {
/*    */     private final OutputStream originalStream;
/*    */     
/*    */     public UncloseableOutputStream(OutputStream originalStream) {
/* 50 */       this.originalStream = originalStream;
/*    */     }
/*    */ 
/*    */     
/*    */     public void write(int b) throws IOException {
/* 55 */       this.originalStream.write(b);
/*    */     }
/*    */ 
/*    */     
/*    */     public void write(byte[] b) throws IOException {
/* 60 */       this.originalStream.write(b);
/*    */     }
/*    */ 
/*    */     
/*    */     public void write(byte[] b, int off, int len) throws IOException {
/* 65 */       this.originalStream.write(b, off, len);
/*    */     }
/*    */ 
/*    */     
/*    */     public void flush() throws IOException {
/* 70 */       this.originalStream.flush();
/*    */     }
/*    */     
/*    */     public void close() throws IOException {}
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\marshaller\Marshaller.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */