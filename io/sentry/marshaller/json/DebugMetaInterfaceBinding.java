/*    */ package io.sentry.marshaller.json;
/*    */ 
/*    */ import com.fasterxml.jackson.core.JsonGenerator;
/*    */ import io.sentry.event.interfaces.DebugMetaInterface;
/*    */ import io.sentry.event.interfaces.SentryInterface;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DebugMetaInterfaceBinding
/*    */   implements InterfaceBinding<DebugMetaInterface>
/*    */ {
/*    */   private static final String IMAGES = "images";
/*    */   private static final String UUID = "uuid";
/*    */   private static final String TYPE = "type";
/*    */   
/*    */   public void writeInterface(JsonGenerator generator, DebugMetaInterface debugMetaInterface) throws IOException {
/* 18 */     generator.writeStartObject();
/* 19 */     writeDebugImages(generator, debugMetaInterface);
/* 20 */     generator.writeEndObject();
/*    */   }
/*    */   
/*    */   private void writeDebugImages(JsonGenerator generator, DebugMetaInterface debugMetaInterface) throws IOException {
/* 24 */     generator.writeArrayFieldStart("images");
/* 25 */     for (DebugMetaInterface.DebugImage debugImage : debugMetaInterface.getDebugImages()) {
/* 26 */       generator.writeStartObject();
/* 27 */       generator.writeStringField("uuid", debugImage.getUuid());
/* 28 */       generator.writeStringField("type", debugImage.getType());
/* 29 */       generator.writeEndObject();
/*    */     } 
/* 31 */     generator.writeEndArray();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\marshaller\json\DebugMetaInterfaceBinding.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */