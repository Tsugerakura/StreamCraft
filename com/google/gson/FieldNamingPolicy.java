/*     */ package com.google.gson;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Locale;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum FieldNamingPolicy
/*     */   implements FieldNamingStrategy
/*     */ {
/*  37 */   IDENTITY {
/*     */     public String translateName(Field f) {
/*  39 */       return f.getName();
/*     */     }
/*     */   },
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
/*  53 */   UPPER_CAMEL_CASE {
/*     */     public String translateName(Field f) {
/*  55 */       return null.upperCaseFirstLetter(f.getName());
/*     */     }
/*     */   },
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
/*  72 */   UPPER_CAMEL_CASE_WITH_SPACES {
/*     */     public String translateName(Field f) {
/*  74 */       return null.upperCaseFirstLetter(null.separateCamelCase(f.getName(), " "));
/*     */     }
/*     */   },
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
/*  90 */   LOWER_CASE_WITH_UNDERSCORES {
/*     */     public String translateName(Field f) {
/*  92 */       return null.separateCamelCase(f.getName(), "_").toLowerCase(Locale.ENGLISH);
/*     */     }
/*     */   },
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
/* 113 */   LOWER_CASE_WITH_DASHES {
/*     */     public String translateName(Field f) {
/* 115 */       return null.separateCamelCase(f.getName(), "-").toLowerCase(Locale.ENGLISH);
/*     */     }
/*     */   },
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
/* 136 */   LOWER_CASE_WITH_DOTS {
/*     */     public String translateName(Field f) {
/* 138 */       return null.separateCamelCase(f.getName(), ".").toLowerCase(Locale.ENGLISH);
/*     */     }
/*     */   };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String separateCamelCase(String name, String separator) {
/* 147 */     StringBuilder translation = new StringBuilder();
/* 148 */     for (int i = 0, length = name.length(); i < length; i++) {
/* 149 */       char character = name.charAt(i);
/* 150 */       if (Character.isUpperCase(character) && translation.length() != 0) {
/* 151 */         translation.append(separator);
/*     */       }
/* 153 */       translation.append(character);
/*     */     } 
/* 155 */     return translation.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String upperCaseFirstLetter(String name) {
/* 162 */     StringBuilder fieldNameBuilder = new StringBuilder();
/* 163 */     int index = 0;
/* 164 */     char firstCharacter = name.charAt(index);
/* 165 */     int length = name.length();
/*     */     
/* 167 */     while (index < length - 1 && 
/* 168 */       !Character.isLetter(firstCharacter)) {
/*     */ 
/*     */ 
/*     */       
/* 172 */       fieldNameBuilder.append(firstCharacter);
/* 173 */       firstCharacter = name.charAt(++index);
/*     */     } 
/*     */     
/* 176 */     if (!Character.isUpperCase(firstCharacter)) {
/* 177 */       String modifiedTarget = modifyString(Character.toUpperCase(firstCharacter), name, ++index);
/* 178 */       return fieldNameBuilder.append(modifiedTarget).toString();
/*     */     } 
/* 180 */     return name;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String modifyString(char firstCharacter, String srcString, int indexOfSubstring) {
/* 185 */     return (indexOfSubstring < srcString.length()) ? (firstCharacter + srcString
/* 186 */       .substring(indexOfSubstring)) : 
/* 187 */       String.valueOf(firstCharacter);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\FieldNamingPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */