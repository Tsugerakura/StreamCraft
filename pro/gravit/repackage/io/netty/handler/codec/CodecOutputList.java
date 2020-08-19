/*     */ package pro.gravit.repackage.io.netty.handler.codec;
/*     */ 
/*     */ import java.util.AbstractList;
/*     */ import java.util.RandomAccess;
/*     */ import pro.gravit.repackage.io.netty.util.concurrent.FastThreadLocal;
/*     */ import pro.gravit.repackage.io.netty.util.internal.MathUtil;
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
/*     */ final class CodecOutputList
/*     */   extends AbstractList<Object>
/*     */   implements RandomAccess
/*     */ {
/*  31 */   private static final CodecOutputListRecycler NOOP_RECYCLER = new CodecOutputListRecycler()
/*     */     {
/*     */       public void recycle(CodecOutputList object) {}
/*     */     };
/*     */ 
/*     */ 
/*     */   
/*  38 */   private static final FastThreadLocal<CodecOutputLists> CODEC_OUTPUT_LISTS_POOL = new FastThreadLocal<CodecOutputLists>()
/*     */     {
/*     */       
/*     */       protected CodecOutputList.CodecOutputLists initialValue() throws Exception
/*     */       {
/*  43 */         return new CodecOutputList.CodecOutputLists(16);
/*     */       }
/*     */     };
/*     */   
/*     */   private final CodecOutputListRecycler recycler;
/*     */   private int size;
/*     */   private Object[] array;
/*     */   private boolean insertSinceRecycled;
/*     */   
/*     */   private static interface CodecOutputListRecycler {
/*     */     void recycle(CodecOutputList param1CodecOutputList); }
/*     */   
/*     */   private static final class CodecOutputLists implements CodecOutputListRecycler { private final CodecOutputList[] elements;
/*     */     private final int mask;
/*     */     
/*     */     CodecOutputLists(int numElements) {
/*  59 */       this.elements = new CodecOutputList[MathUtil.safeFindNextPositivePowerOfTwo(numElements)];
/*  60 */       for (int i = 0; i < this.elements.length; i++)
/*     */       {
/*  62 */         this.elements[i] = new CodecOutputList(this, 16);
/*     */       }
/*  64 */       this.count = this.elements.length;
/*  65 */       this.currentIdx = this.elements.length;
/*  66 */       this.mask = this.elements.length - 1;
/*     */     }
/*     */     private int currentIdx; private int count;
/*     */     public CodecOutputList getOrCreate() {
/*  70 */       if (this.count == 0)
/*     */       {
/*     */         
/*  73 */         return new CodecOutputList(CodecOutputList.NOOP_RECYCLER, 4);
/*     */       }
/*  75 */       this.count--;
/*     */       
/*  77 */       int idx = this.currentIdx - 1 & this.mask;
/*  78 */       CodecOutputList list = this.elements[idx];
/*  79 */       this.currentIdx = idx;
/*  80 */       return list;
/*     */     }
/*     */ 
/*     */     
/*     */     public void recycle(CodecOutputList codecOutputList) {
/*  85 */       int idx = this.currentIdx;
/*  86 */       this.elements[idx] = codecOutputList;
/*  87 */       this.currentIdx = idx + 1 & this.mask;
/*  88 */       this.count++;
/*  89 */       assert this.count <= this.elements.length;
/*     */     } }
/*     */ 
/*     */   
/*     */   static CodecOutputList newInstance() {
/*  94 */     return ((CodecOutputLists)CODEC_OUTPUT_LISTS_POOL.get()).getOrCreate();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CodecOutputList(CodecOutputListRecycler recycler, int size) {
/* 103 */     this.recycler = recycler;
/* 104 */     this.array = new Object[size];
/*     */   }
/*     */ 
/*     */   
/*     */   public Object get(int index) {
/* 109 */     checkIndex(index);
/* 110 */     return this.array[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/* 115 */     return this.size;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean add(Object element) {
/* 120 */     ObjectUtil.checkNotNull(element, "element");
/*     */     try {
/* 122 */       insert(this.size, element);
/* 123 */     } catch (IndexOutOfBoundsException ignore) {
/*     */       
/* 125 */       expandArray();
/* 126 */       insert(this.size, element);
/*     */     } 
/* 128 */     this.size++;
/* 129 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object set(int index, Object element) {
/* 134 */     ObjectUtil.checkNotNull(element, "element");
/* 135 */     checkIndex(index);
/*     */     
/* 137 */     Object old = this.array[index];
/* 138 */     insert(index, element);
/* 139 */     return old;
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(int index, Object element) {
/* 144 */     ObjectUtil.checkNotNull(element, "element");
/* 145 */     checkIndex(index);
/*     */     
/* 147 */     if (this.size == this.array.length) {
/* 148 */       expandArray();
/*     */     }
/*     */     
/* 151 */     if (index != this.size) {
/* 152 */       System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
/*     */     }
/*     */     
/* 155 */     insert(index, element);
/* 156 */     this.size++;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object remove(int index) {
/* 161 */     checkIndex(index);
/* 162 */     Object old = this.array[index];
/*     */     
/* 164 */     int len = this.size - index - 1;
/* 165 */     if (len > 0) {
/* 166 */       System.arraycopy(this.array, index + 1, this.array, index, len);
/*     */     }
/* 168 */     this.array[--this.size] = null;
/*     */     
/* 170 */     return old;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 177 */     this.size = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean insertSinceRecycled() {
/* 184 */     return this.insertSinceRecycled;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void recycle() {
/* 191 */     for (int i = 0; i < this.size; i++) {
/* 192 */       this.array[i] = null;
/*     */     }
/* 194 */     this.size = 0;
/* 195 */     this.insertSinceRecycled = false;
/*     */     
/* 197 */     this.recycler.recycle(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Object getUnsafe(int index) {
/* 204 */     return this.array[index];
/*     */   }
/*     */   
/*     */   private void checkIndex(int index) {
/* 208 */     if (index >= this.size) {
/* 209 */       throw new IndexOutOfBoundsException();
/*     */     }
/*     */   }
/*     */   
/*     */   private void insert(int index, Object element) {
/* 214 */     this.array[index] = element;
/* 215 */     this.insertSinceRecycled = true;
/*     */   }
/*     */ 
/*     */   
/*     */   private void expandArray() {
/* 220 */     int newCapacity = this.array.length << 1;
/*     */     
/* 222 */     if (newCapacity < 0) {
/* 223 */       throw new OutOfMemoryError();
/*     */     }
/*     */     
/* 226 */     Object[] newArray = new Object[newCapacity];
/* 227 */     System.arraycopy(this.array, 0, newArray, 0, this.array.length);
/*     */     
/* 229 */     this.array = newArray;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\CodecOutputList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */