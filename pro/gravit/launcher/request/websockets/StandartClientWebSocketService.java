/*     */ package pro.gravit.launcher.request.websockets;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import javax.net.ssl.SSLException;
/*     */ import pro.gravit.launcher.events.ExceptionEvent;
/*     */ import pro.gravit.launcher.events.request.ErrorRequestEvent;
/*     */ import pro.gravit.launcher.request.Request;
/*     */ import pro.gravit.launcher.request.RequestException;
/*     */ import pro.gravit.launcher.request.WebSocketEvent;
/*     */ import pro.gravit.utils.helper.JVMHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ 
/*     */ public class StandartClientWebSocketService
/*     */   extends ClientWebSocketService {
/*  19 */   public WaitEventHandler waitEventHandler = new WaitEventHandler();
/*     */   
/*     */   public StandartClientWebSocketService(String address) throws SSLException {
/*  22 */     super(address);
/*     */   }
/*     */   
/*     */   public class RequestFuture
/*     */     implements Future<WebSocketEvent>
/*     */   {
/*     */     public final WaitEventHandler.ResultEvent event;
/*     */     
/*     */     public RequestFuture(WebSocketRequest request) throws IOException {
/*  31 */       this.event = new WaitEventHandler.ResultEvent();
/*  32 */       this.event.type = request.getType();
/*  33 */       if (request instanceof Request) {
/*  34 */         this.event.uuid = ((Request)request).requestUUID;
/*     */       }
/*  36 */       StandartClientWebSocketService.this.waitEventHandler.requests.add(this.event);
/*  37 */       StandartClientWebSocketService.this.sendObject(request);
/*     */     }
/*     */     public boolean isCanceled = false;
/*     */     
/*     */     public boolean cancel(boolean mayInterruptIfRunning) {
/*  42 */       StandartClientWebSocketService.this.waitEventHandler.requests.remove(this.event);
/*  43 */       this.isCanceled = true;
/*  44 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isCancelled() {
/*  49 */       return this.isCanceled;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isDone() {
/*  54 */       return this.event.ready;
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketEvent get() throws InterruptedException, ExecutionException {
/*  59 */       if (this.isCanceled) return null; 
/*  60 */       synchronized (this.event) {
/*  61 */         while (!this.event.ready) {
/*  62 */           this.event.wait();
/*     */         }
/*     */       } 
/*  65 */       WebSocketEvent result = this.event.result;
/*  66 */       StandartClientWebSocketService.this.waitEventHandler.requests.remove(this.event);
/*  67 */       if (this.event.result.getType().equals("error") || this.event.result.getType().equals("exception")) {
/*  68 */         ErrorRequestEvent errorRequestEvent = (ErrorRequestEvent)this.event.result;
/*  69 */         throw new ExecutionException(new RequestException(errorRequestEvent.error));
/*     */       } 
/*  71 */       if (this.event.result.getType().equals("exception")) {
/*  72 */         ExceptionEvent error = (ExceptionEvent)this.event.result;
/*  73 */         throw new ExecutionException(new RequestException(String.format("LaunchServer fatal error: %s: %s", new Object[] { error.clazz, error.message })));
/*     */       } 
/*  75 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public WebSocketEvent get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException {
/*  80 */       if (this.isCanceled) return null; 
/*  81 */       synchronized (this.event) {
/*  82 */         while (!this.event.ready) {
/*  83 */           this.event.wait(timeout);
/*     */         }
/*     */       } 
/*  86 */       WebSocketEvent result = this.event.result;
/*  87 */       StandartClientWebSocketService.this.waitEventHandler.requests.remove(this.event);
/*  88 */       if (this.event.result.getType().equals("error")) {
/*  89 */         ErrorRequestEvent errorRequestEvent = (ErrorRequestEvent)this.event.result;
/*  90 */         throw new ExecutionException(new RequestException(errorRequestEvent.error));
/*     */       } 
/*  92 */       if (this.event.result.getType().equals("exception")) {
/*  93 */         ExceptionEvent error = (ExceptionEvent)this.event.result;
/*  94 */         throw new ExecutionException(new RequestException(String.format("LaunchServer fatal error: %s: %s", new Object[] { error.clazz, error.message })));
/*     */       } 
/*  96 */       return result;
/*     */     } }
/*     */   
/*     */   public WebSocketEvent sendRequest(WebSocketRequest request) throws IOException, InterruptedException {
/*     */     WebSocketEvent result;
/* 101 */     RequestFuture future = new RequestFuture(request);
/*     */     
/*     */     try {
/* 104 */       result = future.get();
/* 105 */     } catch (ExecutionException e) {
/* 106 */       throw (RequestException)e.getCause();
/*     */     } 
/* 108 */     return result;
/*     */   }
/*     */   
/*     */   public RequestFuture asyncSendRequest(WebSocketRequest request) throws IOException {
/* 112 */     return new RequestFuture(request);
/*     */   }
/*     */   
/*     */   public static StandartClientWebSocketService initWebSockets(String address, boolean async) {
/*     */     StandartClientWebSocketService service;
/*     */     try {
/* 118 */       service = new StandartClientWebSocketService(address);
/* 119 */     } catch (SSLException e) {
/* 120 */       throw new SecurityException(e);
/*     */     } 
/* 122 */     service.registerResults();
/* 123 */     service.registerRequests();
/* 124 */     service.registerHandler(service.waitEventHandler);
/* 125 */     if (!async) {
/*     */       try {
/* 127 */         service.open();
/* 128 */         LogHelper.debug("Connect to %s", new Object[] { address });
/* 129 */       } catch (Exception e) {
/* 130 */         e.printStackTrace();
/*     */       } 
/*     */     } else {
/*     */       try {
/* 134 */         service.open();
/* 135 */       } catch (Exception e) {
/* 136 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/* 139 */     JVMHelper.RUNTIME.addShutdownHook(new Thread(() -> {
/*     */ 
/*     */             
/*     */             try {
/*     */               service.close();
/* 144 */             } catch (InterruptedException e) {
/*     */               LogHelper.error(e);
/*     */             } 
/*     */           }));
/* 148 */     return service;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\websockets\StandartClientWebSocketService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */