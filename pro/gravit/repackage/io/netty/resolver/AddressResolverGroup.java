/*     */ package pro.gravit.repackage.io.netty.resolver;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AddressResolverGroup<T extends SocketAddress>
/*     */   implements Closeable
/*     */ {
/*  39 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AddressResolverGroup.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   private final Map<EventExecutor, AddressResolver<T>> resolvers = new IdentityHashMap<EventExecutor, AddressResolver<T>>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AddressResolver<T> getResolver(final EventExecutor executor) {
/*     */     AddressResolver<T> r;
/*  56 */     ObjectUtil.checkNotNull(executor, "executor");
/*     */     
/*  58 */     if (executor.isShuttingDown()) {
/*  59 */       throw new IllegalStateException("executor not accepting a task");
/*     */     }
/*     */ 
/*     */     
/*  63 */     synchronized (this.resolvers) {
/*  64 */       r = this.resolvers.get(executor);
/*  65 */       if (r == null) {
/*     */         final AddressResolver<T> newResolver;
/*     */         try {
/*  68 */           newResolver = newResolver(executor);
/*  69 */         } catch (Exception e) {
/*  70 */           throw new IllegalStateException("failed to create a new resolver", e);
/*     */         } 
/*     */         
/*  73 */         this.resolvers.put(executor, newResolver);
/*  74 */         executor.terminationFuture().addListener((GenericFutureListener)new FutureListener<Object>()
/*     */             {
/*     */               public void operationComplete(Future<Object> future) throws Exception {
/*  77 */                 synchronized (AddressResolverGroup.this.resolvers) {
/*  78 */                   AddressResolverGroup.this.resolvers.remove(executor);
/*     */                 } 
/*  80 */                 newResolver.close();
/*     */               }
/*     */             });
/*     */         
/*  84 */         r = newResolver;
/*     */       } 
/*     */     } 
/*     */     
/*  88 */     return r;
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
/*     */   public void close() {
/*     */     AddressResolver[] arrayOfAddressResolver;
/* 103 */     synchronized (this.resolvers) {
/* 104 */       arrayOfAddressResolver = (AddressResolver[])this.resolvers.values().toArray((Object[])new AddressResolver[0]);
/* 105 */       this.resolvers.clear();
/*     */     } 
/*     */     
/* 108 */     for (AddressResolver<T> r : arrayOfAddressResolver) {
/*     */       try {
/* 110 */         r.close();
/* 111 */       } catch (Throwable t) {
/* 112 */         logger.warn("Failed to close a resolver:", t);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract AddressResolver<T> newResolver(EventExecutor paramEventExecutor) throws Exception;
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\resolver\AddressResolverGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */