/*    */ package pro.gravit.repackage.io.netty.channel;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*    */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*    */ public class ReflectiveChannelFactory<T extends Channel>
/*    */   implements ChannelFactory<T>
/*    */ {
/*    */   private final Constructor<? extends T> constructor;
/*    */   
/*    */   public ReflectiveChannelFactory(Class<? extends T> clazz) {
/* 32 */     ObjectUtil.checkNotNull(clazz, "clazz");
/*    */     try {
/* 34 */       this.constructor = clazz.getConstructor(new Class[0]);
/* 35 */     } catch (NoSuchMethodException e) {
/* 36 */       throw new IllegalArgumentException("Class " + StringUtil.simpleClassName(clazz) + " does not have a public non-arg constructor", e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public T newChannel() {
/*    */     try {
/* 44 */       return this.constructor.newInstance(new Object[0]);
/* 45 */     } catch (Throwable t) {
/* 46 */       throw new ChannelException("Unable to create Channel from class " + this.constructor.getDeclaringClass(), t);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 52 */     return StringUtil.simpleClassName(ReflectiveChannelFactory.class) + '(' + 
/* 53 */       StringUtil.simpleClassName(this.constructor.getDeclaringClass()) + ".class)";
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\ReflectiveChannelFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */