/*     */ package pro.gravit.repackage.io.netty.resolver;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Promise;
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
/*     */ public final class CompositeNameResolver<T>
/*     */   extends SimpleNameResolver<T>
/*     */ {
/*     */   private final NameResolver<T>[] resolvers;
/*     */   
/*     */   public CompositeNameResolver(EventExecutor executor, NameResolver<T>... resolvers) {
/*  46 */     super(executor);
/*  47 */     ObjectUtil.checkNotNull(resolvers, "resolvers");
/*  48 */     for (int i = 0; i < resolvers.length; i++) {
/*  49 */       ObjectUtil.checkNotNull(resolvers[i], "resolvers[" + i + ']');
/*     */     }
/*  51 */     if (resolvers.length < 2) {
/*  52 */       throw new IllegalArgumentException("resolvers: " + Arrays.asList(resolvers) + " (expected: at least 2 resolvers)");
/*     */     }
/*     */     
/*  55 */     this.resolvers = (NameResolver<T>[])resolvers.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doResolve(String inetHost, Promise<T> promise) throws Exception {
/*  60 */     doResolveRec(inetHost, promise, 0, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doResolveRec(final String inetHost, final Promise<T> promise, final int resolverIndex, Throwable lastFailure) throws Exception {
/*  67 */     if (resolverIndex >= this.resolvers.length) {
/*  68 */       promise.setFailure(lastFailure);
/*     */     } else {
/*  70 */       NameResolver<T> resolver = this.resolvers[resolverIndex];
/*  71 */       resolver.resolve(inetHost).addListener((GenericFutureListener)new FutureListener<T>()
/*     */           {
/*     */             public void operationComplete(Future<T> future) throws Exception {
/*  74 */               if (future.isSuccess()) {
/*  75 */                 promise.setSuccess(future.getNow());
/*     */               } else {
/*  77 */                 CompositeNameResolver.this.doResolveRec(inetHost, promise, resolverIndex + 1, future.cause());
/*     */               } 
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doResolveAll(String inetHost, Promise<List<T>> promise) throws Exception {
/*  86 */     doResolveAllRec(inetHost, promise, 0, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doResolveAllRec(final String inetHost, final Promise<List<T>> promise, final int resolverIndex, Throwable lastFailure) throws Exception {
/*  93 */     if (resolverIndex >= this.resolvers.length) {
/*  94 */       promise.setFailure(lastFailure);
/*     */     } else {
/*  96 */       NameResolver<T> resolver = this.resolvers[resolverIndex];
/*  97 */       resolver.resolveAll(inetHost).addListener((GenericFutureListener)new FutureListener<List<T>>()
/*     */           {
/*     */             public void operationComplete(Future<List<T>> future) throws Exception {
/* 100 */               if (future.isSuccess()) {
/* 101 */                 promise.setSuccess(future.getNow());
/*     */               } else {
/* 103 */                 CompositeNameResolver.this.doResolveAllRec(inetHost, promise, resolverIndex + 1, future.cause());
/*     */               } 
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\resolver\CompositeNameResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */