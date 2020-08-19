/*     */ package io.sentry.connection;
/*     */ 
/*     */ import io.sentry.SentryClient;
/*     */ import io.sentry.environment.SentryEnvironment;
/*     */ import io.sentry.event.Event;
/*     */ import io.sentry.util.Nullable;
/*     */ import io.sentry.util.Util;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import org.slf4j.MDC;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AsyncConnection
/*     */   implements Connection
/*     */ {
/*  26 */   private static final Logger logger = LoggerFactory.getLogger(AsyncConnection.class);
/*     */   
/*  28 */   private static final Logger lockdownLogger = LoggerFactory.getLogger(SentryClient.class.getName() + ".lockdown");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   final ShutDownHook shutDownHook = new ShutDownHook();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final long shutdownTimeout;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Connection actualConnection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final ExecutorService executorService;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean gracefulShutdown;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean closed;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AsyncConnection(Connection actualConnection, @Nullable ExecutorService executorService, boolean gracefulShutdown, long shutdownTimeout) {
/*  69 */     this.actualConnection = actualConnection;
/*  70 */     if (executorService == null) {
/*  71 */       this.executorService = Executors.newSingleThreadExecutor();
/*     */     } else {
/*  73 */       this.executorService = executorService;
/*     */     } 
/*  75 */     if (gracefulShutdown) {
/*  76 */       this.gracefulShutdown = gracefulShutdown;
/*  77 */       addShutdownHook();
/*     */     } 
/*  79 */     this.shutdownTimeout = shutdownTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addShutdownHook() {
/*  87 */     Runtime.getRuntime().addShutdownHook(this.shutDownHook);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void send(Event event) {
/*  97 */     if (!this.closed) {
/*  98 */       this.executorService.execute(new EventSubmitter(event, MDC.getCopyOfContextMap()));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void addEventSendCallback(EventSendCallback eventSendCallback) {
/* 104 */     this.actualConnection.addEventSendCallback(eventSendCallback);
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
/*     */   public void close() throws IOException {
/* 117 */     if (this.gracefulShutdown) {
/* 118 */       Util.safelyRemoveShutdownHook(this.shutDownHook);
/* 119 */       this.shutDownHook.enabled = false;
/*     */     } 
/*     */     
/* 122 */     doClose();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doClose() throws IOException {
/* 132 */     logger.debug("Gracefully shutting down Sentry async threads.");
/* 133 */     this.closed = true;
/* 134 */     this.executorService.shutdown();
/*     */     try {
/* 136 */       if (this.shutdownTimeout == -1L) {
/*     */         
/* 138 */         long waitBetweenLoggingMs = 5000L;
/*     */         
/* 140 */         while (!this.executorService.awaitTermination(waitBetweenLoggingMs, TimeUnit.MILLISECONDS))
/*     */         {
/*     */           
/* 143 */           logger.debug("Still waiting on async executor to terminate.");
/*     */         }
/* 145 */       } else if (!this.executorService.awaitTermination(this.shutdownTimeout, TimeUnit.MILLISECONDS)) {
/* 146 */         logger.warn("Graceful shutdown took too much time, forcing the shutdown.");
/* 147 */         List<Runnable> tasks = this.executorService.shutdownNow();
/* 148 */         logger.warn("{} tasks failed to execute before shutdown.", Integer.valueOf(tasks.size()));
/*     */       } 
/* 150 */       logger.debug("Shutdown finished.");
/* 151 */     } catch (InterruptedException e) {
/* 152 */       Thread.currentThread().interrupt();
/* 153 */       logger.warn("Graceful shutdown interrupted, forcing the shutdown.");
/* 154 */       List<Runnable> tasks = this.executorService.shutdownNow();
/* 155 */       logger.warn("{} tasks failed to execute before shutdown.", Integer.valueOf(tasks.size()));
/*     */     } finally {
/* 157 */       this.actualConnection.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private final class EventSubmitter
/*     */     implements Runnable
/*     */   {
/*     */     private final Event event;
/*     */     
/*     */     private Map<String, String> mdcContext;
/*     */     
/*     */     private EventSubmitter(Event event, Map<String, String> mdcContext) {
/* 170 */       this.event = event;
/* 171 */       this.mdcContext = mdcContext;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 176 */       SentryEnvironment.startManagingThread();
/*     */       
/* 178 */       Map<String, String> previous = MDC.getCopyOfContextMap();
/* 179 */       if (this.mdcContext == null) {
/* 180 */         MDC.clear();
/*     */       } else {
/* 182 */         MDC.setContextMap(this.mdcContext);
/*     */       } 
/*     */ 
/*     */       
/*     */       try {
/* 187 */         AsyncConnection.this.actualConnection.send(this.event);
/* 188 */       } catch (LockedDownException|TooManyRequestsException e) {
/* 189 */         AsyncConnection.logger.debug("Dropping an Event due to lockdown: " + this.event);
/* 190 */       } catch (RuntimeException e) {
/* 191 */         AsyncConnection.logger.error("An exception occurred while sending the event to Sentry.", e);
/*     */       } finally {
/* 193 */         if (previous == null) {
/* 194 */           MDC.clear();
/*     */         } else {
/* 196 */           MDC.setContextMap(previous);
/*     */         } 
/*     */         
/* 199 */         SentryEnvironment.stopManagingThread();
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final class ShutDownHook
/*     */     extends Thread
/*     */   {
/*     */     private volatile boolean enabled = true;
/*     */ 
/*     */     
/*     */     public void run() {
/* 213 */       if (!this.enabled) {
/*     */         return;
/*     */       }
/*     */       
/* 217 */       SentryEnvironment.startManagingThread();
/*     */       
/*     */       try {
/* 220 */         AsyncConnection.this.doClose();
/* 221 */       } catch (IOException|RuntimeException e) {
/* 222 */         AsyncConnection.logger.error("An exception occurred while closing the connection.", e);
/*     */       } finally {
/* 224 */         SentryEnvironment.stopManagingThread();
/*     */       } 
/*     */     }
/*     */     
/*     */     private ShutDownHook() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\AsyncConnection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */