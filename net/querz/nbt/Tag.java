/*     */ package net.querz.nbt;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public abstract class Tag<T>
/*     */   implements Cloneable
/*     */ {
/*     */   public static final int DEFAULT_MAX_DEPTH = 512;
/*     */   private static final Map<String, String> ESCAPE_CHARACTERS;
/*     */   
/*     */   static {
/*  46 */     Map<String, String> temp = new HashMap<>();
/*  47 */     temp.put("\\", "\\\\\\\\");
/*  48 */     temp.put("\n", "\\\\n");
/*  49 */     temp.put("\t", "\\\\t");
/*  50 */     temp.put("\r", "\\\\r");
/*  51 */     temp.put("\"", "\\\\\"");
/*  52 */     ESCAPE_CHARACTERS = Collections.unmodifiableMap(temp);
/*     */   }
/*     */   
/*  55 */   private static final Pattern ESCAPE_PATTERN = Pattern.compile("[\\\\\n\t\r\"]");
/*  56 */   private static final Pattern NON_QUOTE_PATTERN = Pattern.compile("[a-zA-Z0-9_\\-+]+");
/*     */ 
/*     */ 
/*     */   
/*     */   private T value;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Tag(T value) {
/*  66 */     setValue(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte getID() {
/*  73 */     return TagFactory.idFromClass(getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected T getValue() {
/*  80 */     return this.value;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setValue(T value) {
/*  89 */     this.value = checkValue(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected T checkValue(T value) {
/*  99 */     return Objects.requireNonNull(value);
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
/*     */   public final void serialize(DataOutputStream dos, int maxDepth) throws IOException {
/* 112 */     serialize(dos, "", maxDepth);
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
/*     */   public final void serialize(DataOutputStream dos, String name, int maxDepth) throws IOException {
/* 125 */     dos.writeByte(getID());
/* 126 */     if (getID() != 0) {
/* 127 */       dos.writeUTF(name);
/*     */     }
/* 129 */     serializeValue(dos, maxDepth);
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
/*     */   public static Tag<?> deserialize(DataInputStream dis, int maxDepth) throws IOException {
/* 143 */     int id = dis.readByte() & 0xFF;
/* 144 */     Tag<?> tag = TagFactory.fromID(id);
/* 145 */     if (id != 0) {
/* 146 */       dis.readUTF();
/* 147 */       tag.deserializeValue(dis, maxDepth);
/*     */     } 
/* 149 */     return tag;
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
/*     */   public final String toString() {
/* 177 */     return toString(512);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString(int maxDepth) {
/* 187 */     return "{\"type\":\"" + getClass().getSimpleName() + "\",\"value\":" + 
/* 188 */       valueToString(maxDepth) + "}";
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
/*     */   
/*     */   public final String toTagString() {
/* 205 */     return toTagString(512);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toTagString(int maxDepth) {
/* 215 */     return valueToTagString(maxDepth);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 236 */     return (other != null && getClass() == other.getClass());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 246 */     return this.value.hashCode();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int decrementMaxDepth(int maxDepth) {
/* 267 */     if (maxDepth < 0)
/* 268 */       throw new IllegalArgumentException("negative maximum depth is not allowed"); 
/* 269 */     if (maxDepth == 0) {
/* 270 */       throw new MaxDepthReachedException("reached maximum depth of NBT structure");
/*     */     }
/* 272 */     return --maxDepth;
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
/*     */   protected static String escapeString(String s, boolean lenient) {
/* 284 */     StringBuffer sb = new StringBuffer();
/* 285 */     Matcher m = ESCAPE_PATTERN.matcher(s);
/* 286 */     while (m.find()) {
/* 287 */       m.appendReplacement(sb, ESCAPE_CHARACTERS.get(m.group()));
/*     */     }
/* 289 */     m.appendTail(sb);
/* 290 */     m = NON_QUOTE_PATTERN.matcher(s);
/* 291 */     if (!lenient || !m.matches()) {
/* 292 */       sb.insert(0, "\"").append("\"");
/*     */     }
/* 294 */     return sb.toString();
/*     */   }
/*     */   
/*     */   public abstract void serializeValue(DataOutputStream paramDataOutputStream, int paramInt) throws IOException;
/*     */   
/*     */   public abstract void deserializeValue(DataInputStream paramDataInputStream, int paramInt) throws IOException;
/*     */   
/*     */   public abstract String valueToString(int paramInt);
/*     */   
/*     */   public abstract String valueToTagString(int paramInt);
/*     */   
/*     */   public abstract Tag<T> clone();
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\Tag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */