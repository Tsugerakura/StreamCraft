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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DiskAttribute
/*     */   extends AbstractDiskHttpData
/*     */   implements Attribute
/*     */ {
/*     */   public static String baseDirectory;
/*     */   public static boolean deleteOnExitTemporaryFile = true;
/*     */   public static final String prefix = "Attr_";
/*     */   public static final String postfix = ".att";
/*     */   
/*     */   public DiskAttribute(String name) {
/*  44 */     this(name, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */   
/*     */   public DiskAttribute(String name, long definedSize) {
/*  48 */     this(name, definedSize, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */   
/*     */   public DiskAttribute(String name, Charset charset) {
/*  52 */     super(name, charset, 0L);
/*     */   }
/*     */   
/*     */   public DiskAttribute(String name, long definedSize, Charset charset) {
/*  56 */     super(name, charset, definedSize);
/*     */   }
/*     */   
/*     */   public DiskAttribute(String name, String value) throws IOException {
/*  60 */     this(name, value, HttpConstants.DEFAULT_CHARSET);
/*     */   }
/*     */   
/*     */   public DiskAttribute(String name, String value, Charset charset) throws IOException {
/*  64 */     super(name, charset, 0L);
/*  65 */     setValue(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData.HttpDataType getHttpDataType() {
/*  70 */     return InterfaceHttpData.HttpDataType.Attribute;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getValue() throws IOException {
/*  75 */     byte[] bytes = get();
/*  76 */     return new String(bytes, getCharset());
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(String value) throws IOException {
/*  81 */     ObjectUtil.checkNotNull(value, "value");
/*  82 */     byte[] bytes = value.getBytes(getCharset());
/*  83 */     checkSize(bytes.length);
/*  84 */     ByteBuf buffer = Unpooled.wrappedBuffer(bytes);
/*  85 */     if (this.definedSize > 0L) {
/*  86 */       this.definedSize = buffer.readableBytes();
/*     */     }
/*  88 */     setContent(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addContent(ByteBuf buffer, boolean last) throws IOException {
/*  93 */     long newDefinedSize = this.size + buffer.readableBytes();
/*  94 */     checkSize(newDefinedSize);
/*  95 */     if (this.definedSize > 0L && this.definedSize < newDefinedSize) {
/*  96 */       this.definedSize = newDefinedSize;
/*     */     }
/*  98 */     super.addContent(buffer, last);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 103 */     return getName().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 108 */     if (!(o instanceof Attribute)) {
/* 109 */       return false;
/*     */     }
/* 111 */     Attribute attribute = (Attribute)o;
/* 112 */     return getName().equalsIgnoreCase(attribute.getName());
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(InterfaceHttpData o) {
/* 117 */     if (!(o instanceof Attribute)) {
/* 118 */       throw new ClassCastException("Cannot compare " + getHttpDataType() + " with " + o
/* 119 */           .getHttpDataType());
/*     */     }
/* 121 */     return compareTo((Attribute)o);
/*     */   }
/*     */   
/*     */   public int compareTo(Attribute o) {
/* 125 */     return getName().compareToIgnoreCase(o.getName());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     try {
/* 131 */       return getName() + '=' + getValue();
/* 132 */     } catch (IOException e) {
/* 133 */       return getName() + '=' + e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean deleteOnExit() {
/* 139 */     return deleteOnExitTemporaryFile;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getBaseDirectory() {
/* 144 */     return baseDirectory;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getDiskFilename() {
/* 149 */     return getName() + ".att";
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getPostfix() {
/* 154 */     return ".att";
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getPrefix() {
/* 159 */     return "Attr_";
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute copy() {
/* 164 */     ByteBuf content = content();
/* 165 */     return replace((content != null) ? content.copy() : null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute duplicate() {
/* 170 */     ByteBuf content = content();
/* 171 */     return replace((content != null) ? content.duplicate() : null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute retainedDuplicate() {
/* 176 */     ByteBuf content = content();
/* 177 */     if (content != null) {
/* 178 */       content = content.retainedDuplicate();
/* 179 */       boolean success = false;
/*     */       try {
/* 181 */         Attribute duplicate = replace(content);
/* 182 */         success = true;
/* 183 */         return duplicate;
/*     */       } finally {
/* 185 */         if (!success) {
/* 186 */           content.release();
/*     */         }
/*     */       } 
/*     */     } 
/* 190 */     return replace((ByteBuf)null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Attribute replace(ByteBuf content) {
/* 196 */     DiskAttribute attr = new DiskAttribute(getName());
/* 197 */     attr.setCharset(getCharset());
/* 198 */     if (content != null) {
/*     */       try {
/* 200 */         attr.setContent(content);
/* 201 */       } catch (IOException e) {
/* 202 */         throw new ChannelException(e);
/*     */       } 
/*     */     }
/* 205 */     return attr;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute retain(int increment) {
/* 210 */     super.retain(increment);
/* 211 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute retain() {
/* 216 */     super.retain();
/* 217 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute touch() {
/* 222 */     super.touch();
/* 223 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Attribute touch(Object hint) {
/* 228 */     super.touch(hint);
/* 229 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\DiskAttribute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */