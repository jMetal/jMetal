package org.uma.jmetal;

import static java.lang.reflect.Modifier.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class provides a set of helper methods to name instances. By naming, we
 * mean that we override their {@link Object#toString()} method to return a
 * meaningful name.
 */
public class Naming {

  /**
   * Adapt the {@link Object#toString()} of the provided object to return the
   * given name.
   * <p>
   * Although it does not return the same instance, it returns an instance which,
   * besides its string representation, behaves in the same way than the provided
   * object.
   * <p>
   * This method is not able to reproduce the behavior of
   * {@link Object#hashCode()}, which may return a different value.
   * <p>
   * If you need to generate a super type, you can specify the {@link Class} by
   * using {@link #name(String, Object, Class)}. It is for example useful for
   * naming lambdas, which are internally implemented as final child classes and
   * thus cannot be adapted directly.
   * 
   * @param <T>
   *          the type of object to name
   * @param name
   *          the name to apply
   * @param object
   *          the object to adapt
   * @return an equivalent object with the given name
   */
  public static <T> T name(String name, T object) {
    return name(name, object, findNonSyntheticClass(object));
  }

  /**
   * Adapt the {@link Object#toString()} of the provided object to return the
   * given name.
   * <p>
   * Although it does not return the same instance, it returns an instance which,
   * besides its string representation, behaves in the same way than the provided
   * object.
   * <p>
   * This method is not able to reproduce the behavior of
   * {@link Object#hashCode()}, which may return a different value.
   * 
   * @param <T>
   *          the type of object to name
   * @param name
   *          the name to apply
   * @param object
   *          the object to adapt
   * @param objectClass
   *          the type of object to adapt
   * @return an equivalent object with the given name
   */
  public static <T> T name(String name, T object, Class<T> objectClass) {
    // Creates a decorator which delegates all the calls to the original object
    // methods
    T decorator = (T) mock(objectClass, call -> {
      return call.getMethod().invoke(object, call.getArguments());
    });

    // Sets all the relevant fields to the original object values
    Stream.of(objectClass.getDeclaredFields())//
        .filter(field -> !field.isSynthetic())//
        .filter(field -> !isStatic(field.getModifiers()))//
        .forEach(field -> {
          try {
            field.setAccessible(true);
            field.set(decorator, field.get(object));
          } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
          }
        });

    // Overrides the toString() behavior to return the given name
    when(decorator.toString()).thenReturn(name);

    return decorator;
  }

  /**
   * Adapt the {@link Object#toString()} of the provided object to return the name
   * of the method it calls. In other words, it is assumed to be an object which
   * calls the method of another, like a {@link FunctionalInterface}. The focus is
   * given to any method consuming a single argument. So as long as there is only
   * one such method, the caller can be more complex than a mere
   * {@link FunctionalInterface}.
   * <p>
   * Although it does not return the same instance, it returns an instance which,
   * besides its string representation, behaves in the same way than the provided
   * object.
   * <p>
   * This method is not able to reproduce the behavior of
   * {@link Object#hashCode()}, which may return a different value.
   * 
   * @param <C>
   *          the type of caller
   * @param caller
   *          the caller to name
   * @return an adapted caller
   */
  public static <C> C nameAsCalledMethod(C caller) {
    Class<C> callerClass = findNonSyntheticClass(caller);
    Class<?> argClass = findSingleArgType(callerClass);
    Method method = findSingleCallerMethodConsumingSingleArg(callerClass, argClass);
    BiConsumer<C, Object> call = createCallExecutor(method);
    String name = retrieveArgMethodName(caller, call, argClass);
    return name(name, caller, callerClass);
  }

  @SuppressWarnings("unchecked")
  private static <T> Class<T> findNonSyntheticClass(T caller) {
    Class<?> clazz = caller.getClass();
    while (clazz.isSynthetic()) {
      List<Class<?>> candidates = Stream//
          .concat(//
              Stream.of(clazz.getSuperclass()), //
              Stream.of(clazz.getInterfaces()))//
          .filter(clazz2 -> clazz2 != Object.class)//
          .collect(Collectors.toList());
      if (candidates.size() != 1) {
        throw new IllegalArgumentException("Cannot find a single non synthetic class/interface: " + candidates);
      } else {
        clazz = candidates.get(0);
      }
    }
    return (Class<T>) clazz;
  }

  private static Class<?> findSingleArgType(Class<?> callerClass) {
    List<Class<?>> candidateTypes = Stream.of(callerClass.getMethods())//
        .filter(method -> method.getParameterCount() == 1)//
        .map(method -> method.getParameterTypes()[0])//
        .collect(Collectors.toList());
    if (candidateTypes.size() != 1) {
      throw new IllegalArgumentException("Not a single type consumed: " + candidateTypes);
    }
    return candidateTypes.get(0);
  }

  private static <C> Method findSingleCallerMethodConsumingSingleArg(Class<C> callerClass, Class<?> argClass) {
    List<Method> candidateMethods = Stream.of(callerClass.getMethods())//
        .filter(method -> method.getParameterCount() == 1)//
        .filter(method -> method.getParameterTypes()[0].isAssignableFrom(argClass))//
        .collect(Collectors.toList());
    if (candidateMethods.size() != 1) {
      throw new IllegalArgumentException("Not a single method calling " + argClass);
    }
    return candidateMethods.get(0);
  }

  private static <C> BiConsumer<C, Object> createCallExecutor(Method method) {
    return (caller, object) -> {
      try {
        method.invoke(caller, object);
      } catch (InvocationTargetException cause) {
        Throwable subCause = cause.getCause();
        if (subCause instanceof RuntimeException) {
          throw (RuntimeException) subCause;
        } else {
          throw new RuntimeException(subCause);
        }
      } catch (IllegalAccessException | IllegalArgumentException cause) {
        throw new RuntimeException(cause);
      }
    };
  }

  private static <C> String retrieveArgMethodName(C caller, BiConsumer<C, Object> call, Class<?> argClass) {
    @SuppressWarnings("serial")
    class CallException extends RuntimeException {

    }
    Object arg = mock(argClass, invocation -> {
      throw new CallException();
    });

    try {
      call.accept(caller, arg);
      throw new RuntimeException("Failed to generate an exception to retrieve the called method");
    } catch (CallException cause) {
      return Stream.of(cause.getStackTrace())//
          .filter(trace -> trace.getClassName().startsWith(argClass.getName() + "$"))//
          .map(trace -> trace.getMethodName())//
          .findFirst().get();
    }
  }
}
