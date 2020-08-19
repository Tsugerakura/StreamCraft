/*    */ package net.querz.nbt;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.function.Supplier;
/*    */ 
/*    */ public final class TagFactory
/*    */ {
/*    */   private static class TagMapping<T extends Tag<?>>
/*    */   {
/*    */     private int id;
/*    */     private Supplier<T> factory;
/*    */     private Class<T> clazz;
/*    */     
/*    */     TagMapping(int id, Supplier<T> factory, Class<T> clazz) {
/* 16 */       this.id = id;
/* 17 */       this.factory = factory;
/* 18 */       this.clazz = clazz;
/*    */     }
/*    */   }
/*    */   
/* 22 */   private static Map<Integer, TagMapping<?>> idMapping = new HashMap<>();
/* 23 */   private static Map<Class<?>, TagMapping<?>> classMapping = new HashMap<>();
/*    */   static {
/* 25 */     put(0, () -> EndTag.INSTANCE, EndTag.class);
/* 26 */     put(1, ByteTag::new, ByteTag.class);
/* 27 */     put(2, ShortTag::new, ShortTag.class);
/* 28 */     put(3, IntTag::new, IntTag.class);
/* 29 */     put(4, LongTag::new, LongTag.class);
/* 30 */     put(5, FloatTag::new, FloatTag.class);
/* 31 */     put(6, DoubleTag::new, DoubleTag.class);
/* 32 */     put(7, ByteArrayTag::new, ByteArrayTag.class);
/* 33 */     put(8, StringTag::new, StringTag.class);
/* 34 */     put(9, ListTag::createUnchecked, ListTag.class);
/* 35 */     put(10, CompoundTag::new, CompoundTag.class);
/* 36 */     put(11, IntArrayTag::new, IntArrayTag.class);
/* 37 */     put(12, LongArrayTag::new, LongArrayTag.class);
/*    */   }
/*    */   
/*    */   private static <T extends Tag<?>> void put(int id, Supplier<T> factory, Class<T> clazz) {
/* 41 */     TagMapping<T> mapping = new TagMapping<>(id, factory, clazz);
/* 42 */     idMapping.put(Integer.valueOf(id), mapping);
/* 43 */     classMapping.put(clazz, mapping);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static Tag<?> fromID(int id) {
/* 49 */     TagMapping<?> mapping = idMapping.get(Integer.valueOf(id));
/* 50 */     if (mapping == null) {
/* 51 */       throw new IllegalArgumentException("unknown Tag id " + id);
/*    */     }
/* 53 */     return mapping.factory.get();
/*    */   }
/*    */   
/*    */   public static Class<?> classFromID(int id) {
/* 57 */     TagMapping<?> mapping = idMapping.get(Integer.valueOf(id));
/* 58 */     if (mapping == null) {
/* 59 */       throw new IllegalArgumentException("unknown Tag id " + id);
/*    */     }
/* 61 */     return mapping.clazz;
/*    */   }
/*    */   
/*    */   public static byte idFromClass(Class<?> clazz) {
/* 65 */     TagMapping<?> mapping = classMapping.get(clazz);
/* 66 */     if (mapping == null) {
/* 67 */       throw new IllegalArgumentException("unknown Tag class " + clazz.getName());
/*    */     }
/* 69 */     return (byte)mapping.id;
/*    */   }
/*    */   
/*    */   public static <T extends Tag<?>> void registerCustomTag(int id, Supplier<T> factory, Class<T> clazz) {
/* 73 */     checkID(id);
/* 74 */     if (idMapping.containsKey(Integer.valueOf(id))) {
/* 75 */       throw new IllegalArgumentException("custom tag already registered");
/*    */     }
/* 77 */     put(id, factory, clazz);
/*    */   }
/*    */   
/*    */   public static void unregisterCustomTag(int id) {
/* 81 */     idMapping.remove(Integer.valueOf(id));
/* 82 */     for (TagMapping<?> mapping : classMapping.values()) {
/* 83 */       if (mapping.id == id) {
/* 84 */         classMapping.remove(mapping.clazz);
/*    */         return;
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   private static void checkID(int id) {
/* 91 */     if (id < 0) {
/* 92 */       throw new IllegalArgumentException("id cannot be negative");
/*    */     }
/* 94 */     if (id <= 12) {
/* 95 */       throw new IllegalArgumentException("cannot change default tags");
/*    */     }
/* 97 */     if (id > 127)
/* 98 */       throw new IllegalArgumentException("id out of bounds: " + id); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\TagFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */