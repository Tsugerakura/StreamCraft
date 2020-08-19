/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal.LinkedTreeMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class JsonObject
/*     */   extends JsonElement
/*     */ {
/*  33 */   private final LinkedTreeMap<String, JsonElement> members = new LinkedTreeMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonObject deepCopy() {
/*  42 */     JsonObject result = new JsonObject();
/*  43 */     for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)this.members.entrySet()) {
/*  44 */       result.add(entry.getKey(), ((JsonElement)entry.getValue()).deepCopy());
/*     */     }
/*  46 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(String property, JsonElement value) {
/*  58 */     if (value == null) {
/*  59 */       value = JsonNull.INSTANCE;
/*     */     }
/*  61 */     this.members.put(property, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonElement remove(String property) {
/*  72 */     return (JsonElement)this.members.remove(property);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addProperty(String property, String value) {
/*  83 */     add(property, createJsonElement(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addProperty(String property, Number value) {
/*  94 */     add(property, createJsonElement(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addProperty(String property, Boolean value) {
/* 105 */     add(property, createJsonElement(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addProperty(String property, Character value) {
/* 116 */     add(property, createJsonElement(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private JsonElement createJsonElement(Object value) {
/* 126 */     return (value == null) ? JsonNull.INSTANCE : new JsonPrimitive(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, JsonElement>> entrySet() {
/* 136 */     return this.members.entrySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> keySet() {
/* 146 */     return this.members.keySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 155 */     return this.members.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean has(String memberName) {
/* 165 */     return this.members.containsKey(memberName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonElement get(String memberName) {
/* 175 */     return (JsonElement)this.members.get(memberName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPrimitive getAsJsonPrimitive(String memberName) {
/* 185 */     return (JsonPrimitive)this.members.get(memberName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonArray getAsJsonArray(String memberName) {
/* 195 */     return (JsonArray)this.members.get(memberName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonObject getAsJsonObject(String memberName) {
/* 205 */     return (JsonObject)this.members.get(memberName);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 210 */     return (o == this || (o instanceof JsonObject && ((JsonObject)o).members
/* 211 */       .equals(this.members)));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 216 */     return this.members.hashCode();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\JsonObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */