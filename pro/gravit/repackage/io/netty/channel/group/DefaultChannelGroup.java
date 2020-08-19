/*     */ package pro.gravit.repackage.io.netty.channel.group;
/*     */ 
/*     */ import java.util.AbstractSet;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.channel.Channel;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFuture;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelFutureListener;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelId;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.EventExecutor;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.Future;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.GenericFutureListener;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ public class DefaultChannelGroup
/*     */   extends AbstractSet<Channel>
/*     */   implements ChannelGroup
/*     */ {
/*  45 */   private static final AtomicInteger nextId = new AtomicInteger();
/*     */   private final String name;
/*     */   private final EventExecutor executor;
/*  48 */   private final ConcurrentMap<ChannelId, Channel> serverChannels = PlatformDependent.newConcurrentHashMap();
/*  49 */   private final ConcurrentMap<ChannelId, Channel> nonServerChannels = PlatformDependent.newConcurrentHashMap();
/*  50 */   private final ChannelFutureListener remover = new ChannelFutureListener()
/*     */     {
/*     */       public void operationComplete(ChannelFuture future) throws Exception {
/*  53 */         DefaultChannelGroup.this.remove(future.channel());
/*     */       }
/*     */     };
/*  56 */   private final VoidChannelGroupFuture voidFuture = new VoidChannelGroupFuture(this);
/*     */ 
/*     */   
/*     */   private final boolean stayClosed;
/*     */   
/*     */   private volatile boolean closed;
/*     */ 
/*     */   
/*     */   public DefaultChannelGroup(EventExecutor executor) {
/*  65 */     this(executor, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultChannelGroup(String name, EventExecutor executor) {
/*  74 */     this(name, executor, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultChannelGroup(EventExecutor executor, boolean stayClosed) {
/*  84 */     this("group-0x" + Integer.toHexString(nextId.incrementAndGet()), executor, stayClosed);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultChannelGroup(String name, EventExecutor executor, boolean stayClosed) {
/*  95 */     ObjectUtil.checkNotNull(name, "name");
/*  96 */     this.name = name;
/*  97 */     this.executor = executor;
/*  98 */     this.stayClosed = stayClosed;
/*     */   }
/*     */ 
/*     */   
/*     */   public String name() {
/* 103 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public Channel find(ChannelId id) {
/* 108 */     Channel c = this.nonServerChannels.get(id);
/* 109 */     if (c != null) {
/* 110 */       return c;
/*     */     }
/* 112 */     return this.serverChannels.get(id);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 118 */     return (this.nonServerChannels.isEmpty() && this.serverChannels.isEmpty());
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 123 */     return this.nonServerChannels.size() + this.serverChannels.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean contains(Object o) {
/* 128 */     if (o instanceof pro.gravit.repackage.io.netty.channel.ServerChannel)
/* 129 */       return this.serverChannels.containsValue(o); 
/* 130 */     if (o instanceof Channel) {
/* 131 */       return this.nonServerChannels.containsValue(o);
/*     */     }
/* 133 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(Channel channel) {
/* 138 */     ConcurrentMap<ChannelId, Channel> map = (channel instanceof pro.gravit.repackage.io.netty.channel.ServerChannel) ? this.serverChannels : this.nonServerChannels;
/*     */ 
/*     */     
/* 141 */     boolean added = (map.putIfAbsent(channel.id(), channel) == null);
/* 142 */     if (added) {
/* 143 */       channel.closeFuture().addListener((GenericFutureListener)this.remover);
/*     */     }
/*     */     
/* 146 */     if (this.stayClosed && this.closed)
/*     */     {
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
/* 159 */       channel.close();
/*     */     }
/*     */     
/* 162 */     return added;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(Object o) {
/* 167 */     Channel c = null;
/* 168 */     if (o instanceof ChannelId) {
/* 169 */       c = this.nonServerChannels.remove(o);
/* 170 */       if (c == null) {
/* 171 */         c = this.serverChannels.remove(o);
/*     */       }
/* 173 */     } else if (o instanceof Channel) {
/* 174 */       c = (Channel)o;
/* 175 */       if (c instanceof pro.gravit.repackage.io.netty.channel.ServerChannel) {
/* 176 */         c = this.serverChannels.remove(c.id());
/*     */       } else {
/* 178 */         c = this.nonServerChannels.remove(c.id());
/*     */       } 
/*     */     } 
/*     */     
/* 182 */     if (c == null) {
/* 183 */       return false;
/*     */     }
/*     */     
/* 186 */     c.closeFuture().removeListener((GenericFutureListener)this.remover);
/* 187 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 192 */     this.nonServerChannels.clear();
/* 193 */     this.serverChannels.clear();
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Channel> iterator() {
/* 198 */     return new CombinedIterator<Channel>(this.serverChannels
/* 199 */         .values().iterator(), this.nonServerChannels
/* 200 */         .values().iterator());
/*     */   }
/*     */ 
/*     */   
/*     */   public Object[] toArray() {
/* 205 */     Collection<Channel> channels = new ArrayList<Channel>(size());
/* 206 */     channels.addAll(this.serverChannels.values());
/* 207 */     channels.addAll(this.nonServerChannels.values());
/* 208 */     return channels.toArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T[] toArray(T[] a) {
/* 213 */     Collection<Channel> channels = new ArrayList<Channel>(size());
/* 214 */     channels.addAll(this.serverChannels.values());
/* 215 */     channels.addAll(this.nonServerChannels.values());
/* 216 */     return channels.toArray(a);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture close() {
/* 221 */     return close(ChannelMatchers.all());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture disconnect() {
/* 226 */     return disconnect(ChannelMatchers.all());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture deregister() {
/* 231 */     return deregister(ChannelMatchers.all());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture write(Object message) {
/* 236 */     return write(message, ChannelMatchers.all());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Object safeDuplicate(Object message) {
/* 242 */     if (message instanceof ByteBuf)
/* 243 */       return ((ByteBuf)message).retainedDuplicate(); 
/* 244 */     if (message instanceof ByteBufHolder) {
/* 245 */       return ((ByteBufHolder)message).retainedDuplicate();
/*     */     }
/* 247 */     return ReferenceCountUtil.retain(message);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture write(Object message, ChannelMatcher matcher) {
/* 253 */     return write(message, matcher, false);
/*     */   }
/*     */   
/*     */   public ChannelGroupFuture write(Object message, ChannelMatcher matcher, boolean voidPromise) {
/*     */     ChannelGroupFuture future;
/* 258 */     ObjectUtil.checkNotNull(message, "message");
/* 259 */     ObjectUtil.checkNotNull(matcher, "matcher");
/*     */ 
/*     */     
/* 262 */     if (voidPromise) {
/* 263 */       for (Channel c : this.nonServerChannels.values()) {
/* 264 */         if (matcher.matches(c)) {
/* 265 */           c.write(safeDuplicate(message), c.voidPromise());
/*     */         }
/*     */       } 
/* 268 */       future = this.voidFuture;
/*     */     } else {
/* 270 */       Map<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(size());
/* 271 */       for (Channel c : this.nonServerChannels.values()) {
/* 272 */         if (matcher.matches(c)) {
/* 273 */           futures.put(c, c.write(safeDuplicate(message)));
/*     */         }
/*     */       } 
/* 276 */       future = new DefaultChannelGroupFuture(this, futures, this.executor);
/*     */     } 
/* 278 */     ReferenceCountUtil.release(message);
/* 279 */     return future;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroup flush() {
/* 284 */     return flush(ChannelMatchers.all());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture flushAndWrite(Object message) {
/* 289 */     return writeAndFlush(message);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture writeAndFlush(Object message) {
/* 294 */     return writeAndFlush(message, ChannelMatchers.all());
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture disconnect(ChannelMatcher matcher) {
/* 299 */     ObjectUtil.checkNotNull(matcher, "matcher");
/*     */ 
/*     */     
/* 302 */     Map<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(size());
/*     */     
/* 304 */     for (Channel c : this.serverChannels.values()) {
/* 305 */       if (matcher.matches(c)) {
/* 306 */         futures.put(c, c.disconnect());
/*     */       }
/*     */     } 
/* 309 */     for (Channel c : this.nonServerChannels.values()) {
/* 310 */       if (matcher.matches(c)) {
/* 311 */         futures.put(c, c.disconnect());
/*     */       }
/*     */     } 
/*     */     
/* 315 */     return new DefaultChannelGroupFuture(this, futures, this.executor);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture close(ChannelMatcher matcher) {
/* 320 */     ObjectUtil.checkNotNull(matcher, "matcher");
/*     */ 
/*     */     
/* 323 */     Map<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(size());
/*     */     
/* 325 */     if (this.stayClosed)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 332 */       this.closed = true;
/*     */     }
/*     */     
/* 335 */     for (Channel c : this.serverChannels.values()) {
/* 336 */       if (matcher.matches(c)) {
/* 337 */         futures.put(c, c.close());
/*     */       }
/*     */     } 
/* 340 */     for (Channel c : this.nonServerChannels.values()) {
/* 341 */       if (matcher.matches(c)) {
/* 342 */         futures.put(c, c.close());
/*     */       }
/*     */     } 
/*     */     
/* 346 */     return new DefaultChannelGroupFuture(this, futures, this.executor);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture deregister(ChannelMatcher matcher) {
/* 351 */     ObjectUtil.checkNotNull(matcher, "matcher");
/*     */ 
/*     */     
/* 354 */     Map<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(size());
/*     */     
/* 356 */     for (Channel c : this.serverChannels.values()) {
/* 357 */       if (matcher.matches(c)) {
/* 358 */         futures.put(c, c.deregister());
/*     */       }
/*     */     } 
/* 361 */     for (Channel c : this.nonServerChannels.values()) {
/* 362 */       if (matcher.matches(c)) {
/* 363 */         futures.put(c, c.deregister());
/*     */       }
/*     */     } 
/*     */     
/* 367 */     return new DefaultChannelGroupFuture(this, futures, this.executor);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroup flush(ChannelMatcher matcher) {
/* 372 */     for (Channel c : this.nonServerChannels.values()) {
/* 373 */       if (matcher.matches(c)) {
/* 374 */         c.flush();
/*     */       }
/*     */     } 
/* 377 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture flushAndWrite(Object message, ChannelMatcher matcher) {
/* 382 */     return writeAndFlush(message, matcher);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher) {
/* 387 */     return writeAndFlush(message, matcher, false);
/*     */   }
/*     */   
/*     */   public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher, boolean voidPromise) {
/*     */     ChannelGroupFuture future;
/* 392 */     ObjectUtil.checkNotNull(message, "message");
/*     */ 
/*     */     
/* 395 */     if (voidPromise) {
/* 396 */       for (Channel c : this.nonServerChannels.values()) {
/* 397 */         if (matcher.matches(c)) {
/* 398 */           c.writeAndFlush(safeDuplicate(message), c.voidPromise());
/*     */         }
/*     */       } 
/* 401 */       future = this.voidFuture;
/*     */     } else {
/* 403 */       Map<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(size());
/* 404 */       for (Channel c : this.nonServerChannels.values()) {
/* 405 */         if (matcher.matches(c)) {
/* 406 */           futures.put(c, c.writeAndFlush(safeDuplicate(message)));
/*     */         }
/*     */       } 
/* 409 */       future = new DefaultChannelGroupFuture(this, futures, this.executor);
/*     */     } 
/* 411 */     ReferenceCountUtil.release(message);
/* 412 */     return future;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture newCloseFuture() {
/* 417 */     return newCloseFuture(ChannelMatchers.all());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelGroupFuture newCloseFuture(ChannelMatcher matcher) {
/* 423 */     Map<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(size());
/*     */     
/* 425 */     for (Channel c : this.serverChannels.values()) {
/* 426 */       if (matcher.matches(c)) {
/* 427 */         futures.put(c, c.closeFuture());
/*     */       }
/*     */     } 
/* 430 */     for (Channel c : this.nonServerChannels.values()) {
/* 431 */       if (matcher.matches(c)) {
/* 432 */         futures.put(c, c.closeFuture());
/*     */       }
/*     */     } 
/*     */     
/* 436 */     return new DefaultChannelGroupFuture(this, futures, this.executor);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 441 */     return System.identityHashCode(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 446 */     return (this == o);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(ChannelGroup o) {
/* 451 */     int v = name().compareTo(o.name());
/* 452 */     if (v != 0) {
/* 453 */       return v;
/*     */     }
/*     */     
/* 456 */     return System.identityHashCode(this) - System.identityHashCode(o);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 461 */     return StringUtil.simpleClassName(this) + "(name: " + name() + ", size: " + size() + ')';
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\group\DefaultChannelGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */