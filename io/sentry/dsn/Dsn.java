/*     */ package io.sentry.dsn;
/*     */ 
/*     */ import io.sentry.config.Lookup;
/*     */ import io.sentry.util.Util;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class Dsn {
/*     */   public static final String DEFAULT_DSN = "noop://localhost?async=false";
/*  22 */   private static final Logger logger = LoggerFactory.getLogger(Dsn.class);
/*     */   
/*     */   private String secretKey;
/*     */   
/*     */   private String publicKey;
/*     */   
/*     */   private String projectId;
/*     */   
/*     */   private String protocol;
/*     */   
/*     */   private String host;
/*     */   
/*     */   private int port;
/*     */   private String path;
/*     */   private Set<String> protocolSettings;
/*     */   private Map<String, String> options;
/*     */   private URI uri;
/*     */   
/*     */   public Dsn(String dsn) throws InvalidDsnException {
/*  41 */     this(URI.create(dsn));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Dsn(URI dsn) throws InvalidDsnException {
/*  51 */     if (dsn == null) {
/*  52 */       throw new InvalidDsnException("DSN constructed with null value!");
/*     */     }
/*     */     
/*  55 */     this.options = new HashMap<>();
/*  56 */     this.protocolSettings = new HashSet<>();
/*     */     
/*  58 */     extractProtocolInfo(dsn);
/*  59 */     extractUserKeys(dsn);
/*  60 */     extractHostInfo(dsn);
/*  61 */     extractPathInfo(dsn);
/*  62 */     extractOptions(dsn);
/*     */     
/*  64 */     makeOptionsImmutable();
/*     */     
/*  66 */     validate();
/*     */     
/*     */     try {
/*  69 */       this.uri = new URI(this.protocol, null, this.host, this.port, this.path, null, null);
/*  70 */     } catch (URISyntaxException e) {
/*  71 */       throw new InvalidDsnException("Impossible to determine Sentry's URI from the DSN '" + dsn + "'", e);
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
/*     */   public static String dsnLookup() {
/*  84 */     return dsnFrom(Lookup.getDefault());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String dsnFrom(Lookup lookup) {
/*  94 */     String dsn = lookup.get("dsn");
/*     */     
/*  96 */     if (Util.isNullOrEmpty(dsn))
/*     */     {
/*  98 */       dsn = lookup.get("dns");
/*     */     }
/*     */     
/* 101 */     if (Util.isNullOrEmpty(dsn)) {
/* 102 */       logger.warn("*** Couldn't find a suitable DSN, Sentry operations will do nothing! See documentation: https://docs.sentry.io/clients/java/ ***");
/*     */       
/* 104 */       dsn = "noop://localhost?async=false";
/*     */     } 
/*     */     
/* 107 */     return dsn;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void extractPathInfo(URI dsnUri) {
/* 116 */     String uriPath = dsnUri.getPath();
/* 117 */     if (uriPath == null) {
/*     */       return;
/*     */     }
/* 120 */     int projectIdStart = uriPath.lastIndexOf("/") + 1;
/* 121 */     this.path = uriPath.substring(0, projectIdStart);
/* 122 */     this.projectId = uriPath.substring(projectIdStart);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void extractHostInfo(URI dsnUri) {
/* 131 */     this.host = dsnUri.getHost();
/* 132 */     this.port = dsnUri.getPort();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void extractProtocolInfo(URI dsnUri) {
/* 141 */     String scheme = dsnUri.getScheme();
/* 142 */     if (scheme == null) {
/*     */       return;
/*     */     }
/* 145 */     String[] schemeDetails = scheme.split("\\+");
/* 146 */     this.protocolSettings.addAll(Arrays.<String>asList(schemeDetails).subList(0, schemeDetails.length - 1));
/* 147 */     this.protocol = schemeDetails[schemeDetails.length - 1];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void extractUserKeys(URI dsnUri) {
/* 156 */     String userInfo = dsnUri.getUserInfo();
/* 157 */     if (userInfo == null) {
/*     */       return;
/*     */     }
/* 160 */     String[] userDetails = userInfo.split(":");
/* 161 */     this.publicKey = userDetails[0];
/* 162 */     if (userDetails.length > 1) {
/* 163 */       this.secretKey = userDetails[1];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void extractOptions(URI dsnUri) {
/* 173 */     String query = dsnUri.getQuery();
/* 174 */     if (query == null || query.isEmpty()) {
/*     */       return;
/*     */     }
/* 177 */     for (String optionPair : query.split("&")) {
/*     */       try {
/* 179 */         String[] pairDetails = optionPair.split("=");
/* 180 */         String key = URLDecoder.decode(pairDetails[0], "UTF-8");
/* 181 */         String value = (pairDetails.length > 1) ? URLDecoder.decode(pairDetails[1], "UTF-8") : null;
/* 182 */         this.options.put(key, value);
/* 183 */       } catch (UnsupportedEncodingException e) {
/* 184 */         throw new IllegalArgumentException("Impossible to decode the query parameter '" + optionPair + "'", e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void makeOptionsImmutable() {
/* 194 */     this.options = Collections.unmodifiableMap(this.options);
/* 195 */     this.protocolSettings = Collections.unmodifiableSet(this.protocolSettings);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void validate() {
/* 204 */     List<String> missingElements = new LinkedList<>();
/* 205 */     if (this.host == null) {
/* 206 */       missingElements.add("host");
/*     */     }
/*     */     
/* 209 */     if (this.protocol != null && 
/* 210 */       !this.protocol.equalsIgnoreCase("noop") && 
/* 211 */       !this.protocol.equalsIgnoreCase("out")) {
/*     */       
/* 213 */       if (this.publicKey == null) {
/* 214 */         missingElements.add("public key");
/*     */       }
/* 216 */       if (this.projectId == null || this.projectId.isEmpty()) {
/* 217 */         missingElements.add("project ID");
/*     */       }
/*     */     } 
/*     */     
/* 221 */     if (!missingElements.isEmpty()) {
/* 222 */       throw new InvalidDsnException("Invalid DSN, the following properties aren't set '" + missingElements + "'");
/*     */     }
/*     */   }
/*     */   
/*     */   public String getSecretKey() {
/* 227 */     return this.secretKey;
/*     */   }
/*     */   
/*     */   public String getPublicKey() {
/* 231 */     return this.publicKey;
/*     */   }
/*     */   
/*     */   public String getProjectId() {
/* 235 */     return this.projectId;
/*     */   }
/*     */   
/*     */   public String getProtocol() {
/* 239 */     return this.protocol;
/*     */   }
/*     */   
/*     */   public String getHost() {
/* 243 */     return this.host;
/*     */   }
/*     */   
/*     */   public int getPort() {
/* 247 */     return this.port;
/*     */   }
/*     */   
/*     */   public String getPath() {
/* 251 */     return this.path;
/*     */   }
/*     */   
/*     */   public Set<String> getProtocolSettings() {
/* 255 */     return this.protocolSettings;
/*     */   }
/*     */   
/*     */   public Map<String, String> getOptions() {
/* 259 */     return this.options;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public URI getUri() {
/* 268 */     return this.uri;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 273 */     if (this == o) {
/* 274 */       return true;
/*     */     }
/* 276 */     if (o == null || getClass() != o.getClass()) {
/* 277 */       return false;
/*     */     }
/*     */     
/* 280 */     Dsn dsn = (Dsn)o;
/*     */     
/* 282 */     if (this.port != dsn.port) {
/* 283 */       return false;
/*     */     }
/* 285 */     if (!this.host.equals(dsn.host)) {
/* 286 */       return false;
/*     */     }
/* 288 */     if (!this.options.equals(dsn.options)) {
/* 289 */       return false;
/*     */     }
/* 291 */     if (!this.path.equals(dsn.path)) {
/* 292 */       return false;
/*     */     }
/* 294 */     if (!this.projectId.equals(dsn.projectId)) {
/* 295 */       return false;
/*     */     }
/* 297 */     if ((this.protocol != null) ? !this.protocol.equals(dsn.protocol) : (dsn.protocol != null)) {
/* 298 */       return false;
/*     */     }
/* 300 */     if (!this.protocolSettings.equals(dsn.protocolSettings)) {
/* 301 */       return false;
/*     */     }
/* 303 */     if (!this.publicKey.equals(dsn.publicKey)) {
/* 304 */       return false;
/*     */     }
/* 306 */     if (!Util.equals(this.secretKey, dsn.secretKey)) {
/* 307 */       return false;
/*     */     }
/*     */     
/* 310 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 315 */     int result = this.publicKey.hashCode();
/* 316 */     result = 31 * result + this.projectId.hashCode();
/* 317 */     result = 31 * result + this.host.hashCode();
/* 318 */     result = 31 * result + this.port;
/* 319 */     result = 31 * result + this.path.hashCode();
/* 320 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 325 */     return "Dsn{uri=" + this.uri + '}';
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\dsn\Dsn.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */