package com.mojang.authlib;

public interface ProfileLookupCallback {
  void onProfileLookupSucceeded(GameProfile paramGameProfile);
  
  void onProfileLookupFailed(GameProfile paramGameProfile, Exception paramException);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\ProfileLookupCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */