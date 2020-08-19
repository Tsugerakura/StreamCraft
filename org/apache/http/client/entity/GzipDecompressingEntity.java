/*    */ package org.apache.http.client.entity;
/*    */ 
/*    */ import org.apache.http.HttpEntity;
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
/*    */ public class GzipDecompressingEntity
/*    */   extends DecompressingEntity
/*    */ {
/*    */   public GzipDecompressingEntity(HttpEntity entity) {
/* 47 */     super(entity, GZIPInputStreamFactory.getInstance());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\client\entity\GzipDecompressingEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */