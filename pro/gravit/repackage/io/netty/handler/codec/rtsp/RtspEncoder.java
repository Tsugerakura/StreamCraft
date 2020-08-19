/*    */ package pro.gravit.repackage.io.netty.handler.codec.rtsp;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.UnsupportedMessageTypeException;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMessage;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpObjectEncoder;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponse;
/*    */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*    */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*    */ 
/*    */ public class RtspEncoder
/*    */   extends HttpObjectEncoder<HttpMessage>
/*    */ {
/*    */   private static final int CRLF_SHORT = 3338;
/*    */   
/*    */   public boolean acceptOutboundMessage(Object msg) throws Exception {
/* 41 */     return (super.acceptOutboundMessage(msg) && (msg instanceof HttpRequest || msg instanceof HttpResponse));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void encodeInitialLine(ByteBuf buf, HttpMessage message) throws Exception {
/* 47 */     if (message instanceof HttpRequest) {
/* 48 */       HttpRequest request = (HttpRequest)message;
/* 49 */       ByteBufUtil.copy(request.method().asciiName(), buf);
/* 50 */       buf.writeByte(32);
/* 51 */       buf.writeCharSequence(request.uri(), CharsetUtil.UTF_8);
/* 52 */       buf.writeByte(32);
/* 53 */       buf.writeCharSequence(request.protocolVersion().toString(), CharsetUtil.US_ASCII);
/* 54 */       ByteBufUtil.writeShortBE(buf, 3338);
/* 55 */     } else if (message instanceof HttpResponse) {
/* 56 */       HttpResponse response = (HttpResponse)message;
/* 57 */       buf.writeCharSequence(response.protocolVersion().toString(), CharsetUtil.US_ASCII);
/* 58 */       buf.writeByte(32);
/* 59 */       ByteBufUtil.copy(response.status().codeAsText(), buf);
/* 60 */       buf.writeByte(32);
/* 61 */       buf.writeCharSequence(response.status().reasonPhrase(), CharsetUtil.US_ASCII);
/* 62 */       ByteBufUtil.writeShortBE(buf, 3338);
/*    */     } else {
/* 64 */       throw new UnsupportedMessageTypeException("Unsupported type " + 
/* 65 */           StringUtil.simpleClassName(message));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\rtsp\RtspEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */