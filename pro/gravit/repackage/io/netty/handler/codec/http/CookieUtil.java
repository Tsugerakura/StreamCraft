/*    */ package pro.gravit.repackage.io.netty.handler.codec.http;
/*    */ 
/*    */ import java.util.BitSet;
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
/*    */ final class CookieUtil
/*    */ {
/* 26 */   private static final BitSet VALID_COOKIE_VALUE_OCTETS = validCookieValueOctets();
/*    */   
/* 28 */   private static final BitSet VALID_COOKIE_NAME_OCTETS = validCookieNameOctets(VALID_COOKIE_VALUE_OCTETS);
/*    */ 
/*    */   
/*    */   private static BitSet validCookieValueOctets() {
/* 32 */     BitSet bits = new BitSet(8);
/* 33 */     for (int i = 35; i < 127; i++)
/*    */     {
/* 35 */       bits.set(i);
/*    */     }
/* 37 */     bits.set(34, false);
/* 38 */     bits.set(44, false);
/* 39 */     bits.set(59, false);
/* 40 */     bits.set(92, false);
/* 41 */     return bits;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static BitSet validCookieNameOctets(BitSet validCookieValueOctets) {
/* 50 */     BitSet bits = new BitSet(8);
/* 51 */     bits.or(validCookieValueOctets);
/* 52 */     bits.set(40, false);
/* 53 */     bits.set(41, false);
/* 54 */     bits.set(60, false);
/* 55 */     bits.set(62, false);
/* 56 */     bits.set(64, false);
/* 57 */     bits.set(58, false);
/* 58 */     bits.set(47, false);
/* 59 */     bits.set(91, false);
/* 60 */     bits.set(93, false);
/* 61 */     bits.set(63, false);
/* 62 */     bits.set(61, false);
/* 63 */     bits.set(123, false);
/* 64 */     bits.set(125, false);
/* 65 */     bits.set(32, false);
/* 66 */     bits.set(9, false);
/* 67 */     return bits;
/*    */   }
/*    */   
/*    */   static int firstInvalidCookieNameOctet(CharSequence cs) {
/* 71 */     return firstInvalidOctet(cs, VALID_COOKIE_NAME_OCTETS);
/*    */   }
/*    */   
/*    */   static int firstInvalidCookieValueOctet(CharSequence cs) {
/* 75 */     return firstInvalidOctet(cs, VALID_COOKIE_VALUE_OCTETS);
/*    */   }
/*    */   
/*    */   static int firstInvalidOctet(CharSequence cs, BitSet bits) {
/* 79 */     for (int i = 0; i < cs.length(); i++) {
/* 80 */       char c = cs.charAt(i);
/* 81 */       if (!bits.get(c)) {
/* 82 */         return i;
/*    */       }
/*    */     } 
/* 85 */     return -1;
/*    */   }
/*    */   
/*    */   static CharSequence unwrapValue(CharSequence cs) {
/* 89 */     int len = cs.length();
/* 90 */     if (len > 0 && cs.charAt(0) == '"') {
/* 91 */       if (len >= 2 && cs.charAt(len - 1) == '"')
/*    */       {
/* 93 */         return (len == 2) ? "" : cs.subSequence(1, len - 1);
/*    */       }
/* 95 */       return null;
/*    */     } 
/*    */     
/* 98 */     return cs;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\CookieUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */