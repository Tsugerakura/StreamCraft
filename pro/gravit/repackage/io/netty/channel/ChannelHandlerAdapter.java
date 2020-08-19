/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
/*    */ import java.util.Map;
/*    */ import pro.gravit.repackage.io.netty.util.internal.InternalThreadLocalMap;
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
/*    */ public abstract class ChannelHandlerAdapter
/*    */   implements ChannelHandler
/*    */ {
/*    */   boolean added;
/*    */   
/*    */   protected void ensureNotSharable() {
/* 36 */     if (isSharable()) {
/* 37 */       throw new IllegalStateException("ChannelHandler " + getClass().getName() + " is not allowed to be shared");
/*    */     }
/*    */   }
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
/*    */   public boolean isSharable() {
/* 54 */     Class<?> clazz = getClass();
/* 55 */     Map<Class<?>, Boolean> cache = InternalThreadLocalMap.get().handlerSharableCache();
/* 56 */     Boolean sharable = cache.get(clazz);
/* 57 */     if (sharable == null) {
/* 58 */       sharable = Boolean.valueOf(clazz.isAnnotationPresent((Class)ChannelHandler.Sharable.class));
/* 59 */       cache.put(clazz, sharable);
/*    */     } 
/* 61 */     return sharable.booleanValue();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Skip
/*    */   @Deprecated
/*    */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 92 */     ctx.fireExceptionCaught(cause);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelHandlerAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */