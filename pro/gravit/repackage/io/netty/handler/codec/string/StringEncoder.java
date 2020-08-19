/*    */ package pro.gravit.repackage.io.netty.handler.codec.string;
/*    */ 
/*    */ import java.nio.CharBuffer;
/*    */ import java.nio.charset.Charset;
/*    */ import java.util.List;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.MessageToMessageEncoder;
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
/*    */ @Sharable
/*    */ public class StringEncoder
/*    */   extends MessageToMessageEncoder<CharSequence>
/*    */ {
/*    */   private final Charset charset;
/*    */   
/*    */   public StringEncoder() {
/* 61 */     this(Charset.defaultCharset());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public StringEncoder(Charset charset) {
/* 68 */     this.charset = (Charset)ObjectUtil.checkNotNull(charset, "charset");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
/* 73 */     if (msg.length() == 0) {
/*    */       return;
/*    */     }
/*    */     
/* 77 */     out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg), this.charset));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\string\StringEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */