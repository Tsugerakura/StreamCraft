/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ @Sharable
/*     */ public class LengthFieldPrepender
/*     */   extends MessageToMessageEncoder<ByteBuf>
/*     */ {
/*     */   private final ByteOrder byteOrder;
/*     */   private final int lengthFieldLength;
/*     */   private final boolean lengthIncludesLengthFieldLength;
/*     */   private final int lengthAdjustment;
/*     */   
/*     */   public LengthFieldPrepender(int lengthFieldLength) {
/*  73 */     this(lengthFieldLength, false);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public LengthFieldPrepender(int lengthFieldLength, boolean lengthIncludesLengthFieldLength) {
/*  90 */     this(lengthFieldLength, 0, lengthIncludesLengthFieldLength);
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
/*     */   
/*     */   public LengthFieldPrepender(int lengthFieldLength, int lengthAdjustment) {
/* 105 */     this(lengthFieldLength, lengthAdjustment, false);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LengthFieldPrepender(int lengthFieldLength, int lengthAdjustment, boolean lengthIncludesLengthFieldLength) {
/* 124 */     this(ByteOrder.BIG_ENDIAN, lengthFieldLength, lengthAdjustment, lengthIncludesLengthFieldLength);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LengthFieldPrepender(ByteOrder byteOrder, int lengthFieldLength, int lengthAdjustment, boolean lengthIncludesLengthFieldLength) {
/* 146 */     if (lengthFieldLength != 1 && lengthFieldLength != 2 && lengthFieldLength != 3 && lengthFieldLength != 4 && lengthFieldLength != 8)
/*     */     {
/*     */       
/* 149 */       throw new IllegalArgumentException("lengthFieldLength must be either 1, 2, 3, 4, or 8: " + lengthFieldLength);
/*     */     }
/*     */ 
/*     */     
/* 153 */     this.byteOrder = (ByteOrder)ObjectUtil.checkNotNull(byteOrder, "byteOrder");
/* 154 */     this.lengthFieldLength = lengthFieldLength;
/* 155 */     this.lengthIncludesLengthFieldLength = lengthIncludesLengthFieldLength;
/* 156 */     this.lengthAdjustment = lengthAdjustment;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
/* 161 */     int length = msg.readableBytes() + this.lengthAdjustment;
/* 162 */     if (this.lengthIncludesLengthFieldLength) {
/* 163 */       length += this.lengthFieldLength;
/*     */     }
/*     */     
/* 166 */     ObjectUtil.checkPositiveOrZero(length, "length");
/*     */     
/* 168 */     switch (this.lengthFieldLength) {
/*     */       case 1:
/* 170 */         if (length >= 256) {
/* 171 */           throw new IllegalArgumentException("length does not fit into a byte: " + length);
/*     */         }
/*     */         
/* 174 */         out.add(ctx.alloc().buffer(1).order(this.byteOrder).writeByte((byte)length));
/*     */         break;
/*     */       case 2:
/* 177 */         if (length >= 65536) {
/* 178 */           throw new IllegalArgumentException("length does not fit into a short integer: " + length);
/*     */         }
/*     */         
/* 181 */         out.add(ctx.alloc().buffer(2).order(this.byteOrder).writeShort((short)length));
/*     */         break;
/*     */       case 3:
/* 184 */         if (length >= 16777216) {
/* 185 */           throw new IllegalArgumentException("length does not fit into a medium integer: " + length);
/*     */         }
/*     */         
/* 188 */         out.add(ctx.alloc().buffer(3).order(this.byteOrder).writeMedium(length));
/*     */         break;
/*     */       case 4:
/* 191 */         out.add(ctx.alloc().buffer(4).order(this.byteOrder).writeInt(length));
/*     */         break;
/*     */       case 8:
/* 194 */         out.add(ctx.alloc().buffer(8).order(this.byteOrder).writeLong(length));
/*     */         break;
/*     */       default:
/* 197 */         throw new Error("should not reach here");
/*     */     } 
/* 199 */     out.add(msg.retain());
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\LengthFieldPrepender.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */