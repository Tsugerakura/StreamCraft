/*    */ package pro.gravit.launcher.request.websockets;
/*    */ 
/*    */ import java.util.Set;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import pro.gravit.launcher.events.RequestEvent;
/*    */ import pro.gravit.launcher.request.WebSocketEvent;
/*    */ import pro.gravit.utils.helper.LogHelper;
/*    */ 
/*    */ public class WaitEventHandler
/*    */   implements ClientWebSocketService.EventHandler {
/* 12 */   public Set<ResultEvent> requests = ConcurrentHashMap.newKeySet();
/*    */ 
/*    */   
/*    */   public void process(WebSocketEvent result) {
/* 16 */     LogHelper.debug("Processing event %s type", new Object[] { result.getType() });
/* 17 */     UUID checkUUID = null;
/* 18 */     if (result instanceof RequestEvent) {
/* 19 */       RequestEvent event = (RequestEvent)result;
/* 20 */       checkUUID = event.requestUUID;
/* 21 */       if (checkUUID != null)
/* 22 */         LogHelper.debug("Event UUID: %s found", new Object[] { checkUUID.toString() }); 
/*    */     } 
/* 24 */     for (ResultEvent r : this.requests) {
/* 25 */       if (r.uuid != null)
/* 26 */         LogHelper.debug("Request UUID found: %s", new Object[] { r.uuid.toString() }); 
/* 27 */       if ((r.uuid != null && r.uuid.equals(checkUUID)) || (checkUUID == null && (r.type.equals(result.getType()) || result.getType().equals("error")))) {
/* 28 */         LogHelper.debug("Event %s type", new Object[] { r.type });
/* 29 */         synchronized (r) {
/* 30 */           r.result = result;
/* 31 */           r.ready = true;
/* 32 */           r.notifyAll();
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public static class ResultEvent {
/*    */     public WebSocketEvent result;
/*    */     public UUID uuid;
/*    */     public String type;
/*    */     public boolean ready;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\request\websockets\WaitEventHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */