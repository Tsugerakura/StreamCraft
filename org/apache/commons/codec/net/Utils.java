/*    */ package org.apache.commons.codec.net;
/*    */ 
/*    */ import org.apache.commons.codec.DecoderException;
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
/*    */ class Utils
/*    */ {
/*    */   private static final int RADIX = 16;
/*    */   
/*    */   static int digit16(byte b) throws DecoderException {
/* 48 */     int i = Character.digit((char)b, 16);
/* 49 */     if (i == -1) {
/* 50 */       throw new DecoderException("Invalid URL encoding: not a valid digit (radix 16): " + b);
/*    */     }
/* 52 */     return i;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static char hexDigit(int b) {
/* 62 */     return Character.toUpperCase(Character.forDigit(b & 0xF, 16));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\commons\codec\net\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */