/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public abstract class AbstractDerivedByteBuf
/*     */   extends AbstractByteBuf
/*     */ {
/*     */   protected AbstractDerivedByteBuf(int maxCapacity) {
/*  31 */     super(maxCapacity);
/*     */   }
/*     */ 
/*     */   
/*     */   final boolean isAccessible() {
/*  36 */     return unwrap().isAccessible();
/*     */   }
/*     */ 
/*     */   
/*     */   public final int refCnt() {
/*  41 */     return refCnt0();
/*     */   }
/*     */   
/*     */   int refCnt0() {
/*  45 */     return unwrap().refCnt();
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf retain() {
/*  50 */     return retain0();
/*     */   }
/*     */   
/*     */   ByteBuf retain0() {
/*  54 */     unwrap().retain();
/*  55 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf retain(int increment) {
/*  60 */     return retain0(increment);
/*     */   }
/*     */   
/*     */   ByteBuf retain0(int increment) {
/*  64 */     unwrap().retain(increment);
/*  65 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf touch() {
/*  70 */     return touch0();
/*     */   }
/*     */   
/*     */   ByteBuf touch0() {
/*  74 */     unwrap().touch();
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final ByteBuf touch(Object hint) {
/*  80 */     return touch0(hint);
/*     */   }
/*     */   
/*     */   ByteBuf touch0(Object hint) {
/*  84 */     unwrap().touch(hint);
/*  85 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean release() {
/*  90 */     return release0();
/*     */   }
/*     */   
/*     */   boolean release0() {
/*  94 */     return unwrap().release();
/*     */   }
/*     */ 
/*     */   
/*     */   public final boolean release(int decrement) {
/*  99 */     return release0(decrement);
/*     */   }
/*     */   
/*     */   boolean release0(int decrement) {
/* 103 */     return unwrap().release(decrement);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 108 */     return unwrap().isReadOnly();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer internalNioBuffer(int index, int length) {
/* 113 */     return nioBuffer(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuffer nioBuffer(int index, int length) {
/* 118 */     return unwrap().nioBuffer(index, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isContiguous() {
/* 123 */     return unwrap().isContiguous();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\AbstractDerivedByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */