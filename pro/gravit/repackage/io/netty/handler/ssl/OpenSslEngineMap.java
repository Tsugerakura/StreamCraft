package pro.gravit.repackage.io.netty.handler.ssl;

interface OpenSslEngineMap {
  ReferenceCountedOpenSslEngine remove(long paramLong);
  
  void add(ReferenceCountedOpenSslEngine paramReferenceCountedOpenSslEngine);
  
  ReferenceCountedOpenSslEngine get(long paramLong);
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\handler\ssl\OpenSslEngineMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */