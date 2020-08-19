/*    */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
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
/*    */ final class FileUploadUtil
/*    */ {
/*    */   static int hashCode(FileUpload upload) {
/* 23 */     return upload.getName().hashCode();
/*    */   }
/*    */   
/*    */   static boolean equals(FileUpload upload1, FileUpload upload2) {
/* 27 */     return upload1.getName().equalsIgnoreCase(upload2.getName());
/*    */   }
/*    */   
/*    */   static int compareTo(FileUpload upload1, FileUpload upload2) {
/* 31 */     return upload1.getName().compareToIgnoreCase(upload2.getName());
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\FileUploadUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */