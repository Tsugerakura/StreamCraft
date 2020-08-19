/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.MessageToMessageEncoder;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.UnsupportedMessageTypeException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.FullHttpMessage;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpContent;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaderNames;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpHeaders;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpMessage;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpObject;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpResponse;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.LastHttpContent;
/*     */ import pro.gravit.repackage.io.netty.util.AsciiString;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SpdyHttpEncoder
/*     */   extends MessageToMessageEncoder<HttpObject>
/*     */ {
/*     */   private int currentStreamId;
/*     */   private final boolean validateHeaders;
/*     */   private final boolean headersToLowerCase;
/*     */   
/*     */   public SpdyHttpEncoder(SpdyVersion version) {
/* 136 */     this(version, true, true);
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
/*     */   public SpdyHttpEncoder(SpdyVersion version, boolean headersToLowerCase, boolean validateHeaders) {
/* 148 */     ObjectUtil.checkNotNull(version, "version");
/* 149 */     this.headersToLowerCase = headersToLowerCase;
/* 150 */     this.validateHeaders = validateHeaders;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void encode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
/* 156 */     boolean valid = false;
/* 157 */     boolean last = false;
/*     */     
/* 159 */     if (msg instanceof HttpRequest) {
/*     */       
/* 161 */       HttpRequest httpRequest = (HttpRequest)msg;
/* 162 */       SpdySynStreamFrame spdySynStreamFrame = createSynStreamFrame(httpRequest);
/* 163 */       out.add(spdySynStreamFrame);
/*     */       
/* 165 */       last = (spdySynStreamFrame.isLast() || spdySynStreamFrame.isUnidirectional());
/* 166 */       valid = true;
/*     */     } 
/* 168 */     if (msg instanceof HttpResponse) {
/*     */       
/* 170 */       HttpResponse httpResponse = (HttpResponse)msg;
/* 171 */       SpdyHeadersFrame spdyHeadersFrame = createHeadersFrame(httpResponse);
/* 172 */       out.add(spdyHeadersFrame);
/*     */       
/* 174 */       last = spdyHeadersFrame.isLast();
/* 175 */       valid = true;
/*     */     } 
/* 177 */     if (msg instanceof HttpContent && !last) {
/*     */       
/* 179 */       HttpContent chunk = (HttpContent)msg;
/*     */       
/* 181 */       chunk.content().retain();
/* 182 */       SpdyDataFrame spdyDataFrame = new DefaultSpdyDataFrame(this.currentStreamId, chunk.content());
/* 183 */       if (chunk instanceof LastHttpContent) {
/* 184 */         LastHttpContent trailer = (LastHttpContent)chunk;
/* 185 */         HttpHeaders trailers = trailer.trailingHeaders();
/* 186 */         if (trailers.isEmpty()) {
/* 187 */           spdyDataFrame.setLast(true);
/* 188 */           out.add(spdyDataFrame);
/*     */         } else {
/*     */           
/* 191 */           SpdyHeadersFrame spdyHeadersFrame = new DefaultSpdyHeadersFrame(this.currentStreamId, this.validateHeaders);
/* 192 */           spdyHeadersFrame.setLast(true);
/* 193 */           Iterator<Map.Entry<CharSequence, CharSequence>> itr = trailers.iteratorCharSequence();
/* 194 */           while (itr.hasNext()) {
/* 195 */             Map.Entry<CharSequence, CharSequence> entry = itr.next();
/*     */             
/* 197 */             CharSequence headerName = this.headersToLowerCase ? (CharSequence)AsciiString.of(entry.getKey()).toLowerCase() : entry.getKey();
/* 198 */             spdyHeadersFrame.headers().add(headerName, entry.getValue());
/*     */           } 
/*     */ 
/*     */           
/* 202 */           out.add(spdyDataFrame);
/* 203 */           out.add(spdyHeadersFrame);
/*     */         } 
/*     */       } else {
/* 206 */         out.add(spdyDataFrame);
/*     */       } 
/*     */       
/* 209 */       valid = true;
/*     */     } 
/*     */     
/* 212 */     if (!valid) {
/* 213 */       throw new UnsupportedMessageTypeException(msg, new Class[0]);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private SpdySynStreamFrame createSynStreamFrame(HttpRequest httpRequest) throws Exception {
/* 220 */     HttpHeaders httpHeaders = httpRequest.headers();
/* 221 */     int streamId = httpHeaders.getInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID).intValue();
/* 222 */     int associatedToStreamId = httpHeaders.getInt((CharSequence)SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID, 0);
/* 223 */     byte priority = (byte)httpHeaders.getInt((CharSequence)SpdyHttpHeaders.Names.PRIORITY, 0);
/* 224 */     CharSequence scheme = httpHeaders.get((CharSequence)SpdyHttpHeaders.Names.SCHEME);
/* 225 */     httpHeaders.remove((CharSequence)SpdyHttpHeaders.Names.STREAM_ID);
/* 226 */     httpHeaders.remove((CharSequence)SpdyHttpHeaders.Names.ASSOCIATED_TO_STREAM_ID);
/* 227 */     httpHeaders.remove((CharSequence)SpdyHttpHeaders.Names.PRIORITY);
/* 228 */     httpHeaders.remove((CharSequence)SpdyHttpHeaders.Names.SCHEME);
/*     */ 
/*     */ 
/*     */     
/* 232 */     httpHeaders.remove((CharSequence)HttpHeaderNames.CONNECTION);
/* 233 */     httpHeaders.remove("Keep-Alive");
/* 234 */     httpHeaders.remove("Proxy-Connection");
/* 235 */     httpHeaders.remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
/*     */     
/* 237 */     SpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame(streamId, associatedToStreamId, priority, this.validateHeaders);
/*     */ 
/*     */ 
/*     */     
/* 241 */     SpdyHeaders frameHeaders = spdySynStreamFrame.headers();
/* 242 */     frameHeaders.set(SpdyHeaders.HttpNames.METHOD, httpRequest.method().name());
/* 243 */     frameHeaders.set(SpdyHeaders.HttpNames.PATH, httpRequest.uri());
/* 244 */     frameHeaders.set(SpdyHeaders.HttpNames.VERSION, httpRequest.protocolVersion().text());
/*     */ 
/*     */     
/* 247 */     CharSequence host = httpHeaders.get((CharSequence)HttpHeaderNames.HOST);
/* 248 */     httpHeaders.remove((CharSequence)HttpHeaderNames.HOST);
/* 249 */     frameHeaders.set(SpdyHeaders.HttpNames.HOST, host);
/*     */ 
/*     */     
/* 252 */     if (scheme == null) {
/* 253 */       scheme = "https";
/*     */     }
/* 255 */     frameHeaders.set(SpdyHeaders.HttpNames.SCHEME, scheme);
/*     */ 
/*     */     
/* 258 */     Iterator<Map.Entry<CharSequence, CharSequence>> itr = httpHeaders.iteratorCharSequence();
/* 259 */     while (itr.hasNext()) {
/* 260 */       Map.Entry<CharSequence, CharSequence> entry = itr.next();
/*     */       
/* 262 */       CharSequence headerName = this.headersToLowerCase ? (CharSequence)AsciiString.of(entry.getKey()).toLowerCase() : entry.getKey();
/* 263 */       frameHeaders.add(headerName, entry.getValue());
/*     */     } 
/* 265 */     this.currentStreamId = spdySynStreamFrame.streamId();
/* 266 */     if (associatedToStreamId == 0) {
/* 267 */       spdySynStreamFrame.setLast(isLast((HttpMessage)httpRequest));
/*     */     } else {
/* 269 */       spdySynStreamFrame.setUnidirectional(true);
/*     */     } 
/*     */     
/* 272 */     return spdySynStreamFrame;
/*     */   }
/*     */ 
/*     */   
/*     */   private SpdyHeadersFrame createHeadersFrame(HttpResponse httpResponse) throws Exception {
/*     */     SpdyHeadersFrame spdyHeadersFrame;
/* 278 */     HttpHeaders httpHeaders = httpResponse.headers();
/* 279 */     int streamId = httpHeaders.getInt((CharSequence)SpdyHttpHeaders.Names.STREAM_ID).intValue();
/* 280 */     httpHeaders.remove((CharSequence)SpdyHttpHeaders.Names.STREAM_ID);
/*     */ 
/*     */ 
/*     */     
/* 284 */     httpHeaders.remove((CharSequence)HttpHeaderNames.CONNECTION);
/* 285 */     httpHeaders.remove("Keep-Alive");
/* 286 */     httpHeaders.remove("Proxy-Connection");
/* 287 */     httpHeaders.remove((CharSequence)HttpHeaderNames.TRANSFER_ENCODING);
/*     */ 
/*     */     
/* 290 */     if (SpdyCodecUtil.isServerId(streamId)) {
/* 291 */       spdyHeadersFrame = new DefaultSpdyHeadersFrame(streamId, this.validateHeaders);
/*     */     } else {
/* 293 */       spdyHeadersFrame = new DefaultSpdySynReplyFrame(streamId, this.validateHeaders);
/*     */     } 
/* 295 */     SpdyHeaders frameHeaders = spdyHeadersFrame.headers();
/*     */     
/* 297 */     frameHeaders.set(SpdyHeaders.HttpNames.STATUS, httpResponse.status().codeAsText());
/* 298 */     frameHeaders.set(SpdyHeaders.HttpNames.VERSION, httpResponse.protocolVersion().text());
/*     */ 
/*     */     
/* 301 */     Iterator<Map.Entry<CharSequence, CharSequence>> itr = httpHeaders.iteratorCharSequence();
/* 302 */     while (itr.hasNext()) {
/* 303 */       Map.Entry<CharSequence, CharSequence> entry = itr.next();
/*     */       
/* 305 */       CharSequence headerName = this.headersToLowerCase ? (CharSequence)AsciiString.of(entry.getKey()).toLowerCase() : entry.getKey();
/* 306 */       spdyHeadersFrame.headers().add(headerName, entry.getValue());
/*     */     } 
/*     */     
/* 309 */     this.currentStreamId = streamId;
/* 310 */     spdyHeadersFrame.setLast(isLast((HttpMessage)httpResponse));
/*     */     
/* 312 */     return spdyHeadersFrame;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isLast(HttpMessage httpMessage) {
/* 322 */     if (httpMessage instanceof FullHttpMessage) {
/* 323 */       FullHttpMessage fullMessage = (FullHttpMessage)httpMessage;
/* 324 */       if (fullMessage.trailingHeaders().isEmpty() && !fullMessage.content().isReadable()) {
/* 325 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 329 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyHttpEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */