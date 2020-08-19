/*     */ package io.sentry.event.interfaces;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ public class MessageInterface
/*     */   implements SentryInterface
/*     */ {
/*     */   public static final String MESSAGE_INTERFACE = "sentry.interfaces.Message";
/*     */   private final String message;
/*     */   private final List<String> parameters;
/*     */   private final String formatted;
/*     */   
/*     */   public MessageInterface(String message) {
/*  44 */     this(message, Collections.emptyList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageInterface(String message, String... params) {
/*  54 */     this(message, Arrays.asList(params));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageInterface(String message, List<String> parameters) {
/*  64 */     this(message, parameters, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MessageInterface(String message, List<String> parameters, String formatted) {
/*  75 */     this.message = message;
/*  76 */     this.parameters = Collections.unmodifiableList(new ArrayList<>(parameters));
/*  77 */     this.formatted = formatted;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getInterfaceName() {
/*  82 */     return "sentry.interfaces.Message";
/*     */   }
/*     */   
/*     */   public String getMessage() {
/*  86 */     return this.message;
/*     */   }
/*     */   
/*     */   public List<String> getParameters() {
/*  90 */     return this.parameters;
/*     */   }
/*     */   
/*     */   public String getFormatted() {
/*  94 */     return this.formatted;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  99 */     return "MessageInterface{message='" + this.message + '\'' + ", parameters=" + this.parameters + ", formatted=" + this.formatted + '}';
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 108 */     if (this == o) {
/* 109 */       return true;
/*     */     }
/* 111 */     if (o == null || getClass() != o.getClass()) {
/* 112 */       return false;
/*     */     }
/* 114 */     MessageInterface that = (MessageInterface)o;
/* 115 */     return (Objects.equals(this.message, that.message) && 
/* 116 */       Objects.equals(this.parameters, that.parameters) && 
/* 117 */       Objects.equals(this.formatted, that.formatted));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 122 */     return Objects.hash(new Object[] { this.message, this.parameters, this.formatted });
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\event\interfaces\MessageInterface.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */