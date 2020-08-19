/*     */ package pro.gravit.repackage.io.netty.channel;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import pro.gravit.repackage.io.netty.util.AbstractReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.IllegalReferenceCountException;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLogger;
/*     */ import pro.gravit.repackage.io.netty.util.internal.logging.InternalLoggerFactory;
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
/*     */ public class DefaultFileRegion
/*     */   extends AbstractReferenceCounted
/*     */   implements FileRegion
/*     */ {
/*  40 */   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
/*     */ 
/*     */   
/*     */   private final File f;
/*     */   
/*     */   private final long position;
/*     */   
/*     */   private final long count;
/*     */   
/*     */   private long transferred;
/*     */   
/*     */   private FileChannel file;
/*     */ 
/*     */   
/*     */   public DefaultFileRegion(FileChannel file, long position, long count) {
/*  55 */     this.file = (FileChannel)ObjectUtil.checkNotNull(file, "file");
/*  56 */     this.position = ObjectUtil.checkPositiveOrZero(position, "position");
/*  57 */     this.count = ObjectUtil.checkPositiveOrZero(count, "count");
/*  58 */     this.f = null;
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
/*     */   public DefaultFileRegion(File f, long position, long count) {
/*  70 */     this.f = (File)ObjectUtil.checkNotNull(f, "f");
/*  71 */     this.position = ObjectUtil.checkPositiveOrZero(position, "position");
/*  72 */     this.count = ObjectUtil.checkPositiveOrZero(count, "count");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/*  79 */     return (this.file != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void open() throws IOException {
/*  86 */     if (!isOpen() && refCnt() > 0)
/*     */     {
/*  88 */       this.file = (new RandomAccessFile(this.f, "r")).getChannel();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public long position() {
/*  94 */     return this.position;
/*     */   }
/*     */ 
/*     */   
/*     */   public long count() {
/*  99 */     return this.count;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public long transfered() {
/* 105 */     return this.transferred;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferred() {
/* 110 */     return this.transferred;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(WritableByteChannel target, long position) throws IOException {
/* 115 */     long count = this.count - position;
/* 116 */     if (count < 0L || position < 0L) {
/* 117 */       throw new IllegalArgumentException("position out of range: " + position + " (expected: 0 - " + (this.count - 1L) + ')');
/*     */     }
/*     */ 
/*     */     
/* 121 */     if (count == 0L) {
/* 122 */       return 0L;
/*     */     }
/* 124 */     if (refCnt() == 0) {
/* 125 */       throw new IllegalReferenceCountException(0);
/*     */     }
/*     */     
/* 128 */     open();
/*     */     
/* 130 */     long written = this.file.transferTo(this.position + position, count, target);
/* 131 */     if (written > 0L) {
/* 132 */       this.transferred += written;
/* 133 */     } else if (written == 0L) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 138 */       validate(this, position);
/*     */     } 
/* 140 */     return written;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void deallocate() {
/* 145 */     FileChannel file = this.file;
/*     */     
/* 147 */     if (file == null) {
/*     */       return;
/*     */     }
/* 150 */     this.file = null;
/*     */     
/*     */     try {
/* 153 */       file.close();
/* 154 */     } catch (IOException e) {
/* 155 */       logger.warn("Failed to close a file.", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public FileRegion retain() {
/* 161 */     super.retain();
/* 162 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileRegion retain(int increment) {
/* 167 */     super.retain(increment);
/* 168 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileRegion touch() {
/* 173 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileRegion touch(Object hint) {
/* 178 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void validate(DefaultFileRegion region, long position) throws IOException {
/* 186 */     long size = region.file.size();
/* 187 */     long count = region.count - position;
/* 188 */     if (region.position + count + position > size)
/* 189 */       throw new IOException("Underlying file size " + size + " smaller then requested count " + region.count); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\channel\DefaultFileRegion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */