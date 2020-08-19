/*     */ package org.slf4j.helpers;
/*     */ 
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MessageFormatter
/*     */ {
/*     */   static final char DELIM_START = '{';
/*     */   static final char DELIM_STOP = '}';
/*     */   static final String DELIM_STR = "{}";
/*     */   private static final char ESCAPE_CHAR = '\\';
/*     */   
/*     */   public static final FormattingTuple format(String messagePattern, Object arg) {
/* 124 */     return arrayFormat(messagePattern, new Object[] { arg });
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
/*     */   public static final FormattingTuple format(String messagePattern, Object arg1, Object arg2) {
/* 151 */     return arrayFormat(messagePattern, new Object[] { arg1, arg2 });
/*     */   }
/*     */ 
/*     */   
/*     */   static final Throwable getThrowableCandidate(Object[] argArray) {
/* 156 */     if (argArray == null || argArray.length == 0) {
/* 157 */       return null;
/*     */     }
/*     */     
/* 160 */     Object lastEntry = argArray[argArray.length - 1];
/* 161 */     if (lastEntry instanceof Throwable) {
/* 162 */       return (Throwable)lastEntry;
/*     */     }
/* 164 */     return null;
/*     */   }
/*     */   
/*     */   public static final FormattingTuple arrayFormat(String messagePattern, Object[] argArray) {
/* 168 */     Throwable throwableCandidate = getThrowableCandidate(argArray);
/* 169 */     Object[] args = argArray;
/* 170 */     if (throwableCandidate != null) {
/* 171 */       args = trimmedCopy(argArray);
/*     */     }
/* 173 */     return arrayFormat(messagePattern, args, throwableCandidate);
/*     */   }
/*     */   
/*     */   private static Object[] trimmedCopy(Object[] argArray) {
/* 177 */     if (argArray == null || argArray.length == 0) {
/* 178 */       throw new IllegalStateException("non-sensical empty or null argument array");
/*     */     }
/* 180 */     int trimemdLen = argArray.length - 1;
/* 181 */     Object[] trimmed = new Object[trimemdLen];
/* 182 */     System.arraycopy(argArray, 0, trimmed, 0, trimemdLen);
/* 183 */     return trimmed;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final FormattingTuple arrayFormat(String messagePattern, Object[] argArray, Throwable throwable) {
/* 188 */     if (messagePattern == null) {
/* 189 */       return new FormattingTuple(null, argArray, throwable);
/*     */     }
/*     */     
/* 192 */     if (argArray == null) {
/* 193 */       return new FormattingTuple(messagePattern);
/*     */     }
/*     */     
/* 196 */     int i = 0;
/*     */ 
/*     */     
/* 199 */     StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
/*     */ 
/*     */     
/* 202 */     for (int L = 0; L < argArray.length; L++) {
/*     */       
/* 204 */       int j = messagePattern.indexOf("{}", i);
/*     */       
/* 206 */       if (j == -1) {
/*     */         
/* 208 */         if (i == 0) {
/* 209 */           return new FormattingTuple(messagePattern, argArray, throwable);
/*     */         }
/*     */         
/* 212 */         sbuf.append(messagePattern, i, messagePattern.length());
/* 213 */         return new FormattingTuple(sbuf.toString(), argArray, throwable);
/*     */       } 
/*     */       
/* 216 */       if (isEscapedDelimeter(messagePattern, j)) {
/* 217 */         if (!isDoubleEscaped(messagePattern, j)) {
/* 218 */           L--;
/* 219 */           sbuf.append(messagePattern, i, j - 1);
/* 220 */           sbuf.append('{');
/* 221 */           i = j + 1;
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 226 */           sbuf.append(messagePattern, i, j - 1);
/* 227 */           deeplyAppendParameter(sbuf, argArray[L], (Map)new HashMap<Object, Object>());
/* 228 */           i = j + 2;
/*     */         } 
/*     */       } else {
/*     */         
/* 232 */         sbuf.append(messagePattern, i, j);
/* 233 */         deeplyAppendParameter(sbuf, argArray[L], (Map)new HashMap<Object, Object>());
/* 234 */         i = j + 2;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 239 */     sbuf.append(messagePattern, i, messagePattern.length());
/* 240 */     return new FormattingTuple(sbuf.toString(), argArray, throwable);
/*     */   }
/*     */ 
/*     */   
/*     */   static final boolean isEscapedDelimeter(String messagePattern, int delimeterStartIndex) {
/* 245 */     if (delimeterStartIndex == 0) {
/* 246 */       return false;
/*     */     }
/* 248 */     char potentialEscape = messagePattern.charAt(delimeterStartIndex - 1);
/* 249 */     if (potentialEscape == '\\') {
/* 250 */       return true;
/*     */     }
/* 252 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   static final boolean isDoubleEscaped(String messagePattern, int delimeterStartIndex) {
/* 257 */     if (delimeterStartIndex >= 2 && messagePattern.charAt(delimeterStartIndex - 2) == '\\') {
/* 258 */       return true;
/*     */     }
/* 260 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Map<Object[], Object> seenMap) {
/* 266 */     if (o == null) {
/* 267 */       sbuf.append("null");
/*     */       return;
/*     */     } 
/* 270 */     if (!o.getClass().isArray()) {
/* 271 */       safeObjectAppend(sbuf, o);
/*     */ 
/*     */     
/*     */     }
/* 275 */     else if (o instanceof boolean[]) {
/* 276 */       booleanArrayAppend(sbuf, (boolean[])o);
/* 277 */     } else if (o instanceof byte[]) {
/* 278 */       byteArrayAppend(sbuf, (byte[])o);
/* 279 */     } else if (o instanceof char[]) {
/* 280 */       charArrayAppend(sbuf, (char[])o);
/* 281 */     } else if (o instanceof short[]) {
/* 282 */       shortArrayAppend(sbuf, (short[])o);
/* 283 */     } else if (o instanceof int[]) {
/* 284 */       intArrayAppend(sbuf, (int[])o);
/* 285 */     } else if (o instanceof long[]) {
/* 286 */       longArrayAppend(sbuf, (long[])o);
/* 287 */     } else if (o instanceof float[]) {
/* 288 */       floatArrayAppend(sbuf, (float[])o);
/* 289 */     } else if (o instanceof double[]) {
/* 290 */       doubleArrayAppend(sbuf, (double[])o);
/*     */     } else {
/* 292 */       objectArrayAppend(sbuf, (Object[])o, seenMap);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void safeObjectAppend(StringBuilder sbuf, Object o) {
/*     */     try {
/* 299 */       String oAsString = o.toString();
/* 300 */       sbuf.append(oAsString);
/* 301 */     } catch (Throwable t) {
/* 302 */       Util.report("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + "]", t);
/* 303 */       sbuf.append("[FAILED toString()]");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Map<Object[], Object> seenMap) {
/* 309 */     sbuf.append('[');
/* 310 */     if (!seenMap.containsKey(a)) {
/* 311 */       seenMap.put(a, null);
/* 312 */       int len = a.length;
/* 313 */       for (int i = 0; i < len; i++) {
/* 314 */         deeplyAppendParameter(sbuf, a[i], seenMap);
/* 315 */         if (i != len - 1) {
/* 316 */           sbuf.append(", ");
/*     */         }
/*     */       } 
/* 319 */       seenMap.remove(a);
/*     */     } else {
/* 321 */       sbuf.append("...");
/*     */     } 
/* 323 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
/* 327 */     sbuf.append('[');
/* 328 */     int len = a.length;
/* 329 */     for (int i = 0; i < len; i++) {
/* 330 */       sbuf.append(a[i]);
/* 331 */       if (i != len - 1)
/* 332 */         sbuf.append(", "); 
/*     */     } 
/* 334 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
/* 338 */     sbuf.append('[');
/* 339 */     int len = a.length;
/* 340 */     for (int i = 0; i < len; i++) {
/* 341 */       sbuf.append(a[i]);
/* 342 */       if (i != len - 1)
/* 343 */         sbuf.append(", "); 
/*     */     } 
/* 345 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void charArrayAppend(StringBuilder sbuf, char[] a) {
/* 349 */     sbuf.append('[');
/* 350 */     int len = a.length;
/* 351 */     for (int i = 0; i < len; i++) {
/* 352 */       sbuf.append(a[i]);
/* 353 */       if (i != len - 1)
/* 354 */         sbuf.append(", "); 
/*     */     } 
/* 356 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
/* 360 */     sbuf.append('[');
/* 361 */     int len = a.length;
/* 362 */     for (int i = 0; i < len; i++) {
/* 363 */       sbuf.append(a[i]);
/* 364 */       if (i != len - 1)
/* 365 */         sbuf.append(", "); 
/*     */     } 
/* 367 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void intArrayAppend(StringBuilder sbuf, int[] a) {
/* 371 */     sbuf.append('[');
/* 372 */     int len = a.length;
/* 373 */     for (int i = 0; i < len; i++) {
/* 374 */       sbuf.append(a[i]);
/* 375 */       if (i != len - 1)
/* 376 */         sbuf.append(", "); 
/*     */     } 
/* 378 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void longArrayAppend(StringBuilder sbuf, long[] a) {
/* 382 */     sbuf.append('[');
/* 383 */     int len = a.length;
/* 384 */     for (int i = 0; i < len; i++) {
/* 385 */       sbuf.append(a[i]);
/* 386 */       if (i != len - 1)
/* 387 */         sbuf.append(", "); 
/*     */     } 
/* 389 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
/* 393 */     sbuf.append('[');
/* 394 */     int len = a.length;
/* 395 */     for (int i = 0; i < len; i++) {
/* 396 */       sbuf.append(a[i]);
/* 397 */       if (i != len - 1)
/* 398 */         sbuf.append(", "); 
/*     */     } 
/* 400 */     sbuf.append(']');
/*     */   }
/*     */   
/*     */   private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
/* 404 */     sbuf.append('[');
/* 405 */     int len = a.length;
/* 406 */     for (int i = 0; i < len; i++) {
/* 407 */       sbuf.append(a[i]);
/* 408 */       if (i != len - 1)
/* 409 */         sbuf.append(", "); 
/*     */     } 
/* 411 */     sbuf.append(']');
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\slf4j\helpers\MessageFormatter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */