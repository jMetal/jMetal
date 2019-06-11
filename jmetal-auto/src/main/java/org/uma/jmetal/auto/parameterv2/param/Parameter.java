package org.uma.jmetal.auto.parameterv2.param;

import org.uma.jmetal.auto.algorithm.nsgaiib.MissingParameterException;
import org.uma.jmetal.auto.irace.parametertype.ParameterType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public abstract class Parameter<T> {
  protected T value;
  private List<Parameter<?>> specificParameters = new ArrayList<>();
  private List<Parameter<?>> globalParameters = new ArrayList<>();

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

  public List<Parameter<?>> getSpecificParameters() {
    return specificParameters;
  }
  public void addSpecificParameter(Parameter<?> parameter) { specificParameters.add(parameter);}
  public List<Parameter<?>> getGlobalParameters() {
    return globalParameters;
  }
  public void addGlobalParameter(Parameter<?> parameter) { globalParameters.add(parameter);}
  public T getValue() {
    return value;
  }

  @Override
  public String toString() {
    String result = "Name: " + getName() + ": " + "Value: " + getValue() ;
    for (Parameter<?> parameter : globalParameters) {
      result += " -> " + parameter.toString() ;
    }
    for (Parameter<?> parameter : specificParameters) {
      result += " -> " + parameter.toString() ;
    }
    return result ;
  }
}
