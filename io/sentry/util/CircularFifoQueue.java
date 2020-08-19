/*     */ package io.sentry.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Queue;
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
/*     */ public class CircularFifoQueue<E>
/*     */   extends AbstractCollection<E>
/*     */   implements Queue<E>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -8423413834657610406L;
/*     */   private transient E[] elements;
/*  57 */   private transient int start = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   private transient int end = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   private transient boolean full = false;
/*     */ 
/*     */   
/*     */   private final int maxElements;
/*     */ 
/*     */ 
/*     */   
/*     */   public CircularFifoQueue() {
/*  78 */     this(32);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CircularFifoQueue(int size) {
/*  89 */     if (size <= 0) {
/*  90 */       throw new IllegalArgumentException("The size must be greater than 0");
/*     */     }
/*  92 */     this.elements = (E[])new Object[size];
/*  93 */     this.maxElements = this.elements.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CircularFifoQueue(Collection<? extends E> coll) {
/* 104 */     this(coll.size());
/* 105 */     addAll(coll);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeObject(ObjectOutputStream out) throws IOException {
/* 116 */     out.defaultWriteObject();
/* 117 */     out.writeInt(size());
/* 118 */     for (E e : this) {
/* 119 */       out.writeObject(e);
/*     */     }
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
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 132 */     in.defaultReadObject();
/* 133 */     this.elements = (E[])new Object[this.maxElements];
/* 134 */     int size = in.readInt();
/* 135 */     for (int i = 0; i < size; i++) {
/* 136 */       this.elements[i] = (E)in.readObject();
/*     */     }
/* 138 */     this.start = 0;
/* 139 */     this.full = (size == this.maxElements);
/* 140 */     if (this.full) {
/* 141 */       this.end = 0;
/*     */     } else {
/* 143 */       this.end = size;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 155 */     int size = 0;
/*     */     
/* 157 */     if (this.end < this.start) {
/* 158 */       size = this.maxElements - this.start + this.end;
/* 159 */     } else if (this.end == this.start) {
/* 160 */       size = this.full ? this.maxElements : 0;
/*     */     } else {
/* 162 */       size = this.end - this.start;
/*     */     } 
/*     */     
/* 165 */     return size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 175 */     return (size() == 0);
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
/*     */   public boolean isFull() {
/* 187 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAtFullCapacity() {
/* 198 */     return (size() == this.maxElements);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int maxSize() {
/* 207 */     return this.maxElements;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 215 */     this.full = false;
/* 216 */     this.start = 0;
/* 217 */     this.end = 0;
/* 218 */     Arrays.fill((Object[])this.elements, (Object)null);
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
/*     */   public boolean add(E element) {
/* 231 */     if (null == element) {
/* 232 */       throw new NullPointerException("Attempted to add null object to queue");
/*     */     }
/*     */     
/* 235 */     if (isAtFullCapacity()) {
/* 236 */       remove();
/*     */     }
/*     */     
/* 239 */     this.elements[this.end++] = element;
/*     */     
/* 241 */     if (this.end >= this.maxElements) {
/* 242 */       this.end = 0;
/*     */     }
/*     */     
/* 245 */     if (this.end == this.start) {
/* 246 */       this.full = true;
/*     */     }
/*     */     
/* 249 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public E get(int index) {
/* 260 */     int sz = size();
/* 261 */     if (index < 0 || index >= sz) {
/* 262 */       throw new NoSuchElementException(
/* 263 */           String.format("The specified index (%1$d) is outside the available range [0, %2$d)", new Object[] {
/* 264 */               Integer.valueOf(index), Integer.valueOf(sz)
/*     */             }));
/*     */     }
/* 267 */     int idx = (this.start + index) % this.maxElements;
/* 268 */     return this.elements[idx];
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
/*     */   public boolean offer(E element) {
/* 283 */     return add(element);
/*     */   }
/*     */ 
/*     */   
/*     */   public E poll() {
/* 288 */     if (isEmpty()) {
/* 289 */       return null;
/*     */     }
/* 291 */     return remove();
/*     */   }
/*     */ 
/*     */   
/*     */   public E element() {
/* 296 */     if (isEmpty()) {
/* 297 */       throw new NoSuchElementException("queue is empty");
/*     */     }
/* 299 */     return peek();
/*     */   }
/*     */ 
/*     */   
/*     */   public E peek() {
/* 304 */     if (isEmpty()) {
/* 305 */       return null;
/*     */     }
/* 307 */     return this.elements[this.start];
/*     */   }
/*     */ 
/*     */   
/*     */   public E remove() {
/* 312 */     if (isEmpty()) {
/* 313 */       throw new NoSuchElementException("queue is empty");
/*     */     }
/*     */     
/* 316 */     E element = this.elements[this.start];
/* 317 */     if (null != element) {
/* 318 */       this.elements[this.start++] = null;
/*     */       
/* 320 */       if (this.start >= this.maxElements) {
/* 321 */         this.start = 0;
/*     */       }
/* 323 */       this.full = false;
/*     */     } 
/* 325 */     return element;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int increment(int index) {
/* 336 */     index++;
/* 337 */     if (index >= this.maxElements) {
/* 338 */       index = 0;
/*     */     }
/* 340 */     return index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int decrement(int index) {
/* 350 */     index--;
/* 351 */     if (index < 0) {
/* 352 */       index = this.maxElements - 1;
/*     */     }
/* 354 */     return index;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<E> iterator() {
/* 364 */     return new Iterator<E>()
/*     */       {
/* 366 */         private int index = CircularFifoQueue.this.start;
/* 367 */         private int lastReturnedIndex = -1;
/* 368 */         private boolean isFirst = CircularFifoQueue.this.full;
/*     */ 
/*     */         
/*     */         public boolean hasNext() {
/* 372 */           return (this.isFirst || this.index != CircularFifoQueue.this.end);
/*     */         }
/*     */ 
/*     */         
/*     */         public E next() {
/* 377 */           if (!hasNext()) {
/* 378 */             throw new NoSuchElementException();
/*     */           }
/* 380 */           this.isFirst = false;
/* 381 */           this.lastReturnedIndex = this.index;
/* 382 */           this.index = CircularFifoQueue.this.increment(this.index);
/* 383 */           return (E)CircularFifoQueue.this.elements[this.lastReturnedIndex];
/*     */         }
/*     */ 
/*     */         
/*     */         public void remove() {
/* 388 */           if (this.lastReturnedIndex == -1) {
/* 389 */             throw new IllegalStateException();
/*     */           }
/*     */ 
/*     */           
/* 393 */           if (this.lastReturnedIndex == CircularFifoQueue.this.start) {
/* 394 */             CircularFifoQueue.this.remove();
/* 395 */             this.lastReturnedIndex = -1;
/*     */             
/*     */             return;
/*     */           } 
/* 399 */           int pos = this.lastReturnedIndex + 1;
/* 400 */           if (CircularFifoQueue.this.start < this.lastReturnedIndex && pos < CircularFifoQueue.this.end) {
/*     */             
/* 402 */             System.arraycopy(CircularFifoQueue.this.elements, pos, CircularFifoQueue.this.elements, this.lastReturnedIndex, CircularFifoQueue.this.end - pos);
/*     */           } else {
/*     */             
/* 405 */             while (pos != CircularFifoQueue.this.end) {
/* 406 */               if (pos >= CircularFifoQueue.this.maxElements) {
/* 407 */                 CircularFifoQueue.this.elements[pos - 1] = CircularFifoQueue.this.elements[0];
/* 408 */                 pos = 0; continue;
/*     */               } 
/* 410 */               CircularFifoQueue.this.elements[CircularFifoQueue.this.decrement(pos)] = CircularFifoQueue.this.elements[pos];
/* 411 */               pos = CircularFifoQueue.this.increment(pos);
/*     */             } 
/*     */           } 
/*     */ 
/*     */           
/* 416 */           this.lastReturnedIndex = -1;
/* 417 */           CircularFifoQueue.this.end = CircularFifoQueue.this.decrement(CircularFifoQueue.this.end);
/* 418 */           CircularFifoQueue.this.elements[CircularFifoQueue.this.end] = null;
/* 419 */           CircularFifoQueue.this.full = false;
/* 420 */           this.index = CircularFifoQueue.this.decrement(this.index);
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentr\\util\CircularFifoQueue.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */