/*     */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*     */ 
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.ByteToMessageDecoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.DecoderResult;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.PrematureChannelClosureException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.TooLongFrameException;
/*     */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*     */ import pro.gravit.repackage.io.netty.util.internal.AppendableCharSequence;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class HttpObjectDecoder
/*     */   extends ByteToMessageDecoder
/*     */ {
/*     */   private static final String EMPTY_VALUE = "";
/*     */   private final int maxChunkSize;
/*     */   private final boolean chunkedSupported;
/*     */   protected final boolean validateHeaders;
/*     */   private final HeaderParser headerParser;
/*     */   private final LineParser lineParser;
/*     */   private HttpMessage message;
/*     */   private long chunkSize;
/* 115 */   private long contentLength = Long.MIN_VALUE;
/*     */ 
/*     */   
/*     */   private volatile boolean resetRequested;
/*     */   
/*     */   private CharSequence name;
/*     */   
/*     */   private CharSequence value;
/*     */   
/*     */   private LastHttpContent trailer;
/*     */ 
/*     */   
/*     */   private enum State
/*     */   {
/* 129 */     SKIP_CONTROL_CHARS,
/* 130 */     READ_INITIAL,
/* 131 */     READ_HEADER,
/* 132 */     READ_VARIABLE_LENGTH_CONTENT,
/* 133 */     READ_FIXED_LENGTH_CONTENT,
/* 134 */     READ_CHUNK_SIZE,
/* 135 */     READ_CHUNKED_CONTENT,
/* 136 */     READ_CHUNK_DELIMITER,
/* 137 */     READ_CHUNK_FOOTER,
/* 138 */     BAD_MESSAGE,
/* 139 */     UPGRADED;
/*     */   }
/*     */   
/* 142 */   private State currentState = State.SKIP_CONTROL_CHARS;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpObjectDecoder() {
/* 150 */     this(4096, 8192, 8192, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported) {
/* 158 */     this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders) {
/* 167 */     this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, validateHeaders, 128);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders, int initialBufferSize) {
/* 173 */     ObjectUtil.checkPositive(maxInitialLineLength, "maxInitialLineLength");
/* 174 */     ObjectUtil.checkPositive(maxHeaderSize, "maxHeaderSize");
/* 175 */     ObjectUtil.checkPositive(maxChunkSize, "maxChunkSize");
/*     */     
/* 177 */     AppendableCharSequence seq = new AppendableCharSequence(initialBufferSize);
/* 178 */     this.lineParser = new LineParser(seq, maxInitialLineLength);
/* 179 */     this.headerParser = new HeaderParser(seq, maxHeaderSize);
/* 180 */     this.maxChunkSize = maxChunkSize;
/* 181 */     this.chunkedSupported = chunkedSupported;
/* 182 */     this.validateHeaders = validateHeaders; } protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception { int i; int readLimit; int toRead; int wIdx; int readableBytes;
/*     */     int j;
/*     */     HttpContent chunk;
/*     */     int rIdx;
/*     */     ByteBuf content;
/* 187 */     if (this.resetRequested) {
/* 188 */       resetNow();
/*     */     }
/*     */     
/* 191 */     switch (this.currentState) {
/*     */       case SKIP_CONTROL_CHARS:
/* 193 */         if (!skipControlCharacters(buffer)) {
/*     */           return;
/*     */         }
/* 196 */         this.currentState = State.READ_INITIAL;
/*     */       case READ_INITIAL:
/*     */         try {
/* 199 */           AppendableCharSequence line = this.lineParser.parse(buffer);
/* 200 */           if (line == null) {
/*     */             return;
/*     */           }
/* 203 */           String[] initialLine = splitInitialLine(line);
/* 204 */           if (initialLine.length < 3) {
/*     */             
/* 206 */             this.currentState = State.SKIP_CONTROL_CHARS;
/*     */             
/*     */             return;
/*     */           } 
/* 210 */           this.message = createMessage(initialLine);
/* 211 */           this.currentState = State.READ_HEADER;
/*     */         }
/* 213 */         catch (Exception e) {
/* 214 */           out.add(invalidMessage(buffer, e)); return;
/*     */         } 
/*     */       case READ_HEADER:
/*     */         try {
/* 218 */           State nextState = readHeaders(buffer);
/* 219 */           if (nextState == null) {
/*     */             return;
/*     */           }
/* 222 */           this.currentState = nextState;
/* 223 */           switch (nextState) {
/*     */ 
/*     */             
/*     */             case SKIP_CONTROL_CHARS:
/* 227 */               out.add(this.message);
/* 228 */               out.add(LastHttpContent.EMPTY_LAST_CONTENT);
/* 229 */               resetNow();
/*     */               return;
/*     */             case READ_CHUNK_SIZE:
/* 232 */               if (!this.chunkedSupported) {
/* 233 */                 throw new IllegalArgumentException("Chunked messages not supported");
/*     */               }
/*     */               
/* 236 */               out.add(this.message);
/*     */               return;
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 245 */           long contentLength = contentLength();
/* 246 */           if (contentLength == 0L || (contentLength == -1L && isDecodingRequest())) {
/* 247 */             out.add(this.message);
/* 248 */             out.add(LastHttpContent.EMPTY_LAST_CONTENT);
/* 249 */             resetNow();
/*     */             
/*     */             return;
/*     */           } 
/* 253 */           assert nextState == State.READ_FIXED_LENGTH_CONTENT || nextState == State.READ_VARIABLE_LENGTH_CONTENT;
/*     */ 
/*     */           
/* 256 */           out.add(this.message);
/*     */           
/* 258 */           if (nextState == State.READ_FIXED_LENGTH_CONTENT)
/*     */           {
/* 260 */             this.chunkSize = contentLength;
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           return;
/* 266 */         } catch (Exception e) {
/* 267 */           out.add(invalidMessage(buffer, e));
/*     */           return;
/*     */         } 
/*     */       
/*     */       case READ_VARIABLE_LENGTH_CONTENT:
/* 272 */         i = Math.min(buffer.readableBytes(), this.maxChunkSize);
/* 273 */         if (i > 0) {
/* 274 */           ByteBuf byteBuf = buffer.readRetainedSlice(i);
/* 275 */           out.add(new DefaultHttpContent(byteBuf));
/*     */         } 
/*     */         return;
/*     */       
/*     */       case READ_FIXED_LENGTH_CONTENT:
/* 280 */         readLimit = buffer.readableBytes();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 288 */         if (readLimit == 0) {
/*     */           return;
/*     */         }
/*     */         
/* 292 */         j = Math.min(readLimit, this.maxChunkSize);
/* 293 */         if (j > this.chunkSize) {
/* 294 */           j = (int)this.chunkSize;
/*     */         }
/* 296 */         content = buffer.readRetainedSlice(j);
/* 297 */         this.chunkSize -= j;
/*     */         
/* 299 */         if (this.chunkSize == 0L) {
/*     */           
/* 301 */           out.add(new DefaultLastHttpContent(content, this.validateHeaders));
/* 302 */           resetNow();
/*     */         } else {
/* 304 */           out.add(new DefaultHttpContent(content));
/*     */         } 
/*     */         return;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case READ_CHUNK_SIZE:
/*     */         try {
/* 313 */           AppendableCharSequence line = this.lineParser.parse(buffer);
/* 314 */           if (line == null) {
/*     */             return;
/*     */           }
/* 317 */           int chunkSize = getChunkSize(line.toString());
/* 318 */           this.chunkSize = chunkSize;
/* 319 */           if (chunkSize == 0) {
/* 320 */             this.currentState = State.READ_CHUNK_FOOTER;
/*     */             return;
/*     */           } 
/* 323 */           this.currentState = State.READ_CHUNKED_CONTENT;
/*     */         }
/* 325 */         catch (Exception e) {
/* 326 */           out.add(invalidChunk(buffer, e));
/*     */           return;
/*     */         } 
/*     */       case READ_CHUNKED_CONTENT:
/* 330 */         assert this.chunkSize <= 2147483647L;
/* 331 */         toRead = Math.min((int)this.chunkSize, this.maxChunkSize);
/* 332 */         toRead = Math.min(toRead, buffer.readableBytes());
/* 333 */         if (toRead == 0) {
/*     */           return;
/*     */         }
/* 336 */         chunk = new DefaultHttpContent(buffer.readRetainedSlice(toRead));
/* 337 */         this.chunkSize -= toRead;
/*     */         
/* 339 */         out.add(chunk);
/*     */         
/* 341 */         if (this.chunkSize != 0L) {
/*     */           return;
/*     */         }
/* 344 */         this.currentState = State.READ_CHUNK_DELIMITER;
/*     */ 
/*     */       
/*     */       case READ_CHUNK_DELIMITER:
/* 348 */         wIdx = buffer.writerIndex();
/* 349 */         rIdx = buffer.readerIndex();
/* 350 */         while (wIdx > rIdx) {
/* 351 */           byte next = buffer.getByte(rIdx++);
/* 352 */           if (next == 10) {
/* 353 */             this.currentState = State.READ_CHUNK_SIZE;
/*     */             break;
/*     */           } 
/*     */         } 
/* 357 */         buffer.readerIndex(rIdx);
/*     */         return;
/*     */       case READ_CHUNK_FOOTER:
/*     */         try {
/* 361 */           LastHttpContent trailer = readTrailingHeaders(buffer);
/* 362 */           if (trailer == null) {
/*     */             return;
/*     */           }
/* 365 */           out.add(trailer);
/* 366 */           resetNow();
/*     */           return;
/* 368 */         } catch (Exception e) {
/* 369 */           out.add(invalidChunk(buffer, e));
/*     */           return;
/*     */         } 
/*     */       
/*     */       case BAD_MESSAGE:
/* 374 */         buffer.skipBytes(buffer.readableBytes());
/*     */         break;
/*     */       
/*     */       case UPGRADED:
/* 378 */         readableBytes = buffer.readableBytes();
/* 379 */         if (readableBytes > 0)
/*     */         {
/*     */ 
/*     */ 
/*     */           
/* 384 */           out.add(buffer.readBytes(readableBytes));
/*     */         }
/*     */         break;
/*     */     }  }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
/* 393 */     super.decodeLast(ctx, in, out);
/*     */     
/* 395 */     if (this.resetRequested)
/*     */     {
/*     */       
/* 398 */       resetNow();
/*     */     }
/*     */     
/* 401 */     if (this.message != null) {
/* 402 */       boolean prematureClosure, chunked = HttpUtil.isTransferEncodingChunked(this.message);
/* 403 */       if (this.currentState == State.READ_VARIABLE_LENGTH_CONTENT && !in.isReadable() && !chunked) {
/*     */         
/* 405 */         out.add(LastHttpContent.EMPTY_LAST_CONTENT);
/* 406 */         resetNow();
/*     */         
/*     */         return;
/*     */       } 
/* 410 */       if (this.currentState == State.READ_HEADER) {
/*     */ 
/*     */         
/* 413 */         out.add(invalidMessage(Unpooled.EMPTY_BUFFER, (Exception)new PrematureChannelClosureException("Connection closed before received headers")));
/*     */         
/* 415 */         resetNow();
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 421 */       if (isDecodingRequest() || chunked) {
/*     */         
/* 423 */         prematureClosure = true;
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 428 */         prematureClosure = (contentLength() > 0L);
/*     */       } 
/*     */       
/* 431 */       if (!prematureClosure) {
/* 432 */         out.add(LastHttpContent.EMPTY_LAST_CONTENT);
/*     */       }
/* 434 */       resetNow();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
/* 440 */     if (evt instanceof HttpExpectationFailedEvent) {
/* 441 */       switch (this.currentState) {
/*     */         case READ_CHUNK_SIZE:
/*     */         case READ_VARIABLE_LENGTH_CONTENT:
/*     */         case READ_FIXED_LENGTH_CONTENT:
/* 445 */           reset();
/*     */           break;
/*     */       } 
/*     */ 
/*     */     
/*     */     }
/* 451 */     super.userEventTriggered(ctx, evt);
/*     */   }
/*     */   
/*     */   protected boolean isContentAlwaysEmpty(HttpMessage msg) {
/* 455 */     if (msg instanceof HttpResponse) {
/* 456 */       HttpResponse res = (HttpResponse)msg;
/* 457 */       int code = res.status().code();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 464 */       if (code >= 100 && code < 200)
/*     */       {
/* 466 */         return (code != 101 || res.headers().contains((CharSequence)HttpHeaderNames.SEC_WEBSOCKET_ACCEPT) || 
/* 467 */           !res.headers().contains((CharSequence)HttpHeaderNames.UPGRADE, (CharSequence)HttpHeaderValues.WEBSOCKET, true));
/*     */       }
/*     */       
/* 470 */       switch (code) { case 204:
/*     */         case 304:
/* 472 */           return true; }
/*     */     
/*     */     } 
/* 475 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isSwitchingToNonHttp1Protocol(HttpResponse msg) {
/* 483 */     if (msg.status().code() != HttpResponseStatus.SWITCHING_PROTOCOLS.code()) {
/* 484 */       return false;
/*     */     }
/* 486 */     String newProtocol = msg.headers().get((CharSequence)HttpHeaderNames.UPGRADE);
/* 487 */     return (newProtocol == null || (
/* 488 */       !newProtocol.contains(HttpVersion.HTTP_1_0.text()) && 
/* 489 */       !newProtocol.contains(HttpVersion.HTTP_1_1.text())));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 497 */     this.resetRequested = true;
/*     */   }
/*     */   
/*     */   private void resetNow() {
/* 501 */     HttpMessage message = this.message;
/* 502 */     this.message = null;
/* 503 */     this.name = null;
/* 504 */     this.value = null;
/* 505 */     this.contentLength = Long.MIN_VALUE;
/* 506 */     this.lineParser.reset();
/* 507 */     this.headerParser.reset();
/* 508 */     this.trailer = null;
/* 509 */     if (!isDecodingRequest()) {
/* 510 */       HttpResponse res = (HttpResponse)message;
/* 511 */       if (res != null && isSwitchingToNonHttp1Protocol(res)) {
/* 512 */         this.currentState = State.UPGRADED;
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 517 */     this.resetRequested = false;
/* 518 */     this.currentState = State.SKIP_CONTROL_CHARS;
/*     */   }
/*     */   
/*     */   private HttpMessage invalidMessage(ByteBuf in, Exception cause) {
/* 522 */     this.currentState = State.BAD_MESSAGE;
/*     */ 
/*     */ 
/*     */     
/* 526 */     in.skipBytes(in.readableBytes());
/*     */     
/* 528 */     if (this.message == null) {
/* 529 */       this.message = createInvalidMessage();
/*     */     }
/* 531 */     this.message.setDecoderResult(DecoderResult.failure(cause));
/*     */     
/* 533 */     HttpMessage ret = this.message;
/* 534 */     this.message = null;
/* 535 */     return ret;
/*     */   }
/*     */   
/*     */   private HttpContent invalidChunk(ByteBuf in, Exception cause) {
/* 539 */     this.currentState = State.BAD_MESSAGE;
/*     */ 
/*     */ 
/*     */     
/* 543 */     in.skipBytes(in.readableBytes());
/*     */     
/* 545 */     HttpContent chunk = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
/* 546 */     chunk.setDecoderResult(DecoderResult.failure(cause));
/* 547 */     this.message = null;
/* 548 */     this.trailer = null;
/* 549 */     return chunk;
/*     */   }
/*     */   
/*     */   private static boolean skipControlCharacters(ByteBuf buffer) {
/* 553 */     boolean skiped = false;
/* 554 */     int wIdx = buffer.writerIndex();
/* 555 */     int rIdx = buffer.readerIndex();
/* 556 */     while (wIdx > rIdx) {
/* 557 */       int c = buffer.getUnsignedByte(rIdx++);
/* 558 */       if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
/* 559 */         rIdx--;
/* 560 */         skiped = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 564 */     buffer.readerIndex(rIdx);
/* 565 */     return skiped;
/*     */   }
/*     */   
/*     */   private State readHeaders(ByteBuf buffer) {
/* 569 */     HttpMessage message = this.message;
/* 570 */     HttpHeaders headers = message.headers();
/*     */     
/* 572 */     AppendableCharSequence line = this.headerParser.parse(buffer);
/* 573 */     if (line == null) {
/* 574 */       return null;
/*     */     }
/* 576 */     if (line.length() > 0) {
/*     */       do {
/* 578 */         char firstChar = line.charAtUnsafe(0);
/* 579 */         if (this.name != null && (firstChar == ' ' || firstChar == '\t')) {
/*     */ 
/*     */           
/* 582 */           String trimmedLine = line.toString().trim();
/* 583 */           String valueStr = String.valueOf(this.value);
/* 584 */           this.value = valueStr + ' ' + trimmedLine;
/*     */         } else {
/* 586 */           if (this.name != null) {
/* 587 */             headers.add(this.name, this.value);
/*     */           }
/* 589 */           splitHeader(line);
/*     */         } 
/*     */         
/* 592 */         line = this.headerParser.parse(buffer);
/* 593 */         if (line == null) {
/* 594 */           return null;
/*     */         }
/* 596 */       } while (line.length() > 0);
/*     */     }
/*     */ 
/*     */     
/* 600 */     if (this.name != null) {
/* 601 */       headers.add(this.name, this.value);
/*     */     }
/*     */ 
/*     */     
/* 605 */     this.name = null;
/* 606 */     this.value = null;
/*     */     
/* 608 */     List<String> values = headers.getAll((CharSequence)HttpHeaderNames.CONTENT_LENGTH);
/* 609 */     int contentLengthValuesCount = values.size();
/*     */     
/* 611 */     if (contentLengthValuesCount > 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 625 */       if (contentLengthValuesCount > 1 && message.protocolVersion() == HttpVersion.HTTP_1_1) {
/* 626 */         throw new IllegalArgumentException("Multiple Content-Length headers found");
/*     */       }
/* 628 */       this.contentLength = Long.parseLong(values.get(0));
/*     */     } 
/*     */     
/* 631 */     if (isContentAlwaysEmpty(message)) {
/* 632 */       HttpUtil.setTransferEncodingChunked(message, false);
/* 633 */       return State.SKIP_CONTROL_CHARS;
/* 634 */     }  if (HttpUtil.isTransferEncodingChunked(message)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 647 */       if (contentLengthValuesCount > 0 && message.protocolVersion() == HttpVersion.HTTP_1_1) {
/* 648 */         throw new IllegalArgumentException("Both 'Content-Length: " + this.contentLength + "' and 'Transfer-Encoding: chunked' found");
/*     */       }
/*     */ 
/*     */       
/* 652 */       return State.READ_CHUNK_SIZE;
/* 653 */     }  if (contentLength() >= 0L) {
/* 654 */       return State.READ_FIXED_LENGTH_CONTENT;
/*     */     }
/* 656 */     return State.READ_VARIABLE_LENGTH_CONTENT;
/*     */   }
/*     */ 
/*     */   
/*     */   private long contentLength() {
/* 661 */     if (this.contentLength == Long.MIN_VALUE) {
/* 662 */       this.contentLength = HttpUtil.getContentLength(this.message, -1L);
/*     */     }
/* 664 */     return this.contentLength;
/*     */   }
/*     */   
/*     */   private LastHttpContent readTrailingHeaders(ByteBuf buffer) {
/* 668 */     AppendableCharSequence line = this.headerParser.parse(buffer);
/* 669 */     if (line == null) {
/* 670 */       return null;
/*     */     }
/* 672 */     LastHttpContent trailer = this.trailer;
/* 673 */     if (line.length() == 0 && trailer == null)
/*     */     {
/*     */       
/* 676 */       return LastHttpContent.EMPTY_LAST_CONTENT;
/*     */     }
/*     */     
/* 679 */     CharSequence lastHeader = null;
/* 680 */     if (trailer == null) {
/* 681 */       trailer = this.trailer = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders);
/*     */     }
/* 683 */     while (line.length() > 0) {
/* 684 */       char firstChar = line.charAtUnsafe(0);
/* 685 */       if (lastHeader != null && (firstChar == ' ' || firstChar == '\t')) {
/* 686 */         List<String> current = trailer.trailingHeaders().getAll(lastHeader);
/* 687 */         if (!current.isEmpty()) {
/* 688 */           int lastPos = current.size() - 1;
/*     */ 
/*     */           
/* 691 */           String lineTrimmed = line.toString().trim();
/* 692 */           String currentLastPos = current.get(lastPos);
/* 693 */           current.set(lastPos, currentLastPos + lineTrimmed);
/*     */         } 
/*     */       } else {
/* 696 */         splitHeader(line);
/* 697 */         CharSequence headerName = this.name;
/* 698 */         if (!HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(headerName) && 
/* 699 */           !HttpHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase(headerName) && 
/* 700 */           !HttpHeaderNames.TRAILER.contentEqualsIgnoreCase(headerName)) {
/* 701 */           trailer.trailingHeaders().add(headerName, this.value);
/*     */         }
/* 703 */         lastHeader = this.name;
/*     */         
/* 705 */         this.name = null;
/* 706 */         this.value = null;
/*     */       } 
/* 708 */       line = this.headerParser.parse(buffer);
/* 709 */       if (line == null) {
/* 710 */         return null;
/*     */       }
/*     */     } 
/*     */     
/* 714 */     this.trailer = null;
/* 715 */     return trailer;
/*     */   }
/*     */   protected abstract boolean isDecodingRequest();
/*     */   protected abstract HttpMessage createMessage(String[] paramArrayOfString) throws Exception;
/*     */   
/*     */   protected abstract HttpMessage createInvalidMessage();
/*     */   
/*     */   private static int getChunkSize(String hex) {
/* 723 */     hex = hex.trim();
/* 724 */     for (int i = 0; i < hex.length(); i++) {
/* 725 */       char c = hex.charAt(i);
/* 726 */       if (c == ';' || Character.isWhitespace(c) || Character.isISOControl(c)) {
/* 727 */         hex = hex.substring(0, i);
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 732 */     return Integer.parseInt(hex, 16);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String[] splitInitialLine(AppendableCharSequence sb) {
/* 743 */     int aStart = findNonWhitespace(sb, 0);
/* 744 */     int aEnd = findWhitespace(sb, aStart);
/*     */     
/* 746 */     int bStart = findNonWhitespace(sb, aEnd);
/* 747 */     int bEnd = findWhitespace(sb, bStart);
/*     */     
/* 749 */     int cStart = findNonWhitespace(sb, bEnd);
/* 750 */     int cEnd = findEndOfString(sb);
/*     */     
/* 752 */     return new String[] { sb
/* 753 */         .subStringUnsafe(aStart, aEnd), sb
/* 754 */         .subStringUnsafe(bStart, bEnd), (cStart < cEnd) ? sb
/* 755 */         .subStringUnsafe(cStart, cEnd) : "" };
/*     */   }
/*     */   
/*     */   private void splitHeader(AppendableCharSequence sb) {
/* 759 */     int length = sb.length();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 766 */     int nameStart = findNonWhitespace(sb, 0); int nameEnd;
/* 767 */     for (nameEnd = nameStart; nameEnd < length; nameEnd++) {
/* 768 */       char ch = sb.charAtUnsafe(nameEnd);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 778 */       if (ch == ':' || (
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 783 */         !isDecodingRequest() && Character.isWhitespace(ch))) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */     
/* 788 */     if (nameEnd == length)
/*     */     {
/* 790 */       throw new IllegalArgumentException("No colon found");
/*     */     }
/*     */     int colonEnd;
/* 793 */     for (colonEnd = nameEnd; colonEnd < length; colonEnd++) {
/* 794 */       if (sb.charAtUnsafe(colonEnd) == ':') {
/* 795 */         colonEnd++;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 800 */     this.name = sb.subStringUnsafe(nameStart, nameEnd);
/* 801 */     int valueStart = findNonWhitespace(sb, colonEnd);
/* 802 */     if (valueStart == length) {
/* 803 */       this.value = "";
/*     */     } else {
/* 805 */       int valueEnd = findEndOfString(sb);
/* 806 */       this.value = sb.subStringUnsafe(valueStart, valueEnd);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int findNonWhitespace(AppendableCharSequence sb, int offset) {
/* 811 */     for (int result = offset; result < sb.length(); result++) {
/* 812 */       if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
/* 813 */         return result;
/*     */       }
/*     */     } 
/* 816 */     return sb.length();
/*     */   }
/*     */   
/*     */   private static int findWhitespace(AppendableCharSequence sb, int offset) {
/* 820 */     for (int result = offset; result < sb.length(); result++) {
/* 821 */       if (Character.isWhitespace(sb.charAtUnsafe(result))) {
/* 822 */         return result;
/*     */       }
/*     */     } 
/* 825 */     return sb.length();
/*     */   }
/*     */   
/*     */   private static int findEndOfString(AppendableCharSequence sb) {
/* 829 */     for (int result = sb.length() - 1; result > 0; result--) {
/* 830 */       if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
/* 831 */         return result + 1;
/*     */       }
/*     */     } 
/* 834 */     return 0;
/*     */   }
/*     */   
/*     */   private static class HeaderParser implements ByteProcessor {
/*     */     private final AppendableCharSequence seq;
/*     */     private final int maxLength;
/*     */     private int size;
/*     */     
/*     */     HeaderParser(AppendableCharSequence seq, int maxLength) {
/* 843 */       this.seq = seq;
/* 844 */       this.maxLength = maxLength;
/*     */     }
/*     */     
/*     */     public AppendableCharSequence parse(ByteBuf buffer) {
/* 848 */       int oldSize = this.size;
/* 849 */       this.seq.reset();
/* 850 */       int i = buffer.forEachByte(this);
/* 851 */       if (i == -1) {
/* 852 */         this.size = oldSize;
/* 853 */         return null;
/*     */       } 
/* 855 */       buffer.readerIndex(i + 1);
/* 856 */       return this.seq;
/*     */     }
/*     */     
/*     */     public void reset() {
/* 860 */       this.size = 0;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean process(byte value) throws Exception {
/* 865 */       char nextByte = (char)(value & 0xFF);
/* 866 */       if (nextByte == '\r') {
/* 867 */         return true;
/*     */       }
/* 869 */       if (nextByte == '\n') {
/* 870 */         return false;
/*     */       }
/*     */       
/* 873 */       if (++this.size > this.maxLength)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 878 */         throw newException(this.maxLength);
/*     */       }
/*     */       
/* 881 */       this.seq.append(nextByte);
/* 882 */       return true;
/*     */     }
/*     */     
/*     */     protected TooLongFrameException newException(int maxLength) {
/* 886 */       return new TooLongFrameException("HTTP header is larger than " + maxLength + " bytes.");
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class LineParser
/*     */     extends HeaderParser {
/*     */     LineParser(AppendableCharSequence seq, int maxLength) {
/* 893 */       super(seq, maxLength);
/*     */     }
/*     */ 
/*     */     
/*     */     public AppendableCharSequence parse(ByteBuf buffer) {
/* 898 */       reset();
/* 899 */       return super.parse(buffer);
/*     */     }
/*     */ 
/*     */     
/*     */     protected TooLongFrameException newException(int maxLength) {
/* 904 */       return new TooLongFrameException("An HTTP line is larger than " + maxLength + " bytes.");
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\HttpObjectDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */