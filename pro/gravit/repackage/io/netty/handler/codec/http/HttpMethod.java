/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*     */ import pro.gravit.repackage.io.netty.util.internal.MathUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public class HttpMethod
/*     */   implements Comparable<HttpMethod>
/*     */ {
/*  36 */   public static final HttpMethod OPTIONS = new HttpMethod("OPTIONS");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   public static final HttpMethod GET = new HttpMethod("GET");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   public static final HttpMethod HEAD = new HttpMethod("HEAD");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   public static final HttpMethod POST = new HttpMethod("POST");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  62 */   public static final HttpMethod PUT = new HttpMethod("PUT");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  68 */   public static final HttpMethod PATCH = new HttpMethod("PATCH");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  74 */   public static final HttpMethod DELETE = new HttpMethod("DELETE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  80 */   public static final HttpMethod TRACE = new HttpMethod("TRACE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  86 */   public static final HttpMethod CONNECT = new HttpMethod("CONNECT");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  91 */   private static final EnumNameMap<HttpMethod> methodMap = new EnumNameMap<HttpMethod>((EnumNameMap.Node<HttpMethod>[])new EnumNameMap.Node[] { new EnumNameMap.Node<HttpMethod>(OPTIONS
/*  92 */           .toString(), OPTIONS), new EnumNameMap.Node<HttpMethod>(GET
/*  93 */           .toString(), GET), new EnumNameMap.Node<HttpMethod>(HEAD
/*  94 */           .toString(), HEAD), new EnumNameMap.Node<HttpMethod>(POST
/*  95 */           .toString(), POST), new EnumNameMap.Node<HttpMethod>(PUT
/*  96 */           .toString(), PUT), new EnumNameMap.Node<HttpMethod>(PATCH
/*  97 */           .toString(), PATCH), new EnumNameMap.Node<HttpMethod>(DELETE
/*  98 */           .toString(), DELETE), new EnumNameMap.Node<HttpMethod>(TRACE
/*  99 */           .toString(), TRACE), new EnumNameMap.Node<HttpMethod>(CONNECT
/* 100 */           .toString(), CONNECT) });
/*     */ 
/*     */ 
/*     */   
/*     */   private final AsciiString name;
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpMethod valueOf(String name) {
/* 109 */     HttpMethod result = methodMap.get(name);
/* 110 */     return (result != null) ? result : new HttpMethod(name);
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
/*     */   public HttpMethod(String name) {
/* 123 */     name = ((String)ObjectUtil.checkNotNull(name, "name")).trim();
/* 124 */     if (name.isEmpty()) {
/* 125 */       throw new IllegalArgumentException("empty name");
/*     */     }
/*     */     
/* 128 */     for (int i = 0; i < name.length(); i++) {
/* 129 */       char c = name.charAt(i);
/* 130 */       if (Character.isISOControl(c) || Character.isWhitespace(c)) {
/* 131 */         throw new IllegalArgumentException("invalid character in name");
/*     */       }
/*     */     } 
/*     */     
/* 135 */     this.name = AsciiString.cached(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String name() {
/* 142 */     return this.name.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsciiString asciiName() {
/* 149 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 154 */     return name().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 159 */     if (this == o) {
/* 160 */       return true;
/*     */     }
/* 162 */     if (!(o instanceof HttpMethod)) {
/* 163 */       return false;
/*     */     }
/*     */     
/* 166 */     HttpMethod that = (HttpMethod)o;
/* 167 */     return name().equals(that.name());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 172 */     return this.name.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(HttpMethod o) {
/* 177 */     if (o == this) {
/* 178 */       return 0;
/*     */     }
/* 180 */     return name().compareTo(o.name());
/*     */   }
/*     */   
/*     */   private static final class EnumNameMap<T> {
/*     */     private final Node<T>[] values;
/*     */     private final int valuesMask;
/*     */     
/*     */     EnumNameMap(Node<T>... nodes) {
/* 188 */       this.values = (Node<T>[])new Node[MathUtil.findNextPositivePowerOfTwo(nodes.length)];
/* 189 */       this.valuesMask = this.values.length - 1;
/* 190 */       for (Node<T> node : nodes) {
/* 191 */         int i = hashCode(node.key) & this.valuesMask;
/* 192 */         if (this.values[i] != null) {
/* 193 */           throw new IllegalArgumentException("index " + i + " collision between values: [" + (this.values[i]).key + ", " + node.key + ']');
/*     */         }
/*     */         
/* 196 */         this.values[i] = node;
/*     */       } 
/*     */     }
/*     */     
/*     */     T get(String name) {
/* 201 */       Node<T> node = this.values[hashCode(name) & this.valuesMask];
/* 202 */       return (node == null || !node.key.equals(name)) ? null : node.value;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static int hashCode(String name) {
/* 211 */       return name.hashCode() >>> 6;
/*     */     }
/*     */     
/*     */     private static final class Node<T>
/*     */     {
/*     */       final String key;
/*     */       final T value;
/*     */       
/* 219 */       Node(String key, T value) { this.key = key;
/* 220 */         this.value = value; } } } private static final class Node<T> { Node(String key, T value) { this.key = key; this.value = value; }
/*     */ 
/*     */     
/*     */     final String key;
/*     */     final T value; }
/*     */ 
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpMethod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */