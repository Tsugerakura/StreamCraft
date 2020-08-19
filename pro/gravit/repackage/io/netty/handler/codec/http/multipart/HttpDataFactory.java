package pro.gravit.repackage.io.netty.handler.codec.http.multipart;

import java.nio.charset.Charset;
import pro.gravit.repackage.io.netty.handler.codec.http.HttpRequest;

public interface HttpDataFactory {
  void setMaxLimit(long paramLong);
  
  Attribute createAttribute(HttpRequest paramHttpRequest, String paramString);
  
  Attribute createAttribute(HttpRequest paramHttpRequest, String paramString, long paramLong);
  
  Attribute createAttribute(HttpRequest paramHttpRequest, String paramString1, String paramString2);
  
  FileUpload createFileUpload(HttpRequest paramHttpRequest, String paramString1, String paramString2, String paramString3, String paramString4, Charset paramCharset, long paramLong);
  
  void removeHttpDataFromClean(HttpRequest paramHttpRequest, InterfaceHttpData paramInterfaceHttpData);
  
  void cleanRequestHttpData(HttpRequest paramHttpRequest);
  
  void cleanAllHttpData();
  
  @Deprecated
  void cleanRequestHttpDatas(HttpRequest paramHttpRequest);
  
  @Deprecated
  void cleanAllHttpDatas();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\codec\http\multipart\HttpDataFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */