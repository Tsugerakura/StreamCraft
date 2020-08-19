/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx;
/*     */ 
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
/*     */ public final class WebSocketCloseStatus
/*     */   implements Comparable<WebSocketCloseStatus>
/*     */ {
/* 173 */   public static final WebSocketCloseStatus NORMAL_CLOSURE = new WebSocketCloseStatus(1000, "Bye");
/*     */ 
/*     */   
/* 176 */   public static final WebSocketCloseStatus ENDPOINT_UNAVAILABLE = new WebSocketCloseStatus(1001, "Endpoint unavailable");
/*     */ 
/*     */   
/* 179 */   public static final WebSocketCloseStatus PROTOCOL_ERROR = new WebSocketCloseStatus(1002, "Protocol error");
/*     */ 
/*     */   
/* 182 */   public static final WebSocketCloseStatus INVALID_MESSAGE_TYPE = new WebSocketCloseStatus(1003, "Invalid message type");
/*     */ 
/*     */   
/* 185 */   public static final WebSocketCloseStatus INVALID_PAYLOAD_DATA = new WebSocketCloseStatus(1007, "Invalid payload data");
/*     */ 
/*     */   
/* 188 */   public static final WebSocketCloseStatus POLICY_VIOLATION = new WebSocketCloseStatus(1008, "Policy violation");
/*     */ 
/*     */   
/* 191 */   public static final WebSocketCloseStatus MESSAGE_TOO_BIG = new WebSocketCloseStatus(1009, "Message too big");
/*     */ 
/*     */   
/* 194 */   public static final WebSocketCloseStatus MANDATORY_EXTENSION = new WebSocketCloseStatus(1010, "Mandatory extension");
/*     */ 
/*     */   
/* 197 */   public static final WebSocketCloseStatus INTERNAL_SERVER_ERROR = new WebSocketCloseStatus(1011, "Internal server error");
/*     */ 
/*     */   
/* 200 */   public static final WebSocketCloseStatus SERVICE_RESTART = new WebSocketCloseStatus(1012, "Service Restart");
/*     */ 
/*     */   
/* 203 */   public static final WebSocketCloseStatus TRY_AGAIN_LATER = new WebSocketCloseStatus(1013, "Try Again Later");
/*     */ 
/*     */   
/* 206 */   public static final WebSocketCloseStatus BAD_GATEWAY = new WebSocketCloseStatus(1014, "Bad Gateway");
/*     */ 
/*     */   
/*     */   private final int statusCode;
/*     */ 
/*     */   
/*     */   private final String reasonText;
/*     */ 
/*     */   
/*     */   private String text;
/*     */ 
/*     */ 
/*     */   
/*     */   public WebSocketCloseStatus(int statusCode, String reasonText) {
/* 220 */     if (!isValidStatusCode(statusCode)) {
/* 221 */       throw new IllegalArgumentException("WebSocket close status code does NOT comply with RFC-6455: " + statusCode);
/*     */     }
/*     */     
/* 224 */     this.statusCode = statusCode;
/* 225 */     this.reasonText = (String)ObjectUtil.checkNotNull(reasonText, "reasonText");
/*     */   }
/*     */   
/*     */   public int code() {
/* 229 */     return this.statusCode;
/*     */   }
/*     */   
/*     */   public String reasonText() {
/* 233 */     return this.reasonText;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(WebSocketCloseStatus o) {
/* 241 */     return code() - o.code();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 249 */     if (this == o) {
/* 250 */       return true;
/*     */     }
/* 252 */     if (null == o || getClass() != o.getClass()) {
/* 253 */       return false;
/*     */     }
/*     */     
/* 256 */     WebSocketCloseStatus that = (WebSocketCloseStatus)o;
/*     */     
/* 258 */     return (this.statusCode == that.statusCode);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 263 */     return this.statusCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 268 */     String text = this.text;
/* 269 */     if (text == null)
/*     */     {
/* 271 */       this.text = text = code() + " " + reasonText();
/*     */     }
/* 273 */     return text;
/*     */   }
/*     */   
/*     */   public static boolean isValidStatusCode(int code) {
/* 277 */     return (code < 0 || (1000 <= code && code <= 1003) || (1007 <= code && code <= 1014) || 3000 <= code);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WebSocketCloseStatus valueOf(int code) {
/* 284 */     switch (code) {
/*     */       case 1000:
/* 286 */         return NORMAL_CLOSURE;
/*     */       case 1001:
/* 288 */         return ENDPOINT_UNAVAILABLE;
/*     */       case 1002:
/* 290 */         return PROTOCOL_ERROR;
/*     */       case 1003:
/* 292 */         return INVALID_MESSAGE_TYPE;
/*     */       case 1007:
/* 294 */         return INVALID_PAYLOAD_DATA;
/*     */       case 1008:
/* 296 */         return POLICY_VIOLATION;
/*     */       case 1009:
/* 298 */         return MESSAGE_TOO_BIG;
/*     */       case 1010:
/* 300 */         return MANDATORY_EXTENSION;
/*     */       case 1011:
/* 302 */         return INTERNAL_SERVER_ERROR;
/*     */       case 1012:
/* 304 */         return SERVICE_RESTART;
/*     */       case 1013:
/* 306 */         return TRY_AGAIN_LATER;
/*     */       case 1014:
/* 308 */         return BAD_GATEWAY;
/*     */     } 
/* 310 */     return new WebSocketCloseStatus(code, "Close status #" + code);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\WebSocketCloseStatus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */