/*     */ package io.sentry.buffer;
/*     */ 
/*     */ import io.sentry.event.Event;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DiskBuffer
/*     */   implements Buffer
/*     */ {
/*     */   public static final String FILE_SUFFIX = ".sentry-event";
/*  23 */   private static final Logger logger = LoggerFactory.getLogger(DiskBuffer.class);
/*     */ 
/*     */ 
/*     */   
/*     */   private int maxEvents;
/*     */ 
/*     */ 
/*     */   
/*     */   private final File bufferDir;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DiskBuffer(File bufferDir, int maxEvents) {
/*  37 */     this.bufferDir = bufferDir;
/*  38 */     this.maxEvents = maxEvents;
/*     */     
/*  40 */     String errMsg = "Could not create or write to disk buffer dir: " + bufferDir.getAbsolutePath();
/*     */     try {
/*  42 */       bufferDir.mkdirs();
/*  43 */       if (!bufferDir.isDirectory() || !bufferDir.canWrite()) {
/*  44 */         throw new RuntimeException(errMsg);
/*     */       }
/*  46 */     } catch (RuntimeException e) {
/*  47 */       throw new RuntimeException(errMsg, e);
/*     */     } 
/*     */     
/*  50 */     logger.debug(Integer.toString(getNumStoredEvents()) + " stored events found in dir: " + bufferDir
/*     */         
/*  52 */         .getAbsolutePath());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(Event event) {
/*  63 */     if (getNumStoredEvents() >= this.maxEvents) {
/*  64 */       logger.warn("Not adding Event because at least " + 
/*  65 */           Integer.toString(this.maxEvents) + " events are already stored: " + event.getId());
/*     */       
/*     */       return;
/*     */     } 
/*  69 */     File eventFile = new File(this.bufferDir.getAbsolutePath(), event.getId().toString() + ".sentry-event");
/*  70 */     if (eventFile.exists()) {
/*  71 */       logger.trace("Not adding Event to offline storage because it already exists: " + eventFile
/*  72 */           .getAbsolutePath());
/*     */       return;
/*     */     } 
/*  75 */     logger.debug("Adding Event to offline storage: " + eventFile.getAbsolutePath());
/*     */ 
/*     */     
/*  78 */     try(FileOutputStream fileOutputStream = new FileOutputStream(eventFile); 
/*  79 */         ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
/*  80 */       objectOutputStream.writeObject(event);
/*  81 */     } catch (IOException|RuntimeException e) {
/*  82 */       logger.error("Error writing Event to offline storage: " + event.getId(), e);
/*     */     } 
/*     */     
/*  85 */     logger.debug(Integer.toString(getNumStoredEvents()) + " stored events are now in dir: " + this.bufferDir
/*     */         
/*  87 */         .getAbsolutePath());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void discard(Event event) {
/*  97 */     File eventFile = new File(this.bufferDir, event.getId().toString() + ".sentry-event");
/*  98 */     if (eventFile.exists()) {
/*  99 */       logger.debug("Discarding Event from offline storage: " + eventFile.getAbsolutePath());
/* 100 */       if (!eventFile.delete()) {
/* 101 */         logger.warn("Failed to delete Event: " + eventFile.getAbsolutePath());
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
/*     */   private Event fileToEvent(File eventFile) {
/*     */     Object eventObj;
/* 115 */     try(FileInputStream fileInputStream = new FileInputStream(new File(eventFile.getAbsolutePath())); 
/* 116 */         ObjectInputStream ois = new ObjectInputStream(fileInputStream)) {
/* 117 */       eventObj = ois.readObject();
/* 118 */     } catch (FileNotFoundException e) {
/*     */       
/* 120 */       return null;
/* 121 */     } catch (IOException|ClassNotFoundException|RuntimeException e) {
/* 122 */       logger.error("Error reading Event file: " + eventFile.getAbsolutePath(), e);
/* 123 */       if (!eventFile.delete()) {
/* 124 */         logger.warn("Failed to delete Event: " + eventFile.getAbsolutePath());
/*     */       }
/* 126 */       return null;
/*     */     } 
/*     */     
/*     */     try {
/* 130 */       return (Event)eventObj;
/* 131 */     } catch (RuntimeException e) {
/* 132 */       logger.error("Error casting Object to Event: " + eventFile.getAbsolutePath(), e);
/* 133 */       if (!eventFile.delete()) {
/* 134 */         logger.warn("Failed to delete Event: " + eventFile.getAbsolutePath());
/*     */       }
/* 136 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Event getNextEvent(Iterator<File> files) {
/* 147 */     while (files.hasNext()) {
/* 148 */       File file = files.next();
/*     */ 
/*     */       
/* 151 */       if (!file.getAbsolutePath().endsWith(".sentry-event")) {
/*     */         continue;
/*     */       }
/*     */       
/* 155 */       Event event = fileToEvent(file);
/* 156 */       if (event != null) {
/* 157 */         return event;
/*     */       }
/*     */     } 
/*     */     
/* 161 */     return null;
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
/*     */   public Iterator<Event> getEvents() {
/* 177 */     File[] fileArray = this.bufferDir.listFiles();
/* 178 */     if (fileArray == null) {
/* 179 */       return Collections.<Event>emptyList().iterator();
/*     */     }
/* 181 */     final Iterator<File> files = Arrays.<File>asList(fileArray).iterator();
/*     */     
/* 183 */     return new Iterator<Event>() {
/* 184 */         private Event next = DiskBuffer.this.getNextEvent(files);
/*     */ 
/*     */         
/*     */         public boolean hasNext() {
/* 188 */           return (this.next != null);
/*     */         }
/*     */ 
/*     */         
/*     */         public Event next() {
/* 193 */           Event toReturn = this.next;
/* 194 */           this.next = DiskBuffer.this.getNextEvent(files);
/* 195 */           return toReturn;
/*     */         }
/*     */ 
/*     */         
/*     */         public void remove() {
/* 200 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private int getNumStoredEvents() {
/* 206 */     int count = 0;
/* 207 */     for (File file : this.bufferDir.listFiles()) {
/* 208 */       if (file.getAbsolutePath().endsWith(".sentry-event")) {
/* 209 */         count++;
/*     */       }
/*     */     } 
/* 212 */     return count;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\io\sentry\buffer\DiskBuffer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */