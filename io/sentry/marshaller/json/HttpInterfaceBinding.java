/*     */ package io.sentry.marshaller.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import io.sentry.event.interfaces.HttpInterface;
/*     */ import io.sentry.event.interfaces.SentryInterface;
/*     */ import io.sentry.util.Util;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
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
/*     */ public class HttpInterfaceBinding
/*     */   implements InterfaceBinding<HttpInterface>
/*     */ {
/*     */   public static final int MAX_BODY_LENGTH = 2048;
/*     */   private static final String URL = "url";
/*     */   private static final String METHOD = "method";
/*     */   private static final String DATA = "data";
/*     */   private static final String BODY = "body";
/*     */   private static final String QUERY_STRING = "query_string";
/*     */   private static final String COOKIES = "cookies";
/*     */   private static final String HEADERS = "headers";
/*     */   private static final String ENVIRONMENT = "env";
/*     */   private static final String ENV_REMOTE_ADDR = "REMOTE_ADDR";
/*     */   private static final String ENV_SERVER_NAME = "SERVER_NAME";
/*     */   private static final String ENV_SERVER_PORT = "SERVER_PORT";
/*     */   private static final String ENV_LOCAL_ADDR = "LOCAL_ADDR";
/*     */   private static final String ENV_LOCAL_NAME = "LOCAL_NAME";
/*     */   private static final String ENV_LOCAL_PORT = "LOCAL_PORT";
/*     */   private static final String ENV_SERVER_PROTOCOL = "SERVER_PROTOCOL";
/*     */   private static final String ENV_REQUEST_SECURE = "REQUEST_SECURE";
/*     */   private static final String ENV_REQUEST_ASYNC = "REQUEST_ASYNC";
/*     */   private static final String ENV_AUTH_TYPE = "AUTH_TYPE";
/*     */   private static final String ENV_REMOTE_USER = "REMOTE_USER";
/*     */   
/*     */   public void writeInterface(JsonGenerator generator, HttpInterface httpInterface) throws IOException {
/*  45 */     generator.writeStartObject();
/*  46 */     generator.writeStringField("url", httpInterface.getRequestUrl());
/*  47 */     generator.writeStringField("method", httpInterface.getMethod());
/*  48 */     generator.writeFieldName("data");
/*  49 */     writeData(generator, httpInterface.getParameters(), httpInterface.getBody());
/*  50 */     generator.writeStringField("query_string", httpInterface.getQueryString());
/*  51 */     generator.writeFieldName("cookies");
/*  52 */     writeCookies(generator, httpInterface.getCookies());
/*  53 */     generator.writeFieldName("headers");
/*  54 */     writeHeaders(generator, httpInterface.getHeaders());
/*  55 */     generator.writeFieldName("env");
/*  56 */     writeEnvironment(generator, httpInterface);
/*  57 */     generator.writeEndObject();
/*     */   }
/*     */   
/*     */   private void writeEnvironment(JsonGenerator generator, HttpInterface httpInterface) throws IOException {
/*  61 */     generator.writeStartObject();
/*  62 */     generator.writeStringField("REMOTE_ADDR", httpInterface.getRemoteAddr());
/*  63 */     generator.writeStringField("SERVER_NAME", httpInterface.getServerName());
/*  64 */     generator.writeNumberField("SERVER_PORT", httpInterface.getServerPort());
/*  65 */     generator.writeStringField("LOCAL_ADDR", httpInterface.getLocalAddr());
/*  66 */     generator.writeStringField("LOCAL_NAME", httpInterface.getLocalName());
/*  67 */     generator.writeNumberField("LOCAL_PORT", httpInterface.getLocalPort());
/*  68 */     generator.writeStringField("SERVER_PROTOCOL", httpInterface.getProtocol());
/*  69 */     generator.writeBooleanField("REQUEST_SECURE", httpInterface.isSecure());
/*  70 */     generator.writeBooleanField("REQUEST_ASYNC", httpInterface.isAsyncStarted());
/*  71 */     generator.writeStringField("AUTH_TYPE", httpInterface.getAuthType());
/*     */     
/*  73 */     generator.writeStringField("REMOTE_USER", httpInterface.getRemoteUser());
/*  74 */     generator.writeEndObject();
/*     */   }
/*     */   
/*     */   private void writeHeaders(JsonGenerator generator, Map<String, Collection<String>> headers) throws IOException {
/*  78 */     generator.writeStartArray();
/*  79 */     for (Map.Entry<String, Collection<String>> headerEntry : headers.entrySet()) {
/*  80 */       for (String value : headerEntry.getValue()) {
/*  81 */         generator.writeStartArray();
/*  82 */         generator.writeString(headerEntry.getKey());
/*  83 */         generator.writeString(value);
/*  84 */         generator.writeEndArray();
/*     */       } 
/*     */     } 
/*  87 */     generator.writeEndArray();
/*     */   }
/*     */ 
/*     */   
/*     */   private void writeCookies(JsonGenerator generator, Map<String, String> cookies) throws IOException {
/*  92 */     if (cookies.isEmpty()) {
/*  93 */       generator.writeNull();
/*     */       
/*     */       return;
/*     */     } 
/*  97 */     generator.writeStartObject();
/*  98 */     for (Map.Entry<String, String> cookie : cookies.entrySet()) {
/*  99 */       generator.writeStringField(cookie.getKey(), cookie.getValue());
/*     */     }
/* 101 */     generator.writeEndObject();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeData(JsonGenerator generator, Map<String, Collection<String>> parameterMap, String body) throws IOException {
/* 107 */     if (parameterMap == null && body == null) {
/* 108 */       generator.writeNull();
/*     */       
/*     */       return;
/*     */     } 
/* 112 */     if ((parameterMap == null || parameterMap.isEmpty()) && body != null) {
/* 113 */       generator.writeString(Util.trimString(body, 2048));
/*     */     } else {
/* 115 */       generator.writeStartObject();
/* 116 */       if (body != null) {
/* 117 */         generator.writeStringField("body", Util.trimString(body, 2048));
/*     */       }
/* 119 */       if (parameterMap != null) {
/* 120 */         for (Map.Entry<String, Collection<String>> parameter : parameterMap.entrySet()) {
/* 121 */           generator.writeArrayFieldStart(parameter.getKey());
/* 122 */           for (String parameterValue : parameter.getValue()) {
/* 123 */             generator.writeString(parameterValue);
/*     */           }
/* 125 */           generator.writeEndArray();
/*     */         } 
/*     */       }
/* 128 */       generator.writeEndObject();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\marshaller\json\HttpInterfaceBinding.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */