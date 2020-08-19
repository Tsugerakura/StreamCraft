/*     */ package io.sentry.event.interfaces;
/*     */ 
/*     */ import io.sentry.jvmti.Frame;
/*     */ import java.util.Arrays;
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
/*     */ public class StackTraceInterface
/*     */   implements SentryInterface
/*     */ {
/*     */   public static final String STACKTRACE_INTERFACE = "sentry.interfaces.Stacktrace";
/*     */   private final SentryStackTraceElement[] stackTrace;
/*     */   private final int framesCommonWithEnclosing;
/*     */   
/*     */   public StackTraceInterface(StackTraceElement[] stackTrace) {
/*  24 */     this(stackTrace, new StackTraceElement[0], null);
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
/*     */   public StackTraceInterface(StackTraceElement[] stackTrace, StackTraceElement[] enclosingStackTrace) {
/*  38 */     this(stackTrace, enclosingStackTrace, null);
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
/*     */   public StackTraceInterface(StackTraceElement[] stackTrace, StackTraceElement[] enclosingStackTrace, Frame[] cachedFrames) {
/*  55 */     this.stackTrace = SentryStackTraceElement.fromStackTraceElements(stackTrace, cachedFrames);
/*     */     
/*  57 */     int m = stackTrace.length - 1;
/*  58 */     int n = enclosingStackTrace.length - 1;
/*  59 */     while (m >= 0 && n >= 0 && stackTrace[m].equals(enclosingStackTrace[n])) {
/*  60 */       m--;
/*  61 */       n--;
/*     */     } 
/*  63 */     this.framesCommonWithEnclosing = stackTrace.length - 1 - m;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StackTraceInterface(SentryStackTraceElement[] stackTrace) {
/*  72 */     this.stackTrace = Arrays.<SentryStackTraceElement>copyOf(stackTrace, stackTrace.length);
/*  73 */     this.framesCommonWithEnclosing = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getInterfaceName() {
/*  78 */     return "sentry.interfaces.Stacktrace";
/*     */   }
/*     */   
/*     */   public SentryStackTraceElement[] getStackTrace() {
/*  82 */     return Arrays.<SentryStackTraceElement>copyOf(this.stackTrace, this.stackTrace.length);
/*     */   }
/*     */   
/*     */   public int getFramesCommonWithEnclosing() {
/*  86 */     return this.framesCommonWithEnclosing;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  91 */     if (this == o) {
/*  92 */       return true;
/*     */     }
/*  94 */     if (o == null || getClass() != o.getClass()) {
/*  95 */       return false;
/*     */     }
/*     */     
/*  98 */     StackTraceInterface that = (StackTraceInterface)o;
/*     */     
/* 100 */     return Arrays.equals((Object[])this.stackTrace, (Object[])that.stackTrace);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 105 */     return Arrays.hashCode((Object[])this.stackTrace);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 110 */     return "StackTraceInterface{stackTrace=" + 
/* 111 */       Arrays.toString((Object[])this.stackTrace) + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\interfaces\StackTraceInterface.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */