/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
/*     */ import pro.gravit.repackage.io.netty.util.internal.MacAddressUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ 
/*     */ 
/*     */ public final class DefaultChannelId
/*     */   implements ChannelId
/*     */ {
/*     */   private static final long serialVersionUID = 3884076183504074063L;
/*  41 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelId.class);
/*     */   
/*     */   private static final byte[] MACHINE_ID;
/*     */   private static final int PROCESS_ID_LEN = 4;
/*     */   private static final int PROCESS_ID;
/*     */   private static final int SEQUENCE_LEN = 4;
/*     */   private static final int TIMESTAMP_LEN = 8;
/*     */   private static final int RANDOM_LEN = 4;
/*  49 */   private static final AtomicInteger nextSequence = new AtomicInteger(); private final byte[] data;
/*     */   private final int hashCode;
/*     */   private transient String shortValue;
/*     */   private transient String longValue;
/*     */   
/*     */   public static DefaultChannelId newInstance() {
/*  55 */     return new DefaultChannelId();
/*     */   }
/*     */   
/*     */   static {
/*  59 */     int processId = -1;
/*  60 */     String customProcessId = SystemPropertyUtil.get("pro.gravit.repackage.io.netty.processId");
/*  61 */     if (customProcessId != null) {
/*     */       try {
/*  63 */         processId = Integer.parseInt(customProcessId);
/*  64 */       } catch (NumberFormatException numberFormatException) {}
/*     */ 
/*     */ 
/*     */       
/*  68 */       if (processId < 0) {
/*  69 */         processId = -1;
/*  70 */         logger.warn("-Dio.netty.processId: {} (malformed)", customProcessId);
/*  71 */       } else if (logger.isDebugEnabled()) {
/*  72 */         logger.debug("-Dio.netty.processId: {} (user-set)", Integer.valueOf(processId));
/*     */       } 
/*     */     } 
/*     */     
/*  76 */     if (processId < 0) {
/*  77 */       processId = defaultProcessId();
/*  78 */       if (logger.isDebugEnabled()) {
/*  79 */         logger.debug("-Dio.netty.processId: {} (auto-detected)", Integer.valueOf(processId));
/*     */       }
/*     */     } 
/*     */     
/*  83 */     PROCESS_ID = processId;
/*     */     
/*  85 */     byte[] machineId = null;
/*  86 */     String customMachineId = SystemPropertyUtil.get("pro.gravit.repackage.io.netty.machineId");
/*  87 */     if (customMachineId != null) {
/*     */       try {
/*  89 */         machineId = MacAddressUtil.parseMAC(customMachineId);
/*  90 */       } catch (Exception e) {
/*  91 */         logger.warn("-Dio.netty.machineId: {} (malformed)", customMachineId, e);
/*     */       } 
/*  93 */       if (machineId != null) {
/*  94 */         logger.debug("-Dio.netty.machineId: {} (user-set)", customMachineId);
/*     */       }
/*     */     } 
/*     */     
/*  98 */     if (machineId == null) {
/*  99 */       machineId = MacAddressUtil.defaultMachineId();
/* 100 */       if (logger.isDebugEnabled()) {
/* 101 */         logger.debug("-Dio.netty.machineId: {} (auto-detected)", MacAddressUtil.formatAddress(machineId));
/*     */       }
/*     */     } 
/*     */     
/* 105 */     MACHINE_ID = machineId;
/*     */   } private static int defaultProcessId() {
/*     */     String value;
/*     */     int pid;
/* 109 */     ClassLoader loader = null;
/*     */     
/*     */     try {
/* 112 */       loader = PlatformDependent.getClassLoader(DefaultChannelId.class);
/*     */       
/* 114 */       Class<?> mgmtFactoryType = Class.forName("java.lang.management.ManagementFactory", true, loader);
/* 115 */       Class<?> runtimeMxBeanType = Class.forName("java.lang.management.RuntimeMXBean", true, loader);
/*     */       
/* 117 */       Method getRuntimeMXBean = mgmtFactoryType.getMethod("getRuntimeMXBean", EmptyArrays.EMPTY_CLASSES);
/* 118 */       Object bean = getRuntimeMXBean.invoke(null, EmptyArrays.EMPTY_OBJECTS);
/* 119 */       Method getName = runtimeMxBeanType.getMethod("getName", EmptyArrays.EMPTY_CLASSES);
/* 120 */       value = (String)getName.invoke(bean, EmptyArrays.EMPTY_OBJECTS);
/* 121 */     } catch (Throwable t) {
/* 122 */       logger.debug("Could not invoke ManagementFactory.getRuntimeMXBean().getName(); Android?", t);
/*     */       
/*     */       try {
/* 125 */         Class<?> processType = Class.forName("android.os.Process", true, loader);
/* 126 */         Method myPid = processType.getMethod("myPid", EmptyArrays.EMPTY_CLASSES);
/* 127 */         value = myPid.invoke(null, EmptyArrays.EMPTY_OBJECTS).toString();
/* 128 */       } catch (Throwable t2) {
/* 129 */         logger.debug("Could not invoke Process.myPid(); not Android?", t2);
/* 130 */         value = "";
/*     */       } 
/*     */     } 
/*     */     
/* 134 */     int atIndex = value.indexOf('@');
/* 135 */     if (atIndex >= 0) {
/* 136 */       value = value.substring(0, atIndex);
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 141 */       pid = Integer.parseInt(value);
/* 142 */     } catch (NumberFormatException e) {
/*     */       
/* 144 */       pid = -1;
/*     */     } 
/*     */     
/* 147 */     if (pid < 0) {
/* 148 */       pid = PlatformDependent.threadLocalRandom().nextInt();
/* 149 */       logger.warn("Failed to find the current process ID from '{}'; using a random value: {}", value, Integer.valueOf(pid));
/*     */     } 
/*     */     
/* 152 */     return pid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private DefaultChannelId() {
/* 162 */     this.data = new byte[MACHINE_ID.length + 4 + 4 + 8 + 4];
/* 163 */     int i = 0;
/*     */ 
/*     */     
/* 166 */     System.arraycopy(MACHINE_ID, 0, this.data, i, MACHINE_ID.length);
/* 167 */     i += MACHINE_ID.length;
/*     */ 
/*     */     
/* 170 */     i = writeInt(i, PROCESS_ID);
/*     */ 
/*     */     
/* 173 */     i = writeInt(i, nextSequence.getAndIncrement());
/*     */ 
/*     */     
/* 176 */     i = writeLong(i, Long.reverse(System.nanoTime()) ^ System.currentTimeMillis());
/*     */ 
/*     */     
/* 179 */     int random = PlatformDependent.threadLocalRandom().nextInt();
/* 180 */     i = writeInt(i, random);
/* 181 */     assert i == this.data.length;
/*     */     
/* 183 */     this.hashCode = Arrays.hashCode(this.data);
/*     */   }
/*     */   
/*     */   private int writeInt(int i, int value) {
/* 187 */     this.data[i++] = (byte)(value >>> 24);
/* 188 */     this.data[i++] = (byte)(value >>> 16);
/* 189 */     this.data[i++] = (byte)(value >>> 8);
/* 190 */     this.data[i++] = (byte)value;
/* 191 */     return i;
/*     */   }
/*     */   
/*     */   private int writeLong(int i, long value) {
/* 195 */     this.data[i++] = (byte)(int)(value >>> 56L);
/* 196 */     this.data[i++] = (byte)(int)(value >>> 48L);
/* 197 */     this.data[i++] = (byte)(int)(value >>> 40L);
/* 198 */     this.data[i++] = (byte)(int)(value >>> 32L);
/* 199 */     this.data[i++] = (byte)(int)(value >>> 24L);
/* 200 */     this.data[i++] = (byte)(int)(value >>> 16L);
/* 201 */     this.data[i++] = (byte)(int)(value >>> 8L);
/* 202 */     this.data[i++] = (byte)(int)value;
/* 203 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public String asShortText() {
/* 208 */     String shortValue = this.shortValue;
/* 209 */     if (shortValue == null) {
/* 210 */       this.shortValue = shortValue = ByteBufUtil.hexDump(this.data, this.data.length - 4, 4);
/*     */     }
/* 212 */     return shortValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public String asLongText() {
/* 217 */     String longValue = this.longValue;
/* 218 */     if (longValue == null) {
/* 219 */       this.longValue = longValue = newLongValue();
/*     */     }
/* 221 */     return longValue;
/*     */   }
/*     */   
/*     */   private String newLongValue() {
/* 225 */     StringBuilder buf = new StringBuilder(2 * this.data.length + 5);
/* 226 */     int i = 0;
/* 227 */     i = appendHexDumpField(buf, i, MACHINE_ID.length);
/* 228 */     i = appendHexDumpField(buf, i, 4);
/* 229 */     i = appendHexDumpField(buf, i, 4);
/* 230 */     i = appendHexDumpField(buf, i, 8);
/* 231 */     i = appendHexDumpField(buf, i, 4);
/* 232 */     assert i == this.data.length;
/* 233 */     return buf.substring(0, buf.length() - 1);
/*     */   }
/*     */   
/*     */   private int appendHexDumpField(StringBuilder buf, int i, int length) {
/* 237 */     buf.append(ByteBufUtil.hexDump(this.data, i, length));
/* 238 */     buf.append('-');
/* 239 */     i += length;
/* 240 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 245 */     return this.hashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(ChannelId o) {
/* 250 */     if (this == o)
/*     */     {
/* 252 */       return 0;
/*     */     }
/* 254 */     if (o instanceof DefaultChannelId) {
/*     */       
/* 256 */       byte[] otherData = ((DefaultChannelId)o).data;
/* 257 */       int len1 = this.data.length;
/* 258 */       int len2 = otherData.length;
/* 259 */       int len = Math.min(len1, len2);
/*     */       
/* 261 */       for (int k = 0; k < len; k++) {
/* 262 */         byte x = this.data[k];
/* 263 */         byte y = otherData[k];
/* 264 */         if (x != y)
/*     */         {
/* 266 */           return (x & 0xFF) - (y & 0xFF);
/*     */         }
/*     */       } 
/* 269 */       return len1 - len2;
/*     */     } 
/*     */     
/* 272 */     return asLongText().compareTo(o.asLongText());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 277 */     if (this == obj) {
/* 278 */       return true;
/*     */     }
/* 280 */     if (!(obj instanceof DefaultChannelId)) {
/* 281 */       return false;
/*     */     }
/* 283 */     DefaultChannelId other = (DefaultChannelId)obj;
/* 284 */     return (this.hashCode == other.hashCode && Arrays.equals(this.data, other.data));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 289 */     return asShortText();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultChannelId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */