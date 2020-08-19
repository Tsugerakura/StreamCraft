/*     */ package io.sentry.connection;
/*     */ 
/*     */ import io.sentry.environment.SentryEnvironment;
/*     */ import io.sentry.event.Event;
/*     */ import io.sentry.util.Util;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ public abstract class AbstractConnection
/*     */   implements Connection
/*     */ {
/*     */   public static final String SENTRY_PROTOCOL_VERSION = "6";
/*  24 */   private static final Logger logger = LoggerFactory.getLogger(AbstractConnection.class);
/*     */ 
/*     */   
/*  27 */   private static final Logger lockdownLogger = LoggerFactory.getLogger(AbstractConnection.class.getName() + ".lockdown");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final String authHeader;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Set<EventSendCallback> eventSendCallbacks;
/*     */ 
/*     */ 
/*     */   
/*     */   private LockdownManager lockdownManager;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractConnection(String publicKey, String secretKey) {
/*  47 */     this(publicKey, secretKey, new LockdownManager());
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
/*     */   AbstractConnection(String publicKey, String secretKey, LockdownManager lockdownManager) {
/*  59 */     this.lockdownManager = lockdownManager;
/*  60 */     this.eventSendCallbacks = new HashSet<>();
/*  61 */     this
/*     */ 
/*     */       
/*  64 */       .authHeader = "Sentry sentry_version=6,sentry_client=" + SentryEnvironment.getSentryName() + ",sentry_key=" + publicKey + (!Util.isNullOrEmpty(secretKey) ? (",sentry_secret=" + secretKey) : "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getAuthHeader() {
/*  73 */     return this.authHeader;
/*     */   }
/*     */ 
/*     */   
/*     */   public final void send(Event event) throws ConnectionException {
/*     */     try {
/*  79 */       if (this.lockdownManager.isLockedDown())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  85 */         throw new LockedDownException();
/*     */       }
/*     */       
/*  88 */       doSend(event);
/*     */ 
/*     */       
/*  91 */       this.lockdownManager.unlock();
/*     */       
/*  93 */       for (EventSendCallback eventSendCallback : this.eventSendCallbacks) {
/*     */         try {
/*  95 */           eventSendCallback.onSuccess(event);
/*  96 */         } catch (RuntimeException exc) {
/*  97 */           logger.warn("An exception occurred while running an EventSendCallback.onSuccess: " + eventSendCallback
/*  98 */               .getClass().getName(), exc);
/*     */         } 
/*     */       } 
/* 101 */     } catch (ConnectionException e) {
/* 102 */       for (EventSendCallback eventSendCallback : this.eventSendCallbacks) {
/*     */         try {
/* 104 */           eventSendCallback.onFailure(event, e);
/* 105 */         } catch (RuntimeException exc) {
/* 106 */           logger.warn("An exception occurred while running an EventSendCallback.onFailure: " + eventSendCallback
/* 107 */               .getClass().getName(), exc);
/*     */         } 
/*     */       } 
/*     */       
/* 111 */       boolean lockedDown = this.lockdownManager.lockdown(e);
/* 112 */       if (lockedDown) {
/* 113 */         lockdownLogger.warn("Initiated a temporary lockdown because of exception: " + e.getMessage());
/*     */       }
/*     */       
/* 116 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void doSend(Event paramEvent) throws ConnectionException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addEventSendCallback(EventSendCallback eventSendCallback) {
/* 135 */     this.eventSendCallbacks.add(eventSendCallback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLockedDown() {
/* 144 */     return this.lockdownManager.isLockedDown();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\AbstractConnection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */