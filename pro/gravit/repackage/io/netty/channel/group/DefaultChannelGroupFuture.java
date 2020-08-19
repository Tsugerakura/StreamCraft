/*     */ package pro.gravit.repackage.io.netty.channel.group;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.BlockingOperationException;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.DefaultPromise;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.ImmediateEventExecutor;
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
/*     */ final class DefaultChannelGroupFuture
/*     */   extends DefaultPromise<Void>
/*     */   implements ChannelGroupFuture
/*     */ {
/*     */   private final ChannelGroup group;
/*     */   private final Map<Channel, ChannelFuture> futures;
/*     */   private int successCount;
/*     */   private int failureCount;
/*     */   
/*  48 */   private final ChannelFutureListener childListener = new ChannelFutureListener()
/*     */     {
/*     */       public void operationComplete(ChannelFuture future) throws Exception {
/*  51 */         boolean callSetDone, success = future.isSuccess();
/*     */         
/*  53 */         synchronized (DefaultChannelGroupFuture.this) {
/*  54 */           if (success) {
/*  55 */             DefaultChannelGroupFuture.this.successCount++;
/*     */           } else {
/*  57 */             DefaultChannelGroupFuture.this.failureCount++;
/*     */           } 
/*     */           
/*  60 */           callSetDone = (DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount == DefaultChannelGroupFuture.this.futures.size());
/*  61 */           assert DefaultChannelGroupFuture.this.successCount + DefaultChannelGroupFuture.this.failureCount <= DefaultChannelGroupFuture.this.futures.size();
/*     */         } 
/*     */         
/*  64 */         if (callSetDone) {
/*  65 */           if (DefaultChannelGroupFuture.this.failureCount > 0) {
/*     */             
/*  67 */             List<Map.Entry<Channel, Throwable>> failed = new ArrayList<Map.Entry<Channel, Throwable>>(DefaultChannelGroupFuture.this.failureCount);
/*  68 */             for (ChannelFuture f : DefaultChannelGroupFuture.this.futures.values()) {
/*  69 */               if (!f.isSuccess()) {
/*  70 */                 failed.add(new DefaultChannelGroupFuture.DefaultEntry<Channel, Throwable>(f.channel(), f.cause()));
/*     */               }
/*     */             } 
/*  73 */             DefaultChannelGroupFuture.this.setFailure0(new ChannelGroupException(failed));
/*     */           } else {
/*  75 */             DefaultChannelGroupFuture.this.setSuccess0();
/*     */           } 
/*     */         }
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   DefaultChannelGroupFuture(ChannelGroup group, Collection<ChannelFuture> futures, EventExecutor executor) {
/*  85 */     super(executor);
/*  86 */     this.group = (ChannelGroup)ObjectUtil.checkNotNull(group, "group");
/*  87 */     ObjectUtil.checkNotNull(futures, "futures");
/*     */     
/*  89 */     Map<Channel, ChannelFuture> futureMap = new LinkedHashMap<Channel, ChannelFuture>();
/*  90 */     for (ChannelFuture f : futures) {
/*  91 */       futureMap.put(f.channel(), f);
/*     */     }
/*     */     
/*  94 */     this.futures = Collections.unmodifiableMap(futureMap);
/*     */     
/*  96 */     for (ChannelFuture f : this.futures.values()) {
/*  97 */       f.addListener((GenericFutureListener)this.childListener);
/*     */     }
/*     */ 
/*     */     
/* 101 */     if (this.futures.isEmpty()) {
/* 102 */       setSuccess0();
/*     */     }
/*     */   }
/*     */   
/*     */   DefaultChannelGroupFuture(ChannelGroup group, Map<Channel, ChannelFuture> futures, EventExecutor executor) {
/* 107 */     super(executor);
/* 108 */     this.group = group;
/* 109 */     this.futures = Collections.unmodifiableMap(futures);
/* 110 */     for (ChannelFuture f : this.futures.values()) {
/* 111 */       f.addListener((GenericFutureListener)this.childListener);
/*     */     }
/*     */ 
/*     */     
/* 115 */     if (this.futures.isEmpty()) {
/* 116 */       setSuccess0();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroup group() {
/* 122 */     return this.group;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelFuture find(Channel channel) {
/* 127 */     return this.futures.get(channel);
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<ChannelFuture> iterator() {
/* 132 */     return this.futures.values().iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean isPartialSuccess() {
/* 137 */     return (this.successCount != 0 && this.successCount != this.futures.size());
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized boolean isPartialFailure() {
/* 142 */     return (this.failureCount != 0 && this.failureCount != this.futures.size());
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultChannelGroupFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/* 147 */     super.addListener(listener);
/* 148 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultChannelGroupFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/* 153 */     super.addListeners((GenericFutureListener[])listeners);
/* 154 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultChannelGroupFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener) {
/* 159 */     super.removeListener(listener);
/* 160 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultChannelGroupFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners) {
/* 166 */     super.removeListeners((GenericFutureListener[])listeners);
/* 167 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultChannelGroupFuture await() throws InterruptedException {
/* 172 */     super.await();
/* 173 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultChannelGroupFuture awaitUninterruptibly() {
/* 178 */     super.awaitUninterruptibly();
/* 179 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultChannelGroupFuture syncUninterruptibly() {
/* 184 */     super.syncUninterruptibly();
/* 185 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultChannelGroupFuture sync() throws InterruptedException {
/* 190 */     super.sync();
/* 191 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupException cause() {
/* 196 */     return (ChannelGroupException)super.cause();
/*     */   }
/*     */   
/*     */   private void setSuccess0() {
/* 200 */     super.setSuccess(null);
/*     */   }
/*     */   
/*     */   private void setFailure0(ChannelGroupException cause) {
/* 204 */     super.setFailure((Throwable)cause);
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultChannelGroupFuture setSuccess(Void result) {
/* 209 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean trySuccess(Void result) {
/* 214 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultChannelGroupFuture setFailure(Throwable cause) {
/* 219 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean tryFailure(Throwable cause) {
/* 224 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void checkDeadLock() {
/* 229 */     EventExecutor e = executor();
/* 230 */     if (e != null && e != ImmediateEventExecutor.INSTANCE && e.inEventLoop())
/* 231 */       throw new BlockingOperationException(); 
/*     */   }
/*     */   
/*     */   private static final class DefaultEntry<K, V>
/*     */     implements Map.Entry<K, V> {
/*     */     private final K key;
/*     */     private final V value;
/*     */     
/*     */     DefaultEntry(K key, V value) {
/* 240 */       this.key = key;
/* 241 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public K getKey() {
/* 246 */       return this.key;
/*     */     }
/*     */ 
/*     */     
/*     */     public V getValue() {
/* 251 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public V setValue(V value) {
/* 256 */       throw new UnsupportedOperationException("read-only");
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\group\DefaultChannelGroupFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */