/*    */ package org.apache.http.impl.conn.tsccm;
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
/*    */ @Deprecated
/*    */ public class WaitingThreadAborter
/*    */ {
/*    */   private WaitingThread waitingThread;
/*    */   private boolean aborted;
/*    */   
/*    */   public void abort() {
/* 48 */     this.aborted = true;
/*    */     
/* 50 */     if (this.waitingThread != null) {
/* 51 */       this.waitingThread.interrupt();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setWaitingThread(WaitingThread waitingThread) {
/* 63 */     this.waitingThread = waitingThread;
/* 64 */     if (this.aborted)
/* 65 */       waitingThread.interrupt(); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\impl\conn\tsccm\WaitingThreadAborter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */