/*    */ package io.sentry.marshaller.json;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
/*    */ import io.sentry.event.interfaces.SentryInterface;
/*    */ import io.sentry.event.interfaces.UserInterface;
/*    */ import java.io.IOException;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UserInterfaceBinding
/*    */   implements InterfaceBinding<UserInterface>
/*    */ {
/*    */   private static final String ID = "id";
/*    */   private static final String USERNAME = "username";
/*    */   private static final String EMAIL = "email";
/*    */   private static final String IP_ADDRESS = "ip_address";
/*    */   private static final String DATA = "data";
/*    */   
/*    */   public void writeInterface(JsonGenerator generator, UserInterface userInterface) throws IOException {
/* 21 */     generator.writeStartObject();
/* 22 */     generator.writeStringField("id", userInterface.getId());
/* 23 */     generator.writeStringField("username", userInterface.getUsername());
/* 24 */     generator.writeStringField("email", userInterface.getEmail());
/* 25 */     generator.writeStringField("ip_address", userInterface.getIpAddress());
/*    */     
/* 27 */     if (userInterface.getData() != null && !userInterface.getData().isEmpty()) {
/* 28 */       generator.writeObjectFieldStart("data");
/* 29 */       for (Map.Entry<String, Object> entry : (Iterable<Map.Entry<String, Object>>)userInterface.getData().entrySet()) {
/* 30 */         String name = entry.getKey();
/* 31 */         Object value = entry.getValue();
/* 32 */         if (value == null) {
/* 33 */           generator.writeNullField(name); continue;
/*    */         } 
/* 35 */         generator.writeObjectField(name, value);
/*    */       } 
/*    */       
/* 38 */       generator.writeEndObject();
/*    */     } 
/*    */     
/* 41 */     generator.writeEndObject();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\marshaller\json\UserInterfaceBinding.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */