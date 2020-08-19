/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.text.ParsePosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
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
/*     */ @Deprecated
/*     */ public final class HttpHeaderDateFormat
/*     */   extends SimpleDateFormat
/*     */ {
/*     */   private static final long serialVersionUID = -925286159755905325L;
/*  42 */   private final SimpleDateFormat format1 = new HttpHeaderDateFormatObsolete1();
/*  43 */   private final SimpleDateFormat format2 = new HttpHeaderDateFormatObsolete2();
/*     */   
/*  45 */   private static final FastThreadLocal<HttpHeaderDateFormat> dateFormatThreadLocal = new FastThreadLocal<HttpHeaderDateFormat>()
/*     */     {
/*     */       protected HttpHeaderDateFormat initialValue()
/*     */       {
/*  49 */         return new HttpHeaderDateFormat();
/*     */       }
/*     */     };
/*     */   
/*     */   public static HttpHeaderDateFormat get() {
/*  54 */     return (HttpHeaderDateFormat)dateFormatThreadLocal.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private HttpHeaderDateFormat() {
/*  62 */     super("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
/*  63 */     setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */   }
/*     */ 
/*     */   
/*     */   public Date parse(String text, ParsePosition pos) {
/*  68 */     Date date = super.parse(text, pos);
/*  69 */     if (date == null) {
/*  70 */       date = this.format1.parse(text, pos);
/*     */     }
/*  72 */     if (date == null) {
/*  73 */       date = this.format2.parse(text, pos);
/*     */     }
/*  75 */     return date;
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class HttpHeaderDateFormatObsolete1
/*     */     extends SimpleDateFormat
/*     */   {
/*     */     private static final long serialVersionUID = -3178072504225114298L;
/*     */ 
/*     */     
/*     */     HttpHeaderDateFormatObsolete1() {
/*  86 */       super("E, dd-MMM-yy HH:mm:ss z", Locale.ENGLISH);
/*  87 */       setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class HttpHeaderDateFormatObsolete2
/*     */     extends SimpleDateFormat
/*     */   {
/*     */     private static final long serialVersionUID = 3010674519968303714L;
/*     */ 
/*     */     
/*     */     HttpHeaderDateFormatObsolete2() {
/* 100 */       super("E MMM d HH:mm:ss yyyy", Locale.ENGLISH);
/* 101 */       setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpHeaderDateFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */