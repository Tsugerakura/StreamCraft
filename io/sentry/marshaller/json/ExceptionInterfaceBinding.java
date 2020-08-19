/*    */ package io.sentry.marshaller.json;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
/*    */ import io.sentry.event.interfaces.ExceptionInterface;
/*    */ import io.sentry.event.interfaces.ExceptionMechanism;
/*    */ import io.sentry.event.interfaces.SentryException;
/*    */ import io.sentry.event.interfaces.SentryInterface;
/*    */ import io.sentry.event.interfaces.StackTraceInterface;
/*    */ import java.io.IOException;
/*    */ import java.util.Deque;
/*    */ import java.util.Iterator;
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
/*    */ public class ExceptionInterfaceBinding
/*    */   implements InterfaceBinding<ExceptionInterface>
/*    */ {
/*    */   private static final String TYPE_PARAMETER = "type";
/*    */   private static final String VALUE_PARAMETER = "value";
/*    */   private static final String MODULE_PARAMETER = "module";
/*    */   private static final String STACKTRACE_PARAMETER = "stacktrace";
/*    */   private final InterfaceBinding<StackTraceInterface> stackTraceInterfaceBinding;
/*    */   
/*    */   public ExceptionInterfaceBinding(InterfaceBinding<StackTraceInterface> stackTraceInterfaceBinding) {
/* 33 */     this.stackTraceInterfaceBinding = stackTraceInterfaceBinding;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeInterface(JsonGenerator generator, ExceptionInterface exceptionInterface) throws IOException {
/* 38 */     Deque<SentryException> exceptions = exceptionInterface.getExceptions();
/*    */     
/* 40 */     generator.writeStartArray();
/* 41 */     for (Iterator<SentryException> iterator = exceptions.descendingIterator(); iterator.hasNext();) {
/* 42 */       writeException(generator, iterator.next());
/*    */     }
/* 44 */     generator.writeEndArray();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void writeException(JsonGenerator generator, SentryException sentryException) throws IOException {
/* 55 */     generator.writeStartObject();
/* 56 */     generator.writeStringField("type", sentryException.getExceptionClassName());
/* 57 */     generator.writeStringField("value", sentryException.getExceptionMessage());
/* 58 */     generator.writeStringField("module", sentryException.getExceptionPackageName());
/*    */     
/* 60 */     ExceptionMechanism exceptionMechanism = sentryException.getExceptionMechanism();
/* 61 */     if (exceptionMechanism != null) {
/* 62 */       generator.writeFieldName("mechanism");
/* 63 */       generator.writeStartObject();
/* 64 */       generator.writeStringField("type", exceptionMechanism.getType());
/* 65 */       generator.writeBooleanField("handled", exceptionMechanism.isHandled());
/* 66 */       generator.writeEndObject();
/*    */     } 
/*    */     
/* 69 */     generator.writeFieldName("stacktrace");
/* 70 */     this.stackTraceInterfaceBinding.writeInterface(generator, sentryException.getStackTraceInterface());
/* 71 */     generator.writeEndObject();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\marshaller\json\ExceptionInterfaceBinding.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */