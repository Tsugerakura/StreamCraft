/*    */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*    */ 
/*    */ import java.util.ArrayDeque;
/*    */ import java.util.List;
/*    */ import java.util.Queue;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.MessageToMessageCodec;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMessage;
/*    */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
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
/*    */ public class SpdyHttpResponseStreamIdHandler
/*    */   extends MessageToMessageCodec<Object, HttpMessage>
/*    */ {
/* 35 */   private static final Integer NO_ID = Integer.valueOf(-1);
/* 36 */   private final Queue<Integer> ids = new ArrayDeque<Integer>();
/*    */ 
/*    */   
/*    */   public boolean acceptInboundMessage(Object msg) throws Exception {
/* 40 */     return (msg instanceof HttpMessage || msg instanceof SpdyRstStreamFrame);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void encode(ChannelHandlerContext ctx, HttpMessage msg, List<Object> out) throws Exception {
/* 45 */     Integer id = this.ids.poll();
/* 46 */     if (id != null && id.intValue() != NO_ID.intValue() && !msg.headers().contains((CharSequence)SpdyHttpHeaders.Names.STREAM_ID)) {
/* 47 */       msg.headers().setInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID, id.intValue());
/*    */     }
/*    */     
/* 50 */     out.add(ReferenceCountUtil.retain(msg));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
/* 55 */     if (msg instanceof HttpMessage) {
/* 56 */       boolean contains = ((HttpMessage)msg).headers().contains((CharSequence)SpdyHttpHeaders.Names.STREAM_ID);
/* 57 */       if (!contains) {
/* 58 */         this.ids.add(NO_ID);
/*    */       } else {
/* 60 */         this.ids.add(((HttpMessage)msg).headers().getInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID));
/*    */       } 
/* 62 */     } else if (msg instanceof SpdyRstStreamFrame) {
/* 63 */       this.ids.remove(Integer.valueOf(((SpdyRstStreamFrame)msg).streamId()));
/*    */     } 
/*    */     
/* 66 */     out.add(ReferenceCountUtil.retain(msg));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyHttpResponseStreamIdHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */