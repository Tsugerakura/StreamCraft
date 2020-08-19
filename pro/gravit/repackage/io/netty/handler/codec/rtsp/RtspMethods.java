/*     */ package pro.gravit.repackage.io.netty.handler.codec.rtsp;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMethod;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RtspMethods
/*     */ {
/*  36 */   public static final HttpMethod OPTIONS = HttpMethod.OPTIONS;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  42 */   public static final HttpMethod DESCRIBE = HttpMethod.valueOf("DESCRIBE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   public static final HttpMethod ANNOUNCE = HttpMethod.valueOf("ANNOUNCE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   public static final HttpMethod SETUP = HttpMethod.valueOf("SETUP");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   public static final HttpMethod PLAY = HttpMethod.valueOf("PLAY");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  67 */   public static final HttpMethod PAUSE = HttpMethod.valueOf("PAUSE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   public static final HttpMethod TEARDOWN = HttpMethod.valueOf("TEARDOWN");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  79 */   public static final HttpMethod GET_PARAMETER = HttpMethod.valueOf("GET_PARAMETER");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  85 */   public static final HttpMethod SET_PARAMETER = HttpMethod.valueOf("SET_PARAMETER");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  91 */   public static final HttpMethod REDIRECT = HttpMethod.valueOf("REDIRECT");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  97 */   public static final HttpMethod RECORD = HttpMethod.valueOf("RECORD");
/*     */   
/*  99 */   private static final Map<String, HttpMethod> methodMap = new HashMap<String, HttpMethod>();
/*     */   
/*     */   static {
/* 102 */     methodMap.put(DESCRIBE.toString(), DESCRIBE);
/* 103 */     methodMap.put(ANNOUNCE.toString(), ANNOUNCE);
/* 104 */     methodMap.put(GET_PARAMETER.toString(), GET_PARAMETER);
/* 105 */     methodMap.put(OPTIONS.toString(), OPTIONS);
/* 106 */     methodMap.put(PAUSE.toString(), PAUSE);
/* 107 */     methodMap.put(PLAY.toString(), PLAY);
/* 108 */     methodMap.put(RECORD.toString(), RECORD);
/* 109 */     methodMap.put(REDIRECT.toString(), REDIRECT);
/* 110 */     methodMap.put(SETUP.toString(), SETUP);
/* 111 */     methodMap.put(SET_PARAMETER.toString(), SET_PARAMETER);
/* 112 */     methodMap.put(TEARDOWN.toString(), TEARDOWN);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HttpMethod valueOf(String name) {
/* 121 */     ObjectUtil.checkNotNull(name, "name");
/*     */     
/* 123 */     name = name.trim().toUpperCase();
/* 124 */     if (name.isEmpty()) {
/* 125 */       throw new IllegalArgumentException("empty name");
/*     */     }
/*     */     
/* 128 */     HttpMethod result = methodMap.get(name);
/* 129 */     if (result != null) {
/* 130 */       return result;
/*     */     }
/* 132 */     return HttpMethod.valueOf(name);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\rtsp\RtspMethods.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */