/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.cookie;
/*     */ 
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
/*     */ 
/*     */ public class DefaultCookie
/*     */   implements Cookie
/*     */ {
/*     */   private final String name;
/*     */   private String value;
/*     */   private boolean wrap;
/*     */   private String domain;
/*     */   private String path;
/*  31 */   private long maxAge = Long.MIN_VALUE;
/*     */   
/*     */   private boolean secure;
/*     */   
/*     */   private boolean httpOnly;
/*     */ 
/*     */   
/*     */   public DefaultCookie(String name, String value) {
/*  39 */     name = ((String)ObjectUtil.checkNotNull(name, "name")).trim();
/*  40 */     if (name.isEmpty()) {
/*  41 */       throw new IllegalArgumentException("empty name");
/*     */     }
/*  43 */     this.name = name;
/*  44 */     setValue(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public String name() {
/*  49 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String value() {
/*  54 */     return this.value;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(String value) {
/*  59 */     this.value = (String)ObjectUtil.checkNotNull(value, "value");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean wrap() {
/*  64 */     return this.wrap;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWrap(boolean wrap) {
/*  69 */     this.wrap = wrap;
/*     */   }
/*     */ 
/*     */   
/*     */   public String domain() {
/*  74 */     return this.domain;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDomain(String domain) {
/*  79 */     this.domain = CookieUtil.validateAttributeValue("domain", domain);
/*     */   }
/*     */ 
/*     */   
/*     */   public String path() {
/*  84 */     return this.path;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPath(String path) {
/*  89 */     this.path = CookieUtil.validateAttributeValue("path", path);
/*     */   }
/*     */ 
/*     */   
/*     */   public long maxAge() {
/*  94 */     return this.maxAge;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxAge(long maxAge) {
/*  99 */     this.maxAge = maxAge;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSecure() {
/* 104 */     return this.secure;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSecure(boolean secure) {
/* 109 */     this.secure = secure;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isHttpOnly() {
/* 114 */     return this.httpOnly;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setHttpOnly(boolean httpOnly) {
/* 119 */     this.httpOnly = httpOnly;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 124 */     return name().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 129 */     if (this == o) {
/* 130 */       return true;
/*     */     }
/*     */     
/* 133 */     if (!(o instanceof Cookie)) {
/* 134 */       return false;
/*     */     }
/*     */     
/* 137 */     Cookie that = (Cookie)o;
/* 138 */     if (!name().equals(that.name())) {
/* 139 */       return false;
/*     */     }
/*     */     
/* 142 */     if (path() == null) {
/* 143 */       if (that.path() != null)
/* 144 */         return false; 
/*     */     } else {
/* 146 */       if (that.path() == null)
/* 147 */         return false; 
/* 148 */       if (!path().equals(that.path())) {
/* 149 */         return false;
/*     */       }
/*     */     } 
/* 152 */     if (domain() == null) {
/* 153 */       if (that.domain() != null) {
/* 154 */         return false;
/*     */       }
/*     */     } else {
/* 157 */       return domain().equalsIgnoreCase(that.domain());
/*     */     } 
/*     */     
/* 160 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(Cookie c) {
/* 165 */     int v = name().compareTo(c.name());
/* 166 */     if (v != 0) {
/* 167 */       return v;
/*     */     }
/*     */     
/* 170 */     if (path() == null) {
/* 171 */       if (c.path() != null)
/* 172 */         return -1; 
/*     */     } else {
/* 174 */       if (c.path() == null) {
/* 175 */         return 1;
/*     */       }
/* 177 */       v = path().compareTo(c.path());
/* 178 */       if (v != 0) {
/* 179 */         return v;
/*     */       }
/*     */     } 
/*     */     
/* 183 */     if (domain() == null) {
/* 184 */       if (c.domain() != null)
/* 185 */         return -1; 
/*     */     } else {
/* 187 */       if (c.domain() == null) {
/* 188 */         return 1;
/*     */       }
/* 190 */       v = domain().compareToIgnoreCase(c.domain());
/* 191 */       return v;
/*     */     } 
/*     */     
/* 194 */     return 0;
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
/*     */   @Deprecated
/*     */   protected String validateValue(String name, String value) {
/* 207 */     return CookieUtil.validateAttributeValue(name, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 215 */     StringBuilder buf = CookieUtil.stringBuilder().append(name()).append('=').append(value());
/* 216 */     if (domain() != null) {
/* 217 */       buf.append(", domain=")
/* 218 */         .append(domain());
/*     */     }
/* 220 */     if (path() != null) {
/* 221 */       buf.append(", path=")
/* 222 */         .append(path());
/*     */     }
/* 224 */     if (maxAge() >= 0L) {
/* 225 */       buf.append(", maxAge=")
/* 226 */         .append(maxAge())
/* 227 */         .append('s');
/*     */     }
/* 229 */     if (isSecure()) {
/* 230 */       buf.append(", secure");
/*     */     }
/* 232 */     if (isHttpOnly()) {
/* 233 */       buf.append(", HTTPOnly");
/*     */     }
/* 235 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\cookie\DefaultCookie.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */