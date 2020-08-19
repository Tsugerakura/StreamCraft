package org.apache.http;

public interface HttpEntityEnclosingRequest extends HttpRequest {
  boolean expectContinue();
  
  void setEntity(HttpEntity paramHttpEntity);
  
  HttpEntity getEntity();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\HttpEntityEnclosingRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */