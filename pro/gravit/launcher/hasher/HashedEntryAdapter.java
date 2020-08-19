/*    */ package pro.gravit.launcher.hasher;
/*    */ 
/*    */ import com.google.gson.JsonDeserializationContext;
/*    */ import com.google.gson.JsonDeserializer;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonObject;
/*    */ import com.google.gson.JsonParseException;
/*    */ import com.google.gson.JsonPrimitive;
/*    */ import com.google.gson.JsonSerializationContext;
/*    */ import com.google.gson.JsonSerializer;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HashedEntryAdapter
/*    */   implements JsonSerializer<HashedEntry>, JsonDeserializer<HashedEntry>
/*    */ {
/*    */   private static final String PROP_NAME = "type";
/*    */   
/*    */   public HashedEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
/* 24 */     String typename = json.getAsJsonObject().getAsJsonPrimitive("type").getAsString();
/* 25 */     Class<?> cls = null;
/* 26 */     if (typename.equals("dir")) cls = HashedDir.class; 
/* 27 */     if (typename.equals("file")) cls = HashedFile.class;
/*    */     
/* 29 */     return (HashedEntry)context.deserialize(json, cls);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public JsonElement serialize(HashedEntry src, Type typeOfSrc, JsonSerializationContext context) {
/* 35 */     JsonObject jo = context.serialize(src).getAsJsonObject();
/*    */     
/* 37 */     HashedEntry.Type type = src.getType();
/* 38 */     if (type == HashedEntry.Type.DIR)
/* 39 */       jo.add("type", (JsonElement)new JsonPrimitive("dir")); 
/* 40 */     if (type == HashedEntry.Type.FILE) {
/* 41 */       jo.add("type", (JsonElement)new JsonPrimitive("file"));
/*    */     }
/* 43 */     return (JsonElement)jo;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\hasher\HashedEntryAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */