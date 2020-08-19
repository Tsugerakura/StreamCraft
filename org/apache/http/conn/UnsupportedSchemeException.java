/*    */ package org.apache.http.conn;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ public class UnsupportedSchemeException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 3597127619218687636L;
/*    */   
/*    */   public UnsupportedSchemeException(String message) {
/* 45 */     super(message);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\conn\UnsupportedSchemeException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */