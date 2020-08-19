/*     */ package io.sentry.jul;
/*     */ 
/*     */ import io.sentry.Sentry;
/*     */ import io.sentry.environment.SentryEnvironment;
/*     */ import io.sentry.event.Event;
/*     */ import io.sentry.event.EventBuilder;
/*     */ import io.sentry.event.interfaces.ExceptionInterface;
/*     */ import io.sentry.event.interfaces.MessageInterface;
/*     */ import io.sentry.event.interfaces.SentryInterface;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.logging.Filter;
/*     */ import java.util.logging.Handler;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.LogManager;
/*     */ import java.util.logging.LogRecord;
/*     */ import org.slf4j.MDC;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SentryHandler
/*     */   extends Handler
/*     */ {
/*     */   public static final String THREAD_ID = "Sentry-ThreadId";
/*     */   protected boolean printfStyle;
/*     */   
/*     */   public SentryHandler() {
/*  37 */     retrieveProperties();
/*  38 */     setFilter(new DropSentryFilter());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static Event.Level getLevel(Level level) {
/*  48 */     if (level.intValue() >= Level.SEVERE.intValue())
/*  49 */       return Event.Level.ERROR; 
/*  50 */     if (level.intValue() >= Level.WARNING.intValue())
/*  51 */       return Event.Level.WARNING; 
/*  52 */     if (level.intValue() >= Level.INFO.intValue())
/*  53 */       return Event.Level.INFO; 
/*  54 */     if (level.intValue() >= Level.ALL.intValue()) {
/*  55 */       return Event.Level.DEBUG;
/*     */     }
/*  57 */     return null;
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
/*     */   protected static List<String> formatMessageParameters(Object[] parameters) {
/*  70 */     List<String> formattedParameters = new ArrayList<>(parameters.length);
/*  71 */     for (Object parameter : parameters) {
/*  72 */       formattedParameters.add((parameter != null) ? parameter.toString() : null);
/*     */     }
/*  74 */     return formattedParameters;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void retrieveProperties() {
/*  81 */     LogManager manager = LogManager.getLogManager();
/*  82 */     String className = SentryHandler.class.getName();
/*  83 */     setPrintfStyle(Boolean.valueOf(manager.getProperty(className + ".printfStyle")).booleanValue());
/*  84 */     setLevel(parseLevelOrDefault(manager.getProperty(className + ".level")));
/*     */   }
/*     */   
/*     */   private Level parseLevelOrDefault(String levelName) {
/*     */     try {
/*  89 */       return Level.parse(levelName.trim());
/*  90 */     } catch (RuntimeException e) {
/*  91 */       return Level.WARNING;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void publish(LogRecord record) {
/*  98 */     if (!isLoggable(record) || SentryEnvironment.isManagingThread()) {
/*     */       return;
/*     */     }
/*     */     
/* 102 */     SentryEnvironment.startManagingThread();
/*     */     try {
/* 104 */       EventBuilder eventBuilder = createEventBuilder(record);
/* 105 */       Sentry.capture(eventBuilder);
/* 106 */     } catch (RuntimeException e) {
/* 107 */       reportError("An exception occurred while creating a new event in Sentry", e, 1);
/*     */     } finally {
/* 109 */       SentryEnvironment.stopManagingThread();
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
/*     */   protected EventBuilder createEventBuilder(LogRecord record) {
/* 124 */     EventBuilder eventBuilder = (new EventBuilder()).withSdkIntegration("java.util.logging").withLevel(getLevel(record.getLevel())).withTimestamp(new Date(record.getMillis())).withLogger(record.getLoggerName());
/*     */     
/* 126 */     String message = record.getMessage();
/* 127 */     if (record.getResourceBundle() != null && record.getResourceBundle().containsKey(record.getMessage())) {
/* 128 */       message = record.getResourceBundle().getString(record.getMessage());
/*     */     }
/*     */     
/* 131 */     String topLevelMessage = message;
/* 132 */     if (record.getParameters() == null) {
/* 133 */       eventBuilder.withSentryInterface((SentryInterface)new MessageInterface(message));
/*     */     } else {
/*     */       String formatted;
/* 136 */       List<String> parameters = formatMessageParameters(record.getParameters());
/*     */       try {
/* 138 */         formatted = formatMessage(message, record.getParameters());
/* 139 */         topLevelMessage = formatted;
/* 140 */       } catch (RuntimeException e) {
/*     */         
/* 142 */         formatted = null;
/*     */       } 
/* 144 */       eventBuilder.withSentryInterface((SentryInterface)new MessageInterface(message, parameters, formatted));
/*     */     } 
/* 146 */     eventBuilder.withMessage(topLevelMessage);
/*     */     
/* 148 */     Throwable throwable = record.getThrown();
/* 149 */     if (throwable != null) {
/* 150 */       eventBuilder.withSentryInterface((SentryInterface)new ExceptionInterface(throwable));
/*     */     }
/*     */     
/* 153 */     Map<String, String> mdc = MDC.getMDCAdapter().getCopyOfContextMap();
/* 154 */     if (mdc != null) {
/* 155 */       for (Map.Entry<String, String> mdcEntry : mdc.entrySet()) {
/* 156 */         if (Sentry.getStoredClient().getMdcTags().contains(mdcEntry.getKey())) {
/* 157 */           eventBuilder.withTag(mdcEntry.getKey(), mdcEntry.getValue()); continue;
/*     */         } 
/* 159 */         eventBuilder.withExtra(mdcEntry.getKey(), mdcEntry.getValue());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 164 */     eventBuilder.withExtra("Sentry-ThreadId", Integer.valueOf(record.getThreadID()));
/*     */     
/* 166 */     return eventBuilder;
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
/*     */   protected String formatMessage(String message, Object[] parameters) {
/*     */     String formatted;
/* 179 */     if (this.printfStyle) {
/* 180 */       formatted = String.format(message, parameters);
/*     */     } else {
/* 182 */       formatted = MessageFormat.format(message, parameters);
/*     */     } 
/* 184 */     return formatted;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() {}
/*     */ 
/*     */   
/*     */   public void close() throws SecurityException {
/* 193 */     SentryEnvironment.startManagingThread();
/*     */     try {
/* 195 */       Sentry.close();
/* 196 */     } catch (RuntimeException e) {
/* 197 */       reportError("An exception occurred while closing the Sentry connection", e, 3);
/*     */     } finally {
/* 199 */       SentryEnvironment.stopManagingThread();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setPrintfStyle(boolean printfStyle) {
/* 204 */     this.printfStyle = printfStyle;
/*     */   }
/*     */   
/*     */   private class DropSentryFilter implements Filter { private DropSentryFilter() {}
/*     */     
/*     */     public boolean isLoggable(LogRecord record) {
/* 210 */       String loggerName = record.getLoggerName();
/* 211 */       return (loggerName == null || !loggerName.startsWith("io.sentry"));
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\jul\SentryHandler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */