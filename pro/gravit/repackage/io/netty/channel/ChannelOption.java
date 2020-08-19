/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*     */ import pro.gravit.repackage.io.netty.util.AbstractConstant;
/*     */ import pro.gravit.repackage.io.netty.util.Constant;
/*     */ import pro.gravit.repackage.io.netty.util.ConstantPool;
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
/*     */ public class ChannelOption<T>
/*     */   extends AbstractConstant<ChannelOption<T>>
/*     */ {
/*  36 */   private static final ConstantPool<ChannelOption<Object>> pool = new ConstantPool<ChannelOption<Object>>()
/*     */     {
/*     */       protected ChannelOption<Object> newConstant(int id, String name) {
/*  39 */         return new ChannelOption(id, name);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ChannelOption<T> valueOf(String name) {
/*  48 */     return (ChannelOption<T>)pool.valueOf(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ChannelOption<T> valueOf(Class<?> firstNameComponent, String secondNameComponent) {
/*  56 */     return (ChannelOption<T>)pool.valueOf(firstNameComponent, secondNameComponent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean exists(String name) {
/*  63 */     return pool.exists(name);
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
/*     */   public static <T> ChannelOption<T> newInstance(String name) {
/*  75 */     return (ChannelOption<T>)pool.newInstance(name);
/*     */   }
/*     */   
/*  78 */   public static final ChannelOption<ByteBufAllocator> ALLOCATOR = valueOf("ALLOCATOR");
/*  79 */   public static final ChannelOption<RecvByteBufAllocator> RCVBUF_ALLOCATOR = valueOf("RCVBUF_ALLOCATOR");
/*  80 */   public static final ChannelOption<MessageSizeEstimator> MESSAGE_SIZE_ESTIMATOR = valueOf("MESSAGE_SIZE_ESTIMATOR");
/*     */   
/*  82 */   public static final ChannelOption<Integer> CONNECT_TIMEOUT_MILLIS = valueOf("CONNECT_TIMEOUT_MILLIS");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  88 */   public static final ChannelOption<Integer> MAX_MESSAGES_PER_READ = valueOf("MAX_MESSAGES_PER_READ");
/*  89 */   public static final ChannelOption<Integer> WRITE_SPIN_COUNT = valueOf("WRITE_SPIN_COUNT");
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  94 */   public static final ChannelOption<Integer> WRITE_BUFFER_HIGH_WATER_MARK = valueOf("WRITE_BUFFER_HIGH_WATER_MARK");
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  99 */   public static final ChannelOption<Integer> WRITE_BUFFER_LOW_WATER_MARK = valueOf("WRITE_BUFFER_LOW_WATER_MARK");
/*     */   
/* 101 */   public static final ChannelOption<WriteBufferWaterMark> WRITE_BUFFER_WATER_MARK = valueOf("WRITE_BUFFER_WATER_MARK");
/*     */   
/* 103 */   public static final ChannelOption<Boolean> ALLOW_HALF_CLOSURE = valueOf("ALLOW_HALF_CLOSURE");
/* 104 */   public static final ChannelOption<Boolean> AUTO_READ = valueOf("AUTO_READ");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 110 */   public static final ChannelOption<Boolean> AUTO_CLOSE = valueOf("AUTO_CLOSE");
/*     */   
/* 112 */   public static final ChannelOption<Boolean> SO_BROADCAST = valueOf("SO_BROADCAST");
/* 113 */   public static final ChannelOption<Boolean> SO_KEEPALIVE = valueOf("SO_KEEPALIVE");
/* 114 */   public static final ChannelOption<Integer> SO_SNDBUF = valueOf("SO_SNDBUF");
/* 115 */   public static final ChannelOption<Integer> SO_RCVBUF = valueOf("SO_RCVBUF");
/* 116 */   public static final ChannelOption<Boolean> SO_REUSEADDR = valueOf("SO_REUSEADDR");
/* 117 */   public static final ChannelOption<Integer> SO_LINGER = valueOf("SO_LINGER");
/* 118 */   public static final ChannelOption<Integer> SO_BACKLOG = valueOf("SO_BACKLOG");
/* 119 */   public static final ChannelOption<Integer> SO_TIMEOUT = valueOf("SO_TIMEOUT");
/*     */   
/* 121 */   public static final ChannelOption<Integer> IP_TOS = valueOf("IP_TOS");
/* 122 */   public static final ChannelOption<InetAddress> IP_MULTICAST_ADDR = valueOf("IP_MULTICAST_ADDR");
/* 123 */   public static final ChannelOption<NetworkInterface> IP_MULTICAST_IF = valueOf("IP_MULTICAST_IF");
/* 124 */   public static final ChannelOption<Integer> IP_MULTICAST_TTL = valueOf("IP_MULTICAST_TTL");
/* 125 */   public static final ChannelOption<Boolean> IP_MULTICAST_LOOP_DISABLED = valueOf("IP_MULTICAST_LOOP_DISABLED");
/*     */   
/* 127 */   public static final ChannelOption<Boolean> TCP_NODELAY = valueOf("TCP_NODELAY");
/*     */ 
/*     */   
/*     */   @Deprecated
/* 131 */   public static final ChannelOption<Boolean> DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION = valueOf("DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION");
/*     */ 
/*     */   
/* 134 */   public static final ChannelOption<Boolean> SINGLE_EVENTEXECUTOR_PER_GROUP = valueOf("SINGLE_EVENTEXECUTOR_PER_GROUP");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ChannelOption(int id, String name) {
/* 140 */     super(id, name);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   protected ChannelOption(String name) {
/* 145 */     this(pool.nextId(), name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void validate(T value) {
/* 153 */     ObjectUtil.checkNotNull(value, "value");
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ChannelOption.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */