/*     */ package io.sentry;
/*     */ 
/*     */ import io.sentry.event.Event;
/*     */ import io.sentry.event.EventBuilder;
/*     */ import io.sentry.event.interfaces.ExceptionInterface;
/*     */ import io.sentry.event.interfaces.SentryInterface;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ public class SentryUncaughtExceptionHandler
/*     */   implements Thread.UncaughtExceptionHandler
/*     */ {
/*  14 */   private static final Logger logger = LoggerFactory.getLogger(SentryClientFactory.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Thread.UncaughtExceptionHandler defaultExceptionHandler;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  25 */   private volatile Boolean enabled = Boolean.valueOf(true);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SentryUncaughtExceptionHandler(Thread.UncaughtExceptionHandler defaultExceptionHandler) {
/*  34 */     this.defaultExceptionHandler = defaultExceptionHandler;
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
/*     */   public void uncaughtException(Thread thread, Throwable thrown) {
/*  46 */     if (this.enabled.booleanValue()) {
/*  47 */       logger.trace("Uncaught exception received.");
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  52 */       EventBuilder eventBuilder = (new EventBuilder()).withMessage(thrown.getMessage()).withLevel(Event.Level.FATAL).withSentryInterface((SentryInterface)new ExceptionInterface(thrown));
/*     */       
/*     */       try {
/*  55 */         Sentry.capture(eventBuilder);
/*  56 */       } catch (RuntimeException e) {
/*  57 */         logger.error("Error sending uncaught exception to Sentry.", e);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  62 */     if (this.defaultExceptionHandler != null) {
/*     */       
/*  64 */       this.defaultExceptionHandler.uncaughtException(thread, thrown);
/*  65 */     } else if (!(thrown instanceof ThreadDeath)) {
/*     */       
/*  67 */       System.err.print("Exception in thread \"" + thread.getName() + "\" ");
/*  68 */       thrown.printStackTrace(System.err);
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
/*     */   public static SentryUncaughtExceptionHandler setup() {
/*  80 */     logger.debug("Configuring uncaught exception handler.");
/*     */     
/*  82 */     Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
/*  83 */     if (currentHandler != null) {
/*  84 */       logger.debug("default UncaughtExceptionHandler class='" + currentHandler.getClass().getName() + "'");
/*     */     }
/*     */     
/*  87 */     SentryUncaughtExceptionHandler handler = new SentryUncaughtExceptionHandler(currentHandler);
/*  88 */     Thread.setDefaultUncaughtExceptionHandler(handler);
/*  89 */     return handler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void disable() {
/*  96 */     this.enabled = Boolean.valueOf(false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 103 */     Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
/* 104 */     if (currentHandler == this) {
/* 105 */       Thread.setDefaultUncaughtExceptionHandler(this.defaultExceptionHandler);
/*     */     }
/*     */   }
/*     */   
/*     */   public Boolean isEnabled() {
/* 110 */     return this.enabled;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\SentryUncaughtExceptionHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */