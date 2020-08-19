/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.concurrent.ConcurrentMap;
/*    */ import javax.net.ssl.X509KeyManager;
/*    */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class OpenSslCachingKeyMaterialProvider
/*    */   extends OpenSslKeyMaterialProvider
/*    */ {
/*    */   private final int maxCachedEntries;
/*    */   private volatile boolean full;
/* 33 */   private final ConcurrentMap<String, OpenSslKeyMaterial> cache = new ConcurrentHashMap<String, OpenSslKeyMaterial>();
/*    */   
/*    */   OpenSslCachingKeyMaterialProvider(X509KeyManager keyManager, String password, int maxCachedEntries) {
/* 36 */     super(keyManager, password);
/* 37 */     this.maxCachedEntries = maxCachedEntries;
/*    */   }
/*    */ 
/*    */   
/*    */   OpenSslKeyMaterial chooseKeyMaterial(ByteBufAllocator allocator, String alias) throws Exception {
/* 42 */     OpenSslKeyMaterial material = this.cache.get(alias);
/* 43 */     if (material == null) {
/* 44 */       material = super.chooseKeyMaterial(allocator, alias);
/* 45 */       if (material == null)
/*    */       {
/* 47 */         return null;
/*    */       }
/*    */       
/* 50 */       if (this.full) {
/* 51 */         return material;
/*    */       }
/* 53 */       if (this.cache.size() > this.maxCachedEntries) {
/* 54 */         this.full = true;
/*    */         
/* 56 */         return material;
/*    */       } 
/* 58 */       OpenSslKeyMaterial old = this.cache.putIfAbsent(alias, material);
/* 59 */       if (old != null) {
/* 60 */         material.release();
/* 61 */         material = old;
/*    */       } 
/*    */     } 
/*    */     
/* 65 */     return material.retain();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   void destroy() {
/*    */     do {
/* 72 */       Iterator<OpenSslKeyMaterial> iterator = this.cache.values().iterator();
/* 73 */       while (iterator.hasNext()) {
/* 74 */         ((OpenSslKeyMaterial)iterator.next()).release();
/* 75 */         iterator.remove();
/*    */       } 
/* 77 */     } while (!this.cache.isEmpty());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslCachingKeyMaterialProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */