/*     */ package com.google.gson.internal.bind;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonPrimitive;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
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
/*     */ public final class JsonTreeReader
/*     */   extends JsonReader
/*     */ {
/*  38 */   private static final Reader UNREADABLE_READER = new Reader() {
/*     */       public int read(char[] buffer, int offset, int count) throws IOException {
/*  40 */         throw new AssertionError();
/*     */       }
/*     */       public void close() throws IOException {
/*  43 */         throw new AssertionError();
/*     */       }
/*     */     };
/*  46 */   private static final Object SENTINEL_CLOSED = new Object();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   private Object[] stack = new Object[32];
/*  52 */   private int stackSize = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  62 */   private String[] pathNames = new String[32];
/*  63 */   private int[] pathIndices = new int[32];
/*     */   
/*     */   public JsonTreeReader(JsonElement element) {
/*  66 */     super(UNREADABLE_READER);
/*  67 */     push(element);
/*     */   }
/*     */   
/*     */   public void beginArray() throws IOException {
/*  71 */     expect(JsonToken.BEGIN_ARRAY);
/*  72 */     JsonArray array = (JsonArray)peekStack();
/*  73 */     push(array.iterator());
/*  74 */     this.pathIndices[this.stackSize - 1] = 0;
/*     */   }
/*     */   
/*     */   public void endArray() throws IOException {
/*  78 */     expect(JsonToken.END_ARRAY);
/*  79 */     popStack();
/*  80 */     popStack();
/*  81 */     if (this.stackSize > 0) {
/*  82 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*     */     }
/*     */   }
/*     */   
/*     */   public void beginObject() throws IOException {
/*  87 */     expect(JsonToken.BEGIN_OBJECT);
/*  88 */     JsonObject object = (JsonObject)peekStack();
/*  89 */     push(object.entrySet().iterator());
/*     */   }
/*     */   
/*     */   public void endObject() throws IOException {
/*  93 */     expect(JsonToken.END_OBJECT);
/*  94 */     popStack();
/*  95 */     popStack();
/*  96 */     if (this.stackSize > 0) {
/*  97 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean hasNext() throws IOException {
/* 102 */     JsonToken token = peek();
/* 103 */     return (token != JsonToken.END_OBJECT && token != JsonToken.END_ARRAY);
/*     */   }
/*     */   
/*     */   public JsonToken peek() throws IOException {
/* 107 */     if (this.stackSize == 0) {
/* 108 */       return JsonToken.END_DOCUMENT;
/*     */     }
/*     */     
/* 111 */     Object o = peekStack();
/* 112 */     if (o instanceof Iterator) {
/* 113 */       boolean isObject = this.stack[this.stackSize - 2] instanceof JsonObject;
/* 114 */       Iterator<?> iterator = (Iterator)o;
/* 115 */       if (iterator.hasNext()) {
/* 116 */         if (isObject) {
/* 117 */           return JsonToken.NAME;
/*     */         }
/* 119 */         push(iterator.next());
/* 120 */         return peek();
/*     */       } 
/*     */       
/* 123 */       return isObject ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
/*     */     } 
/* 125 */     if (o instanceof JsonObject)
/* 126 */       return JsonToken.BEGIN_OBJECT; 
/* 127 */     if (o instanceof JsonArray)
/* 128 */       return JsonToken.BEGIN_ARRAY; 
/* 129 */     if (o instanceof JsonPrimitive) {
/* 130 */       JsonPrimitive primitive = (JsonPrimitive)o;
/* 131 */       if (primitive.isString())
/* 132 */         return JsonToken.STRING; 
/* 133 */       if (primitive.isBoolean())
/* 134 */         return JsonToken.BOOLEAN; 
/* 135 */       if (primitive.isNumber()) {
/* 136 */         return JsonToken.NUMBER;
/*     */       }
/* 138 */       throw new AssertionError();
/*     */     } 
/* 140 */     if (o instanceof com.google.gson.JsonNull)
/* 141 */       return JsonToken.NULL; 
/* 142 */     if (o == SENTINEL_CLOSED) {
/* 143 */       throw new IllegalStateException("JsonReader is closed");
/*     */     }
/* 145 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   
/*     */   private Object peekStack() {
/* 150 */     return this.stack[this.stackSize - 1];
/*     */   }
/*     */   
/*     */   private Object popStack() {
/* 154 */     Object result = this.stack[--this.stackSize];
/* 155 */     this.stack[this.stackSize] = null;
/* 156 */     return result;
/*     */   }
/*     */   
/*     */   private void expect(JsonToken expected) throws IOException {
/* 160 */     if (peek() != expected) {
/* 161 */       throw new IllegalStateException("Expected " + expected + " but was " + 
/* 162 */           peek() + locationString());
/*     */     }
/*     */   }
/*     */   
/*     */   public String nextName() throws IOException {
/* 167 */     expect(JsonToken.NAME);
/* 168 */     Iterator<?> i = (Iterator)peekStack();
/* 169 */     Map.Entry<?, ?> entry = (Map.Entry<?, ?>)i.next();
/* 170 */     String result = (String)entry.getKey();
/* 171 */     this.pathNames[this.stackSize - 1] = result;
/* 172 */     push(entry.getValue());
/* 173 */     return result;
/*     */   }
/*     */   
/*     */   public String nextString() throws IOException {
/* 177 */     JsonToken token = peek();
/* 178 */     if (token != JsonToken.STRING && token != JsonToken.NUMBER) {
/* 179 */       throw new IllegalStateException("Expected " + JsonToken.STRING + " but was " + token + 
/* 180 */           locationString());
/*     */     }
/* 182 */     String result = ((JsonPrimitive)popStack()).getAsString();
/* 183 */     if (this.stackSize > 0) {
/* 184 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*     */     }
/* 186 */     return result;
/*     */   }
/*     */   
/*     */   public boolean nextBoolean() throws IOException {
/* 190 */     expect(JsonToken.BOOLEAN);
/* 191 */     boolean result = ((JsonPrimitive)popStack()).getAsBoolean();
/* 192 */     if (this.stackSize > 0) {
/* 193 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*     */     }
/* 195 */     return result;
/*     */   }
/*     */   
/*     */   public void nextNull() throws IOException {
/* 199 */     expect(JsonToken.NULL);
/* 200 */     popStack();
/* 201 */     if (this.stackSize > 0) {
/* 202 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*     */     }
/*     */   }
/*     */   
/*     */   public double nextDouble() throws IOException {
/* 207 */     JsonToken token = peek();
/* 208 */     if (token != JsonToken.NUMBER && token != JsonToken.STRING) {
/* 209 */       throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + token + 
/* 210 */           locationString());
/*     */     }
/* 212 */     double result = ((JsonPrimitive)peekStack()).getAsDouble();
/* 213 */     if (!isLenient() && (Double.isNaN(result) || Double.isInfinite(result))) {
/* 214 */       throw new NumberFormatException("JSON forbids NaN and infinities: " + result);
/*     */     }
/* 216 */     popStack();
/* 217 */     if (this.stackSize > 0) {
/* 218 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*     */     }
/* 220 */     return result;
/*     */   }
/*     */   
/*     */   public long nextLong() throws IOException {
/* 224 */     JsonToken token = peek();
/* 225 */     if (token != JsonToken.NUMBER && token != JsonToken.STRING) {
/* 226 */       throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + token + 
/* 227 */           locationString());
/*     */     }
/* 229 */     long result = ((JsonPrimitive)peekStack()).getAsLong();
/* 230 */     popStack();
/* 231 */     if (this.stackSize > 0) {
/* 232 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*     */     }
/* 234 */     return result;
/*     */   }
/*     */   
/*     */   public int nextInt() throws IOException {
/* 238 */     JsonToken token = peek();
/* 239 */     if (token != JsonToken.NUMBER && token != JsonToken.STRING) {
/* 240 */       throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + token + 
/* 241 */           locationString());
/*     */     }
/* 243 */     int result = ((JsonPrimitive)peekStack()).getAsInt();
/* 244 */     popStack();
/* 245 */     if (this.stackSize > 0) {
/* 246 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*     */     }
/* 248 */     return result;
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 252 */     this.stack = new Object[] { SENTINEL_CLOSED };
/* 253 */     this.stackSize = 1;
/*     */   }
/*     */   
/*     */   public void skipValue() throws IOException {
/* 257 */     if (peek() == JsonToken.NAME) {
/* 258 */       nextName();
/* 259 */       this.pathNames[this.stackSize - 2] = "null";
/*     */     } else {
/* 261 */       popStack();
/* 262 */       if (this.stackSize > 0) {
/* 263 */         this.pathNames[this.stackSize - 1] = "null";
/*     */       }
/*     */     } 
/* 266 */     if (this.stackSize > 0) {
/* 267 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*     */     }
/*     */   }
/*     */   
/*     */   public String toString() {
/* 272 */     return getClass().getSimpleName();
/*     */   }
/*     */   
/*     */   public void promoteNameToValue() throws IOException {
/* 276 */     expect(JsonToken.NAME);
/* 277 */     Iterator<?> i = (Iterator)peekStack();
/* 278 */     Map.Entry<?, ?> entry = (Map.Entry<?, ?>)i.next();
/* 279 */     push(entry.getValue());
/* 280 */     push(new JsonPrimitive((String)entry.getKey()));
/*     */   }
/*     */   
/*     */   private void push(Object newTop) {
/* 284 */     if (this.stackSize == this.stack.length) {
/* 285 */       Object[] newStack = new Object[this.stackSize * 2];
/* 286 */       int[] newPathIndices = new int[this.stackSize * 2];
/* 287 */       String[] newPathNames = new String[this.stackSize * 2];
/* 288 */       System.arraycopy(this.stack, 0, newStack, 0, this.stackSize);
/* 289 */       System.arraycopy(this.pathIndices, 0, newPathIndices, 0, this.stackSize);
/* 290 */       System.arraycopy(this.pathNames, 0, newPathNames, 0, this.stackSize);
/* 291 */       this.stack = newStack;
/* 292 */       this.pathIndices = newPathIndices;
/* 293 */       this.pathNames = newPathNames;
/*     */     } 
/* 295 */     this.stack[this.stackSize++] = newTop;
/*     */   }
/*     */   
/*     */   public String getPath() {
/* 299 */     StringBuilder result = (new StringBuilder()).append('$');
/* 300 */     for (int i = 0; i < this.stackSize; i++) {
/* 301 */       if (this.stack[i] instanceof JsonArray) {
/* 302 */         if (this.stack[++i] instanceof Iterator) {
/* 303 */           result.append('[').append(this.pathIndices[i]).append(']');
/*     */         }
/* 305 */       } else if (this.stack[i] instanceof JsonObject && 
/* 306 */         this.stack[++i] instanceof Iterator) {
/* 307 */         result.append('.');
/* 308 */         if (this.pathNames[i] != null) {
/* 309 */           result.append(this.pathNames[i]);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 314 */     return result.toString();
/*     */   }
/*     */   
/*     */   private String locationString() {
/* 318 */     return " at path " + getPath();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\bind\JsonTreeReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */