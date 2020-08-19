/*    */ package pro.gravit.repackage.io.netty.handler.codec.serialization;
/*    */ 
/*    */ import java.lang.ref.Reference;
/*    */ import java.lang.ref.SoftReference;
/*    */ import java.util.Map;
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
/*    */ final class SoftReferenceMap<K, V>
/*    */   extends ReferenceMap<K, V>
/*    */ {
/*    */   SoftReferenceMap(Map<K, Reference<V>> delegate) {
/* 25 */     super(delegate);
/*    */   }
/*    */ 
/*    */   
/*    */   Reference<V> fold(V value) {
/* 30 */     return new SoftReference<V>(value);
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\serialization\SoftReferenceMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */