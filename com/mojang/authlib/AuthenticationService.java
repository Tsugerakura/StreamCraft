package com.mojang.authlib;

import com.mojang.authlib.minecraft.MinecraftSessionService;

public interface AuthenticationService {
  UserAuthentication createUserAuthentication(Agent paramAgent);
  
  MinecraftSessionService createMinecraftSessionService();
  
  GameProfileRepository createProfileRepository();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\mojang\authlib\AuthenticationService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */