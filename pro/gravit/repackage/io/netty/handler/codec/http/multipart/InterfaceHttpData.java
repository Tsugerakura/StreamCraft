/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*    */ 
/*    */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface InterfaceHttpData
/*    */   extends Comparable<InterfaceHttpData>, ReferenceCounted
/*    */ {
/*    */   String getName();
/*    */   
/*    */   HttpDataType getHttpDataType();
/*    */   
/*    */   InterfaceHttpData retain();
/*    */   
/*    */   InterfaceHttpData retain(int paramInt);
/*    */   
/*    */   InterfaceHttpData touch();
/*    */   
/*    */   InterfaceHttpData touch(Object paramObject);
/*    */   
/*    */   public enum HttpDataType
/*    */   {
/* 25 */     Attribute, FileUpload, InternalAttribute;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\InterfaceHttpData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */