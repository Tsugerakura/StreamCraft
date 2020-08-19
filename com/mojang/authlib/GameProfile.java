package com.mojang.authlib;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GameProfile {
  private final UUID id;
  
  private final String name;
  
  private final PropertyMap properties = new PropertyMap();
  
  private boolean legacy;
  
  public GameProfile(UUID paramUUID, String paramString) {
    if (paramUUID == null && StringUtils.isBlank(paramString))
      throw new IllegalArgumentException("Name and ID cannot both be blank"); 
    this.id = paramUUID;
    this.name = paramString;
  }
  
  public GameProfile(String paramString1, String paramString2) {
    this(StringUtils.isBlank(paramString1) ? null : UUIDTypeAdapter.fromString(paramString1), paramString2);
  }
  
  public UUID getId() {
    return this.id;
  }
  
  public String getId() {
    return (this.id == null) ? null : UUIDTypeAdapter.fromUUID(this.id);
  }
  
  public UUID getUUID() {
    return this.id;
  }
  
  public String getName() {
    return this.name;
  }
  
  public PropertyMap getProperties() {
    return this.properties;
  }
  
  public boolean isComplete() {
    return (this.id != null && StringUtils.isNotBlank(getName()));
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    GameProfile gameProfile = (GameProfile)paramObject;
    return ((this.id != null) ? !this.id.equals(gameProfile.id) : (gameProfile.id != null)) ? false : (!((this.name != null) ? !this.name.equals(gameProfile.name) : (gameProfile.name != null)));
  }
  
  public int hashCode() {
    null = (this.id != null) ? this.id.hashCode() : 0;
    return 31 * null + ((this.name != null) ? this.name.hashCode() : 0);
  }
  
  public String toString() {
    return (new ToStringBuilder(this)).append("id", this.id).append("name", this.name).append("properties", this.properties).append("legacy", this.legacy).toString();
  }
  
  public boolean isLegacy() {
    return this.legacy;
  }
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\GameProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */