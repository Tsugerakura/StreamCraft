package org.apache.http.client.protocol;

@Deprecated
public interface ClientContext {
  public static final String ROUTE = "http.route";
  
  public static final String SCHEME_REGISTRY = "http.scheme-registry";
  
  public static final String COOKIESPEC_REGISTRY = "http.cookiespec-registry";
  
  public static final String COOKIE_SPEC = "http.cookie-spec";
  
  public static final String COOKIE_ORIGIN = "http.cookie-origin";
  
  public static final String COOKIE_STORE = "http.cookie-store";
  
  public static final String CREDS_PROVIDER = "http.auth.credentials-provider";
  
  public static final String AUTH_CACHE = "http.auth.auth-cache";
  
  public static final String TARGET_AUTH_STATE = "http.auth.target-scope";
  
  public static final String PROXY_AUTH_STATE = "http.auth.proxy-scope";
  
  public static final String AUTH_SCHEME_PREF = "http.auth.scheme-pref";
  
  public static final String USER_TOKEN = "http.user-token";
  
  public static final String AUTHSCHEME_REGISTRY = "http.authscheme-registry";
  
  public static final String SOCKET_FACTORY_REGISTRY = "http.socket-factory-registry";
  
  public static final String REQUEST_CONFIG = "http.request-config";
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\org\apache\http\client\protocol\ClientContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */