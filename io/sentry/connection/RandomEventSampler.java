/*    */ package io.sentry.connection;
/*    */ 
/*    */ import io.sentry.event.Event;
/*    */ import java.util.Random;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RandomEventSampler
/*    */   implements EventSampler
/*    */ {
/*    */   private double sampleRate;
/*    */   private Random random;
/*    */   
/*    */   public RandomEventSampler(double sampleRate) {
/* 21 */     this(sampleRate, new Random());
/*    */   }
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
/*    */   public RandomEventSampler(double sampleRate, Random random) {
/* 35 */     this.sampleRate = sampleRate;
/* 36 */     this.random = random;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean shouldSendEvent(Event event) {
/* 47 */     double randomDouble = this.random.nextDouble();
/* 48 */     return (this.sampleRate >= Math.abs(randomDouble));
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\connection\RandomEventSampler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */