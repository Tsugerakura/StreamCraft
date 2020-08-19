/*     */ package com.fasterxml.jackson.core.json;
/*     */ 
/*     */ import com.fasterxml.jackson.core.JsonGenerationException;
/*     */ import com.fasterxml.jackson.core.JsonGenerator;
/*     */ import com.fasterxml.jackson.core.JsonProcessingException;
/*     */ import com.fasterxml.jackson.core.JsonStreamContext;
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
/*     */ public class JsonWriteContext
/*     */   extends JsonStreamContext
/*     */ {
/*     */   public static final int STATUS_OK_AS_IS = 0;
/*     */   public static final int STATUS_OK_AFTER_COMMA = 1;
/*     */   public static final int STATUS_OK_AFTER_COLON = 2;
/*     */   public static final int STATUS_OK_AFTER_SPACE = 3;
/*     */   public static final int STATUS_EXPECT_VALUE = 4;
/*     */   public static final int STATUS_EXPECT_NAME = 5;
/*     */   protected final JsonWriteContext _parent;
/*     */   protected DupDetector _dups;
/*     */   protected JsonWriteContext _child;
/*     */   protected String _currentName;
/*     */   protected Object _currentValue;
/*     */   protected boolean _gotName;
/*     */   
/*     */   protected JsonWriteContext(int type, JsonWriteContext parent, DupDetector dups) {
/*  70 */     this._type = type;
/*  71 */     this._parent = parent;
/*  72 */     this._dups = dups;
/*  73 */     this._index = -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected JsonWriteContext(int type, JsonWriteContext parent, DupDetector dups, Object currValue) {
/*  80 */     this._type = type;
/*  81 */     this._parent = parent;
/*  82 */     this._dups = dups;
/*  83 */     this._index = -1;
/*  84 */     this._currentValue = currValue;
/*     */   }
/*     */   
/*     */   protected JsonWriteContext reset(int type) {
/*  88 */     this._type = type;
/*  89 */     this._index = -1;
/*  90 */     this._currentName = null;
/*  91 */     this._gotName = false;
/*  92 */     this._currentValue = null;
/*  93 */     if (this._dups != null) this._dups.reset(); 
/*  94 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected JsonWriteContext reset(int type, Object currValue) {
/*  99 */     this._type = type;
/* 100 */     this._index = -1;
/* 101 */     this._currentName = null;
/* 102 */     this._gotName = false;
/* 103 */     this._currentValue = currValue;
/* 104 */     if (this._dups != null) this._dups.reset(); 
/* 105 */     return this;
/*     */   }
/*     */   
/*     */   public JsonWriteContext withDupDetector(DupDetector dups) {
/* 109 */     this._dups = dups;
/* 110 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getCurrentValue() {
/* 115 */     return this._currentValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setCurrentValue(Object v) {
/* 120 */     this._currentValue = v;
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
/*     */   @Deprecated
/*     */   public static JsonWriteContext createRootContext() {
/* 133 */     return createRootContext((DupDetector)null);
/*     */   }
/*     */   public static JsonWriteContext createRootContext(DupDetector dd) {
/* 136 */     return new JsonWriteContext(0, null, dd);
/*     */   }
/*     */   
/*     */   public JsonWriteContext createChildArrayContext() {
/* 140 */     JsonWriteContext ctxt = this._child;
/* 141 */     if (ctxt == null) {
/* 142 */       this
/* 143 */         ._child = ctxt = new JsonWriteContext(1, this, (this._dups == null) ? null : this._dups.child());
/* 144 */       return ctxt;
/*     */     } 
/* 146 */     return ctxt.reset(1);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonWriteContext createChildArrayContext(Object currValue) {
/* 151 */     JsonWriteContext ctxt = this._child;
/* 152 */     if (ctxt == null) {
/* 153 */       this
/* 154 */         ._child = ctxt = new JsonWriteContext(1, this, (this._dups == null) ? null : this._dups.child(), currValue);
/* 155 */       return ctxt;
/*     */     } 
/* 157 */     return ctxt.reset(1, currValue);
/*     */   }
/*     */   
/*     */   public JsonWriteContext createChildObjectContext() {
/* 161 */     JsonWriteContext ctxt = this._child;
/* 162 */     if (ctxt == null) {
/* 163 */       this
/* 164 */         ._child = ctxt = new JsonWriteContext(2, this, (this._dups == null) ? null : this._dups.child());
/* 165 */       return ctxt;
/*     */     } 
/* 167 */     return ctxt.reset(2);
/*     */   }
/*     */ 
/*     */   
/*     */   public JsonWriteContext createChildObjectContext(Object currValue) {
/* 172 */     JsonWriteContext ctxt = this._child;
/* 173 */     if (ctxt == null) {
/* 174 */       this
/* 175 */         ._child = ctxt = new JsonWriteContext(2, this, (this._dups == null) ? null : this._dups.child(), currValue);
/* 176 */       return ctxt;
/*     */     } 
/* 178 */     return ctxt.reset(2, currValue);
/*     */   }
/*     */   
/* 181 */   public final JsonWriteContext getParent() { return this._parent; } public final String getCurrentName() {
/* 182 */     return this._currentName;
/*     */   } public boolean hasCurrentName() {
/* 184 */     return (this._currentName != null);
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
/*     */   public JsonWriteContext clearAndGetParent() {
/* 197 */     this._currentValue = null;
/*     */     
/* 199 */     return this._parent;
/*     */   }
/*     */   
/*     */   public DupDetector getDupDetector() {
/* 203 */     return this._dups;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int writeFieldName(String name) throws JsonProcessingException {
/* 212 */     if (this._type != 2 || this._gotName) {
/* 213 */       return 4;
/*     */     }
/* 215 */     this._gotName = true;
/* 216 */     this._currentName = name;
/* 217 */     if (this._dups != null) _checkDup(this._dups, name); 
/* 218 */     return (this._index < 0) ? 0 : 1;
/*     */   }
/*     */   
/*     */   private final void _checkDup(DupDetector dd, String name) throws JsonProcessingException {
/* 222 */     if (dd.isDup(name)) {
/* 223 */       Object src = dd.getSource();
/* 224 */       throw new JsonGenerationException("Duplicate field '" + name + "'", (src instanceof JsonGenerator) ? (JsonGenerator)src : null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int writeValue() {
/* 231 */     if (this._type == 2) {
/* 232 */       if (!this._gotName) {
/* 233 */         return 5;
/*     */       }
/* 235 */       this._gotName = false;
/* 236 */       this._index++;
/* 237 */       return 2;
/*     */     } 
/*     */ 
/*     */     
/* 241 */     if (this._type == 1) {
/* 242 */       int ix = this._index;
/* 243 */       this._index++;
/* 244 */       return (ix < 0) ? 0 : 1;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 249 */     this._index++;
/* 250 */     return (this._index == 0) ? 0 : 3;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\JsonWriteContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */