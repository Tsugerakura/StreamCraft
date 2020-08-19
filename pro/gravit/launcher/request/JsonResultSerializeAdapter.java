/*    */ package pro.gravit.launcher.request;
/*    */ 
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonObject;
/*    */ import com.google.gson.JsonPrimitive;
/*    */ import com.google.gson.JsonSerializationContext;
/*    */ import com.google.gson.JsonSerializer;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ 
/*    */ public class JsonResultSerializeAdapter
/*    */   implements JsonSerializer<WebSocketEvent>
/*    */ {
/*    */   private static final String PROP_NAME = "type";
/*    */   
/*    */   public JsonElement serialize(WebSocketEvent src, Type typeOfSrc, JsonSerializationContext context) {
/* 17 */     JsonObject jo = context.serialize(src).getAsJsonObject();
/*    */     
/* 19 */     String classPath = src.getType();
/* 20 */     jo.add("type", (JsonElement)new JsonPrimitive(classPath));
/*    */     
/* 22 */     return (JsonElement)jo;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\JsonResultSerializeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */