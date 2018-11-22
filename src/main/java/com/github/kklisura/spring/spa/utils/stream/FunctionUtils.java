package com.github.kklisura.spring.spa.utils.stream;

import java.util.function.Supplier;

/**
 * Utility functions.
 *
 * @author Kenan Klisura
 */
public final class FunctionUtils {

  private FunctionUtils() {
    // Empty ctor.
  }

  /**
   * Memoize the original supplier.
   *
   * @param original Original supplier.
   * @param <T> Type of supplier return call.
   * @return Memoization supplier.
   */
  public static <T> Supplier<T> memoize(Supplier<T> original) {
    return new Supplier<T>() {
      Supplier<T> delegate = this::firstTime;
      boolean initialized;

      public T get() {
        return delegate.get();
      }

      private synchronized T firstTime() {
        if (!initialized) {
          T value = original.get();
          delegate = () -> value;
          initialized = true;
        }
        return delegate.get();
      }
    };
  }
}
