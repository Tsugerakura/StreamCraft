/*     */ package org.slf4j.event;
/*     */ 
/*     */ import java.util.Queue;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.Marker;
/*     */ import org.slf4j.helpers.SubstituteLogger;
/*     */ 
/*     */ public class EventRecodingLogger
/*     */   implements Logger
/*     */ {
/*     */   String name;
/*     */   SubstituteLogger logger;
/*     */   Queue<SubstituteLoggingEvent> eventQueue;
/*     */   
/*     */   public EventRecodingLogger(SubstituteLogger logger, Queue<SubstituteLoggingEvent> eventQueue) {
/*  16 */     this.logger = logger;
/*  17 */     this.name = logger.getName();
/*  18 */     this.eventQueue = eventQueue;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  22 */     return this.name;
/*     */   }
/*     */   
/*     */   private void recordEvent(Level level, String msg, Object[] args, Throwable throwable) {
/*  26 */     recordEvent(level, null, msg, args, throwable);
/*     */   }
/*     */ 
/*     */   
/*     */   private void recordEvent(Level level, Marker marker, String msg, Object[] args, Throwable throwable) {
/*  31 */     SubstituteLoggingEvent loggingEvent = new SubstituteLoggingEvent();
/*  32 */     loggingEvent.setTimeStamp(System.currentTimeMillis());
/*  33 */     loggingEvent.setLevel(level);
/*  34 */     loggingEvent.setLogger(this.logger);
/*  35 */     loggingEvent.setLoggerName(this.name);
/*  36 */     loggingEvent.setMarker(marker);
/*  37 */     loggingEvent.setMessage(msg);
/*  38 */     loggingEvent.setArgumentArray(args);
/*  39 */     loggingEvent.setThrowable(throwable);
/*  40 */     loggingEvent.setThreadName(Thread.currentThread().getName());
/*  41 */     this.eventQueue.add(loggingEvent);
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled() {
/*  45 */     return true;
/*     */   }
/*     */   
/*     */   public void trace(String msg) {
/*  49 */     recordEvent(Level.TRACE, msg, null, null);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object arg) {
/*  53 */     recordEvent(Level.TRACE, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object arg1, Object arg2) {
/*  57 */     recordEvent(Level.TRACE, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */   public void trace(String format, Object... arguments) {
/*  61 */     recordEvent(Level.TRACE, format, arguments, null);
/*     */   }
/*     */   
/*     */   public void trace(String msg, Throwable t) {
/*  65 */     recordEvent(Level.TRACE, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isTraceEnabled(Marker marker) {
/*  69 */     return true;
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String msg) {
/*  73 */     recordEvent(Level.TRACE, marker, msg, null, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(Marker marker, String format, Object arg) {
/*  78 */     recordEvent(Level.TRACE, marker, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object arg1, Object arg2) {
/*  82 */     recordEvent(Level.TRACE, marker, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */   public void trace(Marker marker, String format, Object... argArray) {
/*  86 */     recordEvent(Level.TRACE, marker, format, argArray, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void trace(Marker marker, String msg, Throwable t) {
/*  91 */     recordEvent(Level.TRACE, marker, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled() {
/*  95 */     return true;
/*     */   }
/*     */   
/*     */   public void debug(String msg) {
/*  99 */     recordEvent(Level.TRACE, msg, null, null);
/*     */   }
/*     */   
/*     */   public void debug(String format, Object arg) {
/* 103 */     recordEvent(Level.DEBUG, format, new Object[] { arg }, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String format, Object arg1, Object arg2) {
/* 108 */     recordEvent(Level.DEBUG, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void debug(String format, Object... arguments) {
/* 113 */     recordEvent(Level.DEBUG, format, arguments, null);
/*     */   }
/*     */   
/*     */   public void debug(String msg, Throwable t) {
/* 117 */     recordEvent(Level.DEBUG, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isDebugEnabled(Marker marker) {
/* 121 */     return true;
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String msg) {
/* 125 */     recordEvent(Level.DEBUG, marker, msg, null, null);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object arg) {
/* 129 */     recordEvent(Level.DEBUG, marker, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object arg1, Object arg2) {
/* 133 */     recordEvent(Level.DEBUG, marker, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String format, Object... arguments) {
/* 137 */     recordEvent(Level.DEBUG, marker, format, arguments, null);
/*     */   }
/*     */   
/*     */   public void debug(Marker marker, String msg, Throwable t) {
/* 141 */     recordEvent(Level.DEBUG, marker, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled() {
/* 145 */     return true;
/*     */   }
/*     */   
/*     */   public void info(String msg) {
/* 149 */     recordEvent(Level.INFO, msg, null, null);
/*     */   }
/*     */   
/*     */   public void info(String format, Object arg) {
/* 153 */     recordEvent(Level.INFO, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */   public void info(String format, Object arg1, Object arg2) {
/* 157 */     recordEvent(Level.INFO, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */   public void info(String format, Object... arguments) {
/* 161 */     recordEvent(Level.INFO, format, arguments, null);
/*     */   }
/*     */   
/*     */   public void info(String msg, Throwable t) {
/* 165 */     recordEvent(Level.INFO, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isInfoEnabled(Marker marker) {
/* 169 */     return true;
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String msg) {
/* 173 */     recordEvent(Level.INFO, marker, msg, null, null);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object arg) {
/* 177 */     recordEvent(Level.INFO, marker, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object arg1, Object arg2) {
/* 181 */     recordEvent(Level.INFO, marker, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String format, Object... arguments) {
/* 185 */     recordEvent(Level.INFO, marker, format, arguments, null);
/*     */   }
/*     */   
/*     */   public void info(Marker marker, String msg, Throwable t) {
/* 189 */     recordEvent(Level.INFO, marker, msg, null, t);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWarnEnabled() {
/* 194 */     return true;
/*     */   }
/*     */   
/*     */   public void warn(String msg) {
/* 198 */     recordEvent(Level.WARN, msg, null, null);
/*     */   }
/*     */   
/*     */   public void warn(String format, Object arg) {
/* 202 */     recordEvent(Level.WARN, format, new Object[] { arg }, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(String format, Object arg1, Object arg2) {
/* 207 */     recordEvent(Level.WARN, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */   public void warn(String format, Object... arguments) {
/* 211 */     recordEvent(Level.WARN, format, arguments, null);
/*     */   }
/*     */   
/*     */   public void warn(String msg, Throwable t) {
/* 215 */     recordEvent(Level.WARN, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isWarnEnabled(Marker marker) {
/* 219 */     return true;
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String msg) {
/* 223 */     recordEvent(Level.WARN, msg, null, null);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object arg) {
/* 227 */     recordEvent(Level.WARN, format, new Object[] { arg }, null);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String format, Object arg1, Object arg2) {
/* 231 */     recordEvent(Level.WARN, marker, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void warn(Marker marker, String format, Object... arguments) {
/* 236 */     recordEvent(Level.WARN, marker, format, arguments, null);
/*     */   }
/*     */   
/*     */   public void warn(Marker marker, String msg, Throwable t) {
/* 240 */     recordEvent(Level.WARN, marker, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled() {
/* 244 */     return true;
/*     */   }
/*     */   
/*     */   public void error(String msg) {
/* 248 */     recordEvent(Level.ERROR, msg, null, null);
/*     */   }
/*     */   
/*     */   public void error(String format, Object arg) {
/* 252 */     recordEvent(Level.ERROR, format, new Object[] { arg }, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String format, Object arg1, Object arg2) {
/* 257 */     recordEvent(Level.ERROR, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String format, Object... arguments) {
/* 262 */     recordEvent(Level.ERROR, format, arguments, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(String msg, Throwable t) {
/* 267 */     recordEvent(Level.ERROR, msg, null, t);
/*     */   }
/*     */   
/*     */   public boolean isErrorEnabled(Marker marker) {
/* 271 */     return true;
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String msg) {
/* 275 */     recordEvent(Level.ERROR, marker, msg, null, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(Marker marker, String format, Object arg) {
/* 280 */     recordEvent(Level.ERROR, marker, format, new Object[] { arg }, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void error(Marker marker, String format, Object arg1, Object arg2) {
/* 285 */     recordEvent(Level.ERROR, marker, format, new Object[] { arg1, arg2 }, null);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String format, Object... arguments) {
/* 289 */     recordEvent(Level.ERROR, marker, format, arguments, null);
/*     */   }
/*     */   
/*     */   public void error(Marker marker, String msg, Throwable t) {
/* 293 */     recordEvent(Level.ERROR, marker, msg, null, t);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\slf4j\event\EventRecodingLogger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */