/*     */ package io.sentry;
/*     */ import io.sentry.buffer.Buffer;
/*     */ import io.sentry.config.Lookup;
/*     */ import io.sentry.connection.AsyncConnection;
/*     */ import io.sentry.connection.BufferedConnection;
/*     */ import io.sentry.connection.Connection;
/*     */ import io.sentry.connection.EventSampler;
/*     */ import io.sentry.connection.HttpConnection;
/*     */ import io.sentry.connection.NoopConnection;
/*     */ import io.sentry.connection.OutputStreamConnection;
/*     */ import io.sentry.connection.ProxyAuthenticator;
/*     */ import io.sentry.connection.RandomEventSampler;
/*     */ import io.sentry.context.ContextManager;
/*     */ import io.sentry.context.ThreadLocalContextManager;
/*     */ import io.sentry.dsn.Dsn;
/*     */ import io.sentry.event.helper.ContextBuilderHelper;
/*     */ import io.sentry.event.helper.EventBuilderHelper;
/*     */ import io.sentry.event.helper.HttpEventBuilderHelper;
/*     */ import io.sentry.event.interfaces.StackTraceInterface;
/*     */ import io.sentry.event.interfaces.UserInterface;
/*     */ import io.sentry.jvmti.FrameCache;
/*     */ import io.sentry.marshaller.Marshaller;
/*     */ import io.sentry.marshaller.json.HttpInterfaceBinding;
/*     */ import io.sentry.marshaller.json.InterfaceBinding;
/*     */ import io.sentry.marshaller.json.JsonMarshaller;
/*     */ import io.sentry.marshaller.json.MessageInterfaceBinding;
/*     */ import io.sentry.marshaller.json.StackTraceInterfaceBinding;
/*     */ import io.sentry.marshaller.json.UserInterfaceBinding;
/*     */ import io.sentry.util.Util;
/*     */ import java.io.File;
/*     */ import java.net.Authenticator;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Proxy;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.BlockingDeque;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.LinkedBlockingDeque;
/*     */ import java.util.concurrent.RejectedExecutionHandler;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class DefaultSentryClientFactory extends SentryClientFactory {
/*  55 */   public static final int CONNECTION_TIMEOUT_DEFAULT = (int)TimeUnit.SECONDS.toMillis(1L);
/*     */   
/*     */   public static final String NAIVE_PROTOCOL = "naive";
/*     */   
/*     */   public static final String COMPRESSION_OPTION = "compression";
/*     */   public static final String MAX_MESSAGE_LENGTH_OPTION = "maxmessagelength";
/*     */   public static final String CONNECTION_TIMEOUT_OPTION = "timeout";
/*     */   public static final String READ_TIMEOUT_OPTION = "readtimeout";
/*  63 */   public static final int READ_TIMEOUT_DEFAULT = (int)TimeUnit.SECONDS.toMillis(5L);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String BUFFER_ENABLED_OPTION = "buffer.enabled";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final boolean BUFFER_ENABLED_DEFAULT = true;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String BUFFER_DIR_OPTION = "buffer.dir";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String BUFFER_SIZE_OPTION = "buffer.size";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int BUFFER_SIZE_DEFAULT = 10;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String BUFFER_FLUSHTIME_OPTION = "buffer.flushtime";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final long BUFFER_FLUSHTIME_DEFAULT = 60000L;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String BUFFER_GRACEFUL_SHUTDOWN_OPTION = "buffer.gracefulshutdown";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String BUFFER_SHUTDOWN_TIMEOUT_OPTION = "buffer.shutdowntimeout";
/*     */ 
/*     */ 
/*     */   
/* 104 */   public static final long BUFFER_SHUTDOWN_TIMEOUT_DEFAULT = TimeUnit.SECONDS.toMillis(1L);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ASYNC_OPTION = "async";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ASYNC_GRACEFUL_SHUTDOWN_OPTION = "async.gracefulshutdown";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ASYNC_THREADS_OPTION = "async.threads";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ASYNC_PRIORITY_OPTION = "async.priority";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ASYNC_QUEUE_SIZE_OPTION = "async.queuesize";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ASYNC_QUEUE_OVERFLOW_OPTION = "async.queue.overflow";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ASYNC_QUEUE_DISCARDOLD = "discardold";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ASYNC_QUEUE_DISCARDNEW = "discardnew";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ASYNC_QUEUE_SYNC = "sync";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ASYNC_QUEUE_OVERFLOW_DEFAULT = "discardold";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ASYNC_SHUTDOWN_TIMEOUT_OPTION = "async.shutdowntimeout";
/*     */ 
/*     */ 
/*     */   
/* 154 */   public static final long ASYNC_SHUTDOWN_TIMEOUT_DEFAULT = TimeUnit.SECONDS.toMillis(1L);
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String IN_APP_FRAMES_OPTION = "stacktrace.app.packages";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String HIDE_COMMON_FRAMES_OPTION = "stacktrace.hidecommon";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SAMPLE_RATE_OPTION = "sample.rate";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String HTTP_PROXY_HOST_OPTION = "http.proxy.host";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String HTTP_PROXY_PORT_OPTION = "http.proxy.port";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String HTTP_PROXY_USER_OPTION = "http.proxy.user";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String HTTP_PROXY_PASS_OPTION = "http.proxy.password";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int QUEUE_SIZE_DEFAULT = 50;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int HTTP_PROXY_PORT_DEFAULT = 80;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String RELEASE_OPTION = "release";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String DIST_OPTION = "dist";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String ENVIRONMENT_OPTION = "environment";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String SERVERNAME_OPTION = "servername";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String TAGS_OPTION = "tags";
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static final String EXTRATAGS_OPTION = "extratags";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String MDCTAGS_OPTION = "mdctags";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String EXTRA_OPTION = "extra";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String UNCAUGHT_HANDLER_ENABLED_OPTION = "uncaught.handler.enabled";
/*     */ 
/*     */ 
/*     */   
/* 231 */   private static final Logger logger = LoggerFactory.getLogger(DefaultSentryClientFactory.class);
/* 232 */   private static final String FALSE = Boolean.FALSE.toString();
/*     */   
/* 234 */   private static final Map<String, RejectedExecutionHandler> REJECT_EXECUTION_HANDLERS = new HashMap<>();
/*     */   static {
/* 236 */     REJECT_EXECUTION_HANDLERS.put("sync", new ThreadPoolExecutor.CallerRunsPolicy());
/* 237 */     REJECT_EXECUTION_HANDLERS.put("discardnew", new ThreadPoolExecutor.DiscardPolicy());
/* 238 */     REJECT_EXECUTION_HANDLERS.put("discardold", new ThreadPoolExecutor.DiscardOldestPolicy());
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
/*     */   public DefaultSentryClientFactory() {
/* 251 */     this(Lookup.getDefault());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultSentryClientFactory(Lookup lookup) {
/* 259 */     super(lookup);
/*     */   }
/*     */ 
/*     */   
/*     */   public SentryClient createSentryClient(Dsn dsn) {
/*     */     try {
/* 265 */       SentryClient sentryClient = new SentryClient(createConnection(dsn), getContextManager(dsn));
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 270 */         Class.forName("javax.servlet.ServletRequestListener", false, getClass().getClassLoader());
/* 271 */         sentryClient.addBuilderHelper((EventBuilderHelper)new HttpEventBuilderHelper());
/* 272 */       } catch (ClassNotFoundException e) {
/* 273 */         logger.debug("The current environment doesn't provide access to servlets, or provides an unsupported version.");
/*     */       } 
/*     */       
/* 276 */       sentryClient.addBuilderHelper((EventBuilderHelper)new ContextBuilderHelper(sentryClient));
/* 277 */       return configureSentryClient(sentryClient, dsn);
/* 278 */     } catch (RuntimeException e) {
/* 279 */       logger.error("Failed to initialize sentry, falling back to no-op client", e);
/* 280 */       return new SentryClient((Connection)new NoopConnection(), (ContextManager)new ThreadLocalContextManager());
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
/*     */   protected SentryClient configureSentryClient(SentryClient sentryClient, Dsn dsn) {
/* 292 */     String release = getRelease(dsn);
/* 293 */     if (release != null) {
/* 294 */       sentryClient.setRelease(release);
/*     */     }
/*     */     
/* 297 */     String dist = getDist(dsn);
/* 298 */     if (dist != null) {
/* 299 */       sentryClient.setDist(dist);
/*     */     }
/*     */     
/* 302 */     String environment = getEnvironment(dsn);
/* 303 */     if (environment != null) {
/* 304 */       sentryClient.setEnvironment(environment);
/*     */     }
/*     */     
/* 307 */     String serverName = getServerName(dsn);
/* 308 */     if (serverName != null) {
/* 309 */       sentryClient.setServerName(serverName);
/*     */     }
/*     */     
/* 312 */     Map<String, String> tags = getTags(dsn);
/* 313 */     if (!tags.isEmpty()) {
/* 314 */       for (Map.Entry<String, String> tagEntry : tags.entrySet()) {
/* 315 */         sentryClient.addTag(tagEntry.getKey(), tagEntry.getValue());
/*     */       }
/*     */     }
/*     */     
/* 319 */     Set<String> mdcTags = getMdcTags(dsn);
/* 320 */     if (!mdcTags.isEmpty()) {
/* 321 */       for (String mdcTag : mdcTags) {
/* 322 */         sentryClient.addMdcTag(mdcTag);
/*     */       }
/*     */     }
/*     */     
/* 326 */     Map<String, String> extra = getExtra(dsn);
/* 327 */     if (!extra.isEmpty()) {
/* 328 */       for (Map.Entry<String, String> extraEntry : extra.entrySet()) {
/* 329 */         sentryClient.addExtra(extraEntry.getKey(), extraEntry.getValue());
/*     */       }
/*     */     }
/*     */     
/* 333 */     if (getUncaughtHandlerEnabled(dsn)) {
/* 334 */       sentryClient.setupUncaughtExceptionHandler();
/*     */     }
/*     */     
/* 337 */     for (String inAppPackage : getInAppFrames(dsn)) {
/* 338 */       FrameCache.addAppPackage(inAppPackage);
/*     */     }
/*     */     
/* 341 */     return sentryClient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Connection createConnection(Dsn dsn) {
/*     */     NoopConnection noopConnection;
/*     */     BufferedConnection bufferedConnection1;
/*     */     Connection connection;
/* 351 */     String protocol = dsn.getProtocol();
/*     */ 
/*     */     
/* 354 */     if (protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https")) {
/* 355 */       logger.debug("Using an {} connection to Sentry.", protocol.toUpperCase());
/* 356 */       connection = createHttpConnection(dsn);
/* 357 */     } else if (protocol.equalsIgnoreCase("out")) {
/* 358 */       logger.debug("Using StdOut to send events.");
/* 359 */       connection = createStdOutConnection(dsn);
/* 360 */     } else if (protocol.equalsIgnoreCase("noop")) {
/* 361 */       logger.debug("Using noop to send events.");
/* 362 */       noopConnection = new NoopConnection();
/*     */     } else {
/* 364 */       throw new IllegalStateException("Couldn't create a connection for the protocol '" + protocol + "'");
/*     */     } 
/*     */     
/* 367 */     BufferedConnection bufferedConnection = null;
/* 368 */     if (getBufferEnabled(dsn)) {
/* 369 */       Buffer eventBuffer = getBuffer(dsn);
/* 370 */       if (eventBuffer != null) {
/* 371 */         long flushtime = getBufferFlushtime(dsn);
/* 372 */         boolean gracefulShutdown = getBufferedConnectionGracefulShutdownEnabled(dsn);
/* 373 */         Long shutdownTimeout = Long.valueOf(getBufferedConnectionShutdownTimeout(dsn));
/*     */         
/* 375 */         bufferedConnection = new BufferedConnection((Connection)noopConnection, eventBuffer, flushtime, gracefulShutdown, shutdownTimeout.longValue());
/* 376 */         bufferedConnection1 = bufferedConnection;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 381 */     if (getAsyncEnabled(dsn)) {
/* 382 */       connection = createAsyncConnection(dsn, (Connection)bufferedConnection1);
/*     */     }
/*     */ 
/*     */     
/* 386 */     if (bufferedConnection != null) {
/* 387 */       connection = bufferedConnection.wrapConnectionWithBufferWriter(connection);
/*     */     }
/*     */     
/* 390 */     return connection;
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
/*     */   protected Connection createAsyncConnection(Dsn dsn, Connection connection) {
/*     */     BlockingDeque<Runnable> queue;
/* 403 */     int maxThreads = getAsyncThreads(dsn);
/* 404 */     int priority = getAsyncPriority(dsn);
/*     */ 
/*     */     
/* 407 */     int queueSize = getAsyncQueueSize(dsn);
/* 408 */     if (queueSize == -1) {
/* 409 */       queue = new LinkedBlockingDeque<>();
/*     */     } else {
/* 411 */       queue = new LinkedBlockingDeque<>(queueSize);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 416 */     ExecutorService executorService = new ThreadPoolExecutor(maxThreads, maxThreads, 0L, TimeUnit.MILLISECONDS, queue, new DaemonThreadFactory(priority), getRejectedExecutionHandler(dsn));
/*     */     
/* 418 */     boolean gracefulShutdown = getAsyncGracefulShutdownEnabled(dsn);
/*     */     
/* 420 */     long shutdownTimeout = getAsyncShutdownTimeout(dsn);
/* 421 */     return (Connection)new AsyncConnection(connection, executorService, gracefulShutdown, shutdownTimeout);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Connection createHttpConnection(Dsn dsn) {
/*     */     RandomEventSampler randomEventSampler;
/* 431 */     URL sentryApiUrl = HttpConnection.getSentryApiUrl(dsn.getUri(), dsn.getProjectId());
/*     */     
/* 433 */     String proxyHost = getProxyHost(dsn);
/* 434 */     String proxyUser = getProxyUser(dsn);
/* 435 */     String proxyPass = getProxyPass(dsn);
/* 436 */     int proxyPort = getProxyPort(dsn);
/*     */     
/* 438 */     Proxy proxy = null;
/* 439 */     if (proxyHost != null) {
/* 440 */       InetSocketAddress proxyAddr = new InetSocketAddress(proxyHost, proxyPort);
/* 441 */       proxy = new Proxy(Proxy.Type.HTTP, proxyAddr);
/* 442 */       if (proxyUser != null && proxyPass != null) {
/* 443 */         Authenticator.setDefault((Authenticator)new ProxyAuthenticator(proxyUser, proxyPass));
/*     */       }
/*     */     } 
/*     */     
/* 447 */     Double sampleRate = getSampleRate(dsn);
/* 448 */     EventSampler eventSampler = null;
/* 449 */     if (sampleRate != null) {
/* 450 */       randomEventSampler = new RandomEventSampler(sampleRate.doubleValue());
/*     */     }
/*     */ 
/*     */     
/* 454 */     HttpConnection httpConnection = new HttpConnection(sentryApiUrl, dsn.getPublicKey(), dsn.getSecretKey(), proxy, (EventSampler)randomEventSampler);
/*     */     
/* 456 */     Marshaller marshaller = createMarshaller(dsn);
/* 457 */     httpConnection.setMarshaller(marshaller);
/*     */     
/* 459 */     int timeout = getTimeout(dsn);
/* 460 */     httpConnection.setConnectionTimeout(timeout);
/*     */     
/* 462 */     int readTimeout = getReadTimeout(dsn);
/* 463 */     httpConnection.setReadTimeout(readTimeout);
/*     */     
/* 465 */     boolean bypassSecurityEnabled = getBypassSecurityEnabled(dsn);
/* 466 */     httpConnection.setBypassSecurity(bypassSecurityEnabled);
/*     */     
/* 468 */     return (Connection)httpConnection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Connection createStdOutConnection(Dsn dsn) {
/* 479 */     OutputStreamConnection stdOutConnection = new OutputStreamConnection(System.out);
/*     */     
/* 481 */     stdOutConnection.setMarshaller(createMarshaller(dsn));
/* 482 */     return (Connection)stdOutConnection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Marshaller createMarshaller(Dsn dsn) {
/* 493 */     int maxMessageLength = getMaxMessageLength(dsn);
/* 494 */     JsonMarshaller marshaller = createJsonMarshaller(maxMessageLength);
/*     */ 
/*     */     
/* 497 */     StackTraceInterfaceBinding stackTraceBinding = new StackTraceInterfaceBinding();
/*     */     
/* 499 */     stackTraceBinding.setRemoveCommonFramesWithEnclosing(getHideCommonFramesEnabled(dsn));
/* 500 */     stackTraceBinding.setInAppFrames(getInAppFrames(dsn));
/*     */     
/* 502 */     marshaller.addInterfaceBinding(StackTraceInterface.class, (InterfaceBinding)stackTraceBinding);
/* 503 */     marshaller.addInterfaceBinding(ExceptionInterface.class, (InterfaceBinding)new ExceptionInterfaceBinding((InterfaceBinding)stackTraceBinding));
/* 504 */     marshaller.addInterfaceBinding(MessageInterface.class, (InterfaceBinding)new MessageInterfaceBinding(maxMessageLength));
/* 505 */     marshaller.addInterfaceBinding(UserInterface.class, (InterfaceBinding)new UserInterfaceBinding());
/* 506 */     marshaller.addInterfaceBinding(DebugMetaInterface.class, (InterfaceBinding)new DebugMetaInterfaceBinding());
/* 507 */     HttpInterfaceBinding httpBinding = new HttpInterfaceBinding();
/*     */ 
/*     */     
/* 510 */     marshaller.addInterfaceBinding(HttpInterface.class, (InterfaceBinding)httpBinding);
/*     */ 
/*     */     
/* 513 */     marshaller.setCompression(getCompressionEnabled(dsn));
/*     */     
/* 515 */     return (Marshaller)marshaller;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonMarshaller createJsonMarshaller(int maxMessageLength) {
/* 526 */     return new JsonMarshaller(maxMessageLength);
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
/*     */   protected ContextManager getContextManager(Dsn dsn) {
/* 539 */     return (ContextManager)new ThreadLocalContextManager();
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
/*     */   protected Collection<String> getInAppFrames(Dsn dsn) {
/* 552 */     String inAppFramesOption = this.lookup.get("stacktrace.app.packages", dsn);
/* 553 */     if (Util.isNullOrEmpty(inAppFramesOption)) {
/*     */       
/* 555 */       if (inAppFramesOption == null) {
/* 556 */         logger.warn("No 'stacktrace.app.packages' was configured, this option is highly recommended as it affects stacktrace grouping and display on Sentry. See documentation: https://docs.sentry.io/clients/java/config/#in-application-stack-frames");
/*     */       }
/*     */ 
/*     */       
/* 560 */       return Collections.emptyList();
/*     */     } 
/*     */     
/* 563 */     List<String> inAppPackages = new ArrayList<>();
/* 564 */     for (String inAppPackage : inAppFramesOption.split(",")) {
/* 565 */       if (!inAppPackage.trim().equals("")) {
/* 566 */         inAppPackages.add(inAppPackage);
/*     */       }
/*     */     } 
/*     */     
/* 570 */     return inAppPackages;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getAsyncEnabled(Dsn dsn) {
/* 580 */     return !FALSE.equalsIgnoreCase(this.lookup.get("async", dsn));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected RejectedExecutionHandler getRejectedExecutionHandler(Dsn dsn) {
/* 590 */     String overflowName = "discardold";
/* 591 */     String asyncQueueOverflowOption = this.lookup.get("async.queue.overflow", dsn);
/* 592 */     if (!Util.isNullOrEmpty(asyncQueueOverflowOption)) {
/* 593 */       overflowName = asyncQueueOverflowOption.toLowerCase();
/*     */     }
/*     */     
/* 596 */     RejectedExecutionHandler handler = REJECT_EXECUTION_HANDLERS.get(overflowName);
/* 597 */     if (handler == null) {
/* 598 */       String options = Arrays.toString(REJECT_EXECUTION_HANDLERS.keySet().toArray());
/* 599 */       throw new RuntimeException("RejectedExecutionHandler not found: '" + overflowName + "', valid choices are: " + options);
/*     */     } 
/*     */ 
/*     */     
/* 603 */     return handler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long getBufferedConnectionShutdownTimeout(Dsn dsn) {
/* 613 */     return Util.parseLong(this.lookup.get("buffer.shutdowntimeout", dsn), Long.valueOf(BUFFER_SHUTDOWN_TIMEOUT_DEFAULT)).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getBufferedConnectionGracefulShutdownEnabled(Dsn dsn) {
/* 623 */     return !FALSE.equalsIgnoreCase(this.lookup.get("buffer.gracefulshutdown", dsn));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long getBufferFlushtime(Dsn dsn) {
/* 633 */     return Util.parseLong(this.lookup.get("buffer.flushtime", dsn), Long.valueOf(60000L)).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long getAsyncShutdownTimeout(Dsn dsn) {
/* 643 */     return Util.parseLong(this.lookup.get("async.shutdowntimeout", dsn), Long.valueOf(ASYNC_SHUTDOWN_TIMEOUT_DEFAULT)).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getAsyncGracefulShutdownEnabled(Dsn dsn) {
/* 653 */     return !FALSE.equalsIgnoreCase(this.lookup.get("async.gracefulshutdown", dsn));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getAsyncQueueSize(Dsn dsn) {
/* 663 */     return Util.parseInteger(this.lookup.get("async.queuesize", dsn), Integer.valueOf(50)).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getAsyncPriority(Dsn dsn) {
/* 673 */     return Util.parseInteger(this.lookup.get("async.priority", dsn), Integer.valueOf(1)).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getAsyncThreads(Dsn dsn) {
/* 683 */     return Util.parseInteger(this.lookup.get("async.threads", dsn), 
/* 684 */         Integer.valueOf(Runtime.getRuntime().availableProcessors())).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getBypassSecurityEnabled(Dsn dsn) {
/* 694 */     return dsn.getProtocolSettings().contains("naive");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Double getSampleRate(Dsn dsn) {
/* 704 */     return Util.parseDouble(this.lookup.get("sample.rate", dsn), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getProxyPort(Dsn dsn) {
/* 714 */     return Util.parseInteger(this.lookup.get("http.proxy.port", dsn), Integer.valueOf(80)).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getProxyHost(Dsn dsn) {
/* 724 */     return this.lookup.get("http.proxy.host", dsn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getProxyUser(Dsn dsn) {
/* 734 */     return this.lookup.get("http.proxy.user", dsn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getProxyPass(Dsn dsn) {
/* 744 */     return this.lookup.get("http.proxy.password", dsn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getRelease(Dsn dsn) {
/* 755 */     return this.lookup.get("release", dsn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getDist(Dsn dsn) {
/* 766 */     return this.lookup.get("dist", dsn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getEnvironment(Dsn dsn) {
/* 777 */     return this.lookup.get("environment", dsn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getServerName(Dsn dsn) {
/* 788 */     return this.lookup.get("servername", dsn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<String, String> getTags(Dsn dsn) {
/* 798 */     return Util.parseTags(this.lookup.get("tags", dsn));
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
/*     */   protected Set<String> getExtraTags(Dsn dsn) {
/* 810 */     return getMdcTags(dsn);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Set<String> getMdcTags(Dsn dsn) {
/* 820 */     String val = this.lookup.get("mdctags", dsn);
/* 821 */     if (Util.isNullOrEmpty(val)) {
/* 822 */       val = this.lookup.get("extratags", dsn);
/* 823 */       if (!Util.isNullOrEmpty(val)) {
/* 824 */         logger.warn("The 'extratags' option is deprecated, please use the 'mdctags' option instead.");
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 829 */     return Util.parseMdcTags(val);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Map<String, String> getExtra(Dsn dsn) {
/* 839 */     return Util.parseExtra(this.lookup.get("extra", dsn));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getCompressionEnabled(Dsn dsn) {
/* 849 */     return !FALSE.equalsIgnoreCase(this.lookup.get("compression", dsn));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getHideCommonFramesEnabled(Dsn dsn) {
/* 859 */     return !FALSE.equalsIgnoreCase(this.lookup.get("stacktrace.hidecommon", dsn));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getMaxMessageLength(Dsn dsn) {
/* 869 */     return Util.parseInteger(this.lookup
/* 870 */         .get("maxmessagelength", dsn), Integer.valueOf(1000)).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getTimeout(Dsn dsn) {
/* 880 */     return Util.parseInteger(this.lookup.get("timeout", dsn), Integer.valueOf(CONNECTION_TIMEOUT_DEFAULT)).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getReadTimeout(Dsn dsn) {
/* 890 */     return Util.parseInteger(this.lookup.get("readtimeout", dsn), Integer.valueOf(READ_TIMEOUT_DEFAULT)).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getBufferEnabled(Dsn dsn) {
/* 900 */     String bufferEnabled = this.lookup.get("buffer.enabled", dsn);
/* 901 */     if (bufferEnabled != null) {
/* 902 */       return Boolean.parseBoolean(bufferEnabled);
/*     */     }
/* 904 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Buffer getBuffer(Dsn dsn) {
/* 914 */     String bufferDir = this.lookup.get("buffer.dir", dsn);
/* 915 */     if (bufferDir != null) {
/* 916 */       return (Buffer)new DiskBuffer(new File(bufferDir), getBufferSize(dsn));
/*     */     }
/* 918 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getBufferSize(Dsn dsn) {
/* 928 */     return Util.parseInteger(this.lookup.get("buffer.size", dsn), Integer.valueOf(10)).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean getUncaughtHandlerEnabled(Dsn dsn) {
/* 938 */     return !FALSE.equalsIgnoreCase(this.lookup.get("uncaught.handler.enabled", dsn));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final class DaemonThreadFactory
/*     */     implements ThreadFactory
/*     */   {
/* 949 */     private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
/*     */     private final ThreadGroup group;
/* 951 */     private final AtomicInteger threadNumber = new AtomicInteger(1);
/*     */     private final String namePrefix;
/*     */     private final int priority;
/*     */     
/*     */     private DaemonThreadFactory(int priority) {
/* 956 */       SecurityManager s = System.getSecurityManager();
/* 957 */       this.group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
/* 958 */       this.namePrefix = "sentry-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
/* 959 */       this.priority = priority;
/*     */     }
/*     */ 
/*     */     
/*     */     public Thread newThread(Runnable r) {
/* 964 */       Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
/* 965 */       if (!t.isDaemon()) {
/* 966 */         t.setDaemon(true);
/*     */       }
/* 968 */       if (t.getPriority() != this.priority) {
/* 969 */         t.setPriority(this.priority);
/*     */       }
/* 971 */       return t;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\DefaultSentryClientFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */