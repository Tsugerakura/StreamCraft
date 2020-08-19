/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class OpenSslEngine
/*    */   extends ReferenceCountedOpenSslEngine
/*    */ {
/*    */   OpenSslEngine(OpenSslContext context, ByteBufAllocator alloc, String peerHost, int peerPort, boolean jdkCompatibilityMode) {
/* 32 */     super(context, alloc, peerHost, peerPort, jdkCompatibilityMode, false);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void finalize() throws Throwable {
/* 38 */     super.finalize();
/* 39 */     OpenSsl.releaseIfNeeded(this);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslEngine.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */