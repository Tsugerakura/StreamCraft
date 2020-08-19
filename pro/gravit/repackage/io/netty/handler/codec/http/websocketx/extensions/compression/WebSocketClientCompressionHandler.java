/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.compression;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandler;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
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
/*    */ @Sharable
/*    */ public final class WebSocketClientCompressionHandler
/*    */   extends WebSocketClientExtensionHandler
/*    */ {
/* 30 */   public static final WebSocketClientCompressionHandler INSTANCE = new WebSocketClientCompressionHandler();
/*    */   
/*    */   private WebSocketClientCompressionHandler() {
/* 33 */     super(new WebSocketClientExtensionHandshaker[] { new PerMessageDeflateClientExtensionHandshaker(), new DeflateFrameClientExtensionHandshaker(false), new DeflateFrameClientExtensionHandshaker(true) });
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\websocketx\extensions\compression\WebSocketClientCompressionHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */