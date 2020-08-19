/*    */ package com.fasterxml.jackson.core.json;
/*    */ 
/*    */ import com.fasterxml.jackson.core.Version;
/*    */ import com.fasterxml.jackson.core.Versioned;
/*    */ import com.fasterxml.jackson.core.util.VersionUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class PackageVersion
/*    */   implements Versioned
/*    */ {
/* 13 */   public static final Version VERSION = VersionUtil.parseVersion("2.10.0", "com.fasterxml.jackson.core", "jackson-core");
/*    */ 
/*    */ 
/*    */   
/*    */   public Version version() {
/* 18 */     return VERSION;
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\json\PackageVersion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */