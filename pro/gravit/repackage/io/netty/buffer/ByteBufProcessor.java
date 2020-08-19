/*     */ package pro.gravit.repackage.io.netty.buffer;
/*     */ 
/*     */ import pro.gravit.repackage.io.netty.util.ByteProcessor;
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
/*     */ @Deprecated
/*     */ public interface ByteBufProcessor
/*     */   extends ByteProcessor
/*     */ {
/*     */   @Deprecated
/*  31 */   public static final ByteBufProcessor FIND_NUL = new ByteBufProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/*  34 */         return (value != 0);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  42 */   public static final ByteBufProcessor FIND_NON_NUL = new ByteBufProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/*  45 */         return (value == 0);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  53 */   public static final ByteBufProcessor FIND_CR = new ByteBufProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/*  56 */         return (value != 13);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  64 */   public static final ByteBufProcessor FIND_NON_CR = new ByteBufProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/*  67 */         return (value == 13);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  75 */   public static final ByteBufProcessor FIND_LF = new ByteBufProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/*  78 */         return (value != 10);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  86 */   public static final ByteBufProcessor FIND_NON_LF = new ByteBufProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/*  89 */         return (value == 10);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  97 */   public static final ByteBufProcessor FIND_CRLF = new ByteBufProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/* 100 */         return (value != 13 && value != 10);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 108 */   public static final ByteBufProcessor FIND_NON_CRLF = new ByteBufProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/* 111 */         return (value == 13 || value == 10);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 119 */   public static final ByteBufProcessor FIND_LINEAR_WHITESPACE = new ByteBufProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/* 122 */         return (value != 32 && value != 9);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/* 130 */   public static final ByteBufProcessor FIND_NON_LINEAR_WHITESPACE = new ByteBufProcessor()
/*     */     {
/*     */       public boolean process(byte value) throws Exception {
/* 133 */         return (value == 32 || value == 9);
/*     */       }
/*     */     };
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\ByteBufProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */