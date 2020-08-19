/*    */ package io.sentry.marshaller.json;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
/*    */ import io.sentry.event.interfaces.MessageInterface;
/*    */ import io.sentry.event.interfaces.SentryInterface;
/*    */ import io.sentry.util.Util;
/*    */ import java.io.IOException;
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
/*    */ public class MessageInterfaceBinding
/*    */   implements InterfaceBinding<MessageInterface>
/*    */ {
/*    */   public static final int DEFAULT_MAX_MESSAGE_LENGTH = 1000;
/*    */   private static final String MESSAGE_PARAMETER = "message";
/*    */   private static final String PARAMS_PARAMETER = "params";
/*    */   private static final String FORMATTED_PARAMETER = "formatted";
/*    */   private final int maxMessageLength;
/*    */   
/*    */   public MessageInterfaceBinding() {
/* 30 */     this.maxMessageLength = 1000;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MessageInterfaceBinding(int maxMessageLength) {
/* 39 */     this.maxMessageLength = maxMessageLength;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeInterface(JsonGenerator generator, MessageInterface messageInterface) throws IOException {
/* 44 */     generator.writeStartObject();
/* 45 */     generator.writeStringField("message", Util.trimString(messageInterface.getMessage(), this.maxMessageLength));
/* 46 */     generator.writeArrayFieldStart("params");
/* 47 */     for (String parameter : messageInterface.getParameters()) {
/* 48 */       generator.writeString(parameter);
/*    */     }
/* 50 */     generator.writeEndArray();
/* 51 */     if (messageInterface.getFormatted() != null) {
/* 52 */       generator.writeStringField("formatted", 
/* 53 */           Util.trimString(messageInterface.getFormatted(), this.maxMessageLength));
/*    */     }
/* 55 */     generator.writeEndObject();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\marshaller\json\MessageInterfaceBinding.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */