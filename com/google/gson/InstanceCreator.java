package com.google.gson;

import java.lang.reflect.Type;

public interface InstanceCreator<T> {
  T createInstance(Type paramType);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\google\gson\InstanceCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */