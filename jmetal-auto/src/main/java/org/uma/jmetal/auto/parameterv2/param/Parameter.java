package org.uma.jmetal.auto.parameterv2.param;

import org.uma.jmetal.auto.algorithm.nsgaiib.MissingParameterException;

import java.util.Arrays;
import java.util.function.Function;

public abstract class Parameter<T> {
  protected T value;

  public T on(String key, String[] args, Function<String, T> parser) {
    return parser.apply(retrieve(args, key));
  }

  private String retrieve(String[] args, String key) {
    int index = Arrays.asList(args).indexOf(key);
    if (index == -1 || index == args.length - 1) {
      throw new MissingParameterException(key);
    } else {
      return args[index + 1];
    }
  }

  public abstract String getName();

  public T getValue() {
    return value;
  }
}
