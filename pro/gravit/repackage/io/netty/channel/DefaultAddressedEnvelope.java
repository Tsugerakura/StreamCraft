/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCountUtil;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DefaultAddressedEnvelope<M, A extends SocketAddress>
/*     */   implements AddressedEnvelope<M, A>
/*     */ {
/*     */   private final M message;
/*     */   private final A sender;
/*     */   private final A recipient;
/*     */   
/*     */   public DefaultAddressedEnvelope(M message, A recipient, A sender) {
/*  43 */     ObjectUtil.checkNotNull(message, "message");
/*  44 */     if (recipient == null && sender == null) {
/*  45 */       throw new NullPointerException("recipient and sender");
/*     */     }
/*     */     
/*  48 */     this.message = message;
/*  49 */     this.sender = sender;
/*  50 */     this.recipient = recipient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultAddressedEnvelope(M message, A recipient) {
/*  58 */     this(message, recipient, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public M content() {
/*  63 */     return this.message;
/*     */   }
/*     */ 
/*     */   
/*     */   public A sender() {
/*  68 */     return this.sender;
/*     */   }
/*     */ 
/*     */   
/*     */   public A recipient() {
/*  73 */     return this.recipient;
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt() {
/*  78 */     if (this.message instanceof ReferenceCounted) {
/*  79 */       return ((ReferenceCounted)this.message).refCnt();
/*     */     }
/*  81 */     return 1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AddressedEnvelope<M, A> retain() {
/*  87 */     ReferenceCountUtil.retain(this.message);
/*  88 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public AddressedEnvelope<M, A> retain(int increment) {
/*  93 */     ReferenceCountUtil.retain(this.message, increment);
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/*  99 */     return ReferenceCountUtil.release(this.message);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 104 */     return ReferenceCountUtil.release(this.message, decrement);
/*     */   }
/*     */ 
/*     */   
/*     */   public AddressedEnvelope<M, A> touch() {
/* 109 */     ReferenceCountUtil.touch(this.message);
/* 110 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public AddressedEnvelope<M, A> touch(Object hint) {
/* 115 */     ReferenceCountUtil.touch(this.message, hint);
/* 116 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 121 */     if (this.sender != null) {
/* 122 */       return StringUtil.simpleClassName(this) + '(' + this.sender + " => " + this.recipient + ", " + this.message + ')';
/*     */     }
/*     */     
/* 125 */     return StringUtil.simpleClassName(this) + "(=> " + this.recipient + ", " + this.message + ')';
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultAddressedEnvelope.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */