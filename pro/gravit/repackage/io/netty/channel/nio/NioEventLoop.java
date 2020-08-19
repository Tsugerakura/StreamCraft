/*     */ package pro.gravit.repackage.io.netty.channel.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.nio.channels.CancelledKeyException;
/*     */ import java.nio.channels.SelectableChannel;
/*     */ import java.nio.channels.SelectionKey;
/*     */ import java.nio.channels.Selector;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoop;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoopException;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoopGroup;
/*     */ import pro.gravit.repackage.io.netty.channel.EventLoopTaskQueueFactory;
/*     */ import pro.gravit.repackage.io.netty.channel.SelectStrategy;
/*     */ import pro.gravit.repackage.io.netty.channel.SingleThreadEventLoop;
/*     */ import pro.gravit.repackage.io.netty.util.IntSupplier;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.RejectedExecutionHandler;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ReflectionUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
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
/*     */ public final class NioEventLoop
/*     */   extends SingleThreadEventLoop
/*     */ {
/*  59 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
/*     */ 
/*     */   
/*     */   private static final int CLEANUP_INTERVAL = 256;
/*     */   
/*  64 */   private static final boolean DISABLE_KEY_SET_OPTIMIZATION = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.noKeySetOptimization", false);
/*     */   
/*     */   private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
/*     */   private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
/*     */   
/*  69 */   private final IntSupplier selectNowSupplier = new IntSupplier()
/*     */     {
/*     */       public int get() throws Exception {
/*  72 */         return NioEventLoop.this.selectNow();
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   private Selector selector;
/*     */   private Selector unwrappedSelector;
/*     */   private SelectedSelectionKeySet selectedKeys;
/*     */   
/*     */   static {
/*  82 */     String key = "sun.nio.ch.bugLevel";
/*  83 */     String bugLevel = SystemPropertyUtil.get("sun.nio.ch.bugLevel");
/*  84 */     if (bugLevel == null) {
/*     */       try {
/*  86 */         AccessController.doPrivileged(new PrivilegedAction<Void>()
/*     */             {
/*     */               public Void run() {
/*  89 */                 System.setProperty("sun.nio.ch.bugLevel", "");
/*  90 */                 return null;
/*     */               }
/*     */             });
/*  93 */       } catch (SecurityException e) {
/*  94 */         logger.debug("Unable to get/set System Property: sun.nio.ch.bugLevel", e);
/*     */       } 
/*     */     }
/*     */     
/*  98 */     int selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("pro.gravit.repackage.io.netty.selectorAutoRebuildThreshold", 512);
/*  99 */     if (selectorAutoRebuildThreshold < 3) {
/* 100 */       selectorAutoRebuildThreshold = 0;
/*     */     }
/*     */     
/* 103 */     SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
/*     */     
/* 105 */     if (logger.isDebugEnabled()) {
/* 106 */       logger.debug("-Dio.netty.noKeySetOptimization: {}", Boolean.valueOf(DISABLE_KEY_SET_OPTIMIZATION));
/* 107 */       logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", Integer.valueOf(SELECTOR_AUTO_REBUILD_THRESHOLD));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final SelectorProvider provider;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long AWAKE = -1L;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final long NONE = 9223372036854775807L;
/*     */ 
/*     */ 
/*     */   
/* 127 */   private final AtomicLong nextWakeupNanos = new AtomicLong(-1L);
/*     */   
/*     */   private final SelectStrategy selectStrategy;
/*     */   
/* 131 */   private volatile int ioRatio = 50;
/*     */   
/*     */   private int cancelledKeys;
/*     */   
/*     */   private boolean needsToSelectAgain;
/*     */   
/*     */   NioEventLoop(NioEventLoopGroup parent, Executor executor, SelectorProvider selectorProvider, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler, EventLoopTaskQueueFactory queueFactory) {
/* 138 */     super((EventLoopGroup)parent, executor, false, newTaskQueue(queueFactory), newTaskQueue(queueFactory), rejectedExecutionHandler);
/*     */     
/* 140 */     this.provider = (SelectorProvider)ObjectUtil.checkNotNull(selectorProvider, "selectorProvider");
/* 141 */     this.selectStrategy = (SelectStrategy)ObjectUtil.checkNotNull(strategy, "selectStrategy");
/* 142 */     SelectorTuple selectorTuple = openSelector();
/* 143 */     this.selector = selectorTuple.selector;
/* 144 */     this.unwrappedSelector = selectorTuple.unwrappedSelector;
/*     */   }
/*     */ 
/*     */   
/*     */   private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {
/* 149 */     if (queueFactory == null) {
/* 150 */       return newTaskQueue0(DEFAULT_MAX_PENDING_TASKS);
/*     */     }
/* 152 */     return queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
/*     */   }
/*     */   
/*     */   private static final class SelectorTuple {
/*     */     final Selector unwrappedSelector;
/*     */     final Selector selector;
/*     */     
/*     */     SelectorTuple(Selector unwrappedSelector) {
/* 160 */       this.unwrappedSelector = unwrappedSelector;
/* 161 */       this.selector = unwrappedSelector;
/*     */     }
/*     */     
/*     */     SelectorTuple(Selector unwrappedSelector, Selector selector) {
/* 165 */       this.unwrappedSelector = unwrappedSelector;
/* 166 */       this.selector = selector;
/*     */     }
/*     */   }
/*     */   
/*     */   private SelectorTuple openSelector() {
/*     */     final Selector unwrappedSelector;
/*     */     try {
/* 173 */       unwrappedSelector = this.provider.openSelector();
/* 174 */     } catch (IOException e) {
/* 175 */       throw new ChannelException("failed to open a new selector", e);
/*     */     } 
/*     */     
/* 178 */     if (DISABLE_KEY_SET_OPTIMIZATION) {
/* 179 */       return new SelectorTuple(unwrappedSelector);
/*     */     }
/*     */     
/* 182 */     Object maybeSelectorImplClass = AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/*     */             try {
/* 186 */               return Class.forName("sun.nio.ch.SelectorImpl", false, 
/*     */ 
/*     */                   
/* 189 */                   PlatformDependent.getSystemClassLoader());
/* 190 */             } catch (Throwable cause) {
/* 191 */               return cause;
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 196 */     if (!(maybeSelectorImplClass instanceof Class) || 
/*     */       
/* 198 */       !((Class)maybeSelectorImplClass).isAssignableFrom(unwrappedSelector.getClass())) {
/* 199 */       if (maybeSelectorImplClass instanceof Throwable) {
/* 200 */         Throwable t = (Throwable)maybeSelectorImplClass;
/* 201 */         logger.trace("failed to instrument a special java.util.Set into: {}", unwrappedSelector, t);
/*     */       } 
/* 203 */       return new SelectorTuple(unwrappedSelector);
/*     */     } 
/*     */     
/* 206 */     final Class<?> selectorImplClass = (Class)maybeSelectorImplClass;
/* 207 */     final SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
/*     */     
/* 209 */     Object maybeException = AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/*     */             try {
/* 213 */               Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
/* 214 */               Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
/*     */               
/* 216 */               if (PlatformDependent.javaVersion() >= 9 && PlatformDependent.hasUnsafe()) {
/*     */ 
/*     */                 
/* 219 */                 long selectedKeysFieldOffset = PlatformDependent.objectFieldOffset(selectedKeysField);
/*     */                 
/* 221 */                 long publicSelectedKeysFieldOffset = PlatformDependent.objectFieldOffset(publicSelectedKeysField);
/*     */                 
/* 223 */                 if (selectedKeysFieldOffset != -1L && publicSelectedKeysFieldOffset != -1L) {
/* 224 */                   PlatformDependent.putObject(unwrappedSelector, selectedKeysFieldOffset, selectedKeySet);
/*     */                   
/* 226 */                   PlatformDependent.putObject(unwrappedSelector, publicSelectedKeysFieldOffset, selectedKeySet);
/*     */                   
/* 228 */                   return null;
/*     */                 } 
/*     */               } 
/*     */ 
/*     */               
/* 233 */               Throwable cause = ReflectionUtil.trySetAccessible(selectedKeysField, true);
/* 234 */               if (cause != null) {
/* 235 */                 return cause;
/*     */               }
/* 237 */               cause = ReflectionUtil.trySetAccessible(publicSelectedKeysField, true);
/* 238 */               if (cause != null) {
/* 239 */                 return cause;
/*     */               }
/*     */               
/* 242 */               selectedKeysField.set(unwrappedSelector, selectedKeySet);
/* 243 */               publicSelectedKeysField.set(unwrappedSelector, selectedKeySet);
/* 244 */               return null;
/* 245 */             } catch (NoSuchFieldException e) {
/* 246 */               return e;
/* 247 */             } catch (IllegalAccessException e) {
/* 248 */               return e;
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 253 */     if (maybeException instanceof Exception) {
/* 254 */       this.selectedKeys = null;
/* 255 */       Exception e = (Exception)maybeException;
/* 256 */       logger.trace("failed to instrument a special java.util.Set into: {}", unwrappedSelector, e);
/* 257 */       return new SelectorTuple(unwrappedSelector);
/*     */     } 
/* 259 */     this.selectedKeys = selectedKeySet;
/* 260 */     logger.trace("instrumented a special java.util.Set into: {}", unwrappedSelector);
/* 261 */     return new SelectorTuple(unwrappedSelector, new SelectedSelectionKeySetSelector(unwrappedSelector, selectedKeySet));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SelectorProvider selectorProvider() {
/* 269 */     return this.provider;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
/* 274 */     return newTaskQueue0(maxPendingTasks);
/*     */   }
/*     */ 
/*     */   
/*     */   private static Queue<Runnable> newTaskQueue0(int maxPendingTasks) {
/* 279 */     return (maxPendingTasks == Integer.MAX_VALUE) ? PlatformDependent.newMpscQueue() : 
/* 280 */       PlatformDependent.newMpscQueue(maxPendingTasks);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void register(final SelectableChannel ch, final int interestOps, final NioTask<?> task) {
/* 289 */     ObjectUtil.checkNotNull(ch, "ch");
/* 290 */     if (interestOps == 0) {
/* 291 */       throw new IllegalArgumentException("interestOps must be non-zero.");
/*     */     }
/* 293 */     if ((interestOps & (ch.validOps() ^ 0xFFFFFFFF)) != 0) {
/* 294 */       throw new IllegalArgumentException("invalid interestOps: " + interestOps + "(validOps: " + ch
/* 295 */           .validOps() + ')');
/*     */     }
/* 297 */     ObjectUtil.checkNotNull(task, "task");
/*     */     
/* 299 */     if (isShutdown()) {
/* 300 */       throw new IllegalStateException("event loop shut down");
/*     */     }
/*     */     
/* 303 */     if (inEventLoop()) {
/* 304 */       register0(ch, interestOps, task);
/*     */     } else {
/*     */ 
/*     */       
/*     */       try {
/* 309 */         submit(new Runnable()
/*     */             {
/*     */               public void run() {
/* 312 */                 NioEventLoop.this.register0(ch, interestOps, task);
/*     */               }
/* 314 */             }).sync();
/* 315 */       } catch (InterruptedException ignore) {
/*     */         
/* 317 */         Thread.currentThread().interrupt();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void register0(SelectableChannel ch, int interestOps, NioTask<?> task) {
/*     */     try {
/* 324 */       ch.register(this.unwrappedSelector, interestOps, task);
/* 325 */     } catch (Exception e) {
/* 326 */       throw new EventLoopException("failed to register a channel", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIoRatio() {
/* 334 */     return this.ioRatio;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIoRatio(int ioRatio) {
/* 344 */     if (ioRatio <= 0 || ioRatio > 100) {
/* 345 */       throw new IllegalArgumentException("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)");
/*     */     }
/* 347 */     this.ioRatio = ioRatio;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rebuildSelector() {
/* 355 */     if (!inEventLoop()) {
/* 356 */       execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 359 */               NioEventLoop.this.rebuildSelector0();
/*     */             }
/*     */           });
/*     */       return;
/*     */     } 
/* 364 */     rebuildSelector0();
/*     */   }
/*     */ 
/*     */   
/*     */   public int registeredChannels() {
/* 369 */     return this.selector.keys().size() - this.cancelledKeys;
/*     */   }
/*     */   private void rebuildSelector0() {
/*     */     SelectorTuple newSelectorTuple;
/* 373 */     Selector oldSelector = this.selector;
/*     */ 
/*     */     
/* 376 */     if (oldSelector == null) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/* 381 */       newSelectorTuple = openSelector();
/* 382 */     } catch (Exception e) {
/* 383 */       logger.warn("Failed to create a new Selector.", e);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 388 */     int nChannels = 0;
/* 389 */     for (SelectionKey key : oldSelector.keys()) {
/* 390 */       Object a = key.attachment();
/*     */       try {
/* 392 */         if (!key.isValid() || key.channel().keyFor(newSelectorTuple.unwrappedSelector) != null) {
/*     */           continue;
/*     */         }
/*     */         
/* 396 */         int interestOps = key.interestOps();
/* 397 */         key.cancel();
/* 398 */         SelectionKey newKey = key.channel().register(newSelectorTuple.unwrappedSelector, interestOps, a);
/* 399 */         if (a instanceof AbstractNioChannel)
/*     */         {
/* 401 */           ((AbstractNioChannel)a).selectionKey = newKey;
/*     */         }
/* 403 */         nChannels++;
/* 404 */       } catch (Exception e) {
/* 405 */         logger.warn("Failed to re-register a Channel to the new Selector.", e);
/* 406 */         if (a instanceof AbstractNioChannel) {
/* 407 */           AbstractNioChannel ch = (AbstractNioChannel)a;
/* 408 */           ch.unsafe().close(ch.unsafe().voidPromise());
/*     */           continue;
/*     */         } 
/* 411 */         NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
/* 412 */         invokeChannelUnregistered(task, key, e);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 417 */     this.selector = newSelectorTuple.selector;
/* 418 */     this.unwrappedSelector = newSelectorTuple.unwrappedSelector;
/*     */ 
/*     */     
/*     */     try {
/* 422 */       oldSelector.close();
/* 423 */     } catch (Throwable t) {
/* 424 */       if (logger.isWarnEnabled()) {
/* 425 */         logger.warn("Failed to close the old Selector.", t);
/*     */       }
/*     */     } 
/*     */     
/* 429 */     if (logger.isInfoEnabled()) {
/* 430 */       logger.info("Migrated " + nChannels + " channel(s) to the new Selector.");
/*     */     }
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
/*     */   protected void run() {
/*     */     // Byte code:
/*     */     //   0: iconst_0
/*     */     //   1: istore_1
/*     */     //   2: aload_0
/*     */     //   3: getfield selectStrategy : Lpro/gravit/repackage/io/netty/channel/SelectStrategy;
/*     */     //   6: aload_0
/*     */     //   7: getfield selectNowSupplier : Lpro/gravit/repackage/io/netty/util/IntSupplier;
/*     */     //   10: aload_0
/*     */     //   11: invokevirtual hasTasks : ()Z
/*     */     //   14: invokeinterface calculateStrategy : (Lpro/gravit/repackage/io/netty/util/IntSupplier;Z)I
/*     */     //   19: istore_2
/*     */     //   20: iload_2
/*     */     //   21: tableswitch default -> 117, -3 -> 51, -2 -> 48, -1 -> 51
/*     */     //   48: goto -> 2
/*     */     //   51: aload_0
/*     */     //   52: invokevirtual nextScheduledTaskDeadlineNanos : ()J
/*     */     //   55: lstore_3
/*     */     //   56: lload_3
/*     */     //   57: ldc2_w -1
/*     */     //   60: lcmp
/*     */     //   61: ifne -> 68
/*     */     //   64: ldc2_w 9223372036854775807
/*     */     //   67: lstore_3
/*     */     //   68: aload_0
/*     */     //   69: getfield nextWakeupNanos : Ljava/util/concurrent/atomic/AtomicLong;
/*     */     //   72: lload_3
/*     */     //   73: invokevirtual set : (J)V
/*     */     //   76: aload_0
/*     */     //   77: invokevirtual hasTasks : ()Z
/*     */     //   80: ifne -> 89
/*     */     //   83: aload_0
/*     */     //   84: lload_3
/*     */     //   85: invokespecial select : (J)I
/*     */     //   88: istore_2
/*     */     //   89: aload_0
/*     */     //   90: getfield nextWakeupNanos : Ljava/util/concurrent/atomic/AtomicLong;
/*     */     //   93: ldc2_w -1
/*     */     //   96: invokevirtual lazySet : (J)V
/*     */     //   99: goto -> 117
/*     */     //   102: astore #5
/*     */     //   104: aload_0
/*     */     //   105: getfield nextWakeupNanos : Ljava/util/concurrent/atomic/AtomicLong;
/*     */     //   108: ldc2_w -1
/*     */     //   111: invokevirtual lazySet : (J)V
/*     */     //   114: aload #5
/*     */     //   116: athrow
/*     */     //   117: goto -> 134
/*     */     //   120: astore_3
/*     */     //   121: aload_0
/*     */     //   122: invokespecial rebuildSelector0 : ()V
/*     */     //   125: iconst_0
/*     */     //   126: istore_1
/*     */     //   127: aload_3
/*     */     //   128: invokestatic handleLoopException : (Ljava/lang/Throwable;)V
/*     */     //   131: goto -> 2
/*     */     //   134: iinc #1, 1
/*     */     //   137: aload_0
/*     */     //   138: iconst_0
/*     */     //   139: putfield cancelledKeys : I
/*     */     //   142: aload_0
/*     */     //   143: iconst_0
/*     */     //   144: putfield needsToSelectAgain : Z
/*     */     //   147: aload_0
/*     */     //   148: getfield ioRatio : I
/*     */     //   151: istore_3
/*     */     //   152: iload_3
/*     */     //   153: bipush #100
/*     */     //   155: if_icmpne -> 189
/*     */     //   158: iload_2
/*     */     //   159: ifle -> 166
/*     */     //   162: aload_0
/*     */     //   163: invokespecial processSelectedKeys : ()V
/*     */     //   166: aload_0
/*     */     //   167: invokevirtual runAllTasks : ()Z
/*     */     //   170: istore #4
/*     */     //   172: goto -> 186
/*     */     //   175: astore #6
/*     */     //   177: aload_0
/*     */     //   178: invokevirtual runAllTasks : ()Z
/*     */     //   181: istore #4
/*     */     //   183: aload #6
/*     */     //   185: athrow
/*     */     //   186: goto -> 270
/*     */     //   189: iload_2
/*     */     //   190: ifle -> 263
/*     */     //   193: invokestatic nanoTime : ()J
/*     */     //   196: lstore #5
/*     */     //   198: aload_0
/*     */     //   199: invokespecial processSelectedKeys : ()V
/*     */     //   202: invokestatic nanoTime : ()J
/*     */     //   205: lload #5
/*     */     //   207: lsub
/*     */     //   208: lstore #7
/*     */     //   210: aload_0
/*     */     //   211: lload #7
/*     */     //   213: bipush #100
/*     */     //   215: iload_3
/*     */     //   216: isub
/*     */     //   217: i2l
/*     */     //   218: lmul
/*     */     //   219: iload_3
/*     */     //   220: i2l
/*     */     //   221: ldiv
/*     */     //   222: invokevirtual runAllTasks : (J)Z
/*     */     //   225: istore #4
/*     */     //   227: goto -> 260
/*     */     //   230: astore #9
/*     */     //   232: invokestatic nanoTime : ()J
/*     */     //   235: lload #5
/*     */     //   237: lsub
/*     */     //   238: lstore #10
/*     */     //   240: aload_0
/*     */     //   241: lload #10
/*     */     //   243: bipush #100
/*     */     //   245: iload_3
/*     */     //   246: isub
/*     */     //   247: i2l
/*     */     //   248: lmul
/*     */     //   249: iload_3
/*     */     //   250: i2l
/*     */     //   251: ldiv
/*     */     //   252: invokevirtual runAllTasks : (J)Z
/*     */     //   255: istore #4
/*     */     //   257: aload #9
/*     */     //   259: athrow
/*     */     //   260: goto -> 270
/*     */     //   263: aload_0
/*     */     //   264: lconst_0
/*     */     //   265: invokevirtual runAllTasks : (J)Z
/*     */     //   268: istore #4
/*     */     //   270: iload #4
/*     */     //   272: ifne -> 279
/*     */     //   275: iload_2
/*     */     //   276: ifle -> 321
/*     */     //   279: iload_1
/*     */     //   280: iconst_3
/*     */     //   281: if_icmple -> 316
/*     */     //   284: getstatic pro/gravit/repackage/io/netty/channel/nio/NioEventLoop.logger : Lpro/gravit/repackage/io/netty/util/internal/logging/InternalLogger;
/*     */     //   287: invokeinterface isDebugEnabled : ()Z
/*     */     //   292: ifeq -> 316
/*     */     //   295: getstatic pro/gravit/repackage/io/netty/channel/nio/NioEventLoop.logger : Lpro/gravit/repackage/io/netty/util/internal/logging/InternalLogger;
/*     */     //   298: ldc_w 'Selector.select() returned prematurely {} times in a row for Selector {}.'
/*     */     //   301: iload_1
/*     */     //   302: iconst_1
/*     */     //   303: isub
/*     */     //   304: invokestatic valueOf : (I)Ljava/lang/Integer;
/*     */     //   307: aload_0
/*     */     //   308: getfield selector : Ljava/nio/channels/Selector;
/*     */     //   311: invokeinterface debug : (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
/*     */     //   316: iconst_0
/*     */     //   317: istore_1
/*     */     //   318: goto -> 331
/*     */     //   321: aload_0
/*     */     //   322: iload_1
/*     */     //   323: invokespecial unexpectedSelectorWakeup : (I)Z
/*     */     //   326: ifeq -> 331
/*     */     //   329: iconst_0
/*     */     //   330: istore_1
/*     */     //   331: goto -> 392
/*     */     //   334: astore_2
/*     */     //   335: getstatic pro/gravit/repackage/io/netty/channel/nio/NioEventLoop.logger : Lpro/gravit/repackage/io/netty/util/internal/logging/InternalLogger;
/*     */     //   338: invokeinterface isDebugEnabled : ()Z
/*     */     //   343: ifeq -> 384
/*     */     //   346: getstatic pro/gravit/repackage/io/netty/channel/nio/NioEventLoop.logger : Lpro/gravit/repackage/io/netty/util/internal/logging/InternalLogger;
/*     */     //   349: new java/lang/StringBuilder
/*     */     //   352: dup
/*     */     //   353: invokespecial <init> : ()V
/*     */     //   356: ldc_w java/nio/channels/CancelledKeyException
/*     */     //   359: invokevirtual getSimpleName : ()Ljava/lang/String;
/*     */     //   362: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   365: ldc_w ' raised by a Selector {} - JDK bug?'
/*     */     //   368: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   371: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   374: aload_0
/*     */     //   375: getfield selector : Ljava/nio/channels/Selector;
/*     */     //   378: aload_2
/*     */     //   379: invokeinterface debug : (Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
/*     */     //   384: goto -> 392
/*     */     //   387: astore_2
/*     */     //   388: aload_2
/*     */     //   389: invokestatic handleLoopException : (Ljava/lang/Throwable;)V
/*     */     //   392: aload_0
/*     */     //   393: invokevirtual isShuttingDown : ()Z
/*     */     //   396: ifeq -> 411
/*     */     //   399: aload_0
/*     */     //   400: invokespecial closeAll : ()V
/*     */     //   403: aload_0
/*     */     //   404: invokevirtual confirmShutdown : ()Z
/*     */     //   407: ifeq -> 411
/*     */     //   410: return
/*     */     //   411: goto -> 2
/*     */     //   414: astore_2
/*     */     //   415: aload_2
/*     */     //   416: invokestatic handleLoopException : (Ljava/lang/Throwable;)V
/*     */     //   419: goto -> 2
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #436	-> 0
/*     */     //   #441	-> 2
/*     */     //   #442	-> 20
/*     */     //   #444	-> 48
/*     */     //   #450	-> 51
/*     */     //   #451	-> 56
/*     */     //   #452	-> 64
/*     */     //   #454	-> 68
/*     */     //   #456	-> 76
/*     */     //   #457	-> 83
/*     */     //   #462	-> 89
/*     */     //   #463	-> 99
/*     */     //   #462	-> 102
/*     */     //   #463	-> 114
/*     */     //   #474	-> 117
/*     */     //   #467	-> 120
/*     */     //   #470	-> 121
/*     */     //   #471	-> 125
/*     */     //   #472	-> 127
/*     */     //   #473	-> 131
/*     */     //   #476	-> 134
/*     */     //   #477	-> 137
/*     */     //   #478	-> 142
/*     */     //   #479	-> 147
/*     */     //   #481	-> 152
/*     */     //   #483	-> 158
/*     */     //   #484	-> 162
/*     */     //   #488	-> 166
/*     */     //   #489	-> 172
/*     */     //   #488	-> 175
/*     */     //   #489	-> 183
/*     */     //   #490	-> 189
/*     */     //   #491	-> 193
/*     */     //   #493	-> 198
/*     */     //   #496	-> 202
/*     */     //   #497	-> 210
/*     */     //   #498	-> 227
/*     */     //   #496	-> 230
/*     */     //   #497	-> 240
/*     */     //   #498	-> 257
/*     */     //   #499	-> 260
/*     */     //   #500	-> 263
/*     */     //   #503	-> 270
/*     */     //   #504	-> 279
/*     */     //   #505	-> 295
/*     */     //   #506	-> 304
/*     */     //   #505	-> 311
/*     */     //   #508	-> 316
/*     */     //   #509	-> 321
/*     */     //   #510	-> 329
/*     */     //   #520	-> 331
/*     */     //   #512	-> 334
/*     */     //   #514	-> 335
/*     */     //   #515	-> 346
/*     */     //   #520	-> 384
/*     */     //   #518	-> 387
/*     */     //   #519	-> 388
/*     */     //   #523	-> 392
/*     */     //   #524	-> 399
/*     */     //   #525	-> 403
/*     */     //   #526	-> 410
/*     */     //   #531	-> 411
/*     */     //   #529	-> 414
/*     */     //   #530	-> 415
/*     */     //   #531	-> 419
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   56	61	3	curDeadlineNanos	J
/*     */     //   20	100	2	strategy	I
/*     */     //   121	13	3	e	Ljava/io/IOException;
/*     */     //   172	3	4	ranTasks	Z
/*     */     //   183	6	4	ranTasks	Z
/*     */     //   210	17	7	ioTime	J
/*     */     //   227	3	4	ranTasks	Z
/*     */     //   240	17	10	ioTime	J
/*     */     //   198	62	5	ioStartTime	J
/*     */     //   257	6	4	ranTasks	Z
/*     */     //   134	197	2	strategy	I
/*     */     //   152	179	3	ioRatio	I
/*     */     //   270	61	4	ranTasks	Z
/*     */     //   335	49	2	e	Ljava/nio/channels/CancelledKeyException;
/*     */     //   388	4	2	t	Ljava/lang/Throwable;
/*     */     //   415	4	2	t	Ljava/lang/Throwable;
/*     */     //   0	422	0	this	Lpro/gravit/repackage/io/netty/channel/nio/NioEventLoop;
/*     */     //   2	420	1	selectCnt	I
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   2	48	120	java/io/IOException
/*     */     //   2	48	334	java/nio/channels/CancelledKeyException
/*     */     //   2	48	387	java/lang/Throwable
/*     */     //   51	117	120	java/io/IOException
/*     */     //   51	131	334	java/nio/channels/CancelledKeyException
/*     */     //   51	131	387	java/lang/Throwable
/*     */     //   76	89	102	finally
/*     */     //   102	104	102	finally
/*     */     //   134	331	334	java/nio/channels/CancelledKeyException
/*     */     //   134	331	387	java/lang/Throwable
/*     */     //   158	166	175	finally
/*     */     //   175	177	175	finally
/*     */     //   198	202	230	finally
/*     */     //   230	232	230	finally
/*     */     //   392	410	414	java/lang/Throwable
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
/*     */   private boolean unexpectedSelectorWakeup(int selectCnt) {
/* 537 */     if (Thread.interrupted()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 543 */       if (logger.isDebugEnabled()) {
/* 544 */         logger.debug("Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
/*     */       }
/*     */ 
/*     */       
/* 548 */       return true;
/*     */     } 
/* 550 */     if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
/*     */ 
/*     */ 
/*     */       
/* 554 */       logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", 
/* 555 */           Integer.valueOf(selectCnt), this.selector);
/* 556 */       rebuildSelector();
/* 557 */       return true;
/*     */     } 
/* 559 */     return false;
/*     */   }
/*     */   
/*     */   private static void handleLoopException(Throwable t) {
/* 563 */     logger.warn("Unexpected exception in the selector loop.", t);
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 568 */       Thread.sleep(1000L);
/* 569 */     } catch (InterruptedException interruptedException) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void processSelectedKeys() {
/* 575 */     if (this.selectedKeys != null) {
/* 576 */       processSelectedKeysOptimized();
/*     */     } else {
/* 578 */       processSelectedKeysPlain(this.selector.selectedKeys());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void cleanup() {
/*     */     try {
/* 585 */       this.selector.close();
/* 586 */     } catch (IOException e) {
/* 587 */       logger.warn("Failed to close a selector.", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   void cancel(SelectionKey key) {
/* 592 */     key.cancel();
/* 593 */     this.cancelledKeys++;
/* 594 */     if (this.cancelledKeys >= 256) {
/* 595 */       this.cancelledKeys = 0;
/* 596 */       this.needsToSelectAgain = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
/* 604 */     if (selectedKeys.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     
/* 608 */     Iterator<SelectionKey> i = selectedKeys.iterator();
/*     */     while (true) {
/* 610 */       SelectionKey k = i.next();
/* 611 */       Object a = k.attachment();
/* 612 */       i.remove();
/*     */       
/* 614 */       if (a instanceof AbstractNioChannel) {
/* 615 */         processSelectedKey(k, (AbstractNioChannel)a);
/*     */       } else {
/*     */         
/* 618 */         NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
/* 619 */         processSelectedKey(k, task);
/*     */       } 
/*     */       
/* 622 */       if (!i.hasNext()) {
/*     */         break;
/*     */       }
/*     */       
/* 626 */       if (this.needsToSelectAgain) {
/* 627 */         selectAgain();
/* 628 */         selectedKeys = this.selector.selectedKeys();
/*     */ 
/*     */         
/* 631 */         if (selectedKeys.isEmpty()) {
/*     */           break;
/*     */         }
/* 634 */         i = selectedKeys.iterator();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void processSelectedKeysOptimized() {
/* 641 */     for (int i = 0; i < this.selectedKeys.size; i++) {
/* 642 */       SelectionKey k = this.selectedKeys.keys[i];
/*     */ 
/*     */       
/* 645 */       this.selectedKeys.keys[i] = null;
/*     */       
/* 647 */       Object a = k.attachment();
/*     */       
/* 649 */       if (a instanceof AbstractNioChannel) {
/* 650 */         processSelectedKey(k, (AbstractNioChannel)a);
/*     */       } else {
/*     */         
/* 653 */         NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
/* 654 */         processSelectedKey(k, task);
/*     */       } 
/*     */       
/* 657 */       if (this.needsToSelectAgain) {
/*     */ 
/*     */         
/* 660 */         this.selectedKeys.reset(i + 1);
/*     */         
/* 662 */         selectAgain();
/* 663 */         i = -1;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
/* 669 */     AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();
/* 670 */     if (!k.isValid()) {
/*     */       NioEventLoop nioEventLoop;
/*     */       try {
/* 673 */         nioEventLoop = ch.eventLoop();
/* 674 */       } catch (Throwable ignored) {
/*     */         return;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 684 */       if (nioEventLoop == this)
/*     */       {
/* 686 */         unsafe.close(unsafe.voidPromise());
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/*     */     try {
/* 692 */       int readyOps = k.readyOps();
/*     */ 
/*     */       
/* 695 */       if ((readyOps & 0x8) != 0) {
/*     */ 
/*     */         
/* 698 */         int ops = k.interestOps();
/* 699 */         ops &= 0xFFFFFFF7;
/* 700 */         k.interestOps(ops);
/*     */         
/* 702 */         unsafe.finishConnect();
/*     */       } 
/*     */ 
/*     */       
/* 706 */       if ((readyOps & 0x4) != 0)
/*     */       {
/* 708 */         ch.unsafe().forceFlush();
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 713 */       if ((readyOps & 0x11) != 0 || readyOps == 0) {
/* 714 */         unsafe.read();
/*     */       }
/* 716 */     } catch (CancelledKeyException ignored) {
/* 717 */       EventLoop eventLoop; unsafe.close(unsafe.voidPromise());
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void processSelectedKey(SelectionKey k, NioTask<SelectableChannel> task) {
/* 722 */     int state = 0;
/*     */     try {
/* 724 */       task.channelReady(k.channel(), k);
/* 725 */       state = 1;
/* 726 */     } catch (Exception e) {
/* 727 */       k.cancel();
/* 728 */       invokeChannelUnregistered(task, k, e);
/* 729 */       state = 2;
/*     */     } finally {
/* 731 */       switch (state) {
/*     */         case 0:
/* 733 */           k.cancel();
/* 734 */           invokeChannelUnregistered(task, k, (Throwable)null);
/*     */           break;
/*     */         case 1:
/* 737 */           if (!k.isValid()) {
/* 738 */             invokeChannelUnregistered(task, k, (Throwable)null);
/*     */           }
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void closeAll() {
/* 746 */     selectAgain();
/* 747 */     Set<SelectionKey> keys = this.selector.keys();
/* 748 */     Collection<AbstractNioChannel> channels = new ArrayList<AbstractNioChannel>(keys.size());
/* 749 */     for (SelectionKey k : keys) {
/* 750 */       Object a = k.attachment();
/* 751 */       if (a instanceof AbstractNioChannel) {
/* 752 */         channels.add((AbstractNioChannel)a); continue;
/*     */       } 
/* 754 */       k.cancel();
/*     */       
/* 756 */       NioTask<SelectableChannel> task = (NioTask<SelectableChannel>)a;
/* 757 */       invokeChannelUnregistered(task, k, (Throwable)null);
/*     */     } 
/*     */ 
/*     */     
/* 761 */     for (AbstractNioChannel ch : channels) {
/* 762 */       ch.unsafe().close(ch.unsafe().voidPromise());
/*     */     }
/*     */   }
/*     */   
/*     */   private static void invokeChannelUnregistered(NioTask<SelectableChannel> task, SelectionKey k, Throwable cause) {
/*     */     try {
/* 768 */       task.channelUnregistered(k.channel(), cause);
/* 769 */     } catch (Exception e) {
/* 770 */       logger.warn("Unexpected exception while running NioTask.channelUnregistered()", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void wakeup(boolean inEventLoop) {
/* 776 */     if (!inEventLoop && this.nextWakeupNanos.getAndSet(-1L) != -1L) {
/* 777 */       this.selector.wakeup();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
/* 784 */     return (deadlineNanos < this.nextWakeupNanos.get());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
/* 790 */     return (deadlineNanos < this.nextWakeupNanos.get());
/*     */   }
/*     */   
/*     */   Selector unwrappedSelector() {
/* 794 */     return this.unwrappedSelector;
/*     */   }
/*     */   
/*     */   int selectNow() throws IOException {
/* 798 */     return this.selector.selectNow();
/*     */   }
/*     */   
/*     */   private int select(long deadlineNanos) throws IOException {
/* 802 */     if (deadlineNanos == Long.MAX_VALUE) {
/* 803 */       return this.selector.select();
/*     */     }
/*     */     
/* 806 */     long timeoutMillis = deadlineToDelayNanos(deadlineNanos + 995000L) / 1000000L;
/* 807 */     return (timeoutMillis <= 0L) ? this.selector.selectNow() : this.selector.select(timeoutMillis);
/*     */   }
/*     */   
/*     */   private void selectAgain() {
/* 811 */     this.needsToSelectAgain = false;
/*     */     try {
/* 813 */       this.selector.selectNow();
/* 814 */     } catch (Throwable t) {
/* 815 */       logger.warn("Failed to update SelectionKeys.", t);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\nio\NioEventLoop.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */