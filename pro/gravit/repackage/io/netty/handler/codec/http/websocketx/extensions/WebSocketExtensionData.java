/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.Map;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class WebSocketExtensionData
/*    */ {
/*    */   private final String name;
/*    */   private final Map<String, String> parameters;
/*    */   
/*    */   public WebSocketExtensionData(String name, Map<String, String> parameters) {
/* 34 */     this.name = (String)ObjectUtil.checkNotNull(name, "name");
/* 35 */     this.parameters = Collections.unmodifiableMap(
/* 36 */         (Map<? extends String, ? extends String>)ObjectUtil.checkNotNull(parameters, "parameters"));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String name() {
/* 43 */     return this.name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<String, String> parameters() {
/* 50 */     return this.parameters;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\WebSocketExtensionData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */