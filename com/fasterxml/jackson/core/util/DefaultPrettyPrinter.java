/*     */ package com.fasterxml.jackson.core.util;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.PrettyPrinter;
/*     */ import com.fasterxml.jackson.core.SerializableString;
/*     */ import com.fasterxml.jackson.core.io.SerializedString;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
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
/*     */ public class DefaultPrettyPrinter
/*     */   implements PrettyPrinter, Instantiatable<DefaultPrettyPrinter>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  28 */   public static final SerializedString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(" ");
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
/*  52 */   protected Indenter _arrayIndenter = FixedSpaceIndenter.instance;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  60 */   protected Indenter _objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected final SerializableString _rootSeparator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean _spacesInObjectEntries = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected transient int _nesting;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Separators _separators;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String _objectFieldValueSeparatorWithSpaces;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter() {
/* 101 */     this((SerializableString)DEFAULT_ROOT_VALUE_SEPARATOR);
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
/*     */   public DefaultPrettyPrinter(String rootSeparator) {
/* 116 */     this((rootSeparator == null) ? null : (SerializableString)new SerializedString(rootSeparator));
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
/*     */   public DefaultPrettyPrinter(SerializableString rootSeparator) {
/* 128 */     this._rootSeparator = rootSeparator;
/* 129 */     withSeparators(DEFAULT_SEPARATORS);
/*     */   }
/*     */   
/*     */   public DefaultPrettyPrinter(DefaultPrettyPrinter base) {
/* 133 */     this(base, base._rootSeparator);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter(DefaultPrettyPrinter base, SerializableString rootSeparator) {
/* 139 */     this._arrayIndenter = base._arrayIndenter;
/* 140 */     this._objectIndenter = base._objectIndenter;
/* 141 */     this._spacesInObjectEntries = base._spacesInObjectEntries;
/* 142 */     this._nesting = base._nesting;
/*     */     
/* 144 */     this._separators = base._separators;
/* 145 */     this._objectFieldValueSeparatorWithSpaces = base._objectFieldValueSeparatorWithSpaces;
/*     */     
/* 147 */     this._rootSeparator = rootSeparator;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter withRootSeparator(SerializableString rootSeparator) {
/* 152 */     if (this._rootSeparator == rootSeparator || (rootSeparator != null && rootSeparator
/* 153 */       .equals(this._rootSeparator))) {
/* 154 */       return this;
/*     */     }
/* 156 */     return new DefaultPrettyPrinter(this, rootSeparator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter withRootSeparator(String rootSeparator) {
/* 163 */     return withRootSeparator((rootSeparator == null) ? null : (SerializableString)new SerializedString(rootSeparator));
/*     */   }
/*     */   
/*     */   public void indentArraysWith(Indenter i) {
/* 167 */     this._arrayIndenter = (i == null) ? NopIndenter.instance : i;
/*     */   }
/*     */   
/*     */   public void indentObjectsWith(Indenter i) {
/* 171 */     this._objectIndenter = (i == null) ? NopIndenter.instance : i;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter withArrayIndenter(Indenter i) {
/* 178 */     if (i == null) {
/* 179 */       i = NopIndenter.instance;
/*     */     }
/* 181 */     if (this._arrayIndenter == i) {
/* 182 */       return this;
/*     */     }
/* 184 */     DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
/* 185 */     pp._arrayIndenter = i;
/* 186 */     return pp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter withObjectIndenter(Indenter i) {
/* 193 */     if (i == null) {
/* 194 */       i = NopIndenter.instance;
/*     */     }
/* 196 */     if (this._objectIndenter == i) {
/* 197 */       return this;
/*     */     }
/* 199 */     DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
/* 200 */     pp._objectIndenter = i;
/* 201 */     return pp;
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
/*     */   public DefaultPrettyPrinter withSpacesInObjectEntries() {
/* 213 */     return _withSpaces(true);
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
/*     */   public DefaultPrettyPrinter withoutSpacesInObjectEntries() {
/* 225 */     return _withSpaces(false);
/*     */   }
/*     */ 
/*     */   
/*     */   protected DefaultPrettyPrinter _withSpaces(boolean state) {
/* 230 */     if (this._spacesInObjectEntries == state) {
/* 231 */       return this;
/*     */     }
/* 233 */     DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
/* 234 */     pp._spacesInObjectEntries = state;
/* 235 */     return pp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter withSeparators(Separators separators) {
/* 242 */     this._separators = separators;
/* 243 */     this._objectFieldValueSeparatorWithSpaces = " " + separators.getObjectFieldValueSeparator() + " ";
/* 244 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultPrettyPrinter createInstance() {
/* 255 */     if (getClass() != DefaultPrettyPrinter.class) {
/* 256 */       throw new IllegalStateException("Failed `createInstance()`: " + getClass().getName() + " does not override method; it has to");
/*     */     }
/*     */     
/* 259 */     return new DefaultPrettyPrinter(this);
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
/*     */   public void writeRootValueSeparator(JsonGenerator g) throws IOException {
/* 271 */     if (this._rootSeparator != null) {
/* 272 */       g.writeRaw(this._rootSeparator);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeStartObject(JsonGenerator g) throws IOException {
/* 279 */     g.writeRaw('{');
/* 280 */     if (!this._objectIndenter.isInline()) {
/* 281 */       this._nesting++;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void beforeObjectEntries(JsonGenerator g) throws IOException {
/* 288 */     this._objectIndenter.writeIndentation(g, this._nesting);
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
/*     */   public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
/* 303 */     if (this._spacesInObjectEntries) {
/* 304 */       g.writeRaw(this._objectFieldValueSeparatorWithSpaces);
/*     */     } else {
/* 306 */       g.writeRaw(this._separators.getObjectFieldValueSeparator());
/*     */     } 
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
/*     */   public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
/* 322 */     g.writeRaw(this._separators.getObjectEntrySeparator());
/* 323 */     this._objectIndenter.writeIndentation(g, this._nesting);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
/* 329 */     if (!this._objectIndenter.isInline()) {
/* 330 */       this._nesting--;
/*     */     }
/* 332 */     if (nrOfEntries > 0) {
/* 333 */       this._objectIndenter.writeIndentation(g, this._nesting);
/*     */     } else {
/* 335 */       g.writeRaw(' ');
/*     */     } 
/* 337 */     g.writeRaw('}');
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeStartArray(JsonGenerator g) throws IOException {
/* 343 */     if (!this._arrayIndenter.isInline()) {
/* 344 */       this._nesting++;
/*     */     }
/* 346 */     g.writeRaw('[');
/*     */   }
/*     */ 
/*     */   
/*     */   public void beforeArrayValues(JsonGenerator g) throws IOException {
/* 351 */     this._arrayIndenter.writeIndentation(g, this._nesting);
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
/*     */   public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
/* 366 */     g.writeRaw(this._separators.getArrayValueSeparator());
/* 367 */     this._arrayIndenter.writeIndentation(g, this._nesting);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
/* 373 */     if (!this._arrayIndenter.isInline()) {
/* 374 */       this._nesting--;
/*     */     }
/* 376 */     if (nrOfValues > 0) {
/* 377 */       this._arrayIndenter.writeIndentation(g, this._nesting);
/*     */     } else {
/* 379 */       g.writeRaw(' ');
/*     */     } 
/* 381 */     g.writeRaw(']');
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface Indenter
/*     */   {
/*     */     void writeIndentation(JsonGenerator param1JsonGenerator, int param1Int) throws IOException;
/*     */ 
/*     */     
/*     */     boolean isInline();
/*     */   }
/*     */   
/*     */   public static class NopIndenter
/*     */     implements Indenter, Serializable
/*     */   {
/* 396 */     public static final NopIndenter instance = new NopIndenter();
/*     */ 
/*     */     
/*     */     public void writeIndentation(JsonGenerator g, int level) throws IOException {}
/*     */     
/*     */     public boolean isInline() {
/* 402 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class FixedSpaceIndenter
/*     */     extends NopIndenter
/*     */   {
/* 412 */     public static final FixedSpaceIndenter instance = new FixedSpaceIndenter();
/*     */ 
/*     */ 
/*     */     
/*     */     public void writeIndentation(JsonGenerator g, int level) throws IOException {
/* 417 */       g.writeRaw(' ');
/*     */     }
/*     */     
/*     */     public boolean isInline() {
/* 421 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\cor\\util\DefaultPrettyPrinter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */