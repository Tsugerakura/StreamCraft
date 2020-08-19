/*    */ package com.mojang.util;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ import java.util.concurrent.locks.ReadWriteLock;
/*    */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*    */ import org.apache.logging.log4j.core.Filter;
/*    */ import org.apache.logging.log4j.core.Layout;
/*    */ import org.apache.logging.log4j.core.LogEvent;
/*    */ import org.apache.logging.log4j.core.appender.AbstractAppender;
/*    */ import org.apache.logging.log4j.core.config.plugins.Plugin;
/*    */ import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
/*    */ import org.apache.logging.log4j.core.config.plugins.PluginElement;
/*    */ import org.apache.logging.log4j.core.config.plugins.PluginFactory;
/*    */ import org.apache.logging.log4j.core.layout.PatternLayout;
/*    */ 
/*    */ @Plugin(name = "Queue", category = "Core", elementType = "appender", printObject = true)
/*    */ public class QueueLogAppender
/*    */   extends AbstractAppender {
/*    */   private static final int MAX_CAPACITY = 250;
/* 24 */   private static final Map<String, BlockingQueue<String>> QUEUES = new HashMap<>();
/* 25 */   private static final ReadWriteLock QUEUE_LOCK = new ReentrantReadWriteLock();
/*    */   
/*    */   private final BlockingQueue<String> queue;
/*    */   
/*    */   public QueueLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions, BlockingQueue<String> queue) {
/* 30 */     super(name, filter, layout, ignoreExceptions);
/* 31 */     this.queue = queue;
/*    */   }
/*    */ 
/*    */   
/*    */   public void append(LogEvent event) {
/* 36 */     if (this.queue.size() >= 250) {
/* 37 */       this.queue.clear();
/*    */     }
/* 39 */     this.queue.add(getLayout().toSerializable(event).toString());
/*    */   }
/*    */   @PluginFactory
/*    */   public static QueueLogAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("ignoreExceptions") String ignore, @PluginElement("Layout") Layout<? extends Serializable> layout, @PluginElement("Filters") Filter filter, @PluginAttribute("target") String target) {
/*    */     PatternLayout patternLayout;
/* 44 */     boolean ignoreExceptions = Boolean.parseBoolean(ignore);
/*    */     
/* 46 */     if (name == null) {
/* 47 */       LOGGER.error("No name provided for QueueLogAppender");
/* 48 */       return null;
/*    */     } 
/*    */     
/* 51 */     if (target == null) {
/* 52 */       target = name;
/*    */     }
/*    */     
/* 55 */     QUEUE_LOCK.writeLock().lock();
/* 56 */     BlockingQueue<String> queue = QUEUES.get(target);
/* 57 */     if (queue == null) {
/* 58 */       queue = new LinkedBlockingQueue<>();
/* 59 */       QUEUES.put(target, queue);
/*    */     } 
/* 61 */     QUEUE_LOCK.writeLock().unlock();
/*    */     
/* 63 */     if (layout == null) {
/* 64 */       patternLayout = PatternLayout.newBuilder().build();
/*    */     }
/*    */     
/* 67 */     return new QueueLogAppender(name, filter, (Layout<? extends Serializable>)patternLayout, ignoreExceptions, queue);
/*    */   }
/*    */   
/*    */   public static String getNextLogEvent(String queueName) {
/* 71 */     QUEUE_LOCK.readLock().lock();
/* 72 */     BlockingQueue<String> queue = QUEUES.get(queueName);
/* 73 */     QUEUE_LOCK.readLock().unlock();
/*    */     
/* 75 */     if (queue != null) {
/*    */       try {
/* 77 */         return queue.take();
/* 78 */       } catch (InterruptedException interruptedException) {}
/*    */     }
/*    */ 
/*    */     
/* 82 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojan\\util\QueueLogAppender.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */