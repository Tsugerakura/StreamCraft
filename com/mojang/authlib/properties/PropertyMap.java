/*    */ package com.mojang.authlib.properties;
/*    */ 
/*    */ import com.google.common.collect.ForwardingMultimap;
/*    */ import com.google.common.collect.LinkedHashMultimap;
/*    */ import com.google.common.collect.Multimap;
/*    */ import com.google.gson.JsonArray;
/*    */ import com.google.gson.JsonDeserializationContext;
/*    */ import com.google.gson.JsonDeserializer;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonObject;
/*    */ import com.google.gson.JsonParseException;
/*    */ import com.google.gson.JsonSerializationContext;
/*    */ import com.google.gson.JsonSerializer;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class PropertyMap
/*    */   extends ForwardingMultimap<String, Property> {
/* 19 */   private final Multimap<String, Property> properties = (Multimap<String, Property>)LinkedHashMultimap.create();
/*    */ 
/*    */   
/*    */   protected Multimap<String, Property> delegate() {
/* 23 */     return this.properties;
/*    */   }
/*    */   
/*    */   public static class Serializer
/*    */     implements JsonSerializer<PropertyMap>, JsonDeserializer<PropertyMap> {
/*    */     public PropertyMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
/* 29 */       PropertyMap result = new PropertyMap();
/*    */       
/* 31 */       if (json instanceof JsonObject) {
/* 32 */         JsonObject object = (JsonObject)json;
/*    */         
/* 34 */         for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)object.entrySet()) {
/* 35 */           if (entry.getValue() instanceof JsonArray) {
/* 36 */             for (JsonElement element : entry.getValue()) {
/* 37 */               result.put(entry.getKey(), new Property(entry.getKey(), element.getAsString()));
/*    */             }
/*    */           }
/*    */         } 
/* 41 */       } else if (json instanceof JsonArray) {
/* 42 */         for (JsonElement element : json) {
/* 43 */           if (element instanceof JsonObject) {
/* 44 */             JsonObject object = (JsonObject)element;
/* 45 */             String name = object.getAsJsonPrimitive("name").getAsString();
/* 46 */             String value = object.getAsJsonPrimitive("value").getAsString();
/*    */             
/* 48 */             if (object.has("signature")) {
/* 49 */               result.put(name, new Property(name, value, object.getAsJsonPrimitive("signature").getAsString())); continue;
/*    */             } 
/* 51 */             result.put(name, new Property(name, value));
/*    */           } 
/*    */         } 
/*    */       } 
/*    */ 
/*    */       
/* 57 */       return result;
/*    */     }
/*    */ 
/*    */     
/*    */     public JsonElement serialize(PropertyMap src, Type typeOfSrc, JsonSerializationContext context) {
/* 62 */       JsonArray result = new JsonArray();
/*    */       
/* 64 */       for (Property property : src.values()) {
/* 65 */         JsonObject object = new JsonObject();
/*    */         
/* 67 */         object.addProperty("name", property.getName());
/* 68 */         object.addProperty("value", property.getValue());
/*    */         
/* 70 */         if (property.hasSignature()) {
/* 71 */           object.addProperty("signature", property.getSignature());
/*    */         }
/*    */         
/* 74 */         result.add((JsonElement)object);
/*    */       } 
/*    */       
/* 77 */       return (JsonElement)result;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\properties\PropertyMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */