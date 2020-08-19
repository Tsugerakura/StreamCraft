/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpConstants;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpContent;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.QueryStringDecoder;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*     */ public class HttpPostStandardRequestDecoder
/*     */   implements InterfaceHttpPostRequestDecoder
/*     */ {
/*     */   private final HttpDataFactory factory;
/*     */   private final HttpRequest request;
/*     */   private final Charset charset;
/*     */   private boolean isLastChunk;
/*  72 */   private final List<InterfaceHttpData> bodyListHttpData = new ArrayList<InterfaceHttpData>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  77 */   private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<String, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ByteBuf undecodedChunk;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int bodyListHttpDataRank;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  93 */   private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
/*     */ 
/*     */   
/*     */   private Attribute currentAttribute;
/*     */ 
/*     */   
/*     */   private boolean destroyed;
/*     */ 
/*     */   
/* 102 */   private int discardThreshold = 10485760;
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
/*     */   public HttpPostStandardRequestDecoder(HttpRequest request) {
/* 115 */     this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
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
/*     */ 
/*     */   
/*     */   public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request) {
/* 131 */     this(factory, request, HttpConstants.DEFAULT_CHARSET);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
/* 149 */     this.request = (HttpRequest)ObjectUtil.checkNotNull(request, "request");
/* 150 */     this.charset = (Charset)ObjectUtil.checkNotNull(charset, "charset");
/* 151 */     this.factory = (HttpDataFactory)ObjectUtil.checkNotNull(factory, "factory");
/*     */     try {
/* 153 */       if (request instanceof HttpContent) {
/*     */ 
/*     */         
/* 156 */         offer((HttpContent)request);
/*     */       } else {
/* 158 */         this.undecodedChunk = Unpooled.buffer();
/* 159 */         parseBody();
/*     */       } 
/* 161 */     } catch (Throwable e) {
/* 162 */       destroy();
/* 163 */       PlatformDependent.throwException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkDestroyed() {
/* 168 */     if (this.destroyed) {
/* 169 */       throw new IllegalStateException(HttpPostStandardRequestDecoder.class.getSimpleName() + " was destroyed already");
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
/*     */   public boolean isMultipart() {
/* 181 */     checkDestroyed();
/* 182 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDiscardThreshold(int discardThreshold) {
/* 192 */     this.discardThreshold = ObjectUtil.checkPositiveOrZero(discardThreshold, "discardThreshold");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDiscardThreshold() {
/* 200 */     return this.discardThreshold;
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
/*     */   
/*     */   public List<InterfaceHttpData> getBodyHttpDatas() {
/* 215 */     checkDestroyed();
/*     */     
/* 217 */     if (!this.isLastChunk) {
/* 218 */       throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*     */     }
/* 220 */     return this.bodyListHttpData;
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
/*     */ 
/*     */   
/*     */   public List<InterfaceHttpData> getBodyHttpDatas(String name) {
/* 236 */     checkDestroyed();
/*     */     
/* 238 */     if (!this.isLastChunk) {
/* 239 */       throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*     */     }
/* 241 */     return this.bodyMapHttpData.get(name);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public InterfaceHttpData getBodyHttpData(String name) {
/* 258 */     checkDestroyed();
/*     */     
/* 260 */     if (!this.isLastChunk) {
/* 261 */       throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
/*     */     }
/* 263 */     List<InterfaceHttpData> list = this.bodyMapHttpData.get(name);
/* 264 */     if (list != null) {
/* 265 */       return list.get(0);
/*     */     }
/* 267 */     return null;
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
/*     */   public HttpPostStandardRequestDecoder offer(HttpContent content) {
/* 281 */     checkDestroyed();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 286 */     ByteBuf buf = content.content();
/* 287 */     if (this.undecodedChunk == null) {
/* 288 */       this.undecodedChunk = buf.copy();
/*     */     } else {
/* 290 */       this.undecodedChunk.writeBytes(buf);
/*     */     } 
/* 292 */     if (content instanceof pro.gravit.repackage.io.netty.handler.codec.http.LastHttpContent) {
/* 293 */       this.isLastChunk = true;
/*     */     }
/* 295 */     parseBody();
/* 296 */     if (this.undecodedChunk != null && this.undecodedChunk.writerIndex() > this.discardThreshold) {
/* 297 */       this.undecodedChunk.discardReadBytes();
/*     */     }
/* 299 */     return this;
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
/*     */   
/*     */   public boolean hasNext() {
/* 314 */     checkDestroyed();
/*     */     
/* 316 */     if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE)
/*     */     {
/* 318 */       if (this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
/* 319 */         throw new HttpPostRequestDecoder.EndOfDataDecoderException();
/*     */       }
/*     */     }
/* 322 */     return (!this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size());
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
/*     */ 
/*     */ 
/*     */   
/*     */   public InterfaceHttpData next() {
/* 339 */     checkDestroyed();
/*     */     
/* 341 */     if (hasNext()) {
/* 342 */       return this.bodyListHttpData.get(this.bodyListHttpDataRank++);
/*     */     }
/* 344 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData currentPartialHttpData() {
/* 349 */     return this.currentAttribute;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseBody() {
/* 360 */     if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
/* 361 */       if (this.isLastChunk) {
/* 362 */         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
/*     */       }
/*     */       return;
/*     */     } 
/* 366 */     parseBodyAttributes();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void addHttpData(InterfaceHttpData data) {
/* 373 */     if (data == null) {
/*     */       return;
/*     */     }
/* 376 */     List<InterfaceHttpData> datas = this.bodyMapHttpData.get(data.getName());
/* 377 */     if (datas == null) {
/* 378 */       datas = new ArrayList<InterfaceHttpData>(1);
/* 379 */       this.bodyMapHttpData.put(data.getName(), datas);
/*     */     } 
/* 381 */     datas.add(data);
/* 382 */     this.bodyListHttpData.add(data);
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
/*     */   private void parseBodyAttributesStandard() {
/* 394 */     int firstpos = this.undecodedChunk.readerIndex();
/* 395 */     int currentpos = firstpos;
/*     */ 
/*     */     
/* 398 */     if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
/* 399 */       this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
/*     */     }
/* 401 */     boolean contRead = true;
/*     */     try {
/* 403 */       while (this.undecodedChunk.isReadable() && contRead) {
/* 404 */         char read = (char)this.undecodedChunk.readUnsignedByte();
/* 405 */         currentpos++;
/* 406 */         switch (this.currentStatus) {
/*     */           case DISPOSITION:
/* 408 */             if (read == '=') {
/* 409 */               this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
/* 410 */               int equalpos = currentpos - 1;
/* 411 */               String key = decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
/*     */               
/* 413 */               this.currentAttribute = this.factory.createAttribute(this.request, key);
/* 414 */               firstpos = currentpos; continue;
/* 415 */             }  if (read == '&') {
/* 416 */               this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
/* 417 */               int ampersandpos = currentpos - 1;
/* 418 */               String key = decodeAttribute(this.undecodedChunk
/* 419 */                   .toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
/* 420 */               this.currentAttribute = this.factory.createAttribute(this.request, key);
/* 421 */               this.currentAttribute.setValue("");
/* 422 */               addHttpData(this.currentAttribute);
/* 423 */               this.currentAttribute = null;
/* 424 */               firstpos = currentpos;
/* 425 */               contRead = true;
/*     */             } 
/*     */             continue;
/*     */           case FIELD:
/* 429 */             if (read == '&') {
/* 430 */               this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
/* 431 */               int ampersandpos = currentpos - 1;
/* 432 */               setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
/* 433 */               firstpos = currentpos;
/* 434 */               contRead = true; continue;
/* 435 */             }  if (read == '\r') {
/* 436 */               if (this.undecodedChunk.isReadable()) {
/* 437 */                 read = (char)this.undecodedChunk.readUnsignedByte();
/* 438 */                 currentpos++;
/* 439 */                 if (read == '\n') {
/* 440 */                   this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
/* 441 */                   int ampersandpos = currentpos - 2;
/* 442 */                   setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
/* 443 */                   firstpos = currentpos;
/* 444 */                   contRead = false;
/*     */                   continue;
/*     */                 } 
/* 447 */                 throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
/*     */               } 
/*     */               
/* 450 */               currentpos--; continue;
/*     */             } 
/* 452 */             if (read == '\n') {
/* 453 */               this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
/* 454 */               int ampersandpos = currentpos - 1;
/* 455 */               setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
/* 456 */               firstpos = currentpos;
/* 457 */               contRead = false;
/*     */             } 
/*     */             continue;
/*     */         } 
/*     */         
/* 462 */         contRead = false;
/*     */       } 
/*     */       
/* 465 */       if (this.isLastChunk && this.currentAttribute != null) {
/*     */         
/* 467 */         int ampersandpos = currentpos;
/* 468 */         if (ampersandpos > firstpos) {
/* 469 */           setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
/* 470 */         } else if (!this.currentAttribute.isCompleted()) {
/* 471 */           setFinalBuffer(Unpooled.EMPTY_BUFFER);
/*     */         } 
/* 473 */         firstpos = currentpos;
/* 474 */         this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
/* 475 */       } else if (contRead && this.currentAttribute != null && this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
/*     */         
/* 477 */         this.currentAttribute.addContent(this.undecodedChunk.copy(firstpos, currentpos - firstpos), false);
/*     */         
/* 479 */         firstpos = currentpos;
/*     */       } 
/* 481 */       this.undecodedChunk.readerIndex(firstpos);
/* 482 */     } catch (ErrorDataDecoderException e) {
/*     */       
/* 484 */       this.undecodedChunk.readerIndex(firstpos);
/* 485 */       throw e;
/* 486 */     } catch (IOException e) {
/*     */       
/* 488 */       this.undecodedChunk.readerIndex(firstpos);
/* 489 */       throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
/* 490 */     } catch (IllegalArgumentException e) {
/*     */       
/* 492 */       this.undecodedChunk.readerIndex(firstpos);
/* 493 */       throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
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
/*     */   private void parseBodyAttributes() {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   4: invokevirtual hasArray : ()Z
/*     */     //   7: ifne -> 15
/*     */     //   10: aload_0
/*     */     //   11: invokespecial parseBodyAttributesStandard : ()V
/*     */     //   14: return
/*     */     //   15: new pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostBodyUtil$SeekAheadOptimize
/*     */     //   18: dup
/*     */     //   19: aload_0
/*     */     //   20: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   23: invokespecial <init> : (Lpro/gravit/repackage/io/netty/buffer/ByteBuf;)V
/*     */     //   26: astore_1
/*     */     //   27: aload_0
/*     */     //   28: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   31: invokevirtual readerIndex : ()I
/*     */     //   34: istore_2
/*     */     //   35: iload_2
/*     */     //   36: istore_3
/*     */     //   37: aload_0
/*     */     //   38: getfield currentStatus : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   41: getstatic pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.NOTSTARTED : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   44: if_acmpne -> 54
/*     */     //   47: aload_0
/*     */     //   48: getstatic pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.DISPOSITION : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   51: putfield currentStatus : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   54: iconst_1
/*     */     //   55: istore #6
/*     */     //   57: aload_1
/*     */     //   58: getfield pos : I
/*     */     //   61: aload_1
/*     */     //   62: getfield limit : I
/*     */     //   65: if_icmpge -> 522
/*     */     //   68: aload_1
/*     */     //   69: getfield bytes : [B
/*     */     //   72: aload_1
/*     */     //   73: dup
/*     */     //   74: getfield pos : I
/*     */     //   77: dup_x1
/*     */     //   78: iconst_1
/*     */     //   79: iadd
/*     */     //   80: putfield pos : I
/*     */     //   83: baload
/*     */     //   84: sipush #255
/*     */     //   87: iand
/*     */     //   88: i2c
/*     */     //   89: istore #7
/*     */     //   91: iinc #3, 1
/*     */     //   94: getstatic pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostStandardRequestDecoder$1.$SwitchMap$io$netty$handler$codec$http$multipart$HttpPostRequestDecoder$MultiPartStatus : [I
/*     */     //   97: aload_0
/*     */     //   98: getfield currentStatus : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   101: invokevirtual ordinal : ()I
/*     */     //   104: iaload
/*     */     //   105: lookupswitch default -> 508, 1 -> 132, 2 -> 296
/*     */     //   132: iload #7
/*     */     //   134: bipush #61
/*     */     //   136: if_icmpne -> 200
/*     */     //   139: aload_0
/*     */     //   140: getstatic pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.FIELD : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   143: putfield currentStatus : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   146: iload_3
/*     */     //   147: iconst_1
/*     */     //   148: isub
/*     */     //   149: istore #4
/*     */     //   151: aload_0
/*     */     //   152: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   155: iload_2
/*     */     //   156: iload #4
/*     */     //   158: iload_2
/*     */     //   159: isub
/*     */     //   160: aload_0
/*     */     //   161: getfield charset : Ljava/nio/charset/Charset;
/*     */     //   164: invokevirtual toString : (IILjava/nio/charset/Charset;)Ljava/lang/String;
/*     */     //   167: aload_0
/*     */     //   168: getfield charset : Ljava/nio/charset/Charset;
/*     */     //   171: invokestatic decodeAttribute : (Ljava/lang/String;Ljava/nio/charset/Charset;)Ljava/lang/String;
/*     */     //   174: astore #8
/*     */     //   176: aload_0
/*     */     //   177: aload_0
/*     */     //   178: getfield factory : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpDataFactory;
/*     */     //   181: aload_0
/*     */     //   182: getfield request : Lpro/gravit/repackage/io/netty/handler/codec/http/HttpRequest;
/*     */     //   185: aload #8
/*     */     //   187: invokeinterface createAttribute : (Lpro/gravit/repackage/io/netty/handler/codec/http/HttpRequest;Ljava/lang/String;)Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/Attribute;
/*     */     //   192: putfield currentAttribute : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/Attribute;
/*     */     //   195: iload_3
/*     */     //   196: istore_2
/*     */     //   197: goto -> 519
/*     */     //   200: iload #7
/*     */     //   202: bipush #38
/*     */     //   204: if_icmpne -> 519
/*     */     //   207: aload_0
/*     */     //   208: getstatic pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.DISPOSITION : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   211: putfield currentStatus : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   214: iload_3
/*     */     //   215: iconst_1
/*     */     //   216: isub
/*     */     //   217: istore #5
/*     */     //   219: aload_0
/*     */     //   220: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   223: iload_2
/*     */     //   224: iload #5
/*     */     //   226: iload_2
/*     */     //   227: isub
/*     */     //   228: aload_0
/*     */     //   229: getfield charset : Ljava/nio/charset/Charset;
/*     */     //   232: invokevirtual toString : (IILjava/nio/charset/Charset;)Ljava/lang/String;
/*     */     //   235: aload_0
/*     */     //   236: getfield charset : Ljava/nio/charset/Charset;
/*     */     //   239: invokestatic decodeAttribute : (Ljava/lang/String;Ljava/nio/charset/Charset;)Ljava/lang/String;
/*     */     //   242: astore #8
/*     */     //   244: aload_0
/*     */     //   245: aload_0
/*     */     //   246: getfield factory : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpDataFactory;
/*     */     //   249: aload_0
/*     */     //   250: getfield request : Lpro/gravit/repackage/io/netty/handler/codec/http/HttpRequest;
/*     */     //   253: aload #8
/*     */     //   255: invokeinterface createAttribute : (Lpro/gravit/repackage/io/netty/handler/codec/http/HttpRequest;Ljava/lang/String;)Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/Attribute;
/*     */     //   260: putfield currentAttribute : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/Attribute;
/*     */     //   263: aload_0
/*     */     //   264: getfield currentAttribute : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/Attribute;
/*     */     //   267: ldc_w ''
/*     */     //   270: invokeinterface setValue : (Ljava/lang/String;)V
/*     */     //   275: aload_0
/*     */     //   276: aload_0
/*     */     //   277: getfield currentAttribute : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/Attribute;
/*     */     //   280: invokevirtual addHttpData : (Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/InterfaceHttpData;)V
/*     */     //   283: aload_0
/*     */     //   284: aconst_null
/*     */     //   285: putfield currentAttribute : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/Attribute;
/*     */     //   288: iload_3
/*     */     //   289: istore_2
/*     */     //   290: iconst_1
/*     */     //   291: istore #6
/*     */     //   293: goto -> 519
/*     */     //   296: iload #7
/*     */     //   298: bipush #38
/*     */     //   300: if_icmpne -> 339
/*     */     //   303: aload_0
/*     */     //   304: getstatic pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.DISPOSITION : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   307: putfield currentStatus : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   310: iload_3
/*     */     //   311: iconst_1
/*     */     //   312: isub
/*     */     //   313: istore #5
/*     */     //   315: aload_0
/*     */     //   316: aload_0
/*     */     //   317: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   320: iload_2
/*     */     //   321: iload #5
/*     */     //   323: iload_2
/*     */     //   324: isub
/*     */     //   325: invokevirtual copy : (II)Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   328: invokespecial setFinalBuffer : (Lpro/gravit/repackage/io/netty/buffer/ByteBuf;)V
/*     */     //   331: iload_3
/*     */     //   332: istore_2
/*     */     //   333: iconst_1
/*     */     //   334: istore #6
/*     */     //   336: goto -> 519
/*     */     //   339: iload #7
/*     */     //   341: bipush #13
/*     */     //   343: if_icmpne -> 460
/*     */     //   346: aload_1
/*     */     //   347: getfield pos : I
/*     */     //   350: aload_1
/*     */     //   351: getfield limit : I
/*     */     //   354: if_icmpge -> 447
/*     */     //   357: aload_1
/*     */     //   358: getfield bytes : [B
/*     */     //   361: aload_1
/*     */     //   362: dup
/*     */     //   363: getfield pos : I
/*     */     //   366: dup_x1
/*     */     //   367: iconst_1
/*     */     //   368: iadd
/*     */     //   369: putfield pos : I
/*     */     //   372: baload
/*     */     //   373: sipush #255
/*     */     //   376: iand
/*     */     //   377: i2c
/*     */     //   378: istore #7
/*     */     //   380: iinc #3, 1
/*     */     //   383: iload #7
/*     */     //   385: bipush #10
/*     */     //   387: if_icmpne -> 431
/*     */     //   390: aload_0
/*     */     //   391: getstatic pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.PREEPILOGUE : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   394: putfield currentStatus : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   397: iload_3
/*     */     //   398: iconst_2
/*     */     //   399: isub
/*     */     //   400: istore #5
/*     */     //   402: aload_1
/*     */     //   403: iconst_0
/*     */     //   404: invokevirtual setReadPosition : (I)V
/*     */     //   407: aload_0
/*     */     //   408: aload_0
/*     */     //   409: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   412: iload_2
/*     */     //   413: iload #5
/*     */     //   415: iload_2
/*     */     //   416: isub
/*     */     //   417: invokevirtual copy : (II)Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   420: invokespecial setFinalBuffer : (Lpro/gravit/repackage/io/netty/buffer/ByteBuf;)V
/*     */     //   423: iload_3
/*     */     //   424: istore_2
/*     */     //   425: iconst_0
/*     */     //   426: istore #6
/*     */     //   428: goto -> 522
/*     */     //   431: aload_1
/*     */     //   432: iconst_0
/*     */     //   433: invokevirtual setReadPosition : (I)V
/*     */     //   436: new pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$ErrorDataDecoderException
/*     */     //   439: dup
/*     */     //   440: ldc_w 'Bad end of line'
/*     */     //   443: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   446: athrow
/*     */     //   447: aload_1
/*     */     //   448: getfield limit : I
/*     */     //   451: ifle -> 519
/*     */     //   454: iinc #3, -1
/*     */     //   457: goto -> 519
/*     */     //   460: iload #7
/*     */     //   462: bipush #10
/*     */     //   464: if_icmpne -> 519
/*     */     //   467: aload_0
/*     */     //   468: getstatic pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.PREEPILOGUE : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   471: putfield currentStatus : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   474: iload_3
/*     */     //   475: iconst_1
/*     */     //   476: isub
/*     */     //   477: istore #5
/*     */     //   479: aload_1
/*     */     //   480: iconst_0
/*     */     //   481: invokevirtual setReadPosition : (I)V
/*     */     //   484: aload_0
/*     */     //   485: aload_0
/*     */     //   486: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   489: iload_2
/*     */     //   490: iload #5
/*     */     //   492: iload_2
/*     */     //   493: isub
/*     */     //   494: invokevirtual copy : (II)Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   497: invokespecial setFinalBuffer : (Lpro/gravit/repackage/io/netty/buffer/ByteBuf;)V
/*     */     //   500: iload_3
/*     */     //   501: istore_2
/*     */     //   502: iconst_0
/*     */     //   503: istore #6
/*     */     //   505: goto -> 522
/*     */     //   508: aload_1
/*     */     //   509: iconst_0
/*     */     //   510: invokevirtual setReadPosition : (I)V
/*     */     //   513: iconst_0
/*     */     //   514: istore #6
/*     */     //   516: goto -> 522
/*     */     //   519: goto -> 57
/*     */     //   522: aload_0
/*     */     //   523: getfield isLastChunk : Z
/*     */     //   526: ifeq -> 595
/*     */     //   529: aload_0
/*     */     //   530: getfield currentAttribute : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/Attribute;
/*     */     //   533: ifnull -> 595
/*     */     //   536: iload_3
/*     */     //   537: istore #5
/*     */     //   539: iload #5
/*     */     //   541: iload_2
/*     */     //   542: if_icmple -> 564
/*     */     //   545: aload_0
/*     */     //   546: aload_0
/*     */     //   547: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   550: iload_2
/*     */     //   551: iload #5
/*     */     //   553: iload_2
/*     */     //   554: isub
/*     */     //   555: invokevirtual copy : (II)Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   558: invokespecial setFinalBuffer : (Lpro/gravit/repackage/io/netty/buffer/ByteBuf;)V
/*     */     //   561: goto -> 583
/*     */     //   564: aload_0
/*     */     //   565: getfield currentAttribute : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/Attribute;
/*     */     //   568: invokeinterface isCompleted : ()Z
/*     */     //   573: ifne -> 583
/*     */     //   576: aload_0
/*     */     //   577: getstatic pro/gravit/repackage/io/netty/buffer/Unpooled.EMPTY_BUFFER : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   580: invokespecial setFinalBuffer : (Lpro/gravit/repackage/io/netty/buffer/ByteBuf;)V
/*     */     //   583: iload_3
/*     */     //   584: istore_2
/*     */     //   585: aload_0
/*     */     //   586: getstatic pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.EPILOGUE : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   589: putfield currentStatus : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   592: goto -> 640
/*     */     //   595: iload #6
/*     */     //   597: ifeq -> 640
/*     */     //   600: aload_0
/*     */     //   601: getfield currentAttribute : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/Attribute;
/*     */     //   604: ifnull -> 640
/*     */     //   607: aload_0
/*     */     //   608: getfield currentStatus : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   611: getstatic pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.FIELD : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
/*     */     //   614: if_acmpne -> 640
/*     */     //   617: aload_0
/*     */     //   618: getfield currentAttribute : Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/Attribute;
/*     */     //   621: aload_0
/*     */     //   622: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   625: iload_2
/*     */     //   626: iload_3
/*     */     //   627: iload_2
/*     */     //   628: isub
/*     */     //   629: invokevirtual copy : (II)Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   632: iconst_0
/*     */     //   633: invokeinterface addContent : (Lpro/gravit/repackage/io/netty/buffer/ByteBuf;Z)V
/*     */     //   638: iload_3
/*     */     //   639: istore_2
/*     */     //   640: aload_0
/*     */     //   641: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   644: iload_2
/*     */     //   645: invokevirtual readerIndex : (I)Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   648: pop
/*     */     //   649: goto -> 708
/*     */     //   652: astore #7
/*     */     //   654: aload_0
/*     */     //   655: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   658: iload_2
/*     */     //   659: invokevirtual readerIndex : (I)Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   662: pop
/*     */     //   663: aload #7
/*     */     //   665: athrow
/*     */     //   666: astore #7
/*     */     //   668: aload_0
/*     */     //   669: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   672: iload_2
/*     */     //   673: invokevirtual readerIndex : (I)Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   676: pop
/*     */     //   677: new pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$ErrorDataDecoderException
/*     */     //   680: dup
/*     */     //   681: aload #7
/*     */     //   683: invokespecial <init> : (Ljava/lang/Throwable;)V
/*     */     //   686: athrow
/*     */     //   687: astore #7
/*     */     //   689: aload_0
/*     */     //   690: getfield undecodedChunk : Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   693: iload_2
/*     */     //   694: invokevirtual readerIndex : (I)Lpro/gravit/repackage/io/netty/buffer/ByteBuf;
/*     */     //   697: pop
/*     */     //   698: new pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$ErrorDataDecoderException
/*     */     //   701: dup
/*     */     //   702: aload #7
/*     */     //   704: invokespecial <init> : (Ljava/lang/Throwable;)V
/*     */     //   707: athrow
/*     */     //   708: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #506	-> 0
/*     */     //   #507	-> 10
/*     */     //   #508	-> 14
/*     */     //   #510	-> 15
/*     */     //   #511	-> 27
/*     */     //   #512	-> 35
/*     */     //   #515	-> 37
/*     */     //   #516	-> 47
/*     */     //   #518	-> 54
/*     */     //   #520	-> 57
/*     */     //   #521	-> 68
/*     */     //   #522	-> 91
/*     */     //   #523	-> 94
/*     */     //   #525	-> 132
/*     */     //   #526	-> 139
/*     */     //   #527	-> 146
/*     */     //   #528	-> 151
/*     */     //   #530	-> 176
/*     */     //   #531	-> 195
/*     */     //   #532	-> 197
/*     */     //   #533	-> 207
/*     */     //   #534	-> 214
/*     */     //   #535	-> 219
/*     */     //   #536	-> 232
/*     */     //   #535	-> 239
/*     */     //   #537	-> 244
/*     */     //   #538	-> 263
/*     */     //   #539	-> 275
/*     */     //   #540	-> 283
/*     */     //   #541	-> 288
/*     */     //   #542	-> 290
/*     */     //   #543	-> 293
/*     */     //   #546	-> 296
/*     */     //   #547	-> 303
/*     */     //   #548	-> 310
/*     */     //   #549	-> 315
/*     */     //   #550	-> 331
/*     */     //   #551	-> 333
/*     */     //   #552	-> 339
/*     */     //   #553	-> 346
/*     */     //   #554	-> 357
/*     */     //   #555	-> 380
/*     */     //   #556	-> 383
/*     */     //   #557	-> 390
/*     */     //   #558	-> 397
/*     */     //   #559	-> 402
/*     */     //   #560	-> 407
/*     */     //   #561	-> 423
/*     */     //   #562	-> 425
/*     */     //   #563	-> 428
/*     */     //   #566	-> 431
/*     */     //   #567	-> 436
/*     */     //   #570	-> 447
/*     */     //   #571	-> 454
/*     */     //   #574	-> 460
/*     */     //   #575	-> 467
/*     */     //   #576	-> 474
/*     */     //   #577	-> 479
/*     */     //   #578	-> 484
/*     */     //   #579	-> 500
/*     */     //   #580	-> 502
/*     */     //   #581	-> 505
/*     */     //   #586	-> 508
/*     */     //   #587	-> 513
/*     */     //   #588	-> 516
/*     */     //   #590	-> 519
/*     */     //   #591	-> 522
/*     */     //   #593	-> 536
/*     */     //   #594	-> 539
/*     */     //   #595	-> 545
/*     */     //   #596	-> 564
/*     */     //   #597	-> 576
/*     */     //   #599	-> 583
/*     */     //   #600	-> 585
/*     */     //   #601	-> 595
/*     */     //   #603	-> 617
/*     */     //   #605	-> 638
/*     */     //   #607	-> 640
/*     */     //   #620	-> 649
/*     */     //   #608	-> 652
/*     */     //   #610	-> 654
/*     */     //   #611	-> 663
/*     */     //   #612	-> 666
/*     */     //   #614	-> 668
/*     */     //   #615	-> 677
/*     */     //   #616	-> 687
/*     */     //   #618	-> 689
/*     */     //   #619	-> 698
/*     */     //   #621	-> 708
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   176	21	8	key	Ljava/lang/String;
/*     */     //   151	49	4	equalpos	I
/*     */     //   244	49	8	key	Ljava/lang/String;
/*     */     //   219	77	5	ampersandpos	I
/*     */     //   315	24	5	ampersandpos	I
/*     */     //   402	29	5	ampersandpos	I
/*     */     //   479	29	5	ampersandpos	I
/*     */     //   91	428	7	read	C
/*     */     //   539	56	5	ampersandpos	I
/*     */     //   654	12	7	e	Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$ErrorDataDecoderException;
/*     */     //   668	19	7	e	Ljava/io/IOException;
/*     */     //   689	19	7	e	Ljava/lang/IllegalArgumentException;
/*     */     //   0	709	0	this	Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostStandardRequestDecoder;
/*     */     //   27	682	1	sao	Lpro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostBodyUtil$SeekAheadOptimize;
/*     */     //   35	674	2	firstpos	I
/*     */     //   37	672	3	currentpos	I
/*     */     //   57	652	6	contRead	Z
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   57	649	652	pro/gravit/repackage/io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$ErrorDataDecoderException
/*     */     //   57	649	666	java/io/IOException
/*     */     //   57	649	687	java/lang/IllegalArgumentException
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
/*     */   private void setFinalBuffer(ByteBuf buffer) throws IOException {
/* 624 */     this.currentAttribute.addContent(buffer, true);
/* 625 */     String value = decodeAttribute(this.currentAttribute.getByteBuf().toString(this.charset), this.charset);
/* 626 */     this.currentAttribute.setValue(value);
/* 627 */     addHttpData(this.currentAttribute);
/* 628 */     this.currentAttribute = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String decodeAttribute(String s, Charset charset) {
/*     */     try {
/* 638 */       return QueryStringDecoder.decodeComponent(s, charset);
/* 639 */     } catch (IllegalArgumentException e) {
/* 640 */       throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad string: '" + s + '\'', e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void destroy() {
/* 651 */     cleanFiles();
/*     */     
/* 653 */     this.destroyed = true;
/*     */     
/* 655 */     if (this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
/* 656 */       this.undecodedChunk.release();
/* 657 */       this.undecodedChunk = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cleanFiles() {
/* 666 */     checkDestroyed();
/*     */     
/* 668 */     this.factory.cleanRequestHttpData(this.request);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeHttpDataFromClean(InterfaceHttpData data) {
/* 676 */     checkDestroyed();
/*     */     
/* 678 */     this.factory.removeHttpDataFromClean(this.request, data);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\HttpPostStandardRequestDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */