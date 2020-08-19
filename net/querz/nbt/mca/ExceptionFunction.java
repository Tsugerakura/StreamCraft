package net.querz.nbt.mca;

@FunctionalInterface
public interface ExceptionFunction<T, R, E extends Exception> {
  R accept(T paramT) throws E;
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\net\querz\nbt\mca\ExceptionFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */