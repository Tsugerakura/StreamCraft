/*     */ package com.google.gson.internal;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.GenericDeclaration;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Properties;
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
/*     */ public final class $Gson$Types
/*     */ {
/*  40 */   static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
/*     */   
/*     */   private $Gson$Types() {
/*  43 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ParameterizedType newParameterizedTypeWithOwner(Type ownerType, Type rawType, Type... typeArguments) {
/*  54 */     return new ParameterizedTypeImpl(ownerType, rawType, typeArguments);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static GenericArrayType arrayOf(Type componentType) {
/*  64 */     return new GenericArrayTypeImpl(componentType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WildcardType subtypeOf(Type bound) {
/*     */     Type[] upperBounds;
/*  75 */     if (bound instanceof WildcardType) {
/*  76 */       upperBounds = ((WildcardType)bound).getUpperBounds();
/*     */     } else {
/*  78 */       upperBounds = new Type[] { bound };
/*     */     } 
/*  80 */     return new WildcardTypeImpl(upperBounds, EMPTY_TYPE_ARRAY);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static WildcardType supertypeOf(Type bound) {
/*     */     Type[] lowerBounds;
/*  90 */     if (bound instanceof WildcardType) {
/*  91 */       lowerBounds = ((WildcardType)bound).getLowerBounds();
/*     */     } else {
/*  93 */       lowerBounds = new Type[] { bound };
/*     */     } 
/*  95 */     return new WildcardTypeImpl(new Type[] { Object.class }, lowerBounds);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type canonicalize(Type type) {
/* 104 */     if (type instanceof Class) {
/* 105 */       Class<?> c = (Class)type;
/* 106 */       return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;
/*     */     } 
/* 108 */     if (type instanceof ParameterizedType) {
/* 109 */       ParameterizedType p = (ParameterizedType)type;
/* 110 */       return new ParameterizedTypeImpl(p.getOwnerType(), p
/* 111 */           .getRawType(), p.getActualTypeArguments());
/*     */     } 
/* 113 */     if (type instanceof GenericArrayType) {
/* 114 */       GenericArrayType g = (GenericArrayType)type;
/* 115 */       return new GenericArrayTypeImpl(g.getGenericComponentType());
/*     */     } 
/* 117 */     if (type instanceof WildcardType) {
/* 118 */       WildcardType w = (WildcardType)type;
/* 119 */       return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());
/*     */     } 
/*     */ 
/*     */     
/* 123 */     return type;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Class<?> getRawType(Type type) {
/* 128 */     if (type instanceof Class)
/*     */     {
/* 130 */       return (Class)type;
/*     */     }
/* 132 */     if (type instanceof ParameterizedType) {
/* 133 */       ParameterizedType parameterizedType = (ParameterizedType)type;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 138 */       Type rawType = parameterizedType.getRawType();
/* 139 */       $Gson$Preconditions.checkArgument(rawType instanceof Class);
/* 140 */       return (Class)rawType;
/*     */     } 
/* 142 */     if (type instanceof GenericArrayType) {
/* 143 */       Type componentType = ((GenericArrayType)type).getGenericComponentType();
/* 144 */       return Array.newInstance(getRawType(componentType), 0).getClass();
/*     */     } 
/* 146 */     if (type instanceof TypeVariable)
/*     */     {
/*     */       
/* 149 */       return Object.class;
/*     */     }
/* 151 */     if (type instanceof WildcardType) {
/* 152 */       return getRawType(((WildcardType)type).getUpperBounds()[0]);
/*     */     }
/*     */     
/* 155 */     String className = (type == null) ? "null" : type.getClass().getName();
/* 156 */     throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean equal(Object a, Object b) {
/* 162 */     return (a == b || (a != null && a.equals(b)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean equals(Type a, Type b) {
/* 169 */     if (a == b)
/*     */     {
/* 171 */       return true;
/*     */     }
/* 173 */     if (a instanceof Class)
/*     */     {
/* 175 */       return a.equals(b);
/*     */     }
/* 177 */     if (a instanceof ParameterizedType) {
/* 178 */       if (!(b instanceof ParameterizedType)) {
/* 179 */         return false;
/*     */       }
/*     */ 
/*     */       
/* 183 */       ParameterizedType pa = (ParameterizedType)a;
/* 184 */       ParameterizedType pb = (ParameterizedType)b;
/* 185 */       return (equal(pa.getOwnerType(), pb.getOwnerType()) && pa
/* 186 */         .getRawType().equals(pb.getRawType()) && 
/* 187 */         Arrays.equals((Object[])pa.getActualTypeArguments(), (Object[])pb.getActualTypeArguments()));
/*     */     } 
/* 189 */     if (a instanceof GenericArrayType) {
/* 190 */       if (!(b instanceof GenericArrayType)) {
/* 191 */         return false;
/*     */       }
/*     */       
/* 194 */       GenericArrayType ga = (GenericArrayType)a;
/* 195 */       GenericArrayType gb = (GenericArrayType)b;
/* 196 */       return equals(ga.getGenericComponentType(), gb.getGenericComponentType());
/*     */     } 
/* 198 */     if (a instanceof WildcardType) {
/* 199 */       if (!(b instanceof WildcardType)) {
/* 200 */         return false;
/*     */       }
/*     */       
/* 203 */       WildcardType wa = (WildcardType)a;
/* 204 */       WildcardType wb = (WildcardType)b;
/* 205 */       return (Arrays.equals((Object[])wa.getUpperBounds(), (Object[])wb.getUpperBounds()) && 
/* 206 */         Arrays.equals((Object[])wa.getLowerBounds(), (Object[])wb.getLowerBounds()));
/*     */     } 
/* 208 */     if (a instanceof TypeVariable) {
/* 209 */       if (!(b instanceof TypeVariable)) {
/* 210 */         return false;
/*     */       }
/* 212 */       TypeVariable<?> va = (TypeVariable)a;
/* 213 */       TypeVariable<?> vb = (TypeVariable)b;
/* 214 */       return (va.getGenericDeclaration() == vb.getGenericDeclaration() && va
/* 215 */         .getName().equals(vb.getName()));
/*     */     } 
/*     */ 
/*     */     
/* 219 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   static int hashCodeOrZero(Object o) {
/* 224 */     return (o != null) ? o.hashCode() : 0;
/*     */   }
/*     */   
/*     */   public static String typeToString(Type type) {
/* 228 */     return (type instanceof Class) ? ((Class)type).getName() : type.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve) {
/* 237 */     if (toResolve == rawType) {
/* 238 */       return context;
/*     */     }
/*     */ 
/*     */     
/* 242 */     if (toResolve.isInterface()) {
/* 243 */       Class<?>[] interfaces = rawType.getInterfaces();
/* 244 */       for (int i = 0, length = interfaces.length; i < length; i++) {
/* 245 */         if (interfaces[i] == toResolve)
/* 246 */           return rawType.getGenericInterfaces()[i]; 
/* 247 */         if (toResolve.isAssignableFrom(interfaces[i])) {
/* 248 */           return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 254 */     if (!rawType.isInterface()) {
/* 255 */       while (rawType != Object.class) {
/* 256 */         Class<?> rawSupertype = rawType.getSuperclass();
/* 257 */         if (rawSupertype == toResolve)
/* 258 */           return rawType.getGenericSuperclass(); 
/* 259 */         if (toResolve.isAssignableFrom(rawSupertype)) {
/* 260 */           return getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
/*     */         }
/* 262 */         rawType = rawSupertype;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 267 */     return toResolve;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype) {
/* 278 */     if (context instanceof WildcardType)
/*     */     {
/* 280 */       context = ((WildcardType)context).getUpperBounds()[0];
/*     */     }
/* 282 */     $Gson$Preconditions.checkArgument(supertype.isAssignableFrom(contextRawType));
/* 283 */     return resolve(context, contextRawType, 
/* 284 */         getGenericSupertype(context, contextRawType, supertype));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getArrayComponentType(Type array) {
/* 292 */     return (array instanceof GenericArrayType) ? ((GenericArrayType)array)
/* 293 */       .getGenericComponentType() : ((Class)array)
/* 294 */       .getComponentType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Type getCollectionElementType(Type context, Class<?> contextRawType) {
/* 302 */     Type collectionType = getSupertype(context, contextRawType, Collection.class);
/*     */     
/* 304 */     if (collectionType instanceof WildcardType) {
/* 305 */       collectionType = ((WildcardType)collectionType).getUpperBounds()[0];
/*     */     }
/* 307 */     if (collectionType instanceof ParameterizedType) {
/* 308 */       return ((ParameterizedType)collectionType).getActualTypeArguments()[0];
/*     */     }
/* 310 */     return Object.class;
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
/*     */   public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType) {
/* 323 */     if (context == Properties.class) {
/* 324 */       return new Type[] { String.class, String.class };
/*     */     }
/*     */     
/* 327 */     Type mapType = getSupertype(context, contextRawType, Map.class);
/*     */     
/* 329 */     if (mapType instanceof ParameterizedType) {
/* 330 */       ParameterizedType mapParameterizedType = (ParameterizedType)mapType;
/* 331 */       return mapParameterizedType.getActualTypeArguments();
/*     */     } 
/* 333 */     return new Type[] { Object.class, Object.class };
/*     */   }
/*     */   
/*     */   public static Type resolve(Type context, Class<?> contextRawType, Type toResolve) {
/* 337 */     return resolve(context, contextRawType, toResolve, new HashSet<TypeVariable>());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Type resolve(Type context, Class<?> contextRawType, Type toResolve, Collection<TypeVariable> visitedTypeVariables) {
/* 344 */     while (toResolve instanceof TypeVariable) {
/* 345 */       TypeVariable<?> typeVariable = (TypeVariable)toResolve;
/* 346 */       if (visitedTypeVariables.contains(typeVariable))
/*     */       {
/* 348 */         return toResolve;
/*     */       }
/* 350 */       visitedTypeVariables.add(typeVariable);
/*     */       
/* 352 */       toResolve = resolveTypeVariable(context, contextRawType, typeVariable);
/* 353 */       if (toResolve == typeVariable) {
/* 354 */         return toResolve;
/*     */       }
/*     */     } 
/* 357 */     if (toResolve instanceof Class && ((Class)toResolve).isArray()) {
/* 358 */       Class<?> original = (Class)toResolve;
/* 359 */       Type<?> componentType = original.getComponentType();
/* 360 */       Type newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables);
/* 361 */       return (componentType == newComponentType) ? original : 
/*     */         
/* 363 */         arrayOf(newComponentType);
/*     */     } 
/* 365 */     if (toResolve instanceof GenericArrayType) {
/* 366 */       GenericArrayType original = (GenericArrayType)toResolve;
/* 367 */       Type componentType = original.getGenericComponentType();
/* 368 */       Type newComponentType = resolve(context, contextRawType, componentType, visitedTypeVariables);
/* 369 */       return (componentType == newComponentType) ? original : 
/*     */         
/* 371 */         arrayOf(newComponentType);
/*     */     } 
/* 373 */     if (toResolve instanceof ParameterizedType) {
/* 374 */       ParameterizedType original = (ParameterizedType)toResolve;
/* 375 */       Type ownerType = original.getOwnerType();
/* 376 */       Type newOwnerType = resolve(context, contextRawType, ownerType, visitedTypeVariables);
/* 377 */       boolean changed = (newOwnerType != ownerType);
/*     */       
/* 379 */       Type[] args = original.getActualTypeArguments();
/* 380 */       for (int t = 0, length = args.length; t < length; t++) {
/* 381 */         Type resolvedTypeArgument = resolve(context, contextRawType, args[t], visitedTypeVariables);
/* 382 */         if (resolvedTypeArgument != args[t]) {
/* 383 */           if (!changed) {
/* 384 */             args = (Type[])args.clone();
/* 385 */             changed = true;
/*     */           } 
/* 387 */           args[t] = resolvedTypeArgument;
/*     */         } 
/*     */       } 
/*     */       
/* 391 */       return changed ? 
/* 392 */         newParameterizedTypeWithOwner(newOwnerType, original.getRawType(), args) : original;
/*     */     } 
/*     */     
/* 395 */     if (toResolve instanceof WildcardType) {
/* 396 */       WildcardType original = (WildcardType)toResolve;
/* 397 */       Type[] originalLowerBound = original.getLowerBounds();
/* 398 */       Type[] originalUpperBound = original.getUpperBounds();
/*     */       
/* 400 */       if (originalLowerBound.length == 1) {
/* 401 */         Type lowerBound = resolve(context, contextRawType, originalLowerBound[0], visitedTypeVariables);
/* 402 */         if (lowerBound != originalLowerBound[0]) {
/* 403 */           return supertypeOf(lowerBound);
/*     */         }
/* 405 */       } else if (originalUpperBound.length == 1) {
/* 406 */         Type upperBound = resolve(context, contextRawType, originalUpperBound[0], visitedTypeVariables);
/* 407 */         if (upperBound != originalUpperBound[0]) {
/* 408 */           return subtypeOf(upperBound);
/*     */         }
/*     */       } 
/* 411 */       return original;
/*     */     } 
/*     */     
/* 414 */     return toResolve;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown) {
/* 420 */     Class<?> declaredByRaw = declaringClassOf(unknown);
/*     */ 
/*     */     
/* 423 */     if (declaredByRaw == null) {
/* 424 */       return unknown;
/*     */     }
/*     */     
/* 427 */     Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
/* 428 */     if (declaredBy instanceof ParameterizedType) {
/* 429 */       int index = indexOf((Object[])declaredByRaw.getTypeParameters(), unknown);
/* 430 */       return ((ParameterizedType)declaredBy).getActualTypeArguments()[index];
/*     */     } 
/*     */     
/* 433 */     return unknown;
/*     */   }
/*     */   
/*     */   private static int indexOf(Object[] array, Object toFind) {
/* 437 */     for (int i = 0, length = array.length; i < length; i++) {
/* 438 */       if (toFind.equals(array[i])) {
/* 439 */         return i;
/*     */       }
/*     */     } 
/* 442 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Class<?> declaringClassOf(TypeVariable<?> typeVariable) {
/* 450 */     GenericDeclaration genericDeclaration = (GenericDeclaration)typeVariable.getGenericDeclaration();
/* 451 */     return (genericDeclaration instanceof Class) ? (Class)genericDeclaration : null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void checkNotPrimitive(Type type) {
/* 457 */     $Gson$Preconditions.checkArgument((!(type instanceof Class) || !((Class)type).isPrimitive()));
/*     */   }
/*     */   
/*     */   private static final class ParameterizedTypeImpl implements ParameterizedType, Serializable {
/*     */     private final Type ownerType;
/*     */     private final Type rawType;
/*     */     private final Type[] typeArguments;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     public ParameterizedTypeImpl(Type ownerType, Type rawType, Type... typeArguments) {
/* 467 */       if (rawType instanceof Class) {
/* 468 */         Class<?> rawTypeAsClass = (Class)rawType;
/*     */         
/* 470 */         boolean isStaticOrTopLevelClass = (Modifier.isStatic(rawTypeAsClass.getModifiers()) || rawTypeAsClass.getEnclosingClass() == null);
/* 471 */         $Gson$Preconditions.checkArgument((ownerType != null || isStaticOrTopLevelClass));
/*     */       } 
/*     */       
/* 474 */       this.ownerType = (ownerType == null) ? null : $Gson$Types.canonicalize(ownerType);
/* 475 */       this.rawType = $Gson$Types.canonicalize(rawType);
/* 476 */       this.typeArguments = (Type[])typeArguments.clone();
/* 477 */       for (int t = 0, length = this.typeArguments.length; t < length; t++) {
/* 478 */         $Gson$Preconditions.checkNotNull(this.typeArguments[t]);
/* 479 */         $Gson$Types.checkNotPrimitive(this.typeArguments[t]);
/* 480 */         this.typeArguments[t] = $Gson$Types.canonicalize(this.typeArguments[t]);
/*     */       } 
/*     */     }
/*     */     
/*     */     public Type[] getActualTypeArguments() {
/* 485 */       return (Type[])this.typeArguments.clone();
/*     */     }
/*     */     
/*     */     public Type getRawType() {
/* 489 */       return this.rawType;
/*     */     }
/*     */     
/*     */     public Type getOwnerType() {
/* 493 */       return this.ownerType;
/*     */     }
/*     */     
/*     */     public boolean equals(Object other) {
/* 497 */       return (other instanceof ParameterizedType && 
/* 498 */         $Gson$Types.equals(this, (ParameterizedType)other));
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 502 */       return Arrays.hashCode((Object[])this.typeArguments) ^ this.rawType
/* 503 */         .hashCode() ^ 
/* 504 */         $Gson$Types.hashCodeOrZero(this.ownerType);
/*     */     }
/*     */     
/*     */     public String toString() {
/* 508 */       int length = this.typeArguments.length;
/* 509 */       if (length == 0) {
/* 510 */         return $Gson$Types.typeToString(this.rawType);
/*     */       }
/*     */       
/* 513 */       StringBuilder stringBuilder = new StringBuilder(30 * (length + 1));
/* 514 */       stringBuilder.append($Gson$Types.typeToString(this.rawType)).append("<").append($Gson$Types.typeToString(this.typeArguments[0]));
/* 515 */       for (int i = 1; i < length; i++) {
/* 516 */         stringBuilder.append(", ").append($Gson$Types.typeToString(this.typeArguments[i]));
/*     */       }
/* 518 */       return stringBuilder.append(">").toString();
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class GenericArrayTypeImpl
/*     */     implements GenericArrayType, Serializable {
/*     */     private final Type componentType;
/*     */     private static final long serialVersionUID = 0L;
/*     */     
/*     */     public GenericArrayTypeImpl(Type componentType) {
/* 528 */       this.componentType = $Gson$Types.canonicalize(componentType);
/*     */     }
/*     */     
/*     */     public Type getGenericComponentType() {
/* 532 */       return this.componentType;
/*     */     }
/*     */     
/*     */     public boolean equals(Object o) {
/* 536 */       return (o instanceof GenericArrayType && 
/* 537 */         $Gson$Types.equals(this, (GenericArrayType)o));
/*     */     }
/*     */     
/*     */     public int hashCode() {
/* 541 */       return this.componentType.hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 545 */       return $Gson$Types.typeToString(this.componentType) + "[]";
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class WildcardTypeImpl
/*     */     implements WildcardType, Serializable
/*     */   {
/*     */     private final Type upperBound;
/*     */     
/*     */     private final Type lowerBound;
/*     */     
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     
/*     */     public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds) {
/* 561 */       $Gson$Preconditions.checkArgument((lowerBounds.length <= 1));
/* 562 */       $Gson$Preconditions.checkArgument((upperBounds.length == 1));
/*     */       
/* 564 */       if (lowerBounds.length == 1) {
/* 565 */         $Gson$Preconditions.checkNotNull(lowerBounds[0]);
/* 566 */         $Gson$Types.checkNotPrimitive(lowerBounds[0]);
/* 567 */         $Gson$Preconditions.checkArgument((upperBounds[0] == Object.class));
/* 568 */         this.lowerBound = $Gson$Types.canonicalize(lowerBounds[0]);
/* 569 */         this.upperBound = Object.class;
/*     */       } else {
/*     */         
/* 572 */         $Gson$Preconditions.checkNotNull(upperBounds[0]);
/* 573 */         $Gson$Types.checkNotPrimitive(upperBounds[0]);
/* 574 */         this.lowerBound = null;
/* 575 */         this.upperBound = $Gson$Types.canonicalize(upperBounds[0]);
/*     */       } 
/*     */     }
/*     */     
/*     */     public Type[] getUpperBounds() {
/* 580 */       return new Type[] { this.upperBound };
/*     */     }
/*     */     
/*     */     public Type[] getLowerBounds() {
/* 584 */       (new Type[1])[0] = this.lowerBound; return (this.lowerBound != null) ? new Type[1] : $Gson$Types.EMPTY_TYPE_ARRAY;
/*     */     }
/*     */     
/*     */     public boolean equals(Object other) {
/* 588 */       return (other instanceof WildcardType && 
/* 589 */         $Gson$Types.equals(this, (WildcardType)other));
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 594 */       return ((this.lowerBound != null) ? (31 + this.lowerBound.hashCode()) : 1) ^ 31 + this.upperBound
/* 595 */         .hashCode();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 599 */       if (this.lowerBound != null)
/* 600 */         return "? super " + $Gson$Types.typeToString(this.lowerBound); 
/* 601 */       if (this.upperBound == Object.class) {
/* 602 */         return "?";
/*     */       }
/* 604 */       return "? extends " + $Gson$Types.typeToString(this.upperBound);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\internal\$Gson$Types.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */