/*      */ package com.google.gson.stream;
/*      */ 
/*      */ import com.google.gson.internal.JsonReaderInternalAccess;
/*      */ import com.google.gson.internal.bind.JsonTreeReader;
/*      */ import java.io.Closeable;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.Reader;
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
/*      */ public class JsonReader
/*      */   implements Closeable
/*      */ {
/*  192 */   private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
/*      */   
/*      */   private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
/*      */   
/*      */   private static final int PEEKED_NONE = 0;
/*      */   
/*      */   private static final int PEEKED_BEGIN_OBJECT = 1;
/*      */   
/*      */   private static final int PEEKED_END_OBJECT = 2;
/*      */   
/*      */   private static final int PEEKED_BEGIN_ARRAY = 3;
/*      */   
/*      */   private static final int PEEKED_END_ARRAY = 4;
/*      */   
/*      */   private static final int PEEKED_TRUE = 5;
/*      */   
/*      */   private static final int PEEKED_FALSE = 6;
/*      */   
/*      */   private static final int PEEKED_NULL = 7;
/*      */   
/*      */   private static final int PEEKED_SINGLE_QUOTED = 8;
/*      */   
/*      */   private static final int PEEKED_DOUBLE_QUOTED = 9;
/*      */   
/*      */   private static final int PEEKED_UNQUOTED = 10;
/*      */   
/*      */   private static final int PEEKED_BUFFERED = 11;
/*      */   
/*      */   private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
/*      */   
/*      */   private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
/*      */   
/*      */   private static final int PEEKED_UNQUOTED_NAME = 14;
/*      */   private static final int PEEKED_LONG = 15;
/*      */   private static final int PEEKED_NUMBER = 16;
/*      */   private static final int PEEKED_EOF = 17;
/*      */   private static final int NUMBER_CHAR_NONE = 0;
/*      */   private static final int NUMBER_CHAR_SIGN = 1;
/*      */   private static final int NUMBER_CHAR_DIGIT = 2;
/*      */   private static final int NUMBER_CHAR_DECIMAL = 3;
/*      */   private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
/*      */   private static final int NUMBER_CHAR_EXP_E = 5;
/*      */   private static final int NUMBER_CHAR_EXP_SIGN = 6;
/*      */   private static final int NUMBER_CHAR_EXP_DIGIT = 7;
/*      */   private final Reader in;
/*      */   private boolean lenient = false;
/*  238 */   private final char[] buffer = new char[1024];
/*  239 */   private int pos = 0;
/*  240 */   private int limit = 0;
/*      */   
/*  242 */   private int lineNumber = 0;
/*  243 */   private int lineStart = 0;
/*      */   
/*  245 */   int peeked = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long peekedLong;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int peekedNumberLength;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String peekedString;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  269 */   private int[] stack = new int[32];
/*  270 */   private int stackSize = 0; private String[] pathNames; private int[] pathIndices;
/*      */   public JsonReader(Reader in) {
/*  272 */     this.stack[this.stackSize++] = 6;
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
/*  283 */     this.pathNames = new String[32];
/*  284 */     this.pathIndices = new int[32];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  290 */     if (in == null) {
/*  291 */       throw new NullPointerException("in == null");
/*      */     }
/*  293 */     this.in = in;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final void setLenient(boolean lenient) {
/*  326 */     this.lenient = lenient;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public final boolean isLenient() {
/*  333 */     return this.lenient;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void beginArray() throws IOException {
/*  341 */     int p = this.peeked;
/*  342 */     if (p == 0) {
/*  343 */       p = doPeek();
/*      */     }
/*  345 */     if (p == 3) {
/*  346 */       push(1);
/*  347 */       this.pathIndices[this.stackSize - 1] = 0;
/*  348 */       this.peeked = 0;
/*      */     } else {
/*  350 */       throw new IllegalStateException("Expected BEGIN_ARRAY but was " + peek() + locationString());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void endArray() throws IOException {
/*  359 */     int p = this.peeked;
/*  360 */     if (p == 0) {
/*  361 */       p = doPeek();
/*      */     }
/*  363 */     if (p == 4) {
/*  364 */       this.stackSize--;
/*  365 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*  366 */       this.peeked = 0;
/*      */     } else {
/*  368 */       throw new IllegalStateException("Expected END_ARRAY but was " + peek() + locationString());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void beginObject() throws IOException {
/*  377 */     int p = this.peeked;
/*  378 */     if (p == 0) {
/*  379 */       p = doPeek();
/*      */     }
/*  381 */     if (p == 1) {
/*  382 */       push(3);
/*  383 */       this.peeked = 0;
/*      */     } else {
/*  385 */       throw new IllegalStateException("Expected BEGIN_OBJECT but was " + peek() + locationString());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void endObject() throws IOException {
/*  394 */     int p = this.peeked;
/*  395 */     if (p == 0) {
/*  396 */       p = doPeek();
/*      */     }
/*  398 */     if (p == 2) {
/*  399 */       this.stackSize--;
/*  400 */       this.pathNames[this.stackSize] = null;
/*  401 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*  402 */       this.peeked = 0;
/*      */     } else {
/*  404 */       throw new IllegalStateException("Expected END_OBJECT but was " + peek() + locationString());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean hasNext() throws IOException {
/*  412 */     int p = this.peeked;
/*  413 */     if (p == 0) {
/*  414 */       p = doPeek();
/*      */     }
/*  416 */     return (p != 2 && p != 4);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public JsonToken peek() throws IOException {
/*  423 */     int p = this.peeked;
/*  424 */     if (p == 0) {
/*  425 */       p = doPeek();
/*      */     }
/*      */     
/*  428 */     switch (p) {
/*      */       case 1:
/*  430 */         return JsonToken.BEGIN_OBJECT;
/*      */       case 2:
/*  432 */         return JsonToken.END_OBJECT;
/*      */       case 3:
/*  434 */         return JsonToken.BEGIN_ARRAY;
/*      */       case 4:
/*  436 */         return JsonToken.END_ARRAY;
/*      */       case 12:
/*      */       case 13:
/*      */       case 14:
/*  440 */         return JsonToken.NAME;
/*      */       case 5:
/*      */       case 6:
/*  443 */         return JsonToken.BOOLEAN;
/*      */       case 7:
/*  445 */         return JsonToken.NULL;
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/*      */       case 11:
/*  450 */         return JsonToken.STRING;
/*      */       case 15:
/*      */       case 16:
/*  453 */         return JsonToken.NUMBER;
/*      */       case 17:
/*  455 */         return JsonToken.END_DOCUMENT;
/*      */     } 
/*  457 */     throw new AssertionError();
/*      */   }
/*      */ 
/*      */   
/*      */   int doPeek() throws IOException {
/*  462 */     int peekStack = this.stack[this.stackSize - 1];
/*  463 */     if (peekStack == 1)
/*  464 */     { this.stack[this.stackSize - 1] = 2; }
/*  465 */     else if (peekStack == 2)
/*      */     
/*  467 */     { int i = nextNonWhitespace(true);
/*  468 */       switch (i) {
/*      */         case 93:
/*  470 */           return this.peeked = 4;
/*      */         case 59:
/*  472 */           checkLenient(); break;
/*      */         case 44:
/*      */           break;
/*      */         default:
/*  476 */           throw syntaxError("Unterminated array");
/*      */       }  }
/*  478 */     else { if (peekStack == 3 || peekStack == 5) {
/*  479 */         this.stack[this.stackSize - 1] = 4;
/*      */         
/*  481 */         if (peekStack == 5) {
/*  482 */           int j = nextNonWhitespace(true);
/*  483 */           switch (j) {
/*      */             case 125:
/*  485 */               return this.peeked = 2;
/*      */             case 59:
/*  487 */               checkLenient(); break;
/*      */             case 44:
/*      */               break;
/*      */             default:
/*  491 */               throw syntaxError("Unterminated object");
/*      */           } 
/*      */         } 
/*  494 */         int i = nextNonWhitespace(true);
/*  495 */         switch (i) {
/*      */           case 34:
/*  497 */             return this.peeked = 13;
/*      */           case 39:
/*  499 */             checkLenient();
/*  500 */             return this.peeked = 12;
/*      */           case 125:
/*  502 */             if (peekStack != 5) {
/*  503 */               return this.peeked = 2;
/*      */             }
/*  505 */             throw syntaxError("Expected name");
/*      */         } 
/*      */         
/*  508 */         checkLenient();
/*  509 */         this.pos--;
/*  510 */         if (isLiteral((char)i)) {
/*  511 */           return this.peeked = 14;
/*      */         }
/*  513 */         throw syntaxError("Expected name");
/*      */       } 
/*      */       
/*  516 */       if (peekStack == 4) {
/*  517 */         this.stack[this.stackSize - 1] = 5;
/*      */         
/*  519 */         int i = nextNonWhitespace(true);
/*  520 */         switch (i) {
/*      */           case 58:
/*      */             break;
/*      */           case 61:
/*  524 */             checkLenient();
/*  525 */             if ((this.pos < this.limit || fillBuffer(1)) && this.buffer[this.pos] == '>') {
/*  526 */               this.pos++;
/*      */             }
/*      */             break;
/*      */           default:
/*  530 */             throw syntaxError("Expected ':'");
/*      */         } 
/*  532 */       } else if (peekStack == 6) {
/*  533 */         if (this.lenient) {
/*  534 */           consumeNonExecutePrefix();
/*      */         }
/*  536 */         this.stack[this.stackSize - 1] = 7;
/*  537 */       } else if (peekStack == 7) {
/*  538 */         int i = nextNonWhitespace(false);
/*  539 */         if (i == -1) {
/*  540 */           return this.peeked = 17;
/*      */         }
/*  542 */         checkLenient();
/*  543 */         this.pos--;
/*      */       }
/*  545 */       else if (peekStack == 8) {
/*  546 */         throw new IllegalStateException("JsonReader is closed");
/*      */       }  }
/*      */     
/*  549 */     int c = nextNonWhitespace(true);
/*  550 */     switch (c) {
/*      */       case 93:
/*  552 */         if (peekStack == 1) {
/*  553 */           return this.peeked = 4;
/*      */         }
/*      */ 
/*      */       
/*      */       case 44:
/*      */       case 59:
/*  559 */         if (peekStack == 1 || peekStack == 2) {
/*  560 */           checkLenient();
/*  561 */           this.pos--;
/*  562 */           return this.peeked = 7;
/*      */         } 
/*  564 */         throw syntaxError("Unexpected value");
/*      */       
/*      */       case 39:
/*  567 */         checkLenient();
/*  568 */         return this.peeked = 8;
/*      */       case 34:
/*  570 */         return this.peeked = 9;
/*      */       case 91:
/*  572 */         return this.peeked = 3;
/*      */       case 123:
/*  574 */         return this.peeked = 1;
/*      */     } 
/*  576 */     this.pos--;
/*      */ 
/*      */     
/*  579 */     int result = peekKeyword();
/*  580 */     if (result != 0) {
/*  581 */       return result;
/*      */     }
/*      */     
/*  584 */     result = peekNumber();
/*  585 */     if (result != 0) {
/*  586 */       return result;
/*      */     }
/*      */     
/*  589 */     if (!isLiteral(this.buffer[this.pos])) {
/*  590 */       throw syntaxError("Expected value");
/*      */     }
/*      */     
/*  593 */     checkLenient();
/*  594 */     return this.peeked = 10;
/*      */   }
/*      */   private int peekKeyword() throws IOException {
/*      */     String keyword, keywordUpper;
/*      */     int peeking;
/*  599 */     char c = this.buffer[this.pos];
/*      */ 
/*      */ 
/*      */     
/*  603 */     if (c == 't' || c == 'T') {
/*  604 */       keyword = "true";
/*  605 */       keywordUpper = "TRUE";
/*  606 */       peeking = 5;
/*  607 */     } else if (c == 'f' || c == 'F') {
/*  608 */       keyword = "false";
/*  609 */       keywordUpper = "FALSE";
/*  610 */       peeking = 6;
/*  611 */     } else if (c == 'n' || c == 'N') {
/*  612 */       keyword = "null";
/*  613 */       keywordUpper = "NULL";
/*  614 */       peeking = 7;
/*      */     } else {
/*  616 */       return 0;
/*      */     } 
/*      */ 
/*      */     
/*  620 */     int length = keyword.length();
/*  621 */     for (int i = 1; i < length; i++) {
/*  622 */       if (this.pos + i >= this.limit && !fillBuffer(i + 1)) {
/*  623 */         return 0;
/*      */       }
/*  625 */       c = this.buffer[this.pos + i];
/*  626 */       if (c != keyword.charAt(i) && c != keywordUpper.charAt(i)) {
/*  627 */         return 0;
/*      */       }
/*      */     } 
/*      */     
/*  631 */     if ((this.pos + length < this.limit || fillBuffer(length + 1)) && 
/*  632 */       isLiteral(this.buffer[this.pos + length])) {
/*  633 */       return 0;
/*      */     }
/*      */ 
/*      */     
/*  637 */     this.pos += length;
/*  638 */     return this.peeked = peeking;
/*      */   }
/*      */   
/*      */   private int peekNumber() throws IOException {
/*      */     int j;
/*  643 */     char[] buffer = this.buffer;
/*  644 */     int p = this.pos;
/*  645 */     int l = this.limit;
/*      */     
/*  647 */     long value = 0L;
/*  648 */     boolean negative = false;
/*  649 */     boolean fitsInLong = true;
/*  650 */     int last = 0;
/*      */     
/*  652 */     int i = 0;
/*      */ 
/*      */     
/*  655 */     for (;; i++) {
/*  656 */       if (p + i == l) {
/*  657 */         if (i == buffer.length)
/*      */         {
/*      */           
/*  660 */           return 0;
/*      */         }
/*  662 */         if (!fillBuffer(i + 1)) {
/*      */           break;
/*      */         }
/*  665 */         p = this.pos;
/*  666 */         l = this.limit;
/*      */       } 
/*      */       
/*  669 */       char c = buffer[p + i];
/*  670 */       switch (c) {
/*      */         case '-':
/*  672 */           if (last == 0) {
/*  673 */             negative = true;
/*  674 */             last = 1; break;
/*      */           } 
/*  676 */           if (last == 5) {
/*  677 */             last = 6;
/*      */             break;
/*      */           } 
/*  680 */           return 0;
/*      */         
/*      */         case '+':
/*  683 */           if (last == 5) {
/*  684 */             last = 6;
/*      */             break;
/*      */           } 
/*  687 */           return 0;
/*      */         
/*      */         case 'E':
/*      */         case 'e':
/*  691 */           if (last == 2 || last == 4) {
/*  692 */             last = 5;
/*      */             break;
/*      */           } 
/*  695 */           return 0;
/*      */         
/*      */         case '.':
/*  698 */           if (last == 2) {
/*  699 */             last = 3;
/*      */             break;
/*      */           } 
/*  702 */           return 0;
/*      */         
/*      */         default:
/*  705 */           if (c < '0' || c > '9') {
/*  706 */             if (!isLiteral(c)) {
/*      */               break;
/*      */             }
/*  709 */             return 0;
/*      */           } 
/*  711 */           if (last == 1 || last == 0) {
/*  712 */             value = -(c - 48);
/*  713 */             last = 2; break;
/*  714 */           }  if (last == 2) {
/*  715 */             if (value == 0L) {
/*  716 */               return 0;
/*      */             }
/*  718 */             long newValue = value * 10L - (c - 48);
/*  719 */             j = fitsInLong & ((value > -922337203685477580L || (value == -922337203685477580L && newValue < value)) ? 1 : 0);
/*      */             
/*  721 */             value = newValue; break;
/*  722 */           }  if (last == 3) {
/*  723 */             last = 4; break;
/*  724 */           }  if (last == 5 || last == 6) {
/*  725 */             last = 7;
/*      */           }
/*      */           break;
/*      */       } 
/*      */     
/*      */     } 
/*  731 */     if (last == 2 && j != 0 && (value != Long.MIN_VALUE || negative) && (value != 0L || false == negative)) {
/*  732 */       this.peekedLong = negative ? value : -value;
/*  733 */       this.pos += i;
/*  734 */       return this.peeked = 15;
/*  735 */     }  if (last == 2 || last == 4 || last == 7) {
/*      */       
/*  737 */       this.peekedNumberLength = i;
/*  738 */       return this.peeked = 16;
/*      */     } 
/*  740 */     return 0;
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isLiteral(char c) throws IOException {
/*  745 */     switch (c) {
/*      */       case '#':
/*      */       case '/':
/*      */       case ';':
/*      */       case '=':
/*      */       case '\\':
/*  751 */         checkLenient();
/*      */       case '\t':
/*      */       case '\n':
/*      */       case '\f':
/*      */       case '\r':
/*      */       case ' ':
/*      */       case ',':
/*      */       case ':':
/*      */       case '[':
/*      */       case ']':
/*      */       case '{':
/*      */       case '}':
/*  763 */         return false;
/*      */     } 
/*  765 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nextName() throws IOException {
/*      */     String result;
/*  777 */     int p = this.peeked;
/*  778 */     if (p == 0) {
/*  779 */       p = doPeek();
/*      */     }
/*      */     
/*  782 */     if (p == 14) {
/*  783 */       result = nextUnquotedValue();
/*  784 */     } else if (p == 12) {
/*  785 */       result = nextQuotedValue('\'');
/*  786 */     } else if (p == 13) {
/*  787 */       result = nextQuotedValue('"');
/*      */     } else {
/*  789 */       throw new IllegalStateException("Expected a name but was " + peek() + locationString());
/*      */     } 
/*  791 */     this.peeked = 0;
/*  792 */     this.pathNames[this.stackSize - 1] = result;
/*  793 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String nextString() throws IOException {
/*      */     String result;
/*  805 */     int p = this.peeked;
/*  806 */     if (p == 0) {
/*  807 */       p = doPeek();
/*      */     }
/*      */     
/*  810 */     if (p == 10) {
/*  811 */       result = nextUnquotedValue();
/*  812 */     } else if (p == 8) {
/*  813 */       result = nextQuotedValue('\'');
/*  814 */     } else if (p == 9) {
/*  815 */       result = nextQuotedValue('"');
/*  816 */     } else if (p == 11) {
/*  817 */       result = this.peekedString;
/*  818 */       this.peekedString = null;
/*  819 */     } else if (p == 15) {
/*  820 */       result = Long.toString(this.peekedLong);
/*  821 */     } else if (p == 16) {
/*  822 */       result = new String(this.buffer, this.pos, this.peekedNumberLength);
/*  823 */       this.pos += this.peekedNumberLength;
/*      */     } else {
/*  825 */       throw new IllegalStateException("Expected a string but was " + peek() + locationString());
/*      */     } 
/*  827 */     this.peeked = 0;
/*  828 */     this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*  829 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean nextBoolean() throws IOException {
/*  840 */     int p = this.peeked;
/*  841 */     if (p == 0) {
/*  842 */       p = doPeek();
/*      */     }
/*  844 */     if (p == 5) {
/*  845 */       this.peeked = 0;
/*  846 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*  847 */       return true;
/*  848 */     }  if (p == 6) {
/*  849 */       this.peeked = 0;
/*  850 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*  851 */       return false;
/*      */     } 
/*  853 */     throw new IllegalStateException("Expected a boolean but was " + peek() + locationString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void nextNull() throws IOException {
/*  864 */     int p = this.peeked;
/*  865 */     if (p == 0) {
/*  866 */       p = doPeek();
/*      */     }
/*  868 */     if (p == 7) {
/*  869 */       this.peeked = 0;
/*  870 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*      */     } else {
/*  872 */       throw new IllegalStateException("Expected null but was " + peek() + locationString());
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
/*      */   public double nextDouble() throws IOException {
/*  886 */     int p = this.peeked;
/*  887 */     if (p == 0) {
/*  888 */       p = doPeek();
/*      */     }
/*      */     
/*  891 */     if (p == 15) {
/*  892 */       this.peeked = 0;
/*  893 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*  894 */       return this.peekedLong;
/*      */     } 
/*      */     
/*  897 */     if (p == 16) {
/*  898 */       this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
/*  899 */       this.pos += this.peekedNumberLength;
/*  900 */     } else if (p == 8 || p == 9) {
/*  901 */       this.peekedString = nextQuotedValue((p == 8) ? 39 : 34);
/*  902 */     } else if (p == 10) {
/*  903 */       this.peekedString = nextUnquotedValue();
/*  904 */     } else if (p != 11) {
/*  905 */       throw new IllegalStateException("Expected a double but was " + peek() + locationString());
/*      */     } 
/*      */     
/*  908 */     this.peeked = 11;
/*  909 */     double result = Double.parseDouble(this.peekedString);
/*  910 */     if (!this.lenient && (Double.isNaN(result) || Double.isInfinite(result))) {
/*  911 */       throw new MalformedJsonException("JSON forbids NaN and infinities: " + result + 
/*  912 */           locationString());
/*      */     }
/*  914 */     this.peekedString = null;
/*  915 */     this.peeked = 0;
/*  916 */     this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*  917 */     return result;
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
/*      */   public long nextLong() throws IOException {
/*  931 */     int p = this.peeked;
/*  932 */     if (p == 0) {
/*  933 */       p = doPeek();
/*      */     }
/*      */     
/*  936 */     if (p == 15) {
/*  937 */       this.peeked = 0;
/*  938 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*  939 */       return this.peekedLong;
/*      */     } 
/*      */     
/*  942 */     if (p == 16) {
/*  943 */       this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
/*  944 */       this.pos += this.peekedNumberLength;
/*  945 */     } else if (p == 8 || p == 9 || p == 10) {
/*  946 */       if (p == 10) {
/*  947 */         this.peekedString = nextUnquotedValue();
/*      */       } else {
/*  949 */         this.peekedString = nextQuotedValue((p == 8) ? 39 : 34);
/*      */       } 
/*      */       try {
/*  952 */         long l = Long.parseLong(this.peekedString);
/*  953 */         this.peeked = 0;
/*  954 */         this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*  955 */         return l;
/*  956 */       } catch (NumberFormatException numberFormatException) {}
/*      */     }
/*      */     else {
/*      */       
/*  960 */       throw new IllegalStateException("Expected a long but was " + peek() + locationString());
/*      */     } 
/*      */     
/*  963 */     this.peeked = 11;
/*  964 */     double asDouble = Double.parseDouble(this.peekedString);
/*  965 */     long result = (long)asDouble;
/*  966 */     if (result != asDouble) {
/*  967 */       throw new NumberFormatException("Expected a long but was " + this.peekedString + locationString());
/*      */     }
/*  969 */     this.peekedString = null;
/*  970 */     this.peeked = 0;
/*  971 */     this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/*  972 */     return result;
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
/*      */   private String nextQuotedValue(char quote) throws IOException {
/*  987 */     char[] buffer = this.buffer;
/*  988 */     StringBuilder builder = null;
/*      */     while (true) {
/*  990 */       int p = this.pos;
/*  991 */       int l = this.limit;
/*      */       
/*  993 */       int start = p;
/*  994 */       while (p < l) {
/*  995 */         int c = buffer[p++];
/*      */         
/*  997 */         if (c == quote) {
/*  998 */           this.pos = p;
/*  999 */           int len = p - start - 1;
/* 1000 */           if (builder == null) {
/* 1001 */             return new String(buffer, start, len);
/*      */           }
/* 1003 */           builder.append(buffer, start, len);
/* 1004 */           return builder.toString();
/*      */         } 
/* 1006 */         if (c == 92) {
/* 1007 */           this.pos = p;
/* 1008 */           int len = p - start - 1;
/* 1009 */           if (builder == null) {
/* 1010 */             int estimatedLength = (len + 1) * 2;
/* 1011 */             builder = new StringBuilder(Math.max(estimatedLength, 16));
/*      */           } 
/* 1013 */           builder.append(buffer, start, len);
/* 1014 */           builder.append(readEscapeCharacter());
/* 1015 */           p = this.pos;
/* 1016 */           l = this.limit;
/* 1017 */           start = p; continue;
/* 1018 */         }  if (c == 10) {
/* 1019 */           this.lineNumber++;
/* 1020 */           this.lineStart = p;
/*      */         } 
/*      */       } 
/*      */       
/* 1024 */       if (builder == null) {
/* 1025 */         int estimatedLength = (p - start) * 2;
/* 1026 */         builder = new StringBuilder(Math.max(estimatedLength, 16));
/*      */       } 
/* 1028 */       builder.append(buffer, start, p - start);
/* 1029 */       this.pos = p;
/* 1030 */       if (!fillBuffer(1)) {
/* 1031 */         throw syntaxError("Unterminated string");
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String nextUnquotedValue() throws IOException {
/* 1041 */     StringBuilder builder = null;
/* 1042 */     int i = 0;
/*      */ 
/*      */     
/*      */     label34: while (true) {
/* 1046 */       for (; this.pos + i < this.limit; i++)
/* 1047 */       { switch (this.buffer[this.pos + i])
/*      */         { case '#':
/*      */           case '/':
/*      */           case ';':
/*      */           case '=':
/*      */           case '\\':
/* 1053 */             checkLenient(); break label34;
/*      */           case '\t': break label34;
/*      */           case '\n': break label34;
/*      */           case '\f': break label34;
/*      */           case '\r': break label34;
/*      */           case ' ': break label34;
/*      */           case ',':
/*      */             break label34;
/*      */           case ':':
/*      */             break label34;
/*      */           case '[':
/*      */             break label34;
/*      */           case ']':
/*      */             break label34;
/*      */           case '{':
/*      */             break label34;
/*      */           case '}':
/* 1070 */             break label34; }  }  if (i < this.buffer.length) {
/* 1071 */         if (fillBuffer(i + 1)) {
/*      */           continue;
/*      */         }
/*      */ 
/*      */         
/*      */         break;
/*      */       } 
/*      */       
/* 1079 */       if (builder == null) {
/* 1080 */         builder = new StringBuilder(Math.max(i, 16));
/*      */       }
/* 1082 */       builder.append(this.buffer, this.pos, i);
/* 1083 */       this.pos += i;
/* 1084 */       i = 0;
/* 1085 */       if (!fillBuffer(1)) {
/*      */         break;
/*      */       }
/*      */     } 
/*      */     
/* 1090 */     String result = (null == builder) ? new String(this.buffer, this.pos, i) : builder.append(this.buffer, this.pos, i).toString();
/* 1091 */     this.pos += i;
/* 1092 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   private void skipQuotedValue(char quote) throws IOException {
/* 1097 */     char[] buffer = this.buffer;
/*      */     while (true) {
/* 1099 */       int p = this.pos;
/* 1100 */       int l = this.limit;
/*      */       
/* 1102 */       while (p < l) {
/* 1103 */         int c = buffer[p++];
/* 1104 */         if (c == quote) {
/* 1105 */           this.pos = p; return;
/*      */         } 
/* 1107 */         if (c == 92) {
/* 1108 */           this.pos = p;
/* 1109 */           readEscapeCharacter();
/* 1110 */           p = this.pos;
/* 1111 */           l = this.limit; continue;
/* 1112 */         }  if (c == 10) {
/* 1113 */           this.lineNumber++;
/* 1114 */           this.lineStart = p;
/*      */         } 
/*      */       } 
/* 1117 */       this.pos = p;
/* 1118 */       if (!fillBuffer(1))
/* 1119 */         throw syntaxError("Unterminated string"); 
/*      */     } 
/*      */   }
/*      */   private void skipUnquotedValue() throws IOException {
/*      */     do {
/* 1124 */       int i = 0;
/* 1125 */       for (; this.pos + i < this.limit; i++) {
/* 1126 */         switch (this.buffer[this.pos + i]) {
/*      */           case '#':
/*      */           case '/':
/*      */           case ';':
/*      */           case '=':
/*      */           case '\\':
/* 1132 */             checkLenient();
/*      */           case '\t':
/*      */           case '\n':
/*      */           case '\f':
/*      */           case '\r':
/*      */           case ' ':
/*      */           case ',':
/*      */           case ':':
/*      */           case '[':
/*      */           case ']':
/*      */           case '{':
/*      */           case '}':
/* 1144 */             this.pos += i;
/*      */             return;
/*      */         } 
/*      */       } 
/* 1148 */       this.pos += i;
/* 1149 */     } while (fillBuffer(1));
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
/*      */   public int nextInt() throws IOException {
/* 1163 */     int p = this.peeked;
/* 1164 */     if (p == 0) {
/* 1165 */       p = doPeek();
/*      */     }
/*      */ 
/*      */     
/* 1169 */     if (p == 15) {
/* 1170 */       int i = (int)this.peekedLong;
/* 1171 */       if (this.peekedLong != i) {
/* 1172 */         throw new NumberFormatException("Expected an int but was " + this.peekedLong + locationString());
/*      */       }
/* 1174 */       this.peeked = 0;
/* 1175 */       this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/* 1176 */       return i;
/*      */     } 
/*      */     
/* 1179 */     if (p == 16) {
/* 1180 */       this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
/* 1181 */       this.pos += this.peekedNumberLength;
/* 1182 */     } else if (p == 8 || p == 9 || p == 10) {
/* 1183 */       if (p == 10) {
/* 1184 */         this.peekedString = nextUnquotedValue();
/*      */       } else {
/* 1186 */         this.peekedString = nextQuotedValue((p == 8) ? 39 : 34);
/*      */       } 
/*      */       try {
/* 1189 */         int i = Integer.parseInt(this.peekedString);
/* 1190 */         this.peeked = 0;
/* 1191 */         this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/* 1192 */         return i;
/* 1193 */       } catch (NumberFormatException numberFormatException) {}
/*      */     }
/*      */     else {
/*      */       
/* 1197 */       throw new IllegalStateException("Expected an int but was " + peek() + locationString());
/*      */     } 
/*      */     
/* 1200 */     this.peeked = 11;
/* 1201 */     double asDouble = Double.parseDouble(this.peekedString);
/* 1202 */     int result = (int)asDouble;
/* 1203 */     if (result != asDouble) {
/* 1204 */       throw new NumberFormatException("Expected an int but was " + this.peekedString + locationString());
/*      */     }
/* 1206 */     this.peekedString = null;
/* 1207 */     this.peeked = 0;
/* 1208 */     this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/* 1209 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() throws IOException {
/* 1216 */     this.peeked = 0;
/* 1217 */     this.stack[0] = 8;
/* 1218 */     this.stackSize = 1;
/* 1219 */     this.in.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void skipValue() throws IOException {
/* 1228 */     int count = 0;
/*      */     do {
/* 1230 */       int p = this.peeked;
/* 1231 */       if (p == 0) {
/* 1232 */         p = doPeek();
/*      */       }
/*      */       
/* 1235 */       if (p == 3) {
/* 1236 */         push(1);
/* 1237 */         count++;
/* 1238 */       } else if (p == 1) {
/* 1239 */         push(3);
/* 1240 */         count++;
/* 1241 */       } else if (p == 4) {
/* 1242 */         this.stackSize--;
/* 1243 */         count--;
/* 1244 */       } else if (p == 2) {
/* 1245 */         this.stackSize--;
/* 1246 */         count--;
/* 1247 */       } else if (p == 14 || p == 10) {
/* 1248 */         skipUnquotedValue();
/* 1249 */       } else if (p == 8 || p == 12) {
/* 1250 */         skipQuotedValue('\'');
/* 1251 */       } else if (p == 9 || p == 13) {
/* 1252 */         skipQuotedValue('"');
/* 1253 */       } else if (p == 16) {
/* 1254 */         this.pos += this.peekedNumberLength;
/*      */       } 
/* 1256 */       this.peeked = 0;
/* 1257 */     } while (count != 0);
/*      */     
/* 1259 */     this.pathIndices[this.stackSize - 1] = this.pathIndices[this.stackSize - 1] + 1;
/* 1260 */     this.pathNames[this.stackSize - 1] = "null";
/*      */   }
/*      */   
/*      */   private void push(int newTop) {
/* 1264 */     if (this.stackSize == this.stack.length) {
/* 1265 */       int[] newStack = new int[this.stackSize * 2];
/* 1266 */       int[] newPathIndices = new int[this.stackSize * 2];
/* 1267 */       String[] newPathNames = new String[this.stackSize * 2];
/* 1268 */       System.arraycopy(this.stack, 0, newStack, 0, this.stackSize);
/* 1269 */       System.arraycopy(this.pathIndices, 0, newPathIndices, 0, this.stackSize);
/* 1270 */       System.arraycopy(this.pathNames, 0, newPathNames, 0, this.stackSize);
/* 1271 */       this.stack = newStack;
/* 1272 */       this.pathIndices = newPathIndices;
/* 1273 */       this.pathNames = newPathNames;
/*      */     } 
/* 1275 */     this.stack[this.stackSize++] = newTop;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean fillBuffer(int minimum) throws IOException {
/* 1284 */     char[] buffer = this.buffer;
/* 1285 */     this.lineStart -= this.pos;
/* 1286 */     if (this.limit != this.pos) {
/* 1287 */       this.limit -= this.pos;
/* 1288 */       System.arraycopy(buffer, this.pos, buffer, 0, this.limit);
/*      */     } else {
/* 1290 */       this.limit = 0;
/*      */     } 
/*      */     
/* 1293 */     this.pos = 0;
/*      */     int total;
/* 1295 */     while ((total = this.in.read(buffer, this.limit, buffer.length - this.limit)) != -1) {
/* 1296 */       this.limit += total;
/*      */ 
/*      */       
/* 1299 */       if (this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && buffer[0] == '﻿') {
/* 1300 */         this.pos++;
/* 1301 */         this.lineStart++;
/* 1302 */         minimum++;
/*      */       } 
/*      */       
/* 1305 */       if (this.limit >= minimum) {
/* 1306 */         return true;
/*      */       }
/*      */     } 
/* 1309 */     return false;
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
/*      */   private int nextNonWhitespace(boolean throwOnEof) throws IOException {
/* 1327 */     char[] buffer = this.buffer;
/* 1328 */     int p = this.pos;
/* 1329 */     int l = this.limit;
/*      */     while (true) {
/* 1331 */       if (p == l) {
/* 1332 */         this.pos = p;
/* 1333 */         if (!fillBuffer(1)) {
/*      */           break;
/*      */         }
/* 1336 */         p = this.pos;
/* 1337 */         l = this.limit;
/*      */       } 
/*      */       
/* 1340 */       int c = buffer[p++];
/* 1341 */       if (c == 10) {
/* 1342 */         this.lineNumber++;
/* 1343 */         this.lineStart = p; continue;
/*      */       } 
/* 1345 */       if (c == 32 || c == 13 || c == 9) {
/*      */         continue;
/*      */       }
/*      */       
/* 1349 */       if (c == 47) {
/* 1350 */         this.pos = p;
/* 1351 */         if (p == l) {
/* 1352 */           this.pos--;
/* 1353 */           boolean charsLoaded = fillBuffer(2);
/* 1354 */           this.pos++;
/* 1355 */           if (!charsLoaded) {
/* 1356 */             return c;
/*      */           }
/*      */         } 
/*      */         
/* 1360 */         checkLenient();
/* 1361 */         char peek = buffer[this.pos];
/* 1362 */         switch (peek) {
/*      */           
/*      */           case '*':
/* 1365 */             this.pos++;
/* 1366 */             if (!skipTo("*/")) {
/* 1367 */               throw syntaxError("Unterminated comment");
/*      */             }
/* 1369 */             p = this.pos + 2;
/* 1370 */             l = this.limit;
/*      */             continue;
/*      */ 
/*      */           
/*      */           case '/':
/* 1375 */             this.pos++;
/* 1376 */             skipToEndOfLine();
/* 1377 */             p = this.pos;
/* 1378 */             l = this.limit;
/*      */             continue;
/*      */         } 
/*      */         
/* 1382 */         return c;
/*      */       } 
/* 1384 */       if (c == 35) {
/* 1385 */         this.pos = p;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1391 */         checkLenient();
/* 1392 */         skipToEndOfLine();
/* 1393 */         p = this.pos;
/* 1394 */         l = this.limit; continue;
/*      */       } 
/* 1396 */       this.pos = p;
/* 1397 */       return c;
/*      */     } 
/*      */     
/* 1400 */     if (throwOnEof) {
/* 1401 */       throw new EOFException("End of input" + locationString());
/*      */     }
/* 1403 */     return -1;
/*      */   }
/*      */ 
/*      */   
/*      */   private void checkLenient() throws IOException {
/* 1408 */     if (!this.lenient) {
/* 1409 */       throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void skipToEndOfLine() throws IOException {
/* 1419 */     while (this.pos < this.limit || fillBuffer(1)) {
/* 1420 */       char c = this.buffer[this.pos++];
/* 1421 */       if (c == '\n') {
/* 1422 */         this.lineNumber++;
/* 1423 */         this.lineStart = this.pos; break;
/*      */       } 
/* 1425 */       if (c == '\r') {
/*      */         break;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean skipTo(String toFind) throws IOException {
/* 1435 */     int length = toFind.length();
/*      */     
/* 1437 */     for (; this.pos + length <= this.limit || fillBuffer(length); this.pos++) {
/* 1438 */       if (this.buffer[this.pos] == '\n') {
/* 1439 */         this.lineNumber++;
/* 1440 */         this.lineStart = this.pos + 1;
/*      */       } else {
/*      */         
/* 1443 */         int c = 0; while (true) { if (c < length) {
/* 1444 */             if (this.buffer[this.pos + c] != toFind.charAt(c))
/*      */               break;  c++;
/*      */             continue;
/*      */           } 
/* 1448 */           return true; } 
/*      */       } 
/* 1450 */     }  return false;
/*      */   }
/*      */   
/*      */   public String toString() {
/* 1454 */     return getClass().getSimpleName() + locationString();
/*      */   }
/*      */   
/*      */   String locationString() {
/* 1458 */     int line = this.lineNumber + 1;
/* 1459 */     int column = this.pos - this.lineStart + 1;
/* 1460 */     return " at line " + line + " column " + column + " path " + getPath();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPath() {
/* 1468 */     StringBuilder result = (new StringBuilder()).append('$');
/* 1469 */     for (int i = 0, size = this.stackSize; i < size; i++) {
/* 1470 */       switch (this.stack[i]) {
/*      */         case 1:
/*      */         case 2:
/* 1473 */           result.append('[').append(this.pathIndices[i]).append(']');
/*      */           break;
/*      */         
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/* 1479 */           result.append('.');
/* 1480 */           if (this.pathNames[i] != null) {
/* 1481 */             result.append(this.pathNames[i]);
/*      */           }
/*      */           break;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     } 
/* 1491 */     return result.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private char readEscapeCharacter() throws IOException {
/*      */     char result;
/*      */     int i, end;
/* 1504 */     if (this.pos == this.limit && !fillBuffer(1)) {
/* 1505 */       throw syntaxError("Unterminated escape sequence");
/*      */     }
/*      */     
/* 1508 */     char escaped = this.buffer[this.pos++];
/* 1509 */     switch (escaped) {
/*      */       case 'u':
/* 1511 */         if (this.pos + 4 > this.limit && !fillBuffer(4)) {
/* 1512 */           throw syntaxError("Unterminated escape sequence");
/*      */         }
/*      */         
/* 1515 */         result = Character.MIN_VALUE;
/* 1516 */         for (i = this.pos, end = i + 4; i < end; i++) {
/* 1517 */           char c = this.buffer[i];
/* 1518 */           result = (char)(result << 4);
/* 1519 */           if (c >= '0' && c <= '9') {
/* 1520 */             result = (char)(result + c - 48);
/* 1521 */           } else if (c >= 'a' && c <= 'f') {
/* 1522 */             result = (char)(result + c - 97 + 10);
/* 1523 */           } else if (c >= 'A' && c <= 'F') {
/* 1524 */             result = (char)(result + c - 65 + 10);
/*      */           } else {
/* 1526 */             throw new NumberFormatException("\\u" + new String(this.buffer, this.pos, 4));
/*      */           } 
/*      */         } 
/* 1529 */         this.pos += 4;
/* 1530 */         return result;
/*      */       
/*      */       case 't':
/* 1533 */         return '\t';
/*      */       
/*      */       case 'b':
/* 1536 */         return '\b';
/*      */       
/*      */       case 'n':
/* 1539 */         return '\n';
/*      */       
/*      */       case 'r':
/* 1542 */         return '\r';
/*      */       
/*      */       case 'f':
/* 1545 */         return '\f';
/*      */       
/*      */       case '\n':
/* 1548 */         this.lineNumber++;
/* 1549 */         this.lineStart = this.pos;
/*      */ 
/*      */       
/*      */       case '"':
/*      */       case '\'':
/*      */       case '/':
/*      */       case '\\':
/* 1556 */         return escaped;
/*      */     } 
/*      */     
/* 1559 */     throw syntaxError("Invalid escape sequence");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private IOException syntaxError(String message) throws IOException {
/* 1568 */     throw new MalformedJsonException(message + locationString());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void consumeNonExecutePrefix() throws IOException {
/* 1576 */     nextNonWhitespace(true);
/* 1577 */     this.pos--;
/*      */     
/* 1579 */     if (this.pos + NON_EXECUTE_PREFIX.length > this.limit && !fillBuffer(NON_EXECUTE_PREFIX.length)) {
/*      */       return;
/*      */     }
/*      */     
/* 1583 */     for (int i = 0; i < NON_EXECUTE_PREFIX.length; i++) {
/* 1584 */       if (this.buffer[this.pos + i] != NON_EXECUTE_PREFIX[i]) {
/*      */         return;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1590 */     this.pos += NON_EXECUTE_PREFIX.length;
/*      */   }
/*      */   
/*      */   static {
/* 1594 */     JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
/*      */         public void promoteNameToValue(JsonReader reader) throws IOException {
/* 1596 */           if (reader instanceof JsonTreeReader) {
/* 1597 */             ((JsonTreeReader)reader).promoteNameToValue();
/*      */             return;
/*      */           } 
/* 1600 */           int p = reader.peeked;
/* 1601 */           if (p == 0) {
/* 1602 */             p = reader.doPeek();
/*      */           }
/* 1604 */           if (p == 13) {
/* 1605 */             reader.peeked = 9;
/* 1606 */           } else if (p == 12) {
/* 1607 */             reader.peeked = 8;
/* 1608 */           } else if (p == 14) {
/* 1609 */             reader.peeked = 10;
/*      */           } else {
/* 1611 */             throw new IllegalStateException("Expected a name but was " + reader
/* 1612 */                 .peek() + reader.locationString());
/*      */           } 
/*      */         }
/*      */       };
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\stream\JsonReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */