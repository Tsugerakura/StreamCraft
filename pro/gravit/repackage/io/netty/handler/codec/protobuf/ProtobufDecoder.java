/*     */ package pro.gravit.repackage.io.netty.handler.codec.protobuf;
/*     */ 
/*     */ import com.google.protobuf.ExtensionRegistry;
/*     */ import com.google.protobuf.ExtensionRegistryLite;
/*     */ import com.google.protobuf.MessageLite;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufUtil;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandler.Sharable;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelHandlerContext;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.MessageToMessageDecoder;
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
/*     */ @Sharable
/*     */ public class ProtobufDecoder
/*     */   extends MessageToMessageDecoder<ByteBuf>
/*     */ {
/*     */   private static final boolean HAS_PARSER;
/*     */   private final MessageLite prototype;
/*     */   private final ExtensionRegistryLite extensionRegistry;
/*     */   
/*     */   static {
/*  72 */     boolean hasParser = false;
/*     */     
/*     */     try {
/*  75 */       MessageLite.class.getDeclaredMethod("getParserForType", new Class[0]);
/*  76 */       hasParser = true;
/*  77 */     } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */     
/*  81 */     HAS_PARSER = hasParser;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ProtobufDecoder(MessageLite prototype) {
/*  91 */     this(prototype, (ExtensionRegistry)null);
/*     */   }
/*     */   
/*     */   public ProtobufDecoder(MessageLite prototype, ExtensionRegistry extensionRegistry) {
/*  95 */     this(prototype, (ExtensionRegistryLite)extensionRegistry);
/*     */   }
/*     */   
/*     */   public ProtobufDecoder(MessageLite prototype, ExtensionRegistryLite extensionRegistry) {
/*  99 */     this.prototype = ((MessageLite)ObjectUtil.checkNotNull(prototype, "prototype")).getDefaultInstanceForType();
/* 100 */     this.extensionRegistry = extensionRegistry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
/*     */     byte[] array;
/* 108 */     int offset, length = msg.readableBytes();
/* 109 */     if (msg.hasArray()) {
/* 110 */       array = msg.array();
/* 111 */       offset = msg.arrayOffset() + msg.readerIndex();
/*     */     } else {
/* 113 */       array = ByteBufUtil.getBytes(msg, msg.readerIndex(), length, false);
/* 114 */       offset = 0;
/*     */     } 
/*     */     
/* 117 */     if (this.extensionRegistry == null) {
/* 118 */       if (HAS_PARSER) {
/* 119 */         out.add(this.prototype.getParserForType().parseFrom(array, offset, length));
/*     */       } else {
/* 121 */         out.add(this.prototype.newBuilderForType().mergeFrom(array, offset, length).build());
/*     */       }
/*     */     
/* 124 */     } else if (HAS_PARSER) {
/* 125 */       out.add(this.prototype.getParserForType().parseFrom(array, offset, length, this.extensionRegistry));
/*     */     } else {
/*     */       
/* 128 */       out.add(this.prototype.newBuilderForType().mergeFrom(array, offset, length, this.extensionRegistry)
/* 129 */           .build());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\protobuf\ProtobufDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */