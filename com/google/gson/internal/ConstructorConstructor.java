/*     */ package com.google.gson.internal;
/*     */ 
/*     */ import com.google.gson.InstanceCreator;
/*     */ import com.google.gson.JsonIOException;
/*     */ import com.google.gson.internal.reflect.ReflectionAccessor;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeMap;
/*     */ import java.util.TreeSet;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.ConcurrentNavigableMap;
/*     */ import java.util.concurrent.ConcurrentSkipListMap;
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
/*     */ 
/*     */ public final class ConstructorConstructor
/*     */ {
/*     */   private final Map<Type, InstanceCreator<?>> instanceCreators;
/*  51 */   private final ReflectionAccessor accessor = ReflectionAccessor.getInstance();
/*     */   
/*     */   public ConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators) {
/*  54 */     this.instanceCreators = instanceCreators;
/*     */   }
/*     */   
/*     */   public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
/*  58 */     final Type type = typeToken.getType();
/*  59 */     Class<? super T> rawType = typeToken.getRawType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  64 */     final InstanceCreator<T> typeCreator = (InstanceCreator<T>)this.instanceCreators.get(type);
/*  65 */     if (typeCreator != null) {
/*  66 */       return new ObjectConstructor<T>() {
/*     */           public T construct() {
/*  68 */             return (T)typeCreator.createInstance(type);
/*     */           }
/*     */         };
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  76 */     final InstanceCreator<T> rawTypeCreator = (InstanceCreator<T>)this.instanceCreators.get(rawType);
/*  77 */     if (rawTypeCreator != null) {
/*  78 */       return new ObjectConstructor<T>() {
/*     */           public T construct() {
/*  80 */             return (T)rawTypeCreator.createInstance(type);
/*     */           }
/*     */         };
/*     */     }
/*     */     
/*  85 */     ObjectConstructor<T> defaultConstructor = newDefaultConstructor(rawType);
/*  86 */     if (defaultConstructor != null) {
/*  87 */       return defaultConstructor;
/*     */     }
/*     */     
/*  90 */     ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
/*  91 */     if (defaultImplementation != null) {
/*  92 */       return defaultImplementation;
/*     */     }
/*     */ 
/*     */     
/*  96 */     return newUnsafeAllocator(type, rawType);
/*     */   }
/*     */   
/*     */   private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
/*     */     try {
/* 101 */       final Constructor<? super T> constructor = rawType.getDeclaredConstructor(new Class[0]);
/* 102 */       if (!constructor.isAccessible()) {
/* 103 */         this.accessor.makeAccessible(constructor);
/*     */       }
/* 105 */       return new ObjectConstructor<T>()
/*     */         {
/*     */           public T construct() {
/*     */             try {
/* 109 */               Object[] args = null;
/* 110 */               return constructor.newInstance(args);
/* 111 */             } catch (InstantiationException e) {
/*     */               
/* 113 */               throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
/* 114 */             } catch (InvocationTargetException e) {
/*     */ 
/*     */               
/* 117 */               throw new RuntimeException("Failed to invoke " + constructor + " with no args", e
/* 118 */                   .getTargetException());
/* 119 */             } catch (IllegalAccessException e) {
/* 120 */               throw new AssertionError(e);
/*     */             } 
/*     */           }
/*     */         };
/* 124 */     } catch (NoSuchMethodException e) {
/* 125 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private <T> ObjectConstructor<T> newDefaultImplementationConstructor(final Type type, Class<? super T> rawType) {
/* 136 */     if (Collection.class.isAssignableFrom(rawType)) {
/* 137 */       if (SortedSet.class.isAssignableFrom(rawType))
/* 138 */         return new ObjectConstructor<T>() {
/*     */             public T construct() {
/* 140 */               return (T)new TreeSet();
/*     */             }
/*     */           }; 
/* 143 */       if (EnumSet.class.isAssignableFrom(rawType)) {
/* 144 */         return new ObjectConstructor<T>()
/*     */           {
/*     */             public T construct() {
/* 147 */               if (type instanceof ParameterizedType) {
/* 148 */                 Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
/* 149 */                 if (elementType instanceof Class) {
/* 150 */                   return (T)EnumSet.noneOf((Class<Enum>)elementType);
/*     */                 }
/* 152 */                 throw new JsonIOException("Invalid EnumSet type: " + type.toString());
/*     */               } 
/*     */               
/* 155 */               throw new JsonIOException("Invalid EnumSet type: " + type.toString());
/*     */             }
/*     */           };
/*     */       }
/* 159 */       if (Set.class.isAssignableFrom(rawType))
/* 160 */         return new ObjectConstructor<T>() {
/*     */             public T construct() {
/* 162 */               return (T)new LinkedHashSet();
/*     */             }
/*     */           }; 
/* 165 */       if (Queue.class.isAssignableFrom(rawType)) {
/* 166 */         return new ObjectConstructor<T>() {
/*     */             public T construct() {
/* 168 */               return (T)new ArrayDeque();
/*     */             }
/*     */           };
/*     */       }
/* 172 */       return new ObjectConstructor<T>() {
/*     */           public T construct() {
/* 174 */             return (T)new ArrayList();
/*     */           }
/*     */         };
/*     */     } 
/*     */ 
/*     */     
/* 180 */     if (Map.class.isAssignableFrom(rawType)) {
/* 181 */       if (ConcurrentNavigableMap.class.isAssignableFrom(rawType))
/* 182 */         return new ObjectConstructor<T>() {
/*     */             public T construct() {
/* 184 */               return (T)new ConcurrentSkipListMap<Object, Object>();
/*     */             }
/*     */           }; 
/* 187 */       if (ConcurrentMap.class.isAssignableFrom(rawType))
/* 188 */         return new ObjectConstructor<T>() {
/*     */             public T construct() {
/* 190 */               return (T)new ConcurrentHashMap<Object, Object>();
/*     */             }
/*     */           }; 
/* 193 */       if (SortedMap.class.isAssignableFrom(rawType))
/* 194 */         return new ObjectConstructor<T>() {
/*     */             public T construct() {
/* 196 */               return (T)new TreeMap<Object, Object>();
/*     */             }
/*     */           }; 
/* 199 */       if (type instanceof ParameterizedType && !String.class.isAssignableFrom(
/* 200 */           TypeToken.get(((ParameterizedType)type).getActualTypeArguments()[0]).getRawType())) {
/* 201 */         return new ObjectConstructor<T>() {
/*     */             public T construct() {
/* 203 */               return (T)new LinkedHashMap<Object, Object>();
/*     */             }
/*     */           };
/*     */       }
/* 207 */       return new ObjectConstructor<T>() {
/*     */           public T construct() {
/* 209 */             return (T)new LinkedTreeMap<Object, Object>();
/*     */           }
/*     */         };
/*     */     } 
/*     */ 
/*     */     
/* 215 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private <T> ObjectConstructor<T> newUnsafeAllocator(final Type type, final Class<? super T> rawType) {
/* 220 */     return new ObjectConstructor<T>() {
/* 221 */         private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();
/*     */         
/*     */         public T construct() {
/*     */           try {
/* 225 */             Object newInstance = this.unsafeAllocator.newInstance(rawType);
/* 226 */             return (T)newInstance;
/* 227 */           } catch (Exception e) {
/* 228 */             throw new RuntimeException("Unable to invoke no-args constructor for " + type + ". Registering an InstanceCreator with Gson for this type may fix this problem.", e);
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 236 */     return this.instanceCreators.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\ConstructorConstructor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */