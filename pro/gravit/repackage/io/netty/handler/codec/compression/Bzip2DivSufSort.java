/*      */ package pro.gravit.repackage.io.netty.handler.codec.compression;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ final class Bzip2DivSufSort
/*      */ {
/*      */   private static final int STACK_SIZE = 64;
/*      */   private static final int BUCKET_A_SIZE = 256;
/*      */   private static final int BUCKET_B_SIZE = 65536;
/*      */   private static final int SS_BLOCKSIZE = 1024;
/*      */   private static final int INSERTIONSORT_THRESHOLD = 8;
/*   33 */   private static final int[] LOG_2_TABLE = new int[] { -1, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int[] SA;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final byte[] T;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private final int n;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Bzip2DivSufSort(byte[] block, int[] bwtBlock, int blockLength) {
/*   54 */     this.T = block;
/*   55 */     this.SA = bwtBlock;
/*   56 */     this.n = blockLength;
/*      */   }
/*      */   
/*      */   private static void swapElements(int[] array1, int idx1, int[] array2, int idx2) {
/*   60 */     int temp = array1[idx1];
/*   61 */     array1[idx1] = array2[idx2];
/*   62 */     array2[idx2] = temp;
/*      */   }
/*      */   
/*      */   private int ssCompare(int p1, int p2, int depth) {
/*   66 */     int[] SA = this.SA;
/*   67 */     byte[] T = this.T;
/*      */ 
/*      */     
/*   70 */     int U1n = SA[p1 + 1] + 2;
/*   71 */     int U2n = SA[p2 + 1] + 2;
/*      */     
/*   73 */     int U1 = depth + SA[p1];
/*   74 */     int U2 = depth + SA[p2];
/*      */     
/*   76 */     while (U1 < U1n && U2 < U2n && T[U1] == T[U2]) {
/*   77 */       U1++;
/*   78 */       U2++;
/*      */     } 
/*      */     
/*   81 */     return (U1 < U1n) ? ((U2 < U2n) ? ((T[U1] & 0xFF) - (T[U2] & 0xFF)) : 1) : ((U2 < U2n) ? -1 : 0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int ssCompareLast(int pa, int p1, int p2, int depth, int size) {
/*   87 */     int[] SA = this.SA;
/*   88 */     byte[] T = this.T;
/*      */     
/*   90 */     int U1 = depth + SA[p1];
/*   91 */     int U2 = depth + SA[p2];
/*   92 */     int U1n = size;
/*   93 */     int U2n = SA[p2 + 1] + 2;
/*      */     
/*   95 */     while (U1 < U1n && U2 < U2n && T[U1] == T[U2]) {
/*   96 */       U1++;
/*   97 */       U2++;
/*      */     } 
/*      */     
/*  100 */     if (U1 < U1n) {
/*  101 */       return (U2 < U2n) ? ((T[U1] & 0xFF) - (T[U2] & 0xFF)) : 1;
/*      */     }
/*  103 */     if (U2 == U2n) {
/*  104 */       return 1;
/*      */     }
/*      */     
/*  107 */     U1 %= size;
/*  108 */     U1n = SA[pa] + 2;
/*  109 */     while (U1 < U1n && U2 < U2n && T[U1] == T[U2]) {
/*  110 */       U1++;
/*  111 */       U2++;
/*      */     } 
/*      */     
/*  114 */     return (U1 < U1n) ? ((U2 < U2n) ? ((T[U1] & 0xFF) - (T[U2] & 0xFF)) : 1) : ((U2 < U2n) ? -1 : 0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void ssInsertionSort(int pa, int first, int last, int depth) {
/*  120 */     int[] SA = this.SA;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  126 */     for (int i = last - 2; first <= i; i--) {
/*  127 */       int j; int t; int r; for (t = SA[i], j = i + 1; 0 < (r = ssCompare(pa + t, pa + SA[j], depth)); ) {
/*      */         do {
/*  129 */           SA[j - 1] = SA[j];
/*  130 */         } while (++j < last && SA[j] < 0);
/*  131 */         if (last <= j) {
/*      */           break;
/*      */         }
/*      */       } 
/*  135 */       if (r == 0) {
/*  136 */         SA[j] = SA[j] ^ 0xFFFFFFFF;
/*      */       }
/*  138 */       SA[j - 1] = t;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void ssFixdown(int td, int pa, int sa, int i, int size) {
/*  143 */     int[] SA = this.SA;
/*  144 */     byte[] T = this.T;
/*      */ 
/*      */     
/*      */     int j, v, c;
/*      */ 
/*      */     
/*  150 */     for (v = SA[sa + i], c = T[td + SA[pa + v]] & 0xFF; (j = 2 * i + 1) < size; SA[sa + i] = SA[sa + k], i = k) {
/*  151 */       int k, d = T[td + SA[pa + SA[sa + (k = j++)]]] & 0xFF; int e;
/*  152 */       if (d < (e = T[td + SA[pa + SA[sa + j]]] & 0xFF)) {
/*  153 */         k = j;
/*  154 */         d = e;
/*      */       } 
/*  156 */       if (d <= c) {
/*      */         break;
/*      */       }
/*      */     } 
/*  160 */     SA[sa + i] = v;
/*      */   }
/*      */   
/*      */   private void ssHeapSort(int td, int pa, int sa, int size) {
/*  164 */     int[] SA = this.SA;
/*  165 */     byte[] T = this.T;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  170 */     int m = size;
/*  171 */     if (size % 2 == 0) {
/*  172 */       m--;
/*  173 */       if ((T[td + SA[pa + SA[sa + m / 2]]] & 0xFF) < (T[td + SA[pa + SA[sa + m]]] & 0xFF)) {
/*  174 */         swapElements(SA, sa + m, SA, sa + m / 2);
/*      */       }
/*      */     } 
/*      */     int i;
/*  178 */     for (i = m / 2 - 1; 0 <= i; i--) {
/*  179 */       ssFixdown(td, pa, sa, i, m);
/*      */     }
/*      */     
/*  182 */     if (size % 2 == 0) {
/*  183 */       swapElements(SA, sa, SA, sa + m);
/*  184 */       ssFixdown(td, pa, sa, 0, m);
/*      */     } 
/*      */     
/*  187 */     for (i = m - 1; 0 < i; i--) {
/*  188 */       int t = SA[sa];
/*  189 */       SA[sa] = SA[sa + i];
/*  190 */       ssFixdown(td, pa, sa, 0, i);
/*  191 */       SA[sa + i] = t;
/*      */     } 
/*      */   }
/*      */   
/*      */   private int ssMedian3(int td, int pa, int v1, int v2, int v3) {
/*  196 */     int[] SA = this.SA;
/*  197 */     byte[] T = this.T;
/*      */     
/*  199 */     int T_v1 = T[td + SA[pa + SA[v1]]] & 0xFF;
/*  200 */     int T_v2 = T[td + SA[pa + SA[v2]]] & 0xFF;
/*  201 */     int T_v3 = T[td + SA[pa + SA[v3]]] & 0xFF;
/*      */     
/*  203 */     if (T_v1 > T_v2) {
/*  204 */       int temp = v1;
/*  205 */       v1 = v2;
/*  206 */       v2 = temp;
/*  207 */       int T_vtemp = T_v1;
/*  208 */       T_v1 = T_v2;
/*  209 */       T_v2 = T_vtemp;
/*      */     } 
/*  211 */     if (T_v2 > T_v3) {
/*  212 */       if (T_v1 > T_v3) {
/*  213 */         return v1;
/*      */       }
/*  215 */       return v3;
/*      */     } 
/*  217 */     return v2;
/*      */   }
/*      */   
/*      */   private int ssMedian5(int td, int pa, int v1, int v2, int v3, int v4, int v5) {
/*  221 */     int[] SA = this.SA;
/*  222 */     byte[] T = this.T;
/*      */     
/*  224 */     int T_v1 = T[td + SA[pa + SA[v1]]] & 0xFF;
/*  225 */     int T_v2 = T[td + SA[pa + SA[v2]]] & 0xFF;
/*  226 */     int T_v3 = T[td + SA[pa + SA[v3]]] & 0xFF;
/*  227 */     int T_v4 = T[td + SA[pa + SA[v4]]] & 0xFF;
/*  228 */     int T_v5 = T[td + SA[pa + SA[v5]]] & 0xFF;
/*      */ 
/*      */ 
/*      */     
/*  232 */     if (T_v2 > T_v3) {
/*  233 */       int temp = v2;
/*  234 */       v2 = v3;
/*  235 */       v3 = temp;
/*  236 */       int T_vtemp = T_v2;
/*  237 */       T_v2 = T_v3;
/*  238 */       T_v3 = T_vtemp;
/*      */     } 
/*  240 */     if (T_v4 > T_v5) {
/*  241 */       int temp = v4;
/*  242 */       v4 = v5;
/*  243 */       v5 = temp;
/*  244 */       int T_vtemp = T_v4;
/*  245 */       T_v4 = T_v5;
/*  246 */       T_v5 = T_vtemp;
/*      */     } 
/*  248 */     if (T_v2 > T_v4) {
/*  249 */       int temp = v2;
/*  250 */       v4 = temp;
/*  251 */       int T_vtemp = T_v2;
/*  252 */       T_v4 = T_vtemp;
/*  253 */       temp = v3;
/*  254 */       v3 = v5;
/*  255 */       v5 = temp;
/*  256 */       T_vtemp = T_v3;
/*  257 */       T_v3 = T_v5;
/*  258 */       T_v5 = T_vtemp;
/*      */     } 
/*  260 */     if (T_v1 > T_v3) {
/*  261 */       int temp = v1;
/*  262 */       v1 = v3;
/*  263 */       v3 = temp;
/*  264 */       int T_vtemp = T_v1;
/*  265 */       T_v1 = T_v3;
/*  266 */       T_v3 = T_vtemp;
/*      */     } 
/*  268 */     if (T_v1 > T_v4) {
/*  269 */       int temp = v1;
/*  270 */       v4 = temp;
/*  271 */       int T_vtemp = T_v1;
/*  272 */       T_v4 = T_vtemp;
/*  273 */       v3 = v5;
/*  274 */       T_v3 = T_v5;
/*      */     } 
/*  276 */     if (T_v3 > T_v4) {
/*  277 */       return v4;
/*      */     }
/*  279 */     return v3;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int ssPivot(int td, int pa, int first, int last) {
/*  286 */     int t = last - first;
/*  287 */     int middle = first + t / 2;
/*      */     
/*  289 */     if (t <= 512) {
/*  290 */       if (t <= 32) {
/*  291 */         return ssMedian3(td, pa, first, middle, last - 1);
/*      */       }
/*  293 */       t >>= 2;
/*  294 */       return ssMedian5(td, pa, first, first + t, middle, last - 1 - t, last - 1);
/*      */     } 
/*  296 */     t >>= 3;
/*  297 */     return ssMedian3(td, pa, 
/*      */         
/*  299 */         ssMedian3(td, pa, first, first + t, first + (t << 1)), 
/*  300 */         ssMedian3(td, pa, middle - t, middle, middle + t), 
/*  301 */         ssMedian3(td, pa, last - 1 - (t << 1), last - 1 - t, last - 1));
/*      */   }
/*      */ 
/*      */   
/*      */   private static int ssLog(int n) {
/*  306 */     return ((n & 0xFF00) != 0) ? (8 + LOG_2_TABLE[n >> 8 & 0xFF]) : LOG_2_TABLE[n & 0xFF];
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int ssSubstringPartition(int pa, int first, int last, int depth) {
/*  312 */     int[] SA = this.SA;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  317 */     int a = first - 1, b = last; while (true) {
/*  318 */       if (++a < b && SA[pa + SA[a]] + depth >= SA[pa + SA[a] + 1] + 1) {
/*  319 */         SA[a] = SA[a] ^ 0xFFFFFFFF; continue;
/*      */       } 
/*  321 */       b--;
/*  322 */       while (a < b && SA[pa + SA[b]] + depth < SA[pa + SA[b] + 1] + 1) {
/*  323 */         b--;
/*      */       }
/*      */       
/*  326 */       if (b <= a) {
/*      */         break;
/*      */       }
/*  329 */       int t = SA[b] ^ 0xFFFFFFFF;
/*  330 */       SA[b] = SA[a];
/*  331 */       SA[a] = t;
/*      */     } 
/*  333 */     if (first < a) {
/*  334 */       SA[first] = SA[first] ^ 0xFFFFFFFF;
/*      */     }
/*  336 */     return a;
/*      */   }
/*      */   
/*      */   private static class StackEntry {
/*      */     final int a;
/*      */     final int b;
/*      */     final int c;
/*      */     final int d;
/*      */     
/*      */     StackEntry(int a, int b, int c, int d) {
/*  346 */       this.a = a;
/*  347 */       this.b = b;
/*  348 */       this.c = c;
/*  349 */       this.d = d;
/*      */     }
/*      */   }
/*      */   
/*      */   private void ssMultiKeyIntroSort(int pa, int first, int last, int depth) {
/*  354 */     int[] SA = this.SA;
/*  355 */     byte[] T = this.T;
/*      */     
/*  357 */     StackEntry[] stack = new StackEntry[64];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  364 */     int x = 0;
/*      */     
/*  366 */     int ssize = 0, limit = ssLog(last - first); while (true) {
/*  367 */       while (last - first <= 8) {
/*  368 */         if (1 < last - first) {
/*  369 */           ssInsertionSort(pa, first, last, depth);
/*      */         }
/*  371 */         if (ssize == 0) {
/*      */           return;
/*      */         }
/*  374 */         StackEntry entry = stack[--ssize];
/*  375 */         first = entry.a;
/*  376 */         last = entry.b;
/*  377 */         depth = entry.c;
/*  378 */         limit = entry.d;
/*      */       } 
/*      */ 
/*      */       
/*  382 */       int Td = depth;
/*  383 */       if (limit-- == 0) {
/*  384 */         ssHeapSort(Td, pa, first, last - first);
/*      */       }
/*  386 */       if (limit < 0) {
/*  387 */         int i; int j; for (i = first + 1, j = T[Td + SA[pa + SA[first]]] & 0xFF; i < last; i++) {
/*  388 */           if ((x = T[Td + SA[pa + SA[i]]] & 0xFF) != j) {
/*  389 */             if (1 < i - first) {
/*      */               break;
/*      */             }
/*  392 */             j = x;
/*  393 */             first = i;
/*      */           } 
/*      */         } 
/*  396 */         if ((T[Td + SA[pa + SA[first]] - 1] & 0xFF) < j) {
/*  397 */           first = ssSubstringPartition(pa, first, i, depth);
/*      */         }
/*  399 */         if (i - first <= last - i) {
/*  400 */           if (1 < i - first) {
/*  401 */             stack[ssize++] = new StackEntry(i, last, depth, -1);
/*  402 */             last = i;
/*  403 */             depth++;
/*  404 */             limit = ssLog(i - first); continue;
/*      */           } 
/*  406 */           first = i;
/*  407 */           limit = -1;
/*      */           continue;
/*      */         } 
/*  410 */         if (1 < last - i) {
/*  411 */           stack[ssize++] = new StackEntry(first, i, depth + 1, ssLog(i - first));
/*  412 */           first = i;
/*  413 */           limit = -1; continue;
/*      */         } 
/*  415 */         last = i;
/*  416 */         depth++;
/*  417 */         limit = ssLog(i - first);
/*      */ 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/*  423 */       int a = ssPivot(Td, pa, first, last);
/*  424 */       int v = T[Td + SA[pa + SA[a]]] & 0xFF;
/*  425 */       swapElements(SA, first, SA, a);
/*      */       
/*  427 */       int b = first + 1;
/*  428 */       while (b < last && (x = T[Td + SA[pa + SA[b]]] & 0xFF) == v) {
/*  429 */         b++;
/*      */       }
/*  431 */       if ((a = b) < last && x < v) {
/*  432 */         while (++b < last && (x = T[Td + SA[pa + SA[b]]] & 0xFF) <= v) {
/*  433 */           if (x == v) {
/*  434 */             swapElements(SA, b, SA, a);
/*  435 */             a++;
/*      */           } 
/*      */         } 
/*      */       }
/*      */       
/*  440 */       int c = last - 1;
/*  441 */       while (b < c && (x = T[Td + SA[pa + SA[c]]] & 0xFF) == v)
/*  442 */         c--; 
/*      */       int d;
/*  444 */       if (b < (d = c) && x > v) {
/*  445 */         while (b < --c && (x = T[Td + SA[pa + SA[c]]] & 0xFF) >= v) {
/*  446 */           if (x == v) {
/*  447 */             swapElements(SA, c, SA, d);
/*  448 */             d--;
/*      */           } 
/*      */         } 
/*      */       }
/*  452 */       while (b < c) {
/*  453 */         swapElements(SA, b, SA, c);
/*  454 */         while (++b < c && (x = T[Td + SA[pa + SA[b]]] & 0xFF) <= v) {
/*  455 */           if (x == v) {
/*  456 */             swapElements(SA, b, SA, a);
/*  457 */             a++;
/*      */           } 
/*      */         } 
/*  460 */         while (b < --c && (x = T[Td + SA[pa + SA[c]]] & 0xFF) >= v) {
/*  461 */           if (x == v) {
/*  462 */             swapElements(SA, c, SA, d);
/*  463 */             d--;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/*  468 */       if (a <= d) {
/*  469 */         c = b - 1;
/*      */         int s, t;
/*  471 */         if ((s = a - first) > (t = b - a))
/*  472 */           s = t; 
/*      */         int e, f;
/*  474 */         for (e = first, f = b - s; 0 < s; s--, e++, f++) {
/*  475 */           swapElements(SA, e, SA, f);
/*      */         }
/*  477 */         if ((s = d - c) > (t = last - d - 1)) {
/*  478 */           s = t;
/*      */         }
/*  480 */         for (e = b, f = last - s; 0 < s; s--, e++, f++) {
/*  481 */           swapElements(SA, e, SA, f);
/*      */         }
/*      */         
/*  484 */         a = first + b - a;
/*  485 */         c = last - d - c;
/*  486 */         b = (v <= (T[Td + SA[pa + SA[a]] - 1] & 0xFF)) ? a : ssSubstringPartition(pa, a, c, depth);
/*      */         
/*  488 */         if (a - first <= last - c) {
/*  489 */           if (last - c <= c - b) {
/*  490 */             stack[ssize++] = new StackEntry(b, c, depth + 1, ssLog(c - b));
/*  491 */             stack[ssize++] = new StackEntry(c, last, depth, limit);
/*  492 */             last = a; continue;
/*  493 */           }  if (a - first <= c - b) {
/*  494 */             stack[ssize++] = new StackEntry(c, last, depth, limit);
/*  495 */             stack[ssize++] = new StackEntry(b, c, depth + 1, ssLog(c - b));
/*  496 */             last = a; continue;
/*      */           } 
/*  498 */           stack[ssize++] = new StackEntry(c, last, depth, limit);
/*  499 */           stack[ssize++] = new StackEntry(first, a, depth, limit);
/*  500 */           first = b;
/*  501 */           last = c;
/*  502 */           depth++;
/*  503 */           limit = ssLog(c - b);
/*      */           continue;
/*      */         } 
/*  506 */         if (a - first <= c - b) {
/*  507 */           stack[ssize++] = new StackEntry(b, c, depth + 1, ssLog(c - b));
/*  508 */           stack[ssize++] = new StackEntry(first, a, depth, limit);
/*  509 */           first = c; continue;
/*  510 */         }  if (last - c <= c - b) {
/*  511 */           stack[ssize++] = new StackEntry(first, a, depth, limit);
/*  512 */           stack[ssize++] = new StackEntry(b, c, depth + 1, ssLog(c - b));
/*  513 */           first = c; continue;
/*      */         } 
/*  515 */         stack[ssize++] = new StackEntry(first, a, depth, limit);
/*  516 */         stack[ssize++] = new StackEntry(c, last, depth, limit);
/*  517 */         first = b;
/*  518 */         last = c;
/*  519 */         depth++;
/*  520 */         limit = ssLog(c - b);
/*      */         
/*      */         continue;
/*      */       } 
/*  524 */       limit++;
/*  525 */       if ((T[Td + SA[pa + SA[first]] - 1] & 0xFF) < v) {
/*  526 */         first = ssSubstringPartition(pa, first, last, depth);
/*  527 */         limit = ssLog(last - first);
/*      */       } 
/*  529 */       depth++;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void ssBlockSwap(int[] array1, int first1, int[] array2, int first2, int size) {
/*  538 */     for (int i = size, a = first1, b = first2; 0 < i; i--, a++, b++) {
/*  539 */       swapElements(array1, a, array2, b);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void ssMergeForward(int pa, int[] buf, int bufoffset, int first, int middle, int last, int depth) {
/*  545 */     int[] SA = this.SA;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  552 */     int bufend = bufoffset + middle - first - 1;
/*  553 */     ssBlockSwap(buf, bufoffset, SA, first, middle - first);
/*      */     
/*  555 */     int t = SA[first], i = first, j = bufoffset, k = middle; label46: while (true) {
/*  556 */       int r = ssCompare(pa + buf[j], pa + SA[k], depth);
/*  557 */       if (r < 0)
/*      */       { while (true)
/*  559 */         { SA[i++] = buf[j];
/*  560 */           if (bufend <= j) {
/*  561 */             buf[j] = t;
/*      */             return;
/*      */           } 
/*  564 */           buf[j++] = SA[i];
/*  565 */           if (buf[j] >= 0)
/*  566 */             continue label46;  }  break; }  if (r > 0) {
/*      */         while (true)
/*  568 */         { SA[i++] = SA[k];
/*  569 */           SA[k++] = SA[i];
/*  570 */           if (last <= k) {
/*  571 */             while (j < bufend) {
/*  572 */               SA[i++] = buf[j]; buf[j++] = SA[i];
/*      */             } 
/*  574 */             SA[i] = buf[j]; buf[j] = t;
/*      */             return;
/*      */           } 
/*  577 */           if (SA[k] >= 0)
/*      */             continue label46;  }  break;
/*  579 */       }  SA[k] = SA[k] ^ 0xFFFFFFFF;
/*      */       do {
/*  581 */         SA[i++] = buf[j];
/*  582 */         if (bufend <= j) {
/*  583 */           buf[j] = t;
/*      */           return;
/*      */         } 
/*  586 */         buf[j++] = SA[i];
/*  587 */       } while (buf[j] < 0);
/*      */       
/*      */       while (true) {
/*  590 */         SA[i++] = SA[k];
/*  591 */         SA[k++] = SA[i];
/*  592 */         if (last <= k) {
/*  593 */           while (j < bufend) {
/*  594 */             SA[i++] = buf[j];
/*  595 */             buf[j++] = SA[i];
/*      */           } 
/*  597 */           SA[i] = buf[j]; buf[j] = t;
/*      */           return;
/*      */         } 
/*  600 */         if (SA[k] >= 0)
/*      */           continue label46; 
/*      */       } 
/*      */       break;
/*      */     } 
/*      */   }
/*      */   private void ssMergeBackward(int pa, int[] buf, int bufoffset, int first, int middle, int last, int depth) {
/*  607 */     int p1, p2, SA[] = this.SA;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  616 */     int bufend = bufoffset + last - middle;
/*  617 */     ssBlockSwap(buf, bufoffset, SA, middle, last - middle);
/*      */     
/*  619 */     int x = 0;
/*  620 */     if (buf[bufend - 1] < 0) {
/*  621 */       x |= 0x1;
/*  622 */       p1 = pa + (buf[bufend - 1] ^ 0xFFFFFFFF);
/*      */     } else {
/*  624 */       p1 = pa + buf[bufend - 1];
/*      */     } 
/*  626 */     if (SA[middle - 1] < 0) {
/*  627 */       x |= 0x2;
/*  628 */       p2 = pa + (SA[middle - 1] ^ 0xFFFFFFFF);
/*      */     } else {
/*  630 */       p2 = pa + SA[middle - 1];
/*      */     } 
/*  632 */     int t = SA[last - 1], i = last - 1, j = bufend - 1, k = middle - 1;
/*      */     while (true) {
/*  634 */       int r = ssCompare(p1, p2, depth);
/*  635 */       if (r > 0) {
/*  636 */         if ((x & 0x1) != 0)
/*      */           while (true) {
/*  638 */             SA[i--] = buf[j];
/*  639 */             buf[j--] = SA[i];
/*  640 */             if (buf[j] >= 0) {
/*  641 */               x ^= 0x1; break;
/*      */             } 
/*  643 */           }   SA[i--] = buf[j];
/*  644 */         if (j <= bufoffset) {
/*  645 */           buf[j] = t;
/*      */           return;
/*      */         } 
/*  648 */         buf[j--] = SA[i];
/*      */         
/*  650 */         if (buf[j] < 0) {
/*  651 */           x |= 0x1;
/*  652 */           p1 = pa + (buf[j] ^ 0xFFFFFFFF); continue;
/*      */         } 
/*  654 */         p1 = pa + buf[j]; continue;
/*      */       } 
/*  656 */       if (r < 0) {
/*  657 */         if ((x & 0x2) != 0)
/*      */           while (true) {
/*  659 */             SA[i--] = SA[k];
/*  660 */             SA[k--] = SA[i];
/*  661 */             if (SA[k] >= 0) {
/*  662 */               x ^= 0x2; break;
/*      */             } 
/*  664 */           }   SA[i--] = SA[k];
/*  665 */         SA[k--] = SA[i];
/*  666 */         if (k < first) {
/*  667 */           while (bufoffset < j) {
/*  668 */             SA[i--] = buf[j];
/*  669 */             buf[j--] = SA[i];
/*      */           } 
/*  671 */           SA[i] = buf[j];
/*  672 */           buf[j] = t;
/*      */           
/*      */           return;
/*      */         } 
/*  676 */         if (SA[k] < 0) {
/*  677 */           x |= 0x2;
/*  678 */           p2 = pa + (SA[k] ^ 0xFFFFFFFF); continue;
/*      */         } 
/*  680 */         p2 = pa + SA[k];
/*      */         continue;
/*      */       } 
/*  683 */       if ((x & 0x1) != 0)
/*      */         while (true) {
/*  685 */           SA[i--] = buf[j];
/*  686 */           buf[j--] = SA[i];
/*  687 */           if (buf[j] >= 0) {
/*  688 */             x ^= 0x1; break;
/*      */           } 
/*  690 */         }   SA[i--] = buf[j] ^ 0xFFFFFFFF;
/*  691 */       if (j <= bufoffset) {
/*  692 */         buf[j] = t;
/*      */         return;
/*      */       } 
/*  695 */       buf[j--] = SA[i];
/*      */       
/*  697 */       if ((x & 0x2) != 0)
/*      */         while (true) {
/*  699 */           SA[i--] = SA[k];
/*  700 */           SA[k--] = SA[i];
/*  701 */           if (SA[k] >= 0) {
/*  702 */             x ^= 0x2; break;
/*      */           } 
/*  704 */         }   SA[i--] = SA[k];
/*  705 */       SA[k--] = SA[i];
/*  706 */       if (k < first) {
/*  707 */         while (bufoffset < j) {
/*  708 */           SA[i--] = buf[j];
/*  709 */           buf[j--] = SA[i];
/*      */         } 
/*  711 */         SA[i] = buf[j];
/*  712 */         buf[j] = t;
/*      */         
/*      */         return;
/*      */       } 
/*  716 */       if (buf[j] < 0) {
/*  717 */         x |= 0x1;
/*  718 */         p1 = pa + (buf[j] ^ 0xFFFFFFFF);
/*      */       } else {
/*  720 */         p1 = pa + buf[j];
/*      */       } 
/*  722 */       if (SA[k] < 0) {
/*  723 */         x |= 0x2;
/*  724 */         p2 = pa + (SA[k] ^ 0xFFFFFFFF); continue;
/*      */       } 
/*  726 */       p2 = pa + SA[k];
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static int getIDX(int a) {
/*  733 */     return (0 <= a) ? a : (a ^ 0xFFFFFFFF);
/*      */   }
/*      */   
/*      */   private void ssMergeCheckEqual(int pa, int depth, int a) {
/*  737 */     int[] SA = this.SA;
/*      */     
/*  739 */     if (0 <= SA[a] && ssCompare(pa + getIDX(SA[a - 1]), pa + SA[a], depth) == 0) {
/*  740 */       SA[a] = SA[a] ^ 0xFFFFFFFF;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void ssMerge(int pa, int first, int middle, int last, int[] buf, int bufoffset, int bufsize, int depth) {
/*  746 */     int[] SA = this.SA;
/*      */     
/*  748 */     StackEntry[] stack = new StackEntry[64];
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  755 */     int check = 0, ssize = 0;
/*      */     while (true) {
/*  757 */       while (last - middle <= bufsize) {
/*  758 */         if (first < middle && middle < last) {
/*  759 */           ssMergeBackward(pa, buf, bufoffset, first, middle, last, depth);
/*      */         }
/*      */         
/*  762 */         if ((check & 0x1) != 0) {
/*  763 */           ssMergeCheckEqual(pa, depth, first);
/*      */         }
/*  765 */         if ((check & 0x2) != 0) {
/*  766 */           ssMergeCheckEqual(pa, depth, last);
/*      */         }
/*  768 */         if (ssize == 0) {
/*      */           return;
/*      */         }
/*  771 */         StackEntry stackEntry = stack[--ssize];
/*  772 */         first = stackEntry.a;
/*  773 */         middle = stackEntry.b;
/*  774 */         last = stackEntry.c;
/*  775 */         check = stackEntry.d;
/*      */       } 
/*      */ 
/*      */       
/*  779 */       if (middle - first <= bufsize) {
/*  780 */         if (first < middle) {
/*  781 */           ssMergeForward(pa, buf, bufoffset, first, middle, last, depth);
/*      */         }
/*  783 */         if ((check & 0x1) != 0) {
/*  784 */           ssMergeCheckEqual(pa, depth, first);
/*      */         }
/*  786 */         if ((check & 0x2) != 0) {
/*  787 */           ssMergeCheckEqual(pa, depth, last);
/*      */         }
/*  789 */         if (ssize == 0) {
/*      */           return;
/*      */         }
/*  792 */         StackEntry stackEntry = stack[--ssize];
/*  793 */         first = stackEntry.a;
/*  794 */         middle = stackEntry.b;
/*  795 */         last = stackEntry.c;
/*  796 */         check = stackEntry.d;
/*      */         
/*      */         continue;
/*      */       } 
/*  800 */       int m = 0, len = Math.min(middle - first, last - middle), half = len >> 1;
/*  801 */       for (; 0 < len; 
/*  802 */         len = half, half >>= 1) {
/*      */         
/*  804 */         if (ssCompare(pa + getIDX(SA[middle + m + half]), pa + 
/*  805 */             getIDX(SA[middle - m - half - 1]), depth) < 0) {
/*  806 */           m += half + 1;
/*  807 */           half -= len & 0x1 ^ 0x1;
/*      */         } 
/*      */       } 
/*      */       
/*  811 */       if (0 < m) {
/*  812 */         ssBlockSwap(SA, middle - m, SA, middle, m);
/*  813 */         int j = middle, i = j;
/*  814 */         int next = 0;
/*  815 */         if (middle + m < last) {
/*  816 */           if (SA[middle + m] < 0) {
/*  817 */             while (SA[i - 1] < 0) {
/*  818 */               i--;
/*      */             }
/*  820 */             SA[middle + m] = SA[middle + m] ^ 0xFFFFFFFF;
/*      */           } 
/*  822 */           for (j = middle; SA[j] < 0;) {
/*  823 */             j++;
/*      */           }
/*  825 */           next = 1;
/*      */         } 
/*  827 */         if (i - first <= last - j) {
/*  828 */           stack[ssize++] = new StackEntry(j, middle + m, last, check & 0x2 | next & 0x1);
/*  829 */           middle -= m;
/*  830 */           last = i;
/*  831 */           check &= 0x1; continue;
/*      */         } 
/*  833 */         if (i == middle && middle == j) {
/*  834 */           next <<= 1;
/*      */         }
/*  836 */         stack[ssize++] = new StackEntry(first, middle - m, i, check & 0x1 | next & 0x2);
/*  837 */         first = j;
/*  838 */         middle += m;
/*  839 */         check = check & 0x2 | next & 0x1;
/*      */         continue;
/*      */       } 
/*  842 */       if ((check & 0x1) != 0) {
/*  843 */         ssMergeCheckEqual(pa, depth, first);
/*      */       }
/*  845 */       ssMergeCheckEqual(pa, depth, middle);
/*  846 */       if ((check & 0x2) != 0) {
/*  847 */         ssMergeCheckEqual(pa, depth, last);
/*      */       }
/*  849 */       if (ssize == 0) {
/*      */         return;
/*      */       }
/*  852 */       StackEntry entry = stack[--ssize];
/*  853 */       first = entry.a;
/*  854 */       middle = entry.b;
/*  855 */       last = entry.c;
/*  856 */       check = entry.d;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void subStringSort(int pa, int first, int last, int[] buf, int bufoffset, int bufsize, int depth, boolean lastsuffix, int size) {
/*  864 */     int[] SA = this.SA;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  872 */     if (lastsuffix)
/*  873 */       first++; 
/*      */     int a, i;
/*  875 */     for (a = first, i = 0; a + 1024 < last; a += 1024, i++) {
/*  876 */       ssMultiKeyIntroSort(pa, a, a + 1024, depth);
/*  877 */       int[] curbuf = SA;
/*  878 */       int curbufoffset = a + 1024;
/*  879 */       int curbufsize = last - a + 1024;
/*  880 */       if (curbufsize <= bufsize) {
/*  881 */         curbufsize = bufsize;
/*  882 */         curbuf = buf;
/*  883 */         curbufoffset = bufoffset;
/*      */       }  int j;
/*  885 */       for (int b = a, m = 1024; (j & 0x1) != 0; b -= m, m <<= 1, j >>>= 1) {
/*  886 */         ssMerge(pa, b - m, b, b + m, curbuf, curbufoffset, curbufsize, depth);
/*      */       }
/*      */     } 
/*      */     
/*  890 */     ssMultiKeyIntroSort(pa, a, last, depth);
/*      */     
/*  892 */     for (int k = 1024; i != 0; k <<= 1, i >>= 1) {
/*  893 */       if ((i & 0x1) != 0) {
/*  894 */         ssMerge(pa, a - k, a, last, buf, bufoffset, bufsize, depth);
/*  895 */         a -= k;
/*      */       } 
/*      */     } 
/*      */     
/*  899 */     if (lastsuffix) {
/*      */       
/*  901 */       a = first; i = SA[first - 1]; int r = 1;
/*  902 */       for (; a < last && (SA[a] < 0 || 0 < (r = ssCompareLast(pa, pa + i, pa + SA[a], depth, size))); 
/*  903 */         a++) {
/*  904 */         SA[a - 1] = SA[a];
/*      */       }
/*  906 */       if (r == 0) {
/*  907 */         SA[a] = SA[a] ^ 0xFFFFFFFF;
/*      */       }
/*  909 */       SA[a - 1] = i;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int trGetC(int isa, int isaD, int isaN, int p) {
/*  916 */     return (isaD + p < isaN) ? this.SA[isaD + p] : this.SA[isa + (isaD - isa + p) % (isaN - isa)];
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void trFixdown(int isa, int isaD, int isaN, int sa, int i, int size) {
/*  922 */     int[] SA = this.SA;
/*      */ 
/*      */     
/*      */     int j, v, c;
/*      */ 
/*      */     
/*  928 */     for (v = SA[sa + i], c = trGetC(isa, isaD, isaN, v); (j = 2 * i + 1) < size; SA[sa + i] = SA[sa + k], i = k) {
/*  929 */       int k = j++;
/*  930 */       int d = trGetC(isa, isaD, isaN, SA[sa + k]); int e;
/*  931 */       if (d < (e = trGetC(isa, isaD, isaN, SA[sa + j]))) {
/*  932 */         k = j;
/*  933 */         d = e;
/*      */       } 
/*  935 */       if (d <= c) {
/*      */         break;
/*      */       }
/*      */     } 
/*  939 */     SA[sa + i] = v;
/*      */   }
/*      */   
/*      */   private void trHeapSort(int isa, int isaD, int isaN, int sa, int size) {
/*  943 */     int[] SA = this.SA;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  948 */     int m = size;
/*  949 */     if (size % 2 == 0) {
/*  950 */       m--;
/*  951 */       if (trGetC(isa, isaD, isaN, SA[sa + m / 2]) < trGetC(isa, isaD, isaN, SA[sa + m])) {
/*  952 */         swapElements(SA, sa + m, SA, sa + m / 2);
/*      */       }
/*      */     } 
/*      */     int i;
/*  956 */     for (i = m / 2 - 1; 0 <= i; i--) {
/*  957 */       trFixdown(isa, isaD, isaN, sa, i, m);
/*      */     }
/*      */     
/*  960 */     if (size % 2 == 0) {
/*  961 */       swapElements(SA, sa, SA, sa + m);
/*  962 */       trFixdown(isa, isaD, isaN, sa, 0, m);
/*      */     } 
/*      */     
/*  965 */     for (i = m - 1; 0 < i; i--) {
/*  966 */       int t = SA[sa];
/*  967 */       SA[sa] = SA[sa + i];
/*  968 */       trFixdown(isa, isaD, isaN, sa, 0, i);
/*  969 */       SA[sa + i] = t;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void trInsertionSort(int isa, int isaD, int isaN, int first, int last) {
/*  974 */     int[] SA = this.SA;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  979 */     for (int a = first + 1; a < last; a++) {
/*  980 */       int b; int t; int r; for (t = SA[a], b = a - 1; 0 > (r = trGetC(isa, isaD, isaN, t) - trGetC(isa, isaD, isaN, SA[b])); ) {
/*      */         do {
/*  982 */           SA[b + 1] = SA[b];
/*  983 */         } while (first <= --b && SA[b] < 0);
/*  984 */         if (b < first) {
/*      */           break;
/*      */         }
/*      */       } 
/*  988 */       if (r == 0) {
/*  989 */         SA[b] = SA[b] ^ 0xFFFFFFFF;
/*      */       }
/*  991 */       SA[b + 1] = t;
/*      */     } 
/*      */   }
/*      */   
/*      */   private static int trLog(int n) {
/*  996 */     return ((n & 0xFFFF0000) != 0) ? (((n & 0xFF000000) != 0) ? (24 + LOG_2_TABLE[n >> 24 & 0xFF]) : LOG_2_TABLE[n >> 16 & 0x10F]) : (((n & 0xFF00) != 0) ? (8 + LOG_2_TABLE[n >> 8 & 0xFF]) : LOG_2_TABLE[n & 0xFF]);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private int trMedian3(int isa, int isaD, int isaN, int v1, int v2, int v3) {
/* 1002 */     int[] SA = this.SA;
/*      */     
/* 1004 */     int SA_v1 = trGetC(isa, isaD, isaN, SA[v1]);
/* 1005 */     int SA_v2 = trGetC(isa, isaD, isaN, SA[v2]);
/* 1006 */     int SA_v3 = trGetC(isa, isaD, isaN, SA[v3]);
/*      */     
/* 1008 */     if (SA_v1 > SA_v2) {
/* 1009 */       int temp = v1;
/* 1010 */       v1 = v2;
/* 1011 */       v2 = temp;
/* 1012 */       int SA_vtemp = SA_v1;
/* 1013 */       SA_v1 = SA_v2;
/* 1014 */       SA_v2 = SA_vtemp;
/*      */     } 
/* 1016 */     if (SA_v2 > SA_v3) {
/* 1017 */       if (SA_v1 > SA_v3) {
/* 1018 */         return v1;
/*      */       }
/* 1020 */       return v3;
/*      */     } 
/*      */     
/* 1023 */     return v2;
/*      */   }
/*      */   
/*      */   private int trMedian5(int isa, int isaD, int isaN, int v1, int v2, int v3, int v4, int v5) {
/* 1027 */     int[] SA = this.SA;
/*      */     
/* 1029 */     int SA_v1 = trGetC(isa, isaD, isaN, SA[v1]);
/* 1030 */     int SA_v2 = trGetC(isa, isaD, isaN, SA[v2]);
/* 1031 */     int SA_v3 = trGetC(isa, isaD, isaN, SA[v3]);
/* 1032 */     int SA_v4 = trGetC(isa, isaD, isaN, SA[v4]);
/* 1033 */     int SA_v5 = trGetC(isa, isaD, isaN, SA[v5]);
/*      */ 
/*      */ 
/*      */     
/* 1037 */     if (SA_v2 > SA_v3) {
/* 1038 */       int temp = v2;
/* 1039 */       v2 = v3;
/* 1040 */       v3 = temp;
/* 1041 */       int SA_vtemp = SA_v2;
/* 1042 */       SA_v2 = SA_v3;
/* 1043 */       SA_v3 = SA_vtemp;
/*      */     } 
/* 1045 */     if (SA_v4 > SA_v5) {
/* 1046 */       int temp = v4;
/* 1047 */       v4 = v5;
/* 1048 */       v5 = temp;
/* 1049 */       int SA_vtemp = SA_v4;
/* 1050 */       SA_v4 = SA_v5;
/* 1051 */       SA_v5 = SA_vtemp;
/*      */     } 
/* 1053 */     if (SA_v2 > SA_v4) {
/* 1054 */       int temp = v2;
/* 1055 */       v4 = temp;
/* 1056 */       int SA_vtemp = SA_v2;
/* 1057 */       SA_v4 = SA_vtemp;
/* 1058 */       temp = v3;
/* 1059 */       v3 = v5;
/* 1060 */       v5 = temp;
/* 1061 */       SA_vtemp = SA_v3;
/* 1062 */       SA_v3 = SA_v5;
/* 1063 */       SA_v5 = SA_vtemp;
/*      */     } 
/* 1065 */     if (SA_v1 > SA_v3) {
/* 1066 */       int temp = v1;
/* 1067 */       v1 = v3;
/* 1068 */       v3 = temp;
/* 1069 */       int SA_vtemp = SA_v1;
/* 1070 */       SA_v1 = SA_v3;
/* 1071 */       SA_v3 = SA_vtemp;
/*      */     } 
/* 1073 */     if (SA_v1 > SA_v4) {
/* 1074 */       int temp = v1;
/* 1075 */       v4 = temp;
/* 1076 */       int SA_vtemp = SA_v1;
/* 1077 */       SA_v4 = SA_vtemp;
/* 1078 */       v3 = v5;
/* 1079 */       SA_v3 = SA_v5;
/*      */     } 
/* 1081 */     if (SA_v3 > SA_v4) {
/* 1082 */       return v4;
/*      */     }
/* 1084 */     return v3;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int trPivot(int isa, int isaD, int isaN, int first, int last) {
/* 1091 */     int t = last - first;
/* 1092 */     int middle = first + t / 2;
/*      */     
/* 1094 */     if (t <= 512) {
/* 1095 */       if (t <= 32) {
/* 1096 */         return trMedian3(isa, isaD, isaN, first, middle, last - 1);
/*      */       }
/* 1098 */       t >>= 2;
/* 1099 */       return trMedian5(isa, isaD, isaN, first, first + t, middle, last - 1 - t, last - 1);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1106 */     t >>= 3;
/* 1107 */     return trMedian3(isa, isaD, isaN, 
/*      */         
/* 1109 */         trMedian3(isa, isaD, isaN, first, first + t, first + (t << 1)), 
/* 1110 */         trMedian3(isa, isaD, isaN, middle - t, middle, middle + t), 
/* 1111 */         trMedian3(isa, isaD, isaN, last - 1 - (t << 1), last - 1 - t, last - 1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void lsUpdateGroup(int isa, int first, int last) {
/* 1118 */     int[] SA = this.SA;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1123 */     for (int a = first; a < last; ) {
/* 1124 */       if (0 <= SA[a]) {
/* 1125 */         int i = a;
/*      */         do {
/* 1127 */           SA[isa + SA[a]] = a;
/* 1128 */         } while (++a < last && 0 <= SA[a]);
/* 1129 */         SA[i] = i - a;
/* 1130 */         if (last <= a) {
/*      */           break;
/*      */         }
/*      */       } 
/* 1134 */       int b = a;
/*      */       while (true) {
/* 1136 */         SA[a] = SA[a] ^ 0xFFFFFFFF;
/* 1137 */         if (SA[++a] >= 0) {
/* 1138 */           int t = a;
/*      */           for (;; a++) {
/* 1140 */             SA[isa + SA[b]] = t;
/* 1141 */             if (++b > a)
/*      */               continue;  continue;
/*      */           } 
/*      */         } 
/*      */       } 
/* 1146 */     }  } private void lsIntroSort(int isa, int isaD, int isaN, int first, int last) { int[] SA = this.SA;
/*      */     
/* 1148 */     StackEntry[] stack = new StackEntry[64];
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1153 */     int x = 0;
/*      */ 
/*      */     
/* 1156 */     int ssize = 0, limit = trLog(last - first); while (true) {
/* 1157 */       while (last - first <= 8) {
/* 1158 */         if (1 < last - first) {
/* 1159 */           trInsertionSort(isa, isaD, isaN, first, last);
/* 1160 */           lsUpdateGroup(isa, first, last);
/* 1161 */         } else if (last - first == 1) {
/* 1162 */           SA[first] = -1;
/*      */         } 
/* 1164 */         if (ssize == 0) {
/*      */           return;
/*      */         }
/* 1167 */         StackEntry stackEntry = stack[--ssize];
/* 1168 */         first = stackEntry.a;
/* 1169 */         last = stackEntry.b;
/* 1170 */         limit = stackEntry.c;
/*      */       } 
/*      */ 
/*      */       
/* 1174 */       if (limit-- == 0) {
/* 1175 */         trHeapSort(isa, isaD, isaN, first, last - first); int i;
/* 1176 */         for (i = last - 1; first < i; i = j) {
/* 1177 */           x = trGetC(isa, isaD, isaN, SA[i]); int j = i - 1;
/* 1178 */           for (; first <= j && trGetC(isa, isaD, isaN, SA[j]) == x; 
/* 1179 */             j--) {
/* 1180 */             SA[j] = SA[j] ^ 0xFFFFFFFF;
/*      */           }
/*      */         } 
/* 1183 */         lsUpdateGroup(isa, first, last);
/* 1184 */         if (ssize == 0) {
/*      */           return;
/*      */         }
/* 1187 */         StackEntry stackEntry = stack[--ssize];
/* 1188 */         first = stackEntry.a;
/* 1189 */         last = stackEntry.b;
/* 1190 */         limit = stackEntry.c;
/*      */         
/*      */         continue;
/*      */       } 
/* 1194 */       int a = trPivot(isa, isaD, isaN, first, last);
/* 1195 */       swapElements(SA, first, SA, a);
/* 1196 */       int v = trGetC(isa, isaD, isaN, SA[first]);
/*      */       
/* 1198 */       int b = first + 1;
/* 1199 */       while (b < last && (x = trGetC(isa, isaD, isaN, SA[b])) == v) {
/* 1200 */         b++;
/*      */       }
/* 1202 */       if ((a = b) < last && x < v) {
/* 1203 */         while (++b < last && (x = trGetC(isa, isaD, isaN, SA[b])) <= v) {
/* 1204 */           if (x == v) {
/* 1205 */             swapElements(SA, b, SA, a);
/* 1206 */             a++;
/*      */           } 
/*      */         } 
/*      */       }
/*      */       
/* 1211 */       int c = last - 1;
/* 1212 */       while (b < c && (x = trGetC(isa, isaD, isaN, SA[c])) == v)
/* 1213 */         c--; 
/*      */       int d;
/* 1215 */       if (b < (d = c) && x > v) {
/* 1216 */         while (b < --c && (x = trGetC(isa, isaD, isaN, SA[c])) >= v) {
/* 1217 */           if (x == v) {
/* 1218 */             swapElements(SA, c, SA, d);
/* 1219 */             d--;
/*      */           } 
/*      */         } 
/*      */       }
/* 1223 */       while (b < c) {
/* 1224 */         swapElements(SA, b, SA, c);
/* 1225 */         while (++b < c && (x = trGetC(isa, isaD, isaN, SA[b])) <= v) {
/* 1226 */           if (x == v) {
/* 1227 */             swapElements(SA, b, SA, a);
/* 1228 */             a++;
/*      */           } 
/*      */         } 
/* 1231 */         while (b < --c && (x = trGetC(isa, isaD, isaN, SA[c])) >= v) {
/* 1232 */           if (x == v) {
/* 1233 */             swapElements(SA, c, SA, d);
/* 1234 */             d--;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1239 */       if (a <= d) {
/* 1240 */         c = b - 1;
/*      */         int s, t;
/* 1242 */         if ((s = a - first) > (t = b - a))
/* 1243 */           s = t; 
/*      */         int e, f;
/* 1245 */         for (e = first, f = b - s; 0 < s; s--, e++, f++) {
/* 1246 */           swapElements(SA, e, SA, f);
/*      */         }
/* 1248 */         if ((s = d - c) > (t = last - d - 1)) {
/* 1249 */           s = t;
/*      */         }
/* 1251 */         for (e = b, f = last - s; 0 < s; s--, e++, f++) {
/* 1252 */           swapElements(SA, e, SA, f);
/*      */         }
/*      */         
/* 1255 */         a = first + b - a;
/* 1256 */         b = last - d - c;
/*      */         
/* 1258 */         for (c = first, v = a - 1; c < a; c++) {
/* 1259 */           SA[isa + SA[c]] = v;
/*      */         }
/* 1261 */         if (b < last) {
/* 1262 */           for (c = a, v = b - 1; c < b; c++) {
/* 1263 */             SA[isa + SA[c]] = v;
/*      */           }
/*      */         }
/* 1266 */         if (b - a == 1) {
/* 1267 */           SA[a] = -1;
/*      */         }
/*      */         
/* 1270 */         if (a - first <= last - b) {
/* 1271 */           if (first < a) {
/* 1272 */             stack[ssize++] = new StackEntry(b, last, limit, 0);
/* 1273 */             last = a; continue;
/*      */           } 
/* 1275 */           first = b;
/*      */           continue;
/*      */         } 
/* 1278 */         if (b < last) {
/* 1279 */           stack[ssize++] = new StackEntry(first, a, limit, 0);
/* 1280 */           first = b; continue;
/*      */         } 
/* 1282 */         last = a;
/*      */         
/*      */         continue;
/*      */       } 
/* 1286 */       if (ssize == 0) {
/*      */         return;
/*      */       }
/* 1289 */       StackEntry entry = stack[--ssize];
/* 1290 */       first = entry.a;
/* 1291 */       last = entry.b;
/* 1292 */       limit = entry.c;
/*      */     }  }
/*      */ 
/*      */ 
/*      */   
/*      */   private void lsSort(int isa, int n, int depth) {
/* 1298 */     int[] SA = this.SA;
/*      */ 
/*      */     
/*      */     int isaD;
/*      */ 
/*      */     
/* 1304 */     for (isaD = isa + depth; -n < SA[0]; ) {
/* 1305 */       int first = 0;
/* 1306 */       int skip = 0; while (true) {
/*      */         int t;
/* 1308 */         if ((t = SA[first]) < 0) {
/* 1309 */           first -= t;
/* 1310 */           skip += t;
/*      */         } else {
/* 1312 */           if (skip != 0) {
/* 1313 */             SA[first + skip] = skip;
/* 1314 */             skip = 0;
/*      */           } 
/* 1316 */           int last = SA[isa + t] + 1;
/* 1317 */           lsIntroSort(isa, isaD, isa + n, first, last);
/* 1318 */           first = last;
/*      */         } 
/* 1320 */         if (first >= n) {
/* 1321 */           if (skip != 0) {
/* 1322 */             SA[first + skip] = skip;
/*      */           }
/* 1324 */           if (n < isaD - isa) {
/* 1325 */             first = 0;
/*      */             do {
/* 1327 */               if ((t = SA[first]) < 0) {
/* 1328 */                 first -= t;
/*      */               } else {
/* 1330 */                 int last = SA[isa + t] + 1;
/* 1331 */                 for (int i = first; i < last; i++) {
/* 1332 */                   SA[isa + SA[i]] = i;
/*      */                 }
/* 1334 */                 first = last;
/*      */               } 
/* 1336 */             } while (first < n);
/*      */             break;
/*      */           } 
/*      */           isaD += isaD - isa;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   private static class PartitionResult { final int first;
/*      */     final int last;
/*      */     
/*      */     PartitionResult(int first, int last) {
/* 1349 */       this.first = first;
/* 1350 */       this.last = last;
/*      */     } }
/*      */ 
/*      */ 
/*      */   
/*      */   private PartitionResult trPartition(int isa, int isaD, int isaN, int first, int last, int v) {
/* 1356 */     int[] SA = this.SA;
/*      */ 
/*      */ 
/*      */     
/* 1360 */     int x = 0;
/*      */     
/* 1362 */     int b = first;
/* 1363 */     while (b < last && (x = trGetC(isa, isaD, isaN, SA[b])) == v)
/* 1364 */       b++; 
/*      */     int a;
/* 1366 */     if ((a = b) < last && x < v) {
/* 1367 */       while (++b < last && (x = trGetC(isa, isaD, isaN, SA[b])) <= v) {
/* 1368 */         if (x == v) {
/* 1369 */           swapElements(SA, b, SA, a);
/* 1370 */           a++;
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 1375 */     int c = last - 1;
/* 1376 */     while (b < c && (x = trGetC(isa, isaD, isaN, SA[c])) == v)
/* 1377 */       c--; 
/*      */     int d;
/* 1379 */     if (b < (d = c) && x > v) {
/* 1380 */       while (b < --c && (x = trGetC(isa, isaD, isaN, SA[c])) >= v) {
/* 1381 */         if (x == v) {
/* 1382 */           swapElements(SA, c, SA, d);
/* 1383 */           d--;
/*      */         } 
/*      */       } 
/*      */     }
/* 1387 */     while (b < c) {
/* 1388 */       swapElements(SA, b, SA, c);
/* 1389 */       while (++b < c && (x = trGetC(isa, isaD, isaN, SA[b])) <= v) {
/* 1390 */         if (x == v) {
/* 1391 */           swapElements(SA, b, SA, a);
/* 1392 */           a++;
/*      */         } 
/*      */       } 
/* 1395 */       while (b < --c && (x = trGetC(isa, isaD, isaN, SA[c])) >= v) {
/* 1396 */         if (x == v) {
/* 1397 */           swapElements(SA, c, SA, d);
/* 1398 */           d--;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1403 */     if (a <= d) {
/* 1404 */       c = b - 1; int t, s;
/* 1405 */       if ((s = a - first) > (t = b - a))
/* 1406 */         s = t; 
/*      */       int e, f;
/* 1408 */       for (e = first, f = b - s; 0 < s; s--, e++, f++) {
/* 1409 */         swapElements(SA, e, SA, f);
/*      */       }
/* 1411 */       if ((s = d - c) > (t = last - d - 1)) {
/* 1412 */         s = t;
/*      */       }
/* 1414 */       for (e = b, f = last - s; 0 < s; s--, e++, f++) {
/* 1415 */         swapElements(SA, e, SA, f);
/*      */       }
/* 1417 */       first += b - a;
/* 1418 */       last -= d - c;
/*      */     } 
/* 1420 */     return new PartitionResult(first, last);
/*      */   }
/*      */ 
/*      */   
/*      */   private void trCopy(int isa, int isaN, int first, int a, int b, int last, int depth) {
/* 1425 */     int[] SA = this.SA;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1430 */     int v = b - 1;
/*      */     int c, d;
/* 1432 */     for (c = first, d = a - 1; c <= d; c++) {
/* 1433 */       int s; if ((s = SA[c] - depth) < 0) {
/* 1434 */         s += isaN - isa;
/*      */       }
/* 1436 */       if (SA[isa + s] == v) {
/* 1437 */         SA[++d] = s;
/* 1438 */         SA[isa + s] = d;
/*      */       } 
/*      */     }  int e;
/* 1441 */     for (c = last - 1, e = d + 1, d = b; e < d; c--) {
/* 1442 */       int s; if ((s = SA[c] - depth) < 0) {
/* 1443 */         s += isaN - isa;
/*      */       }
/* 1445 */       if (SA[isa + s] == v) {
/* 1446 */         SA[--d] = s;
/* 1447 */         SA[isa + s] = d;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void trIntroSort(int isa, int isaD, int isaN, int first, int last, TRBudget budget, int size) {
/* 1454 */     int[] SA = this.SA;
/*      */     
/* 1456 */     StackEntry[] stack = new StackEntry[64];
/*      */ 
/*      */ 
/*      */     
/* 1460 */     int x = 0;
/*      */ 
/*      */ 
/*      */     
/* 1464 */     int ssize = 0, limit = trLog(last - first); label274: while (true) {
/* 1465 */       while (limit < 0) {
/* 1466 */         if (limit == -1) {
/* 1467 */           if (!budget.update(size, last - first)) {
/*      */             break label274;
/*      */           }
/* 1470 */           PartitionResult result = trPartition(isa, isaD - 1, isaN, first, last, last - 1);
/* 1471 */           int i = result.first;
/* 1472 */           int j = result.last;
/* 1473 */           if (first < i || j < last) {
/* 1474 */             if (i < last) {
/* 1475 */               for (int m = first, n = i - 1; m < i; m++) {
/* 1476 */                 SA[isa + SA[m]] = n;
/*      */               }
/*      */             }
/* 1479 */             if (j < last) {
/* 1480 */               for (int m = i, n = j - 1; m < j; m++) {
/* 1481 */                 SA[isa + SA[m]] = n;
/*      */               }
/*      */             }
/*      */             
/* 1485 */             stack[ssize++] = new StackEntry(0, i, j, 0);
/* 1486 */             stack[ssize++] = new StackEntry(isaD - 1, first, last, -2);
/* 1487 */             if (i - first <= last - j) {
/* 1488 */               if (1 < i - first) {
/* 1489 */                 stack[ssize++] = new StackEntry(isaD, j, last, trLog(last - j));
/* 1490 */                 last = i; limit = trLog(i - first); continue;
/* 1491 */               }  if (1 < last - j) {
/* 1492 */                 first = j; limit = trLog(last - j); continue;
/*      */               } 
/* 1494 */               if (ssize == 0) {
/*      */                 return;
/*      */               }
/* 1497 */               StackEntry stackEntry2 = stack[--ssize];
/* 1498 */               isaD = stackEntry2.a;
/* 1499 */               first = stackEntry2.b;
/* 1500 */               last = stackEntry2.c;
/* 1501 */               limit = stackEntry2.d;
/*      */               continue;
/*      */             } 
/* 1504 */             if (1 < last - j) {
/* 1505 */               stack[ssize++] = new StackEntry(isaD, first, i, trLog(i - first));
/* 1506 */               first = j;
/* 1507 */               limit = trLog(last - j); continue;
/* 1508 */             }  if (1 < i - first) {
/* 1509 */               last = i;
/* 1510 */               limit = trLog(i - first); continue;
/*      */             } 
/* 1512 */             if (ssize == 0) {
/*      */               return;
/*      */             }
/* 1515 */             StackEntry stackEntry1 = stack[--ssize];
/* 1516 */             isaD = stackEntry1.a;
/* 1517 */             first = stackEntry1.b;
/* 1518 */             last = stackEntry1.c;
/* 1519 */             limit = stackEntry1.d;
/*      */             
/*      */             continue;
/*      */           } 
/* 1523 */           for (int k = first; k < last; k++) {
/* 1524 */             SA[isa + SA[k]] = k;
/*      */           }
/* 1526 */           if (ssize == 0) {
/*      */             return;
/*      */           }
/* 1529 */           StackEntry stackEntry = stack[--ssize];
/* 1530 */           isaD = stackEntry.a;
/* 1531 */           first = stackEntry.b;
/* 1532 */           last = stackEntry.c;
/* 1533 */           limit = stackEntry.d; continue;
/*      */         } 
/* 1535 */         if (limit == -2) {
/* 1536 */           int i = (stack[--ssize]).b;
/* 1537 */           int j = (stack[ssize]).c;
/* 1538 */           trCopy(isa, isaN, first, i, j, last, isaD - isa);
/* 1539 */           if (ssize == 0) {
/*      */             return;
/*      */           }
/* 1542 */           StackEntry stackEntry = stack[--ssize];
/* 1543 */           isaD = stackEntry.a;
/* 1544 */           first = stackEntry.b;
/* 1545 */           last = stackEntry.c;
/* 1546 */           limit = stackEntry.d; continue;
/*      */         } 
/* 1548 */         if (0 <= SA[first]) {
/* 1549 */           int i = first;
/*      */           do {
/* 1551 */             SA[isa + SA[i]] = i;
/* 1552 */           } while (++i < last && 0 <= SA[i]);
/* 1553 */           first = i;
/*      */         } 
/* 1555 */         if (first < last) {
/* 1556 */           int next, i = first;
/*      */           while (true) {
/* 1558 */             SA[i] = SA[i] ^ 0xFFFFFFFF;
/* 1559 */             if (SA[++i] >= 0) {
/* 1560 */               next = (SA[isa + SA[i]] != SA[isaD + SA[i]]) ? trLog(i - first + 1) : -1;
/* 1561 */               if (++i < last) {
/* 1562 */                 for (int j = first, k = i - 1; j < i; j++) {
/* 1563 */                   SA[isa + SA[j]] = k;
/*      */                 }
/*      */               }
/*      */               
/* 1567 */               if (i - first <= last - i) {
/* 1568 */                 stack[ssize++] = new StackEntry(isaD, i, last, -3);
/* 1569 */                 isaD++; last = i; limit = next; continue;
/*      */               } 
/* 1571 */               if (1 < last - i)
/* 1572 */               { stack[ssize++] = new StackEntry(isaD + 1, first, i, next);
/* 1573 */                 first = i; limit = -3; continue; }  break;
/*      */             } 
/* 1575 */           }  isaD++; last = i; limit = next;
/*      */           
/*      */           continue;
/*      */         } 
/* 1579 */         if (ssize == 0) {
/*      */           return;
/*      */         }
/* 1582 */         StackEntry entry = stack[--ssize];
/* 1583 */         isaD = entry.a;
/* 1584 */         first = entry.b;
/* 1585 */         last = entry.c;
/* 1586 */         limit = entry.d;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1592 */       if (last - first <= 8) {
/* 1593 */         if (!budget.update(size, last - first)) {
/*      */           break;
/*      */         }
/* 1596 */         trInsertionSort(isa, isaD, isaN, first, last);
/* 1597 */         limit = -3;
/*      */         
/*      */         continue;
/*      */       } 
/* 1601 */       if (limit-- == 0) {
/* 1602 */         if (!budget.update(size, last - first)) {
/*      */           break;
/*      */         }
/* 1605 */         trHeapSort(isa, isaD, isaN, first, last - first); int i;
/* 1606 */         for (i = last - 1; first < i; i = j) {
/* 1607 */           x = trGetC(isa, isaD, isaN, SA[i]); int j = i - 1;
/* 1608 */           for (; first <= j && trGetC(isa, isaD, isaN, SA[j]) == x; 
/* 1609 */             j--) {
/* 1610 */             SA[j] = SA[j] ^ 0xFFFFFFFF;
/*      */           }
/*      */         } 
/* 1613 */         limit = -3;
/*      */         
/*      */         continue;
/*      */       } 
/* 1617 */       int a = trPivot(isa, isaD, isaN, first, last);
/*      */       
/* 1619 */       swapElements(SA, first, SA, a);
/* 1620 */       int v = trGetC(isa, isaD, isaN, SA[first]);
/*      */       
/* 1622 */       int b = first + 1;
/* 1623 */       while (b < last && (x = trGetC(isa, isaD, isaN, SA[b])) == v) {
/* 1624 */         b++;
/*      */       }
/* 1626 */       if ((a = b) < last && x < v) {
/* 1627 */         while (++b < last && (x = trGetC(isa, isaD, isaN, SA[b])) <= v) {
/* 1628 */           if (x == v) {
/* 1629 */             swapElements(SA, b, SA, a);
/* 1630 */             a++;
/*      */           } 
/*      */         } 
/*      */       }
/*      */       
/* 1635 */       int c = last - 1;
/* 1636 */       while (b < c && (x = trGetC(isa, isaD, isaN, SA[c])) == v)
/* 1637 */         c--; 
/*      */       int d;
/* 1639 */       if (b < (d = c) && x > v) {
/* 1640 */         while (b < --c && (x = trGetC(isa, isaD, isaN, SA[c])) >= v) {
/* 1641 */           if (x == v) {
/* 1642 */             swapElements(SA, c, SA, d);
/* 1643 */             d--;
/*      */           } 
/*      */         } 
/*      */       }
/* 1647 */       while (b < c) {
/* 1648 */         swapElements(SA, b, SA, c);
/* 1649 */         while (++b < c && (x = trGetC(isa, isaD, isaN, SA[b])) <= v) {
/* 1650 */           if (x == v) {
/* 1651 */             swapElements(SA, b, SA, a);
/* 1652 */             a++;
/*      */           } 
/*      */         } 
/* 1655 */         while (b < --c && (x = trGetC(isa, isaD, isaN, SA[c])) >= v) {
/* 1656 */           if (x == v) {
/* 1657 */             swapElements(SA, c, SA, d);
/* 1658 */             d--;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1663 */       if (a <= d) {
/* 1664 */         c = b - 1;
/*      */         int i, t;
/* 1666 */         if ((i = a - first) > (t = b - a))
/* 1667 */           i = t; 
/*      */         int e, f;
/* 1669 */         for (e = first, f = b - i; 0 < i; i--, e++, f++) {
/* 1670 */           swapElements(SA, e, SA, f);
/*      */         }
/* 1672 */         if ((i = d - c) > (t = last - d - 1)) {
/* 1673 */           i = t;
/*      */         }
/* 1675 */         for (e = b, f = last - i; 0 < i; i--, e++, f++) {
/* 1676 */           swapElements(SA, e, SA, f);
/*      */         }
/*      */         
/* 1679 */         a = first + b - a;
/* 1680 */         b = last - d - c;
/* 1681 */         int next = (SA[isa + SA[a]] != v) ? trLog(b - a) : -1;
/*      */         
/* 1683 */         for (c = first, v = a - 1; c < a; c++) {
/* 1684 */           SA[isa + SA[c]] = v;
/*      */         }
/* 1686 */         if (b < last) {
/* 1687 */           for (c = a, v = b - 1; c < b; c++) {
/* 1688 */             SA[isa + SA[c]] = v;
/*      */           }
/*      */         }
/* 1691 */         if (a - first <= last - b) {
/* 1692 */           if (last - b <= b - a) {
/* 1693 */             if (1 < a - first) {
/* 1694 */               stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
/* 1695 */               stack[ssize++] = new StackEntry(isaD, b, last, limit);
/* 1696 */               last = a; continue;
/* 1697 */             }  if (1 < last - b) {
/* 1698 */               stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
/* 1699 */               first = b; continue;
/* 1700 */             }  if (1 < b - a) {
/* 1701 */               isaD++;
/* 1702 */               first = a;
/* 1703 */               last = b;
/* 1704 */               limit = next; continue;
/*      */             } 
/* 1706 */             if (ssize == 0) {
/*      */               return;
/*      */             }
/* 1709 */             StackEntry entry = stack[--ssize];
/* 1710 */             isaD = entry.a;
/* 1711 */             first = entry.b;
/* 1712 */             last = entry.c;
/* 1713 */             limit = entry.d; continue;
/*      */           } 
/* 1715 */           if (a - first <= b - a) {
/* 1716 */             if (1 < a - first) {
/* 1717 */               stack[ssize++] = new StackEntry(isaD, b, last, limit);
/* 1718 */               stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
/* 1719 */               last = a; continue;
/* 1720 */             }  if (1 < b - a) {
/* 1721 */               stack[ssize++] = new StackEntry(isaD, b, last, limit);
/* 1722 */               isaD++;
/* 1723 */               first = a;
/* 1724 */               last = b;
/* 1725 */               limit = next; continue;
/*      */             } 
/* 1727 */             first = b;
/*      */             continue;
/*      */           } 
/* 1730 */           if (1 < b - a) {
/* 1731 */             stack[ssize++] = new StackEntry(isaD, b, last, limit);
/* 1732 */             stack[ssize++] = new StackEntry(isaD, first, a, limit);
/* 1733 */             isaD++;
/* 1734 */             first = a;
/* 1735 */             last = b;
/* 1736 */             limit = next; continue;
/*      */           } 
/* 1738 */           stack[ssize++] = new StackEntry(isaD, b, last, limit);
/* 1739 */           last = a;
/*      */           
/*      */           continue;
/*      */         } 
/* 1743 */         if (a - first <= b - a) {
/* 1744 */           if (1 < last - b) {
/* 1745 */             stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
/* 1746 */             stack[ssize++] = new StackEntry(isaD, first, a, limit);
/* 1747 */             first = b; continue;
/* 1748 */           }  if (1 < a - first) {
/* 1749 */             stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
/* 1750 */             last = a; continue;
/* 1751 */           }  if (1 < b - a) {
/* 1752 */             isaD++;
/* 1753 */             first = a;
/* 1754 */             last = b;
/* 1755 */             limit = next; continue;
/*      */           } 
/* 1757 */           stack[ssize++] = new StackEntry(isaD, first, last, limit); continue;
/*      */         } 
/* 1759 */         if (last - b <= b - a) {
/* 1760 */           if (1 < last - b) {
/* 1761 */             stack[ssize++] = new StackEntry(isaD, first, a, limit);
/* 1762 */             stack[ssize++] = new StackEntry(isaD + 1, a, b, next);
/* 1763 */             first = b; continue;
/* 1764 */           }  if (1 < b - a) {
/* 1765 */             stack[ssize++] = new StackEntry(isaD, first, a, limit);
/* 1766 */             isaD++;
/* 1767 */             first = a;
/* 1768 */             last = b;
/* 1769 */             limit = next; continue;
/*      */           } 
/* 1771 */           last = a;
/*      */           continue;
/*      */         } 
/* 1774 */         if (1 < b - a) {
/* 1775 */           stack[ssize++] = new StackEntry(isaD, first, a, limit);
/* 1776 */           stack[ssize++] = new StackEntry(isaD, b, last, limit);
/* 1777 */           isaD++;
/* 1778 */           first = a;
/* 1779 */           last = b;
/* 1780 */           limit = next; continue;
/*      */         } 
/* 1782 */         stack[ssize++] = new StackEntry(isaD, first, a, limit);
/* 1783 */         first = b;
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 1788 */       if (!budget.update(size, last - first)) {
/*      */         break;
/*      */       }
/* 1791 */       limit++; isaD++;
/*      */     } 
/*      */ 
/*      */     
/* 1795 */     for (int s = 0; s < ssize; s++) {
/* 1796 */       if ((stack[s]).d == -3)
/* 1797 */         lsUpdateGroup(isa, (stack[s]).b, (stack[s]).c); 
/*      */     } 
/*      */   }
/*      */   
/*      */   private static class TRBudget
/*      */   {
/*      */     int budget;
/*      */     int chance;
/*      */     
/*      */     TRBudget(int budget, int chance) {
/* 1807 */       this.budget = budget;
/* 1808 */       this.chance = chance;
/*      */     }
/*      */     
/*      */     boolean update(int size, int n) {
/* 1812 */       this.budget -= n;
/* 1813 */       if (this.budget <= 0) {
/* 1814 */         if (--this.chance == 0) {
/* 1815 */           return false;
/*      */         }
/* 1817 */         this.budget += size;
/*      */       } 
/* 1819 */       return true;
/*      */     }
/*      */   }
/*      */   
/*      */   private void trSort(int isa, int n, int depth) {
/* 1824 */     int[] SA = this.SA;
/*      */     
/* 1826 */     int first = 0;
/*      */ 
/*      */     
/* 1829 */     if (-n < SA[0]) {
/* 1830 */       TRBudget budget = new TRBudget(n, trLog(n) * 2 / 3 + 1); do {
/*      */         int t;
/* 1832 */         if ((t = SA[first]) < 0) {
/* 1833 */           first -= t;
/*      */         } else {
/* 1835 */           int last = SA[isa + t] + 1;
/* 1836 */           if (1 < last - first) {
/* 1837 */             trIntroSort(isa, isa + depth, isa + n, first, last, budget, n);
/* 1838 */             if (budget.chance == 0) {
/*      */               
/* 1840 */               if (0 < first) {
/* 1841 */                 SA[0] = -first;
/*      */               }
/* 1843 */               lsSort(isa, n, depth);
/*      */               break;
/*      */             } 
/*      */           } 
/* 1847 */           first = last;
/*      */         } 
/* 1849 */       } while (first < n);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private static int BUCKET_B(int c0, int c1) {
/* 1856 */     return c1 << 8 | c0;
/*      */   }
/*      */   
/*      */   private static int BUCKET_BSTAR(int c0, int c1) {
/* 1860 */     return c0 << 8 | c1;
/*      */   }
/*      */   
/*      */   private int sortTypeBstar(int[] bucketA, int[] bucketB) {
/* 1864 */     byte[] T = this.T;
/* 1865 */     int[] SA = this.SA;
/* 1866 */     int n = this.n;
/* 1867 */     int[] tempbuf = new int[256];
/*      */ 
/*      */ 
/*      */     
/*      */     int i, flag;
/*      */ 
/*      */ 
/*      */     
/* 1875 */     for (i = 1, flag = 1; i < n; i++) {
/* 1876 */       if (T[i - 1] != T[i]) {
/* 1877 */         if ((T[i - 1] & 0xFF) > (T[i] & 0xFF)) {
/* 1878 */           flag = 0;
/*      */         }
/*      */         break;
/*      */       } 
/*      */     } 
/* 1883 */     i = n - 1;
/* 1884 */     int m = n;
/*      */     
/*      */     int ti, t0;
/* 1887 */     if ((ti = T[i] & 0xFF) < (t0 = T[0] & 0xFF) || (T[i] == T[0] && flag != 0)) {
/* 1888 */       if (flag == 0) {
/* 1889 */         bucketB[BUCKET_BSTAR(ti, t0)] = bucketB[BUCKET_BSTAR(ti, t0)] + 1;
/* 1890 */         SA[--m] = i;
/*      */       } else {
/* 1892 */         bucketB[BUCKET_B(ti, t0)] = bucketB[BUCKET_B(ti, t0)] + 1;
/*      */       }  int ti1;
/* 1894 */       for (; 0 <= --i && (ti = T[i] & 0xFF) <= (ti1 = T[i + 1] & 0xFF); i--) {
/* 1895 */         bucketB[BUCKET_B(ti, ti1)] = bucketB[BUCKET_B(ti, ti1)] + 1;
/*      */       }
/*      */     } 
/*      */     
/* 1899 */     while (0 <= i) {
/*      */       do {
/* 1901 */         bucketA[T[i] & 0xFF] = bucketA[T[i] & 0xFF] + 1;
/* 1902 */       } while (0 <= --i && (T[i] & 0xFF) >= (T[i + 1] & 0xFF));
/* 1903 */       if (0 <= i) {
/* 1904 */         bucketB[BUCKET_BSTAR(T[i] & 0xFF, T[i + 1] & 0xFF)] = bucketB[BUCKET_BSTAR(T[i] & 0xFF, T[i + 1] & 0xFF)] + 1;
/* 1905 */         SA[--m] = i; int ti1;
/* 1906 */         for (; 0 <= --i && (ti = T[i] & 0xFF) <= (ti1 = T[i + 1] & 0xFF); i--) {
/* 1907 */           bucketB[BUCKET_B(ti, ti1)] = bucketB[BUCKET_B(ti, ti1)] + 1;
/*      */         }
/*      */       } 
/*      */     } 
/* 1911 */     m = n - m;
/* 1912 */     if (m == 0) {
/* 1913 */       for (i = 0; i < n; i++) {
/* 1914 */         SA[i] = i;
/*      */       }
/* 1916 */       return 0;
/*      */     } 
/*      */     int j, c0;
/* 1919 */     for (c0 = 0, i = -1, j = 0; c0 < 256; c0++) {
/* 1920 */       int i1 = i + bucketA[c0];
/* 1921 */       bucketA[c0] = i + j;
/* 1922 */       i = i1 + bucketB[BUCKET_B(c0, c0)];
/* 1923 */       for (int i2 = c0 + 1; i2 < 256; i2++) {
/* 1924 */         j += bucketB[BUCKET_BSTAR(c0, i2)];
/* 1925 */         bucketB[c0 << 8 | i2] = j;
/* 1926 */         i += bucketB[BUCKET_B(c0, i2)];
/*      */       } 
/*      */     } 
/*      */     
/* 1930 */     int PAb = n - m;
/* 1931 */     int ISAb = m;
/* 1932 */     for (i = m - 2; 0 <= i; i--) {
/* 1933 */       int i1 = SA[PAb + i];
/* 1934 */       c0 = T[i1] & 0xFF;
/* 1935 */       int i2 = T[i1 + 1] & 0xFF;
/* 1936 */       bucketB[BUCKET_BSTAR(c0, i2)] = bucketB[BUCKET_BSTAR(c0, i2)] - 1; SA[bucketB[BUCKET_BSTAR(c0, i2)] - 1] = i;
/*      */     } 
/* 1938 */     int t = SA[PAb + m - 1];
/* 1939 */     c0 = T[t] & 0xFF;
/* 1940 */     int c1 = T[t + 1] & 0xFF;
/* 1941 */     bucketB[BUCKET_BSTAR(c0, c1)] = bucketB[BUCKET_BSTAR(c0, c1)] - 1; SA[bucketB[BUCKET_BSTAR(c0, c1)] - 1] = m - 1;
/*      */     
/* 1943 */     int[] buf = SA;
/* 1944 */     int bufoffset = m;
/* 1945 */     int bufsize = n - 2 * m;
/* 1946 */     if (bufsize <= 256) {
/* 1947 */       buf = tempbuf;
/* 1948 */       bufoffset = 0;
/* 1949 */       bufsize = 256;
/*      */     } 
/*      */     
/* 1952 */     for (c0 = 255, j = m; 0 < j; c0--) {
/* 1953 */       for (c1 = 255; c0 < c1; j = i, c1--) {
/* 1954 */         i = bucketB[BUCKET_BSTAR(c0, c1)];
/* 1955 */         if (1 < j - i) {
/* 1956 */           subStringSort(PAb, i, j, buf, bufoffset, bufsize, 2, (SA[i] == m - 1), n);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 1961 */     for (i = m - 1; 0 <= i; i--) {
/* 1962 */       if (0 <= SA[i]) {
/* 1963 */         j = i;
/*      */         do {
/* 1965 */           SA[ISAb + SA[i]] = i;
/* 1966 */         } while (0 <= --i && 0 <= SA[i]);
/* 1967 */         SA[i + 1] = i - j;
/* 1968 */         if (i <= 0) {
/*      */           break;
/*      */         }
/*      */       } 
/* 1972 */       j = i;
/*      */       while (true) {
/* 1974 */         SA[i] = SA[i] ^ 0xFFFFFFFF; SA[ISAb + (SA[i] ^ 0xFFFFFFFF)] = j;
/* 1975 */         if (SA[--i] >= 0) {
/* 1976 */           SA[ISAb + SA[i]] = j; break;
/*      */         } 
/*      */       } 
/* 1979 */     }  trSort(ISAb, m, 1);
/*      */     
/* 1981 */     i = n - 1; j = m;
/* 1982 */     if ((T[i] & 0xFF) < (T[0] & 0xFF) || (T[i] == T[0] && flag != 0)) {
/* 1983 */       if (flag == 0) {
/* 1984 */         SA[SA[ISAb + --j]] = i;
/*      */       }
/* 1986 */       while (0 <= --i && (T[i] & 0xFF) <= (T[i + 1] & 0xFF)) {
/* 1987 */         i--;
/*      */       }
/*      */     } 
/* 1990 */     while (0 <= i) {
/* 1991 */       while (0 <= --i && (T[i] & 0xFF) >= (T[i + 1] & 0xFF)) {
/* 1992 */         i--;
/*      */       }
/* 1994 */       if (0 <= i) {
/* 1995 */         SA[SA[ISAb + --j]] = i;
/* 1996 */         while (0 <= --i && (T[i] & 0xFF) <= (T[i + 1] & 0xFF)) {
/* 1997 */           i--;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     int k;
/* 2002 */     for (c0 = 255, i = n - 1, k = m - 1; 0 <= c0; c0--) {
/* 2003 */       for (c1 = 255; c0 < c1; c1--) {
/* 2004 */         t = i - bucketB[BUCKET_B(c0, c1)];
/* 2005 */         bucketB[BUCKET_B(c0, c1)] = i + 1;
/*      */         
/* 2007 */         for (i = t, j = bucketB[BUCKET_BSTAR(c0, c1)]; j <= k; i--, k--) {
/* 2008 */           SA[i] = SA[k];
/*      */         }
/*      */       } 
/* 2011 */       t = i - bucketB[BUCKET_B(c0, c0)];
/* 2012 */       bucketB[BUCKET_B(c0, c0)] = i + 1;
/* 2013 */       if (c0 < 255) {
/* 2014 */         bucketB[BUCKET_BSTAR(c0, c0 + 1)] = t + 1;
/*      */       }
/* 2016 */       i = bucketA[c0];
/*      */     } 
/* 2018 */     return m;
/*      */   }
/*      */   
/*      */   private int constructBWT(int[] bucketA, int[] bucketB) {
/* 2022 */     byte[] T = this.T;
/* 2023 */     int[] SA = this.SA;
/* 2024 */     int n = this.n;
/*      */     
/* 2026 */     int t = 0;
/*      */     
/* 2028 */     int c2 = 0;
/* 2029 */     int orig = -1;
/*      */     
/* 2031 */     for (int c1 = 254; 0 <= c1; c1--) {
/* 2032 */       int k = bucketB[BUCKET_BSTAR(c1, c1 + 1)], j = bucketA[c1 + 1]; t = 0; c2 = -1;
/* 2033 */       for (; k <= j; 
/* 2034 */         j--) {
/* 2035 */         int s; int s1; if (0 <= (s1 = s = SA[j])) {
/* 2036 */           if (--s < 0)
/* 2037 */             s = n - 1; 
/*      */           int c0;
/* 2039 */           if ((c0 = T[s] & 0xFF) <= c1) {
/* 2040 */             SA[j] = s1 ^ 0xFFFFFFFF;
/* 2041 */             if (0 < s && (T[s - 1] & 0xFF) > c0) {
/* 2042 */               s ^= 0xFFFFFFFF;
/*      */             }
/* 2044 */             if (c2 == c0) {
/* 2045 */               SA[--t] = s;
/*      */             } else {
/* 2047 */               if (0 <= c2) {
/* 2048 */                 bucketB[BUCKET_B(c2, c1)] = t;
/*      */               }
/* 2050 */               SA[t = bucketB[BUCKET_B(c2 = c0, c1)] - 1] = s;
/*      */             } 
/*      */           } 
/*      */         } else {
/* 2054 */           SA[j] = s ^ 0xFFFFFFFF;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 2059 */     for (int i = 0; i < n; i++) {
/* 2060 */       int s; int s1; if (0 <= (s1 = s = SA[i])) {
/* 2061 */         if (--s < 0)
/* 2062 */           s = n - 1; 
/*      */         int c0;
/* 2064 */         if ((c0 = T[s] & 0xFF) >= (T[s + 1] & 0xFF)) {
/* 2065 */           if (0 < s && (T[s - 1] & 0xFF) < c0) {
/* 2066 */             s ^= 0xFFFFFFFF;
/*      */           }
/* 2068 */           if (c0 == c2) {
/* 2069 */             SA[++t] = s;
/*      */           } else {
/* 2071 */             if (c2 != -1) {
/* 2072 */               bucketA[c2] = t;
/*      */             }
/* 2074 */             SA[t = bucketA[c2 = c0] + 1] = s;
/*      */           } 
/*      */         } 
/*      */       } else {
/* 2078 */         s1 ^= 0xFFFFFFFF;
/*      */       } 
/*      */       
/* 2081 */       if (s1 == 0) {
/* 2082 */         SA[i] = T[n - 1];
/* 2083 */         orig = i;
/*      */       } else {
/* 2085 */         SA[i] = T[s1 - 1];
/*      */       } 
/*      */     } 
/* 2088 */     return orig;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int bwt() {
/* 2096 */     int[] SA = this.SA;
/* 2097 */     byte[] T = this.T;
/* 2098 */     int n = this.n;
/*      */     
/* 2100 */     int[] bucketA = new int[256];
/* 2101 */     int[] bucketB = new int[65536];
/*      */     
/* 2103 */     if (n == 0) {
/* 2104 */       return 0;
/*      */     }
/* 2106 */     if (n == 1) {
/* 2107 */       SA[0] = T[0];
/* 2108 */       return 0;
/*      */     } 
/*      */     
/* 2111 */     int m = sortTypeBstar(bucketA, bucketB);
/* 2112 */     if (0 < m) {
/* 2113 */       return constructBWT(bucketA, bucketB);
/*      */     }
/* 2115 */     return 0;
/*      */   }
/*      */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\compression\Bzip2DivSufSort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */