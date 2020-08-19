/*     */ package pro.gravit.repackage.io.netty.handler.ssl;
/*     */ 
/*     */ import java.security.Principal;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.X509ExtendedKeyManager;
/*     */ import javax.net.ssl.X509KeyManager;
/*     */ import javax.security.auth.x500.X500Principal;
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
/*     */ final class OpenSslKeyMaterialManager
/*     */ {
/*     */   static final String KEY_TYPE_RSA = "RSA";
/*     */   static final String KEY_TYPE_DH_RSA = "DH_RSA";
/*     */   static final String KEY_TYPE_EC = "EC";
/*     */   static final String KEY_TYPE_EC_EC = "EC_EC";
/*     */   static final String KEY_TYPE_EC_RSA = "EC_RSA";
/*  49 */   private static final Map<String, String> KEY_TYPES = new HashMap<String, String>();
/*     */   static {
/*  51 */     KEY_TYPES.put("RSA", "RSA");
/*  52 */     KEY_TYPES.put("DHE_RSA", "RSA");
/*  53 */     KEY_TYPES.put("ECDHE_RSA", "RSA");
/*  54 */     KEY_TYPES.put("ECDHE_ECDSA", "EC");
/*  55 */     KEY_TYPES.put("ECDH_RSA", "EC_RSA");
/*  56 */     KEY_TYPES.put("ECDH_ECDSA", "EC_EC");
/*  57 */     KEY_TYPES.put("DH_RSA", "DH_RSA");
/*     */   }
/*     */   
/*     */   private final OpenSslKeyMaterialProvider provider;
/*     */   
/*     */   OpenSslKeyMaterialManager(OpenSslKeyMaterialProvider provider) {
/*  63 */     this.provider = provider;
/*     */   }
/*     */   
/*     */   void setKeyMaterialServerSide(ReferenceCountedOpenSslEngine engine) throws SSLException {
/*  67 */     String[] authMethods = engine.authMethods();
/*  68 */     if (authMethods.length == 0) {
/*     */       return;
/*     */     }
/*  71 */     Set<String> aliases = new HashSet<String>(authMethods.length);
/*  72 */     for (String authMethod : authMethods) {
/*  73 */       String type = KEY_TYPES.get(authMethod);
/*  74 */       if (type != null) {
/*  75 */         String alias = chooseServerAlias(engine, type);
/*  76 */         if (alias != null && aliases.add(alias) && 
/*  77 */           !setKeyMaterial(engine, alias)) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   void setKeyMaterialClientSide(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) throws SSLException {
/*  87 */     String alias = chooseClientAlias(engine, keyTypes, issuer);
/*     */ 
/*     */ 
/*     */     
/*  91 */     if (alias != null) {
/*  92 */       setKeyMaterial(engine, alias);
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean setKeyMaterial(ReferenceCountedOpenSslEngine engine, String alias) throws SSLException {
/*  97 */     OpenSslKeyMaterial keyMaterial = null;
/*     */     try {
/*  99 */       keyMaterial = this.provider.chooseKeyMaterial(engine.alloc, alias);
/* 100 */       return (keyMaterial == null || engine.setKeyMaterial(keyMaterial));
/* 101 */     } catch (SSLException e) {
/* 102 */       throw e;
/* 103 */     } catch (Exception e) {
/* 104 */       throw new SSLException(e);
/*     */     } finally {
/* 106 */       if (keyMaterial != null) {
/* 107 */         keyMaterial.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private String chooseClientAlias(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) {
/* 113 */     X509KeyManager manager = this.provider.keyManager();
/* 114 */     if (manager instanceof X509ExtendedKeyManager) {
/* 115 */       return ((X509ExtendedKeyManager)manager).chooseEngineClientAlias(keyTypes, (Principal[])issuer, engine);
/*     */     }
/* 117 */     return manager.chooseClientAlias(keyTypes, (Principal[])issuer, null);
/*     */   }
/*     */   
/*     */   private String chooseServerAlias(ReferenceCountedOpenSslEngine engine, String type) {
/* 121 */     X509KeyManager manager = this.provider.keyManager();
/* 122 */     if (manager instanceof X509ExtendedKeyManager) {
/* 123 */       return ((X509ExtendedKeyManager)manager).chooseEngineServerAlias(type, null, engine);
/*     */     }
/* 125 */     return manager.chooseServerAlias(type, null, null);
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslKeyMaterialManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */