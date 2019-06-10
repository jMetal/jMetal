package org.uma.jmetal.auto.parameterv2;

import org.uma.jmetal.auto.algorithm.nsgaiib.MissingParameterException;

import java.util.Arrays;
import java.util.function.Function;

abstract class Parameter<T> {
  public enum ParameterType {singleValue, categorical}

  protected T value;
  protected ParameterType type ;

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

  public ParameterType getType() {return type ;} ;

    /*
    private Parameter<T> parent = null ;
    protected Boolean isGlobalParameter = false ; ;

    protected List<Parameter<?>> globalParameters = new ArrayList<>();
    protected List<Parameter<?>> specificParameters = new ArrayList<>();
    */
}
