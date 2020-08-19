/*     */ package com.mojang.authlib;
/*     */ 
/*     */ import com.google.common.collect.Multimap;
/*     */ import com.mojang.authlib.properties.Property;
/*     */ import com.mojang.authlib.properties.PropertyMap;
/*     */ import com.mojang.util.UUIDTypeAdapter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.lang3.StringUtils;
/*     */ import org.apache.commons.lang3.Validate;
/*     */ import org.apache.logging.log4j.LogManager;
/*     */ import org.apache.logging.log4j.Logger;
/*     */ 
/*     */ public abstract class BaseUserAuthentication implements UserAuthentication {
/*  17 */   private static final Logger LOGGER = LogManager.getLogger();
/*     */   
/*     */   protected static final String STORAGE_KEY_PROFILE_NAME = "displayName";
/*     */   
/*     */   protected static final String STORAGE_KEY_PROFILE_ID = "uuid";
/*     */   protected static final String STORAGE_KEY_PROFILE_PROPERTIES = "profileProperties";
/*     */   protected static final String STORAGE_KEY_USER_NAME = "username";
/*     */   protected static final String STORAGE_KEY_USER_ID = "userid";
/*     */   protected static final String STORAGE_KEY_USER_PROPERTIES = "userProperties";
/*     */   private final AuthenticationService authenticationService;
/*  27 */   private final PropertyMap userProperties = new PropertyMap();
/*     */   private String userid;
/*     */   private String username;
/*     */   private String password;
/*     */   private GameProfile selectedProfile;
/*     */   private UserType userType;
/*     */   
/*     */   protected BaseUserAuthentication(AuthenticationService authenticationService) {
/*  35 */     Validate.notNull(authenticationService);
/*  36 */     this.authenticationService = authenticationService;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canLogIn() {
/*  41 */     return (!canPlayOnline() && StringUtils.isNotBlank(getUsername()) && StringUtils.isNotBlank(getPassword()));
/*     */   }
/*     */ 
/*     */   
/*     */   public void logOut() {
/*  46 */     this.password = null;
/*  47 */     this.userid = null;
/*  48 */     setSelectedProfile(null);
/*  49 */     getModifiableUserProperties().clear();
/*  50 */     setUserType(null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLoggedIn() {
/*  55 */     return (getSelectedProfile() != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setUsername(String username) {
/*  60 */     if (isLoggedIn() && canPlayOnline()) {
/*  61 */       throw new IllegalStateException("Cannot change username whilst logged in & online");
/*     */     }
/*     */     
/*  64 */     this.username = username;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPassword(String password) {
/*  69 */     if (isLoggedIn() && canPlayOnline() && StringUtils.isNotBlank(password)) {
/*  70 */       throw new IllegalStateException("Cannot set password whilst logged in & online");
/*     */     }
/*     */     
/*  73 */     this.password = password;
/*     */   }
/*     */   
/*     */   protected String getUsername() {
/*  77 */     return this.username;
/*     */   }
/*     */   
/*     */   protected String getPassword() {
/*  81 */     return this.password;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void loadFromStorage(Map<String, Object> credentials) {
/*  87 */     logOut();
/*     */     
/*  89 */     setUsername(String.valueOf(credentials.get("username")));
/*     */     
/*  91 */     if (credentials.containsKey("userid")) {
/*  92 */       this.userid = String.valueOf(credentials.get("userid"));
/*     */     } else {
/*  94 */       this.userid = this.username;
/*     */     } 
/*     */     
/*  97 */     if (credentials.containsKey("userProperties")) {
/*     */       try {
/*  99 */         List<Map<String, String>> list = (List<Map<String, String>>)credentials.get("userProperties");
/*     */         
/* 101 */         for (Map<String, String> propertyMap : list) {
/* 102 */           String name = propertyMap.get("name");
/* 103 */           String value = propertyMap.get("value");
/* 104 */           String signature = propertyMap.get("signature");
/*     */           
/* 106 */           if (signature == null) {
/* 107 */             getModifiableUserProperties().put(name, new Property(name, value)); continue;
/*     */           } 
/* 109 */           getModifiableUserProperties().put(name, new Property(name, value, signature));
/*     */         }
/*     */       
/* 112 */       } catch (Throwable t) {
/* 113 */         LOGGER.warn("Couldn't deserialize user properties", t);
/*     */       } 
/*     */     }
/*     */     
/* 117 */     if (credentials.containsKey("displayName") && credentials.containsKey("uuid")) {
/* 118 */       GameProfile profile = new GameProfile(UUIDTypeAdapter.fromString(String.valueOf(credentials.get("uuid"))), String.valueOf(credentials.get("displayName")));
/* 119 */       if (credentials.containsKey("profileProperties")) {
/*     */         try {
/* 121 */           List<Map<String, String>> list = (List<Map<String, String>>)credentials.get("profileProperties");
/* 122 */           for (Map<String, String> propertyMap : list) {
/* 123 */             String name = propertyMap.get("name");
/* 124 */             String value = propertyMap.get("value");
/* 125 */             String signature = propertyMap.get("signature");
/*     */             
/* 127 */             if (signature == null) {
/* 128 */               profile.getProperties().put(name, new Property(name, value)); continue;
/*     */             } 
/* 130 */             profile.getProperties().put(name, new Property(name, value, signature));
/*     */           }
/*     */         
/* 133 */         } catch (Throwable t) {
/* 134 */           LOGGER.warn("Couldn't deserialize profile properties", t);
/*     */         } 
/*     */       }
/* 137 */       setSelectedProfile(profile);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Map<String, Object> saveForStorage() {
/* 143 */     Map<String, Object> result = new HashMap<String, Object>();
/*     */     
/* 145 */     if (getUsername() != null) {
/* 146 */       result.put("username", getUsername());
/*     */     }
/* 148 */     if (getUserID() != null) {
/* 149 */       result.put("userid", getUserID());
/* 150 */     } else if (getUsername() != null) {
/* 151 */       result.put("username", getUsername());
/*     */     } 
/*     */     
/* 154 */     if (!getUserProperties().isEmpty()) {
/* 155 */       List<Map<String, String>> properties = new ArrayList<Map<String, String>>();
/* 156 */       for (Property userProperty : getUserProperties().values()) {
/* 157 */         Map<String, String> property = new HashMap<String, String>();
/* 158 */         property.put("name", userProperty.getName());
/* 159 */         property.put("value", userProperty.getValue());
/* 160 */         property.put("signature", userProperty.getSignature());
/* 161 */         properties.add(property);
/*     */       } 
/* 163 */       result.put("userProperties", properties);
/*     */     } 
/*     */     
/* 166 */     GameProfile selectedProfile = getSelectedProfile();
/* 167 */     if (selectedProfile != null) {
/* 168 */       result.put("displayName", selectedProfile.getName());
/* 169 */       result.put("uuid", selectedProfile.getId());
/*     */       
/* 171 */       List<Map<String, String>> properties = new ArrayList<Map<String, String>>();
/* 172 */       for (Property profileProperty : selectedProfile.getProperties().values()) {
/* 173 */         Map<String, String> property = new HashMap<String, String>();
/* 174 */         property.put("name", profileProperty.getName());
/* 175 */         property.put("value", profileProperty.getValue());
/* 176 */         property.put("signature", profileProperty.getSignature());
/* 177 */         properties.add(property);
/*     */       } 
/*     */       
/* 180 */       if (!properties.isEmpty()) {
/* 181 */         result.put("profileProperties", properties);
/*     */       }
/*     */     } 
/*     */     
/* 185 */     return result;
/*     */   }
/*     */   
/*     */   protected void setSelectedProfile(GameProfile selectedProfile) {
/* 189 */     this.selectedProfile = selectedProfile;
/*     */   }
/*     */ 
/*     */   
/*     */   public GameProfile getSelectedProfile() {
/* 194 */     return this.selectedProfile;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 199 */     StringBuilder result = new StringBuilder();
/*     */     
/* 201 */     result.append(getClass().getSimpleName());
/* 202 */     result.append("{");
/*     */     
/* 204 */     if (isLoggedIn()) {
/* 205 */       result.append("Logged in as ");
/* 206 */       result.append(getUsername());
/*     */       
/* 208 */       if (getSelectedProfile() != null) {
/* 209 */         result.append(" / ");
/* 210 */         result.append(getSelectedProfile());
/* 211 */         result.append(" - ");
/*     */         
/* 213 */         if (canPlayOnline()) {
/* 214 */           result.append("Online");
/*     */         } else {
/* 216 */           result.append("Offline");
/*     */         } 
/*     */       } 
/*     */     } else {
/* 220 */       result.append("Not logged in");
/*     */     } 
/*     */     
/* 223 */     result.append("}");
/*     */     
/* 225 */     return result.toString();
/*     */   }
/*     */   
/*     */   public AuthenticationService getAuthenticationService() {
/* 229 */     return this.authenticationService;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getUserID() {
/* 234 */     return this.userid;
/*     */   }
/*     */ 
/*     */   
/*     */   public PropertyMap getUserProperties() {
/* 239 */     if (isLoggedIn()) {
/* 240 */       PropertyMap result = new PropertyMap();
/* 241 */       result.putAll((Multimap)getModifiableUserProperties());
/* 242 */       return result;
/*     */     } 
/* 244 */     return new PropertyMap();
/*     */   }
/*     */ 
/*     */   
/*     */   protected PropertyMap getModifiableUserProperties() {
/* 249 */     return this.userProperties;
/*     */   }
/*     */ 
/*     */   
/*     */   public UserType getUserType() {
/* 254 */     if (isLoggedIn()) {
/* 255 */       return (this.userType == null) ? UserType.LEGACY : this.userType;
/*     */     }
/* 257 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setUserType(UserType userType) {
/* 262 */     this.userType = userType;
/*     */   }
/*     */   
/*     */   protected void setUserid(String userid) {
/* 266 */     this.userid = userid;
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\BaseUserAuthentication.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */