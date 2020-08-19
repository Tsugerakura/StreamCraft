/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.NumberInput;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JsonPointer
/*     */ {
/*     */   public static final char SEPARATOR = '/';
/*  34 */   protected static final JsonPointer EMPTY = new JsonPointer();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final JsonPointer _nextSegment;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected volatile JsonPointer _head;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String _asString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final String _matchingPropertyName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int _matchingElementIndex;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonPointer() {
/*  78 */     this._nextSegment = null;
/*  79 */     this._matchingPropertyName = "";
/*  80 */     this._matchingElementIndex = -1;
/*  81 */     this._asString = "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonPointer(String fullString, String segment, JsonPointer next) {
/*  88 */     this._asString = fullString;
/*  89 */     this._nextSegment = next;
/*     */     
/*  91 */     this._matchingPropertyName = segment;
/*     */     
/*  93 */     this._matchingElementIndex = _parseIndex(segment);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonPointer(String fullString, String segment, int matchIndex, JsonPointer next) {
/* 100 */     this._asString = fullString;
/* 101 */     this._nextSegment = next;
/* 102 */     this._matchingPropertyName = segment;
/* 103 */     this._matchingElementIndex = matchIndex;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonPointer compile(String input) throws IllegalArgumentException {
/* 124 */     if (input == null || input.length() == 0) {
/* 125 */       return EMPTY;
/*     */     }
/*     */     
/* 128 */     if (input.charAt(0) != '/') {
/* 129 */       throw new IllegalArgumentException("Invalid input: JSON Pointer expression must start with '/': \"" + input + "\"");
/*     */     }
/* 131 */     return _parseTail(input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonPointer valueOf(String input) {
/* 138 */     return compile(input);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonPointer empty() {
/* 149 */     return EMPTY;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JsonPointer forPath(JsonStreamContext context, boolean includeRoot) {
/* 166 */     if (context == null) {
/* 167 */       return EMPTY;
/*     */     }
/* 169 */     if (!context.hasPathSegment())
/*     */     {
/* 171 */       if (!includeRoot || !context.inRoot() || !context.hasCurrentIndex()) {
/* 172 */         context = context.getParent();
/*     */       }
/*     */     }
/* 175 */     JsonPointer tail = null;
/*     */     
/* 177 */     for (; context != null; context = context.getParent()) {
/* 178 */       if (context.inObject()) {
/* 179 */         String seg = context.getCurrentName();
/* 180 */         if (seg == null) {
/* 181 */           seg = "";
/*     */         }
/* 183 */         tail = new JsonPointer(_fullPath(tail, seg), seg, tail);
/* 184 */       } else if (context.inArray() || includeRoot) {
/* 185 */         int ix = context.getCurrentIndex();
/* 186 */         String ixStr = String.valueOf(ix);
/* 187 */         tail = new JsonPointer(_fullPath(tail, ixStr), ixStr, ix, tail);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 193 */     if (tail == null) {
/* 194 */       return EMPTY;
/*     */     }
/* 196 */     return tail;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String _fullPath(JsonPointer tail, String segment) {
/* 201 */     if (tail == null) {
/* 202 */       StringBuilder stringBuilder = new StringBuilder(segment.length() + 1);
/* 203 */       stringBuilder.append('/');
/* 204 */       _appendEscaped(stringBuilder, segment);
/* 205 */       return stringBuilder.toString();
/*     */     } 
/* 207 */     String tailDesc = tail._asString;
/* 208 */     StringBuilder sb = new StringBuilder(segment.length() + 1 + tailDesc.length());
/* 209 */     sb.append('/');
/* 210 */     _appendEscaped(sb, segment);
/* 211 */     sb.append(tailDesc);
/* 212 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   private static void _appendEscaped(StringBuilder sb, String segment) {
/* 217 */     for (int i = 0, end = segment.length(); i < end; i++) {
/* 218 */       char c = segment.charAt(i);
/* 219 */       if (c == '/') {
/* 220 */         sb.append("~1");
/*     */       
/*     */       }
/* 223 */       else if (c == '~') {
/* 224 */         sb.append("~0");
/*     */       } else {
/*     */         
/* 227 */         sb.append(c);
/*     */       } 
/*     */     } 
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
/*     */   public boolean matches() {
/* 259 */     return (this._nextSegment == null);
/* 260 */   } public String getMatchingProperty() { return this._matchingPropertyName; } public int getMatchingIndex() {
/* 261 */     return this._matchingElementIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean mayMatchProperty() {
/* 267 */     return (this._matchingPropertyName != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean mayMatchElement() {
/* 273 */     return (this._matchingElementIndex >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPointer last() {
/* 282 */     JsonPointer current = this;
/* 283 */     if (current == EMPTY) {
/* 284 */       return null;
/*     */     }
/*     */     JsonPointer next;
/* 287 */     while ((next = current._nextSegment) != EMPTY) {
/* 288 */       current = next;
/*     */     }
/* 290 */     return current;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPointer append(JsonPointer tail) {
/* 310 */     if (this == EMPTY) {
/* 311 */       return tail;
/*     */     }
/* 313 */     if (tail == EMPTY) {
/* 314 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 319 */     String currentJsonPointer = this._asString;
/* 320 */     if (currentJsonPointer.endsWith("/"))
/*     */     {
/* 322 */       currentJsonPointer = currentJsonPointer.substring(0, currentJsonPointer.length() - 1);
/*     */     }
/* 324 */     return compile(currentJsonPointer + tail._asString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matchesProperty(String name) {
/* 334 */     return (this._nextSegment != null && this._matchingPropertyName.equals(name));
/*     */   }
/*     */   
/*     */   public JsonPointer matchProperty(String name) {
/* 338 */     if (this._nextSegment != null && this._matchingPropertyName.equals(name)) {
/* 339 */       return this._nextSegment;
/*     */     }
/* 341 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matchesElement(int index) {
/* 351 */     return (index == this._matchingElementIndex && index >= 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPointer matchElement(int index) {
/* 358 */     if (index != this._matchingElementIndex || index < 0) {
/* 359 */       return null;
/*     */     }
/* 361 */     return this._nextSegment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonPointer tail() {
/* 370 */     return this._nextSegment;
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
/*     */ 
/*     */   
/*     */   public JsonPointer head() {
/* 384 */     JsonPointer h = this._head;
/* 385 */     if (h == null) {
/* 386 */       if (this != EMPTY) {
/* 387 */         h = _constructHead();
/*     */       }
/* 389 */       this._head = h;
/*     */     } 
/* 391 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 400 */     return this._asString; } public int hashCode() {
/* 401 */     return this._asString.hashCode();
/*     */   }
/*     */   public boolean equals(Object o) {
/* 404 */     if (o == this) return true; 
/* 405 */     if (o == null) return false; 
/* 406 */     if (!(o instanceof JsonPointer)) return false; 
/* 407 */     return this._asString.equals(((JsonPointer)o)._asString);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int _parseIndex(String str) {
/* 417 */     int len = str.length();
/*     */ 
/*     */     
/* 420 */     if (len == 0 || len > 10) {
/* 421 */       return -1;
/*     */     }
/*     */     
/* 424 */     char c = str.charAt(0);
/* 425 */     if (c <= '0') {
/* 426 */       return (len == 1 && c == '0') ? 0 : -1;
/*     */     }
/* 428 */     if (c > '9') {
/* 429 */       return -1;
/*     */     }
/* 431 */     for (int i = 1; i < len; i++) {
/* 432 */       c = str.charAt(i);
/* 433 */       if (c > '9' || c < '0') {
/* 434 */         return -1;
/*     */       }
/*     */     } 
/* 437 */     if (len == 10) {
/* 438 */       long l = NumberInput.parseLong(str);
/* 439 */       if (l > 2147483647L) {
/* 440 */         return -1;
/*     */       }
/*     */     } 
/* 443 */     return NumberInput.parseInt(str);
/*     */   }
/*     */   
/*     */   protected static JsonPointer _parseTail(String input) {
/* 447 */     int end = input.length();
/*     */ 
/*     */     
/* 450 */     for (int i = 1; i < end; ) {
/* 451 */       char c = input.charAt(i);
/* 452 */       if (c == '/') {
/* 453 */         return new JsonPointer(input, input.substring(1, i), 
/* 454 */             _parseTail(input.substring(i)));
/*     */       }
/* 456 */       i++;
/*     */       
/* 458 */       if (c == '~' && i < end) {
/* 459 */         return _parseQuotedTail(input, i);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 464 */     return new JsonPointer(input, input.substring(1), EMPTY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static JsonPointer _parseQuotedTail(String input, int i) {
/* 475 */     int end = input.length();
/* 476 */     StringBuilder sb = new StringBuilder(Math.max(16, end));
/* 477 */     if (i > 2) {
/* 478 */       sb.append(input, 1, i - 1);
/*     */     }
/* 480 */     _appendEscape(sb, input.charAt(i++));
/* 481 */     while (i < end) {
/* 482 */       char c = input.charAt(i);
/* 483 */       if (c == '/') {
/* 484 */         return new JsonPointer(input, sb.toString(), 
/* 485 */             _parseTail(input.substring(i)));
/*     */       }
/* 487 */       i++;
/* 488 */       if (c == '~' && i < end) {
/* 489 */         _appendEscape(sb, input.charAt(i++));
/*     */         continue;
/*     */       } 
/* 492 */       sb.append(c);
/*     */     } 
/*     */     
/* 495 */     return new JsonPointer(input, sb.toString(), EMPTY);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonPointer _constructHead() {
/* 501 */     JsonPointer last = last();
/* 502 */     if (last == this) {
/* 503 */       return EMPTY;
/*     */     }
/*     */     
/* 506 */     int suffixLength = last._asString.length();
/* 507 */     JsonPointer next = this._nextSegment;
/* 508 */     return new JsonPointer(this._asString.substring(0, this._asString.length() - suffixLength), this._matchingPropertyName, this._matchingElementIndex, next
/* 509 */         ._constructHead(suffixLength, last));
/*     */   }
/*     */ 
/*     */   
/*     */   protected JsonPointer _constructHead(int suffixLength, JsonPointer last) {
/* 514 */     if (this == last) {
/* 515 */       return EMPTY;
/*     */     }
/* 517 */     JsonPointer next = this._nextSegment;
/* 518 */     String str = this._asString;
/* 519 */     return new JsonPointer(str.substring(0, str.length() - suffixLength), this._matchingPropertyName, this._matchingElementIndex, next
/* 520 */         ._constructHead(suffixLength, last));
/*     */   }
/*     */   
/*     */   private static void _appendEscape(StringBuilder sb, char c) {
/* 524 */     if (c == '0') {
/* 525 */       c = '~';
/* 526 */     } else if (c == '1') {
/* 527 */       c = '/';
/*     */     } else {
/* 529 */       sb.append('~');
/*     */     } 
/* 531 */     sb.append(c);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\JsonPointer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */