/*      */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.URLEncoder;
/*      */ import java.nio.charset.Charset;
/*      */ import java.util.AbstractMap;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Map;
/*      */ import java.util.regex.Pattern;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*      */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*      */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.DecoderResult;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultFullHttpRequest;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.DefaultHttpContent;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.EmptyHttpHeaders;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpMessage;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpRequest;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpConstants;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpContent;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderValues;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMessage;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMethod;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpUtil;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpVersion;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.LastHttpContent;
/*      */ import pro.gravit.repackage.io.netty.handler.stream.ChunkedInput;
/*      */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class HttpPostRequestEncoder
/*      */   implements ChunkedInput<HttpContent>
/*      */ {
/*      */   public enum EncoderMode
/*      */   {
/*   78 */     RFC1738,
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   83 */     RFC3986,
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*   94 */     HTML5;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  101 */   private static final Map.Entry[] percentEncodings = new Map.Entry[] { new AbstractMap.SimpleImmutableEntry<Pattern, String>(
/*  102 */         Pattern.compile("\\*"), "%2A"), new AbstractMap.SimpleImmutableEntry<Pattern, String>(
/*  103 */         Pattern.compile("\\+"), "%20"), new AbstractMap.SimpleImmutableEntry<Pattern, String>(
/*  104 */         Pattern.compile("~"), "%7E") };
/*      */ 
/*      */   
/*      */   private final HttpDataFactory factory;
/*      */ 
/*      */   
/*      */   private final HttpRequest request;
/*      */ 
/*      */   
/*      */   private final Charset charset;
/*      */ 
/*      */   
/*      */   private boolean isChunked;
/*      */ 
/*      */   
/*      */   private final List<InterfaceHttpData> bodyListDatas;
/*      */ 
/*      */   
/*      */   final List<InterfaceHttpData> multipartHttpDatas;
/*      */ 
/*      */   
/*      */   private final boolean isMultipart;
/*      */ 
/*      */   
/*      */   String multipartDataBoundary;
/*      */ 
/*      */   
/*      */   String multipartMixedBoundary;
/*      */ 
/*      */   
/*      */   private boolean headerFinalized;
/*      */ 
/*      */   
/*      */   private final EncoderMode encoderMode;
/*      */ 
/*      */   
/*      */   private boolean isLastChunk;
/*      */ 
/*      */   
/*      */   private boolean isLastChunkSent;
/*      */ 
/*      */   
/*      */   private FileUpload currentFileUpload;
/*      */ 
/*      */   
/*      */   private boolean duringMixedMode;
/*      */ 
/*      */   
/*      */   private long globalBodySize;
/*      */ 
/*      */   
/*      */   private long globalProgress;
/*      */ 
/*      */   
/*      */   private ListIterator<InterfaceHttpData> iterator;
/*      */ 
/*      */   
/*      */   private ByteBuf currentBuffer;
/*      */ 
/*      */   
/*      */   private InterfaceHttpData currentData;
/*      */   
/*      */   private boolean isKey;
/*      */ 
/*      */   
/*      */   public HttpPostRequestEncoder(HttpRequest request, boolean multipart) throws ErrorDataEncoderException {
/*  170 */     this(new DefaultHttpDataFactory(16384L), request, multipart, HttpConstants.DEFAULT_CHARSET, EncoderMode.RFC1738);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpPostRequestEncoder(HttpDataFactory factory, HttpRequest request, boolean multipart) throws ErrorDataEncoderException {
/*  189 */     this(factory, request, multipart, HttpConstants.DEFAULT_CHARSET, EncoderMode.RFC1738);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpPostRequestEncoder(HttpDataFactory factory, HttpRequest request, boolean multipart, Charset charset, EncoderMode encoderMode) throws ErrorDataEncoderException {
/*  865 */     this.isKey = true; this.request = (HttpRequest)ObjectUtil.checkNotNull(request, "request"); this.charset = (Charset)ObjectUtil.checkNotNull(charset, "charset"); this.factory = (HttpDataFactory)ObjectUtil.checkNotNull(factory, "factory"); if (HttpMethod.TRACE.equals(request.method())) throw new ErrorDataEncoderException("Cannot create a Encoder if request is a TRACE");  this.bodyListDatas = new ArrayList<InterfaceHttpData>(); this.isLastChunk = false; this.isLastChunkSent = false; this.isMultipart = multipart; this.multipartHttpDatas = new ArrayList<InterfaceHttpData>(); this.encoderMode = encoderMode; if (this.isMultipart) initDataMultipart(); 
/*      */   } public void cleanFiles() { this.factory.cleanRequestHttpData(this.request); } public boolean isMultipart() { return this.isMultipart; }
/*      */   private void initDataMultipart() { this.multipartDataBoundary = getNewMultipartDelimiter(); }
/*      */   private void initMixedMultipart() { this.multipartMixedBoundary = getNewMultipartDelimiter(); }
/*      */   private static String getNewMultipartDelimiter() { return Long.toHexString(PlatformDependent.threadLocalRandom().nextLong()); }
/*      */   public List<InterfaceHttpData> getBodyListAttributes() { return this.bodyListDatas; }
/*      */   public void setBodyHttpDatas(List<InterfaceHttpData> datas) throws ErrorDataEncoderException { ObjectUtil.checkNotNull(datas, "datas"); this.globalBodySize = 0L; this.bodyListDatas.clear(); this.currentFileUpload = null; this.duringMixedMode = false; this.multipartHttpDatas.clear(); for (InterfaceHttpData data : datas) addBodyHttpData(data);  }
/*  872 */   private ByteBuf fillByteBuf() { int length = this.currentBuffer.readableBytes();
/*  873 */     if (length > 8096) {
/*  874 */       return this.currentBuffer.readRetainedSlice(8096);
/*      */     }
/*      */     
/*  877 */     ByteBuf slice = this.currentBuffer;
/*  878 */     this.currentBuffer = null;
/*  879 */     return slice; } public void addBodyAttribute(String name, String value) throws ErrorDataEncoderException { String svalue = (value != null) ? value : ""; Attribute data = this.factory.createAttribute(this.request, (String)ObjectUtil.checkNotNull(name, "name"), svalue); addBodyHttpData(data); }
/*      */   public void addBodyFileUpload(String name, File file, String contentType, boolean isText) throws ErrorDataEncoderException { addBodyFileUpload(name, file.getName(), file, contentType, isText); }
/*      */   public void addBodyFileUpload(String name, String filename, File file, String contentType, boolean isText) throws ErrorDataEncoderException { ObjectUtil.checkNotNull(name, "name"); ObjectUtil.checkNotNull(file, "file"); if (filename == null)
/*      */       filename = "";  String scontentType = contentType; String contentTransferEncoding = null; if (contentType == null)
/*      */       if (isText) { scontentType = "text/plain"; }
/*      */       else { scontentType = "application/octet-stream"; }
/*      */         if (!isText)
/*      */       contentTransferEncoding = HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value();  FileUpload fileUpload = this.factory.createFileUpload(this.request, name, filename, scontentType, contentTransferEncoding, null, file.length()); try {
/*      */       fileUpload.setContent(file);
/*      */     } catch (IOException e) {
/*      */       throw new ErrorDataEncoderException(e);
/*      */     }  addBodyHttpData(fileUpload); }
/*      */   public void addBodyFileUploads(String name, File[] file, String[] contentType, boolean[] isText) throws ErrorDataEncoderException { if (file.length != contentType.length && file.length != isText.length)
/*      */       throw new IllegalArgumentException("Different array length");  for (int i = 0; i < file.length; i++)
/*      */       addBodyFileUpload(name, file[i], contentType[i], isText[i]);  }
/*  894 */   private HttpContent encodeNextChunkMultipart(int sizeleft) throws ErrorDataEncoderException { if (this.currentData == null) {
/*  895 */       return null;
/*      */     }
/*      */     
/*  898 */     if (this.currentData instanceof InternalAttribute) {
/*  899 */       buffer = ((InternalAttribute)this.currentData).toByteBuf();
/*  900 */       this.currentData = null;
/*      */     } else {
/*      */       try {
/*  903 */         buffer = ((HttpData)this.currentData).getChunk(sizeleft);
/*  904 */       } catch (IOException e) {
/*  905 */         throw new ErrorDataEncoderException(e);
/*      */       } 
/*  907 */       if (buffer.capacity() == 0) {
/*      */         
/*  909 */         this.currentData = null;
/*  910 */         return null;
/*      */       } 
/*      */     } 
/*  913 */     if (this.currentBuffer == null) {
/*  914 */       this.currentBuffer = buffer;
/*      */     } else {
/*  916 */       this.currentBuffer = Unpooled.wrappedBuffer(new ByteBuf[] { this.currentBuffer, buffer });
/*      */     } 
/*  918 */     if (this.currentBuffer.readableBytes() < 8096) {
/*  919 */       this.currentData = null;
/*  920 */       return null;
/*      */     } 
/*  922 */     ByteBuf buffer = fillByteBuf();
/*  923 */     return (HttpContent)new DefaultHttpContent(buffer); }
/*      */   public void addBodyHttpData(InterfaceHttpData data) throws ErrorDataEncoderException { if (this.headerFinalized)
/*      */       throw new ErrorDataEncoderException("Cannot add value once finalized");  this.bodyListDatas.add(ObjectUtil.checkNotNull(data, "data")); if (!this.isMultipart) { if (data instanceof Attribute) { Attribute attribute = (Attribute)data; try { String key = encodeAttribute(attribute.getName(), this.charset); String value = encodeAttribute(attribute.getValue(), this.charset); Attribute newattribute = this.factory.createAttribute(this.request, key, value); this.multipartHttpDatas.add(newattribute); this.globalBodySize += (newattribute.getName().length() + 1) + newattribute.length() + 1L; } catch (IOException e) { throw new ErrorDataEncoderException(e); }  } else if (data instanceof FileUpload) { FileUpload fileUpload = (FileUpload)data; String key = encodeAttribute(fileUpload.getName(), this.charset); String value = encodeAttribute(fileUpload.getFilename(), this.charset); Attribute newattribute = this.factory.createAttribute(this.request, key, value); this.multipartHttpDatas.add(newattribute); this.globalBodySize += (newattribute.getName().length() + 1) + newattribute.length() + 1L; }  return; }  if (data instanceof Attribute) { if (this.duringMixedMode) { InternalAttribute internalAttribute = new InternalAttribute(this.charset); internalAttribute.addValue("\r\n--" + this.multipartMixedBoundary + "--"); this.multipartHttpDatas.add(internalAttribute); this.multipartMixedBoundary = null; this.currentFileUpload = null; this.duringMixedMode = false; }  InternalAttribute internal = new InternalAttribute(this.charset); if (!this.multipartHttpDatas.isEmpty())
/*      */         internal.addValue("\r\n");  internal.addValue("--" + this.multipartDataBoundary + "\r\n"); Attribute attribute = (Attribute)data; internal.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + attribute.getName() + "\"\r\n"); internal.addValue(HttpHeaderNames.CONTENT_LENGTH + ": " + attribute.length() + "\r\n"); Charset localcharset = attribute.getCharset(); if (localcharset != null)
/*      */         internal.addValue(HttpHeaderNames.CONTENT_TYPE + ": " + "text/plain" + "; " + HttpHeaderValues.CHARSET + '=' + localcharset.name() + "\r\n");  internal.addValue("\r\n"); this.multipartHttpDatas.add(internal); this.multipartHttpDatas.add(data); this.globalBodySize += attribute.length() + internal.size(); } else if (data instanceof FileUpload) { boolean localMixed; FileUpload fileUpload = (FileUpload)data; InternalAttribute internal = new InternalAttribute(this.charset); if (!this.multipartHttpDatas.isEmpty())
/*      */         internal.addValue("\r\n");  if (this.duringMixedMode) { if (this.currentFileUpload != null && this.currentFileUpload.getName().equals(fileUpload.getName())) { localMixed = true; } else { internal.addValue("--" + this.multipartMixedBoundary + "--"); this.multipartHttpDatas.add(internal); this.multipartMixedBoundary = null; internal = new InternalAttribute(this.charset); internal.addValue("\r\n"); localMixed = false; this.currentFileUpload = fileUpload; this.duringMixedMode = false; }  } else if (this.encoderMode != EncoderMode.HTML5 && this.currentFileUpload != null && this.currentFileUpload.getName().equals(fileUpload.getName())) { initMixedMultipart(); InternalAttribute pastAttribute = (InternalAttribute)this.multipartHttpDatas.get(this.multipartHttpDatas.size() - 2); this.globalBodySize -= pastAttribute.size(); StringBuilder replacement = (new StringBuilder(139 + this.multipartDataBoundary.length() + this.multipartMixedBoundary.length() * 2 + fileUpload.getFilename().length() + fileUpload.getName().length())).append("--").append(this.multipartDataBoundary).append("\r\n").append((CharSequence)HttpHeaderNames.CONTENT_DISPOSITION).append(": ").append((CharSequence)HttpHeaderValues.FORM_DATA).append("; ").append((CharSequence)HttpHeaderValues.NAME).append("=\"").append(fileUpload.getName()).append("\"\r\n").append((CharSequence)HttpHeaderNames.CONTENT_TYPE).append(": ").append((CharSequence)HttpHeaderValues.MULTIPART_MIXED).append("; ").append((CharSequence)HttpHeaderValues.BOUNDARY).append('=').append(this.multipartMixedBoundary).append("\r\n\r\n").append("--").append(this.multipartMixedBoundary).append("\r\n").append((CharSequence)HttpHeaderNames.CONTENT_DISPOSITION).append(": ").append((CharSequence)HttpHeaderValues.ATTACHMENT); if (!fileUpload.getFilename().isEmpty())
/*      */           replacement.append("; ").append((CharSequence)HttpHeaderValues.FILENAME).append("=\"").append(this.currentFileUpload.getFilename()).append('"');  replacement.append("\r\n"); pastAttribute.setValue(replacement.toString(), 1); pastAttribute.setValue("", 2); this.globalBodySize += pastAttribute.size(); localMixed = true; this.duringMixedMode = true; } else { localMixed = false; this.currentFileUpload = fileUpload; this.duringMixedMode = false; }  if (localMixed) { internal.addValue("--" + this.multipartMixedBoundary + "\r\n"); if (fileUpload.getFilename().isEmpty()) { internal.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.ATTACHMENT + "\r\n"); } else { internal.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.ATTACHMENT + "; " + HttpHeaderValues.FILENAME + "=\"" + fileUpload.getFilename() + "\"\r\n"); }  }
/*      */       else { internal.addValue("--" + this.multipartDataBoundary + "\r\n"); if (fileUpload.getFilename().isEmpty()) { internal.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + fileUpload.getName() + "\"\r\n"); }
/*      */         else { internal.addValue(HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + fileUpload.getName() + "\"; " + HttpHeaderValues.FILENAME + "=\"" + fileUpload.getFilename() + "\"\r\n"); }
/*      */          }
/*      */        internal.addValue(HttpHeaderNames.CONTENT_LENGTH + ": " + fileUpload.length() + "\r\n"); internal.addValue(HttpHeaderNames.CONTENT_TYPE + ": " + fileUpload.getContentType()); String contentTransferEncoding = fileUpload.getContentTransferEncoding(); if (contentTransferEncoding != null && contentTransferEncoding.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) { internal.addValue("\r\n" + HttpHeaderNames.CONTENT_TRANSFER_ENCODING + ": " + HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value() + "\r\n\r\n"); }
/*      */       else if (fileUpload.getCharset() != null) { internal.addValue("; " + HttpHeaderValues.CHARSET + '=' + fileUpload.getCharset().name() + "\r\n\r\n"); }
/*      */       else { internal.addValue("\r\n\r\n"); }
/*      */        this.multipartHttpDatas.add(internal); this.multipartHttpDatas.add(data); this.globalBodySize += fileUpload.length() + internal.size(); }
/*  937 */      } private HttpContent encodeNextChunkUrlEncoded(int sizeleft) throws ErrorDataEncoderException { if (this.currentData == null) {
/*  938 */       return null;
/*      */     }
/*  940 */     int size = sizeleft;
/*      */ 
/*      */ 
/*      */     
/*  944 */     if (this.isKey) {
/*  945 */       String key = this.currentData.getName();
/*  946 */       buffer = Unpooled.wrappedBuffer(key.getBytes());
/*  947 */       this.isKey = false;
/*  948 */       if (this.currentBuffer == null) {
/*  949 */         this.currentBuffer = Unpooled.wrappedBuffer(new ByteBuf[] { buffer, Unpooled.wrappedBuffer("=".getBytes()) });
/*      */       } else {
/*  951 */         this.currentBuffer = Unpooled.wrappedBuffer(new ByteBuf[] { this.currentBuffer, buffer, Unpooled.wrappedBuffer("=".getBytes()) });
/*      */       } 
/*      */       
/*  954 */       size -= buffer.readableBytes() + 1;
/*  955 */       if (this.currentBuffer.readableBytes() >= 8096) {
/*  956 */         buffer = fillByteBuf();
/*  957 */         return (HttpContent)new DefaultHttpContent(buffer);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*      */     try {
/*  963 */       buffer = ((HttpData)this.currentData).getChunk(size);
/*  964 */     } catch (IOException e) {
/*  965 */       throw new ErrorDataEncoderException(e);
/*      */     } 
/*      */ 
/*      */     
/*  969 */     ByteBuf delimiter = null;
/*  970 */     if (buffer.readableBytes() < size) {
/*  971 */       this.isKey = true;
/*  972 */       delimiter = this.iterator.hasNext() ? Unpooled.wrappedBuffer("&".getBytes()) : null;
/*      */     } 
/*      */ 
/*      */     
/*  976 */     if (buffer.capacity() == 0) {
/*  977 */       this.currentData = null;
/*  978 */       if (this.currentBuffer == null) {
/*  979 */         if (delimiter == null) {
/*  980 */           return null;
/*      */         }
/*  982 */         this.currentBuffer = delimiter;
/*      */       
/*      */       }
/*  985 */       else if (delimiter != null) {
/*  986 */         this.currentBuffer = Unpooled.wrappedBuffer(new ByteBuf[] { this.currentBuffer, delimiter });
/*      */       } 
/*      */       
/*  989 */       if (this.currentBuffer.readableBytes() >= 8096) {
/*  990 */         buffer = fillByteBuf();
/*  991 */         return (HttpContent)new DefaultHttpContent(buffer);
/*      */       } 
/*  993 */       return null;
/*      */     } 
/*      */ 
/*      */     
/*  997 */     if (this.currentBuffer == null) {
/*  998 */       if (delimiter != null) {
/*  999 */         this.currentBuffer = Unpooled.wrappedBuffer(new ByteBuf[] { buffer, delimiter });
/*      */       } else {
/* 1001 */         this.currentBuffer = buffer;
/*      */       }
/*      */     
/* 1004 */     } else if (delimiter != null) {
/* 1005 */       this.currentBuffer = Unpooled.wrappedBuffer(new ByteBuf[] { this.currentBuffer, buffer, delimiter });
/*      */     } else {
/* 1007 */       this.currentBuffer = Unpooled.wrappedBuffer(new ByteBuf[] { this.currentBuffer, buffer });
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1012 */     if (this.currentBuffer.readableBytes() < 8096) {
/* 1013 */       this.currentData = null;
/* 1014 */       this.isKey = true;
/* 1015 */       return null;
/*      */     } 
/*      */     
/* 1018 */     ByteBuf buffer = fillByteBuf();
/* 1019 */     return (HttpContent)new DefaultHttpContent(buffer); }
/*      */   public HttpRequest finalizeRequest() throws ErrorDataEncoderException { if (!this.headerFinalized) { if (this.isMultipart) { InternalAttribute internal = new InternalAttribute(this.charset); if (this.duringMixedMode)
/*      */           internal.addValue("\r\n--" + this.multipartMixedBoundary + "--");  internal.addValue("\r\n--" + this.multipartDataBoundary + "--\r\n"); this.multipartHttpDatas.add(internal); this.multipartMixedBoundary = null; this.currentFileUpload = null; this.duringMixedMode = false; this.globalBodySize += internal.size(); }  this.headerFinalized = true; } else { throw new ErrorDataEncoderException("Header already encoded"); }  HttpHeaders headers = this.request.headers(); List<String> contentTypes = headers.getAll((CharSequence)HttpHeaderNames.CONTENT_TYPE); List<String> transferEncoding = headers.getAll((CharSequence)HttpHeaderNames.TRANSFER_ENCODING); if (contentTypes != null) { headers.remove((CharSequence)HttpHeaderNames.CONTENT_TYPE); for (String contentType : contentTypes) { String lowercased = contentType.toLowerCase(); if (lowercased.startsWith(HttpHeaderValues.MULTIPART_FORM_DATA.toString()) || lowercased.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString()))
/*      */           continue;  headers.add((CharSequence)HttpHeaderNames.CONTENT_TYPE, contentType); }  }  if (this.isMultipart) { String value = HttpHeaderValues.MULTIPART_FORM_DATA + "; " + HttpHeaderValues.BOUNDARY + '=' + this.multipartDataBoundary; headers.add((CharSequence)HttpHeaderNames.CONTENT_TYPE, value); } else { headers.add((CharSequence)HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED); }  long realSize = this.globalBodySize; if (!this.isMultipart)
/*      */       realSize--;  this.iterator = this.multipartHttpDatas.listIterator(); headers.set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, String.valueOf(realSize)); if (realSize > 8096L || this.isMultipart) { this.isChunked = true; if (transferEncoding != null) { headers.remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING); for (CharSequence v : transferEncoding) { if (HttpHeaderValues.CHUNKED.contentEqualsIgnoreCase(v))
/*      */             continue;  headers.add((CharSequence)HttpHeaderNames.TRANSFER_ENCODING, v); }  }  HttpUtil.setTransferEncodingChunked((HttpMessage)this.request, true); return new WrappedHttpRequest(this.request); }  HttpContent chunk = nextChunk(); if (this.request instanceof FullHttpRequest) { FullHttpRequest fullRequest = (FullHttpRequest)this.request; ByteBuf chunkContent = chunk.content(); if (fullRequest.content() != chunkContent) { fullRequest.content().clear().writeBytes(chunkContent); chunkContent.release(); }  return (HttpRequest)fullRequest; }  return new WrappedFullHttpRequest(this.request, chunk); }
/*      */   public boolean isChunked() { return this.isChunked; }
/*      */   private String encodeAttribute(String s, Charset charset) throws ErrorDataEncoderException { if (s == null)
/*      */       return "";  try { String encoded = URLEncoder.encode(s, charset.name()); if (this.encoderMode == EncoderMode.RFC3986)
/*      */         for (Map.Entry<Pattern, String> entry : percentEncodings) { String replacement = entry.getValue(); encoded = ((Pattern)entry.getKey()).matcher(encoded).replaceAll(replacement); }
/*      */           return encoded; }
/*      */     catch (UnsupportedEncodingException e) { throw new ErrorDataEncoderException(charset.name(), e); }
/* 1031 */      } public void close() throws Exception {} @Deprecated public HttpContent readChunk(ChannelHandlerContext ctx) throws Exception { return readChunk(ctx.alloc()); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpContent readChunk(ByteBufAllocator allocator) throws Exception {
/* 1044 */     if (this.isLastChunkSent) {
/* 1045 */       return null;
/*      */     }
/* 1047 */     HttpContent nextChunk = nextChunk();
/* 1048 */     this.globalProgress += nextChunk.content().readableBytes();
/* 1049 */     return nextChunk;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private HttpContent nextChunk() throws ErrorDataEncoderException {
/* 1062 */     if (this.isLastChunk) {
/* 1063 */       this.isLastChunkSent = true;
/* 1064 */       return (HttpContent)LastHttpContent.EMPTY_LAST_CONTENT;
/*      */     } 
/*      */     
/* 1067 */     int size = calculateRemainingSize();
/* 1068 */     if (size <= 0) {
/*      */       
/* 1070 */       ByteBuf buffer = fillByteBuf();
/* 1071 */       return (HttpContent)new DefaultHttpContent(buffer);
/*      */     } 
/*      */     
/* 1074 */     if (this.currentData != null) {
/*      */       HttpContent chunk;
/*      */       
/* 1077 */       if (this.isMultipart) {
/* 1078 */         chunk = encodeNextChunkMultipart(size);
/*      */       } else {
/* 1080 */         chunk = encodeNextChunkUrlEncoded(size);
/*      */       } 
/* 1082 */       if (chunk != null)
/*      */       {
/* 1084 */         return chunk;
/*      */       }
/* 1086 */       size = calculateRemainingSize();
/*      */     } 
/* 1088 */     if (!this.iterator.hasNext()) {
/* 1089 */       return lastChunk();
/*      */     }
/* 1091 */     while (size > 0 && this.iterator.hasNext()) {
/* 1092 */       HttpContent chunk; this.currentData = this.iterator.next();
/*      */       
/* 1094 */       if (this.isMultipart) {
/* 1095 */         chunk = encodeNextChunkMultipart(size);
/*      */       } else {
/* 1097 */         chunk = encodeNextChunkUrlEncoded(size);
/*      */       } 
/* 1099 */       if (chunk == null) {
/*      */         
/* 1101 */         size = calculateRemainingSize();
/*      */         
/*      */         continue;
/*      */       } 
/* 1105 */       return chunk;
/*      */     } 
/*      */     
/* 1108 */     return lastChunk();
/*      */   }
/*      */   
/*      */   private int calculateRemainingSize() {
/* 1112 */     int size = 8096;
/* 1113 */     if (this.currentBuffer != null) {
/* 1114 */       size -= this.currentBuffer.readableBytes();
/*      */     }
/* 1116 */     return size;
/*      */   }
/*      */   
/*      */   private HttpContent lastChunk() {
/* 1120 */     this.isLastChunk = true;
/* 1121 */     if (this.currentBuffer == null) {
/* 1122 */       this.isLastChunkSent = true;
/*      */       
/* 1124 */       return (HttpContent)LastHttpContent.EMPTY_LAST_CONTENT;
/*      */     } 
/*      */     
/* 1127 */     ByteBuf buffer = this.currentBuffer;
/* 1128 */     this.currentBuffer = null;
/* 1129 */     return (HttpContent)new DefaultHttpContent(buffer);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isEndOfInput() throws Exception {
/* 1134 */     return this.isLastChunkSent;
/*      */   }
/*      */ 
/*      */   
/*      */   public long length() {
/* 1139 */     return this.isMultipart ? this.globalBodySize : (this.globalBodySize - 1L);
/*      */   }
/*      */ 
/*      */   
/*      */   public long progress() {
/* 1144 */     return this.globalProgress;
/*      */   }
/*      */ 
/*      */   
/*      */   public static class ErrorDataEncoderException
/*      */     extends Exception
/*      */   {
/*      */     private static final long serialVersionUID = 5020247425493164465L;
/*      */ 
/*      */     
/*      */     public ErrorDataEncoderException() {}
/*      */     
/*      */     public ErrorDataEncoderException(String msg) {
/* 1157 */       super(msg);
/*      */     }
/*      */     
/*      */     public ErrorDataEncoderException(Throwable cause) {
/* 1161 */       super(cause);
/*      */     }
/*      */     
/*      */     public ErrorDataEncoderException(String msg, Throwable cause) {
/* 1165 */       super(msg, cause);
/*      */     } }
/*      */   
/*      */   private static class WrappedHttpRequest implements HttpRequest {
/*      */     private final HttpRequest request;
/*      */     
/*      */     WrappedHttpRequest(HttpRequest request) {
/* 1172 */       this.request = request;
/*      */     }
/*      */ 
/*      */     
/*      */     public HttpRequest setProtocolVersion(HttpVersion version) {
/* 1177 */       this.request.setProtocolVersion(version);
/* 1178 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public HttpRequest setMethod(HttpMethod method) {
/* 1183 */       this.request.setMethod(method);
/* 1184 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public HttpRequest setUri(String uri) {
/* 1189 */       this.request.setUri(uri);
/* 1190 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public HttpMethod getMethod() {
/* 1195 */       return this.request.method();
/*      */     }
/*      */ 
/*      */     
/*      */     public HttpMethod method() {
/* 1200 */       return this.request.method();
/*      */     }
/*      */ 
/*      */     
/*      */     public String getUri() {
/* 1205 */       return this.request.uri();
/*      */     }
/*      */ 
/*      */     
/*      */     public String uri() {
/* 1210 */       return this.request.uri();
/*      */     }
/*      */ 
/*      */     
/*      */     public HttpVersion getProtocolVersion() {
/* 1215 */       return this.request.protocolVersion();
/*      */     }
/*      */ 
/*      */     
/*      */     public HttpVersion protocolVersion() {
/* 1220 */       return this.request.protocolVersion();
/*      */     }
/*      */ 
/*      */     
/*      */     public HttpHeaders headers() {
/* 1225 */       return this.request.headers();
/*      */     }
/*      */ 
/*      */     
/*      */     public DecoderResult decoderResult() {
/* 1230 */       return this.request.decoderResult();
/*      */     }
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public DecoderResult getDecoderResult() {
/* 1236 */       return this.request.getDecoderResult();
/*      */     }
/*      */ 
/*      */     
/*      */     public void setDecoderResult(DecoderResult result) {
/* 1241 */       this.request.setDecoderResult(result);
/*      */     }
/*      */   }
/*      */   
/*      */   private static final class WrappedFullHttpRequest extends WrappedHttpRequest implements FullHttpRequest {
/*      */     private final HttpContent content;
/*      */     
/*      */     private WrappedFullHttpRequest(HttpRequest request, HttpContent content) {
/* 1249 */       super(request);
/* 1250 */       this.content = content;
/*      */     }
/*      */ 
/*      */     
/*      */     public FullHttpRequest setProtocolVersion(HttpVersion version) {
/* 1255 */       super.setProtocolVersion(version);
/* 1256 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public FullHttpRequest setMethod(HttpMethod method) {
/* 1261 */       super.setMethod(method);
/* 1262 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public FullHttpRequest setUri(String uri) {
/* 1267 */       super.setUri(uri);
/* 1268 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public FullHttpRequest copy() {
/* 1273 */       return replace(content().copy());
/*      */     }
/*      */ 
/*      */     
/*      */     public FullHttpRequest duplicate() {
/* 1278 */       return replace(content().duplicate());
/*      */     }
/*      */ 
/*      */     
/*      */     public FullHttpRequest retainedDuplicate() {
/* 1283 */       return replace(content().retainedDuplicate());
/*      */     }
/*      */ 
/*      */     
/*      */     public FullHttpRequest replace(ByteBuf content) {
/* 1288 */       DefaultFullHttpRequest duplicate = new DefaultFullHttpRequest(protocolVersion(), method(), uri(), content);
/* 1289 */       duplicate.headers().set(headers());
/* 1290 */       duplicate.trailingHeaders().set(trailingHeaders());
/* 1291 */       return (FullHttpRequest)duplicate;
/*      */     }
/*      */ 
/*      */     
/*      */     public FullHttpRequest retain(int increment) {
/* 1296 */       this.content.retain(increment);
/* 1297 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public FullHttpRequest retain() {
/* 1302 */       this.content.retain();
/* 1303 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public FullHttpRequest touch() {
/* 1308 */       this.content.touch();
/* 1309 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public FullHttpRequest touch(Object hint) {
/* 1314 */       this.content.touch(hint);
/* 1315 */       return this;
/*      */     }
/*      */ 
/*      */     
/*      */     public ByteBuf content() {
/* 1320 */       return this.content.content();
/*      */     }
/*      */ 
/*      */     
/*      */     public HttpHeaders trailingHeaders() {
/* 1325 */       if (this.content instanceof LastHttpContent) {
/* 1326 */         return ((LastHttpContent)this.content).trailingHeaders();
/*      */       }
/* 1328 */       return (HttpHeaders)EmptyHttpHeaders.INSTANCE;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int refCnt() {
/* 1334 */       return this.content.refCnt();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean release() {
/* 1339 */       return this.content.release();
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean release(int decrement) {
/* 1344 */       return this.content.release(decrement);
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\HttpPostRequestEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */