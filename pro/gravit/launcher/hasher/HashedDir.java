/*     */ package pro.gravit.launcher.hasher;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.file.FileVisitResult;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.SimpleFileVisitor;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.util.Collections;
/*     */ import java.util.Deque;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import pro.gravit.launcher.LauncherAPI;
/*     */ import pro.gravit.launcher.LauncherNetworkAPI;
/*     */ import pro.gravit.launcher.serialize.HInput;
/*     */ import pro.gravit.launcher.serialize.HOutput;
/*     */ import pro.gravit.launcher.serialize.stream.EnumSerializer;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ import pro.gravit.utils.helper.VerifyHelper;
/*     */ 
/*     */ public final class HashedDir
/*     */   extends HashedEntry
/*     */ {
/*     */   public static final class Diff {
/*     */     @LauncherAPI
/*     */     public final HashedDir mismatch;
/*     */     @LauncherAPI
/*     */     public final HashedDir extra;
/*     */     
/*     */     private Diff(HashedDir mismatch, HashedDir extra) {
/*  34 */       this.mismatch = mismatch;
/*  35 */       this.extra = extra;
/*     */     }
/*     */     
/*     */     @LauncherAPI
/*     */     public boolean isSame() {
/*  40 */       return (this.mismatch.isEmpty() && this.extra.isEmpty());
/*     */     }
/*     */   }
/*     */   
/*     */   private final class HashFileVisitor
/*     */     extends SimpleFileVisitor<Path>
/*     */   {
/*     */     private final Path dir;
/*     */     private final FileNameMatcher matcher;
/*     */     private final boolean allowSymlinks;
/*     */     private final boolean digest;
/*  51 */     private HashedDir current = HashedDir.this;
/*  52 */     private final Deque<String> path = new LinkedList<>();
/*  53 */     private final Deque<HashedDir> stack = new LinkedList<>();
/*     */     
/*     */     private HashFileVisitor(Path dir, FileNameMatcher matcher, boolean allowSymlinks, boolean digest) {
/*  56 */       this.dir = dir;
/*  57 */       this.matcher = matcher;
/*  58 */       this.allowSymlinks = allowSymlinks;
/*  59 */       this.digest = digest;
/*     */     }
/*     */ 
/*     */     
/*     */     public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
/*  64 */       FileVisitResult result = super.postVisitDirectory(dir, exc);
/*  65 */       if (this.dir.equals(dir)) {
/*  66 */         return result;
/*     */       }
/*     */       
/*  69 */       HashedDir parent = this.stack.removeLast();
/*  70 */       parent.map.put(this.path.removeLast(), this.current);
/*  71 */       this.current = parent;
/*     */ 
/*     */       
/*  74 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
/*  79 */       FileVisitResult result = super.preVisitDirectory(dir, attrs);
/*  80 */       if (this.dir.equals(dir)) {
/*  81 */         return result;
/*     */       }
/*     */ 
/*     */       
/*  85 */       if (!this.allowSymlinks && attrs.isSymbolicLink()) {
/*  86 */         throw new SecurityException("Symlinks are not allowed");
/*     */       }
/*     */       
/*  89 */       this.stack.add(this.current);
/*  90 */       this.current = new HashedDir();
/*  91 */       this.path.add(IOHelper.getFileName(dir));
/*     */ 
/*     */       
/*  94 */       return result;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
/* 100 */       if (!this.allowSymlinks && attrs.isSymbolicLink()) {
/* 101 */         throw new SecurityException("Symlinks are not allowed");
/*     */       }
/*     */       
/* 104 */       this.path.add(IOHelper.getFileName(file));
/* 105 */       boolean doDigest = (this.digest && (this.matcher == null || this.matcher.shouldUpdate(this.path)));
/* 106 */       this.current.map.put(this.path.removeLast(), new HashedFile(file, attrs.size(), doDigest));
/* 107 */       return super.visitFile(file, attrs);
/*     */     }
/*     */   }
/*     */   @LauncherNetworkAPI
/* 111 */   private final Map<String, HashedEntry> map = new HashMap<>(32);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public HashedDir(HInput input) throws IOException {
/* 120 */     int entriesCount = input.readLength(0);
/* 121 */     for (int i = 0; i < entriesCount; i++) {
/* 122 */       HashedEntry entry; String name = IOHelper.verifyFileName(input.readString(255));
/*     */ 
/*     */ 
/*     */       
/* 126 */       HashedEntry.Type type = HashedEntry.Type.read(input);
/* 127 */       switch (type) {
/*     */         case FILE:
/* 129 */           entry = new HashedFile(input);
/*     */           break;
/*     */         case DIR:
/* 132 */           entry = new HashedDir(input);
/*     */           break;
/*     */         default:
/* 135 */           throw new AssertionError("Unsupported hashed entry type: " + type.name());
/*     */       } 
/*     */ 
/*     */       
/* 139 */       VerifyHelper.putIfAbsent(this.map, name, entry, String.format("Duplicate dir entry: '%s'", new Object[] { name }));
/*     */     } 
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public HashedDir(Path dir, FileNameMatcher matcher, boolean allowSymlinks, boolean digest) throws IOException {
/* 145 */     IOHelper.walk(dir, new HashFileVisitor(dir, matcher, allowSymlinks, digest), true);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public Diff diff(HashedDir other, FileNameMatcher matcher) {
/* 150 */     HashedDir mismatch = sideDiff(other, matcher, new LinkedList<>(), true);
/* 151 */     HashedDir extra = other.sideDiff(this, matcher, new LinkedList<>(), false);
/* 152 */     return new Diff(mismatch, extra);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public Diff compare(HashedDir other, FileNameMatcher matcher) {
/* 157 */     HashedDir mismatch = sideDiff(other, matcher, new LinkedList<>(), true);
/* 158 */     HashedDir extra = other.sideDiff(this, matcher, new LinkedList<>(), false);
/* 159 */     return new Diff(mismatch, extra);
/*     */   }
/*     */   
/*     */   public void remove(String name) {
/* 163 */     this.map.remove(name);
/*     */   }
/*     */   
/*     */   public void removeR(String name) {
/* 167 */     LinkedList<String> dirs = new LinkedList<>();
/* 168 */     StringTokenizer t = new StringTokenizer(name, "/");
/* 169 */     while (t.hasMoreTokens()) {
/* 170 */       dirs.add(t.nextToken());
/*     */     }
/* 172 */     Map<String, HashedEntry> current = this.map;
/* 173 */     for (String s : dirs) {
/* 174 */       HashedEntry e = current.get(s);
/* 175 */       if (e == null) {
/* 176 */         if (LogHelper.isDebugEnabled()) {
/* 177 */           LogHelper.debug("Null %s", new Object[] { s });
/*     */         }
/* 179 */         if (LogHelper.isDebugEnabled())
/* 180 */           for (String x : current.keySet()) { LogHelper.debug("Contains %s", new Object[] { x }); }
/*     */            
/*     */         break;
/*     */       } 
/* 184 */       if (e.getType() == HashedEntry.Type.DIR) {
/* 185 */         current = ((HashedDir)e).map;
/* 186 */         if (LogHelper.isDebugEnabled())
/* 187 */           LogHelper.debug("Found dir %s", new Object[] { s }); 
/*     */         continue;
/*     */       } 
/* 190 */       current.remove(s);
/* 191 */       if (LogHelper.isDebugEnabled()) {
/* 192 */         LogHelper.debug("Found filename %s", new Object[] { s });
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @LauncherAPI
/*     */   public HashedEntry getEntry(String name) {
/* 201 */     return this.map.get(name);
/*     */   }
/*     */ 
/*     */   
/*     */   public HashedEntry.Type getType() {
/* 206 */     return HashedEntry.Type.DIR;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public boolean isEmpty() {
/* 211 */     return this.map.isEmpty();
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public Map<String, HashedEntry> map() {
/* 216 */     return Collections.unmodifiableMap(this.map);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public HashedEntry resolve(Iterable<String> path) {
/* 221 */     HashedEntry current = this;
/* 222 */     for (String pathEntry : path) {
/* 223 */       if (current instanceof HashedDir) {
/* 224 */         current = ((HashedDir)current).map.get(pathEntry);
/*     */         continue;
/*     */       } 
/* 227 */       return null;
/*     */     } 
/* 229 */     return current;
/*     */   }
/*     */   
/*     */   private HashedDir sideDiff(HashedDir other, FileNameMatcher matcher, Deque<String> path, boolean mismatchList) {
/* 233 */     HashedDir diff = new HashedDir();
/* 234 */     for (Map.Entry<String, HashedEntry> mapEntry : this.map.entrySet()) {
/* 235 */       HashedFile file, otherFile; HashedDir dir, otherDir; String name = mapEntry.getKey();
/* 236 */       HashedEntry entry = mapEntry.getValue();
/* 237 */       path.add(name);
/*     */ 
/*     */       
/* 240 */       boolean shouldUpdate = (matcher == null || matcher.shouldUpdate(path));
/*     */ 
/*     */       
/* 243 */       HashedEntry.Type type = entry.getType();
/* 244 */       HashedEntry otherEntry = other.map.get(name);
/* 245 */       if (otherEntry == null || otherEntry.getType() != type) {
/* 246 */         if (shouldUpdate || (mismatchList && otherEntry == null)) {
/* 247 */           diff.map.put(name, entry);
/*     */ 
/*     */           
/* 250 */           if (!mismatchList)
/* 251 */             entry.flag = true; 
/*     */         } 
/* 253 */         path.removeLast();
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 258 */       switch (type) {
/*     */         case FILE:
/* 260 */           file = (HashedFile)entry;
/* 261 */           otherFile = (HashedFile)otherEntry;
/* 262 */           if (mismatchList && shouldUpdate && !file.isSame(otherFile))
/* 263 */             diff.map.put(name, entry); 
/*     */           break;
/*     */         case DIR:
/* 266 */           dir = (HashedDir)entry;
/* 267 */           otherDir = (HashedDir)otherEntry;
/* 268 */           if (mismatchList || shouldUpdate) {
/* 269 */             HashedDir mismatch = dir.sideDiff(otherDir, matcher, path, mismatchList);
/* 270 */             if (!mismatch.isEmpty())
/* 271 */               diff.map.put(name, mismatch); 
/*     */           } 
/*     */           break;
/*     */         default:
/* 275 */           throw new AssertionError("Unsupported hashed entry type: " + type.name());
/*     */       } 
/*     */ 
/*     */       
/* 279 */       path.removeLast();
/*     */     } 
/* 281 */     return diff;
/*     */   }
/*     */   
/*     */   public HashedDir sideCompare(HashedDir other, FileNameMatcher matcher, Deque<String> path, boolean mismatchList) {
/* 285 */     HashedDir diff = new HashedDir();
/* 286 */     for (Map.Entry<String, HashedEntry> mapEntry : this.map.entrySet()) {
/* 287 */       HashedFile file, otherFile; HashedDir dir, otherDir; String name = mapEntry.getKey();
/* 288 */       HashedEntry entry = mapEntry.getValue();
/* 289 */       path.add(name);
/*     */ 
/*     */       
/* 292 */       boolean shouldUpdate = (matcher == null || matcher.shouldUpdate(path));
/*     */ 
/*     */       
/* 295 */       HashedEntry.Type type = entry.getType();
/* 296 */       HashedEntry otherEntry = other.map.get(name);
/* 297 */       if (otherEntry == null || otherEntry.getType() != type) {
/* 298 */         if (shouldUpdate || (mismatchList && otherEntry == null)) {
/* 299 */           diff.map.put(name, entry);
/*     */ 
/*     */           
/* 302 */           if (!mismatchList)
/* 303 */             entry.flag = true; 
/*     */         } 
/* 305 */         path.removeLast();
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 310 */       switch (type) {
/*     */         case FILE:
/* 312 */           file = (HashedFile)entry;
/* 313 */           otherFile = (HashedFile)otherEntry;
/* 314 */           if (mismatchList && shouldUpdate && file.isSame(otherFile))
/* 315 */             diff.map.put(name, entry); 
/*     */           break;
/*     */         case DIR:
/* 318 */           dir = (HashedDir)entry;
/* 319 */           otherDir = (HashedDir)otherEntry;
/* 320 */           if (mismatchList || shouldUpdate) {
/* 321 */             HashedDir mismatch = dir.sideCompare(otherDir, matcher, path, mismatchList);
/* 322 */             if (!mismatch.isEmpty())
/* 323 */               diff.map.put(name, mismatch); 
/*     */           } 
/*     */           break;
/*     */         default:
/* 327 */           throw new AssertionError("Unsupported hashed entry type: " + type.name());
/*     */       } 
/*     */ 
/*     */       
/* 331 */       path.removeLast();
/*     */     } 
/* 333 */     return diff;
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() {
/* 338 */     return this.map.values().stream().mapToLong(HashedEntry::size).sum();
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(HOutput output) throws IOException {
/* 343 */     Set<Map.Entry<String, HashedEntry>> entries = this.map.entrySet();
/* 344 */     output.writeLength(entries.size(), 0);
/* 345 */     for (Map.Entry<String, HashedEntry> mapEntry : entries) {
/* 346 */       output.writeString(mapEntry.getKey(), 255);
/*     */ 
/*     */       
/* 349 */       HashedEntry entry = mapEntry.getValue();
/* 350 */       EnumSerializer.write(output, entry.getType());
/* 351 */       entry.write(output);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void walk(CharSequence separator, WalkCallback callback) throws IOException {
/* 356 */     String append = "";
/* 357 */     walk(append, separator, callback, true);
/*     */   }
/*     */   
/*     */   public enum WalkAction {
/* 361 */     STOP, CONTINUE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private WalkAction walk(String append, CharSequence separator, WalkCallback callback, boolean noSeparator) throws IOException {
/* 370 */     for (Map.Entry<String, HashedEntry> entry : this.map.entrySet()) {
/* 371 */       String newAppend; HashedEntry e = entry.getValue();
/* 372 */       if (e.getType() == HashedEntry.Type.FILE) {
/* 373 */         if (noSeparator) {
/* 374 */           WalkAction walkAction1 = callback.walked(append + (String)entry.getKey(), entry.getKey(), e);
/* 375 */           if (walkAction1 == WalkAction.STOP) return walkAction1;  continue;
/*     */         } 
/* 377 */         WalkAction walkAction = callback.walked(append + separator + (String)entry.getKey(), entry.getKey(), e);
/* 378 */         if (walkAction == WalkAction.STOP) return walkAction;
/*     */         
/*     */         continue;
/*     */       } 
/* 382 */       if (noSeparator) { newAppend = append + (String)entry.getKey(); }
/* 383 */       else { newAppend = append + separator + (String)entry.getKey(); }
/* 384 */        WalkAction a = callback.walked(newAppend, entry.getKey(), e);
/* 385 */       if (a == WalkAction.STOP) return a; 
/* 386 */       a = ((HashedDir)e).walk(newAppend, separator, callback, false);
/* 387 */       if (a == WalkAction.STOP) return a;
/*     */     
/*     */     } 
/* 390 */     return WalkAction.CONTINUE;
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public HashedDir() {}
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface WalkCallback {
/*     */     HashedDir.WalkAction walked(String param1String1, String param1String2, HashedEntry param1HashedEntry) throws IOException;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\hasher\HashedDir.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */