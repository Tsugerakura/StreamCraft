/*    */ package pro.gravit.repackage.io.netty.handler.codec.marshalling;
/*    */ 
/*    */ import org.jboss.marshalling.Marshaller;
/*    */ import org.jboss.marshalling.MarshallerFactory;
/*    */ import org.jboss.marshalling.MarshallingConfiguration;
/*    */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*    */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
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
/*    */ public class ThreadLocalMarshallerProvider
/*    */   implements MarshallerProvider
/*    */ {
/* 31 */   private final FastThreadLocal<Marshaller> marshallers = new FastThreadLocal();
/*    */ 
/*    */ 
/*    */   
/*    */   private final MarshallerFactory factory;
/*    */ 
/*    */   
/*    */   private final MarshallingConfiguration config;
/*    */ 
/*    */ 
/*    */   
/*    */   public ThreadLocalMarshallerProvider(MarshallerFactory factory, MarshallingConfiguration config) {
/* 43 */     this.factory = factory;
/* 44 */     this.config = config;
/*    */   }
/*    */ 
/*    */   
/*    */   public Marshaller getMarshaller(ChannelHandlerContext ctx) throws Exception {
/* 49 */     Marshaller marshaller = (Marshaller)this.marshallers.get();
/* 50 */     if (marshaller == null) {
/* 51 */       marshaller = this.factory.createMarshaller(this.config);
/* 52 */       this.marshallers.set(marshaller);
/*    */     } 
/* 54 */     return marshaller;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\marshalling\ThreadLocalMarshallerProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */