package org.uma.jmetal.auto.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Abstract class representing generic parameters. Any parameter has a name and a value that is
 * assigned by parsing an array called {@link Parameter#args} which contains sequences of pairs
 * [key, value]. For example, the args field could contain these values:
 * ["--populationSize", "100", "--offspringPopulationSize", "100", "--createInitialSolutions", "random"]
 *
 * @author Antonio J. Nebro
 * @param <T> Type of the parameter
 */
public abstract class Parameter<T> {
  private T value;
  private final String name;
  private final String[] args;
  private final List<Pair<String, Parameter<?>>> specificParameters = new ArrayList<>() ;
  private final List<Parameter<?>> globalParameters = new ArrayList<>();
  private final Map<String, Object> nonConfigurableParameters = new HashMap<>();

  public Parameter(String name, String[] args) {
    this.name = name;
    this.args = args;
  }

  public T on(String key, String[] args, Function<String, T> parser) {
    return parser.apply(retrieve(args, key));
  }

  private String retrieve(String[] args, String key) {
    int index = Arrays.asList(args).indexOf(key);
    Check.that(index != -1 && index != args.length - 1, "Missing parameter: " + key);
    return args[index + 1];
  }

  public abstract void check();

  public abstract Parameter<T> parse();

  public Parameter<T> parse(Function<String, T> parseFunction) {
    setValue(on("--" + getName(), getArgs(), parseFunction));

    for (Parameter<?> parameter : getGlobalParameters()) {
      parameter.parse().check();
    }

    getSpecificParameters()
            .forEach(
                    pair -> {
                      if (pair.getKey().equals(getValue())) {
                        pair.getValue().parse().check();
                      }
                    });

    return this;
  }

  public String getName() {
    return name;
  }

  public List<Pair<String, Parameter<?>>> getSpecificParameters() {
    return specificParameters;
  }

  public void addSpecificParameter(String dependsOn, Parameter<?> parameter) {
    specificParameters.add(new ImmutablePair<>(dependsOn, parameter));
  }

  public List<Parameter<?>> getGlobalParameters() {
    return globalParameters;
  }

  public void addGlobalParameter(Parameter<?> parameter) {
    globalParameters.add(parameter);
  }

  public void addNonConfigurableParameter(String parameterName, Object value) {
    nonConfigurableParameters.put(parameterName, value) ;
  }

  public Object getNonConfigurableParameter(String parameterName) {
    return nonConfigurableParameters.get(parameterName) ;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public String[] getArgs() {
    return args;
  }

  public Parameter<?> findGlobalParameter(String parameterName) {

    return getGlobalParameters().stream()
        .filter(parameter -> parameter.getName().equals(parameterName))
        .findFirst()
        .orElse(null);
  }

  public Parameter<?> findSpecificParameter(String parameterName) {

    return Objects.requireNonNull(getSpecificParameters().stream()
            .filter(pair -> pair.getRight().getName().equals(parameterName))
            .findFirst()
            .orElse(null))
        .getValue();
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("Name: " + getName() + ": " + "Value: " + getValue());
    if (!globalParameters.isEmpty()) {
      result.append("\n\t");
      for (Parameter<?> parameter : globalParameters) {
        result.append(" \n -> ").append(parameter.toString());
      }
    }
    if (!specificParameters.isEmpty()) {
      result.append("\n\t");

      for (Pair<String, Parameter<?>> parameter : specificParameters) {
        result.append(" \n -> ").append(parameter.toString());
      }
    }
    return result.toString();
  }
}
