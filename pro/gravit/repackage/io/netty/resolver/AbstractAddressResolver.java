/*     */ package pro.gravit.repackage.io.netty.resolver;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.nio.channels.UnsupportedAddressTypeException;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Promise;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.TypeParameterMatcher;
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
/*     */ public abstract class AbstractAddressResolver<T extends SocketAddress>
/*     */   implements AddressResolver<T>
/*     */ {
/*     */   private final EventExecutor executor;
/*     */   private final TypeParameterMatcher matcher;
/*     */   
/*     */   protected AbstractAddressResolver(EventExecutor executor) {
/*  46 */     this.executor = (EventExecutor)ObjectUtil.checkNotNull(executor, "executor");
/*  47 */     this.matcher = TypeParameterMatcher.find(this, AbstractAddressResolver.class, "T");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractAddressResolver(EventExecutor executor, Class<? extends T> addressType) {
/*  56 */     this.executor = (EventExecutor)ObjectUtil.checkNotNull(executor, "executor");
/*  57 */     this.matcher = TypeParameterMatcher.get(addressType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected EventExecutor executor() {
/*  65 */     return this.executor;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSupported(SocketAddress address) {
/*  70 */     return this.matcher.match(address);
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean isResolved(SocketAddress address) {
/*  75 */     if (!isSupported(address)) {
/*  76 */       throw new UnsupportedAddressTypeException();
/*     */     }
/*     */ 
/*     */     
/*  80 */     SocketAddress socketAddress = address;
/*  81 */     return doIsResolved((T)socketAddress);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean doIsResolved(T paramT);
/*     */ 
/*     */ 
/*     */   
/*     */   public final Future<T> resolve(SocketAddress address) {
/*  92 */     if (!isSupported((SocketAddress)ObjectUtil.checkNotNull(address, "address")))
/*     */     {
/*  94 */       return executor().newFailedFuture(new UnsupportedAddressTypeException());
/*     */     }
/*     */     
/*  97 */     if (isResolved(address)) {
/*     */ 
/*     */       
/* 100 */       SocketAddress socketAddress = address;
/* 101 */       return this.executor.newSucceededFuture(socketAddress);
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 106 */       SocketAddress socketAddress = address;
/* 107 */       Promise<T> promise = executor().newPromise();
/* 108 */       doResolve((T)socketAddress, promise);
/* 109 */       return (Future<T>)promise;
/* 110 */     } catch (Exception e) {
/* 111 */       return executor().newFailedFuture(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final Future<T> resolve(SocketAddress address, Promise<T> promise) {
/* 117 */     ObjectUtil.checkNotNull(address, "address");
/* 118 */     ObjectUtil.checkNotNull(promise, "promise");
/*     */     
/* 120 */     if (!isSupported(address))
/*     */     {
/* 122 */       return (Future<T>)promise.setFailure(new UnsupportedAddressTypeException());
/*     */     }
/*     */     
/* 125 */     if (isResolved(address)) {
/*     */ 
/*     */       
/* 128 */       SocketAddress socketAddress = address;
/* 129 */       return (Future<T>)promise.setSuccess(socketAddress);
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 134 */       SocketAddress socketAddress = address;
/* 135 */       doResolve((T)socketAddress, promise);
/* 136 */       return (Future<T>)promise;
/* 137 */     } catch (Exception e) {
/* 138 */       return (Future<T>)promise.setFailure(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final Future<List<T>> resolveAll(SocketAddress address) {
/* 144 */     if (!isSupported((SocketAddress)ObjectUtil.checkNotNull(address, "address")))
/*     */     {
/* 146 */       return executor().newFailedFuture(new UnsupportedAddressTypeException());
/*     */     }
/*     */     
/* 149 */     if (isResolved(address)) {
/*     */ 
/*     */       
/* 152 */       SocketAddress socketAddress = address;
/* 153 */       return this.executor.newSucceededFuture(Collections.singletonList(socketAddress));
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 158 */       SocketAddress socketAddress = address;
/* 159 */       Promise<List<T>> promise = executor().newPromise();
/* 160 */       doResolveAll((T)socketAddress, promise);
/* 161 */       return (Future<List<T>>)promise;
/* 162 */     } catch (Exception e) {
/* 163 */       return executor().newFailedFuture(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public final Future<List<T>> resolveAll(SocketAddress address, Promise<List<T>> promise) {
/* 169 */     ObjectUtil.checkNotNull(address, "address");
/* 170 */     ObjectUtil.checkNotNull(promise, "promise");
/*     */     
/* 172 */     if (!isSupported(address))
/*     */     {
/* 174 */       return (Future<List<T>>)promise.setFailure(new UnsupportedAddressTypeException());
/*     */     }
/*     */     
/* 177 */     if (isResolved(address)) {
/*     */ 
/*     */       
/* 180 */       SocketAddress socketAddress = address;
/* 181 */       return (Future<List<T>>)promise.setSuccess(Collections.singletonList(socketAddress));
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 186 */       SocketAddress socketAddress = address;
/* 187 */       doResolveAll((T)socketAddress, promise);
/* 188 */       return (Future<List<T>>)promise;
/* 189 */     } catch (Exception e) {
/* 190 */       return (Future<List<T>>)promise.setFailure(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract void doResolve(T paramT, Promise<T> paramPromise) throws Exception;
/*     */   
/*     */   protected abstract void doResolveAll(T paramT, Promise<List<T>> paramPromise) throws Exception;
/*     */   
/*     */   public void close() {}
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\resolver\AbstractAddressResolver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */