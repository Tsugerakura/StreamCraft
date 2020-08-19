/*      */ package com.fasterxml.jackson.core.sym;
/*      */ 
/*      */ import com.fasterxml.jackson.core.JsonFactory;
/*      */ import com.fasterxml.jackson.core.util.InternCache;
/*      */ import java.util.Arrays;
/*      */ import java.util.concurrent.atomic.AtomicReference;
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
/*      */ public final class ByteQuadsCanonicalizer
/*      */ {
/*      */   private static final int DEFAULT_T_SIZE = 64;
/*      */   private static final int MAX_T_SIZE = 65536;
/*      */   private static final int MIN_HASH_SIZE = 16;
/*      */   static final int MAX_ENTRIES_FOR_REUSE = 6000;
/*      */   private final ByteQuadsCanonicalizer _parent;
/*      */   private final AtomicReference<TableInfo> _tableInfo;
/*      */   private final int _seed;
/*      */   private boolean _intern;
/*      */   private final boolean _failOnDoS;
/*      */   private int[] _hashArea;
/*      */   private int _hashSize;
/*      */   private int _secondaryStart;
/*      */   private int _tertiaryStart;
/*      */   private int _tertiaryShift;
/*      */   private int _count;
/*      */   private String[] _names;
/*      */   private int _spilloverEnd;
/*      */   private int _longNameOffset;
/*      */   private boolean _hashShared;
/*      */   private static final int MULT = 33;
/*      */   private static final int MULT2 = 65599;
/*      */   private static final int MULT3 = 31;
/*      */   
/*      */   private ByteQuadsCanonicalizer(int sz, boolean intern, int seed, boolean failOnDoS) {
/*  228 */     this._parent = null;
/*  229 */     this._seed = seed;
/*  230 */     this._intern = intern;
/*  231 */     this._failOnDoS = failOnDoS;
/*      */     
/*  233 */     if (sz < 16) {
/*  234 */       sz = 16;
/*      */ 
/*      */     
/*      */     }
/*  238 */     else if ((sz & sz - 1) != 0) {
/*  239 */       int curr = 16;
/*  240 */       while (curr < sz) {
/*  241 */         curr += curr;
/*      */       }
/*  243 */       sz = curr;
/*      */     } 
/*      */     
/*  246 */     this._tableInfo = new AtomicReference<TableInfo>(TableInfo.createInitial(sz));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ByteQuadsCanonicalizer(ByteQuadsCanonicalizer parent, boolean intern, int seed, boolean failOnDoS, TableInfo state) {
/*  255 */     this._parent = parent;
/*  256 */     this._seed = seed;
/*  257 */     this._intern = intern;
/*  258 */     this._failOnDoS = failOnDoS;
/*  259 */     this._tableInfo = null;
/*      */ 
/*      */     
/*  262 */     this._count = state.count;
/*  263 */     this._hashSize = state.size;
/*  264 */     this._secondaryStart = this._hashSize << 2;
/*  265 */     this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
/*  266 */     this._tertiaryShift = state.tertiaryShift;
/*      */     
/*  268 */     this._hashArea = state.mainHash;
/*  269 */     this._names = state.names;
/*      */     
/*  271 */     this._spilloverEnd = state.spilloverEnd;
/*  272 */     this._longNameOffset = state.longNameOffset;
/*      */ 
/*      */     
/*  275 */     this._hashShared = true;
/*      */   }
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
/*      */   public static ByteQuadsCanonicalizer createRoot() {
/*  291 */     long now = System.currentTimeMillis();
/*      */     
/*  293 */     int seed = (int)now + (int)(now >>> 32L) | 0x1;
/*  294 */     return createRoot(seed);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected static ByteQuadsCanonicalizer createRoot(int seed) {
/*  300 */     return new ByteQuadsCanonicalizer(64, true, seed, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ByteQuadsCanonicalizer makeChild(int flags) {
/*  308 */     return new ByteQuadsCanonicalizer(this, JsonFactory.Feature.INTERN_FIELD_NAMES
/*  309 */         .enabledIn(flags), this._seed, JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW
/*      */         
/*  311 */         .enabledIn(flags), this._tableInfo
/*  312 */         .get());
/*      */   }
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
/*      */   public void release() {
/*  325 */     if (this._parent != null && maybeDirty()) {
/*  326 */       this._parent.mergeChild(new TableInfo(this));
/*      */ 
/*      */       
/*  329 */       this._hashShared = true;
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void mergeChild(TableInfo childState) {
/*  335 */     int childCount = childState.count;
/*  336 */     TableInfo currState = this._tableInfo.get();
/*      */ 
/*      */ 
/*      */     
/*  340 */     if (childCount == currState.count) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  348 */     if (childCount > 6000)
/*      */     {
/*  350 */       childState = TableInfo.createInitial(64);
/*      */     }
/*  352 */     this._tableInfo.compareAndSet(currState, childState);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int size() {
/*  363 */     if (this._tableInfo != null) {
/*  364 */       return ((TableInfo)this._tableInfo.get()).count;
/*      */     }
/*      */     
/*  367 */     return this._count;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public int bucketCount() {
/*  373 */     return this._hashSize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean maybeDirty() {
/*  380 */     return !this._hashShared;
/*      */   } public int hashSeed() {
/*  382 */     return this._seed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int primaryCount() {
/*  391 */     int count = 0;
/*  392 */     for (int offset = 3, end = this._secondaryStart; offset < end; offset += 4) {
/*  393 */       if (this._hashArea[offset] != 0) {
/*  394 */         count++;
/*      */       }
/*      */     } 
/*  397 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int secondaryCount() {
/*  405 */     int count = 0;
/*  406 */     int offset = this._secondaryStart + 3;
/*  407 */     for (int end = this._tertiaryStart; offset < end; offset += 4) {
/*  408 */       if (this._hashArea[offset] != 0) {
/*  409 */         count++;
/*      */       }
/*      */     } 
/*  412 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int tertiaryCount() {
/*  420 */     int count = 0;
/*  421 */     int offset = this._tertiaryStart + 3;
/*  422 */     for (int end = offset + this._hashSize; offset < end; offset += 4) {
/*  423 */       if (this._hashArea[offset] != 0) {
/*  424 */         count++;
/*      */       }
/*      */     } 
/*  427 */     return count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int spilloverCount() {
/*  436 */     return this._spilloverEnd - _spilloverStart() >> 2;
/*      */   }
/*      */ 
/*      */   
/*      */   public int totalCount() {
/*  441 */     int count = 0;
/*  442 */     for (int offset = 3, end = this._hashSize << 3; offset < end; offset += 4) {
/*  443 */       if (this._hashArea[offset] != 0) {
/*  444 */         count++;
/*      */       }
/*      */     } 
/*  447 */     return count;
/*      */   }
/*      */ 
/*      */   
/*      */   public String toString() {
/*  452 */     int pri = primaryCount();
/*  453 */     int sec = secondaryCount();
/*  454 */     int tert = tertiaryCount();
/*  455 */     int spill = spilloverCount();
/*  456 */     int total = totalCount();
/*  457 */     return String.format("[%s: size=%d, hashSize=%d, %d/%d/%d/%d pri/sec/ter/spill (=%s), total:%d]", new Object[] {
/*  458 */           getClass().getName(), Integer.valueOf(this._count), Integer.valueOf(this._hashSize), 
/*  459 */           Integer.valueOf(pri), Integer.valueOf(sec), Integer.valueOf(tert), Integer.valueOf(spill), Integer.valueOf(pri + sec + tert + spill), Integer.valueOf(total)
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String findName(int q1) {
/*  470 */     int offset = _calcOffset(calcHash(q1));
/*      */     
/*  472 */     int[] hashArea = this._hashArea;
/*      */     
/*  474 */     int len = hashArea[offset + 3];
/*      */     
/*  476 */     if (len == 1) {
/*  477 */       if (hashArea[offset] == q1) {
/*  478 */         return this._names[offset >> 2];
/*      */       }
/*  480 */     } else if (len == 0) {
/*  481 */       return null;
/*      */     } 
/*      */     
/*  484 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  486 */     len = hashArea[offset2 + 3];
/*      */     
/*  488 */     if (len == 1) {
/*  489 */       if (hashArea[offset2] == q1) {
/*  490 */         return this._names[offset2 >> 2];
/*      */       }
/*  492 */     } else if (len == 0) {
/*  493 */       return null;
/*      */     } 
/*      */ 
/*      */     
/*  497 */     return _findSecondary(offset, q1);
/*      */   }
/*      */ 
/*      */   
/*      */   public String findName(int q1, int q2) {
/*  502 */     int offset = _calcOffset(calcHash(q1, q2));
/*      */     
/*  504 */     int[] hashArea = this._hashArea;
/*      */     
/*  506 */     int len = hashArea[offset + 3];
/*      */     
/*  508 */     if (len == 2) {
/*  509 */       if (q1 == hashArea[offset] && q2 == hashArea[offset + 1]) {
/*  510 */         return this._names[offset >> 2];
/*      */       }
/*  512 */     } else if (len == 0) {
/*  513 */       return null;
/*      */     } 
/*      */     
/*  516 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  518 */     len = hashArea[offset2 + 3];
/*      */     
/*  520 */     if (len == 2) {
/*  521 */       if (q1 == hashArea[offset2] && q2 == hashArea[offset2 + 1]) {
/*  522 */         return this._names[offset2 >> 2];
/*      */       }
/*  524 */     } else if (len == 0) {
/*  525 */       return null;
/*      */     } 
/*  527 */     return _findSecondary(offset, q1, q2);
/*      */   }
/*      */ 
/*      */   
/*      */   public String findName(int q1, int q2, int q3) {
/*  532 */     int offset = _calcOffset(calcHash(q1, q2, q3));
/*  533 */     int[] hashArea = this._hashArea;
/*  534 */     int len = hashArea[offset + 3];
/*      */     
/*  536 */     if (len == 3) {
/*  537 */       if (q1 == hashArea[offset] && hashArea[offset + 1] == q2 && hashArea[offset + 2] == q3) {
/*  538 */         return this._names[offset >> 2];
/*      */       }
/*  540 */     } else if (len == 0) {
/*  541 */       return null;
/*      */     } 
/*      */     
/*  544 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  546 */     len = hashArea[offset2 + 3];
/*      */     
/*  548 */     if (len == 3) {
/*  549 */       if (q1 == hashArea[offset2] && hashArea[offset2 + 1] == q2 && hashArea[offset2 + 2] == q3) {
/*  550 */         return this._names[offset2 >> 2];
/*      */       }
/*  552 */     } else if (len == 0) {
/*  553 */       return null;
/*      */     } 
/*  555 */     return _findSecondary(offset, q1, q2, q3);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String findName(int[] q, int qlen) {
/*  564 */     if (qlen < 4) {
/*  565 */       switch (qlen) {
/*      */         case 3:
/*  567 */           return findName(q[0], q[1], q[2]);
/*      */         case 2:
/*  569 */           return findName(q[0], q[1]);
/*      */         case 1:
/*  571 */           return findName(q[0]);
/*      */       } 
/*  573 */       return "";
/*      */     } 
/*      */     
/*  576 */     int hash = calcHash(q, qlen);
/*  577 */     int offset = _calcOffset(hash);
/*      */     
/*  579 */     int[] hashArea = this._hashArea;
/*      */     
/*  581 */     int len = hashArea[offset + 3];
/*      */     
/*  583 */     if (hash == hashArea[offset] && len == qlen)
/*      */     {
/*  585 */       if (_verifyLongName(q, qlen, hashArea[offset + 1])) {
/*  586 */         return this._names[offset >> 2];
/*      */       }
/*      */     }
/*  589 */     if (len == 0) {
/*  590 */       return null;
/*      */     }
/*      */     
/*  593 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*      */     
/*  595 */     int len2 = hashArea[offset2 + 3];
/*  596 */     if (hash == hashArea[offset2] && len2 == qlen && 
/*  597 */       _verifyLongName(q, qlen, hashArea[offset2 + 1])) {
/*  598 */       return this._names[offset2 >> 2];
/*      */     }
/*      */     
/*  601 */     return _findSecondary(offset, hash, q, qlen);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int _calcOffset(int hash) {
/*  609 */     int ix = hash & this._hashSize - 1;
/*      */     
/*  611 */     return ix << 2;
/*      */   }
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
/*      */   private String _findSecondary(int origOffset, int q1) {
/*  626 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  627 */     int[] hashArea = this._hashArea;
/*  628 */     int bucketSize = 1 << this._tertiaryShift;
/*  629 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  630 */       int len = hashArea[offset + 3];
/*  631 */       if (q1 == hashArea[offset] && 1 == len) {
/*  632 */         return this._names[offset >> 2];
/*      */       }
/*  634 */       if (len == 0) {
/*  635 */         return null;
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  641 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  642 */       if (q1 == hashArea[offset] && 1 == hashArea[offset + 3]) {
/*  643 */         return this._names[offset >> 2];
/*      */       }
/*      */     } 
/*  646 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private String _findSecondary(int origOffset, int q1, int q2) {
/*  651 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  652 */     int[] hashArea = this._hashArea;
/*      */     
/*  654 */     int bucketSize = 1 << this._tertiaryShift;
/*  655 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  656 */       int len = hashArea[offset + 3];
/*  657 */       if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == len) {
/*  658 */         return this._names[offset >> 2];
/*      */       }
/*  660 */       if (len == 0) {
/*  661 */         return null;
/*      */       }
/*      */     } 
/*  664 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  665 */       if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && 2 == hashArea[offset + 3]) {
/*  666 */         return this._names[offset >> 2];
/*      */       }
/*      */     } 
/*  669 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private String _findSecondary(int origOffset, int q1, int q2, int q3) {
/*  674 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  675 */     int[] hashArea = this._hashArea;
/*      */     
/*  677 */     int bucketSize = 1 << this._tertiaryShift;
/*  678 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  679 */       int len = hashArea[offset + 3];
/*  680 */       if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == len) {
/*  681 */         return this._names[offset >> 2];
/*      */       }
/*  683 */       if (len == 0) {
/*  684 */         return null;
/*      */       }
/*      */     } 
/*  687 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  688 */       if (q1 == hashArea[offset] && q2 == hashArea[offset + 1] && q3 == hashArea[offset + 2] && 3 == hashArea[offset + 3])
/*      */       {
/*  690 */         return this._names[offset >> 2];
/*      */       }
/*      */     } 
/*  693 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private String _findSecondary(int origOffset, int hash, int[] q, int qlen) {
/*  698 */     int offset = this._tertiaryStart + (origOffset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  699 */     int[] hashArea = this._hashArea;
/*      */     
/*  701 */     int bucketSize = 1 << this._tertiaryShift;
/*  702 */     for (int end = offset + bucketSize; offset < end; offset += 4) {
/*  703 */       int len = hashArea[offset + 3];
/*  704 */       if (hash == hashArea[offset] && qlen == len && 
/*  705 */         _verifyLongName(q, qlen, hashArea[offset + 1])) {
/*  706 */         return this._names[offset >> 2];
/*      */       }
/*      */       
/*  709 */       if (len == 0) {
/*  710 */         return null;
/*      */       }
/*      */     } 
/*  713 */     for (offset = _spilloverStart(); offset < this._spilloverEnd; offset += 4) {
/*  714 */       if (hash == hashArea[offset] && qlen == hashArea[offset + 3] && 
/*  715 */         _verifyLongName(q, qlen, hashArea[offset + 1])) {
/*  716 */         return this._names[offset >> 2];
/*      */       }
/*      */     } 
/*      */     
/*  720 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean _verifyLongName(int[] q, int qlen, int spillOffset) {
/*  725 */     int[] hashArea = this._hashArea;
/*      */     
/*  727 */     int ix = 0;
/*      */     
/*  729 */     switch (qlen)
/*      */     { default:
/*  731 */         return _verifyLongName2(q, qlen, spillOffset);
/*      */       case 8:
/*  733 */         if (q[ix++] != hashArea[spillOffset++]) return false; 
/*      */       case 7:
/*  735 */         if (q[ix++] != hashArea[spillOffset++]) return false; 
/*      */       case 6:
/*  737 */         if (q[ix++] != hashArea[spillOffset++]) return false; 
/*      */       case 5:
/*  739 */         if (q[ix++] != hashArea[spillOffset++]) return false;  break;
/*      */       case 4:
/*  741 */         break; }  if (q[ix++] != hashArea[spillOffset++]) return false; 
/*  742 */     if (q[ix++] != hashArea[spillOffset++]) return false; 
/*  743 */     if (q[ix++] != hashArea[spillOffset++]) return false; 
/*  744 */     if (q[ix++] != hashArea[spillOffset++]) return false;
/*      */     
/*  746 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean _verifyLongName2(int[] q, int qlen, int spillOffset) {
/*  751 */     int ix = 0;
/*      */     while (true) {
/*  753 */       if (q[ix++] != this._hashArea[spillOffset++]) {
/*  754 */         return false;
/*      */       }
/*  756 */       if (ix >= qlen) {
/*  757 */         return true;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String addName(String name, int q1) {
/*  767 */     _verifySharing();
/*  768 */     if (this._intern) {
/*  769 */       name = InternCache.instance.intern(name);
/*      */     }
/*  771 */     int offset = _findOffsetForAdd(calcHash(q1));
/*  772 */     this._hashArea[offset] = q1;
/*  773 */     this._hashArea[offset + 3] = 1;
/*  774 */     this._names[offset >> 2] = name;
/*  775 */     this._count++;
/*  776 */     return name;
/*      */   }
/*      */   
/*      */   public String addName(String name, int q1, int q2) {
/*  780 */     _verifySharing();
/*  781 */     if (this._intern) {
/*  782 */       name = InternCache.instance.intern(name);
/*      */     }
/*  784 */     int hash = (q2 == 0) ? calcHash(q1) : calcHash(q1, q2);
/*  785 */     int offset = _findOffsetForAdd(hash);
/*  786 */     this._hashArea[offset] = q1;
/*  787 */     this._hashArea[offset + 1] = q2;
/*  788 */     this._hashArea[offset + 3] = 2;
/*  789 */     this._names[offset >> 2] = name;
/*  790 */     this._count++;
/*  791 */     return name;
/*      */   }
/*      */   
/*      */   public String addName(String name, int q1, int q2, int q3) {
/*  795 */     _verifySharing();
/*  796 */     if (this._intern) {
/*  797 */       name = InternCache.instance.intern(name);
/*      */     }
/*  799 */     int offset = _findOffsetForAdd(calcHash(q1, q2, q3));
/*  800 */     this._hashArea[offset] = q1;
/*  801 */     this._hashArea[offset + 1] = q2;
/*  802 */     this._hashArea[offset + 2] = q3;
/*  803 */     this._hashArea[offset + 3] = 3;
/*  804 */     this._names[offset >> 2] = name;
/*  805 */     this._count++;
/*  806 */     return name;
/*      */   }
/*      */ 
/*      */   
/*      */   public String addName(String name, int[] q, int qlen) {
/*  811 */     _verifySharing();
/*  812 */     if (this._intern) {
/*  813 */       name = InternCache.instance.intern(name);
/*      */     }
/*      */ 
/*      */     
/*  817 */     switch (qlen)
/*      */     
/*      */     { case 1:
/*  820 */         offset = _findOffsetForAdd(calcHash(q[0]));
/*  821 */         this._hashArea[offset] = q[0];
/*  822 */         this._hashArea[offset + 3] = 1;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  852 */         this._names[offset >> 2] = name;
/*      */ 
/*      */         
/*  855 */         this._count++;
/*  856 */         return name;case 2: offset = _findOffsetForAdd(calcHash(q[0], q[1])); this._hashArea[offset] = q[0]; this._hashArea[offset + 1] = q[1]; this._hashArea[offset + 3] = 2; this._names[offset >> 2] = name; this._count++; return name;case 3: offset = _findOffsetForAdd(calcHash(q[0], q[1], q[2])); this._hashArea[offset] = q[0]; this._hashArea[offset + 1] = q[1]; this._hashArea[offset + 2] = q[2]; this._hashArea[offset + 3] = 3; this._names[offset >> 2] = name; this._count++; return name; }  int hash = calcHash(q, qlen); int offset = _findOffsetForAdd(hash); this._hashArea[offset] = hash; int longStart = _appendLongName(q, qlen); this._hashArea[offset + 1] = longStart; this._hashArea[offset + 3] = qlen; this._names[offset >> 2] = name; this._count++; return name;
/*      */   }
/*      */ 
/*      */   
/*      */   private void _verifySharing() {
/*  861 */     if (this._hashShared) {
/*  862 */       this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length);
/*  863 */       this._names = Arrays.<String>copyOf(this._names, this._names.length);
/*  864 */       this._hashShared = false;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int _findOffsetForAdd(int hash) {
/*  874 */     int offset = _calcOffset(hash);
/*  875 */     int[] hashArea = this._hashArea;
/*  876 */     if (hashArea[offset + 3] == 0)
/*      */     {
/*  878 */       return offset;
/*      */     }
/*      */ 
/*      */     
/*  882 */     if (_checkNeedForRehash()) {
/*  883 */       return _resizeAndFindOffsetForAdd(hash);
/*      */     }
/*      */ 
/*      */     
/*  887 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*  888 */     if (hashArea[offset2 + 3] == 0)
/*      */     {
/*  890 */       return offset2;
/*      */     }
/*      */ 
/*      */     
/*  894 */     offset2 = this._tertiaryStart + (offset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  895 */     int bucketSize = 1 << this._tertiaryShift; int end;
/*  896 */     for (end = offset2 + bucketSize; offset2 < end; offset2 += 4) {
/*  897 */       if (hashArea[offset2 + 3] == 0)
/*      */       {
/*  899 */         return offset2;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  904 */     offset = this._spilloverEnd;
/*  905 */     this._spilloverEnd += 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  915 */     end = this._hashSize << 3;
/*  916 */     if (this._spilloverEnd >= end) {
/*  917 */       if (this._failOnDoS) {
/*  918 */         _reportTooManyCollisions();
/*      */       }
/*  920 */       return _resizeAndFindOffsetForAdd(hash);
/*      */     } 
/*  922 */     return offset;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int _resizeAndFindOffsetForAdd(int hash) {
/*  929 */     rehash();
/*      */ 
/*      */     
/*  932 */     int offset = _calcOffset(hash);
/*  933 */     int[] hashArea = this._hashArea;
/*  934 */     if (hashArea[offset + 3] == 0) {
/*  935 */       return offset;
/*      */     }
/*  937 */     int offset2 = this._secondaryStart + (offset >> 3 << 2);
/*  938 */     if (hashArea[offset2 + 3] == 0) {
/*  939 */       return offset2;
/*      */     }
/*  941 */     offset2 = this._tertiaryStart + (offset >> this._tertiaryShift + 2 << this._tertiaryShift);
/*  942 */     int bucketSize = 1 << this._tertiaryShift;
/*  943 */     for (int end = offset2 + bucketSize; offset2 < end; offset2 += 4) {
/*  944 */       if (hashArea[offset2 + 3] == 0) {
/*  945 */         return offset2;
/*      */       }
/*      */     } 
/*  948 */     offset = this._spilloverEnd;
/*  949 */     this._spilloverEnd += 4;
/*  950 */     return offset;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean _checkNeedForRehash() {
/*  956 */     if (this._count > this._hashSize >> 1) {
/*  957 */       int spillCount = this._spilloverEnd - _spilloverStart() >> 2;
/*  958 */       if (spillCount > 1 + this._count >> 7 || this._count > this._hashSize * 0.8D)
/*      */       {
/*  960 */         return true;
/*      */       }
/*      */     } 
/*  963 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private int _appendLongName(int[] quads, int qlen) {
/*  968 */     int start = this._longNameOffset;
/*      */ 
/*      */     
/*  971 */     if (start + qlen > this._hashArea.length) {
/*      */       
/*  973 */       int toAdd = start + qlen - this._hashArea.length;
/*      */       
/*  975 */       int minAdd = Math.min(4096, this._hashSize);
/*      */       
/*  977 */       int newSize = this._hashArea.length + Math.max(toAdd, minAdd);
/*  978 */       this._hashArea = Arrays.copyOf(this._hashArea, newSize);
/*      */     } 
/*  980 */     System.arraycopy(quads, 0, this._hashArea, start, qlen);
/*  981 */     this._longNameOffset += qlen;
/*  982 */     return start;
/*      */   }
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
/*      */ 
/*      */   
/*      */   public int calcHash(int q1) {
/* 1007 */     int hash = q1 ^ this._seed;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1013 */     hash += hash >>> 16;
/* 1014 */     hash ^= hash << 3;
/* 1015 */     hash += hash >>> 12;
/* 1016 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int calcHash(int q1, int q2) {
/* 1023 */     int hash = q1;
/*      */     
/* 1025 */     hash += hash >>> 15;
/* 1026 */     hash ^= hash >>> 9;
/* 1027 */     hash += q2 * 33;
/* 1028 */     hash ^= this._seed;
/* 1029 */     hash += hash >>> 16;
/* 1030 */     hash ^= hash >>> 4;
/* 1031 */     hash += hash << 3;
/*      */     
/* 1033 */     return hash;
/*      */   }
/*      */ 
/*      */   
/*      */   public int calcHash(int q1, int q2, int q3) {
/* 1038 */     int hash = q1 ^ this._seed;
/* 1039 */     hash += hash >>> 9;
/* 1040 */     hash *= 31;
/* 1041 */     hash += q2;
/* 1042 */     hash *= 33;
/* 1043 */     hash += hash >>> 15;
/* 1044 */     hash ^= q3;
/*      */     
/* 1046 */     hash += hash >>> 4;
/*      */     
/* 1048 */     hash += hash >>> 15;
/* 1049 */     hash ^= hash << 9;
/*      */     
/* 1051 */     return hash;
/*      */   }
/*      */ 
/*      */   
/*      */   public int calcHash(int[] q, int qlen) {
/* 1056 */     if (qlen < 4) {
/* 1057 */       throw new IllegalArgumentException();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1064 */     int hash = q[0] ^ this._seed;
/* 1065 */     hash += hash >>> 9;
/* 1066 */     hash += q[1];
/* 1067 */     hash += hash >>> 15;
/* 1068 */     hash *= 33;
/* 1069 */     hash ^= q[2];
/* 1070 */     hash += hash >>> 4;
/*      */     
/* 1072 */     for (int i = 3; i < qlen; i++) {
/* 1073 */       int next = q[i];
/* 1074 */       next ^= next >> 21;
/* 1075 */       hash += next;
/*      */     } 
/* 1077 */     hash *= 65599;
/*      */ 
/*      */     
/* 1080 */     hash += hash >>> 19;
/* 1081 */     hash ^= hash << 5;
/* 1082 */     return hash;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void rehash() {
/* 1094 */     this._hashShared = false;
/*      */ 
/*      */ 
/*      */     
/* 1098 */     int[] oldHashArea = this._hashArea;
/* 1099 */     String[] oldNames = this._names;
/* 1100 */     int oldSize = this._hashSize;
/* 1101 */     int oldCount = this._count;
/* 1102 */     int newSize = oldSize + oldSize;
/* 1103 */     int oldEnd = this._spilloverEnd;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1108 */     if (newSize > 65536) {
/* 1109 */       nukeSymbols(true);
/*      */       
/*      */       return;
/*      */     } 
/* 1113 */     this._hashArea = new int[oldHashArea.length + (oldSize << 3)];
/* 1114 */     this._hashSize = newSize;
/* 1115 */     this._secondaryStart = newSize << 2;
/* 1116 */     this._tertiaryStart = this._secondaryStart + (this._secondaryStart >> 1);
/* 1117 */     this._tertiaryShift = _calcTertiaryShift(newSize);
/*      */ 
/*      */     
/* 1120 */     this._names = new String[oldNames.length << 1];
/* 1121 */     nukeSymbols(false);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1128 */     int copyCount = 0;
/* 1129 */     int[] q = new int[16];
/* 1130 */     for (int offset = 0, end = oldEnd; offset < end; offset += 4) {
/* 1131 */       int len = oldHashArea[offset + 3];
/* 1132 */       if (len != 0) {
/*      */         int qoff;
/*      */         
/* 1135 */         copyCount++;
/* 1136 */         String name = oldNames[offset >> 2];
/* 1137 */         switch (len) {
/*      */           case 1:
/* 1139 */             q[0] = oldHashArea[offset];
/* 1140 */             addName(name, q, 1);
/*      */             break;
/*      */           case 2:
/* 1143 */             q[0] = oldHashArea[offset];
/* 1144 */             q[1] = oldHashArea[offset + 1];
/* 1145 */             addName(name, q, 2);
/*      */             break;
/*      */           case 3:
/* 1148 */             q[0] = oldHashArea[offset];
/* 1149 */             q[1] = oldHashArea[offset + 1];
/* 1150 */             q[2] = oldHashArea[offset + 2];
/* 1151 */             addName(name, q, 3);
/*      */             break;
/*      */           default:
/* 1154 */             if (len > q.length) {
/* 1155 */               q = new int[len];
/*      */             }
/*      */             
/* 1158 */             qoff = oldHashArea[offset + 1];
/* 1159 */             System.arraycopy(oldHashArea, qoff, q, 0, len);
/* 1160 */             addName(name, q, len);
/*      */             break;
/*      */         } 
/*      */ 
/*      */       
/*      */       } 
/*      */     } 
/* 1167 */     if (copyCount != oldCount) {
/* 1168 */       throw new IllegalStateException("Failed rehash(): old count=" + oldCount + ", copyCount=" + copyCount);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void nukeSymbols(boolean fill) {
/* 1177 */     this._count = 0;
/*      */     
/* 1179 */     this._spilloverEnd = _spilloverStart();
/*      */     
/* 1181 */     this._longNameOffset = this._hashSize << 3;
/* 1182 */     if (fill) {
/* 1183 */       Arrays.fill(this._hashArea, 0);
/* 1184 */       Arrays.fill((Object[])this._names, (Object)null);
/*      */     } 
/*      */   }
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
/*      */   private final int _spilloverStart() {
/* 1200 */     int offset = this._hashSize;
/* 1201 */     return (offset << 3) - offset;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void _reportTooManyCollisions() {
/* 1207 */     if (this._hashSize <= 1024) {
/*      */       return;
/*      */     }
/* 1210 */     throw new IllegalStateException("Spill-over slots in symbol table with " + this._count + " entries, hash area of " + this._hashSize + " slots is now full (all " + (this._hashSize >> 3) + " slots -- suspect a DoS attack based on hash collisions. You can disable the check via `JsonFactory.Feature.FAIL_ON_SYMBOL_HASH_OVERFLOW`");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static int _calcTertiaryShift(int primarySlots) {
/* 1219 */     int tertSlots = primarySlots >> 2;
/*      */ 
/*      */     
/* 1222 */     if (tertSlots < 64) {
/* 1223 */       return 4;
/*      */     }
/* 1225 */     if (tertSlots <= 256) {
/* 1226 */       return 5;
/*      */     }
/* 1228 */     if (tertSlots <= 1024) {
/* 1229 */       return 6;
/*      */     }
/*      */     
/* 1232 */     return 7;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static final class TableInfo
/*      */   {
/*      */     public final int size;
/*      */ 
/*      */     
/*      */     public final int count;
/*      */ 
/*      */     
/*      */     public final int tertiaryShift;
/*      */ 
/*      */     
/*      */     public final int[] mainHash;
/*      */ 
/*      */     
/*      */     public final String[] names;
/*      */ 
/*      */     
/*      */     public final int spilloverEnd;
/*      */ 
/*      */     
/*      */     public final int longNameOffset;
/*      */ 
/*      */     
/*      */     public TableInfo(int size, int count, int tertiaryShift, int[] mainHash, String[] names, int spilloverEnd, int longNameOffset) {
/* 1261 */       this.size = size;
/* 1262 */       this.count = count;
/* 1263 */       this.tertiaryShift = tertiaryShift;
/* 1264 */       this.mainHash = mainHash;
/* 1265 */       this.names = names;
/* 1266 */       this.spilloverEnd = spilloverEnd;
/* 1267 */       this.longNameOffset = longNameOffset;
/*      */     }
/*      */ 
/*      */     
/*      */     public TableInfo(ByteQuadsCanonicalizer src) {
/* 1272 */       this.size = src._hashSize;
/* 1273 */       this.count = src._count;
/* 1274 */       this.tertiaryShift = src._tertiaryShift;
/* 1275 */       this.mainHash = src._hashArea;
/* 1276 */       this.names = src._names;
/* 1277 */       this.spilloverEnd = src._spilloverEnd;
/* 1278 */       this.longNameOffset = src._longNameOffset;
/*      */     }
/*      */     
/*      */     public static TableInfo createInitial(int sz) {
/* 1282 */       int hashAreaSize = sz << 3;
/* 1283 */       int tertShift = ByteQuadsCanonicalizer._calcTertiaryShift(sz);
/*      */       
/* 1285 */       return new TableInfo(sz, 0, tertShift, new int[hashAreaSize], new String[sz << 1], hashAreaSize - sz, hashAreaSize);
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\sym\ByteQuadsCanonicalizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */