/*     */ package pro.gravit.repackage.io.netty.handler.logging;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelDuplexHandler;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogLevel;
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
/*     */ @Sharable
/*     */ public class LoggingHandler
/*     */   extends ChannelDuplexHandler
/*     */ {
/*  44 */   private static final LogLevel DEFAULT_LEVEL = LogLevel.DEBUG;
/*     */ 
/*     */   
/*     */   protected final InternalLogger logger;
/*     */ 
/*     */   
/*     */   protected final InternalLogLevel internalLevel;
/*     */   
/*     */   private final LogLevel level;
/*     */ 
/*     */   
/*     */   public LoggingHandler() {
/*  56 */     this(DEFAULT_LEVEL);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LoggingHandler(LogLevel level) {
/*  66 */     this.level = (LogLevel)ObjectUtil.checkNotNull(level, "level");
/*  67 */     this.logger = InternalLoggerFactory.getInstance(getClass());
/*  68 */     this.internalLevel = level.toInternalLevel();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LoggingHandler(Class<?> clazz) {
/*  78 */     this(clazz, DEFAULT_LEVEL);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LoggingHandler(Class<?> clazz, LogLevel level) {
/*  88 */     ObjectUtil.checkNotNull(clazz, "clazz");
/*  89 */     this.level = (LogLevel)ObjectUtil.checkNotNull(level, "level");
/*  90 */     this.logger = InternalLoggerFactory.getInstance(clazz);
/*  91 */     this.internalLevel = level.toInternalLevel();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LoggingHandler(String name) {
/* 100 */     this(name, DEFAULT_LEVEL);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LoggingHandler(String name, LogLevel level) {
/* 110 */     ObjectUtil.checkNotNull(name, "name");
/*     */     
/* 112 */     this.level = (LogLevel)ObjectUtil.checkNotNull(level, "level");
/* 113 */     this.logger = InternalLoggerFactory.getInstance(name);
/* 114 */     this.internalLevel = level.toInternalLevel();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LogLevel level() {
/* 121 */     return this.level;
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
/* 126 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 127 */       this.logger.log(this.internalLevel, format(ctx, "REGISTERED"));
/*     */     }
/* 129 */     ctx.fireChannelRegistered();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
/* 134 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 135 */       this.logger.log(this.internalLevel, format(ctx, "UNREGISTERED"));
/*     */     }
/* 137 */     ctx.fireChannelUnregistered();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelActive(ChannelHandlerContext ctx) throws Exception {
/* 142 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 143 */       this.logger.log(this.internalLevel, format(ctx, "ACTIVE"));
/*     */     }
/* 145 */     ctx.fireChannelActive();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
/* 150 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 151 */       this.logger.log(this.internalLevel, format(ctx, "INACTIVE"));
/*     */     }
/* 153 */     ctx.fireChannelInactive();
/*     */   }
/*     */ 
/*     */   
/*     */   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
/* 158 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 159 */       this.logger.log(this.internalLevel, format(ctx, "EXCEPTION", cause), cause);
/*     */     }
/* 161 */     ctx.fireExceptionCaught(cause);
/*     */   }
/*     */ 
/*     */   
/*     */   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
/* 166 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 167 */       this.logger.log(this.internalLevel, format(ctx, "USER_EVENT", evt));
/*     */     }
/* 169 */     ctx.fireUserEventTriggered(evt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/* 174 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 175 */       this.logger.log(this.internalLevel, format(ctx, "BIND", localAddress));
/*     */     }
/* 177 */     ctx.bind(localAddress, promise);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
/* 184 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 185 */       this.logger.log(this.internalLevel, format(ctx, "CONNECT", remoteAddress, localAddress));
/*     */     }
/* 187 */     ctx.connect(remoteAddress, localAddress, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 192 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 193 */       this.logger.log(this.internalLevel, format(ctx, "DISCONNECT"));
/*     */     }
/* 195 */     ctx.disconnect(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 200 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 201 */       this.logger.log(this.internalLevel, format(ctx, "CLOSE"));
/*     */     }
/* 203 */     ctx.close(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
/* 208 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 209 */       this.logger.log(this.internalLevel, format(ctx, "DEREGISTER"));
/*     */     }
/* 211 */     ctx.deregister(promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
/* 216 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 217 */       this.logger.log(this.internalLevel, format(ctx, "READ COMPLETE"));
/*     */     }
/* 219 */     ctx.fireChannelReadComplete();
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
/* 224 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 225 */       this.logger.log(this.internalLevel, format(ctx, "READ", msg));
/*     */     }
/* 227 */     ctx.fireChannelRead(msg);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
/* 232 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 233 */       this.logger.log(this.internalLevel, format(ctx, "WRITE", msg));
/*     */     }
/* 235 */     ctx.write(msg, promise);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
/* 240 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 241 */       this.logger.log(this.internalLevel, format(ctx, "WRITABILITY CHANGED"));
/*     */     }
/* 243 */     ctx.fireChannelWritabilityChanged();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush(ChannelHandlerContext ctx) throws Exception {
/* 248 */     if (this.logger.isEnabled(this.internalLevel)) {
/* 249 */       this.logger.log(this.internalLevel, format(ctx, "FLUSH"));
/*     */     }
/* 251 */     ctx.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String format(ChannelHandlerContext ctx, String eventName) {
/* 260 */     String chStr = ctx.channel().toString();
/* 261 */     return (new StringBuilder(chStr.length() + 1 + eventName.length()))
/* 262 */       .append(chStr)
/* 263 */       .append(' ')
/* 264 */       .append(eventName)
/* 265 */       .toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String format(ChannelHandlerContext ctx, String eventName, Object arg) {
/* 275 */     if (arg instanceof ByteBuf)
/* 276 */       return formatByteBuf(ctx, eventName, (ByteBuf)arg); 
/* 277 */     if (arg instanceof ByteBufHolder) {
/* 278 */       return formatByteBufHolder(ctx, eventName, (ByteBufHolder)arg);
/*     */     }
/* 280 */     return formatSimple(ctx, eventName, arg);
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
/*     */   protected String format(ChannelHandlerContext ctx, String eventName, Object firstArg, Object secondArg) {
/* 293 */     if (secondArg == null) {
/* 294 */       return formatSimple(ctx, eventName, firstArg);
/*     */     }
/*     */     
/* 297 */     String chStr = ctx.channel().toString();
/* 298 */     String arg1Str = String.valueOf(firstArg);
/* 299 */     String arg2Str = secondArg.toString();
/*     */     
/* 301 */     StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + arg1Str.length() + 2 + arg2Str.length());
/* 302 */     buf.append(chStr).append(' ').append(eventName).append(": ").append(arg1Str).append(", ").append(arg2Str);
/* 303 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String formatByteBuf(ChannelHandlerContext ctx, String eventName, ByteBuf msg) {
/* 310 */     String chStr = ctx.channel().toString();
/* 311 */     int length = msg.readableBytes();
/* 312 */     if (length == 0) {
/* 313 */       StringBuilder stringBuilder = new StringBuilder(chStr.length() + 1 + eventName.length() + 4);
/* 314 */       stringBuilder.append(chStr).append(' ').append(eventName).append(": 0B");
/* 315 */       return stringBuilder.toString();
/*     */     } 
/* 317 */     int rows = length / 16 + ((length % 15 == 0) ? 0 : 1) + 4;
/* 318 */     StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + 10 + 1 + 2 + rows * 80);
/*     */     
/* 320 */     buf.append(chStr).append(' ').append(eventName).append(": ").append(length).append('B').append(StringUtil.NEWLINE);
/* 321 */     ByteBufUtil.appendPrettyHexDump(buf, msg);
/*     */     
/* 323 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String formatByteBufHolder(ChannelHandlerContext ctx, String eventName, ByteBufHolder msg) {
/* 331 */     String chStr = ctx.channel().toString();
/* 332 */     String msgStr = msg.toString();
/* 333 */     ByteBuf content = msg.content();
/* 334 */     int length = content.readableBytes();
/* 335 */     if (length == 0) {
/* 336 */       StringBuilder stringBuilder = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 4);
/* 337 */       stringBuilder.append(chStr).append(' ').append(eventName).append(", ").append(msgStr).append(", 0B");
/* 338 */       return stringBuilder.toString();
/*     */     } 
/* 340 */     int rows = length / 16 + ((length % 15 == 0) ? 0 : 1) + 4;
/*     */     
/* 342 */     StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 2 + 10 + 1 + 2 + rows * 80);
/*     */     
/* 344 */     buf.append(chStr).append(' ').append(eventName).append(": ")
/* 345 */       .append(msgStr).append(", ").append(length).append('B').append(StringUtil.NEWLINE);
/* 346 */     ByteBufUtil.appendPrettyHexDump(buf, content);
/*     */     
/* 348 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String formatSimple(ChannelHandlerContext ctx, String eventName, Object msg) {
/* 356 */     String chStr = ctx.channel().toString();
/* 357 */     String msgStr = String.valueOf(msg);
/* 358 */     StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + msgStr.length());
/* 359 */     return buf.append(chStr).append(' ').append(eventName).append(": ").append(msgStr).toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\logging\LoggingHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */