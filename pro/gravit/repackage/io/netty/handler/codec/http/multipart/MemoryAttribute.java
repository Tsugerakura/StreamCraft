/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBufHolder;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.channel.ChannelException;
/*     */ import pro.gravit.repackage.io.netty.handler.codec.http.HttpConstants;
/*     */ import pro.gravit.repackage.io.netty.util.ReferenceCounted;
/*     */ import pro.gravit.repackage.io.netty.util.internal.ObjectUtil;
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
/*     */ public class MemoryAttribute
/*     */   extends AbstractMemoryHttpData
/*     */   implements Attribute
/*     */ {
/*     */   public MemoryAttribute(String name) {
/*  34 */     this(name, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */   
/*     */   public MemoryAttribute(String name, long definedSize) {
/*  38 */     this(name, definedSize, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */   
/*     */   public MemoryAttribute(String name, Charset charset) {
/*  42 */     super(name, charset, 0L);
/*     */   }
/*     */   
/*     */   public MemoryAttribute(String name, long definedSize, Charset charset) {
/*  46 */     super(name, charset, definedSize);
/*     */   }
/*     */   
/*     */   public MemoryAttribute(String name, String value) throws IOException {
/*  50 */     this(name, value, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */   
/*     */   public MemoryAttribute(String name, String value, Charset charset) throws IOException {
/*  54 */     super(name, charset, 0L);
/*  55 */     setValue(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData.HttpDataType getHttpDataType() {
/*  60 */     return InterfaceHttpData.HttpDataType.Attribute;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getValue() {
/*  65 */     return getByteBuf().toString(getCharset());
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(String value) throws IOException {
/*  70 */     ObjectUtil.checkNotNull(value, "value");
/*  71 */     byte[] bytes = value.getBytes(getCharset());
/*  72 */     checkSize(bytes.length);
/*  73 */     ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
/*  74 */     if (this.definedSize > 0L) {
/*  75 */       this.definedSize = buffer.readableBytes();
/*     */     }
/*  77 */     setContent(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addContent(ByteBuf buffer, boolean last) throws IOException {
/*  82 */     int localsize = buffer.readableBytes();
/*  83 */     checkSize(this.size + localsize);
/*  84 */     if (this.definedSize > 0L && this.definedSize < this.size + localsize) {
/*  85 */       this.definedSize = this.size + localsize;
/*     */     }
/*  87 */     super.addContent(buffer, last);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  92 */     return getName().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  97 */     if (!(o instanceof Attribute)) {
/*  98 */       return false;
/*     */     }
/* 100 */     Attribute attribute = (Attribute)o;
/* 101 */     return getName().equalsIgnoreCase(attribute.getName());
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(InterfaceHttpData other) {
/* 106 */     if (!(other instanceof Attribute)) {
/* 107 */       throw new ClassCastException("Cannot compare " + getHttpDataType() + " with " + other
/* 108 */           .getHttpDataType());
/*     */     }
/* 110 */     return compareTo((Attribute)other);
/*     */   }
/*     */   
/*     */   public int compareTo(Attribute o) {
/* 114 */     return getName().compareToIgnoreCase(o.getName());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 119 */     return getName() + '=' + getValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute copy() {
/* 124 */     ByteBuf content = content();
/* 125 */     return replace((content != null) ? content.copy() : null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute duplicate() {
/* 130 */     ByteBuf content = content();
/* 131 */     return replace((content != null) ? content.duplicate() : null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute retainedDuplicate() {
/* 136 */     ByteBuf content = content();
/* 137 */     if (content != null) {
/* 138 */       content = content.retainedDuplicate();
/* 139 */       boolean success = false;
/*     */       try {
/* 141 */         Attribute duplicate = replace(content);
/* 142 */         success = true;
/* 143 */         return duplicate;
/*     */       } finally {
/* 145 */         if (!success) {
/* 146 */           content.release();
/*     */         }
/*     */       } 
/*     */     } 
/* 150 */     return replace((ByteBuf)null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Attribute replace(ByteBuf content) {
/* 156 */     MemoryAttribute attr = new MemoryAttribute(getName());
/* 157 */     attr.setCharset(getCharset());
/* 158 */     if (content != null) {
/*     */       try {
/* 160 */         attr.setContent(content);
/* 161 */       } catch (IOException e) {
/* 162 */         throw new ChannelException(e);
/*     */       } 
/*     */     }
/* 165 */     return attr;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute retain() {
/* 170 */     super.retain();
/* 171 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute retain(int increment) {
/* 176 */     super.retain(increment);
/* 177 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute touch() {
/* 182 */     super.touch();
/* 183 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute touch(Object hint) {
/* 188 */     super.touch(hint);
/* 189 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\MemoryAttribute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */