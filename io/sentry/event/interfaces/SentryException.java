/*     */ package io.sentry.event.interfaces;
/*     */ 
/*     */ import io.sentry.jvmti.FrameCache;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ public final class SentryException
/*     */   implements Serializable
/*     */ {
/*     */   public static final String DEFAULT_PACKAGE_NAME = "(default)";
/*     */   private final String exceptionMessage;
/*     */   private final String exceptionClassName;
/*     */   private final String exceptionPackageName;
/*     */   private final StackTraceInterface stackTraceInterface;
/*     */   private final ExceptionMechanism exceptionMechanism;
/*     */   
/*     */   public SentryException(Throwable throwable, StackTraceElement[] childExceptionStackTrace) {
/*  34 */     this(throwable, childExceptionStackTrace, null);
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
/*     */   public SentryException(Throwable throwable, StackTraceElement[] childExceptionStackTrace, ExceptionMechanism exceptionMechanism) {
/*  53 */     Package exceptionPackage = throwable.getClass().getPackage();
/*  54 */     String fullClassName = throwable.getClass().getName();
/*     */     
/*  56 */     this.exceptionMessage = throwable.getMessage();
/*  57 */     this
/*  58 */       .exceptionClassName = (exceptionPackage != null) ? fullClassName.replace(exceptionPackage.getName() + ".", "") : fullClassName;
/*     */ 
/*     */     
/*  61 */     this
/*  62 */       .exceptionPackageName = (exceptionPackage != null) ? exceptionPackage.getName() : null;
/*     */ 
/*     */     
/*  65 */     this
/*     */ 
/*     */       
/*  68 */       .stackTraceInterface = new StackTraceInterface(throwable.getStackTrace(), childExceptionStackTrace, FrameCache.get(throwable));
/*     */     
/*  70 */     this.exceptionMechanism = exceptionMechanism;
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
/*     */   public SentryException(String exceptionMessage, String exceptionClassName, String exceptionPackageName, StackTraceInterface stackTraceInterface) {
/*  86 */     this(exceptionMessage, exceptionClassName, exceptionPackageName, stackTraceInterface, null);
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
/*     */   public SentryException(String exceptionMessage, String exceptionClassName, String exceptionPackageName, StackTraceInterface stackTraceInterface, ExceptionMechanism exceptionMechanism) {
/* 109 */     this.exceptionMessage = exceptionMessage;
/* 110 */     this.exceptionClassName = exceptionClassName;
/* 111 */     this.exceptionPackageName = exceptionPackageName;
/* 112 */     this.stackTraceInterface = stackTraceInterface;
/* 113 */     this.exceptionMechanism = exceptionMechanism;
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
/*     */   public static Deque<SentryException> extractExceptionQueue(Throwable throwable) {
/* 125 */     Deque<SentryException> exceptions = new ArrayDeque<>();
/* 126 */     Set<Throwable> circularityDetector = new HashSet<>();
/* 127 */     StackTraceElement[] childExceptionStackTrace = new StackTraceElement[0];
/* 128 */     ExceptionMechanism exceptionMechanism = null;
/*     */ 
/*     */     
/* 131 */     while (throwable != null && circularityDetector.add(throwable)) {
/* 132 */       if (throwable instanceof ExceptionMechanismThrowable) {
/* 133 */         ExceptionMechanismThrowable exceptionMechanismThrowable = (ExceptionMechanismThrowable)throwable;
/* 134 */         exceptionMechanism = exceptionMechanismThrowable.getExceptionMechanism();
/* 135 */         throwable = exceptionMechanismThrowable.getThrowable();
/*     */       } else {
/* 137 */         exceptionMechanism = null;
/*     */       } 
/*     */       
/* 140 */       exceptions.add(new SentryException(throwable, childExceptionStackTrace, exceptionMechanism));
/* 141 */       childExceptionStackTrace = throwable.getStackTrace();
/* 142 */       throwable = throwable.getCause();
/*     */     } 
/*     */     
/* 145 */     return exceptions;
/*     */   }
/*     */   
/*     */   public String getExceptionMessage() {
/* 149 */     return this.exceptionMessage;
/*     */   }
/*     */   
/*     */   public String getExceptionClassName() {
/* 153 */     return this.exceptionClassName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getExceptionPackageName() {
/* 164 */     return (this.exceptionPackageName != null) ? this.exceptionPackageName : "(default)";
/*     */   }
/*     */   
/*     */   public StackTraceInterface getStackTraceInterface() {
/* 168 */     return this.stackTraceInterface;
/*     */   }
/*     */   
/*     */   public ExceptionMechanism getExceptionMechanism() {
/* 172 */     return this.exceptionMechanism;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 177 */     return "SentryException{exceptionMessage='" + this.exceptionMessage + '\'' + ", exceptionClassName='" + this.exceptionClassName + '\'' + ", exceptionPackageName='" + this.exceptionPackageName + '\'' + ", exceptionMechanism='" + this.exceptionMechanism + '\'' + ", stackTraceInterface=" + this.stackTraceInterface + '}';
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 188 */     if (this == o) {
/* 189 */       return true;
/*     */     }
/* 191 */     if (o == null || getClass() != o.getClass()) {
/* 192 */       return false;
/*     */     }
/*     */     
/* 195 */     SentryException that = (SentryException)o;
/*     */     
/* 197 */     if (!this.exceptionClassName.equals(that.exceptionClassName)) {
/* 198 */       return false;
/*     */     }
/* 200 */     if ((this.exceptionMessage != null) ? !this.exceptionMessage.equals(that.exceptionMessage) : (that.exceptionMessage != null))
/*     */     {
/* 202 */       return false;
/*     */     }
/* 204 */     if ((this.exceptionPackageName != null) ? !this.exceptionPackageName.equals(that.exceptionPackageName) : (that.exceptionPackageName != null))
/*     */     {
/* 206 */       return false;
/*     */     }
/* 208 */     if ((this.exceptionMechanism != null) ? !this.exceptionMechanism.equals(that.exceptionMechanism) : (that.exceptionMechanism != null))
/*     */     {
/* 210 */       return false;
/*     */     }
/*     */     
/* 213 */     return this.stackTraceInterface.equals(that.stackTraceInterface);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 218 */     int result = (this.exceptionMessage != null) ? this.exceptionMessage.hashCode() : 0;
/* 219 */     result = 31 * result + this.exceptionClassName.hashCode();
/* 220 */     result = 31 * result + ((this.exceptionPackageName != null) ? this.exceptionPackageName.hashCode() : 0);
/* 221 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\interfaces\SentryException.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */