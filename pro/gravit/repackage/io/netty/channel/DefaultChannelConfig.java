/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultChannelConfig
/*     */   implements ChannelConfig
/*     */ {
/*  47 */   private static final MessageSizeEstimator DEFAULT_MSG_SIZE_ESTIMATOR = DefaultMessageSizeEstimator.DEFAULT;
/*     */ 
/*     */   
/*     */   private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
/*     */   
/*  52 */   private static final AtomicIntegerFieldUpdater<DefaultChannelConfig> AUTOREAD_UPDATER = AtomicIntegerFieldUpdater.newUpdater(DefaultChannelConfig.class, "autoRead");
/*     */   
/*  54 */   private static final AtomicReferenceFieldUpdater<DefaultChannelConfig, WriteBufferWaterMark> WATERMARK_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultChannelConfig.class, WriteBufferWaterMark.class, "writeBufferWaterMark");
/*     */ 
/*     */   
/*     */   protected final Channel channel;
/*     */   
/*  59 */   private volatile ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
/*     */   private volatile RecvByteBufAllocator rcvBufAllocator;
/*  61 */   private volatile MessageSizeEstimator msgSizeEstimator = DEFAULT_MSG_SIZE_ESTIMATOR;
/*     */   
/*  63 */   private volatile int connectTimeoutMillis = 30000;
/*  64 */   private volatile int writeSpinCount = 16;
/*  65 */   private volatile int autoRead = 1;
/*     */   
/*     */   private volatile boolean autoClose = true;
/*  68 */   private volatile WriteBufferWaterMark writeBufferWaterMark = WriteBufferWaterMark.DEFAULT;
/*     */   private volatile boolean pinEventExecutor = true;
/*     */   
/*     */   public DefaultChannelConfig(Channel channel) {
/*  72 */     this(channel, new AdaptiveRecvByteBufAllocator());
/*     */   }
/*     */   
/*     */   protected DefaultChannelConfig(Channel channel, RecvByteBufAllocator allocator) {
/*  76 */     setRecvByteBufAllocator(allocator, channel.metadata());
/*  77 */     this.channel = channel;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<ChannelOption<?>, Object> getOptions() {
/*  83 */     return getOptions(null, (ChannelOption<?>[])new ChannelOption[] { ChannelOption.CONNECT_TIMEOUT_MILLIS, ChannelOption.MAX_MESSAGES_PER_READ, ChannelOption.WRITE_SPIN_COUNT, ChannelOption.ALLOCATOR, ChannelOption.AUTO_READ, ChannelOption.AUTO_CLOSE, ChannelOption.RCVBUF_ALLOCATOR, ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, ChannelOption.WRITE_BUFFER_WATER_MARK, ChannelOption.MESSAGE_SIZE_ESTIMATOR, ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<ChannelOption<?>, Object> getOptions(Map<ChannelOption<?>, Object> result, ChannelOption<?>... options) {
/*  93 */     if (result == null) {
/*  94 */       result = new IdentityHashMap<ChannelOption<?>, Object>();
/*     */     }
/*  96 */     for (ChannelOption<?> o : options) {
/*  97 */       result.put(o, getOption(o));
/*     */     }
/*  99 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setOptions(Map<ChannelOption<?>, ?> options) {
/* 105 */     ObjectUtil.checkNotNull(options, "options");
/*     */     
/* 107 */     boolean setAllOptions = true;
/* 108 */     for (Map.Entry<ChannelOption<?>, ?> e : options.entrySet()) {
/* 109 */       if (!setOption(e.getKey(), e.getValue())) {
/* 110 */         setAllOptions = false;
/*     */       }
/*     */     } 
/*     */     
/* 114 */     return setAllOptions;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T getOption(ChannelOption<T> option) {
/* 120 */     ObjectUtil.checkNotNull(option, "option");
/*     */     
/* 122 */     if (option == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
/* 123 */       return (T)Integer.valueOf(getConnectTimeoutMillis());
/*     */     }
/* 125 */     if (option == ChannelOption.MAX_MESSAGES_PER_READ) {
/* 126 */       return (T)Integer.valueOf(getMaxMessagesPerRead());
/*     */     }
/* 128 */     if (option == ChannelOption.WRITE_SPIN_COUNT) {
/* 129 */       return (T)Integer.valueOf(getWriteSpinCount());
/*     */     }
/* 131 */     if (option == ChannelOption.ALLOCATOR) {
/* 132 */       return (T)getAllocator();
/*     */     }
/* 134 */     if (option == ChannelOption.RCVBUF_ALLOCATOR) {
/* 135 */       return (T)getRecvByteBufAllocator();
/*     */     }
/* 137 */     if (option == ChannelOption.AUTO_READ) {
/* 138 */       return (T)Boolean.valueOf(isAutoRead());
/*     */     }
/* 140 */     if (option == ChannelOption.AUTO_CLOSE) {
/* 141 */       return (T)Boolean.valueOf(isAutoClose());
/*     */     }
/* 143 */     if (option == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
/* 144 */       return (T)Integer.valueOf(getWriteBufferHighWaterMark());
/*     */     }
/* 146 */     if (option == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
/* 147 */       return (T)Integer.valueOf(getWriteBufferLowWaterMark());
/*     */     }
/* 149 */     if (option == ChannelOption.WRITE_BUFFER_WATER_MARK) {
/* 150 */       return (T)getWriteBufferWaterMark();
/*     */     }
/* 152 */     if (option == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
/* 153 */       return (T)getMessageSizeEstimator();
/*     */     }
/* 155 */     if (option == ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP) {
/* 156 */       return (T)Boolean.valueOf(getPinEventExecutorPerGroup());
/*     */     }
/* 158 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> boolean setOption(ChannelOption<T> option, T value) {
/* 164 */     validate(option, value);
/*     */     
/* 166 */     if (option == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
/* 167 */       setConnectTimeoutMillis(((Integer)value).intValue());
/* 168 */     } else if (option == ChannelOption.MAX_MESSAGES_PER_READ) {
/* 169 */       setMaxMessagesPerRead(((Integer)value).intValue());
/* 170 */     } else if (option == ChannelOption.WRITE_SPIN_COUNT) {
/* 171 */       setWriteSpinCount(((Integer)value).intValue());
/* 172 */     } else if (option == ChannelOption.ALLOCATOR) {
/* 173 */       setAllocator((ByteBufAllocator)value);
/* 174 */     } else if (option == ChannelOption.RCVBUF_ALLOCATOR) {
/* 175 */       setRecvByteBufAllocator((RecvByteBufAllocator)value);
/* 176 */     } else if (option == ChannelOption.AUTO_READ) {
/* 177 */       setAutoRead(((Boolean)value).booleanValue());
/* 178 */     } else if (option == ChannelOption.AUTO_CLOSE) {
/* 179 */       setAutoClose(((Boolean)value).booleanValue());
/* 180 */     } else if (option == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
/* 181 */       setWriteBufferHighWaterMark(((Integer)value).intValue());
/* 182 */     } else if (option == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
/* 183 */       setWriteBufferLowWaterMark(((Integer)value).intValue());
/* 184 */     } else if (option == ChannelOption.WRITE_BUFFER_WATER_MARK) {
/* 185 */       setWriteBufferWaterMark((WriteBufferWaterMark)value);
/* 186 */     } else if (option == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
/* 187 */       setMessageSizeEstimator((MessageSizeEstimator)value);
/* 188 */     } else if (option == ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP) {
/* 189 */       setPinEventExecutorPerGroup(((Boolean)value).booleanValue());
/*     */     } else {
/* 191 */       return false;
/*     */     } 
/*     */     
/* 194 */     return true;
/*     */   }
/*     */   
/*     */   protected <T> void validate(ChannelOption<T> option, T value) {
/* 198 */     ((ChannelOption<T>)ObjectUtil.checkNotNull(option, "option")).validate(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getConnectTimeoutMillis() {
/* 203 */     return this.connectTimeoutMillis;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
/* 208 */     ObjectUtil.checkPositiveOrZero(connectTimeoutMillis, "connectTimeoutMillis");
/* 209 */     this.connectTimeoutMillis = connectTimeoutMillis;
/* 210 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getMaxMessagesPerRead() {
/*     */     try {
/* 223 */       MaxMessagesRecvByteBufAllocator allocator = getRecvByteBufAllocator();
/* 224 */       return allocator.maxMessagesPerRead();
/* 225 */     } catch (ClassCastException e) {
/* 226 */       throw new IllegalStateException("getRecvByteBufAllocator() must return an object of type MaxMessagesRecvByteBufAllocator", e);
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
/*     */   @Deprecated
/*     */   public ChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
/*     */     try {
/* 241 */       MaxMessagesRecvByteBufAllocator allocator = getRecvByteBufAllocator();
/* 242 */       allocator.maxMessagesPerRead(maxMessagesPerRead);
/* 243 */       return this;
/* 244 */     } catch (ClassCastException e) {
/* 245 */       throw new IllegalStateException("getRecvByteBufAllocator() must return an object of type MaxMessagesRecvByteBufAllocator", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWriteSpinCount() {
/* 252 */     return this.writeSpinCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelConfig setWriteSpinCount(int writeSpinCount) {
/* 257 */     ObjectUtil.checkPositive(writeSpinCount, "writeSpinCount");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 262 */     if (writeSpinCount == Integer.MAX_VALUE) {
/* 263 */       writeSpinCount--;
/*     */     }
/* 265 */     this.writeSpinCount = writeSpinCount;
/* 266 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufAllocator getAllocator() {
/* 271 */     return this.allocator;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelConfig setAllocator(ByteBufAllocator allocator) {
/* 276 */     this.allocator = (ByteBufAllocator)ObjectUtil.checkNotNull(allocator, "allocator");
/* 277 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public <T extends RecvByteBufAllocator> T getRecvByteBufAllocator() {
/* 283 */     return (T)this.rcvBufAllocator;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
/* 288 */     this.rcvBufAllocator = (RecvByteBufAllocator)ObjectUtil.checkNotNull(allocator, "allocator");
/* 289 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setRecvByteBufAllocator(RecvByteBufAllocator allocator, ChannelMetadata metadata) {
/* 299 */     if (allocator instanceof MaxMessagesRecvByteBufAllocator) {
/* 300 */       ((MaxMessagesRecvByteBufAllocator)allocator).maxMessagesPerRead(metadata.defaultMaxMessagesPerRead());
/* 301 */     } else if (allocator == null) {
/* 302 */       throw new NullPointerException("allocator");
/*     */     } 
/* 304 */     setRecvByteBufAllocator(allocator);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAutoRead() {
/* 309 */     return (this.autoRead == 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelConfig setAutoRead(boolean autoRead) {
/* 314 */     boolean oldAutoRead = (AUTOREAD_UPDATER.getAndSet(this, autoRead ? 1 : 0) == 1);
/* 315 */     if (autoRead && !oldAutoRead) {
/* 316 */       this.channel.read();
/* 317 */     } else if (!autoRead && oldAutoRead) {
/* 318 */       autoReadCleared();
/*     */     } 
/* 320 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void autoReadCleared() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAutoClose() {
/* 331 */     return this.autoClose;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelConfig setAutoClose(boolean autoClose) {
/* 336 */     this.autoClose = autoClose;
/* 337 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWriteBufferHighWaterMark() {
/* 342 */     return this.writeBufferWaterMark.high();
/*     */   }
/*     */   
/*     */   public ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
/*     */     WriteBufferWaterMark waterMark;
/* 347 */     ObjectUtil.checkPositiveOrZero(writeBufferHighWaterMark, "writeBufferHighWaterMark");
/*     */     do {
/* 349 */       waterMark = this.writeBufferWaterMark;
/* 350 */       if (writeBufferHighWaterMark < waterMark.low()) {
/* 351 */         throw new IllegalArgumentException("writeBufferHighWaterMark cannot be less than writeBufferLowWaterMark (" + waterMark
/*     */             
/* 353 */             .low() + "): " + writeBufferHighWaterMark);
/*     */       }
/*     */     }
/* 356 */     while (!WATERMARK_UPDATER.compareAndSet(this, waterMark, new WriteBufferWaterMark(waterMark
/* 357 */           .low(), writeBufferHighWaterMark, false)));
/* 358 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWriteBufferLowWaterMark() {
/* 365 */     return this.writeBufferWaterMark.low();
/*     */   }
/*     */   
/*     */   public ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
/*     */     WriteBufferWaterMark waterMark;
/* 370 */     ObjectUtil.checkPositiveOrZero(writeBufferLowWaterMark, "writeBufferLowWaterMark");
/*     */     do {
/* 372 */       waterMark = this.writeBufferWaterMark;
/* 373 */       if (writeBufferLowWaterMark > waterMark.high()) {
/* 374 */         throw new IllegalArgumentException("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark (" + waterMark
/*     */             
/* 376 */             .high() + "): " + writeBufferLowWaterMark);
/*     */       }
/*     */     }
/* 379 */     while (!WATERMARK_UPDATER.compareAndSet(this, waterMark, new WriteBufferWaterMark(writeBufferLowWaterMark, waterMark
/* 380 */           .high(), false)));
/* 381 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
/* 388 */     this.writeBufferWaterMark = (WriteBufferWaterMark)ObjectUtil.checkNotNull(writeBufferWaterMark, "writeBufferWaterMark");
/* 389 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public WriteBufferWaterMark getWriteBufferWaterMark() {
/* 394 */     return this.writeBufferWaterMark;
/*     */   }
/*     */ 
/*     */   
/*     */   public MessageSizeEstimator getMessageSizeEstimator() {
/* 399 */     return this.msgSizeEstimator;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
/* 404 */     this.msgSizeEstimator = (MessageSizeEstimator)ObjectUtil.checkNotNull(estimator, "estimator");
/* 405 */     return this;
/*     */   }
/*     */   
/*     */   private ChannelConfig setPinEventExecutorPerGroup(boolean pinEventExecutor) {
/* 409 */     this.pinEventExecutor = pinEventExecutor;
/* 410 */     return this;
/*     */   }
/*     */   
/*     */   private boolean getPinEventExecutorPerGroup() {
/* 414 */     return this.pinEventExecutor;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultChannelConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */