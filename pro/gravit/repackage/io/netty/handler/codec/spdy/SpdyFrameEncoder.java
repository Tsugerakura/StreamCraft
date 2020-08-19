/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Set;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufAllocator;
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
/*     */ public class SpdyFrameEncoder
/*     */ {
/*     */   private final int version;
/*     */   
/*     */   public SpdyFrameEncoder(SpdyVersion spdyVersion) {
/*  38 */     this.version = ((SpdyVersion)ObjectUtil.checkNotNull(spdyVersion, "spdyVersion")).getVersion();
/*     */   }
/*     */   
/*     */   private void writeControlFrameHeader(ByteBuf buffer, int type, byte flags, int length) {
/*  42 */     buffer.writeShort(this.version | 0x8000);
/*  43 */     buffer.writeShort(type);
/*  44 */     buffer.writeByte(flags);
/*  45 */     buffer.writeMedium(length);
/*     */   }
/*     */   
/*     */   public ByteBuf encodeDataFrame(ByteBufAllocator allocator, int streamId, boolean last, ByteBuf data) {
/*  49 */     byte flags = last ? 1 : 0;
/*  50 */     int length = data.readableBytes();
/*  51 */     ByteBuf frame = allocator.ioBuffer(8 + length).order(ByteOrder.BIG_ENDIAN);
/*  52 */     frame.writeInt(streamId & Integer.MAX_VALUE);
/*  53 */     frame.writeByte(flags);
/*  54 */     frame.writeMedium(length);
/*  55 */     frame.writeBytes(data, data.readerIndex(), length);
/*  56 */     return frame;
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBuf encodeSynStreamFrame(ByteBufAllocator allocator, int streamId, int associatedToStreamId, byte priority, boolean last, boolean unidirectional, ByteBuf headerBlock) {
/*  61 */     int headerBlockLength = headerBlock.readableBytes();
/*  62 */     byte flags = last ? 1 : 0;
/*  63 */     if (unidirectional) {
/*  64 */       flags = (byte)(flags | 0x2);
/*     */     }
/*  66 */     int length = 10 + headerBlockLength;
/*  67 */     ByteBuf frame = allocator.ioBuffer(8 + length).order(ByteOrder.BIG_ENDIAN);
/*  68 */     writeControlFrameHeader(frame, 1, flags, length);
/*  69 */     frame.writeInt(streamId);
/*  70 */     frame.writeInt(associatedToStreamId);
/*  71 */     frame.writeShort((priority & 0xFF) << 13);
/*  72 */     frame.writeBytes(headerBlock, headerBlock.readerIndex(), headerBlockLength);
/*  73 */     return frame;
/*     */   }
/*     */   
/*     */   public ByteBuf encodeSynReplyFrame(ByteBufAllocator allocator, int streamId, boolean last, ByteBuf headerBlock) {
/*  77 */     int headerBlockLength = headerBlock.readableBytes();
/*  78 */     byte flags = last ? 1 : 0;
/*  79 */     int length = 4 + headerBlockLength;
/*  80 */     ByteBuf frame = allocator.ioBuffer(8 + length).order(ByteOrder.BIG_ENDIAN);
/*  81 */     writeControlFrameHeader(frame, 2, flags, length);
/*  82 */     frame.writeInt(streamId);
/*  83 */     frame.writeBytes(headerBlock, headerBlock.readerIndex(), headerBlockLength);
/*  84 */     return frame;
/*     */   }
/*     */   
/*     */   public ByteBuf encodeRstStreamFrame(ByteBufAllocator allocator, int streamId, int statusCode) {
/*  88 */     byte flags = 0;
/*  89 */     int length = 8;
/*  90 */     ByteBuf frame = allocator.ioBuffer(8 + length).order(ByteOrder.BIG_ENDIAN);
/*  91 */     writeControlFrameHeader(frame, 3, flags, length);
/*  92 */     frame.writeInt(streamId);
/*  93 */     frame.writeInt(statusCode);
/*  94 */     return frame;
/*     */   }
/*     */   
/*     */   public ByteBuf encodeSettingsFrame(ByteBufAllocator allocator, SpdySettingsFrame spdySettingsFrame) {
/*  98 */     Set<Integer> ids = spdySettingsFrame.ids();
/*  99 */     int numSettings = ids.size();
/*     */     
/* 101 */     byte flags = spdySettingsFrame.clearPreviouslyPersistedSettings() ? 1 : 0;
/*     */     
/* 103 */     int length = 4 + 8 * numSettings;
/* 104 */     ByteBuf frame = allocator.ioBuffer(8 + length).order(ByteOrder.BIG_ENDIAN);
/* 105 */     writeControlFrameHeader(frame, 4, flags, length);
/* 106 */     frame.writeInt(numSettings);
/* 107 */     for (Integer id : ids) {
/* 108 */       flags = 0;
/* 109 */       if (spdySettingsFrame.isPersistValue(id.intValue())) {
/* 110 */         flags = (byte)(flags | 0x1);
/*     */       }
/* 112 */       if (spdySettingsFrame.isPersisted(id.intValue())) {
/* 113 */         flags = (byte)(flags | 0x2);
/*     */       }
/* 115 */       frame.writeByte(flags);
/* 116 */       frame.writeMedium(id.intValue());
/* 117 */       frame.writeInt(spdySettingsFrame.getValue(id.intValue()));
/*     */     } 
/* 119 */     return frame;
/*     */   }
/*     */   
/*     */   public ByteBuf encodePingFrame(ByteBufAllocator allocator, int id) {
/* 123 */     byte flags = 0;
/* 124 */     int length = 4;
/* 125 */     ByteBuf frame = allocator.ioBuffer(8 + length).order(ByteOrder.BIG_ENDIAN);
/* 126 */     writeControlFrameHeader(frame, 6, flags, length);
/* 127 */     frame.writeInt(id);
/* 128 */     return frame;
/*     */   }
/*     */   
/*     */   public ByteBuf encodeGoAwayFrame(ByteBufAllocator allocator, int lastGoodStreamId, int statusCode) {
/* 132 */     byte flags = 0;
/* 133 */     int length = 8;
/* 134 */     ByteBuf frame = allocator.ioBuffer(8 + length).order(ByteOrder.BIG_ENDIAN);
/* 135 */     writeControlFrameHeader(frame, 7, flags, length);
/* 136 */     frame.writeInt(lastGoodStreamId);
/* 137 */     frame.writeInt(statusCode);
/* 138 */     return frame;
/*     */   }
/*     */   
/*     */   public ByteBuf encodeHeadersFrame(ByteBufAllocator allocator, int streamId, boolean last, ByteBuf headerBlock) {
/* 142 */     int headerBlockLength = headerBlock.readableBytes();
/* 143 */     byte flags = last ? 1 : 0;
/* 144 */     int length = 4 + headerBlockLength;
/* 145 */     ByteBuf frame = allocator.ioBuffer(8 + length).order(ByteOrder.BIG_ENDIAN);
/* 146 */     writeControlFrameHeader(frame, 8, flags, length);
/* 147 */     frame.writeInt(streamId);
/* 148 */     frame.writeBytes(headerBlock, headerBlock.readerIndex(), headerBlockLength);
/* 149 */     return frame;
/*     */   }
/*     */   
/*     */   public ByteBuf encodeWindowUpdateFrame(ByteBufAllocator allocator, int streamId, int deltaWindowSize) {
/* 153 */     byte flags = 0;
/* 154 */     int length = 8;
/* 155 */     ByteBuf frame = allocator.ioBuffer(8 + length).order(ByteOrder.BIG_ENDIAN);
/* 156 */     writeControlFrameHeader(frame, 9, flags, length);
/* 157 */     frame.writeInt(streamId);
/* 158 */     frame.writeInt(deltaWindowSize);
/* 159 */     return frame;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdyFrameEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */