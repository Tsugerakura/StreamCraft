/*    */ package pro.gravit.repackage.io.netty.channel.nio;
/*    */ 
/*    */ import java.nio.channels.SelectionKey;
/*    */ import java.util.AbstractSet;
/*    */ import java.util.Arrays;
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
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
/*    */ final class SelectedSelectionKeySet
/*    */   extends AbstractSet<SelectionKey>
/*    */ {
/* 30 */   SelectionKey[] keys = new SelectionKey[1024];
/*    */   
/*    */   int size;
/*    */   
/*    */   public boolean add(SelectionKey o) {
/* 35 */     if (o == null) {
/* 36 */       return false;
/*    */     }
/*    */     
/* 39 */     this.keys[this.size++] = o;
/* 40 */     if (this.size == this.keys.length) {
/* 41 */       increaseCapacity();
/*    */     }
/*    */     
/* 44 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean remove(Object o) {
/* 49 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean contains(Object o) {
/* 54 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 59 */     return this.size;
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<SelectionKey> iterator() {
/* 64 */     return new Iterator<SelectionKey>()
/*    */       {
/*    */         private int idx;
/*    */         
/*    */         public boolean hasNext() {
/* 69 */           return (this.idx < SelectedSelectionKeySet.this.size);
/*    */         }
/*    */ 
/*    */         
/*    */         public SelectionKey next() {
/* 74 */           if (!hasNext()) {
/* 75 */             throw new NoSuchElementException();
/*    */           }
/* 77 */           return SelectedSelectionKeySet.this.keys[this.idx++];
/*    */         }
/*    */ 
/*    */         
/*    */         public void remove() {
/* 82 */           throw new UnsupportedOperationException();
/*    */         }
/*    */       };
/*    */   }
/*    */   
/*    */   void reset() {
/* 88 */     reset(0);
/*    */   }
/*    */   
/*    */   void reset(int start) {
/* 92 */     Arrays.fill((Object[])this.keys, start, this.size, (Object)null);
/* 93 */     this.size = 0;
/*    */   }
/*    */   
/*    */   private void increaseCapacity() {
/* 97 */     SelectionKey[] newKeys = new SelectionKey[this.keys.length << 1];
/* 98 */     System.arraycopy(this.keys, 0, newKeys, 0, this.size);
/* 99 */     this.keys = newKeys;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\nio\SelectedSelectionKeySet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */