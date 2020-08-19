/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.io.NumberInput;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class TextBuffer
/*     */ {
/*  29 */   static final char[] NO_CHARS = new char[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int MIN_SEGMENT_LEN = 500;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static final int MAX_SEGMENT_LEN = 65536;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final BufferRecycler _allocator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char[] _inputBuffer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int _inputStart;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int _inputLen;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ArrayList<char[]> _segments;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean _hasSegments;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int _segmentSize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char[] _currentSegment;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int _currentSize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String _resultString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char[] _resultArray;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TextBuffer(BufferRecycler allocator) {
/* 124 */     this._allocator = allocator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected TextBuffer(BufferRecycler allocator, char[] initialSegment) {
/* 131 */     this._allocator = allocator;
/* 132 */     this._currentSegment = initialSegment;
/* 133 */     this._currentSize = initialSegment.length;
/* 134 */     this._inputStart = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static TextBuffer fromInitial(char[] initialSegment) {
/* 144 */     return new TextBuffer(null, initialSegment);
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
/*     */   public void releaseBuffers() {
/* 158 */     if (this._allocator == null) {
/* 159 */       resetWithEmpty();
/*     */     }
/* 161 */     else if (this._currentSegment != null) {
/*     */       
/* 163 */       resetWithEmpty();
/*     */       
/* 165 */       char[] buf = this._currentSegment;
/* 166 */       this._currentSegment = null;
/* 167 */       this._allocator.releaseCharBuffer(2, buf);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetWithEmpty() {
/* 178 */     this._inputStart = -1;
/* 179 */     this._currentSize = 0;
/* 180 */     this._inputLen = 0;
/*     */     
/* 182 */     this._inputBuffer = null;
/* 183 */     this._resultString = null;
/* 184 */     this._resultArray = null;
/*     */ 
/*     */     
/* 187 */     if (this._hasSegments) {
/* 188 */       clearSegments();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetWith(char ch) {
/* 197 */     this._inputStart = -1;
/* 198 */     this._inputLen = 0;
/*     */     
/* 200 */     this._resultString = null;
/* 201 */     this._resultArray = null;
/*     */     
/* 203 */     if (this._hasSegments) {
/* 204 */       clearSegments();
/* 205 */     } else if (this._currentSegment == null) {
/* 206 */       this._currentSegment = buf(1);
/*     */     } 
/* 208 */     this._currentSegment[0] = ch;
/* 209 */     this._currentSize = this._segmentSize = 1;
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
/*     */   public void resetWithShared(char[] buf, int start, int len) {
/* 221 */     this._resultString = null;
/* 222 */     this._resultArray = null;
/*     */ 
/*     */     
/* 225 */     this._inputBuffer = buf;
/* 226 */     this._inputStart = start;
/* 227 */     this._inputLen = len;
/*     */ 
/*     */     
/* 230 */     if (this._hasSegments) {
/* 231 */       clearSegments();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetWithCopy(char[] buf, int start, int len) {
/* 237 */     this._inputBuffer = null;
/* 238 */     this._inputStart = -1;
/* 239 */     this._inputLen = 0;
/*     */     
/* 241 */     this._resultString = null;
/* 242 */     this._resultArray = null;
/*     */ 
/*     */     
/* 245 */     if (this._hasSegments) {
/* 246 */       clearSegments();
/* 247 */     } else if (this._currentSegment == null) {
/* 248 */       this._currentSegment = buf(len);
/*     */     } 
/* 250 */     this._currentSize = this._segmentSize = 0;
/* 251 */     append(buf, start, len);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void resetWithCopy(String text, int start, int len) {
/* 259 */     this._inputBuffer = null;
/* 260 */     this._inputStart = -1;
/* 261 */     this._inputLen = 0;
/*     */     
/* 263 */     this._resultString = null;
/* 264 */     this._resultArray = null;
/*     */     
/* 266 */     if (this._hasSegments) {
/* 267 */       clearSegments();
/* 268 */     } else if (this._currentSegment == null) {
/* 269 */       this._currentSegment = buf(len);
/*     */     } 
/* 271 */     this._currentSize = this._segmentSize = 0;
/* 272 */     append(text, start, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetWithString(String value) {
/* 277 */     this._inputBuffer = null;
/* 278 */     this._inputStart = -1;
/* 279 */     this._inputLen = 0;
/*     */     
/* 281 */     this._resultString = value;
/* 282 */     this._resultArray = null;
/*     */     
/* 284 */     if (this._hasSegments) {
/* 285 */       clearSegments();
/*     */     }
/* 287 */     this._currentSize = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] getBufferWithoutReset() {
/* 295 */     return this._currentSegment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private char[] buf(int needed) {
/* 304 */     if (this._allocator != null) {
/* 305 */       return this._allocator.allocCharBuffer(2, needed);
/*     */     }
/* 307 */     return new char[Math.max(needed, 500)];
/*     */   }
/*     */ 
/*     */   
/*     */   private void clearSegments() {
/* 312 */     this._hasSegments = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 320 */     this._segments.clear();
/* 321 */     this._currentSize = this._segmentSize = 0;
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
/*     */   public int size() {
/* 334 */     if (this._inputStart >= 0) {
/* 335 */       return this._inputLen;
/*     */     }
/* 337 */     if (this._resultArray != null) {
/* 338 */       return this._resultArray.length;
/*     */     }
/* 340 */     if (this._resultString != null) {
/* 341 */       return this._resultString.length();
/*     */     }
/*     */     
/* 344 */     return this._segmentSize + this._currentSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTextOffset() {
/* 352 */     return (this._inputStart >= 0) ? this._inputStart : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasTextAsCharacters() {
/* 362 */     if (this._inputStart >= 0 || this._resultArray != null) return true;
/*     */     
/* 364 */     if (this._resultString != null) return false; 
/* 365 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] getTextBuffer() {
/* 376 */     if (this._inputStart >= 0) return this._inputBuffer; 
/* 377 */     if (this._resultArray != null) return this._resultArray; 
/* 378 */     if (this._resultString != null) {
/* 379 */       return this._resultArray = this._resultString.toCharArray();
/*     */     }
/*     */     
/* 382 */     if (!this._hasSegments) {
/* 383 */       return (this._currentSegment == null) ? NO_CHARS : this._currentSegment;
/*     */     }
/*     */     
/* 386 */     return contentsAsArray();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String contentsAsString() {
/* 397 */     if (this._resultString == null)
/*     */     {
/* 399 */       if (this._resultArray != null) {
/* 400 */         this._resultString = new String(this._resultArray);
/*     */       
/*     */       }
/* 403 */       else if (this._inputStart >= 0) {
/* 404 */         if (this._inputLen < 1) {
/* 405 */           return this._resultString = "";
/*     */         }
/* 407 */         this._resultString = new String(this._inputBuffer, this._inputStart, this._inputLen);
/*     */       } else {
/*     */         
/* 410 */         int segLen = this._segmentSize;
/* 411 */         int currLen = this._currentSize;
/*     */         
/* 413 */         if (segLen == 0) {
/* 414 */           this._resultString = (currLen == 0) ? "" : new String(this._currentSegment, 0, currLen);
/*     */         } else {
/* 416 */           StringBuilder sb = new StringBuilder(segLen + currLen);
/*     */           
/* 418 */           if (this._segments != null) {
/* 419 */             for (int i = 0, len = this._segments.size(); i < len; i++) {
/* 420 */               char[] curr = this._segments.get(i);
/* 421 */               sb.append(curr, 0, curr.length);
/*     */             } 
/*     */           }
/*     */           
/* 425 */           sb.append(this._currentSegment, 0, this._currentSize);
/* 426 */           this._resultString = sb.toString();
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 431 */     return this._resultString;
/*     */   }
/*     */   
/*     */   public char[] contentsAsArray() {
/* 435 */     char[] result = this._resultArray;
/* 436 */     if (result == null) {
/* 437 */       this._resultArray = result = resultArray();
/*     */     }
/* 439 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigDecimal contentsAsDecimal() throws NumberFormatException {
/* 449 */     if (this._resultArray != null) {
/* 450 */       return NumberInput.parseBigDecimal(this._resultArray);
/*     */     }
/*     */     
/* 453 */     if (this._inputStart >= 0 && this._inputBuffer != null) {
/* 454 */       return NumberInput.parseBigDecimal(this._inputBuffer, this._inputStart, this._inputLen);
/*     */     }
/*     */     
/* 457 */     if (this._segmentSize == 0 && this._currentSegment != null) {
/* 458 */       return NumberInput.parseBigDecimal(this._currentSegment, 0, this._currentSize);
/*     */     }
/*     */     
/* 461 */     return NumberInput.parseBigDecimal(contentsAsArray());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double contentsAsDouble() throws NumberFormatException {
/* 469 */     return NumberInput.parseDouble(contentsAsString());
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
/*     */   public int contentsAsInt(boolean neg) {
/* 481 */     if (this._inputStart >= 0 && this._inputBuffer != null) {
/* 482 */       if (neg) {
/* 483 */         return -NumberInput.parseInt(this._inputBuffer, this._inputStart + 1, this._inputLen - 1);
/*     */       }
/* 485 */       return NumberInput.parseInt(this._inputBuffer, this._inputStart, this._inputLen);
/*     */     } 
/* 487 */     if (neg) {
/* 488 */       return -NumberInput.parseInt(this._currentSegment, 1, this._currentSize - 1);
/*     */     }
/* 490 */     return NumberInput.parseInt(this._currentSegment, 0, this._currentSize);
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
/*     */   public long contentsAsLong(boolean neg) {
/* 502 */     if (this._inputStart >= 0 && this._inputBuffer != null) {
/* 503 */       if (neg) {
/* 504 */         return -NumberInput.parseLong(this._inputBuffer, this._inputStart + 1, this._inputLen - 1);
/*     */       }
/* 506 */       return NumberInput.parseLong(this._inputBuffer, this._inputStart, this._inputLen);
/*     */     } 
/* 508 */     if (neg) {
/* 509 */       return -NumberInput.parseLong(this._currentSegment, 1, this._currentSize - 1);
/*     */     }
/* 511 */     return NumberInput.parseLong(this._currentSegment, 0, this._currentSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int contentsToWriter(Writer w) throws IOException {
/* 519 */     if (this._resultArray != null) {
/* 520 */       w.write(this._resultArray);
/* 521 */       return this._resultArray.length;
/*     */     } 
/* 523 */     if (this._resultString != null) {
/* 524 */       w.write(this._resultString);
/* 525 */       return this._resultString.length();
/*     */     } 
/*     */     
/* 528 */     if (this._inputStart >= 0) {
/* 529 */       int i = this._inputLen;
/* 530 */       if (i > 0) {
/* 531 */         w.write(this._inputBuffer, this._inputStart, i);
/*     */       }
/* 533 */       return i;
/*     */     } 
/*     */     
/* 536 */     int total = 0;
/* 537 */     if (this._segments != null) {
/* 538 */       for (int i = 0, end = this._segments.size(); i < end; i++) {
/* 539 */         char[] curr = this._segments.get(i);
/* 540 */         int currLen = curr.length;
/* 541 */         w.write(curr, 0, currLen);
/* 542 */         total += currLen;
/*     */       } 
/*     */     }
/* 545 */     int len = this._currentSize;
/* 546 */     if (len > 0) {
/* 547 */       w.write(this._currentSegment, 0, len);
/* 548 */       total += len;
/*     */     } 
/* 550 */     return total;
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
/*     */   public void ensureNotShared() {
/* 564 */     if (this._inputStart >= 0) {
/* 565 */       unshare(16);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void append(char c) {
/* 571 */     if (this._inputStart >= 0) {
/* 572 */       unshare(16);
/*     */     }
/* 574 */     this._resultString = null;
/* 575 */     this._resultArray = null;
/*     */     
/* 577 */     char[] curr = this._currentSegment;
/* 578 */     if (this._currentSize >= curr.length) {
/* 579 */       expand(1);
/* 580 */       curr = this._currentSegment;
/*     */     } 
/* 582 */     curr[this._currentSize++] = c;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(char[] c, int start, int len) {
/* 588 */     if (this._inputStart >= 0) {
/* 589 */       unshare(len);
/*     */     }
/* 591 */     this._resultString = null;
/* 592 */     this._resultArray = null;
/*     */ 
/*     */     
/* 595 */     char[] curr = this._currentSegment;
/* 596 */     int max = curr.length - this._currentSize;
/*     */     
/* 598 */     if (max >= len) {
/* 599 */       System.arraycopy(c, start, curr, this._currentSize, len);
/* 600 */       this._currentSize += len;
/*     */       
/*     */       return;
/*     */     } 
/* 604 */     if (max > 0) {
/* 605 */       System.arraycopy(c, start, curr, this._currentSize, max);
/* 606 */       start += max;
/* 607 */       len -= max;
/*     */     } 
/*     */ 
/*     */     
/*     */     do {
/* 612 */       expand(len);
/* 613 */       int amount = Math.min(this._currentSegment.length, len);
/* 614 */       System.arraycopy(c, start, this._currentSegment, 0, amount);
/* 615 */       this._currentSize += amount;
/* 616 */       start += amount;
/* 617 */       len -= amount;
/* 618 */     } while (len > 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void append(String str, int offset, int len) {
/* 624 */     if (this._inputStart >= 0) {
/* 625 */       unshare(len);
/*     */     }
/* 627 */     this._resultString = null;
/* 628 */     this._resultArray = null;
/*     */ 
/*     */     
/* 631 */     char[] curr = this._currentSegment;
/* 632 */     int max = curr.length - this._currentSize;
/* 633 */     if (max >= len) {
/* 634 */       str.getChars(offset, offset + len, curr, this._currentSize);
/* 635 */       this._currentSize += len;
/*     */       
/*     */       return;
/*     */     } 
/* 639 */     if (max > 0) {
/* 640 */       str.getChars(offset, offset + max, curr, this._currentSize);
/* 641 */       len -= max;
/* 642 */       offset += max;
/*     */     } 
/*     */ 
/*     */     
/*     */     do {
/* 647 */       expand(len);
/* 648 */       int amount = Math.min(this._currentSegment.length, len);
/* 649 */       str.getChars(offset, offset + amount, this._currentSegment, 0);
/* 650 */       this._currentSize += amount;
/* 651 */       offset += amount;
/* 652 */       len -= amount;
/* 653 */     } while (len > 0);
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
/*     */   public char[] getCurrentSegment() {
/* 668 */     if (this._inputStart >= 0) {
/* 669 */       unshare(1);
/*     */     } else {
/* 671 */       char[] curr = this._currentSegment;
/* 672 */       if (curr == null) {
/* 673 */         this._currentSegment = buf(0);
/* 674 */       } else if (this._currentSize >= curr.length) {
/*     */         
/* 676 */         expand(1);
/*     */       } 
/*     */     } 
/* 679 */     return this._currentSegment;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] emptyAndGetCurrentSegment() {
/* 685 */     this._inputStart = -1;
/* 686 */     this._currentSize = 0;
/* 687 */     this._inputLen = 0;
/*     */     
/* 689 */     this._inputBuffer = null;
/* 690 */     this._resultString = null;
/* 691 */     this._resultArray = null;
/*     */ 
/*     */     
/* 694 */     if (this._hasSegments) {
/* 695 */       clearSegments();
/*     */     }
/* 697 */     char[] curr = this._currentSegment;
/* 698 */     if (curr == null) {
/* 699 */       this._currentSegment = curr = buf(0);
/*     */     }
/* 701 */     return curr;
/*     */   }
/*     */   
/* 704 */   public int getCurrentSegmentSize() { return this._currentSize; } public void setCurrentLength(int len) {
/* 705 */     this._currentSize = len;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String setCurrentAndReturn(int len) {
/* 711 */     this._currentSize = len;
/*     */     
/* 713 */     if (this._segmentSize > 0) {
/* 714 */       return contentsAsString();
/*     */     }
/*     */     
/* 717 */     int currLen = this._currentSize;
/* 718 */     String str = (currLen == 0) ? "" : new String(this._currentSegment, 0, currLen);
/* 719 */     this._resultString = str;
/* 720 */     return str;
/*     */   }
/*     */   
/*     */   public char[] finishCurrentSegment() {
/* 724 */     if (this._segments == null) {
/* 725 */       this._segments = (ArrayList)new ArrayList<char>();
/*     */     }
/* 727 */     this._hasSegments = true;
/* 728 */     this._segments.add(this._currentSegment);
/* 729 */     int oldLen = this._currentSegment.length;
/* 730 */     this._segmentSize += oldLen;
/* 731 */     this._currentSize = 0;
/*     */ 
/*     */     
/* 734 */     int newLen = oldLen + (oldLen >> 1);
/* 735 */     if (newLen < 500) {
/* 736 */       newLen = 500;
/* 737 */     } else if (newLen > 65536) {
/* 738 */       newLen = 65536;
/*     */     } 
/* 740 */     char[] curr = carr(newLen);
/* 741 */     this._currentSegment = curr;
/* 742 */     return curr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char[] expandCurrentSegment() {
/* 752 */     char[] curr = this._currentSegment;
/*     */     
/* 754 */     int len = curr.length;
/* 755 */     int newLen = len + (len >> 1);
/*     */     
/* 757 */     if (newLen > 65536) {
/* 758 */       newLen = len + (len >> 2);
/*     */     }
/* 760 */     return this._currentSegment = Arrays.copyOf(curr, newLen);
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
/*     */   public char[] expandCurrentSegment(int minSize) {
/* 773 */     char[] curr = this._currentSegment;
/* 774 */     if (curr.length >= minSize) return curr; 
/* 775 */     this._currentSegment = curr = Arrays.copyOf(curr, minSize);
/* 776 */     return curr;
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
/*     */   public String toString() {
/* 790 */     return contentsAsString();
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
/*     */   private void unshare(int needExtra) {
/* 804 */     int sharedLen = this._inputLen;
/* 805 */     this._inputLen = 0;
/* 806 */     char[] inputBuf = this._inputBuffer;
/* 807 */     this._inputBuffer = null;
/* 808 */     int start = this._inputStart;
/* 809 */     this._inputStart = -1;
/*     */ 
/*     */     
/* 812 */     int needed = sharedLen + needExtra;
/* 813 */     if (this._currentSegment == null || needed > this._currentSegment.length) {
/* 814 */       this._currentSegment = buf(needed);
/*     */     }
/* 816 */     if (sharedLen > 0) {
/* 817 */       System.arraycopy(inputBuf, start, this._currentSegment, 0, sharedLen);
/*     */     }
/* 819 */     this._segmentSize = 0;
/* 820 */     this._currentSize = sharedLen;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void expand(int minNewSegmentSize) {
/* 830 */     if (this._segments == null) {
/* 831 */       this._segments = (ArrayList)new ArrayList<char>();
/*     */     }
/* 833 */     char[] curr = this._currentSegment;
/* 834 */     this._hasSegments = true;
/* 835 */     this._segments.add(curr);
/* 836 */     this._segmentSize += curr.length;
/* 837 */     this._currentSize = 0;
/* 838 */     int oldLen = curr.length;
/*     */ 
/*     */     
/* 841 */     int newLen = oldLen + (oldLen >> 1);
/* 842 */     if (newLen < 500) {
/* 843 */       newLen = 500;
/* 844 */     } else if (newLen > 65536) {
/* 845 */       newLen = 65536;
/*     */     } 
/* 847 */     this._currentSegment = carr(newLen);
/*     */   }
/*     */ 
/*     */   
/*     */   private char[] resultArray() {
/* 852 */     if (this._resultString != null) {
/* 853 */       return this._resultString.toCharArray();
/*     */     }
/*     */     
/* 856 */     if (this._inputStart >= 0) {
/* 857 */       int len = this._inputLen;
/* 858 */       if (len < 1) {
/* 859 */         return NO_CHARS;
/*     */       }
/* 861 */       int start = this._inputStart;
/* 862 */       if (start == 0) {
/* 863 */         return Arrays.copyOf(this._inputBuffer, len);
/*     */       }
/* 865 */       return Arrays.copyOfRange(this._inputBuffer, start, start + len);
/*     */     } 
/*     */     
/* 868 */     int size = size();
/* 869 */     if (size < 1) {
/* 870 */       return NO_CHARS;
/*     */     }
/* 872 */     int offset = 0;
/* 873 */     char[] result = carr(size);
/* 874 */     if (this._segments != null) {
/* 875 */       for (int i = 0, len = this._segments.size(); i < len; i++) {
/* 876 */         char[] curr = this._segments.get(i);
/* 877 */         int currLen = curr.length;
/* 878 */         System.arraycopy(curr, 0, result, offset, currLen);
/* 879 */         offset += currLen;
/*     */       } 
/*     */     }
/* 882 */     System.arraycopy(this._currentSegment, 0, result, offset, this._currentSize);
/* 883 */     return result;
/*     */   }
/*     */   private char[] carr(int len) {
/* 886 */     return new char[len];
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\cor\\util\TextBuffer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */