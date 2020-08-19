/*    */ package pro.gravit.repackage.io.netty.channel.nio;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.channels.SelectionKey;
/*    */ import java.nio.channels.Selector;
/*    */ import java.nio.channels.spi.SelectorProvider;
/*    */ import java.util.Set;
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
/*    */ final class SelectedSelectionKeySetSelector
/*    */   extends Selector
/*    */ {
/*    */   private final SelectedSelectionKeySet selectionKeys;
/*    */   private final Selector delegate;
/*    */   
/*    */   SelectedSelectionKeySetSelector(Selector delegate, SelectedSelectionKeySet selectionKeys) {
/* 29 */     this.delegate = delegate;
/* 30 */     this.selectionKeys = selectionKeys;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isOpen() {
/* 35 */     return this.delegate.isOpen();
/*    */   }
/*    */ 
/*    */   
/*    */   public SelectorProvider provider() {
/* 40 */     return this.delegate.provider();
/*    */   }
/*    */ 
/*    */   
/*    */   public Set<SelectionKey> keys() {
/* 45 */     return this.delegate.keys();
/*    */   }
/*    */ 
/*    */   
/*    */   public Set<SelectionKey> selectedKeys() {
/* 50 */     return this.delegate.selectedKeys();
/*    */   }
/*    */ 
/*    */   
/*    */   public int selectNow() throws IOException {
/* 55 */     this.selectionKeys.reset();
/* 56 */     return this.delegate.selectNow();
/*    */   }
/*    */ 
/*    */   
/*    */   public int select(long timeout) throws IOException {
/* 61 */     this.selectionKeys.reset();
/* 62 */     return this.delegate.select(timeout);
/*    */   }
/*    */ 
/*    */   
/*    */   public int select() throws IOException {
/* 67 */     this.selectionKeys.reset();
/* 68 */     return this.delegate.select();
/*    */   }
/*    */ 
/*    */   
/*    */   public Selector wakeup() {
/* 73 */     return this.delegate.wakeup();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 78 */     this.delegate.close();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\nio\SelectedSelectionKeySetSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */