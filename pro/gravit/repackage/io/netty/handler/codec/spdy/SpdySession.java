/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.TreeMap;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelPromise;
/*     */ import pro.gravit.repackage.io.netty.util.internal.PlatformDependent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class SpdySession
/*     */ {
/*  32 */   private final AtomicInteger activeLocalStreams = new AtomicInteger();
/*  33 */   private final AtomicInteger activeRemoteStreams = new AtomicInteger();
/*  34 */   private final Map<Integer, StreamState> activeStreams = PlatformDependent.newConcurrentHashMap();
/*  35 */   private final StreamComparator streamComparator = new StreamComparator();
/*     */   private final AtomicInteger sendWindowSize;
/*     */   private final AtomicInteger receiveWindowSize;
/*     */   
/*     */   SpdySession(int sendWindowSize, int receiveWindowSize) {
/*  40 */     this.sendWindowSize = new AtomicInteger(sendWindowSize);
/*  41 */     this.receiveWindowSize = new AtomicInteger(receiveWindowSize);
/*     */   }
/*     */   
/*     */   int numActiveStreams(boolean remote) {
/*  45 */     if (remote) {
/*  46 */       return this.activeRemoteStreams.get();
/*     */     }
/*  48 */     return this.activeLocalStreams.get();
/*     */   }
/*     */ 
/*     */   
/*     */   boolean noActiveStreams() {
/*  53 */     return this.activeStreams.isEmpty();
/*     */   }
/*     */   
/*     */   boolean isActiveStream(int streamId) {
/*  57 */     return this.activeStreams.containsKey(Integer.valueOf(streamId));
/*     */   }
/*     */ 
/*     */   
/*     */   Map<Integer, StreamState> activeStreams() {
/*  62 */     Map<Integer, StreamState> streams = new TreeMap<Integer, StreamState>(this.streamComparator);
/*  63 */     streams.putAll(this.activeStreams);
/*  64 */     return streams;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void acceptStream(int streamId, byte priority, boolean remoteSideClosed, boolean localSideClosed, int sendWindowSize, int receiveWindowSize, boolean remote) {
/*  70 */     if (!remoteSideClosed || !localSideClosed) {
/*  71 */       StreamState state = this.activeStreams.put(Integer.valueOf(streamId), new StreamState(priority, remoteSideClosed, localSideClosed, sendWindowSize, receiveWindowSize));
/*     */       
/*  73 */       if (state == null) {
/*  74 */         if (remote) {
/*  75 */           this.activeRemoteStreams.incrementAndGet();
/*     */         } else {
/*  77 */           this.activeLocalStreams.incrementAndGet();
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private StreamState removeActiveStream(int streamId, boolean remote) {
/*  84 */     StreamState state = this.activeStreams.remove(Integer.valueOf(streamId));
/*  85 */     if (state != null) {
/*  86 */       if (remote) {
/*  87 */         this.activeRemoteStreams.decrementAndGet();
/*     */       } else {
/*  89 */         this.activeLocalStreams.decrementAndGet();
/*     */       } 
/*     */     }
/*  92 */     return state;
/*     */   }
/*     */   
/*     */   void removeStream(int streamId, Throwable cause, boolean remote) {
/*  96 */     StreamState state = removeActiveStream(streamId, remote);
/*  97 */     if (state != null) {
/*  98 */       state.clearPendingWrites(cause);
/*     */     }
/*     */   }
/*     */   
/*     */   boolean isRemoteSideClosed(int streamId) {
/* 103 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 104 */     return (state == null || state.isRemoteSideClosed());
/*     */   }
/*     */   
/*     */   void closeRemoteSide(int streamId, boolean remote) {
/* 108 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 109 */     if (state != null) {
/* 110 */       state.closeRemoteSide();
/* 111 */       if (state.isLocalSideClosed()) {
/* 112 */         removeActiveStream(streamId, remote);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   boolean isLocalSideClosed(int streamId) {
/* 118 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 119 */     return (state == null || state.isLocalSideClosed());
/*     */   }
/*     */   
/*     */   void closeLocalSide(int streamId, boolean remote) {
/* 123 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 124 */     if (state != null) {
/* 125 */       state.closeLocalSide();
/* 126 */       if (state.isRemoteSideClosed()) {
/* 127 */         removeActiveStream(streamId, remote);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   boolean hasReceivedReply(int streamId) {
/* 137 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 138 */     return (state != null && state.hasReceivedReply());
/*     */   }
/*     */   
/*     */   void receivedReply(int streamId) {
/* 142 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 143 */     if (state != null) {
/* 144 */       state.receivedReply();
/*     */     }
/*     */   }
/*     */   
/*     */   int getSendWindowSize(int streamId) {
/* 149 */     if (streamId == 0) {
/* 150 */       return this.sendWindowSize.get();
/*     */     }
/*     */     
/* 153 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 154 */     return (state != null) ? state.getSendWindowSize() : -1;
/*     */   }
/*     */   
/*     */   int updateSendWindowSize(int streamId, int deltaWindowSize) {
/* 158 */     if (streamId == 0) {
/* 159 */       return this.sendWindowSize.addAndGet(deltaWindowSize);
/*     */     }
/*     */     
/* 162 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 163 */     return (state != null) ? state.updateSendWindowSize(deltaWindowSize) : -1;
/*     */   }
/*     */   
/*     */   int updateReceiveWindowSize(int streamId, int deltaWindowSize) {
/* 167 */     if (streamId == 0) {
/* 168 */       return this.receiveWindowSize.addAndGet(deltaWindowSize);
/*     */     }
/*     */     
/* 171 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 172 */     if (state == null) {
/* 173 */       return -1;
/*     */     }
/* 175 */     if (deltaWindowSize > 0) {
/* 176 */       state.setReceiveWindowSizeLowerBound(0);
/*     */     }
/* 178 */     return state.updateReceiveWindowSize(deltaWindowSize);
/*     */   }
/*     */   
/*     */   int getReceiveWindowSizeLowerBound(int streamId) {
/* 182 */     if (streamId == 0) {
/* 183 */       return 0;
/*     */     }
/*     */     
/* 186 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 187 */     return (state != null) ? state.getReceiveWindowSizeLowerBound() : 0;
/*     */   }
/*     */   
/*     */   void updateAllSendWindowSizes(int deltaWindowSize) {
/* 191 */     for (StreamState state : this.activeStreams.values()) {
/* 192 */       state.updateSendWindowSize(deltaWindowSize);
/*     */     }
/*     */   }
/*     */   
/*     */   void updateAllReceiveWindowSizes(int deltaWindowSize) {
/* 197 */     for (StreamState state : this.activeStreams.values()) {
/* 198 */       state.updateReceiveWindowSize(deltaWindowSize);
/* 199 */       if (deltaWindowSize < 0) {
/* 200 */         state.setReceiveWindowSizeLowerBound(deltaWindowSize);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   boolean putPendingWrite(int streamId, PendingWrite pendingWrite) {
/* 206 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 207 */     return (state != null && state.putPendingWrite(pendingWrite));
/*     */   }
/*     */   
/*     */   PendingWrite getPendingWrite(int streamId) {
/* 211 */     if (streamId == 0) {
/* 212 */       for (Map.Entry<Integer, StreamState> e : activeStreams().entrySet()) {
/* 213 */         StreamState streamState = e.getValue();
/* 214 */         if (streamState.getSendWindowSize() > 0) {
/* 215 */           PendingWrite pendingWrite = streamState.getPendingWrite();
/* 216 */           if (pendingWrite != null) {
/* 217 */             return pendingWrite;
/*     */           }
/*     */         } 
/*     */       } 
/* 221 */       return null;
/*     */     } 
/*     */     
/* 224 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 225 */     return (state != null) ? state.getPendingWrite() : null;
/*     */   }
/*     */   
/*     */   PendingWrite removePendingWrite(int streamId) {
/* 229 */     StreamState state = this.activeStreams.get(Integer.valueOf(streamId));
/* 230 */     return (state != null) ? state.removePendingWrite() : null;
/*     */   }
/*     */   
/*     */   private static final class StreamState
/*     */   {
/*     */     private final byte priority;
/*     */     private boolean remoteSideClosed;
/*     */     private boolean localSideClosed;
/*     */     private boolean receivedReply;
/*     */     private final AtomicInteger sendWindowSize;
/*     */     private final AtomicInteger receiveWindowSize;
/*     */     private int receiveWindowSizeLowerBound;
/* 242 */     private final Queue<SpdySession.PendingWrite> pendingWriteQueue = new ConcurrentLinkedQueue<SpdySession.PendingWrite>();
/*     */ 
/*     */ 
/*     */     
/*     */     StreamState(byte priority, boolean remoteSideClosed, boolean localSideClosed, int sendWindowSize, int receiveWindowSize) {
/* 247 */       this.priority = priority;
/* 248 */       this.remoteSideClosed = remoteSideClosed;
/* 249 */       this.localSideClosed = localSideClosed;
/* 250 */       this.sendWindowSize = new AtomicInteger(sendWindowSize);
/* 251 */       this.receiveWindowSize = new AtomicInteger(receiveWindowSize);
/*     */     }
/*     */     
/*     */     byte getPriority() {
/* 255 */       return this.priority;
/*     */     }
/*     */     
/*     */     boolean isRemoteSideClosed() {
/* 259 */       return this.remoteSideClosed;
/*     */     }
/*     */     
/*     */     void closeRemoteSide() {
/* 263 */       this.remoteSideClosed = true;
/*     */     }
/*     */     
/*     */     boolean isLocalSideClosed() {
/* 267 */       return this.localSideClosed;
/*     */     }
/*     */     
/*     */     void closeLocalSide() {
/* 271 */       this.localSideClosed = true;
/*     */     }
/*     */     
/*     */     boolean hasReceivedReply() {
/* 275 */       return this.receivedReply;
/*     */     }
/*     */     
/*     */     void receivedReply() {
/* 279 */       this.receivedReply = true;
/*     */     }
/*     */     
/*     */     int getSendWindowSize() {
/* 283 */       return this.sendWindowSize.get();
/*     */     }
/*     */     
/*     */     int updateSendWindowSize(int deltaWindowSize) {
/* 287 */       return this.sendWindowSize.addAndGet(deltaWindowSize);
/*     */     }
/*     */     
/*     */     int updateReceiveWindowSize(int deltaWindowSize) {
/* 291 */       return this.receiveWindowSize.addAndGet(deltaWindowSize);
/*     */     }
/*     */     
/*     */     int getReceiveWindowSizeLowerBound() {
/* 295 */       return this.receiveWindowSizeLowerBound;
/*     */     }
/*     */     
/*     */     void setReceiveWindowSizeLowerBound(int receiveWindowSizeLowerBound) {
/* 299 */       this.receiveWindowSizeLowerBound = receiveWindowSizeLowerBound;
/*     */     }
/*     */     
/*     */     boolean putPendingWrite(SpdySession.PendingWrite msg) {
/* 303 */       return this.pendingWriteQueue.offer(msg);
/*     */     }
/*     */     
/*     */     SpdySession.PendingWrite getPendingWrite() {
/* 307 */       return this.pendingWriteQueue.peek();
/*     */     }
/*     */     
/*     */     SpdySession.PendingWrite removePendingWrite() {
/* 311 */       return this.pendingWriteQueue.poll();
/*     */     }
/*     */     
/*     */     void clearPendingWrites(Throwable cause) {
/*     */       while (true) {
/* 316 */         SpdySession.PendingWrite pendingWrite = this.pendingWriteQueue.poll();
/* 317 */         if (pendingWrite == null) {
/*     */           break;
/*     */         }
/* 320 */         pendingWrite.fail(cause);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final class StreamComparator
/*     */     implements Comparator<Integer>
/*     */   {
/*     */     public int compare(Integer id1, Integer id2) {
/* 331 */       SpdySession.StreamState state1 = (SpdySession.StreamState)SpdySession.this.activeStreams.get(id1);
/* 332 */       SpdySession.StreamState state2 = (SpdySession.StreamState)SpdySession.this.activeStreams.get(id2);
/*     */       
/* 334 */       int result = state1.getPriority() - state2.getPriority();
/* 335 */       if (result != 0) {
/* 336 */         return result;
/*     */       }
/*     */       
/* 339 */       return id1.intValue() - id2.intValue();
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class PendingWrite {
/*     */     final SpdyDataFrame spdyDataFrame;
/*     */     final ChannelPromise promise;
/*     */     
/*     */     PendingWrite(SpdyDataFrame spdyDataFrame, ChannelPromise promise) {
/* 348 */       this.spdyDataFrame = spdyDataFrame;
/* 349 */       this.promise = promise;
/*     */     }
/*     */     
/*     */     void fail(Throwable cause) {
/* 353 */       this.spdyDataFrame.release();
/* 354 */       this.promise.setFailure(cause);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\SpdySession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */