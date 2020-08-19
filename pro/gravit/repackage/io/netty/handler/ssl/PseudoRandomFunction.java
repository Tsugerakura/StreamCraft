/*    */ package pro.gravit.repackage.io.netty.handler.ssl;
/*    */ 
/*    */ import java.security.GeneralSecurityException;
/*    */ import java.util.Arrays;
/*    */ import javax.crypto.Mac;
/*    */ import javax.crypto.spec.SecretKeySpec;
/*    */ import pro.gravit.repackage.io.netty.util.internal.EmptyArrays;
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
/*    */ final class PseudoRandomFunction
/*    */ {
/*    */   static byte[] hash(byte[] secret, byte[] label, byte[] seed, int length, String algo) {
/* 62 */     if (length < 0) {
/* 63 */       throw new IllegalArgumentException("You must provide a length greater than zero.");
/*    */     }
/*    */     try {
/* 66 */       Mac hmac = Mac.getInstance(algo);
/* 67 */       hmac.init(new SecretKeySpec(secret, algo));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 75 */       int iterations = (int)Math.ceil(length / hmac.getMacLength());
/* 76 */       byte[] expansion = EmptyArrays.EMPTY_BYTES;
/* 77 */       byte[] data = concat(label, seed);
/* 78 */       byte[] A = data;
/* 79 */       for (int i = 0; i < iterations; i++) {
/* 80 */         A = hmac.doFinal(A);
/* 81 */         expansion = concat(expansion, hmac.doFinal(concat(A, data)));
/*    */       } 
/* 83 */       return Arrays.copyOf(expansion, length);
/* 84 */     } catch (GeneralSecurityException e) {
/* 85 */       throw new IllegalArgumentException("Could not find algo: " + algo, e);
/*    */     } 
/*    */   }
/*    */   
/*    */   private static byte[] concat(byte[] first, byte[] second) {
/* 90 */     byte[] result = Arrays.copyOf(first, first.length + second.length);
/* 91 */     System.arraycopy(second, 0, result, first.length, second.length);
/* 92 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\PseudoRandomFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */