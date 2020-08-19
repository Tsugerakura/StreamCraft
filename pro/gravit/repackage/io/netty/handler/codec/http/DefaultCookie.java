/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.cookie.DefaultCookie;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ @Deprecated
/*     */ public class DefaultCookie
/*     */   extends DefaultCookie
/*     */   implements Cookie
/*     */ {
/*     */   private String comment;
/*     */   private String commentUrl;
/*     */   private boolean discard;
/*  35 */   private Set<Integer> ports = Collections.emptySet();
/*  36 */   private Set<Integer> unmodifiablePorts = this.ports;
/*     */ 
/*     */   
/*     */   private int version;
/*     */ 
/*     */   
/*     */   public DefaultCookie(String name, String value) {
/*  43 */     super(name, value);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String getName() {
/*  49 */     return name();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String getValue() {
/*  55 */     return value();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String getDomain() {
/*  61 */     return domain();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String getPath() {
/*  67 */     return path();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String getComment() {
/*  73 */     return comment();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String comment() {
/*  79 */     return this.comment;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setComment(String comment) {
/*  85 */     this.comment = validateValue("comment", comment);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String getCommentUrl() {
/*  91 */     return commentUrl();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String commentUrl() {
/*  97 */     return this.commentUrl;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setCommentUrl(String commentUrl) {
/* 103 */     this.commentUrl = validateValue("commentUrl", commentUrl);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public boolean isDiscard() {
/* 109 */     return this.discard;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setDiscard(boolean discard) {
/* 115 */     this.discard = discard;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Set<Integer> getPorts() {
/* 121 */     return ports();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public Set<Integer> ports() {
/* 127 */     if (this.unmodifiablePorts == null) {
/* 128 */       this.unmodifiablePorts = Collections.unmodifiableSet(this.ports);
/*     */     }
/* 130 */     return this.unmodifiablePorts;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setPorts(int... ports) {
/* 136 */     ObjectUtil.checkNotNull(ports, "ports");
/*     */     
/* 138 */     int[] portsCopy = (int[])ports.clone();
/* 139 */     if (portsCopy.length == 0) {
/* 140 */       this.unmodifiablePorts = this.ports = Collections.emptySet();
/*     */     } else {
/* 142 */       Set<Integer> newPorts = new TreeSet<Integer>();
/* 143 */       for (int p : portsCopy) {
/* 144 */         if (p <= 0 || p > 65535) {
/* 145 */           throw new IllegalArgumentException("port out of range: " + p);
/*     */         }
/* 147 */         newPorts.add(Integer.valueOf(p));
/*     */       } 
/* 149 */       this.ports = newPorts;
/* 150 */       this.unmodifiablePorts = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setPorts(Iterable<Integer> ports) {
/* 157 */     Set<Integer> newPorts = new TreeSet<Integer>();
/* 158 */     for (Iterator<Integer> iterator = ports.iterator(); iterator.hasNext(); ) { int p = ((Integer)iterator.next()).intValue();
/* 159 */       if (p <= 0 || p > 65535) {
/* 160 */         throw new IllegalArgumentException("port out of range: " + p);
/*     */       }
/* 162 */       newPorts.add(Integer.valueOf(p)); }
/*     */     
/* 164 */     if (newPorts.isEmpty()) {
/* 165 */       this.unmodifiablePorts = this.ports = Collections.emptySet();
/*     */     } else {
/* 167 */       this.ports = newPorts;
/* 168 */       this.unmodifiablePorts = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public long getMaxAge() {
/* 175 */     return maxAge();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int getVersion() {
/* 181 */     return version();
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public int version() {
/* 187 */     return this.version;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void setVersion(int version) {
/* 193 */     this.version = version;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\DefaultCookie.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */