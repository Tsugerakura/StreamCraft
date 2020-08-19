/*     */ package pro.gravit.launcher.downloader;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.nio.file.Path;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.client.ClientProtocolException;
/*     */ import org.apache.http.client.RedirectStrategy;
/*     */ import org.apache.http.client.ResponseHandler;
/*     */ import org.apache.http.client.methods.HttpGet;
/*     */ import org.apache.http.client.methods.HttpUriRequest;
/*     */ import org.apache.http.impl.client.CloseableHttpClient;
/*     */ import org.apache.http.impl.client.HttpClients;
/*     */ import org.apache.http.impl.client.LaxRedirectStrategy;
/*     */ import pro.gravit.utils.helper.CommonHelper;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.LogHelper;
/*     */ import pro.gravit.utils.helper.VerifyHelper;
/*     */ 
/*     */ public class ListDownloader {
/*  37 */   private static final AtomicInteger COUNTER_THR = new AtomicInteger(0); static {
/*  38 */     FACTORY = (r -> CommonHelper.newThread("Downloader Thread #" + COUNTER_THR.incrementAndGet(), true, r));
/*     */   } private static final ThreadFactory FACTORY;
/*     */   private static ExecutorService newExecutor() {
/*  41 */     return new ThreadPoolExecutor(0, VerifyHelper.verifyInt(Integer.parseInt(System.getProperty("launcher.downloadThreads", "3")), VerifyHelper.POSITIVE, "Thread max count must be positive."), 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), FACTORY);
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface DownloadCallback {
/*     */     void stateChanged(String param1String, long param1Long1, long param1Long2);
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface DownloadTotalCallback {
/*     */     void addTotal(long param1Long);
/*     */   }
/*     */   
/*     */   public static class DownloadTask {
/*     */     public String apply;
/*     */     public long size;
/*     */     
/*     */     public DownloadTask(String apply, long size) {
/*  59 */       this.apply = apply;
/*  60 */       this.size = size;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void download(String base, List<DownloadTask> applies, Path dstDirFile, DownloadCallback callback, DownloadTotalCallback totalCallback) throws IOException, URISyntaxException {
/*  67 */     try (CloseableHttpClient httpclient = HttpClients.custom().setUserAgent(IOHelper.USER_AGENT).setRedirectStrategy((RedirectStrategy)new LaxRedirectStrategy()).build()) {
/*  68 */       applies.sort((a, b) -> Long.compare(a.size, b.size));
/*  69 */       List<Callable<Void>> toExec = new ArrayList<>();
/*  70 */       URI baseUri = new URI(base);
/*  71 */       String scheme = baseUri.getScheme();
/*  72 */       String host = baseUri.getHost();
/*  73 */       int port = baseUri.getPort();
/*  74 */       if (port != -1)
/*  75 */         host = host + ":" + port; 
/*  76 */       String path = baseUri.getPath();
/*  77 */       List<IOException> excs = new CopyOnWriteArrayList<>();
/*  78 */       for (DownloadTask apply : applies) {
/*  79 */         URI u = new URI(scheme, host, path + apply.apply, "", "");
/*  80 */         callback.stateChanged(apply.apply, 0L, apply.size);
/*  81 */         Path targetPath = dstDirFile.resolve(apply.apply);
/*  82 */         toExec.add(() -> {
/*     */               if (LogHelper.isDebugEnabled())
/*     */                 LogHelper.debug("Download URL: %s to file %s dir: %s", new Object[] { u.toString(), targetPath.toAbsolutePath().toString(), dstDirFile.toAbsolutePath().toString() }); 
/*     */               try {
/*     */                 httpclient.execute((HttpUriRequest)new HttpGet(u), new FileDownloadResponseHandler(targetPath, apply, callback, totalCallback, false));
/*  87 */               } catch (IOException e) {
/*     */                 excs.add(e);
/*     */               } 
/*     */               return null;
/*     */             });
/*     */       } 
/*     */       try {
/*  94 */         ExecutorService e = newExecutor();
/*  95 */         e.invokeAll(toExec);
/*  96 */         e.shutdown();
/*  97 */         e.awaitTermination(4L, TimeUnit.HOURS);
/*  98 */       } catch (InterruptedException t) {
/*  99 */         LogHelper.error(t);
/*     */       } 
/* 101 */       if (!excs.isEmpty()) {
/* 102 */         IOException toThrow = excs.remove(0);
/* 103 */         excs.forEach(toThrow::addSuppressed);
/* 104 */         throw toThrow;
/*     */       } 
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
/*     */   public void downloadZip(String base, List<DownloadTask> applies, Path dstDirFile, DownloadCallback callback, DownloadTotalCallback totalCallback, boolean fullDownload) throws IOException, URISyntaxException {
/* 119 */     try (ZipInputStream input = IOHelper.newZipInput(new URL(base))) {
/* 120 */       for (ZipEntry entry = input.getNextEntry(); entry != null; entry = input.getNextEntry()) {
/* 121 */         if (!entry.isDirectory()) {
/*     */ 
/*     */           
/* 124 */           String name = entry.getName();
/* 125 */           callback.stateChanged(name, 0L, entry.getSize());
/* 126 */           LogHelper.subInfo("[ZIP] Downloading file: '%s'", new Object[] { name });
/* 127 */           if (fullDownload || applies.stream().anyMatch(t -> t.apply.equals(name))) {
/*     */             
/* 129 */             Path fileName = IOHelper.toPath(name);
/* 130 */             LogHelper.subInfo("[ZIP] Transfet file: '%s' to '%s'", new Object[] { name, dstDirFile.resolve(fileName) });
/* 131 */             transfer(input, dstDirFile.resolve(fileName), fileName.toString(), entry.getSize(), callback, totalCallback);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void downloadOne(String url, Path target) throws IOException, URISyntaxException {
/* 140 */     try (CloseableHttpClient httpclient = HttpClients.custom().setRedirectStrategy((RedirectStrategy)new LaxRedirectStrategy()).build()) {
/*     */ 
/*     */       
/* 143 */       URI u = (new URL(url)).toURI();
/* 144 */       if (LogHelper.isDebugEnabled()) {
/* 145 */         LogHelper.debug("Download URL: %s", new Object[] { u.toString() });
/*     */       }
/* 147 */       HttpGet get = new HttpGet(u);
/* 148 */       httpclient.execute((HttpUriRequest)get, new FileDownloadResponseHandler(target.toAbsolutePath()));
/*     */     } 
/*     */   }
/*     */   
/*     */   static class FileDownloadResponseHandler implements ResponseHandler<Path> {
/*     */     private final Path target;
/*     */     private final ListDownloader.DownloadTask task;
/*     */     private final ListDownloader.DownloadCallback callback;
/*     */     private final ListDownloader.DownloadTotalCallback totalCallback;
/*     */     private final boolean zip;
/*     */     
/*     */     public FileDownloadResponseHandler(Path target) {
/* 160 */       this.target = target;
/* 161 */       this.task = null;
/* 162 */       this.zip = false;
/* 163 */       this.callback = null;
/* 164 */       this.totalCallback = null;
/*     */     }
/*     */     
/*     */     public FileDownloadResponseHandler(Path target, ListDownloader.DownloadTask task, ListDownloader.DownloadCallback callback, ListDownloader.DownloadTotalCallback totalCallback, boolean zip) {
/* 168 */       this.target = target;
/* 169 */       this.task = task;
/* 170 */       this.callback = callback;
/* 171 */       this.totalCallback = totalCallback;
/* 172 */       this.zip = zip;
/*     */     }
/*     */     
/*     */     public FileDownloadResponseHandler(Path target, ListDownloader.DownloadCallback callback, ListDownloader.DownloadTotalCallback totalCallback, boolean zip) {
/* 176 */       this.target = target;
/* 177 */       this.task = null;
/* 178 */       this.callback = callback;
/* 179 */       this.totalCallback = totalCallback;
/* 180 */       this.zip = zip;
/*     */     }
/*     */ 
/*     */     
/*     */     public Path handleResponse(HttpResponse response) throws IOException {
/* 185 */       InputStream source = response.getEntity().getContent();
/* 186 */       int returnCode = response.getStatusLine().getStatusCode();
/* 187 */       if (returnCode != 200)
/*     */       {
/* 189 */         throw new IllegalStateException(String.format("Request download file %s return code %d", new Object[] { this.target.toString(), Integer.valueOf(returnCode) }));
/*     */       }
/* 191 */       long contentLength = response.getEntity().getContentLength();
/* 192 */       if (this.task != null && contentLength != this.task.size)
/*     */       {
/* 194 */         LogHelper.warning("Missing content length: expected %d | found %d", new Object[] { Long.valueOf(this.task.size), Long.valueOf(contentLength) });
/*     */       }
/* 196 */       if (this.zip) {
/* 197 */         try (ZipInputStream input = IOHelper.newZipInput(source)) {
/* 198 */           ZipEntry entry = input.getNextEntry();
/* 199 */           while (entry != null) {
/* 200 */             if (entry.isDirectory()) {
/* 201 */               entry = input.getNextEntry();
/*     */               continue;
/*     */             } 
/* 204 */             long size = entry.getSize();
/* 205 */             String filename = entry.getName();
/* 206 */             Path target = this.target.resolve(filename);
/* 207 */             if (this.callback != null) {
/* 208 */               this.callback.stateChanged(entry.getName(), 0L, entry.getSize());
/*     */             }
/* 210 */             if (LogHelper.isDevEnabled()) {
/* 211 */               LogHelper.dev("Resolved filename %s to %s", new Object[] { filename, target.toAbsolutePath().toString() });
/*     */             }
/* 213 */             ListDownloader.transfer(source, target, filename, size, this.callback, this.totalCallback);
/* 214 */             entry = input.getNextEntry();
/*     */           } 
/*     */         } 
/*     */         
/* 218 */         return null;
/*     */       } 
/* 220 */       if (this.callback != null && this.task != null) {
/* 221 */         this.callback.stateChanged(this.task.apply, 0L, this.task.size);
/* 222 */         ListDownloader.transfer(source, this.target, this.task.apply, this.task.size, this.callback, this.totalCallback);
/*     */       } else {
/* 224 */         IOHelper.transfer(source, this.target);
/* 225 */       }  return this.target;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void transfer(InputStream input, Path file, String filename, long size, DownloadCallback callback, DownloadTotalCallback totalCallback) throws IOException {
/* 230 */     try (OutputStream fileOutput = IOHelper.newOutput(file)) {
/* 231 */       long downloaded = 0L;
/*     */ 
/*     */       
/* 234 */       byte[] bytes = IOHelper.newBuffer();
/* 235 */       while (downloaded < size) {
/* 236 */         int remaining = (int)Math.min(size - downloaded, bytes.length);
/* 237 */         int length = input.read(bytes, 0, remaining);
/* 238 */         if (length < 0) {
/* 239 */           throw new EOFException(String.format("%d bytes remaining", new Object[] { Long.valueOf(size - downloaded) }));
/*     */         }
/*     */         
/* 242 */         fileOutput.write(bytes, 0, length);
/*     */ 
/*     */         
/* 245 */         downloaded += length;
/*     */         
/* 247 */         totalCallback.addTotal(length);
/* 248 */         callback.stateChanged(filename, downloaded, size);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\downloader\ListDownloader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */