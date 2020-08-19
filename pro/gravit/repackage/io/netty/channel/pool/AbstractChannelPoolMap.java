/*     */ package pro.gravit.repackage.io.netty.channel.pool;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GlobalEventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Promise;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ReadOnlyIterator;
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
/*     */ public abstract class AbstractChannelPoolMap<K, P extends ChannelPool>
/*     */   implements ChannelPoolMap<K, P>, Iterable<Map.Entry<K, P>>, Closeable
/*     */ {
/*  38 */   private final ConcurrentMap<K, P> map = PlatformDependent.newConcurrentHashMap();
/*     */ 
/*     */   
/*     */   public final P get(K key) {
/*  42 */     ChannelPool channelPool = (ChannelPool)this.map.get(ObjectUtil.checkNotNull(key, "key"));
/*  43 */     if (channelPool == null) {
/*  44 */       channelPool = (ChannelPool)newPool(key);
/*  45 */       ChannelPool channelPool1 = (ChannelPool)this.map.putIfAbsent(key, (P)channelPool);
/*  46 */       if (channelPool1 != null) {
/*     */         
/*  48 */         poolCloseAsyncIfSupported(channelPool);
/*  49 */         channelPool = channelPool1;
/*     */       } 
/*     */     } 
/*  52 */     return (P)channelPool;
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
/*     */   public final boolean remove(K key) {
/*  64 */     ChannelPool channelPool = (ChannelPool)this.map.remove(ObjectUtil.checkNotNull(key, "key"));
/*  65 */     if (channelPool != null) {
/*  66 */       poolCloseAsyncIfSupported(channelPool);
/*  67 */       return true;
/*     */     } 
/*  69 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Future<Boolean> removeAsyncIfSupported(K key) {
/*  80 */     ChannelPool channelPool = (ChannelPool)this.map.remove(ObjectUtil.checkNotNull(key, "key"));
/*  81 */     if (channelPool != null) {
/*  82 */       final Promise<Boolean> removePromise = GlobalEventExecutor.INSTANCE.newPromise();
/*  83 */       poolCloseAsyncIfSupported(channelPool).addListener(new GenericFutureListener<Future<? super Void>>()
/*     */           {
/*     */             public void operationComplete(Future<? super Void> future) throws Exception {
/*  86 */               if (future.isSuccess()) {
/*  87 */                 removePromise.setSuccess(Boolean.TRUE);
/*     */               } else {
/*  89 */                 removePromise.setFailure(future.cause());
/*     */               } 
/*     */             }
/*     */           });
/*  93 */       return (Future<Boolean>)removePromise;
/*     */     } 
/*  95 */     return GlobalEventExecutor.INSTANCE.newSucceededFuture(Boolean.FALSE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Future<Void> poolCloseAsyncIfSupported(ChannelPool pool) {
/* 105 */     if (pool instanceof SimpleChannelPool) {
/* 106 */       return ((SimpleChannelPool)pool).closeAsync();
/*     */     }
/*     */     try {
/* 109 */       pool.close();
/* 110 */       return GlobalEventExecutor.INSTANCE.newSucceededFuture(null);
/* 111 */     } catch (Exception e) {
/* 112 */       return GlobalEventExecutor.INSTANCE.newFailedFuture(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final Iterator<Map.Entry<K, P>> iterator() {
/* 119 */     return (Iterator<Map.Entry<K, P>>)new ReadOnlyIterator(this.map.entrySet().iterator());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int size() {
/* 126 */     return this.map.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean isEmpty() {
/* 133 */     return this.map.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean contains(K key) {
/* 138 */     return this.map.containsKey(ObjectUtil.checkNotNull(key, "key"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract P newPool(K paramK);
/*     */ 
/*     */ 
/*     */   
/*     */   public final void close() {
/* 148 */     for (K key : this.map.keySet())
/*     */     {
/* 150 */       removeAsyncIfSupported(key).syncUninterruptibly();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\pool\AbstractChannelPoolMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */