/*     */ package net.querz.nbt.custom;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InvalidClassException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Supplier;
/*     */ import net.querz.nbt.Tag;
/*     */ import net.querz.nbt.TagFactory;
/*     */ 
/*     */ public class ObjectTag<T extends Serializable>
/*     */   extends Tag<T> implements Comparable<ObjectTag<T>> {
/*     */   public static void register() {
/*  18 */     TagFactory.registerCustomTag(90, ObjectTag::new, ObjectTag.class);
/*     */   }
/*     */   
/*     */   public ObjectTag() {
/*  22 */     super(null);
/*     */   }
/*     */   
/*     */   public ObjectTag(T value) {
/*  26 */     super(value);
/*     */   }
/*     */ 
/*     */   
/*     */   protected T checkValue(T value) {
/*  31 */     return value;
/*     */   }
/*     */ 
/*     */   
/*     */   public T getValue() {
/*  36 */     return (T)super.getValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(T value) {
/*  41 */     super.setValue(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public <L extends Serializable> ObjectTag<L> asTypedObjectTag(Class<L> type) {
/*  46 */     checkTypeClass(type);
/*  47 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void serializeValue(DataOutputStream dos, int maxDepth) throws IOException {
/*  52 */     (new ObjectOutputStream(dos)).writeObject(getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void deserializeValue(DataInputStream dis, int maxDepth) throws IOException {
/*     */     try {
/*  59 */       setValue((T)(new ObjectInputStream(dis)).readObject());
/*  60 */     } catch (InvalidClassException|ClassNotFoundException e) {
/*  61 */       throw new IOException(e.getCause());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String valueToString(int maxDepth) {
/*  67 */     return (getValue() == null) ? "null" : escapeString(getValue().toString(), false);
/*     */   }
/*     */ 
/*     */   
/*     */   public String valueToTagString(int maxDepth) {
/*  72 */     return (getValue() == null) ? "null" : escapeString(getValue().toString(), true);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/*  77 */     return (super.equals(other) && Objects.equals(getValue(), ((ObjectTag)other).getValue()));
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  82 */     if (getValue() == null) {
/*  83 */       return 0;
/*     */     }
/*  85 */     return getValue().hashCode();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(ObjectTag<T> o) {
/*  91 */     if (o.getValue() instanceof Comparable && getValue() instanceof Comparable)
/*  92 */       return ((Comparable)getValue()).compareTo(o.getValue()); 
/*  93 */     if (o.getValue() == getValue())
/*  94 */       return 0; 
/*  95 */     if (getValue() == null)
/*     */     {
/*  97 */       return 1; } 
/*  98 */     if (o.getValue() == null) {
/*  99 */       return -1;
/*     */     }
/* 101 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ObjectTag<T> clone() {
/* 107 */     if (getValue() == null) {
/* 108 */       return new ObjectTag();
/*     */     }
/*     */     try {
/* 111 */       return new ObjectTag((T)getValue().getClass().getMethod("clone", new Class[0]).invoke(getValue(), new Object[0]));
/* 112 */     } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException|NoSuchMethodException e) {
/* 113 */       return new ObjectTag(getValue());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkTypeClass(Class<?> clazz) {
/* 118 */     if (getValue() != null && !clazz.isAssignableFrom(getValue().getClass()))
/* 119 */       throw new ClassCastException(String.format("cannot cast ObjectTag<%s> to ObjectTag<%s>", new Object[] {
/*     */               
/* 121 */               getValue().getClass().getSimpleName(), clazz.getSimpleName()
/*     */             })); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\custom\ObjectTag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */