/*    */ package pro.gravit.repackage.io.netty.handler.ssl.util;
/*    */ 
/*    */ import java.security.SecureRandom;
/*    */ import java.util.Random;
/*    */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
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
/*    */ final class ThreadLocalInsecureRandom
/*    */   extends SecureRandom
/*    */ {
/*    */   private static final long serialVersionUID = -8209473337192526191L;
/* 32 */   private static final SecureRandom INSTANCE = new ThreadLocalInsecureRandom();
/*    */   
/*    */   static SecureRandom current() {
/* 35 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getAlgorithm() {
/* 42 */     return "insecure";
/*    */   }
/*    */ 
/*    */   
/*    */   public void setSeed(byte[] seed) {}
/*    */ 
/*    */   
/*    */   public void setSeed(long seed) {}
/*    */ 
/*    */   
/*    */   public void nextBytes(byte[] bytes) {
/* 53 */     random().nextBytes(bytes);
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] generateSeed(int numBytes) {
/* 58 */     byte[] seed = new byte[numBytes];
/* 59 */     random().nextBytes(seed);
/* 60 */     return seed;
/*    */   }
/*    */ 
/*    */   
/*    */   public int nextInt() {
/* 65 */     return random().nextInt();
/*    */   }
/*    */ 
/*    */   
/*    */   public int nextInt(int n) {
/* 70 */     return random().nextInt(n);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean nextBoolean() {
/* 75 */     return random().nextBoolean();
/*    */   }
/*    */ 
/*    */   
/*    */   public long nextLong() {
/* 80 */     return random().nextLong();
/*    */   }
/*    */ 
/*    */   
/*    */   public float nextFloat() {
/* 85 */     return random().nextFloat();
/*    */   }
/*    */ 
/*    */   
/*    */   public double nextDouble() {
/* 90 */     return random().nextDouble();
/*    */   }
/*    */ 
/*    */   
/*    */   public double nextGaussian() {
/* 95 */     return random().nextGaussian();
/*    */   }
/*    */   
/*    */   private static Random random() {
/* 99 */     return PlatformDependent.threadLocalRandom();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ss\\util\ThreadLocalInsecureRandom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */