/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.util.IllegalReferenceCountException;
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
/*     */ public class DefaultSpdyDataFrame
/*     */   extends DefaultSpdyStreamFrame
/*     */   implements SpdyDataFrame
/*     */ {
/*     */   private final ByteBuf data;
/*     */   
/*     */   public DefaultSpdyDataFrame(int streamId) {
/*  37 */     this(streamId, Unpooled.buffer(0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultSpdyDataFrame(int streamId, ByteBuf data) {
/*  47 */     super(streamId);
/*  48 */     this.data = validate(
/*  49 */         (ByteBuf)ObjectUtil.checkNotNull(data, "data"));
/*     */   }
/*     */   
/*     */   private static ByteBuf validate(ByteBuf data) {
/*  53 */     if (data.readableBytes() > 16777215) {
/*  54 */       throw new IllegalArgumentException("data payload cannot exceed 16777215 bytes");
/*     */     }
/*     */     
/*  57 */     return data;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyDataFrame setStreamId(int streamId) {
/*  62 */     super.setStreamId(streamId);
/*  63 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyDataFrame setLast(boolean last) {
/*  68 */     super.setLast(last);
/*  69 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf content() {
/*  74 */     if (this.data.refCnt() <= 0) {
/*  75 */       throw new IllegalReferenceCountException(this.data.refCnt());
/*     */     }
/*  77 */     return this.data;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyDataFrame copy() {
/*  82 */     return replace(content().copy());
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyDataFrame duplicate() {
/*  87 */     return replace(content().duplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyDataFrame retainedDuplicate() {
/*  92 */     return replace(content().retainedDuplicate());
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyDataFrame replace(ByteBuf content) {
/*  97 */     SpdyDataFrame frame = new DefaultSpdyDataFrame(streamId(), content);
/*  98 */     frame.setLast(isLast());
/*  99 */     return frame;
/*     */   }
/*     */ 
/*     */   
/*     */   public int refCnt() {
/* 104 */     return this.data.refCnt();
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyDataFrame retain() {
/* 109 */     this.data.retain();
/* 110 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyDataFrame retain(int increment) {
/* 115 */     this.data.retain(increment);
/* 116 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyDataFrame touch() {
/* 121 */     this.data.touch();
/* 122 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdyDataFrame touch(Object hint) {
/* 127 */     this.data.touch(hint);
/* 128 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release() {
/* 133 */     return this.data.release();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean release(int decrement) {
/* 138 */     return this.data.release(decrement);
/*     */   }
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
/*     */   public String toString() {
/* 152 */     StringBuilder buf = (new StringBuilder()).append(StringUtil.simpleClassName(this)).append("(last: ").append(isLast()).append(')').append(StringUtil.NEWLINE).append("--> Stream-ID = ").append(streamId()).append(StringUtil.NEWLINE).append("--> Size = ");
/* 153 */     if (refCnt() == 0) {
/* 154 */       buf.append("(freed)");
/*     */     } else {
/* 156 */       buf.append(content().readableBytes());
/*     */     } 
/* 158 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\DefaultSpdyDataFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */