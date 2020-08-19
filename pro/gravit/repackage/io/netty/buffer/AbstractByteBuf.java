/*      */ package pro.gravit.repackage.io.netty.buffer;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.GatheringByteChannel;
/*      */ import java.nio.channels.ScatteringByteChannel;
/*      */ import java.nio.charset.Charset;
/*      */ import pro.gravit.repackage.io.netty.util.AsciiString;
/*      */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
/*      */ import pro.gravit.repackage.io.netty.util.CharsetUtil;
/*      */ import pro.gravit.repackage.io.netty.util.IllegalReferenceCountException;
/*      */ import pro.gravit.repackage.io.netty.util.ResourceLeakDetector;
/*      */ import pro.gravit.repackage.io.netty.util.ResourceLeakDetectorFactory;
/*      */ import pro.gravit.repackage.io.netty.util.internal.MathUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*      */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.SystemPropertyUtil;
/*      */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*      */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class AbstractByteBuf
/*      */   extends ByteBuf
/*      */ {
/*   48 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractByteBuf.class);
/*      */   
/*      */   private static final String LEGACY_PROP_CHECK_ACCESSIBLE = "pro.gravit.repackage.io.netty.buffer.bytebuf.checkAccessible";
/*      */   private static final String PROP_CHECK_ACCESSIBLE = "pro.gravit.repackage.io.netty.buffer.checkAccessible";
/*      */   static final boolean checkAccessible;
/*      */   private static final String PROP_CHECK_BOUNDS = "pro.gravit.repackage.io.netty.buffer.checkBounds";
/*      */   
/*      */   static {
/*   56 */     if (SystemPropertyUtil.contains("pro.gravit.repackage.io.netty.buffer.checkAccessible")) {
/*   57 */       checkAccessible = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.buffer.checkAccessible", true);
/*      */     } else {
/*   59 */       checkAccessible = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.buffer.bytebuf.checkAccessible", true);
/*      */     } 
/*   61 */   } private static final boolean checkBounds = SystemPropertyUtil.getBoolean("pro.gravit.repackage.io.netty.buffer.checkBounds", true); static {
/*   62 */     if (logger.isDebugEnabled()) {
/*   63 */       logger.debug("-D{}: {}", "pro.gravit.repackage.io.netty.buffer.checkAccessible", Boolean.valueOf(checkAccessible));
/*   64 */       logger.debug("-D{}: {}", "pro.gravit.repackage.io.netty.buffer.checkBounds", Boolean.valueOf(checkBounds));
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*   69 */   static final ResourceLeakDetector<ByteBuf> leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(ByteBuf.class);
/*      */   
/*      */   int readerIndex;
/*      */   int writerIndex;
/*      */   private int markedReaderIndex;
/*      */   private int markedWriterIndex;
/*      */   private int maxCapacity;
/*      */   
/*      */   protected AbstractByteBuf(int maxCapacity) {
/*   78 */     ObjectUtil.checkPositiveOrZero(maxCapacity, "maxCapacity");
/*   79 */     this.maxCapacity = maxCapacity;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadOnly() {
/*   84 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf asReadOnly() {
/*   90 */     if (isReadOnly()) {
/*   91 */       return this;
/*      */     }
/*   93 */     return Unpooled.unmodifiableBuffer(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public int maxCapacity() {
/*   98 */     return this.maxCapacity;
/*      */   }
/*      */   
/*      */   protected final void maxCapacity(int maxCapacity) {
/*  102 */     this.maxCapacity = maxCapacity;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readerIndex() {
/*  107 */     return this.readerIndex;
/*      */   }
/*      */   
/*      */   private static void checkIndexBounds(int readerIndex, int writerIndex, int capacity) {
/*  111 */     if (readerIndex < 0 || readerIndex > writerIndex || writerIndex > capacity) {
/*  112 */       throw new IndexOutOfBoundsException(String.format("readerIndex: %d, writerIndex: %d (expected: 0 <= readerIndex <= writerIndex <= capacity(%d))", new Object[] {
/*      */               
/*  114 */               Integer.valueOf(readerIndex), Integer.valueOf(writerIndex), Integer.valueOf(capacity)
/*      */             }));
/*      */     }
/*      */   }
/*      */   
/*      */   public ByteBuf readerIndex(int readerIndex) {
/*  120 */     if (checkBounds) {
/*  121 */       checkIndexBounds(readerIndex, this.writerIndex, capacity());
/*      */     }
/*  123 */     this.readerIndex = readerIndex;
/*  124 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int writerIndex() {
/*  129 */     return this.writerIndex;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writerIndex(int writerIndex) {
/*  134 */     if (checkBounds) {
/*  135 */       checkIndexBounds(this.readerIndex, writerIndex, capacity());
/*      */     }
/*  137 */     this.writerIndex = writerIndex;
/*  138 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setIndex(int readerIndex, int writerIndex) {
/*  143 */     if (checkBounds) {
/*  144 */       checkIndexBounds(readerIndex, writerIndex, capacity());
/*      */     }
/*  146 */     setIndex0(readerIndex, writerIndex);
/*  147 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf clear() {
/*  152 */     this.readerIndex = this.writerIndex = 0;
/*  153 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadable() {
/*  158 */     return (this.writerIndex > this.readerIndex);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isReadable(int numBytes) {
/*  163 */     return (this.writerIndex - this.readerIndex >= numBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isWritable() {
/*  168 */     return (capacity() > this.writerIndex);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isWritable(int numBytes) {
/*  173 */     return (capacity() - this.writerIndex >= numBytes);
/*      */   }
/*      */ 
/*      */   
/*      */   public int readableBytes() {
/*  178 */     return this.writerIndex - this.readerIndex;
/*      */   }
/*      */ 
/*      */   
/*      */   public int writableBytes() {
/*  183 */     return capacity() - this.writerIndex;
/*      */   }
/*      */ 
/*      */   
/*      */   public int maxWritableBytes() {
/*  188 */     return maxCapacity() - this.writerIndex;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf markReaderIndex() {
/*  193 */     this.markedReaderIndex = this.readerIndex;
/*  194 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf resetReaderIndex() {
/*  199 */     readerIndex(this.markedReaderIndex);
/*  200 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf markWriterIndex() {
/*  205 */     this.markedWriterIndex = this.writerIndex;
/*  206 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf resetWriterIndex() {
/*  211 */     writerIndex(this.markedWriterIndex);
/*  212 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf discardReadBytes() {
/*  217 */     if (this.readerIndex == 0) {
/*  218 */       ensureAccessible();
/*  219 */       return this;
/*      */     } 
/*      */     
/*  222 */     if (this.readerIndex != this.writerIndex) {
/*  223 */       setBytes(0, this, this.readerIndex, this.writerIndex - this.readerIndex);
/*  224 */       this.writerIndex -= this.readerIndex;
/*  225 */       adjustMarkers(this.readerIndex);
/*  226 */       this.readerIndex = 0;
/*      */     } else {
/*  228 */       ensureAccessible();
/*  229 */       adjustMarkers(this.readerIndex);
/*  230 */       this.writerIndex = this.readerIndex = 0;
/*      */     } 
/*  232 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf discardSomeReadBytes() {
/*  237 */     if (this.readerIndex > 0) {
/*  238 */       if (this.readerIndex == this.writerIndex) {
/*  239 */         ensureAccessible();
/*  240 */         adjustMarkers(this.readerIndex);
/*  241 */         this.writerIndex = this.readerIndex = 0;
/*  242 */         return this;
/*      */       } 
/*      */       
/*  245 */       if (this.readerIndex >= capacity() >>> 1) {
/*  246 */         setBytes(0, this, this.readerIndex, this.writerIndex - this.readerIndex);
/*  247 */         this.writerIndex -= this.readerIndex;
/*  248 */         adjustMarkers(this.readerIndex);
/*  249 */         this.readerIndex = 0;
/*  250 */         return this;
/*      */       } 
/*      */     } 
/*  253 */     ensureAccessible();
/*  254 */     return this;
/*      */   }
/*      */   
/*      */   protected final void adjustMarkers(int decrement) {
/*  258 */     int markedReaderIndex = this.markedReaderIndex;
/*  259 */     if (markedReaderIndex <= decrement) {
/*  260 */       this.markedReaderIndex = 0;
/*  261 */       int markedWriterIndex = this.markedWriterIndex;
/*  262 */       if (markedWriterIndex <= decrement) {
/*  263 */         this.markedWriterIndex = 0;
/*      */       } else {
/*  265 */         this.markedWriterIndex = markedWriterIndex - decrement;
/*      */       } 
/*      */     } else {
/*  268 */       this.markedReaderIndex = markedReaderIndex - decrement;
/*  269 */       this.markedWriterIndex -= decrement;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   protected final void trimIndicesToCapacity(int newCapacity) {
/*  275 */     if (writerIndex() > newCapacity) {
/*  276 */       setIndex0(Math.min(readerIndex(), newCapacity), newCapacity);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf ensureWritable(int minWritableBytes) {
/*  282 */     ensureWritable0(ObjectUtil.checkPositiveOrZero(minWritableBytes, "minWritableBytes"));
/*  283 */     return this;
/*      */   }
/*      */   
/*      */   final void ensureWritable0(int minWritableBytes) {
/*  287 */     int writerIndex = writerIndex();
/*  288 */     int targetCapacity = writerIndex + minWritableBytes;
/*  289 */     if (targetCapacity <= capacity()) {
/*  290 */       ensureAccessible();
/*      */       return;
/*      */     } 
/*  293 */     if (checkBounds && targetCapacity > this.maxCapacity) {
/*  294 */       ensureAccessible();
/*  295 */       throw new IndexOutOfBoundsException(String.format("writerIndex(%d) + minWritableBytes(%d) exceeds maxCapacity(%d): %s", new Object[] {
/*      */               
/*  297 */               Integer.valueOf(writerIndex), Integer.valueOf(minWritableBytes), Integer.valueOf(this.maxCapacity), this
/*      */             }));
/*      */     } 
/*      */     
/*  301 */     int fastWritable = maxFastWritableBytes();
/*      */     
/*  303 */     int newCapacity = (fastWritable >= minWritableBytes) ? (writerIndex + fastWritable) : alloc().calculateNewCapacity(targetCapacity, this.maxCapacity);
/*      */ 
/*      */     
/*  306 */     capacity(newCapacity);
/*      */   }
/*      */ 
/*      */   
/*      */   public int ensureWritable(int minWritableBytes, boolean force) {
/*  311 */     ensureAccessible();
/*  312 */     ObjectUtil.checkPositiveOrZero(minWritableBytes, "minWritableBytes");
/*      */     
/*  314 */     if (minWritableBytes <= writableBytes()) {
/*  315 */       return 0;
/*      */     }
/*      */     
/*  318 */     int maxCapacity = maxCapacity();
/*  319 */     int writerIndex = writerIndex();
/*  320 */     if (minWritableBytes > maxCapacity - writerIndex) {
/*  321 */       if (!force || capacity() == maxCapacity) {
/*  322 */         return 1;
/*      */       }
/*      */       
/*  325 */       capacity(maxCapacity);
/*  326 */       return 3;
/*      */     } 
/*      */     
/*  329 */     int fastWritable = maxFastWritableBytes();
/*      */     
/*  331 */     int newCapacity = (fastWritable >= minWritableBytes) ? (writerIndex + fastWritable) : alloc().calculateNewCapacity(writerIndex + minWritableBytes, maxCapacity);
/*      */ 
/*      */     
/*  334 */     capacity(newCapacity);
/*  335 */     return 2;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf order(ByteOrder endianness) {
/*  340 */     if (endianness == order()) {
/*  341 */       return this;
/*      */     }
/*  343 */     ObjectUtil.checkNotNull(endianness, "endianness");
/*  344 */     return newSwappedByteBuf();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected SwappedByteBuf newSwappedByteBuf() {
/*  351 */     return new SwappedByteBuf(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public byte getByte(int index) {
/*  356 */     checkIndex(index);
/*  357 */     return _getByte(index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getBoolean(int index) {
/*  364 */     return (getByte(index) != 0);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getUnsignedByte(int index) {
/*  369 */     return (short)(getByte(index) & 0xFF);
/*      */   }
/*      */ 
/*      */   
/*      */   public short getShort(int index) {
/*  374 */     checkIndex(index, 2);
/*  375 */     return _getShort(index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public short getShortLE(int index) {
/*  382 */     checkIndex(index, 2);
/*  383 */     return _getShortLE(index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getUnsignedShort(int index) {
/*  390 */     return getShort(index) & 0xFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedShortLE(int index) {
/*  395 */     return getShortLE(index) & 0xFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getUnsignedMedium(int index) {
/*  400 */     checkIndex(index, 3);
/*  401 */     return _getUnsignedMedium(index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getUnsignedMediumLE(int index) {
/*  408 */     checkIndex(index, 3);
/*  409 */     return _getUnsignedMediumLE(index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMedium(int index) {
/*  416 */     int value = getUnsignedMedium(index);
/*  417 */     if ((value & 0x800000) != 0) {
/*  418 */       value |= 0xFF000000;
/*      */     }
/*  420 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getMediumLE(int index) {
/*  425 */     int value = getUnsignedMediumLE(index);
/*  426 */     if ((value & 0x800000) != 0) {
/*  427 */       value |= 0xFF000000;
/*      */     }
/*  429 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getInt(int index) {
/*  434 */     checkIndex(index, 4);
/*  435 */     return _getInt(index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getIntLE(int index) {
/*  442 */     checkIndex(index, 4);
/*  443 */     return _getIntLE(index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getUnsignedInt(int index) {
/*  450 */     return getInt(index) & 0xFFFFFFFFL;
/*      */   }
/*      */ 
/*      */   
/*      */   public long getUnsignedIntLE(int index) {
/*  455 */     return getIntLE(index) & 0xFFFFFFFFL;
/*      */   }
/*      */ 
/*      */   
/*      */   public long getLong(int index) {
/*  460 */     checkIndex(index, 8);
/*  461 */     return _getLong(index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getLongLE(int index) {
/*  468 */     checkIndex(index, 8);
/*  469 */     return _getLongLE(index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public char getChar(int index) {
/*  476 */     return (char)getShort(index);
/*      */   }
/*      */ 
/*      */   
/*      */   public float getFloat(int index) {
/*  481 */     return Float.intBitsToFloat(getInt(index));
/*      */   }
/*      */ 
/*      */   
/*      */   public double getDouble(int index) {
/*  486 */     return Double.longBitsToDouble(getLong(index));
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, byte[] dst) {
/*  491 */     getBytes(index, dst, 0, dst.length);
/*  492 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, ByteBuf dst) {
/*  497 */     getBytes(index, dst, dst.writableBytes());
/*  498 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf getBytes(int index, ByteBuf dst, int length) {
/*  503 */     getBytes(index, dst, dst.writerIndex(), length);
/*  504 */     dst.writerIndex(dst.writerIndex() + length);
/*  505 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public CharSequence getCharSequence(int index, int length, Charset charset) {
/*  510 */     if (CharsetUtil.US_ASCII.equals(charset) || CharsetUtil.ISO_8859_1.equals(charset))
/*      */     {
/*  512 */       return (CharSequence)new AsciiString(ByteBufUtil.getBytes(this, index, length, true), false);
/*      */     }
/*  514 */     return toString(index, length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public CharSequence readCharSequence(int length, Charset charset) {
/*  519 */     CharSequence sequence = getCharSequence(this.readerIndex, length, charset);
/*  520 */     this.readerIndex += length;
/*  521 */     return sequence;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setByte(int index, int value) {
/*  526 */     checkIndex(index);
/*  527 */     _setByte(index, value);
/*  528 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf setBoolean(int index, boolean value) {
/*  535 */     setByte(index, value ? 1 : 0);
/*  536 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setShort(int index, int value) {
/*  541 */     checkIndex(index, 2);
/*  542 */     _setShort(index, value);
/*  543 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf setShortLE(int index, int value) {
/*  550 */     checkIndex(index, 2);
/*  551 */     _setShortLE(index, value);
/*  552 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf setChar(int index, int value) {
/*  559 */     setShort(index, value);
/*  560 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setMedium(int index, int value) {
/*  565 */     checkIndex(index, 3);
/*  566 */     _setMedium(index, value);
/*  567 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf setMediumLE(int index, int value) {
/*  574 */     checkIndex(index, 3);
/*  575 */     _setMediumLE(index, value);
/*  576 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf setInt(int index, int value) {
/*  583 */     checkIndex(index, 4);
/*  584 */     _setInt(index, value);
/*  585 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf setIntLE(int index, int value) {
/*  592 */     checkIndex(index, 4);
/*  593 */     _setIntLE(index, value);
/*  594 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf setFloat(int index, float value) {
/*  601 */     setInt(index, Float.floatToRawIntBits(value));
/*  602 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setLong(int index, long value) {
/*  607 */     checkIndex(index, 8);
/*  608 */     _setLong(index, value);
/*  609 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf setLongLE(int index, long value) {
/*  616 */     checkIndex(index, 8);
/*  617 */     _setLongLE(index, value);
/*  618 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteBuf setDouble(int index, double value) {
/*  625 */     setLong(index, Double.doubleToRawLongBits(value));
/*  626 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, byte[] src) {
/*  631 */     setBytes(index, src, 0, src.length);
/*  632 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setBytes(int index, ByteBuf src) {
/*  637 */     setBytes(index, src, src.readableBytes());
/*  638 */     return this;
/*      */   }
/*      */   
/*      */   private static void checkReadableBounds(ByteBuf src, int length) {
/*  642 */     if (length > src.readableBytes()) {
/*  643 */       throw new IndexOutOfBoundsException(String.format("length(%d) exceeds src.readableBytes(%d) where src is: %s", new Object[] {
/*  644 */               Integer.valueOf(length), Integer.valueOf(src.readableBytes()), src
/*      */             }));
/*      */     }
/*      */   }
/*      */   
/*      */   public ByteBuf setBytes(int index, ByteBuf src, int length) {
/*  650 */     checkIndex(index, length);
/*  651 */     ObjectUtil.checkNotNull(src, "src");
/*  652 */     if (checkBounds) {
/*  653 */       checkReadableBounds(src, length);
/*      */     }
/*      */     
/*  656 */     setBytes(index, src, src.readerIndex(), length);
/*  657 */     src.readerIndex(src.readerIndex() + length);
/*  658 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf setZero(int index, int length) {
/*  663 */     if (length == 0) {
/*  664 */       return this;
/*      */     }
/*      */     
/*  667 */     checkIndex(index, length);
/*      */     
/*  669 */     int nLong = length >>> 3;
/*  670 */     int nBytes = length & 0x7; int i;
/*  671 */     for (i = nLong; i > 0; i--) {
/*  672 */       _setLong(index, 0L);
/*  673 */       index += 8;
/*      */     } 
/*  675 */     if (nBytes == 4) {
/*  676 */       _setInt(index, 0);
/*      */     }
/*  678 */     else if (nBytes < 4) {
/*  679 */       for (i = nBytes; i > 0; i--) {
/*  680 */         _setByte(index, 0);
/*  681 */         index++;
/*      */       } 
/*      */     } else {
/*  684 */       _setInt(index, 0);
/*  685 */       index += 4;
/*  686 */       for (i = nBytes - 4; i > 0; i--) {
/*  687 */         _setByte(index, 0);
/*  688 */         index++;
/*      */       } 
/*      */     } 
/*  691 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int setCharSequence(int index, CharSequence sequence, Charset charset) {
/*  696 */     return setCharSequence0(index, sequence, charset, false);
/*      */   }
/*      */   
/*      */   private int setCharSequence0(int index, CharSequence sequence, Charset charset, boolean expand) {
/*  700 */     if (charset.equals(CharsetUtil.UTF_8)) {
/*  701 */       int length = ByteBufUtil.utf8MaxBytes(sequence);
/*  702 */       if (expand) {
/*  703 */         ensureWritable0(length);
/*  704 */         checkIndex0(index, length);
/*      */       } else {
/*  706 */         checkIndex(index, length);
/*      */       } 
/*  708 */       return ByteBufUtil.writeUtf8(this, index, sequence, sequence.length());
/*      */     } 
/*  710 */     if (charset.equals(CharsetUtil.US_ASCII) || charset.equals(CharsetUtil.ISO_8859_1)) {
/*  711 */       int length = sequence.length();
/*  712 */       if (expand) {
/*  713 */         ensureWritable0(length);
/*  714 */         checkIndex0(index, length);
/*      */       } else {
/*  716 */         checkIndex(index, length);
/*      */       } 
/*  718 */       return ByteBufUtil.writeAscii(this, index, sequence, length);
/*      */     } 
/*  720 */     byte[] bytes = sequence.toString().getBytes(charset);
/*  721 */     if (expand) {
/*  722 */       ensureWritable0(bytes.length);
/*      */     }
/*      */     
/*  725 */     setBytes(index, bytes);
/*  726 */     return bytes.length;
/*      */   }
/*      */ 
/*      */   
/*      */   public byte readByte() {
/*  731 */     checkReadableBytes0(1);
/*  732 */     int i = this.readerIndex;
/*  733 */     byte b = _getByte(i);
/*  734 */     this.readerIndex = i + 1;
/*  735 */     return b;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean readBoolean() {
/*  740 */     return (readByte() != 0);
/*      */   }
/*      */ 
/*      */   
/*      */   public short readUnsignedByte() {
/*  745 */     return (short)(readByte() & 0xFF);
/*      */   }
/*      */ 
/*      */   
/*      */   public short readShort() {
/*  750 */     checkReadableBytes0(2);
/*  751 */     short v = _getShort(this.readerIndex);
/*  752 */     this.readerIndex += 2;
/*  753 */     return v;
/*      */   }
/*      */ 
/*      */   
/*      */   public short readShortLE() {
/*  758 */     checkReadableBytes0(2);
/*  759 */     short v = _getShortLE(this.readerIndex);
/*  760 */     this.readerIndex += 2;
/*  761 */     return v;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedShort() {
/*  766 */     return readShort() & 0xFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedShortLE() {
/*  771 */     return readShortLE() & 0xFFFF;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readMedium() {
/*  776 */     int value = readUnsignedMedium();
/*  777 */     if ((value & 0x800000) != 0) {
/*  778 */       value |= 0xFF000000;
/*      */     }
/*  780 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readMediumLE() {
/*  785 */     int value = readUnsignedMediumLE();
/*  786 */     if ((value & 0x800000) != 0) {
/*  787 */       value |= 0xFF000000;
/*      */     }
/*  789 */     return value;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedMedium() {
/*  794 */     checkReadableBytes0(3);
/*  795 */     int v = _getUnsignedMedium(this.readerIndex);
/*  796 */     this.readerIndex += 3;
/*  797 */     return v;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readUnsignedMediumLE() {
/*  802 */     checkReadableBytes0(3);
/*  803 */     int v = _getUnsignedMediumLE(this.readerIndex);
/*  804 */     this.readerIndex += 3;
/*  805 */     return v;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readInt() {
/*  810 */     checkReadableBytes0(4);
/*  811 */     int v = _getInt(this.readerIndex);
/*  812 */     this.readerIndex += 4;
/*  813 */     return v;
/*      */   }
/*      */ 
/*      */   
/*      */   public int readIntLE() {
/*  818 */     checkReadableBytes0(4);
/*  819 */     int v = _getIntLE(this.readerIndex);
/*  820 */     this.readerIndex += 4;
/*  821 */     return v;
/*      */   }
/*      */ 
/*      */   
/*      */   public long readUnsignedInt() {
/*  826 */     return readInt() & 0xFFFFFFFFL;
/*      */   }
/*      */ 
/*      */   
/*      */   public long readUnsignedIntLE() {
/*  831 */     return readIntLE() & 0xFFFFFFFFL;
/*      */   }
/*      */ 
/*      */   
/*      */   public long readLong() {
/*  836 */     checkReadableBytes0(8);
/*  837 */     long v = _getLong(this.readerIndex);
/*  838 */     this.readerIndex += 8;
/*  839 */     return v;
/*      */   }
/*      */ 
/*      */   
/*      */   public long readLongLE() {
/*  844 */     checkReadableBytes0(8);
/*  845 */     long v = _getLongLE(this.readerIndex);
/*  846 */     this.readerIndex += 8;
/*  847 */     return v;
/*      */   }
/*      */ 
/*      */   
/*      */   public char readChar() {
/*  852 */     return (char)readShort();
/*      */   }
/*      */ 
/*      */   
/*      */   public float readFloat() {
/*  857 */     return Float.intBitsToFloat(readInt());
/*      */   }
/*      */ 
/*      */   
/*      */   public double readDouble() {
/*  862 */     return Double.longBitsToDouble(readLong());
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(int length) {
/*  867 */     checkReadableBytes(length);
/*  868 */     if (length == 0) {
/*  869 */       return Unpooled.EMPTY_BUFFER;
/*      */     }
/*      */     
/*  872 */     ByteBuf buf = alloc().buffer(length, this.maxCapacity);
/*  873 */     buf.writeBytes(this, this.readerIndex, length);
/*  874 */     this.readerIndex += length;
/*  875 */     return buf;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readSlice(int length) {
/*  880 */     checkReadableBytes(length);
/*  881 */     ByteBuf slice = slice(this.readerIndex, length);
/*  882 */     this.readerIndex += length;
/*  883 */     return slice;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readRetainedSlice(int length) {
/*  888 */     checkReadableBytes(length);
/*  889 */     ByteBuf slice = retainedSlice(this.readerIndex, length);
/*  890 */     this.readerIndex += length;
/*  891 */     return slice;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(byte[] dst, int dstIndex, int length) {
/*  896 */     checkReadableBytes(length);
/*  897 */     getBytes(this.readerIndex, dst, dstIndex, length);
/*  898 */     this.readerIndex += length;
/*  899 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(byte[] dst) {
/*  904 */     readBytes(dst, 0, dst.length);
/*  905 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuf dst) {
/*  910 */     readBytes(dst, dst.writableBytes());
/*  911 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuf dst, int length) {
/*  916 */     if (checkBounds && 
/*  917 */       length > dst.writableBytes()) {
/*  918 */       throw new IndexOutOfBoundsException(String.format("length(%d) exceeds dst.writableBytes(%d) where dst is: %s", new Object[] {
/*  919 */               Integer.valueOf(length), Integer.valueOf(dst.writableBytes()), dst
/*      */             }));
/*      */     }
/*  922 */     readBytes(dst, dst.writerIndex(), length);
/*  923 */     dst.writerIndex(dst.writerIndex() + length);
/*  924 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
/*  929 */     checkReadableBytes(length);
/*  930 */     getBytes(this.readerIndex, dst, dstIndex, length);
/*  931 */     this.readerIndex += length;
/*  932 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(ByteBuffer dst) {
/*  937 */     int length = dst.remaining();
/*  938 */     checkReadableBytes(length);
/*  939 */     getBytes(this.readerIndex, dst);
/*  940 */     this.readerIndex += length;
/*  941 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int readBytes(GatheringByteChannel out, int length) throws IOException {
/*  947 */     checkReadableBytes(length);
/*  948 */     int readBytes = getBytes(this.readerIndex, out, length);
/*  949 */     this.readerIndex += readBytes;
/*  950 */     return readBytes;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int readBytes(FileChannel out, long position, int length) throws IOException {
/*  956 */     checkReadableBytes(length);
/*  957 */     int readBytes = getBytes(this.readerIndex, out, position, length);
/*  958 */     this.readerIndex += readBytes;
/*  959 */     return readBytes;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf readBytes(OutputStream out, int length) throws IOException {
/*  964 */     checkReadableBytes(length);
/*  965 */     getBytes(this.readerIndex, out, length);
/*  966 */     this.readerIndex += length;
/*  967 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf skipBytes(int length) {
/*  972 */     checkReadableBytes(length);
/*  973 */     this.readerIndex += length;
/*  974 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBoolean(boolean value) {
/*  979 */     writeByte(value ? 1 : 0);
/*  980 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeByte(int value) {
/*  985 */     ensureWritable0(1);
/*  986 */     _setByte(this.writerIndex++, value);
/*  987 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeShort(int value) {
/*  992 */     ensureWritable0(2);
/*  993 */     _setShort(this.writerIndex, value);
/*  994 */     this.writerIndex += 2;
/*  995 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeShortLE(int value) {
/* 1000 */     ensureWritable0(2);
/* 1001 */     _setShortLE(this.writerIndex, value);
/* 1002 */     this.writerIndex += 2;
/* 1003 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeMedium(int value) {
/* 1008 */     ensureWritable0(3);
/* 1009 */     _setMedium(this.writerIndex, value);
/* 1010 */     this.writerIndex += 3;
/* 1011 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeMediumLE(int value) {
/* 1016 */     ensureWritable0(3);
/* 1017 */     _setMediumLE(this.writerIndex, value);
/* 1018 */     this.writerIndex += 3;
/* 1019 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeInt(int value) {
/* 1024 */     ensureWritable0(4);
/* 1025 */     _setInt(this.writerIndex, value);
/* 1026 */     this.writerIndex += 4;
/* 1027 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeIntLE(int value) {
/* 1032 */     ensureWritable0(4);
/* 1033 */     _setIntLE(this.writerIndex, value);
/* 1034 */     this.writerIndex += 4;
/* 1035 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeLong(long value) {
/* 1040 */     ensureWritable0(8);
/* 1041 */     _setLong(this.writerIndex, value);
/* 1042 */     this.writerIndex += 8;
/* 1043 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeLongLE(long value) {
/* 1048 */     ensureWritable0(8);
/* 1049 */     _setLongLE(this.writerIndex, value);
/* 1050 */     this.writerIndex += 8;
/* 1051 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeChar(int value) {
/* 1056 */     writeShort(value);
/* 1057 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeFloat(float value) {
/* 1062 */     writeInt(Float.floatToRawIntBits(value));
/* 1063 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeDouble(double value) {
/* 1068 */     writeLong(Double.doubleToRawLongBits(value));
/* 1069 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(byte[] src, int srcIndex, int length) {
/* 1074 */     ensureWritable(length);
/* 1075 */     setBytes(this.writerIndex, src, srcIndex, length);
/* 1076 */     this.writerIndex += length;
/* 1077 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(byte[] src) {
/* 1082 */     writeBytes(src, 0, src.length);
/* 1083 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuf src) {
/* 1088 */     writeBytes(src, src.readableBytes());
/* 1089 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuf src, int length) {
/* 1094 */     if (checkBounds) {
/* 1095 */       checkReadableBounds(src, length);
/*      */     }
/* 1097 */     writeBytes(src, src.readerIndex(), length);
/* 1098 */     src.readerIndex(src.readerIndex() + length);
/* 1099 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
/* 1104 */     ensureWritable(length);
/* 1105 */     setBytes(this.writerIndex, src, srcIndex, length);
/* 1106 */     this.writerIndex += length;
/* 1107 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeBytes(ByteBuffer src) {
/* 1112 */     int length = src.remaining();
/* 1113 */     ensureWritable0(length);
/* 1114 */     setBytes(this.writerIndex, src);
/* 1115 */     this.writerIndex += length;
/* 1116 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int writeBytes(InputStream in, int length) throws IOException {
/* 1122 */     ensureWritable(length);
/* 1123 */     int writtenBytes = setBytes(this.writerIndex, in, length);
/* 1124 */     if (writtenBytes > 0) {
/* 1125 */       this.writerIndex += writtenBytes;
/*      */     }
/* 1127 */     return writtenBytes;
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
/* 1132 */     ensureWritable(length);
/* 1133 */     int writtenBytes = setBytes(this.writerIndex, in, length);
/* 1134 */     if (writtenBytes > 0) {
/* 1135 */       this.writerIndex += writtenBytes;
/*      */     }
/* 1137 */     return writtenBytes;
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeBytes(FileChannel in, long position, int length) throws IOException {
/* 1142 */     ensureWritable(length);
/* 1143 */     int writtenBytes = setBytes(this.writerIndex, in, position, length);
/* 1144 */     if (writtenBytes > 0) {
/* 1145 */       this.writerIndex += writtenBytes;
/*      */     }
/* 1147 */     return writtenBytes;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf writeZero(int length) {
/* 1152 */     if (length == 0) {
/* 1153 */       return this;
/*      */     }
/*      */     
/* 1156 */     ensureWritable(length);
/* 1157 */     int wIndex = this.writerIndex;
/* 1158 */     checkIndex0(wIndex, length);
/*      */     
/* 1160 */     int nLong = length >>> 3;
/* 1161 */     int nBytes = length & 0x7; int i;
/* 1162 */     for (i = nLong; i > 0; i--) {
/* 1163 */       _setLong(wIndex, 0L);
/* 1164 */       wIndex += 8;
/*      */     } 
/* 1166 */     if (nBytes == 4) {
/* 1167 */       _setInt(wIndex, 0);
/* 1168 */       wIndex += 4;
/* 1169 */     } else if (nBytes < 4) {
/* 1170 */       for (i = nBytes; i > 0; i--) {
/* 1171 */         _setByte(wIndex, 0);
/* 1172 */         wIndex++;
/*      */       } 
/*      */     } else {
/* 1175 */       _setInt(wIndex, 0);
/* 1176 */       wIndex += 4;
/* 1177 */       for (i = nBytes - 4; i > 0; i--) {
/* 1178 */         _setByte(wIndex, 0);
/* 1179 */         wIndex++;
/*      */       } 
/*      */     } 
/* 1182 */     this.writerIndex = wIndex;
/* 1183 */     return this;
/*      */   }
/*      */ 
/*      */   
/*      */   public int writeCharSequence(CharSequence sequence, Charset charset) {
/* 1188 */     int written = setCharSequence0(this.writerIndex, sequence, charset, true);
/* 1189 */     this.writerIndex += written;
/* 1190 */     return written;
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf copy() {
/* 1195 */     return copy(this.readerIndex, readableBytes());
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf duplicate() {
/* 1200 */     ensureAccessible();
/* 1201 */     return new UnpooledDuplicatedByteBuf(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedDuplicate() {
/* 1206 */     return duplicate().retain();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf slice() {
/* 1211 */     return slice(this.readerIndex, readableBytes());
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedSlice() {
/* 1216 */     return slice().retain();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf slice(int index, int length) {
/* 1221 */     ensureAccessible();
/* 1222 */     return new UnpooledSlicedByteBuf(this, index, length);
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuf retainedSlice(int index, int length) {
/* 1227 */     return slice(index, length).retain();
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer nioBuffer() {
/* 1232 */     return nioBuffer(this.readerIndex, readableBytes());
/*      */   }
/*      */ 
/*      */   
/*      */   public ByteBuffer[] nioBuffers() {
/* 1237 */     return nioBuffers(this.readerIndex, readableBytes());
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString(Charset charset) {
/* 1242 */     return toString(this.readerIndex, readableBytes(), charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString(int index, int length, Charset charset) {
/* 1247 */     return ByteBufUtil.decodeString(this, index, length, charset);
/*      */   }
/*      */ 
/*      */   
/*      */   public int indexOf(int fromIndex, int toIndex, byte value) {
/* 1252 */     if (fromIndex <= toIndex) {
/* 1253 */       return firstIndexOf(fromIndex, toIndex, value);
/*      */     }
/* 1255 */     return lastIndexOf(fromIndex, toIndex, value);
/*      */   }
/*      */ 
/*      */   
/*      */   private int firstIndexOf(int fromIndex, int toIndex, byte value) {
/* 1260 */     fromIndex = Math.max(fromIndex, 0);
/* 1261 */     if (fromIndex >= toIndex || capacity() == 0) {
/* 1262 */       return -1;
/*      */     }
/* 1264 */     checkIndex(fromIndex, toIndex - fromIndex);
/*      */     
/* 1266 */     for (int i = fromIndex; i < toIndex; i++) {
/* 1267 */       if (_getByte(i) == value) {
/* 1268 */         return i;
/*      */       }
/*      */     } 
/*      */     
/* 1272 */     return -1;
/*      */   }
/*      */   
/*      */   private int lastIndexOf(int fromIndex, int toIndex, byte value) {
/* 1276 */     fromIndex = Math.min(fromIndex, capacity());
/* 1277 */     if (fromIndex < 0 || capacity() == 0) {
/* 1278 */       return -1;
/*      */     }
/*      */     
/* 1281 */     checkIndex(toIndex, fromIndex - toIndex);
/*      */     
/* 1283 */     for (int i = fromIndex - 1; i >= toIndex; i--) {
/* 1284 */       if (_getByte(i) == value) {
/* 1285 */         return i;
/*      */       }
/*      */     } 
/*      */     
/* 1289 */     return -1;
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(byte value) {
/* 1294 */     return bytesBefore(readerIndex(), readableBytes(), value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(int length, byte value) {
/* 1299 */     checkReadableBytes(length);
/* 1300 */     return bytesBefore(readerIndex(), length, value);
/*      */   }
/*      */ 
/*      */   
/*      */   public int bytesBefore(int index, int length, byte value) {
/* 1305 */     int endIndex = indexOf(index, index + length, value);
/* 1306 */     if (endIndex < 0) {
/* 1307 */       return -1;
/*      */     }
/* 1309 */     return endIndex - index;
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByte(ByteProcessor processor) {
/* 1314 */     ensureAccessible();
/*      */     try {
/* 1316 */       return forEachByteAsc0(this.readerIndex, this.writerIndex, processor);
/* 1317 */     } catch (Exception e) {
/* 1318 */       PlatformDependent.throwException(e);
/* 1319 */       return -1;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByte(int index, int length, ByteProcessor processor) {
/* 1325 */     checkIndex(index, length);
/*      */     try {
/* 1327 */       return forEachByteAsc0(index, index + length, processor);
/* 1328 */     } catch (Exception e) {
/* 1329 */       PlatformDependent.throwException(e);
/* 1330 */       return -1;
/*      */     } 
/*      */   }
/*      */   
/*      */   int forEachByteAsc0(int start, int end, ByteProcessor processor) throws Exception {
/* 1335 */     for (; start < end; start++) {
/* 1336 */       if (!processor.process(_getByte(start))) {
/* 1337 */         return start;
/*      */       }
/*      */     } 
/*      */     
/* 1341 */     return -1;
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByteDesc(ByteProcessor processor) {
/* 1346 */     ensureAccessible();
/*      */     try {
/* 1348 */       return forEachByteDesc0(this.writerIndex - 1, this.readerIndex, processor);
/* 1349 */     } catch (Exception e) {
/* 1350 */       PlatformDependent.throwException(e);
/* 1351 */       return -1;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public int forEachByteDesc(int index, int length, ByteProcessor processor) {
/* 1357 */     checkIndex(index, length);
/*      */     try {
/* 1359 */       return forEachByteDesc0(index + length - 1, index, processor);
/* 1360 */     } catch (Exception e) {
/* 1361 */       PlatformDependent.throwException(e);
/* 1362 */       return -1;
/*      */     } 
/*      */   }
/*      */   
/*      */   int forEachByteDesc0(int rStart, int rEnd, ByteProcessor processor) throws Exception {
/* 1367 */     for (; rStart >= rEnd; rStart--) {
/* 1368 */       if (!processor.process(_getByte(rStart))) {
/* 1369 */         return rStart;
/*      */       }
/*      */     } 
/* 1372 */     return -1;
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1377 */     return ByteBufUtil.hashCode(this);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object o) {
/* 1382 */     return (this == o || (o instanceof ByteBuf && ByteBufUtil.equals(this, (ByteBuf)o)));
/*      */   }
/*      */ 
/*      */   
/*      */   public int compareTo(ByteBuf that) {
/* 1387 */     return ByteBufUtil.compare(this, that);
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/* 1392 */     if (refCnt() == 0) {
/* 1393 */       return StringUtil.simpleClassName(this) + "(freed)";
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1400 */     StringBuilder buf = (new StringBuilder()).append(StringUtil.simpleClassName(this)).append("(ridx: ").append(this.readerIndex).append(", widx: ").append(this.writerIndex).append(", cap: ").append(capacity());
/* 1401 */     if (this.maxCapacity != Integer.MAX_VALUE) {
/* 1402 */       buf.append('/').append(this.maxCapacity);
/*      */     }
/*      */     
/* 1405 */     ByteBuf unwrapped = unwrap();
/* 1406 */     if (unwrapped != null) {
/* 1407 */       buf.append(", unwrapped: ").append(unwrapped);
/*      */     }
/* 1409 */     buf.append(')');
/* 1410 */     return buf.toString();
/*      */   }
/*      */   
/*      */   protected final void checkIndex(int index) {
/* 1414 */     checkIndex(index, 1);
/*      */   }
/*      */   
/*      */   protected final void checkIndex(int index, int fieldLength) {
/* 1418 */     ensureAccessible();
/* 1419 */     checkIndex0(index, fieldLength);
/*      */   }
/*      */ 
/*      */   
/*      */   private static void checkRangeBounds(String indexName, int index, int fieldLength, int capacity) {
/* 1424 */     if (MathUtil.isOutOfBounds(index, fieldLength, capacity)) {
/* 1425 */       throw new IndexOutOfBoundsException(String.format("%s: %d, length: %d (expected: range(0, %d))", new Object[] { indexName, 
/* 1426 */               Integer.valueOf(index), Integer.valueOf(fieldLength), Integer.valueOf(capacity) }));
/*      */     }
/*      */   }
/*      */   
/*      */   final void checkIndex0(int index, int fieldLength) {
/* 1431 */     if (checkBounds) {
/* 1432 */       checkRangeBounds("index", index, fieldLength, capacity());
/*      */     }
/*      */   }
/*      */   
/*      */   protected final void checkSrcIndex(int index, int length, int srcIndex, int srcCapacity) {
/* 1437 */     checkIndex(index, length);
/* 1438 */     if (checkBounds) {
/* 1439 */       checkRangeBounds("srcIndex", srcIndex, length, srcCapacity);
/*      */     }
/*      */   }
/*      */   
/*      */   protected final void checkDstIndex(int index, int length, int dstIndex, int dstCapacity) {
/* 1444 */     checkIndex(index, length);
/* 1445 */     if (checkBounds) {
/* 1446 */       checkRangeBounds("dstIndex", dstIndex, length, dstCapacity);
/*      */     }
/*      */   }
/*      */   
/*      */   protected final void checkDstIndex(int length, int dstIndex, int dstCapacity) {
/* 1451 */     checkReadableBytes(length);
/* 1452 */     if (checkBounds) {
/* 1453 */       checkRangeBounds("dstIndex", dstIndex, length, dstCapacity);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void checkReadableBytes(int minimumReadableBytes) {
/* 1463 */     checkReadableBytes0(ObjectUtil.checkPositiveOrZero(minimumReadableBytes, "minimumReadableBytes"));
/*      */   }
/*      */   
/*      */   protected final void checkNewCapacity(int newCapacity) {
/* 1467 */     ensureAccessible();
/* 1468 */     if (checkBounds && (newCapacity < 0 || newCapacity > maxCapacity())) {
/* 1469 */       throw new IllegalArgumentException("newCapacity: " + newCapacity + " (expected: 0-" + 
/* 1470 */           maxCapacity() + ')');
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkReadableBytes0(int minimumReadableBytes) {
/* 1475 */     ensureAccessible();
/* 1476 */     if (checkBounds && this.readerIndex > this.writerIndex - minimumReadableBytes) {
/* 1477 */       throw new IndexOutOfBoundsException(String.format("readerIndex(%d) + length(%d) exceeds writerIndex(%d): %s", new Object[] {
/*      */               
/* 1479 */               Integer.valueOf(this.readerIndex), Integer.valueOf(minimumReadableBytes), Integer.valueOf(this.writerIndex), this
/*      */             }));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected final void ensureAccessible() {
/* 1488 */     if (checkAccessible && !isAccessible()) {
/* 1489 */       throw new IllegalReferenceCountException(0);
/*      */     }
/*      */   }
/*      */   
/*      */   final void setIndex0(int readerIndex, int writerIndex) {
/* 1494 */     this.readerIndex = readerIndex;
/* 1495 */     this.writerIndex = writerIndex;
/*      */   }
/*      */   
/*      */   final void discardMarks() {
/* 1499 */     this.markedReaderIndex = this.markedWriterIndex = 0;
/*      */   }
/*      */   
/*      */   protected abstract byte _getByte(int paramInt);
/*      */   
/*      */   protected abstract short _getShort(int paramInt);
/*      */   
/*      */   protected abstract short _getShortLE(int paramInt);
/*      */   
/*      */   protected abstract int _getUnsignedMedium(int paramInt);
/*      */   
/*      */   protected abstract int _getUnsignedMediumLE(int paramInt);
/*      */   
/*      */   protected abstract int _getInt(int paramInt);
/*      */   
/*      */   protected abstract int _getIntLE(int paramInt);
/*      */   
/*      */   protected abstract long _getLong(int paramInt);
/*      */   
/*      */   protected abstract long _getLongLE(int paramInt);
/*      */   
/*      */   protected abstract void _setByte(int paramInt1, int paramInt2);
/*      */   
/*      */   protected abstract void _setShort(int paramInt1, int paramInt2);
/*      */   
/*      */   protected abstract void _setShortLE(int paramInt1, int paramInt2);
/*      */   
/*      */   protected abstract void _setMedium(int paramInt1, int paramInt2);
/*      */   
/*      */   protected abstract void _setMediumLE(int paramInt1, int paramInt2);
/*      */   
/*      */   protected abstract void _setInt(int paramInt1, int paramInt2);
/*      */   
/*      */   protected abstract void _setIntLE(int paramInt1, int paramInt2);
/*      */   
/*      */   protected abstract void _setLong(int paramInt, long paramLong);
/*      */   
/*      */   protected abstract void _setLongLE(int paramInt, long paramLong);
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\AbstractByteBuf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */