/*     */ package pro.gravit.repackage.io.netty.handler.codec.spdy;
/*     */ 
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import pro.gravit.repackage.io.netty.util.internal.StringUtil;
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
/*     */ public class DefaultSpdySettingsFrame
/*     */   implements SpdySettingsFrame
/*     */ {
/*     */   private boolean clear;
/*  30 */   private final Map<Integer, Setting> settingsMap = new TreeMap<Integer, Setting>();
/*     */ 
/*     */   
/*     */   public Set<Integer> ids() {
/*  34 */     return this.settingsMap.keySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSet(int id) {
/*  39 */     return this.settingsMap.containsKey(Integer.valueOf(id));
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValue(int id) {
/*  44 */     Setting setting = this.settingsMap.get(Integer.valueOf(id));
/*  45 */     return (setting != null) ? setting.getValue() : -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySettingsFrame setValue(int id, int value) {
/*  50 */     return setValue(id, value, false, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySettingsFrame setValue(int id, int value, boolean persistValue, boolean persisted) {
/*  55 */     if (id < 0 || id > 16777215) {
/*  56 */       throw new IllegalArgumentException("Setting ID is not valid: " + id);
/*     */     }
/*  58 */     Integer key = Integer.valueOf(id);
/*  59 */     Setting setting = this.settingsMap.get(key);
/*  60 */     if (setting != null) {
/*  61 */       setting.setValue(value);
/*  62 */       setting.setPersist(persistValue);
/*  63 */       setting.setPersisted(persisted);
/*     */     } else {
/*  65 */       this.settingsMap.put(key, new Setting(value, persistValue, persisted));
/*     */     } 
/*  67 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySettingsFrame removeValue(int id) {
/*  72 */     this.settingsMap.remove(Integer.valueOf(id));
/*  73 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPersistValue(int id) {
/*  78 */     Setting setting = this.settingsMap.get(Integer.valueOf(id));
/*  79 */     return (setting != null && setting.isPersist());
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySettingsFrame setPersistValue(int id, boolean persistValue) {
/*  84 */     Setting setting = this.settingsMap.get(Integer.valueOf(id));
/*  85 */     if (setting != null) {
/*  86 */       setting.setPersist(persistValue);
/*     */     }
/*  88 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPersisted(int id) {
/*  93 */     Setting setting = this.settingsMap.get(Integer.valueOf(id));
/*  94 */     return (setting != null && setting.isPersisted());
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySettingsFrame setPersisted(int id, boolean persisted) {
/*  99 */     Setting setting = this.settingsMap.get(Integer.valueOf(id));
/* 100 */     if (setting != null) {
/* 101 */       setting.setPersisted(persisted);
/*     */     }
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean clearPreviouslyPersistedSettings() {
/* 108 */     return this.clear;
/*     */   }
/*     */ 
/*     */   
/*     */   public SpdySettingsFrame setClearPreviouslyPersistedSettings(boolean clear) {
/* 113 */     this.clear = clear;
/* 114 */     return this;
/*     */   }
/*     */   
/*     */   private Set<Map.Entry<Integer, Setting>> getSettings() {
/* 118 */     return this.settingsMap.entrySet();
/*     */   }
/*     */   
/*     */   private void appendSettings(StringBuilder buf) {
/* 122 */     for (Map.Entry<Integer, Setting> e : getSettings()) {
/* 123 */       Setting setting = e.getValue();
/* 124 */       buf.append("--> ");
/* 125 */       buf.append(e.getKey());
/* 126 */       buf.append(':');
/* 127 */       buf.append(setting.getValue());
/* 128 */       buf.append(" (persist value: ");
/* 129 */       buf.append(setting.isPersist());
/* 130 */       buf.append("; persisted: ");
/* 131 */       buf.append(setting.isPersisted());
/* 132 */       buf.append(')');
/* 133 */       buf.append(StringUtil.NEWLINE);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 141 */     StringBuilder buf = (new StringBuilder()).append(StringUtil.simpleClassName(this)).append(StringUtil.NEWLINE);
/* 142 */     appendSettings(buf);
/*     */     
/* 144 */     buf.setLength(buf.length() - StringUtil.NEWLINE.length());
/* 145 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private static final class Setting
/*     */   {
/*     */     private int value;
/*     */     private boolean persist;
/*     */     private boolean persisted;
/*     */     
/*     */     Setting(int value, boolean persist, boolean persisted) {
/* 155 */       this.value = value;
/* 156 */       this.persist = persist;
/* 157 */       this.persisted = persisted;
/*     */     }
/*     */     
/*     */     int getValue() {
/* 161 */       return this.value;
/*     */     }
/*     */     
/*     */     void setValue(int value) {
/* 165 */       this.value = value;
/*     */     }
/*     */     
/*     */     boolean isPersist() {
/* 169 */       return this.persist;
/*     */     }
/*     */     
/*     */     void setPersist(boolean persist) {
/* 173 */       this.persist = persist;
/*     */     }
/*     */     
/*     */     boolean isPersisted() {
/* 177 */       return this.persisted;
/*     */     }
/*     */     
/*     */     void setPersisted(boolean persisted) {
/* 181 */       this.persisted = persisted;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\spdy\DefaultSpdySettingsFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */