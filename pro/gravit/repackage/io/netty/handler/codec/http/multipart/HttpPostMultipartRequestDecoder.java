/*      */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.UnsupportedCharsetException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.TreeMap;
/*      */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*      */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpConstants;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpContent;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderValues;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;
/*      */ import pro.gravit.repackage.io.netty.handler.codec.http.QueryStringDecoder;
/*      */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.InternalThreadLocalMap;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class HttpPostMultipartRequestDecoder
/*      */   implements InterfaceHttpPostRequestDecoder
/*      */ {
/*      */   private final HttpDataFactory factory;
/*      */   private final HttpRequest request;
/*      */   private Charset charset;
/*      */   private boolean isLastChunk;
/*   78 */   private final List<InterfaceHttpData> bodyListHttpData = new ArrayList<InterfaceHttpData>();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   83 */   private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<String, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteBuf undecodedChunk;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int bodyListHttpDataRank;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String multipartDataBoundary;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String multipartMixedBoundary;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  110 */   private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
/*      */ 
/*      */ 
/*      */   
/*      */   private Map<CharSequence, Attribute> currentFieldAttributes;
/*      */ 
/*      */ 
/*      */   
/*      */   private FileUpload currentFileUpload;
/*      */ 
/*      */ 
/*      */   
/*      */   private Attribute currentAttribute;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean destroyed;
/*      */ 
/*      */   
/*  129 */   private int discardThreshold = 10485760;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HttpPostMultipartRequestDecoder(HttpRequest request) {
/*  142 */     this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
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
/*      */   public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request) {
/*  158 */     this(factory, request, HttpConstants.DEFAULT_CHARSET);
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
/*      */   public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
/*  176 */     this.request = (HttpRequest)ObjectUtil.checkNotNull(request, "request");
/*  177 */     this.charset = (Charset)ObjectUtil.checkNotNull(charset, "charset");
/*  178 */     this.factory = (HttpDataFactory)ObjectUtil.checkNotNull(factory, "factory");
/*      */ 
/*      */     
/*  181 */     setMultipart(this.request.headers().get((CharSequence)HttpHeaderNames.CONTENT_TYPE));
/*  182 */     if (request instanceof HttpContent) {
/*      */ 
/*      */       
/*  185 */       offer((HttpContent)request);
/*      */     } else {
/*  187 */       this.undecodedChunk = Unpooled.buffer();
/*  188 */       parseBody();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void setMultipart(String contentType) {
/*  196 */     String[] dataBoundary = HttpPostRequestDecoder.getMultipartDataBoundary(contentType);
/*  197 */     if (dataBoundary != null) {
/*  198 */       this.multipartDataBoundary = dataBoundary[0];
/*  199 */       if (dataBoundary.length > 1 && dataBoundary[1] != null) {
/*  200 */         this.charset = Charset.forName(dataBoundary[1]);
/*      */       }
/*      */     } else {
/*  203 */       this.multipartDataBoundary = null;
/*      */     } 
/*  205 */     this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
/*      */   }
/*      */   
/*      */   private void checkDestroyed() {
/*  209 */     if (this.destroyed) {
/*  210 */       throw new IllegalStateException(HttpPostMultipartRequestDecoder.class.getSimpleName() + " was destroyed already");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isMultipart() {
/*  222 */     checkDestroyed();
/*  223 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDiscardThreshold(int discardThreshold) {
/*  233 */     this.discardThreshold = ObjectUtil.checkPositiveOrZero(discardThreshold, "discardThreshold");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getDiscardThreshold() {
/*  241 */     return this.discardThreshold;
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
/*      */   public List<InterfaceHttpData> getBodyHttpDatas() {
/*  256 */     checkDestroyed();
/*      */     
/*  258 */     if (!this.isLastChunk) {
/*  259 */       throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */     }
/*  261 */     return this.bodyListHttpData;
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
/*      */   public List<InterfaceHttpData> getBodyHttpDatas(String name) {
/*  277 */     checkDestroyed();
/*      */     
/*  279 */     if (!this.isLastChunk) {
/*  280 */       throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */     }
/*  282 */     return this.bodyMapHttpData.get(name);
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
/*      */   public InterfaceHttpData getBodyHttpData(String name) {
/*  299 */     checkDestroyed();
/*      */     
/*  301 */     if (!this.isLastChunk) {
/*  302 */       throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */     }
/*  304 */     List<InterfaceHttpData> list = this.bodyMapHttpData.get(name);
/*  305 */     if (list != null) {
/*  306 */       return list.get(0);
/*      */     }
/*  308 */     return null;
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
/*      */   public HttpPostMultipartRequestDecoder offer(HttpContent content) {
/*  322 */     checkDestroyed();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  327 */     ByteBuf buf = content.content();
/*  328 */     if (this.undecodedChunk == null) {
/*  329 */       this.undecodedChunk = buf.copy();
/*      */     } else {
/*  331 */       this.undecodedChunk.writeBytes(buf);
/*      */     } 
/*  333 */     if (content instanceof pro.gravit.repackage.io.netty.handler.codec.http.LastHttpContent) {
/*  334 */       this.isLastChunk = true;
/*      */     }
/*  336 */     parseBody();
/*  337 */     if (this.undecodedChunk != null && this.undecodedChunk.writerIndex() > this.discardThreshold) {
/*  338 */       this.undecodedChunk.discardReadBytes();
/*      */     }
/*  340 */     return this;
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
/*      */   public boolean hasNext() {
/*  355 */     checkDestroyed();
/*      */     
/*  357 */     if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE)
/*      */     {
/*  359 */       if (this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
/*  360 */         throw new HttpPostRequestDecoder.EndOfDataDecoderException();
/*      */       }
/*      */     }
/*  363 */     return (!this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size());
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
/*      */   public InterfaceHttpData next() {
/*  380 */     checkDestroyed();
/*      */     
/*  382 */     if (hasNext()) {
/*  383 */       return this.bodyListHttpData.get(this.bodyListHttpDataRank++);
/*      */     }
/*  385 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   public InterfaceHttpData currentPartialHttpData() {
/*  390 */     if (this.currentFileUpload != null) {
/*  391 */       return this.currentFileUpload;
/*      */     }
/*  393 */     return this.currentAttribute;
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
/*      */   private void parseBody() {
/*  405 */     if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
/*  406 */       if (this.isLastChunk) {
/*  407 */         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
/*      */       }
/*      */       return;
/*      */     } 
/*  411 */     parseBodyMultipart();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void addHttpData(InterfaceHttpData data) {
/*  418 */     if (data == null) {
/*      */       return;
/*      */     }
/*  421 */     List<InterfaceHttpData> datas = this.bodyMapHttpData.get(data.getName());
/*  422 */     if (datas == null) {
/*  423 */       datas = new ArrayList<InterfaceHttpData>(1);
/*  424 */       this.bodyMapHttpData.put(data.getName(), datas);
/*      */     } 
/*  426 */     datas.add(data);
/*  427 */     this.bodyListHttpData.add(data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void parseBodyMultipart() {
/*  438 */     if (this.undecodedChunk == null || this.undecodedChunk.readableBytes() == 0) {
/*      */       return;
/*      */     }
/*      */     
/*  442 */     InterfaceHttpData data = decodeMultipart(this.currentStatus);
/*  443 */     while (data != null) {
/*  444 */       addHttpData(data);
/*  445 */       if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
/*      */         break;
/*      */       }
/*  448 */       data = decodeMultipart(this.currentStatus);
/*      */     } 
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
/*      */   private InterfaceHttpData decodeMultipart(HttpPostRequestDecoder.MultiPartStatus state) {
/*      */     Charset localCharset;
/*      */     Attribute charsetAttribute;
/*      */     Attribute nameAttribute;
/*      */     Attribute finalAttribute;
/*  469 */     switch (state) {
/*      */       case NOTSTARTED:
/*  471 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
/*      */       
/*      */       case PREAMBLE:
/*  474 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
/*      */       
/*      */       case HEADERDELIMITER:
/*  477 */         return findMultipartDelimiter(this.multipartDataBoundary, HttpPostRequestDecoder.MultiPartStatus.DISPOSITION, HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case DISPOSITION:
/*  490 */         return findMultipartDisposition();
/*      */ 
/*      */       
/*      */       case FIELD:
/*  494 */         localCharset = null;
/*  495 */         charsetAttribute = this.currentFieldAttributes.get(HttpHeaderValues.CHARSET);
/*  496 */         if (charsetAttribute != null) {
/*      */           try {
/*  498 */             localCharset = Charset.forName(charsetAttribute.getValue());
/*  499 */           } catch (IOException e) {
/*  500 */             throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  501 */           } catch (UnsupportedCharsetException e) {
/*  502 */             throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */           } 
/*      */         }
/*  505 */         nameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.NAME);
/*  506 */         if (this.currentAttribute == null) {
/*      */           long size;
/*  508 */           Attribute lengthAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);
/*      */           
/*      */           try {
/*  511 */             size = (lengthAttribute != null) ? Long.parseLong(lengthAttribute
/*  512 */                 .getValue()) : 0L;
/*  513 */           } catch (IOException e) {
/*  514 */             throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  515 */           } catch (NumberFormatException ignored) {
/*  516 */             size = 0L;
/*      */           } 
/*      */           try {
/*  519 */             if (size > 0L) {
/*  520 */               this.currentAttribute = this.factory.createAttribute(this.request, 
/*  521 */                   cleanString(nameAttribute.getValue()), size);
/*      */             } else {
/*  523 */               this.currentAttribute = this.factory.createAttribute(this.request, 
/*  524 */                   cleanString(nameAttribute.getValue()));
/*      */             } 
/*  526 */           } catch (NullPointerException e) {
/*  527 */             throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  528 */           } catch (IllegalArgumentException e) {
/*  529 */             throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  530 */           } catch (IOException e) {
/*  531 */             throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */           } 
/*  533 */           if (localCharset != null) {
/*  534 */             this.currentAttribute.setCharset(localCharset);
/*      */           }
/*      */         } 
/*      */         
/*  538 */         if (!loadDataMultipart(this.undecodedChunk, this.multipartDataBoundary, this.currentAttribute))
/*      */         {
/*  540 */           return null;
/*      */         }
/*  542 */         finalAttribute = this.currentAttribute;
/*  543 */         this.currentAttribute = null;
/*  544 */         this.currentFieldAttributes = null;
/*      */         
/*  546 */         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
/*  547 */         return finalAttribute;
/*      */ 
/*      */       
/*      */       case FILEUPLOAD:
/*  551 */         return getFileUpload(this.multipartDataBoundary);
/*      */ 
/*      */ 
/*      */       
/*      */       case MIXEDDELIMITER:
/*  556 */         return findMultipartDelimiter(this.multipartMixedBoundary, HttpPostRequestDecoder.MultiPartStatus.MIXEDDISPOSITION, HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
/*      */ 
/*      */       
/*      */       case MIXEDDISPOSITION:
/*  560 */         return findMultipartDisposition();
/*      */ 
/*      */       
/*      */       case MIXEDFILEUPLOAD:
/*  564 */         return getFileUpload(this.multipartMixedBoundary);
/*      */       
/*      */       case PREEPILOGUE:
/*  567 */         return null;
/*      */       case EPILOGUE:
/*  569 */         return null;
/*      */     } 
/*  571 */     throw new HttpPostRequestDecoder.ErrorDataDecoderException("Shouldn't reach here.");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void skipControlCharacters(ByteBuf undecodedChunk) {
/*  581 */     if (!undecodedChunk.hasArray()) {
/*      */       try {
/*  583 */         skipControlCharactersStandard(undecodedChunk);
/*  584 */       } catch (IndexOutOfBoundsException e1) {
/*  585 */         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e1);
/*      */       } 
/*      */       return;
/*      */     } 
/*  589 */     HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
/*  590 */     while (sao.pos < sao.limit) {
/*  591 */       char c = (char)(sao.bytes[sao.pos++] & 0xFF);
/*  592 */       if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
/*  593 */         sao.setReadPosition(1);
/*      */         return;
/*      */       } 
/*      */     } 
/*  597 */     throw new HttpPostRequestDecoder.NotEnoughDataDecoderException("Access out of bounds");
/*      */   }
/*      */   
/*      */   private static void skipControlCharactersStandard(ByteBuf undecodedChunk) {
/*      */     while (true) {
/*  602 */       char c = (char)undecodedChunk.readUnsignedByte();
/*  603 */       if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
/*  604 */         undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
/*      */         return;
/*      */       } 
/*      */     } 
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
/*      */   private InterfaceHttpData findMultipartDelimiter(String delimiter, HttpPostRequestDecoder.MultiPartStatus dispositionStatus, HttpPostRequestDecoder.MultiPartStatus closeDelimiterStatus) {
/*      */     String newline;
/*  625 */     int readerIndex = this.undecodedChunk.readerIndex();
/*      */     try {
/*  627 */       skipControlCharacters(this.undecodedChunk);
/*  628 */     } catch (NotEnoughDataDecoderException ignored) {
/*  629 */       this.undecodedChunk.readerIndex(readerIndex);
/*  630 */       return null;
/*      */     } 
/*  632 */     skipOneLine();
/*      */     
/*      */     try {
/*  635 */       newline = readDelimiter(this.undecodedChunk, delimiter);
/*  636 */     } catch (NotEnoughDataDecoderException ignored) {
/*  637 */       this.undecodedChunk.readerIndex(readerIndex);
/*  638 */       return null;
/*      */     } 
/*  640 */     if (newline.equals(delimiter)) {
/*  641 */       this.currentStatus = dispositionStatus;
/*  642 */       return decodeMultipart(dispositionStatus);
/*      */     } 
/*  644 */     if (newline.equals(delimiter + "--")) {
/*      */       
/*  646 */       this.currentStatus = closeDelimiterStatus;
/*  647 */       if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER) {
/*      */ 
/*      */         
/*  650 */         this.currentFieldAttributes = null;
/*  651 */         return decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
/*      */       } 
/*  653 */       return null;
/*      */     } 
/*  655 */     this.undecodedChunk.readerIndex(readerIndex);
/*  656 */     throw new HttpPostRequestDecoder.ErrorDataDecoderException("No Multipart delimiter found");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private InterfaceHttpData findMultipartDisposition() {
/*  666 */     int readerIndex = this.undecodedChunk.readerIndex();
/*  667 */     if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
/*  668 */       this.currentFieldAttributes = new TreeMap<CharSequence, Attribute>(CaseIgnoringComparator.INSTANCE);
/*      */     }
/*      */     
/*  671 */     while (!skipOneLine()) {
/*      */       String newline;
/*      */       try {
/*  674 */         skipControlCharacters(this.undecodedChunk);
/*  675 */         newline = readLine(this.undecodedChunk, this.charset);
/*  676 */       } catch (NotEnoughDataDecoderException ignored) {
/*  677 */         this.undecodedChunk.readerIndex(readerIndex);
/*  678 */         return null;
/*      */       } 
/*  680 */       String[] contents = splitMultipartHeader(newline);
/*  681 */       if (HttpHeaderNames.CONTENT_DISPOSITION.contentEqualsIgnoreCase(contents[0])) {
/*      */         boolean checkSecondArg;
/*  683 */         if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
/*  684 */           checkSecondArg = HttpHeaderValues.FORM_DATA.contentEqualsIgnoreCase(contents[1]);
/*      */         } else {
/*      */           
/*  687 */           checkSecondArg = (HttpHeaderValues.ATTACHMENT.contentEqualsIgnoreCase(contents[1]) || HttpHeaderValues.FILE.contentEqualsIgnoreCase(contents[1]));
/*      */         } 
/*  689 */         if (checkSecondArg)
/*      */         {
/*  691 */           for (int i = 2; i < contents.length; i++) {
/*  692 */             Attribute attribute; String[] values = contents[i].split("=", 2);
/*      */             
/*      */             try {
/*  695 */               attribute = getContentDispositionAttribute(values);
/*  696 */             } catch (NullPointerException e) {
/*  697 */               throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  698 */             } catch (IllegalArgumentException e) {
/*  699 */               throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */             } 
/*  701 */             this.currentFieldAttributes.put(attribute.getName(), attribute);
/*      */           }  }  continue;
/*      */       } 
/*  704 */       if (HttpHeaderNames.CONTENT_TRANSFER_ENCODING.contentEqualsIgnoreCase(contents[0])) {
/*      */         Attribute attribute;
/*      */         try {
/*  707 */           attribute = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_TRANSFER_ENCODING.toString(), 
/*  708 */               cleanString(contents[1]));
/*  709 */         } catch (NullPointerException e) {
/*  710 */           throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  711 */         } catch (IllegalArgumentException e) {
/*  712 */           throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */         } 
/*      */         
/*  715 */         this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_TRANSFER_ENCODING, attribute); continue;
/*  716 */       }  if (HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(contents[0])) {
/*      */         Attribute attribute;
/*      */         try {
/*  719 */           attribute = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_LENGTH.toString(), 
/*  720 */               cleanString(contents[1]));
/*  721 */         } catch (NullPointerException e) {
/*  722 */           throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  723 */         } catch (IllegalArgumentException e) {
/*  724 */           throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */         } 
/*      */         
/*  727 */         this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_LENGTH, attribute); continue;
/*  728 */       }  if (HttpHeaderNames.CONTENT_TYPE.contentEqualsIgnoreCase(contents[0])) {
/*      */         
/*  730 */         if (HttpHeaderValues.MULTIPART_MIXED.contentEqualsIgnoreCase(contents[1])) {
/*  731 */           if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
/*  732 */             String values = StringUtil.substringAfter(contents[2], '=');
/*  733 */             this.multipartMixedBoundary = "--" + values;
/*  734 */             this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
/*  735 */             return decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER);
/*      */           } 
/*  737 */           throw new HttpPostRequestDecoder.ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
/*      */         } 
/*      */         
/*  740 */         for (int i = 1; i < contents.length; i++) {
/*  741 */           String charsetHeader = HttpHeaderValues.CHARSET.toString();
/*  742 */           if (contents[i].regionMatches(true, 0, charsetHeader, 0, charsetHeader.length())) {
/*  743 */             Attribute attribute; String values = StringUtil.substringAfter(contents[i], '=');
/*      */             
/*      */             try {
/*  746 */               attribute = this.factory.createAttribute(this.request, charsetHeader, cleanString(values));
/*  747 */             } catch (NullPointerException e) {
/*  748 */               throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  749 */             } catch (IllegalArgumentException e) {
/*  750 */               throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */             } 
/*  752 */             this.currentFieldAttributes.put(HttpHeaderValues.CHARSET, attribute);
/*      */           } else {
/*      */             Attribute attribute;
/*      */             try {
/*  756 */               attribute = this.factory.createAttribute(this.request, 
/*  757 */                   cleanString(contents[0]), contents[i]);
/*  758 */             } catch (NullPointerException e) {
/*  759 */               Attribute attribute1; throw new HttpPostRequestDecoder.ErrorDataDecoderException(attribute1);
/*  760 */             } catch (IllegalArgumentException e) {
/*  761 */               Attribute attribute1; throw new HttpPostRequestDecoder.ErrorDataDecoderException(attribute1);
/*      */             } 
/*  763 */             this.currentFieldAttributes.put(attribute.getName(), attribute);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  770 */     Attribute filenameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
/*  771 */     if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
/*  772 */       if (filenameAttribute != null) {
/*      */         
/*  774 */         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD;
/*      */         
/*  776 */         return decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD);
/*      */       } 
/*      */       
/*  779 */       this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
/*      */       
/*  781 */       return decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FIELD);
/*      */     } 
/*      */     
/*  784 */     if (filenameAttribute != null) {
/*      */       
/*  786 */       this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD;
/*      */       
/*  788 */       return decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD);
/*      */     } 
/*      */     
/*  791 */     throw new HttpPostRequestDecoder.ErrorDataDecoderException("Filename not found");
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*  796 */   private static final String FILENAME_ENCODED = HttpHeaderValues.FILENAME.toString() + '*';
/*      */   
/*      */   private Attribute getContentDispositionAttribute(String... values) {
/*  799 */     String name = cleanString(values[0]);
/*  800 */     String value = values[1];
/*      */ 
/*      */     
/*  803 */     if (HttpHeaderValues.FILENAME.contentEquals(name)) {
/*      */       
/*  805 */       int last = value.length() - 1;
/*  806 */       if (last > 0 && value
/*  807 */         .charAt(0) == '"' && value
/*  808 */         .charAt(last) == '"') {
/*  809 */         value = value.substring(1, last);
/*      */       }
/*  811 */     } else if (FILENAME_ENCODED.equals(name)) {
/*      */       try {
/*  813 */         name = HttpHeaderValues.FILENAME.toString();
/*  814 */         String[] split = value.split("'", 3);
/*  815 */         value = QueryStringDecoder.decodeComponent(split[2], Charset.forName(split[0]));
/*  816 */       } catch (ArrayIndexOutOfBoundsException e) {
/*  817 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  818 */       } catch (UnsupportedCharsetException e) {
/*  819 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */       } 
/*      */     } else {
/*      */       
/*  823 */       value = cleanString(value);
/*      */     } 
/*  825 */     return this.factory.createAttribute(this.request, name, value);
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
/*      */   protected InterfaceHttpData getFileUpload(String delimiter) {
/*  839 */     Attribute encoding = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
/*  840 */     Charset localCharset = this.charset;
/*      */     
/*  842 */     HttpPostBodyUtil.TransferEncodingMechanism mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
/*  843 */     if (encoding != null) {
/*      */       String code;
/*      */       try {
/*  846 */         code = encoding.getValue().toLowerCase();
/*  847 */       } catch (IOException e) {
/*  848 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */       } 
/*  850 */       if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
/*  851 */         localCharset = CharsetUtil.US_ASCII;
/*  852 */       } else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
/*  853 */         localCharset = CharsetUtil.ISO_8859_1;
/*  854 */         mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
/*  855 */       } else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
/*      */         
/*  857 */         mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
/*      */       } else {
/*  859 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException("TransferEncoding Unknown: " + code);
/*      */       } 
/*      */     } 
/*  862 */     Attribute charsetAttribute = this.currentFieldAttributes.get(HttpHeaderValues.CHARSET);
/*  863 */     if (charsetAttribute != null) {
/*      */       try {
/*  865 */         localCharset = Charset.forName(charsetAttribute.getValue());
/*  866 */       } catch (IOException e) {
/*  867 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  868 */       } catch (UnsupportedCharsetException e) {
/*  869 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */       } 
/*      */     }
/*  872 */     if (this.currentFileUpload == null) {
/*  873 */       long size; Attribute filenameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
/*  874 */       Attribute nameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.NAME);
/*  875 */       Attribute contentTypeAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TYPE);
/*  876 */       Attribute lengthAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);
/*      */       
/*      */       try {
/*  879 */         size = (lengthAttribute != null) ? Long.parseLong(lengthAttribute.getValue()) : 0L;
/*  880 */       } catch (IOException e) {
/*  881 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  882 */       } catch (NumberFormatException ignored) {
/*  883 */         size = 0L;
/*      */       } 
/*      */       try {
/*      */         String contentType;
/*  887 */         if (contentTypeAttribute != null) {
/*  888 */           contentType = contentTypeAttribute.getValue();
/*      */         } else {
/*  890 */           contentType = "application/octet-stream";
/*      */         } 
/*  892 */         this.currentFileUpload = this.factory.createFileUpload(this.request, 
/*  893 */             cleanString(nameAttribute.getValue()), cleanString(filenameAttribute.getValue()), contentType, mechanism
/*  894 */             .value(), localCharset, size);
/*      */       }
/*  896 */       catch (NullPointerException e) {
/*  897 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  898 */       } catch (IllegalArgumentException e) {
/*  899 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*  900 */       } catch (IOException e) {
/*  901 */         throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */       } 
/*      */     } 
/*      */     
/*  905 */     if (!loadDataMultipart(this.undecodedChunk, delimiter, this.currentFileUpload))
/*      */     {
/*  907 */       return null;
/*      */     }
/*  909 */     if (this.currentFileUpload.isCompleted()) {
/*      */       
/*  911 */       if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD) {
/*  912 */         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
/*  913 */         this.currentFieldAttributes = null;
/*      */       } else {
/*  915 */         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
/*  916 */         cleanMixedAttributes();
/*      */       } 
/*  918 */       FileUpload fileUpload = this.currentFileUpload;
/*  919 */       this.currentFileUpload = null;
/*  920 */       return fileUpload;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  925 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void destroy() {
/*  935 */     cleanFiles();
/*      */     
/*  937 */     this.destroyed = true;
/*      */     
/*  939 */     if (this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
/*  940 */       this.undecodedChunk.release();
/*  941 */       this.undecodedChunk = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void cleanFiles() {
/*  950 */     checkDestroyed();
/*      */     
/*  952 */     this.factory.cleanRequestHttpData(this.request);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeHttpDataFromClean(InterfaceHttpData data) {
/*  960 */     checkDestroyed();
/*      */     
/*  962 */     this.factory.removeHttpDataFromClean(this.request, data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void cleanMixedAttributes() {
/*  970 */     this.currentFieldAttributes.remove(HttpHeaderValues.CHARSET);
/*  971 */     this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_LENGTH);
/*  972 */     this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
/*  973 */     this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TYPE);
/*  974 */     this.currentFieldAttributes.remove(HttpHeaderValues.FILENAME);
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
/*      */   private static String readLineStandard(ByteBuf undecodedChunk, Charset charset) {
/*  986 */     int readerIndex = undecodedChunk.readerIndex();
/*      */     try {
/*  988 */       ByteBuf line = Unpooled.buffer(64);
/*      */       
/*  990 */       while (undecodedChunk.isReadable()) {
/*  991 */         byte nextByte = undecodedChunk.readByte();
/*  992 */         if (nextByte == 13) {
/*      */           
/*  994 */           nextByte = undecodedChunk.getByte(undecodedChunk.readerIndex());
/*  995 */           if (nextByte == 10) {
/*      */             
/*  997 */             undecodedChunk.readByte();
/*  998 */             return line.toString(charset);
/*      */           } 
/*      */           
/* 1001 */           line.writeByte(13); continue;
/*      */         } 
/* 1003 */         if (nextByte == 10) {
/* 1004 */           return line.toString(charset);
/*      */         }
/* 1006 */         line.writeByte(nextByte);
/*      */       }
/*      */     
/* 1009 */     } catch (IndexOutOfBoundsException e) {
/* 1010 */       undecodedChunk.readerIndex(readerIndex);
/* 1011 */       throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
/*      */     } 
/* 1013 */     undecodedChunk.readerIndex(readerIndex);
/* 1014 */     throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
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
/*      */   private static String readLine(ByteBuf undecodedChunk, Charset charset) {
/* 1026 */     if (!undecodedChunk.hasArray()) {
/* 1027 */       return readLineStandard(undecodedChunk, charset);
/*      */     }
/* 1029 */     HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
/* 1030 */     int readerIndex = undecodedChunk.readerIndex();
/*      */     try {
/* 1032 */       ByteBuf line = Unpooled.buffer(64);
/*      */       
/* 1034 */       while (sao.pos < sao.limit) {
/* 1035 */         byte nextByte = sao.bytes[sao.pos++];
/* 1036 */         if (nextByte == 13) {
/* 1037 */           if (sao.pos < sao.limit) {
/* 1038 */             nextByte = sao.bytes[sao.pos++];
/* 1039 */             if (nextByte == 10) {
/* 1040 */               sao.setReadPosition(0);
/* 1041 */               return line.toString(charset);
/*      */             } 
/*      */             
/* 1044 */             sao.pos--;
/* 1045 */             line.writeByte(13);
/*      */             continue;
/*      */           } 
/* 1048 */           line.writeByte(nextByte); continue;
/*      */         } 
/* 1050 */         if (nextByte == 10) {
/* 1051 */           sao.setReadPosition(0);
/* 1052 */           return line.toString(charset);
/*      */         } 
/* 1054 */         line.writeByte(nextByte);
/*      */       }
/*      */     
/* 1057 */     } catch (IndexOutOfBoundsException e) {
/* 1058 */       undecodedChunk.readerIndex(readerIndex);
/* 1059 */       throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
/*      */     } 
/* 1061 */     undecodedChunk.readerIndex(readerIndex);
/* 1062 */     throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
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
/*      */   private static String readDelimiterStandard(ByteBuf undecodedChunk, String delimiter) {
/* 1081 */     int readerIndex = undecodedChunk.readerIndex();
/*      */     try {
/* 1083 */       StringBuilder sb = new StringBuilder(64);
/* 1084 */       int delimiterPos = 0;
/* 1085 */       int len = delimiter.length();
/* 1086 */       while (undecodedChunk.isReadable() && delimiterPos < len) {
/* 1087 */         byte nextByte = undecodedChunk.readByte();
/* 1088 */         if (nextByte == delimiter.charAt(delimiterPos)) {
/* 1089 */           delimiterPos++;
/* 1090 */           sb.append((char)nextByte);
/*      */           continue;
/*      */         } 
/* 1093 */         undecodedChunk.readerIndex(readerIndex);
/* 1094 */         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */       } 
/*      */ 
/*      */       
/* 1098 */       if (undecodedChunk.isReadable()) {
/* 1099 */         byte nextByte = undecodedChunk.readByte();
/*      */         
/* 1101 */         if (nextByte == 13) {
/* 1102 */           nextByte = undecodedChunk.readByte();
/* 1103 */           if (nextByte == 10) {
/* 1104 */             return sb.toString();
/*      */           }
/*      */ 
/*      */           
/* 1108 */           undecodedChunk.readerIndex(readerIndex);
/* 1109 */           throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */         } 
/* 1111 */         if (nextByte == 10)
/* 1112 */           return sb.toString(); 
/* 1113 */         if (nextByte == 45) {
/* 1114 */           sb.append('-');
/*      */           
/* 1116 */           nextByte = undecodedChunk.readByte();
/* 1117 */           if (nextByte == 45) {
/* 1118 */             sb.append('-');
/*      */             
/* 1120 */             if (undecodedChunk.isReadable()) {
/* 1121 */               nextByte = undecodedChunk.readByte();
/* 1122 */               if (nextByte == 13) {
/* 1123 */                 nextByte = undecodedChunk.readByte();
/* 1124 */                 if (nextByte == 10) {
/* 1125 */                   return sb.toString();
/*      */                 }
/*      */ 
/*      */                 
/* 1129 */                 undecodedChunk.readerIndex(readerIndex);
/* 1130 */                 throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */               } 
/* 1132 */               if (nextByte == 10) {
/* 1133 */                 return sb.toString();
/*      */               }
/*      */ 
/*      */ 
/*      */               
/* 1138 */               undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
/* 1139 */               return sb.toString();
/*      */             } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1146 */             return sb.toString();
/*      */           }
/*      */         
/*      */         }
/*      */       
/*      */       } 
/* 1152 */     } catch (IndexOutOfBoundsException e) {
/* 1153 */       undecodedChunk.readerIndex(readerIndex);
/* 1154 */       throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
/*      */     } 
/* 1156 */     undecodedChunk.readerIndex(readerIndex);
/* 1157 */     throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
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
/*      */   private static String readDelimiter(ByteBuf undecodedChunk, String delimiter) {
/* 1175 */     if (!undecodedChunk.hasArray()) {
/* 1176 */       return readDelimiterStandard(undecodedChunk, delimiter);
/*      */     }
/* 1178 */     HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
/* 1179 */     int readerIndex = undecodedChunk.readerIndex();
/* 1180 */     int delimiterPos = 0;
/* 1181 */     int len = delimiter.length();
/*      */     try {
/* 1183 */       StringBuilder sb = new StringBuilder(64);
/*      */       
/* 1185 */       while (sao.pos < sao.limit && delimiterPos < len) {
/* 1186 */         byte nextByte = sao.bytes[sao.pos++];
/* 1187 */         if (nextByte == delimiter.charAt(delimiterPos)) {
/* 1188 */           delimiterPos++;
/* 1189 */           sb.append((char)nextByte);
/*      */           continue;
/*      */         } 
/* 1192 */         undecodedChunk.readerIndex(readerIndex);
/* 1193 */         throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */       } 
/*      */ 
/*      */       
/* 1197 */       if (sao.pos < sao.limit) {
/* 1198 */         byte nextByte = sao.bytes[sao.pos++];
/* 1199 */         if (nextByte == 13) {
/*      */           
/* 1201 */           if (sao.pos < sao.limit) {
/* 1202 */             nextByte = sao.bytes[sao.pos++];
/* 1203 */             if (nextByte == 10) {
/* 1204 */               sao.setReadPosition(0);
/* 1205 */               return sb.toString();
/*      */             } 
/*      */ 
/*      */             
/* 1209 */             undecodedChunk.readerIndex(readerIndex);
/* 1210 */             throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/* 1215 */           undecodedChunk.readerIndex(readerIndex);
/* 1216 */           throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */         } 
/* 1218 */         if (nextByte == 10) {
/*      */ 
/*      */           
/* 1221 */           sao.setReadPosition(0);
/* 1222 */           return sb.toString();
/* 1223 */         }  if (nextByte == 45) {
/* 1224 */           sb.append('-');
/*      */           
/* 1226 */           if (sao.pos < sao.limit) {
/* 1227 */             nextByte = sao.bytes[sao.pos++];
/* 1228 */             if (nextByte == 45) {
/* 1229 */               sb.append('-');
/*      */               
/* 1231 */               if (sao.pos < sao.limit) {
/* 1232 */                 nextByte = sao.bytes[sao.pos++];
/* 1233 */                 if (nextByte == 13) {
/* 1234 */                   if (sao.pos < sao.limit) {
/* 1235 */                     nextByte = sao.bytes[sao.pos++];
/* 1236 */                     if (nextByte == 10) {
/* 1237 */                       sao.setReadPosition(0);
/* 1238 */                       return sb.toString();
/*      */                     } 
/*      */ 
/*      */                     
/* 1242 */                     undecodedChunk.readerIndex(readerIndex);
/* 1243 */                     throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */                   } 
/*      */ 
/*      */ 
/*      */                   
/* 1248 */                   undecodedChunk.readerIndex(readerIndex);
/* 1249 */                   throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */                 } 
/* 1251 */                 if (nextByte == 10) {
/* 1252 */                   sao.setReadPosition(0);
/* 1253 */                   return sb.toString();
/*      */                 } 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 1259 */                 sao.setReadPosition(1);
/* 1260 */                 return sb.toString();
/*      */               } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1267 */               sao.setReadPosition(0);
/* 1268 */               return sb.toString();
/*      */             }
/*      */           
/*      */           }
/*      */         
/*      */         }
/*      */       
/*      */       } 
/* 1276 */     } catch (IndexOutOfBoundsException e) {
/* 1277 */       undecodedChunk.readerIndex(readerIndex);
/* 1278 */       throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
/*      */     } 
/* 1280 */     undecodedChunk.readerIndex(readerIndex);
/* 1281 */     throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean loadDataMultipartStandard(ByteBuf undecodedChunk, String delimiter, HttpData httpData) {
/* 1291 */     int startReaderIndex = undecodedChunk.readerIndex();
/* 1292 */     int delimeterLength = delimiter.length();
/* 1293 */     int index = 0;
/* 1294 */     int lastPosition = startReaderIndex;
/* 1295 */     byte prevByte = 10;
/* 1296 */     boolean delimiterFound = false;
/* 1297 */     while (undecodedChunk.isReadable()) {
/* 1298 */       byte nextByte = undecodedChunk.readByte();
/*      */       
/* 1300 */       if (prevByte == 10 && nextByte == delimiter.codePointAt(index)) {
/* 1301 */         index++;
/* 1302 */         if (delimeterLength == index) {
/* 1303 */           delimiterFound = true;
/*      */           break;
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1308 */       lastPosition = undecodedChunk.readerIndex();
/* 1309 */       if (nextByte == 10) {
/* 1310 */         index = 0;
/* 1311 */         lastPosition -= (prevByte == 13) ? 2 : 1;
/*      */       } 
/* 1313 */       prevByte = nextByte;
/*      */     } 
/* 1315 */     if (prevByte == 13) {
/* 1316 */       lastPosition--;
/*      */     }
/* 1318 */     ByteBuf content = undecodedChunk.copy(startReaderIndex, lastPosition - startReaderIndex);
/*      */     try {
/* 1320 */       httpData.addContent(content, delimiterFound);
/* 1321 */     } catch (IOException e) {
/* 1322 */       throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */     } 
/* 1324 */     undecodedChunk.readerIndex(lastPosition);
/* 1325 */     return delimiterFound;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean loadDataMultipart(ByteBuf undecodedChunk, String delimiter, HttpData httpData) {
/* 1335 */     if (!undecodedChunk.hasArray()) {
/* 1336 */       return loadDataMultipartStandard(undecodedChunk, delimiter, httpData);
/*      */     }
/* 1338 */     HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
/* 1339 */     int startReaderIndex = undecodedChunk.readerIndex();
/* 1340 */     int delimeterLength = delimiter.length();
/* 1341 */     int index = 0;
/* 1342 */     int lastRealPos = sao.pos;
/* 1343 */     byte prevByte = 10;
/* 1344 */     boolean delimiterFound = false;
/* 1345 */     while (sao.pos < sao.limit) {
/* 1346 */       byte nextByte = sao.bytes[sao.pos++];
/*      */       
/* 1348 */       if (prevByte == 10 && nextByte == delimiter.codePointAt(index)) {
/* 1349 */         index++;
/* 1350 */         if (delimeterLength == index) {
/* 1351 */           delimiterFound = true;
/*      */           break;
/*      */         } 
/*      */         continue;
/*      */       } 
/* 1356 */       lastRealPos = sao.pos;
/* 1357 */       if (nextByte == 10) {
/* 1358 */         index = 0;
/* 1359 */         lastRealPos -= (prevByte == 13) ? 2 : 1;
/*      */       } 
/* 1361 */       prevByte = nextByte;
/*      */     } 
/* 1363 */     if (prevByte == 13) {
/* 1364 */       lastRealPos--;
/*      */     }
/* 1366 */     int lastPosition = sao.getReadPosition(lastRealPos);
/* 1367 */     ByteBuf content = undecodedChunk.copy(startReaderIndex, lastPosition - startReaderIndex);
/*      */     try {
/* 1369 */       httpData.addContent(content, delimiterFound);
/* 1370 */     } catch (IOException e) {
/* 1371 */       throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/*      */     } 
/* 1373 */     undecodedChunk.readerIndex(lastPosition);
/* 1374 */     return delimiterFound;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String cleanString(String field) {
/* 1383 */     int size = field.length();
/* 1384 */     StringBuilder sb = new StringBuilder(size);
/* 1385 */     for (int i = 0; i < size; i++) {
/* 1386 */       char nextChar = field.charAt(i);
/* 1387 */       switch (nextChar) {
/*      */         case '\t':
/*      */         case ',':
/*      */         case ':':
/*      */         case ';':
/*      */         case '=':
/* 1393 */           sb.append(' ');
/*      */           break;
/*      */         
/*      */         case '"':
/*      */           break;
/*      */         default:
/* 1399 */           sb.append(nextChar);
/*      */           break;
/*      */       } 
/*      */     } 
/* 1403 */     return sb.toString().trim();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean skipOneLine() {
/* 1412 */     if (!this.undecodedChunk.isReadable()) {
/* 1413 */       return false;
/*      */     }
/* 1415 */     byte nextByte = this.undecodedChunk.readByte();
/* 1416 */     if (nextByte == 13) {
/* 1417 */       if (!this.undecodedChunk.isReadable()) {
/* 1418 */         this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
/* 1419 */         return false;
/*      */       } 
/* 1421 */       nextByte = this.undecodedChunk.readByte();
/* 1422 */       if (nextByte == 10) {
/* 1423 */         return true;
/*      */       }
/* 1425 */       this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 2);
/* 1426 */       return false;
/*      */     } 
/* 1428 */     if (nextByte == 10) {
/* 1429 */       return true;
/*      */     }
/* 1431 */     this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
/* 1432 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String[] splitMultipartHeader(String sb) {
/*      */     String[] values;
/* 1442 */     ArrayList<String> headers = new ArrayList<String>(1);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1448 */     int nameStart = HttpPostBodyUtil.findNonWhitespace(sb, 0); int nameEnd;
/* 1449 */     for (nameEnd = nameStart; nameEnd < sb.length(); nameEnd++) {
/* 1450 */       char ch = sb.charAt(nameEnd);
/* 1451 */       if (ch == ':' || Character.isWhitespace(ch))
/*      */         break; 
/*      */     } 
/*      */     int colonEnd;
/* 1455 */     for (colonEnd = nameEnd; colonEnd < sb.length(); colonEnd++) {
/* 1456 */       if (sb.charAt(colonEnd) == ':') {
/* 1457 */         colonEnd++;
/*      */         break;
/*      */       } 
/*      */     } 
/* 1461 */     int valueStart = HttpPostBodyUtil.findNonWhitespace(sb, colonEnd);
/* 1462 */     int valueEnd = HttpPostBodyUtil.findEndOfString(sb);
/* 1463 */     headers.add(sb.substring(nameStart, nameEnd));
/* 1464 */     String svalue = (valueStart >= valueEnd) ? "" : sb.substring(valueStart, valueEnd);
/*      */     
/* 1466 */     if (svalue.indexOf(';') >= 0) {
/* 1467 */       values = splitMultipartHeaderValues(svalue);
/*      */     } else {
/* 1469 */       values = svalue.split(",");
/*      */     } 
/* 1471 */     for (String value : values) {
/* 1472 */       headers.add(value.trim());
/*      */     }
/* 1474 */     String[] array = new String[headers.size()];
/* 1475 */     for (int i = 0; i < headers.size(); i++) {
/* 1476 */       array[i] = headers.get(i);
/*      */     }
/* 1478 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String[] splitMultipartHeaderValues(String svalue) {
/* 1486 */     List<String> values = InternalThreadLocalMap.get().arrayList(1);
/* 1487 */     boolean inQuote = false;
/* 1488 */     boolean escapeNext = false;
/* 1489 */     int start = 0;
/* 1490 */     for (int i = 0; i < svalue.length(); i++) {
/* 1491 */       char c = svalue.charAt(i);
/* 1492 */       if (inQuote) {
/* 1493 */         if (escapeNext) {
/* 1494 */           escapeNext = false;
/*      */         }
/* 1496 */         else if (c == '\\') {
/* 1497 */           escapeNext = true;
/* 1498 */         } else if (c == '"') {
/* 1499 */           inQuote = false;
/*      */         }
/*      */       
/*      */       }
/* 1503 */       else if (c == '"') {
/* 1504 */         inQuote = true;
/* 1505 */       } else if (c == ';') {
/* 1506 */         values.add(svalue.substring(start, i));
/* 1507 */         start = i + 1;
/*      */       } 
/*      */     } 
/*      */     
/* 1511 */     values.add(svalue.substring(start));
/* 1512 */     return values.<String>toArray(new String[0]);
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\HttpPostMultipartRequestDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */