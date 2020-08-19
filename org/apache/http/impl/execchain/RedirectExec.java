/*     */ package org.apache.http.impl.execchain;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.HttpException;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.auth.AuthState;
/*     */ import org.apache.http.client.RedirectException;
/*     */ import org.apache.http.client.RedirectStrategy;
/*     */ import org.apache.http.client.config.RequestConfig;
/*     */ import org.apache.http.client.methods.CloseableHttpResponse;
/*     */ import org.apache.http.client.methods.HttpExecutionAware;
/*     */ import org.apache.http.client.methods.HttpRequestWrapper;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.client.protocol.HttpClientContext;
/*     */ import org.apache.http.client.utils.URIUtils;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.conn.routing.HttpRoutePlanner;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.EntityUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
/*     */ public class RedirectExec
/*     */   implements ClientExecChain
/*     */ {
/*  71 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */   
/*     */   private final ClientExecChain requestExecutor;
/*     */   
/*     */   private final RedirectStrategy redirectStrategy;
/*     */   
/*     */   private final HttpRoutePlanner routePlanner;
/*     */ 
/*     */   
/*     */   public RedirectExec(ClientExecChain requestExecutor, HttpRoutePlanner routePlanner, RedirectStrategy redirectStrategy) {
/*  82 */     Args.notNull(requestExecutor, "HTTP client request executor");
/*  83 */     Args.notNull(routePlanner, "HTTP route planner");
/*  84 */     Args.notNull(redirectStrategy, "HTTP redirect strategy");
/*  85 */     this.requestExecutor = requestExecutor;
/*  86 */     this.routePlanner = routePlanner;
/*  87 */     this.redirectStrategy = redirectStrategy;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
/*     */     CloseableHttpResponse response;
/*  96 */     Args.notNull(route, "HTTP route");
/*  97 */     Args.notNull(request, "HTTP request");
/*  98 */     Args.notNull(context, "HTTP context");
/*     */     
/* 100 */     List<URI> redirectLocations = context.getRedirectLocations();
/* 101 */     if (redirectLocations != null) {
/* 102 */       redirectLocations.clear();
/*     */     }
/*     */     
/* 105 */     RequestConfig config = context.getRequestConfig();
/* 106 */     int maxRedirects = (config.getMaxRedirects() > 0) ? config.getMaxRedirects() : 50;
/* 107 */     HttpRoute currentRoute = route;
/* 108 */     HttpRequestWrapper currentRequest = request;
/* 109 */     int redirectCount = 0; while (true) {
/* 110 */       response = this.requestExecutor.execute(currentRoute, currentRequest, context, execAware);
/*     */       
/*     */       try {
/* 113 */         if (config.isRedirectsEnabled() && this.redirectStrategy.isRedirected(currentRequest.getOriginal(), (HttpResponse)response, (HttpContext)context)) {
/*     */ 
/*     */           
/* 116 */           if (redirectCount >= maxRedirects) {
/* 117 */             throw new RedirectException("Maximum redirects (" + maxRedirects + ") exceeded");
/*     */           }
/* 119 */           redirectCount++;
/*     */           
/* 121 */           HttpUriRequest httpUriRequest = this.redirectStrategy.getRedirect(currentRequest.getOriginal(), (HttpResponse)response, (HttpContext)context);
/*     */           
/* 123 */           if (!httpUriRequest.headerIterator().hasNext()) {
/* 124 */             HttpRequest original = request.getOriginal();
/* 125 */             httpUriRequest.setHeaders(original.getAllHeaders());
/*     */           } 
/* 127 */           currentRequest = HttpRequestWrapper.wrap((HttpRequest)httpUriRequest);
/*     */           
/* 129 */           if (currentRequest instanceof HttpEntityEnclosingRequest) {
/* 130 */             RequestEntityProxy.enhance((HttpEntityEnclosingRequest)currentRequest);
/*     */           }
/*     */           
/* 133 */           URI uri = currentRequest.getURI();
/* 134 */           HttpHost newTarget = URIUtils.extractHost(uri);
/* 135 */           if (newTarget == null) {
/* 136 */             throw new ProtocolException("Redirect URI does not specify a valid host name: " + uri);
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 141 */           if (!currentRoute.getTargetHost().equals(newTarget)) {
/* 142 */             AuthState targetAuthState = context.getTargetAuthState();
/* 143 */             if (targetAuthState != null) {
/* 144 */               this.log.debug("Resetting target auth state");
/* 145 */               targetAuthState.reset();
/*     */             } 
/* 147 */             AuthState proxyAuthState = context.getProxyAuthState();
/* 148 */             if (proxyAuthState != null && proxyAuthState.isConnectionBased()) {
/* 149 */               this.log.debug("Resetting proxy auth state");
/* 150 */               proxyAuthState.reset();
/*     */             } 
/*     */           } 
/*     */           
/* 154 */           currentRoute = this.routePlanner.determineRoute(newTarget, (HttpRequest)currentRequest, (HttpContext)context);
/* 155 */           if (this.log.isDebugEnabled()) {
/* 156 */             this.log.debug("Redirecting to '" + uri + "' via " + currentRoute);
/*     */           }
/* 158 */           EntityUtils.consume(response.getEntity());
/* 159 */           response.close(); continue;
/*     */         } 
/* 161 */         return response;
/*     */       }
/* 163 */       catch (RuntimeException runtimeException) {
/* 164 */         response.close();
/* 165 */         throw runtimeException;
/* 166 */       } catch (IOException iOException) {
/* 167 */         response.close();
/* 168 */         throw iOException;
/* 169 */       } catch (HttpException ex) {
/*     */         break;
/*     */       } 
/*     */     }  try {
/* 173 */       EntityUtils.consume(response.getEntity());
/* 174 */     } catch (IOException ioex) {
/* 175 */       this.log.debug("I/O error while releasing connection", ioex);
/*     */     } finally {
/* 177 */       response.close();
/*     */     } 
/* 179 */     throw ex;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\impl\execchain\RedirectExec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */