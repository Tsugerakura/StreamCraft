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
/*    */ public class DeflateDecompressingEntity
/*    */   extends DecompressingEntity
/*    */ {
/*    */   public DeflateDecompressingEntity(HttpEntity entity) {
/* 57 */     super(entity, DeflateInputStreamFactory.getInstance());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\client\entity\DeflateDecompressingEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */