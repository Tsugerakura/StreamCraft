/*     */ package com.fasterxml.jackson.core.sym;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonFactory;
/*     */ import com.fasterxml.jackson.core.util.InternCache;
/*     */ import java.util.Arrays;
/*     */ import java.util.BitSet;
/*     */ import java.util.concurrent.atomic.AtomicReference;
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
/*     */ public final class CharsToNameCanonicalizer
/*     */ {
/*     */   public static final int HASH_MULT = 33;
/*     */   private static final int DEFAULT_T_SIZE = 64;
/*     */   private static final int MAX_T_SIZE = 65536;
/*     */   static final int MAX_ENTRIES_FOR_REUSE = 12000;
/*     */   static final int MAX_COLL_CHAIN_LENGTH = 100;
/*     */   private final CharsToNameCanonicalizer _parent;
/*     */   private final AtomicReference<TableInfo> _tableInfo;
/*     */   private final int _seed;
/*     */   private final int _flags;
/*     */   private boolean _canonicalize;
/*     */   private String[] _symbols;
/*     */   private Bucket[] _buckets;
/*     */   private int _size;
/*     */   private int _sizeThreshold;
/*     */   private int _indexMask;
/*     */   private int _longestCollisionList;
/*     */   private boolean _hashShared;
/*     */   private BitSet _overflows;
/*     */   
/*     */   private CharsToNameCanonicalizer(int seed) {
/* 231 */     this._parent = null;
/* 232 */     this._seed = seed;
/*     */ 
/*     */     
/* 235 */     this._canonicalize = true;
/* 236 */     this._flags = -1;
/*     */     
/* 238 */     this._hashShared = false;
/* 239 */     this._longestCollisionList = 0;
/*     */     
/* 241 */     this._tableInfo = new AtomicReference<TableInfo>(TableInfo.createInitial(64));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CharsToNameCanonicalizer(CharsToNameCanonicalizer parent, int flags, int seed, TableInfo parentState) {
/* 252 */     this._parent = parent;
/* 253 */     this._seed = seed;
/* 254 */     this._tableInfo = null;
/* 255 */     this._flags = flags;
/* 256 */     this._canonicalize = JsonFactory.Feature.CANONICALIZE_FIELD_NAMES.enabledIn(flags);
/*     */ 
/*     */     
/* 259 */     this._symbols = parentState.symbols;
/* 260 */     this._buckets = parentState.buckets;
/*     */     
/* 262 */     this._size = parentState.size;
/* 263 */     this._longestCollisionList = parentState.longestCollisionList;
/*     */ 
/*     */     
/* 266 */     int arrayLen = this._symbols.length;
/* 267 */     this._sizeThreshold = _thresholdSize(arrayLen);
/* 268 */     this._indexMask = arrayLen - 1;
/*     */ 
/*     */     
/* 271 */     this._hashShared = true;
/*     */   }
/*     */   private static int _thresholdSize(int hashAreaSize) {
/* 274 */     return hashAreaSize - (hashAreaSize >> 2);
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
/*     */   public static CharsToNameCanonicalizer createRoot() {
/* 291 */     long now = System.currentTimeMillis();
/*     */     
/* 293 */     int seed = (int)now + (int)(now >>> 32L) | 0x1;
/* 294 */     return createRoot(seed);
/*     */   }
/*     */   
/*     */   protected static CharsToNameCanonicalizer createRoot(int seed) {
/* 298 */     return new CharsToNameCanonicalizer(seed);
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
/*     */   public CharsToNameCanonicalizer makeChild(int flags) {
/* 313 */     return new CharsToNameCanonicalizer(this, flags, this._seed, this._tableInfo.get());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {
/* 324 */     if (!maybeDirty()) {
/*     */       return;
/*     */     }
/* 327 */     if (this._parent != null && this._canonicalize) {
/* 328 */       this._parent.mergeChild(new TableInfo(this));
/*     */ 
/*     */       
/* 331 */       this._hashShared = true;
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
/*     */   private void mergeChild(TableInfo childState) {
/* 344 */     int childCount = childState.size;
/* 345 */     TableInfo currState = this._tableInfo.get();
/*     */ 
/*     */ 
/*     */     
/* 349 */     if (childCount == currState.size) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 356 */     if (childCount > 12000)
/*     */     {
/* 358 */       childState = TableInfo.createInitial(64);
/*     */     }
/* 360 */     this._tableInfo.compareAndSet(currState, childState);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 370 */     if (this._tableInfo != null) {
/* 371 */       return ((TableInfo)this._tableInfo.get()).size;
/*     */     }
/*     */     
/* 374 */     return this._size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int bucketCount() {
/* 383 */     return this._symbols.length;
/* 384 */   } public boolean maybeDirty() { return !this._hashShared; } public int hashSeed() {
/* 385 */     return this._seed;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int collisionCount() {
/* 395 */     int count = 0;
/*     */     
/* 397 */     for (Bucket bucket : this._buckets) {
/* 398 */       if (bucket != null) {
/* 399 */         count += bucket.length;
/*     */       }
/*     */     } 
/* 402 */     return count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int maxCollisionLength() {
/* 412 */     return this._longestCollisionList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String findSymbol(char[] buffer, int start, int len, int h) {
/* 422 */     if (len < 1) {
/* 423 */       return "";
/*     */     }
/* 425 */     if (!this._canonicalize) {
/* 426 */       return new String(buffer, start, len);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 434 */     int index = _hashToIndex(h);
/* 435 */     String sym = this._symbols[index];
/*     */ 
/*     */     
/* 438 */     if (sym != null) {
/*     */       
/* 440 */       if (sym.length() == len) {
/* 441 */         int i = 0;
/* 442 */         while (sym.charAt(i) == buffer[start + i]) {
/*     */           
/* 444 */           if (++i == len) {
/* 445 */             return sym;
/*     */           }
/*     */         } 
/*     */       } 
/* 449 */       Bucket b = this._buckets[index >> 1];
/* 450 */       if (b != null) {
/* 451 */         sym = b.has(buffer, start, len);
/* 452 */         if (sym != null) {
/* 453 */           return sym;
/*     */         }
/* 455 */         sym = _findSymbol2(buffer, start, len, b.next);
/* 456 */         if (sym != null) {
/* 457 */           return sym;
/*     */         }
/*     */       } 
/*     */     } 
/* 461 */     return _addSymbol(buffer, start, len, h, index);
/*     */   }
/*     */   
/*     */   private String _findSymbol2(char[] buffer, int start, int len, Bucket b) {
/* 465 */     while (b != null) {
/* 466 */       String sym = b.has(buffer, start, len);
/* 467 */       if (sym != null) {
/* 468 */         return sym;
/*     */       }
/* 470 */       b = b.next;
/*     */     } 
/* 472 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private String _addSymbol(char[] buffer, int start, int len, int h, int index) {
/* 477 */     if (this._hashShared) {
/* 478 */       copyArrays();
/* 479 */       this._hashShared = false;
/* 480 */     } else if (this._size >= this._sizeThreshold) {
/* 481 */       rehash();
/*     */ 
/*     */       
/* 484 */       index = _hashToIndex(calcHash(buffer, start, len));
/*     */     } 
/*     */     
/* 487 */     String newSymbol = new String(buffer, start, len);
/* 488 */     if (JsonFactory.Feature.INTERN_FIELD_NAMES.enabledIn(this._flags)) {
/* 489 */       newSymbol = InternCache.instance.intern(newSymbol);
/*     */     }
/* 491 */     this._size++;
/*     */     
/* 493 */     if (this._symbols[index] == null) {
/* 494 */       this._symbols[index] = newSymbol;
/*     */     } else {
/* 496 */       int bix = index >> 1;
/* 497 */       Bucket newB = new Bucket(newSymbol, this._buckets[bix]);
/* 498 */       int collLen = newB.length;
/* 499 */       if (collLen > 100) {
/*     */ 
/*     */         
/* 502 */         _handleSpillOverflow(bix, newB, index);
/*     */       } else {
/* 504 */         this._buckets[bix] = newB;
/* 505 */         this._longestCollisionList = Math.max(collLen, this._longestCollisionList);
/*     */       } 
/*     */     } 
/* 508 */     return newSymbol;
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
/*     */   private void _handleSpillOverflow(int bucketIndex, Bucket newBucket, int mainIndex) {
/* 520 */     if (this._overflows == null) {
/* 521 */       this._overflows = new BitSet();
/* 522 */       this._overflows.set(bucketIndex);
/*     */     }
/* 524 */     else if (this._overflows.get(bucketIndex)) {
/*     */       
/* 526 */       if (JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW.enabledIn(this._flags)) {
/* 527 */         reportTooManyCollisions(100);
/*     */       }
/*     */ 
/*     */       
/* 531 */       this._canonicalize = false;
/*     */     } else {
/* 533 */       this._overflows.set(bucketIndex);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 538 */     this._symbols[mainIndex] = newBucket.symbol;
/* 539 */     this._buckets[bucketIndex] = null;
/*     */     
/* 541 */     this._size -= newBucket.length;
/*     */     
/* 543 */     this._longestCollisionList = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int _hashToIndex(int rawHash) {
/* 552 */     rawHash += rawHash >>> 15;
/* 553 */     rawHash ^= rawHash << 7;
/* 554 */     rawHash += rawHash >>> 3;
/* 555 */     return rawHash & this._indexMask;
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
/*     */   public int calcHash(char[] buffer, int start, int len) {
/* 568 */     int hash = this._seed;
/* 569 */     for (int i = start, end = start + len; i < end; i++) {
/* 570 */       hash = hash * 33 + buffer[i];
/*     */     }
/*     */     
/* 573 */     return (hash == 0) ? 1 : hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public int calcHash(String key) {
/* 578 */     int len = key.length();
/*     */     
/* 580 */     int hash = this._seed;
/* 581 */     for (int i = 0; i < len; i++) {
/* 582 */       hash = hash * 33 + key.charAt(i);
/*     */     }
/*     */     
/* 585 */     return (hash == 0) ? 1 : hash;
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
/*     */   private void copyArrays() {
/* 599 */     String[] oldSyms = this._symbols;
/* 600 */     this._symbols = Arrays.<String>copyOf(oldSyms, oldSyms.length);
/* 601 */     Bucket[] oldBuckets = this._buckets;
/* 602 */     this._buckets = Arrays.<Bucket>copyOf(oldBuckets, oldBuckets.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void rehash() {
/* 613 */     int size = this._symbols.length;
/* 614 */     int newSize = size + size;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 620 */     if (newSize > 65536) {
/*     */ 
/*     */       
/* 623 */       this._size = 0;
/* 624 */       this._canonicalize = false;
/*     */       
/* 626 */       this._symbols = new String[64];
/* 627 */       this._buckets = new Bucket[32];
/* 628 */       this._indexMask = 63;
/* 629 */       this._hashShared = false;
/*     */       
/*     */       return;
/*     */     } 
/* 633 */     String[] oldSyms = this._symbols;
/* 634 */     Bucket[] oldBuckets = this._buckets;
/* 635 */     this._symbols = new String[newSize];
/* 636 */     this._buckets = new Bucket[newSize >> 1];
/*     */     
/* 638 */     this._indexMask = newSize - 1;
/* 639 */     this._sizeThreshold = _thresholdSize(newSize);
/*     */     
/* 641 */     int count = 0;
/*     */ 
/*     */ 
/*     */     
/* 645 */     int maxColl = 0;
/* 646 */     for (int i = 0; i < size; i++) {
/* 647 */       String symbol = oldSyms[i];
/* 648 */       if (symbol != null) {
/* 649 */         count++;
/* 650 */         int index = _hashToIndex(calcHash(symbol));
/* 651 */         if (this._symbols[index] == null) {
/* 652 */           this._symbols[index] = symbol;
/*     */         } else {
/* 654 */           int bix = index >> 1;
/* 655 */           Bucket newB = new Bucket(symbol, this._buckets[bix]);
/* 656 */           this._buckets[bix] = newB;
/* 657 */           maxColl = Math.max(maxColl, newB.length);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 662 */     int bucketSize = size >> 1;
/* 663 */     for (int j = 0; j < bucketSize; j++) {
/* 664 */       Bucket b = oldBuckets[j];
/* 665 */       while (b != null) {
/* 666 */         count++;
/* 667 */         String symbol = b.symbol;
/* 668 */         int index = _hashToIndex(calcHash(symbol));
/* 669 */         if (this._symbols[index] == null) {
/* 670 */           this._symbols[index] = symbol;
/*     */         } else {
/* 672 */           int bix = index >> 1;
/* 673 */           Bucket newB = new Bucket(symbol, this._buckets[bix]);
/* 674 */           this._buckets[bix] = newB;
/* 675 */           maxColl = Math.max(maxColl, newB.length);
/*     */         } 
/* 677 */         b = b.next;
/*     */       } 
/*     */     } 
/* 680 */     this._longestCollisionList = maxColl;
/* 681 */     this._overflows = null;
/*     */     
/* 683 */     if (count != this._size) {
/* 684 */       throw new IllegalStateException(String.format("Internal error on SymbolTable.rehash(): had %d entries; now have %d", new Object[] {
/*     */               
/* 686 */               Integer.valueOf(this._size), Integer.valueOf(count)
/*     */             }));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void reportTooManyCollisions(int maxLen) {
/* 694 */     throw new IllegalStateException("Longest collision chain in symbol table (of size " + this._size + ") now exceeds maximum, " + maxLen + " -- suspect a DoS attack based on hash collisions");
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
/*     */   protected void verifyInternalConsistency() {
/* 706 */     int count = 0;
/* 707 */     int size = this._symbols.length;
/*     */     
/* 709 */     for (int i = 0; i < size; i++) {
/* 710 */       String symbol = this._symbols[i];
/* 711 */       if (symbol != null) {
/* 712 */         count++;
/*     */       }
/*     */     } 
/*     */     
/* 716 */     int bucketSize = size >> 1;
/* 717 */     for (int j = 0; j < bucketSize; j++) {
/* 718 */       for (Bucket b = this._buckets[j]; b != null; b = b.next) {
/* 719 */         count++;
/*     */       }
/*     */     } 
/* 722 */     if (count != this._size) {
/* 723 */       throw new IllegalStateException(String.format("Internal error: expected internal size %d vs calculated count %d", new Object[] {
/* 724 */               Integer.valueOf(this._size), Integer.valueOf(count)
/*     */             }));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final class Bucket
/*     */   {
/*     */     public final String symbol;
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
/*     */     public final Bucket next;
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
/*     */     public final int length;
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
/*     */     public Bucket(String s, Bucket n) {
/* 794 */       this.symbol = s;
/* 795 */       this.next = n;
/* 796 */       this.length = (n == null) ? 1 : (n.length + 1);
/*     */     }
/*     */     
/*     */     public String has(char[] buf, int start, int len) {
/* 800 */       if (this.symbol.length() != len) {
/* 801 */         return null;
/*     */       }
/* 803 */       int i = 0;
/*     */       while (true) {
/* 805 */         if (this.symbol.charAt(i) != buf[start + i]) {
/* 806 */           return null;
/*     */         }
/* 808 */         if (++i >= len) {
/* 809 */           return this.symbol;
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class TableInfo
/*     */   {
/*     */     final int size;
/*     */ 
/*     */     
/*     */     final int longestCollisionList;
/*     */     
/*     */     final String[] symbols;
/*     */     
/*     */     final CharsToNameCanonicalizer.Bucket[] buckets;
/*     */ 
/*     */     
/*     */     public TableInfo(int size, int longestCollisionList, String[] symbols, CharsToNameCanonicalizer.Bucket[] buckets) {
/* 830 */       this.size = size;
/* 831 */       this.longestCollisionList = longestCollisionList;
/* 832 */       this.symbols = symbols;
/* 833 */       this.buckets = buckets;
/*     */     }
/*     */ 
/*     */     
/*     */     public TableInfo(CharsToNameCanonicalizer src) {
/* 838 */       this.size = src._size;
/* 839 */       this.longestCollisionList = src._longestCollisionList;
/* 840 */       this.symbols = src._symbols;
/* 841 */       this.buckets = src._buckets;
/*     */     }
/*     */     
/*     */     public static TableInfo createInitial(int sz) {
/* 845 */       return new TableInfo(0, 0, new String[sz], new CharsToNameCanonicalizer.Bucket[sz >> 1]);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\sym\CharsToNameCanonicalizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */