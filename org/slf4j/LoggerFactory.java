/*     */ package org.slf4j;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Enumeration;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import org.slf4j.event.LoggingEvent;
/*     */ import org.slf4j.event.SubstituteLoggingEvent;
/*     */ import org.slf4j.helpers.NOPLoggerFactory;
/*     */ import org.slf4j.helpers.SubstituteLogger;
/*     */ import org.slf4j.helpers.SubstituteLoggerFactory;
/*     */ import org.slf4j.helpers.Util;
/*     */ import org.slf4j.impl.StaticLoggerBinder;
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
/*     */ 
/*     */ public final class LoggerFactory
/*     */ {
/*     */   static final String CODES_PREFIX = "http://www.slf4j.org/codes.html";
/*     */   static final String NO_STATICLOGGERBINDER_URL = "http://www.slf4j.org/codes.html#StaticLoggerBinder";
/*     */   static final String MULTIPLE_BINDINGS_URL = "http://www.slf4j.org/codes.html#multiple_bindings";
/*     */   static final String NULL_LF_URL = "http://www.slf4j.org/codes.html#null_LF";
/*     */   static final String VERSION_MISMATCH = "http://www.slf4j.org/codes.html#version_mismatch";
/*     */   static final String SUBSTITUTE_LOGGER_URL = "http://www.slf4j.org/codes.html#substituteLogger";
/*     */   static final String LOGGER_NAME_MISMATCH_URL = "http://www.slf4j.org/codes.html#loggerNameMismatch";
/*     */   static final String REPLAY_URL = "http://www.slf4j.org/codes.html#replay";
/*     */   static final String UNSUCCESSFUL_INIT_URL = "http://www.slf4j.org/codes.html#unsuccessfulInit";
/*     */   static final String UNSUCCESSFUL_INIT_MSG = "org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also http://www.slf4j.org/codes.html#unsuccessfulInit";
/*     */   static final int UNINITIALIZED = 0;
/*     */   static final int ONGOING_INITIALIZATION = 1;
/*     */   static final int FAILED_INITIALIZATION = 2;
/*     */   static final int SUCCESSFUL_INITIALIZATION = 3;
/*     */   static final int NOP_FALLBACK_INITIALIZATION = 4;
/*  85 */   static volatile int INITIALIZATION_STATE = 0;
/*  86 */   static final SubstituteLoggerFactory SUBST_FACTORY = new SubstituteLoggerFactory();
/*  87 */   static final NOPLoggerFactory NOP_FALLBACK_FACTORY = new NOPLoggerFactory();
/*     */   
/*     */   static final String DETECT_LOGGER_NAME_MISMATCH_PROPERTY = "slf4j.detectLoggerNameMismatch";
/*     */   
/*     */   static final String JAVA_VENDOR_PROPERTY = "java.vendor.url";
/*     */   
/*  93 */   static boolean DETECT_LOGGER_NAME_MISMATCH = Util.safeGetBooleanSystemProperty("slf4j.detectLoggerNameMismatch");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 102 */   private static final String[] API_COMPATIBILITY_LIST = new String[] { "1.6", "1.7" };
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
/*     */   static void reset() {
/* 120 */     INITIALIZATION_STATE = 0;
/*     */   }
/*     */   
/*     */   private static final void performInitialization() {
/* 124 */     bind();
/* 125 */     if (INITIALIZATION_STATE == 3) {
/* 126 */       versionSanityCheck();
/*     */     }
/*     */   }
/*     */   
/*     */   private static boolean messageContainsOrgSlf4jImplStaticLoggerBinder(String msg) {
/* 131 */     if (msg == null)
/* 132 */       return false; 
/* 133 */     if (msg.contains("org/slf4j/impl/StaticLoggerBinder"))
/* 134 */       return true; 
/* 135 */     if (msg.contains("org.slf4j.impl.StaticLoggerBinder"))
/* 136 */       return true; 
/* 137 */     return false;
/*     */   }
/*     */   
/*     */   private static final void bind() {
/*     */     try {
/* 142 */       Set<URL> staticLoggerBinderPathSet = null;
/*     */ 
/*     */       
/* 145 */       if (!isAndroid()) {
/* 146 */         staticLoggerBinderPathSet = findPossibleStaticLoggerBinderPathSet();
/* 147 */         reportMultipleBindingAmbiguity(staticLoggerBinderPathSet);
/*     */       } 
/*     */       
/* 150 */       StaticLoggerBinder.getSingleton();
/* 151 */       INITIALIZATION_STATE = 3;
/* 152 */       reportActualBinding(staticLoggerBinderPathSet);
/* 153 */       fixSubstituteLoggers();
/* 154 */       replayEvents();
/*     */       
/* 156 */       SUBST_FACTORY.clear();
/* 157 */     } catch (NoClassDefFoundError ncde) {
/* 158 */       String msg = ncde.getMessage();
/* 159 */       if (messageContainsOrgSlf4jImplStaticLoggerBinder(msg)) {
/* 160 */         INITIALIZATION_STATE = 4;
/* 161 */         Util.report("Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".");
/* 162 */         Util.report("Defaulting to no-operation (NOP) logger implementation");
/* 163 */         Util.report("See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.");
/*     */       } else {
/* 165 */         failedBinding(ncde);
/* 166 */         throw ncde;
/*     */       } 
/* 168 */     } catch (NoSuchMethodError nsme) {
/* 169 */       String msg = nsme.getMessage();
/* 170 */       if (msg != null && msg.contains("org.slf4j.impl.StaticLoggerBinder.getSingleton()")) {
/* 171 */         INITIALIZATION_STATE = 2;
/* 172 */         Util.report("slf4j-api 1.6.x (or later) is incompatible with this binding.");
/* 173 */         Util.report("Your binding is version 1.5.5 or earlier.");
/* 174 */         Util.report("Upgrade your binding to version 1.6.x.");
/*     */       } 
/* 176 */       throw nsme;
/* 177 */     } catch (Exception e) {
/* 178 */       failedBinding(e);
/* 179 */       throw new IllegalStateException("Unexpected initialization failure", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void fixSubstituteLoggers() {
/* 184 */     synchronized (SUBST_FACTORY) {
/* 185 */       SUBST_FACTORY.postInitialization();
/* 186 */       for (SubstituteLogger substLogger : SUBST_FACTORY.getLoggers()) {
/* 187 */         Logger logger = getLogger(substLogger.getName());
/* 188 */         substLogger.setDelegate(logger);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   static void failedBinding(Throwable t) {
/* 195 */     INITIALIZATION_STATE = 2;
/* 196 */     Util.report("Failed to instantiate SLF4J LoggerFactory", t);
/*     */   }
/*     */   
/*     */   private static void replayEvents() {
/* 200 */     LinkedBlockingQueue<SubstituteLoggingEvent> queue = SUBST_FACTORY.getEventQueue();
/* 201 */     int queueSize = queue.size();
/* 202 */     int count = 0;
/* 203 */     int maxDrain = 128;
/* 204 */     List<SubstituteLoggingEvent> eventList = new ArrayList<SubstituteLoggingEvent>(128);
/*     */     while (true) {
/* 206 */       int numDrained = queue.drainTo(eventList, 128);
/* 207 */       if (numDrained == 0)
/*     */         break; 
/* 209 */       for (SubstituteLoggingEvent event : eventList) {
/* 210 */         replaySingleEvent(event);
/* 211 */         if (count++ == 0)
/* 212 */           emitReplayOrSubstituionWarning(event, queueSize); 
/*     */       } 
/* 214 */       eventList.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void emitReplayOrSubstituionWarning(SubstituteLoggingEvent event, int queueSize) {
/* 219 */     if (event.getLogger().isDelegateEventAware()) {
/* 220 */       emitReplayWarning(queueSize);
/* 221 */     } else if (!event.getLogger().isDelegateNOP()) {
/*     */ 
/*     */       
/* 224 */       emitSubstitutionWarning();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void replaySingleEvent(SubstituteLoggingEvent event) {
/* 229 */     if (event == null) {
/*     */       return;
/*     */     }
/* 232 */     SubstituteLogger substLogger = event.getLogger();
/* 233 */     String loggerName = substLogger.getName();
/* 234 */     if (substLogger.isDelegateNull()) {
/* 235 */       throw new IllegalStateException("Delegate logger cannot be null at this state.");
/*     */     }
/*     */     
/* 238 */     if (!substLogger.isDelegateNOP())
/*     */     {
/* 240 */       if (substLogger.isDelegateEventAware()) {
/* 241 */         substLogger.log((LoggingEvent)event);
/*     */       } else {
/* 243 */         Util.report(loggerName);
/*     */       }  } 
/*     */   }
/*     */   
/*     */   private static void emitSubstitutionWarning() {
/* 248 */     Util.report("The following set of substitute loggers may have been accessed");
/* 249 */     Util.report("during the initialization phase. Logging calls during this");
/* 250 */     Util.report("phase were not honored. However, subsequent logging calls to these");
/* 251 */     Util.report("loggers will work as normally expected.");
/* 252 */     Util.report("See also http://www.slf4j.org/codes.html#substituteLogger");
/*     */   }
/*     */   
/*     */   private static void emitReplayWarning(int eventCount) {
/* 256 */     Util.report("A number (" + eventCount + ") of logging calls during the initialization phase have been intercepted and are");
/* 257 */     Util.report("now being replayed. These are subject to the filtering rules of the underlying logging system.");
/* 258 */     Util.report("See also http://www.slf4j.org/codes.html#replay");
/*     */   }
/*     */   
/*     */   private static final void versionSanityCheck() {
/*     */     try {
/* 263 */       String requested = StaticLoggerBinder.REQUESTED_API_VERSION;
/*     */       
/* 265 */       boolean match = false;
/* 266 */       for (String aAPI_COMPATIBILITY_LIST : API_COMPATIBILITY_LIST) {
/* 267 */         if (requested.startsWith(aAPI_COMPATIBILITY_LIST)) {
/* 268 */           match = true;
/*     */         }
/*     */       } 
/* 271 */       if (!match) {
/* 272 */         Util.report("The requested version " + requested + " by your slf4j binding is not compatible with " + Arrays.<String>asList(API_COMPATIBILITY_LIST).toString());
/*     */         
/* 274 */         Util.report("See http://www.slf4j.org/codes.html#version_mismatch for further details.");
/*     */       } 
/* 276 */     } catch (NoSuchFieldError nsfe) {
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 281 */     catch (Throwable e) {
/*     */       
/* 283 */       Util.report("Unexpected problem occured during version sanity check", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 290 */   private static String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Set<URL> findPossibleStaticLoggerBinderPathSet() {
/* 296 */     Set<URL> staticLoggerBinderPathSet = new LinkedHashSet<URL>(); try {
/*     */       Enumeration<URL> paths;
/* 298 */       ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
/*     */       
/* 300 */       if (loggerFactoryClassLoader == null) {
/* 301 */         paths = ClassLoader.getSystemResources(STATIC_LOGGER_BINDER_PATH);
/*     */       } else {
/* 303 */         paths = loggerFactoryClassLoader.getResources(STATIC_LOGGER_BINDER_PATH);
/*     */       } 
/* 305 */       while (paths.hasMoreElements()) {
/* 306 */         URL path = paths.nextElement();
/* 307 */         staticLoggerBinderPathSet.add(path);
/*     */       } 
/* 309 */     } catch (IOException ioe) {
/* 310 */       Util.report("Error getting resources from path", ioe);
/*     */     } 
/* 312 */     return staticLoggerBinderPathSet;
/*     */   }
/*     */   
/*     */   private static boolean isAmbiguousStaticLoggerBinderPathSet(Set<URL> binderPathSet) {
/* 316 */     return (binderPathSet.size() > 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void reportMultipleBindingAmbiguity(Set<URL> binderPathSet) {
/* 325 */     if (isAmbiguousStaticLoggerBinderPathSet(binderPathSet)) {
/* 326 */       Util.report("Class path contains multiple SLF4J bindings.");
/* 327 */       for (URL path : binderPathSet) {
/* 328 */         Util.report("Found binding in [" + path + "]");
/*     */       }
/* 330 */       Util.report("See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean isAndroid() {
/* 335 */     String vendor = Util.safeGetSystemProperty("java.vendor.url");
/* 336 */     if (vendor == null)
/* 337 */       return false; 
/* 338 */     return vendor.toLowerCase().contains("android");
/*     */   }
/*     */ 
/*     */   
/*     */   private static void reportActualBinding(Set<URL> binderPathSet) {
/* 343 */     if (binderPathSet != null && isAmbiguousStaticLoggerBinderPathSet(binderPathSet)) {
/* 344 */       Util.report("Actual binding is of type [" + StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr() + "]");
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
/*     */   public static Logger getLogger(String name) {
/* 357 */     ILoggerFactory iLoggerFactory = getILoggerFactory();
/* 358 */     return iLoggerFactory.getLogger(name);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Logger getLogger(Class<?> clazz) {
/* 383 */     Logger logger = getLogger(clazz.getName());
/* 384 */     if (DETECT_LOGGER_NAME_MISMATCH) {
/* 385 */       Class<?> autoComputedCallingClass = Util.getCallingClass();
/* 386 */       if (autoComputedCallingClass != null && nonMatchingClasses(clazz, autoComputedCallingClass)) {
/* 387 */         Util.report(String.format("Detected logger name mismatch. Given name: \"%s\"; computed name: \"%s\".", new Object[] { logger.getName(), autoComputedCallingClass.getName() }));
/*     */         
/* 389 */         Util.report("See http://www.slf4j.org/codes.html#loggerNameMismatch for an explanation");
/*     */       } 
/*     */     } 
/* 392 */     return logger;
/*     */   }
/*     */   
/*     */   private static boolean nonMatchingClasses(Class<?> clazz, Class<?> autoComputedCallingClass) {
/* 396 */     return !autoComputedCallingClass.isAssignableFrom(clazz);
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
/*     */   public static ILoggerFactory getILoggerFactory() {
/* 408 */     if (INITIALIZATION_STATE == 0) {
/* 409 */       synchronized (LoggerFactory.class) {
/* 410 */         if (INITIALIZATION_STATE == 0) {
/* 411 */           INITIALIZATION_STATE = 1;
/* 412 */           performInitialization();
/*     */         } 
/*     */       } 
/*     */     }
/* 416 */     switch (INITIALIZATION_STATE) {
/*     */       case 3:
/* 418 */         return StaticLoggerBinder.getSingleton().getLoggerFactory();
/*     */       case 4:
/* 420 */         return (ILoggerFactory)NOP_FALLBACK_FACTORY;
/*     */       case 2:
/* 422 */         throw new IllegalStateException("org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also http://www.slf4j.org/codes.html#unsuccessfulInit");
/*     */ 
/*     */       
/*     */       case 1:
/* 426 */         return (ILoggerFactory)SUBST_FACTORY;
/*     */     } 
/* 428 */     throw new IllegalStateException("Unreachable code");
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\slf4j\LoggerFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */