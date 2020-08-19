package org.apache.http.concurrent;

public interface FutureCallback<T> {
  void completed(T paramT);
  
  void failed(Exception paramException);
  
  void cancelled();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\concurrent\FutureCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */