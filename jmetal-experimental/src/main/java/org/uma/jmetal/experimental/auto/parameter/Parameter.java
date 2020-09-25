package org.uma.jmetal.experimental.auto.parameter;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.util.checking.Check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

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
  private String name;
  private String[] args;
  private List<Pair<String, Parameter<?>>> specificParameters = new ArrayList<>() ;
  private List<Parameter<?>> globalParameters = new ArrayList<>();

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

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public String[] getArgs() {
    return args;
  }

  protected Parameter<?> findGlobalParameter(String parameterName) {

    return getGlobalParameters().stream()
        .filter(parameter -> parameter.getName().equals(parameterName))
        .findFirst()
        .orElse(null);
  }

  protected Parameter<?> findSpecificParameter(String parameterName) {

    return getSpecificParameters().stream()
        .filter(pair -> pair.getRight().getName().equals(parameterName))
        .findFirst()
        .orElse(null)
        .getValue();
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("Name: " + getName() + ": " + "Value: " + getValue());
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
