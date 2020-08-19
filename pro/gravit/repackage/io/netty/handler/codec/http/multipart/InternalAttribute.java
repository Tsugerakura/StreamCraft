/*     */ package pro.gravit.repackage.io.netty.handler.codec.http.multipart;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import pro.gravit.repackage.io.netty.buffer.ByteBuf;
/*     */ import pro.gravit.repackage.io.netty.buffer.Unpooled;
/*     */ import pro.gravit.repackage.io.netty.util.AbstractReferenceCounted;
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
/*     */ final class InternalAttribute
/*     */   extends AbstractReferenceCounted
/*     */   implements InterfaceHttpData
/*     */ {
/*  32 */   private final List<ByteBuf> value = new ArrayList<ByteBuf>();
/*     */   private final Charset charset;
/*     */   private int size;
/*     */   
/*     */   InternalAttribute(Charset charset) {
/*  37 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData.HttpDataType getHttpDataType() {
/*  42 */     return InterfaceHttpData.HttpDataType.InternalAttribute;
/*     */   }
/*     */   
/*     */   public void addValue(String value) {
/*  46 */     ObjectUtil.checkNotNull(value, "value");
/*  47 */     ByteBuf buf = Unpooled.copiedBuffer(value, this.charset);
/*  48 */     this.value.add(buf);
/*  49 */     this.size += buf.readableBytes();
/*     */   }
/*     */   
/*     */   public void addValue(String value, int rank) {
/*  53 */     ObjectUtil.checkNotNull(value, "value");
/*  54 */     ByteBuf buf = Unpooled.copiedBuffer(value, this.charset);
/*  55 */     this.value.add(rank, buf);
/*  56 */     this.size += buf.readableBytes();
/*     */   }
/*     */   
/*     */   public void setValue(String value, int rank) {
/*  60 */     ObjectUtil.checkNotNull(value, "value");
/*  61 */     ByteBuf buf = Unpooled.copiedBuffer(value, this.charset);
/*  62 */     ByteBuf old = this.value.set(rank, buf);
/*  63 */     if (old != null) {
/*  64 */       this.size -= old.readableBytes();
/*  65 */       old.release();
/*     */     } 
/*  67 */     this.size += buf.readableBytes();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  72 */     return getName().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/*  77 */     if (!(o instanceof InternalAttribute)) {
/*  78 */       return false;
/*     */     }
/*  80 */     InternalAttribute attribute = (InternalAttribute)o;
/*  81 */     return getName().equalsIgnoreCase(attribute.getName());
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTo(InterfaceHttpData o) {
/*  86 */     if (!(o instanceof InternalAttribute)) {
/*  87 */       throw new ClassCastException("Cannot compare " + getHttpDataType() + " with " + o
/*  88 */           .getHttpDataType());
/*     */     }
/*  90 */     return compareTo((InternalAttribute)o);
/*     */   }
/*     */   
/*     */   public int compareTo(InternalAttribute o) {
/*  94 */     return getName().compareToIgnoreCase(o.getName());
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  99 */     StringBuilder result = new StringBuilder();
/* 100 */     for (ByteBuf elt : this.value) {
/* 101 */       result.append(elt.toString(this.charset));
/*     */     }
/* 103 */     return result.toString();
/*     */   }
/*     */   
/*     */   public int size() {
/* 107 */     return this.size;
/*     */   }
/*     */   
/*     */   public ByteBuf toByteBuf() {
/* 111 */     return (ByteBuf)Unpooled.compositeBuffer().addComponents(this.value).writerIndex(size()).readerIndex(0);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 116 */     return "InternalAttribute";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void deallocate() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public InterfaceHttpData retain() {
/* 126 */     for (ByteBuf buf : this.value) {
/* 127 */       buf.retain();
/*     */     }
/* 129 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData retain(int increment) {
/* 134 */     for (ByteBuf buf : this.value) {
/* 135 */       buf.retain(increment);
/*     */     }
/* 137 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData touch() {
/* 142 */     for (ByteBuf buf : this.value) {
/* 143 */       buf.touch();
/*     */     }
/* 145 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public InterfaceHttpData touch(Object hint) {
/* 150 */     for (ByteBuf buf : this.value) {
/* 151 */       buf.touch(hint);
/*     */     }
/* 153 */     return this;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\InternalAttribute.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */