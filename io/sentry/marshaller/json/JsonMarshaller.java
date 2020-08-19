/*     */ package io.sentry.marshaller.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import io.sentry.event.Breadcrumb;
/*     */ import io.sentry.event.Event;
/*     */ import io.sentry.event.Sdk;
/*     */ import io.sentry.event.interfaces.SentryInterface;
/*     */ import io.sentry.marshaller.Marshaller;
/*     */ import io.sentry.util.Util;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import java.util.UUID;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
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
/*     */ public class JsonMarshaller
/*     */   implements Marshaller
/*     */ {
/*     */   public static final String EVENT_ID = "event_id";
/*     */   public static final String MESSAGE = "message";
/*     */   public static final String TIMESTAMP = "timestamp";
/*     */   public static final String LEVEL = "level";
/*     */   public static final String LOGGER = "logger";
/*     */   public static final String PLATFORM = "platform";
/*     */   public static final String CULPRIT = "culprit";
/*     */   public static final String TRANSACTION = "transaction";
/*     */   public static final String SDK = "sdk";
/*     */   public static final String TAGS = "tags";
/*     */   public static final String BREADCRUMBS = "breadcrumbs";
/*     */   public static final String CONTEXTS = "contexts";
/*     */   public static final String SERVER_NAME = "server_name";
/*     */   public static final String RELEASE = "release";
/*     */   public static final String DIST = "dist";
/*     */   public static final String ENVIRONMENT = "environment";
/*     */   public static final String FINGERPRINT = "fingerprint";
/*     */   public static final String MODULES = "modules";
/*     */   public static final String EXTRA = "extra";
/*     */   public static final String CHECKSUM = "checksum";
/*     */   public static final int DEFAULT_MAX_MESSAGE_LENGTH = 1000;
/*     */   
/* 114 */   private static final ThreadLocal<DateFormat> ISO_FORMAT = new ThreadLocal<DateFormat>()
/*     */     {
/*     */       protected DateFormat initialValue() {
/* 117 */         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
/* 118 */         dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
/* 119 */         return dateFormat;
/*     */       }
/*     */     };
/*     */   
/* 123 */   private static final Logger logger = LoggerFactory.getLogger(JsonMarshaller.class);
/* 124 */   private final JsonFactory jsonFactory = new JsonFactory();
/* 125 */   private final Map<Class<? extends SentryInterface>, InterfaceBinding<?>> interfaceBindings = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean compression = true;
/*     */ 
/*     */ 
/*     */   
/*     */   private final int maxMessageLength;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonMarshaller() {
/* 139 */     this(1000);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JsonMarshaller(int maxMessageLength) {
/* 148 */     this.maxMessageLength = maxMessageLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public void marshall(Event event, OutputStream destination) throws IOException {
/*     */     GZIPOutputStream gZIPOutputStream;
/* 154 */     Marshaller.UncloseableOutputStream uncloseableOutputStream = new Marshaller.UncloseableOutputStream(destination);
/*     */     
/* 156 */     if (this.compression) {
/* 157 */       gZIPOutputStream = new GZIPOutputStream((OutputStream)uncloseableOutputStream);
/*     */     }
/*     */     
/* 160 */     try (JsonGenerator generator = createJsonGenerator(gZIPOutputStream)) {
/* 161 */       writeContent(generator, event);
/* 162 */     } catch (IOException e) {
/* 163 */       logger.error("An exception occurred while serialising the event.", e);
/*     */     } finally {
/*     */       try {
/* 166 */         gZIPOutputStream.close();
/* 167 */       } catch (IOException e) {
/* 168 */         logger.error("An exception occurred while serialising the event.", e);
/*     */       } 
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
/*     */   
/*     */   protected JsonGenerator createJsonGenerator(OutputStream destination) throws IOException {
/* 183 */     return new SentryJsonGenerator(this.jsonFactory.createGenerator(destination));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContentType() {
/* 188 */     return "application/json";
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContentEncoding() {
/* 193 */     if (isCompressed()) {
/* 194 */       return "gzip";
/*     */     }
/* 196 */     return null;
/*     */   }
/*     */   
/*     */   private void writeContent(JsonGenerator generator, Event event) throws IOException {
/* 200 */     generator.writeStartObject();
/*     */     
/* 202 */     generator.writeStringField("event_id", formatId(event.getId()));
/* 203 */     generator.writeStringField("message", Util.trimString(event.getMessage(), this.maxMessageLength));
/* 204 */     generator.writeStringField("timestamp", ((DateFormat)ISO_FORMAT.get()).format(event.getTimestamp()));
/* 205 */     generator.writeStringField("level", formatLevel(event.getLevel()));
/* 206 */     generator.writeStringField("logger", event.getLogger());
/* 207 */     generator.writeStringField("platform", event.getPlatform());
/* 208 */     generator.writeStringField("culprit", event.getCulprit());
/* 209 */     generator.writeStringField("transaction", event.getTransaction());
/* 210 */     writeSdk(generator, event.getSdk());
/* 211 */     writeTags(generator, event.getTags());
/* 212 */     writeBreadcumbs(generator, event.getBreadcrumbs());
/* 213 */     writeContexts(generator, event.getContexts());
/* 214 */     generator.writeStringField("server_name", event.getServerName());
/* 215 */     generator.writeStringField("release", event.getRelease());
/* 216 */     generator.writeStringField("dist", event.getDist());
/* 217 */     generator.writeStringField("environment", event.getEnvironment());
/* 218 */     writeExtras(generator, event.getExtra());
/* 219 */     writeCollection(generator, "fingerprint", event.getFingerprint());
/* 220 */     generator.writeStringField("checksum", event.getChecksum());
/* 221 */     writeInterfaces(generator, event.getSentryInterfaces());
/*     */     
/* 223 */     generator.writeEndObject();
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeInterfaces(JsonGenerator generator, Map<String, SentryInterface> sentryInterfaces) throws IOException {
/* 228 */     for (Map.Entry<String, SentryInterface> interfaceEntry : sentryInterfaces.entrySet()) {
/* 229 */       SentryInterface sentryInterface = interfaceEntry.getValue();
/*     */       
/* 231 */       if (this.interfaceBindings.containsKey(sentryInterface.getClass())) {
/* 232 */         generator.writeFieldName(interfaceEntry.getKey());
/* 233 */         getInterfaceBinding(sentryInterface).writeInterface(generator, interfaceEntry.getValue()); continue;
/*     */       } 
/* 235 */       logger.error("Couldn't parse the content of '{}' provided in {}.", interfaceEntry
/* 236 */           .getKey(), sentryInterface);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private <T extends SentryInterface> InterfaceBinding<? super T> getInterfaceBinding(T sentryInterface) {
/* 244 */     return (InterfaceBinding<? super T>)this.interfaceBindings.get(sentryInterface.getClass());
/*     */   }
/*     */   
/*     */   private void writeExtras(JsonGenerator generator, Map<String, Object> extras) throws IOException {
/* 248 */     generator.writeObjectFieldStart("extra");
/* 249 */     for (Map.Entry<String, Object> extra : extras.entrySet()) {
/* 250 */       generator.writeFieldName(extra.getKey());
/* 251 */       generator.writeObject(extra.getValue());
/*     */     } 
/* 253 */     generator.writeEndObject();
/*     */   }
/*     */   
/*     */   private void writeCollection(JsonGenerator generator, String name, Collection<String> value) throws IOException {
/* 257 */     if (value != null && !value.isEmpty()) {
/* 258 */       generator.writeArrayFieldStart(name);
/* 259 */       for (String element : value) {
/* 260 */         generator.writeString(element);
/*     */       }
/* 262 */       generator.writeEndArray();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void writeSdk(JsonGenerator generator, Sdk sdk) throws IOException {
/* 267 */     generator.writeObjectFieldStart("sdk");
/* 268 */     generator.writeStringField("name", sdk.getName());
/* 269 */     generator.writeStringField("version", sdk.getVersion());
/* 270 */     if (sdk.getIntegrations() != null && !sdk.getIntegrations().isEmpty()) {
/* 271 */       generator.writeArrayFieldStart("integrations");
/* 272 */       for (String integration : sdk.getIntegrations()) {
/* 273 */         generator.writeString(integration);
/*     */       }
/* 275 */       generator.writeEndArray();
/*     */     } 
/* 277 */     generator.writeEndObject();
/*     */   }
/*     */   
/*     */   private void writeTags(JsonGenerator generator, Map<String, String> tags) throws IOException {
/* 281 */     generator.writeObjectFieldStart("tags");
/* 282 */     for (Map.Entry<String, String> tag : tags.entrySet()) {
/* 283 */       generator.writeStringField(tag.getKey(), tag.getValue());
/*     */     }
/* 285 */     generator.writeEndObject();
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeBreadcumbs(JsonGenerator generator, List<Breadcrumb> breadcrumbs) throws IOException {
/* 290 */     if (breadcrumbs.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     
/* 294 */     generator.writeObjectFieldStart("breadcrumbs");
/* 295 */     generator.writeArrayFieldStart("values");
/* 296 */     for (Breadcrumb breadcrumb : breadcrumbs) {
/* 297 */       generator.writeStartObject();
/* 298 */       TimeZone tz = TimeZone.getTimeZone("UTC");
/* 299 */       DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
/* 300 */       df.setTimeZone(tz);
/* 301 */       generator.writeStringField("timestamp", df.format(breadcrumb.getTimestamp()));
/*     */       
/* 303 */       if (breadcrumb.getType() != null) {
/* 304 */         generator.writeStringField("type", breadcrumb.getType().getValue());
/*     */       }
/* 306 */       if (breadcrumb.getLevel() != null) {
/* 307 */         generator.writeStringField("level", breadcrumb.getLevel().getValue());
/*     */       }
/* 309 */       if (breadcrumb.getMessage() != null) {
/* 310 */         generator.writeStringField("message", breadcrumb.getMessage());
/*     */       }
/* 312 */       if (breadcrumb.getCategory() != null) {
/* 313 */         generator.writeStringField("category", breadcrumb.getCategory());
/*     */       }
/* 315 */       if (breadcrumb.getData() != null && !breadcrumb.getData().isEmpty()) {
/* 316 */         generator.writeObjectFieldStart("data");
/* 317 */         for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)breadcrumb.getData().entrySet()) {
/* 318 */           generator.writeStringField(entry.getKey(), entry.getValue());
/*     */         }
/* 320 */         generator.writeEndObject();
/*     */       } 
/* 322 */       generator.writeEndObject();
/*     */     } 
/* 324 */     generator.writeEndArray();
/* 325 */     generator.writeEndObject();
/*     */   }
/*     */   
/*     */   private void writeContexts(JsonGenerator generator, Map<String, Map<String, Object>> contexts) throws IOException {
/* 329 */     if (contexts.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     
/* 333 */     generator.writeObjectFieldStart("contexts");
/* 334 */     for (Map.Entry<String, Map<String, Object>> contextEntry : contexts.entrySet()) {
/* 335 */       generator.writeObjectFieldStart(contextEntry.getKey());
/* 336 */       for (Map.Entry<String, Object> innerContextEntry : (Iterable<Map.Entry<String, Object>>)((Map)contextEntry.getValue()).entrySet()) {
/* 337 */         generator.writeObjectField(innerContextEntry.getKey(), innerContextEntry.getValue());
/*     */       }
/* 339 */       generator.writeEndObject();
/*     */     } 
/* 341 */     generator.writeEndObject();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String formatId(UUID id) {
/* 351 */     return id.toString().replaceAll("-", "");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String formatLevel(Event.Level level) {
/* 361 */     if (level == null) {
/* 362 */       return null;
/*     */     }
/*     */     
/* 365 */     switch (level) {
/*     */       case DEBUG:
/* 367 */         return "debug";
/*     */       case FATAL:
/* 369 */         return "fatal";
/*     */       case WARNING:
/* 371 */         return "warning";
/*     */       case INFO:
/* 373 */         return "info";
/*     */       case ERROR:
/* 375 */         return "error";
/*     */     } 
/* 377 */     logger.error("The level '{}' isn't supported, this should NEVER happen, contact Sentry developers", level
/* 378 */         .name());
/* 379 */     return null;
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
/*     */   public <T extends SentryInterface, F extends T> void addInterfaceBinding(Class<F> sentryInterfaceClass, InterfaceBinding<T> binding) {
/* 393 */     this.interfaceBindings.put(sentryInterfaceClass, binding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCompression(boolean compression) {
/* 402 */     this.compression = compression;
/*     */   }
/*     */   
/*     */   public boolean isCompressed() {
/* 406 */     return this.compression;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\marshaller\json\JsonMarshaller.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */