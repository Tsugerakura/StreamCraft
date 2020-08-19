package com.fasterxml.jackson.core.async;

public interface NonBlockingInputFeeder {
  boolean needMoreInput();
  
  void endOfInput();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\com\fasterxml\jackson\core\async\NonBlockingInputFeeder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */