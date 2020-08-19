/*    */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.CharSequenceValueConverter;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.DefaultHeaders;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.HeadersUtils;
/*    */ import pro.gravit.repackage.io.netty.handler.codec.ValueConverter;
/*    */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultSpdyHeaders
/*    */   extends DefaultHeaders<CharSequence, CharSequence, SpdyHeaders>
/*    */   implements SpdyHeaders
/*    */ {
/* 30 */   private static final DefaultHeaders.NameValidator<CharSequence> SpdyNameValidator = new DefaultHeaders.NameValidator<CharSequence>()
/*    */     {
/*    */       public void validateName(CharSequence name) {
/* 33 */         SpdyCodecUtil.validateHeaderName(name);
/*    */       }
/*    */     };
/*    */   
/*    */   public DefaultSpdyHeaders() {
/* 38 */     this(true);
/*    */   }
/*    */ 
/*    */   
/*    */   public DefaultSpdyHeaders(boolean validate) {
/* 43 */     super(AsciiString.CASE_INSENSITIVE_HASHER, validate ? (ValueConverter)HeaderValueConverterAndValidator.INSTANCE : (ValueConverter)CharSequenceValueConverter.INSTANCE, validate ? SpdyNameValidator : DefaultHeaders.NameValidator.NOT_NULL);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getAsString(CharSequence name) {
/* 50 */     return HeadersUtils.getAsString(this, name);
/*    */   }
/*    */ 
/*    */   
/*    */   public List<String> getAllAsString(CharSequence name) {
/* 55 */     return HeadersUtils.getAllAsString(this, name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<Map.Entry<String, String>> iteratorAsString() {
/* 60 */     return HeadersUtils.iteratorAsString((Iterable)this);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean contains(CharSequence name, CharSequence value) {
/* 65 */     return contains(name, value, false);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean contains(CharSequence name, CharSequence value, boolean ignoreCase) {
/* 70 */     return contains(name, value, ignoreCase ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
/*    */   }
/*    */   
/*    */   private static final class HeaderValueConverterAndValidator
/*    */     extends CharSequenceValueConverter {
/* 75 */     public static final HeaderValueConverterAndValidator INSTANCE = new HeaderValueConverterAndValidator();
/*    */ 
/*    */     
/*    */     public CharSequence convertObject(Object value) {
/* 79 */       CharSequence seq = super.convertObject(value);
/* 80 */       SpdyCodecUtil.validateHeaderValue(seq);
/* 81 */       return seq;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\DefaultSpdyHeaders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */