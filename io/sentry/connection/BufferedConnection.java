/*     */ package io.sentry.connection;
/*     */ 
/*     */ import io.sentry.buffer.Buffer;
/*     */ import io.sentry.environment.SentryEnvironment;
/*     */ import io.sentry.event.Event;
/*     */ import io.sentry.util.Util;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ public class BufferedConnection
/*     */   implements Connection
/*     */ {
/*  36 */   private static final Logger logger = LoggerFactory.getLogger(BufferedConnection.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  41 */   private final ShutDownHook shutDownHook = new ShutDownHook();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory()
/*     */       {
/*     */         public Thread newThread(Runnable r) {
/*  50 */           Thread thread = new Thread(r);
/*  51 */           thread.setDaemon(true);
/*  52 */           return thread;
/*     */         }
/*     */       });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Connection actualConnection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Buffer buffer;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean gracefulShutdown;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private long shutdownTimeout;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean closed = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedConnection(Connection actualConnection, Buffer buffer, long flushtime, boolean gracefulShutdown, long shutdownTimeout) {
/*  89 */     this.actualConnection = actualConnection;
/*  90 */     this.buffer = buffer;
/*  91 */     this.gracefulShutdown = gracefulShutdown;
/*  92 */     this.shutdownTimeout = shutdownTimeout;
/*     */     
/*  94 */     if (gracefulShutdown) {
/*  95 */       Runtime.getRuntime().addShutdownHook(this.shutDownHook);
/*     */     }
/*     */     
/*  98 */     Flusher flusher = new Flusher(flushtime);
/*  99 */     this.executorService.scheduleWithFixedDelay(flusher, flushtime, flushtime, TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */   
/*     */   public void send(Event event) {
/*     */     try {
/* 105 */       this.actualConnection.send(event);
/* 106 */     } catch (ConnectionException e) {
/* 107 */       boolean notSerializable = e.getCause() instanceof java.io.NotSerializableException;
/*     */       
/* 109 */       Integer responseCode = e.getResponseCode();
/* 110 */       if (notSerializable || responseCode != null)
/*     */       {
/*     */ 
/*     */         
/* 114 */         this.buffer.discard(event);
/*     */       }
/*     */ 
/*     */       
/* 118 */       throw e;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 123 */     this.buffer.discard(event);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addEventSendCallback(EventSendCallback eventSendCallback) {
/* 128 */     this.actualConnection.addEventSendCallback(eventSendCallback);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 134 */     if (this.gracefulShutdown) {
/* 135 */       Util.safelyRemoveShutdownHook(this.shutDownHook);
/* 136 */       this.shutDownHook.enabled = false;
/*     */     } 
/*     */     
/* 139 */     logger.debug("Gracefully shutting down Sentry buffer threads.");
/* 140 */     this.closed = true;
/* 141 */     this.executorService.shutdown();
/*     */     try {
/* 143 */       if (this.shutdownTimeout == -1L) {
/*     */         
/* 145 */         long waitBetweenLoggingMs = 5000L;
/*     */         
/* 147 */         while (!this.executorService.awaitTermination(waitBetweenLoggingMs, TimeUnit.MILLISECONDS))
/*     */         {
/*     */           
/* 150 */           logger.debug("Still waiting on buffer flusher executor to terminate.");
/*     */         }
/* 152 */       } else if (!this.executorService.awaitTermination(this.shutdownTimeout, TimeUnit.MILLISECONDS)) {
/* 153 */         logger.warn("Graceful shutdown took too much time, forcing the shutdown.");
/* 154 */         List<Runnable> tasks = this.executorService.shutdownNow();
/* 155 */         logger.warn("{} tasks failed to execute before the shutdown.", Integer.valueOf(tasks.size()));
/*     */       } 
/* 157 */       logger.debug("Shutdown finished.");
/* 158 */     } catch (InterruptedException e) {
/* 159 */       Thread.currentThread().interrupt();
/* 160 */       logger.warn("Graceful shutdown interrupted, forcing the shutdown.");
/* 161 */       List<Runnable> tasks = this.executorService.shutdownNow();
/* 162 */       logger.warn("{} tasks failed to execute before the shutdown.", Integer.valueOf(tasks.size()));
/*     */     } finally {
/* 164 */       this.actualConnection.close();
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
/*     */   public Connection wrapConnectionWithBufferWriter(final Connection connectionToWrap) {
/* 181 */     return new Connection() {
/* 182 */         final Connection wrappedConnection = connectionToWrap;
/*     */ 
/*     */ 
/*     */         
/*     */         public void send(Event event) throws ConnectionException {
/*     */           try {
/* 188 */             BufferedConnection.this.buffer.add(event);
/* 189 */           } catch (RuntimeException e) {
/* 190 */             BufferedConnection.logger.error("Exception occurred while attempting to add Event to buffer: ", e);
/*     */           } 
/*     */           
/* 193 */           this.wrappedConnection.send(event);
/*     */         }
/*     */ 
/*     */         
/*     */         public void addEventSendCallback(EventSendCallback eventSendCallback) {
/* 198 */           this.wrappedConnection.addEventSendCallback(eventSendCallback);
/*     */         }
/*     */ 
/*     */         
/*     */         public void close() throws IOException {
/* 203 */           this.wrappedConnection.close();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private class Flusher
/*     */     implements Runnable
/*     */   {
/*     */     private long minAgeMillis;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     Flusher(long minAgeMillis) {
/* 220 */       this.minAgeMillis = minAgeMillis;
/*     */     }
/*     */ 
/*     */     
/*     */     public void run() {
/* 225 */       BufferedConnection.logger.trace("Running Flusher");
/*     */       
/* 227 */       SentryEnvironment.startManagingThread();
/*     */       try {
/* 229 */         Iterator<Event> events = BufferedConnection.this.buffer.getEvents();
/* 230 */         while (events.hasNext() && !BufferedConnection.this.closed) {
/* 231 */           Event event = events.next();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 240 */           long now = System.currentTimeMillis();
/* 241 */           long eventTime = event.getTimestamp().getTime();
/* 242 */           long age = now - eventTime;
/* 243 */           if (age < this.minAgeMillis) {
/* 244 */             BufferedConnection.logger.trace("Ignoring buffered event because it only " + age + "ms old.");
/*     */             
/*     */             return;
/*     */           } 
/*     */           try {
/* 249 */             BufferedConnection.logger.trace("Flusher attempting to send Event: " + event.getId());
/* 250 */             BufferedConnection.this.send(event);
/* 251 */             BufferedConnection.logger.trace("Flusher successfully sent Event: " + event.getId());
/* 252 */           } catch (RuntimeException e) {
/* 253 */             BufferedConnection.logger.debug("Flusher failed to send Event: " + event.getId(), e);
/*     */ 
/*     */             
/* 256 */             BufferedConnection.logger.trace("Flusher run exiting early.");
/*     */             return;
/*     */           } 
/*     */         } 
/* 260 */         BufferedConnection.logger.trace("Flusher run exiting, no more events to send.");
/* 261 */       } catch (RuntimeException e) {
/* 262 */         BufferedConnection.logger.error("Error running Flusher: ", e);
/*     */       } finally {
/* 264 */         SentryEnvironment.stopManagingThread();
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
/* 278 */       if (!this.enabled) {
/*     */         return;
/*     */       }
/*     */       
/* 282 */       SentryEnvironment.startManagingThread();
/*     */       
/*     */       try {
/* 285 */         BufferedConnection.this.close();
/* 286 */       } catch (IOException|RuntimeException e) {
/* 287 */         BufferedConnection.logger.error("An exception occurred while closing the connection.", e);
/*     */       } finally {
/* 289 */         SentryEnvironment.stopManagingThread();
/*     */       } 
/*     */     }
/*     */     
/*     */     private ShutDownHook() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\BufferedConnection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */