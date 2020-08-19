/*     */ package io.sentry.event.interfaces;
/*     */ 
/*     */ import io.sentry.jvmti.Frame;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class SentryStackTraceElement
/*     */   implements Serializable
/*     */ {
/*     */   private final String module;
/*     */   private final String function;
/*     */   private final String fileName;
/*     */   private final int lineno;
/*     */   private final Integer colno;
/*     */   private final String absPath;
/*     */   private final String platform;
/*     */   private final Map<String, Object> locals;
/*     */   
/*     */   public SentryStackTraceElement(String module, String function, String fileName, int lineno, Integer colno, String absPath, String platform) {
/*  36 */     this(module, function, fileName, lineno, colno, absPath, platform, null);
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
/*     */   public SentryStackTraceElement(String module, String function, String fileName, int lineno, Integer colno, String absPath, String platform, Map<String, Object> locals) {
/*  55 */     this.module = module;
/*  56 */     this.function = function;
/*  57 */     this.fileName = fileName;
/*  58 */     this.lineno = lineno;
/*  59 */     this.colno = colno;
/*  60 */     this.absPath = absPath;
/*  61 */     this.platform = platform;
/*  62 */     this.locals = locals;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getModule() {
/*  67 */     return this.module;
/*     */   }
/*     */   
/*     */   public String getFunction() {
/*  71 */     return this.function;
/*     */   }
/*     */   
/*     */   public String getFileName() {
/*  75 */     return this.fileName;
/*     */   }
/*     */   
/*     */   public int getLineno() {
/*  79 */     return this.lineno;
/*     */   }
/*     */   
/*     */   public Integer getColno() {
/*  83 */     return this.colno;
/*     */   }
/*     */   
/*     */   public String getAbsPath() {
/*  87 */     return this.absPath;
/*     */   }
/*     */   
/*     */   public String getPlatform() {
/*  91 */     return this.platform;
/*     */   }
/*     */   
/*     */   public Map<String, Object> getLocals() {
/*  95 */     return this.locals;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SentryStackTraceElement[] fromStackTraceElements(StackTraceElement[] stackTraceElements) {
/* 105 */     return fromStackTraceElements(stackTraceElements, null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SentryStackTraceElement[] fromStackTraceElements(StackTraceElement[] stackTraceElements, Frame[] cachedFrames) {
/* 139 */     SentryStackTraceElement[] sentryStackTraceElements = new SentryStackTraceElement[stackTraceElements.length];
/* 140 */     for (int i = 0, j = 0; i < stackTraceElements.length; i++, j++) {
/* 141 */       StackTraceElement stackTraceElement = stackTraceElements[i];
/*     */       
/* 143 */       Map<String, Object> locals = null;
/* 144 */       if (cachedFrames != null) {
/*     */         
/* 146 */         while (j < cachedFrames.length && 
/* 147 */           !stackTraceElement.getMethodName().equals(cachedFrames[j].getMethod().getName())) {
/* 148 */           j++;
/*     */         }
/*     */ 
/*     */         
/* 152 */         if (j < cachedFrames.length) {
/* 153 */           locals = cachedFrames[j].getLocals();
/*     */         }
/*     */       } 
/*     */       
/* 157 */       sentryStackTraceElements[i] = fromStackTraceElement(stackTraceElement, locals);
/*     */     } 
/*     */     
/* 160 */     return sentryStackTraceElements;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SentryStackTraceElement fromStackTraceElement(StackTraceElement stackTraceElement) {
/* 170 */     return fromStackTraceElement(stackTraceElement, null);
/*     */   }
/*     */ 
/*     */   
/*     */   private static SentryStackTraceElement fromStackTraceElement(StackTraceElement stackTraceElement, Map<String, Object> locals) {
/* 175 */     return new SentryStackTraceElement(stackTraceElement
/* 176 */         .getClassName(), stackTraceElement
/* 177 */         .getMethodName(), stackTraceElement
/* 178 */         .getFileName(), stackTraceElement
/* 179 */         .getLineNumber(), null, null, null, locals);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 189 */     if (this == o) {
/* 190 */       return true;
/*     */     }
/* 192 */     if (o == null || getClass() != o.getClass()) {
/* 193 */       return false;
/*     */     }
/* 195 */     SentryStackTraceElement that = (SentryStackTraceElement)o;
/* 196 */     return (this.lineno == that.lineno && 
/* 197 */       Objects.equals(this.module, that.module) && 
/* 198 */       Objects.equals(this.function, that.function) && 
/* 199 */       Objects.equals(this.fileName, that.fileName) && 
/* 200 */       Objects.equals(this.colno, that.colno) && 
/* 201 */       Objects.equals(this.absPath, that.absPath) && 
/* 202 */       Objects.equals(this.platform, that.platform) && 
/* 203 */       Objects.equals(this.locals, that.locals));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 208 */     return Objects.hash(new Object[] { this.module, this.function, this.fileName, Integer.valueOf(this.lineno), this.colno, this.absPath, this.platform, this.locals });
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 213 */     return "SentryStackTraceElement{module='" + this.module + '\'' + ", function='" + this.function + '\'' + ", fileName='" + this.fileName + '\'' + ", lineno=" + this.lineno + ", colno=" + this.colno + ", absPath='" + this.absPath + '\'' + ", platform='" + this.platform + '\'' + ", locals='" + this.locals + '\'' + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\interfaces\SentryStackTraceElement.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */