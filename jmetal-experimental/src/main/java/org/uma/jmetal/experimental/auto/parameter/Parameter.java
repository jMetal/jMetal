package org.uma.jmetal.experimental.auto.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    for (@NotNull Parameter<?> parameter : getGlobalParameters()) {
      parameter.parse().check();
    }

      for (Pair<String, Parameter<?>> pair : getSpecificParameters()) {
          if (pair.getKey().equals(getValue())) {
              pair.getValue().parse().check();
          }
      }

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

  public @NotNull List<Parameter<?>> getGlobalParameters() {
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

  public @Nullable Parameter<?> findGlobalParameter(String parameterName) {

      for (Parameter<?> parameter : getGlobalParameters()) {
          if (parameter.getName().equals(parameterName)) {
              return parameter;
          }
      }
      return null;
  }

  public @Nullable Parameter<?> findSpecificParameter(String parameterName) {

      for (@NotNull Pair<String, Parameter<?>> pair : getSpecificParameters()) {
          if (pair.getRight().getName().equals(parameterName)) {
              return Objects.requireNonNull(pair)
                      .getValue();
          }
      }
      return null;
  }

  @Override
  public String toString() {
    @NotNull StringBuilder result = new StringBuilder("Name: " + getName() + ": " + "Value: " + getValue());
    if (globalParameters.size() > 0) {
      result.append("\n\t");
      for (Parameter<?> parameter : globalParameters) {
        result.append(" \n -> ").append(parameter.toString());
      }
    }
    if (specificParameters.size() > 0) {
      result.append("\n\t");

      for (Pair<String, Parameter<?>> parameter : specificParameters) {
        result.append(" \n -> ").append(parameter.toString());
      }
    }
    return result.toString();
  }
}
