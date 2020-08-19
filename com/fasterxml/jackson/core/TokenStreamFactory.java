/*     */ package com.fasterxml.jackson.core;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.DataOutputAsStream;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.Serializable;
/*     */ import java.io.Writer;
/*     */ import java.net.URL;
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
/*     */ public abstract class TokenStreamFactory
/*     */   implements Versioned, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*     */   
/*     */   public abstract boolean requiresPropertyOrdering();
/*     */   
/*     */   public abstract boolean canHandleBinaryNatively();
/*     */   
/*     */   public abstract boolean canParseAsync();
/*     */   
/*     */   public abstract Class<? extends FormatFeature> getFormatReadFeatureType();
/*     */   
/*     */   public abstract Class<? extends FormatFeature> getFormatWriteFeatureType();
/*     */   
/*     */   public abstract boolean canUseSchema(FormatSchema paramFormatSchema);
/*     */   
/*     */   public abstract String getFormatName();
/*     */   
/*     */   public abstract boolean isEnabled(JsonParser.Feature paramFeature);
/*     */   
/*     */   public abstract boolean isEnabled(JsonGenerator.Feature paramFeature);
/*     */   
/*     */   public abstract int getParserFeatures();
/*     */   
/*     */   public abstract int getGeneratorFeatures();
/*     */   
/*     */   public abstract int getFormatParserFeatures();
/*     */   
/*     */   public abstract int getFormatGeneratorFeatures();
/*     */   
/*     */   public abstract JsonParser createParser(byte[] paramArrayOfbyte) throws IOException;
/*     */   
/*     */   public abstract JsonParser createParser(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
/*     */   
/*     */   public abstract JsonParser createParser(char[] paramArrayOfchar) throws IOException;
/*     */   
/*     */   public abstract JsonParser createParser(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws IOException;
/*     */   
/*     */   public abstract JsonParser createParser(DataInput paramDataInput) throws IOException;
/*     */   
/*     */   public abstract JsonParser createParser(File paramFile) throws IOException;
/*     */   
/*     */   public abstract JsonParser createParser(InputStream paramInputStream) throws IOException;
/*     */   
/*     */   public abstract JsonParser createParser(Reader paramReader) throws IOException;
/*     */   
/*     */   public abstract JsonParser createParser(String paramString) throws IOException;
/*     */   
/*     */   public abstract JsonParser createParser(URL paramURL) throws IOException;
/*     */   
/*     */   public abstract JsonParser createNonBlockingByteArrayParser() throws IOException;
/*     */   
/*     */   public abstract JsonGenerator createGenerator(DataOutput paramDataOutput, JsonEncoding paramJsonEncoding) throws IOException;
/*     */   
/*     */   public abstract JsonGenerator createGenerator(DataOutput paramDataOutput) throws IOException;
/*     */   
/*     */   public abstract JsonGenerator createGenerator(File paramFile, JsonEncoding paramJsonEncoding) throws IOException;
/*     */   
/*     */   public abstract JsonGenerator createGenerator(OutputStream paramOutputStream) throws IOException;
/*     */   
/*     */   public abstract JsonGenerator createGenerator(OutputStream paramOutputStream, JsonEncoding paramJsonEncoding) throws IOException;
/*     */   
/*     */   public abstract JsonGenerator createGenerator(Writer paramWriter) throws IOException;
/*     */   
/*     */   protected OutputStream _createDataOutputWrapper(DataOutput out) {
/* 159 */     return (OutputStream)new DataOutputAsStream(out);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected InputStream _optimizedStreamFromURL(URL url) throws IOException {
/* 167 */     if ("file".equals(url.getProtocol())) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 174 */       String host = url.getHost();
/* 175 */       if (host == null || host.length() == 0) {
/*     */         
/* 177 */         String path = url.getPath();
/* 178 */         if (path.indexOf('%') < 0) {
/* 179 */           return new FileInputStream(url.getPath());
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 185 */     return url.openStream();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\TokenStreamFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */