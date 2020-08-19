/*     */ package io.sentry.marshaller.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import io.sentry.event.interfaces.SentryInterface;
/*     */ import io.sentry.event.interfaces.SentryStackTraceElement;
/*     */ import io.sentry.event.interfaces.StackTraceInterface;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class StackTraceInterfaceBinding
/*     */   implements InterfaceBinding<StackTraceInterface> {
/*     */   private static final String FRAMES_PARAMETER = "frames";
/*     */   private static final String FILENAME_PARAMETER = "filename";
/*     */   private static final String FUNCTION_PARAMETER = "function";
/*     */   private static final String MODULE_PARAMETER = "module";
/*     */   private static final String LINE_NO_PARAMETER = "lineno";
/*     */   private static final String COL_NO_PARAMETER = "colno";
/*     */   private static final String ABSOLUTE_PATH_PARAMETER = "abs_path";
/*     */   private static final String CONTEXT_LINE_PARAMETER = "context_line";
/*     */   private static final String PRE_CONTEXT_PARAMETER = "pre_context";
/*     */   private static final String POST_CONTEXT_PARAMETER = "post_context";
/*     */   private static final String IN_APP_PARAMETER = "in_app";
/*     */   private static final String VARIABLES_PARAMETER = "vars";
/*     */   private static final String PLATFORM_PARAMTER = "platform";
/*  28 */   private static final Pattern IN_APP_BLACKLIST = Pattern.compile("\\$+(?:(?:EnhancerBy[a-zA-Z]*)|(?:FastClassBy[a-zA-Z]*)|(?:HibernateProxy))\\$+");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   private Collection<String> inAppFrames = Collections.emptyList();
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean removeCommonFramesWithEnclosing = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeFrame(JsonGenerator generator, SentryStackTraceElement stackTraceElement, boolean commonWithEnclosing) throws IOException {
/*  44 */     generator.writeStartObject();
/*  45 */     generator.writeStringField("filename", stackTraceElement.getFileName());
/*  46 */     generator.writeStringField("module", stackTraceElement.getModule());
/*  47 */     boolean inApp = ((!this.removeCommonFramesWithEnclosing || !commonWithEnclosing) && isFrameInApp(stackTraceElement));
/*  48 */     generator.writeBooleanField("in_app", inApp);
/*  49 */     generator.writeStringField("function", stackTraceElement.getFunction());
/*  50 */     generator.writeNumberField("lineno", stackTraceElement.getLineno());
/*     */ 
/*     */     
/*  53 */     if (stackTraceElement.getColno() != null) {
/*  54 */       generator.writeNumberField("colno", stackTraceElement.getColno().intValue());
/*     */     }
/*     */     
/*  57 */     if (stackTraceElement.getPlatform() != null) {
/*  58 */       generator.writeStringField("platform", stackTraceElement.getPlatform());
/*     */     }
/*     */     
/*  61 */     if (stackTraceElement.getAbsPath() != null) {
/*  62 */       generator.writeStringField("abs_path", stackTraceElement.getAbsPath());
/*     */     }
/*     */     
/*  65 */     if (stackTraceElement.getLocals() != null && !stackTraceElement.getLocals().isEmpty()) {
/*  66 */       generator.writeObjectFieldStart("vars");
/*  67 */       for (Map.Entry<String, Object> varEntry : (Iterable<Map.Entry<String, Object>>)stackTraceElement.getLocals().entrySet()) {
/*  68 */         generator.writeFieldName(varEntry.getKey());
/*  69 */         generator.writeObject(varEntry.getValue());
/*     */       } 
/*  71 */       generator.writeEndObject();
/*     */     } 
/*     */     
/*  74 */     generator.writeEndObject();
/*     */   }
/*     */   
/*     */   private boolean isFrameInApp(SentryStackTraceElement stackTraceElement) {
/*  78 */     String className = stackTraceElement.getModule();
/*  79 */     if (classIsBlacklisted(className)) {
/*  80 */       return false;
/*     */     }
/*     */     
/*  83 */     for (String inAppFrame : this.inAppFrames) {
/*  84 */       if (className.startsWith(inAppFrame)) {
/*  85 */         return true;
/*     */       }
/*     */     } 
/*  88 */     return false;
/*     */   }
/*     */   
/*     */   private boolean classIsBlacklisted(String className) {
/*  92 */     return ((className.contains("$$EnhancerBy") || className
/*  93 */       .contains("$$FastClassBy") || className
/*  94 */       .contains("$Hibernate")) && IN_APP_BLACKLIST
/*  95 */       .matcher(className).find());
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeInterface(JsonGenerator generator, StackTraceInterface stackTraceInterface) throws IOException {
/* 100 */     generator.writeStartObject();
/* 101 */     generator.writeArrayFieldStart("frames");
/* 102 */     SentryStackTraceElement[] sentryStackTrace = stackTraceInterface.getStackTrace();
/* 103 */     int commonWithEnclosing = stackTraceInterface.getFramesCommonWithEnclosing();
/* 104 */     for (int i = sentryStackTrace.length - 1; i >= 0; i--) {
/* 105 */       writeFrame(generator, sentryStackTrace[i], (commonWithEnclosing-- > 0));
/*     */     }
/* 107 */     generator.writeEndArray();
/* 108 */     generator.writeEndObject();
/*     */   }
/*     */   
/*     */   public void setRemoveCommonFramesWithEnclosing(boolean removeCommonFramesWithEnclosing) {
/* 112 */     this.removeCommonFramesWithEnclosing = removeCommonFramesWithEnclosing;
/*     */   }
/*     */   
/*     */   public void setInAppFrames(Collection<String> inAppFrames) {
/* 116 */     this.inAppFrames = inAppFrames;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\marshaller\json\StackTraceInterfaceBinding.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */