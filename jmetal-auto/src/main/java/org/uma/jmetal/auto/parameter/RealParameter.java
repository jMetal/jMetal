package org.uma.jmetal.auto.parameter;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Parameter representing a double value belonging to a range [{@link #lowerBound}, {@link #upperBound}]
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class RealParameter extends Parameter<Double> {

  private final double lowerBound;
  private final double upperBound;

  public RealParameter(String name, double lowerBound, double upperBound) {
    super(name);
    Check.that(lowerBound < upperBound, "The lower bound " + lowerBound + " "
        + "is not higher that the upper bound " + upperBound);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public RealParameter parse(String[] arguments) {
    return (RealParameter) parse(Double::parseDouble, arguments);
  }


  @Override
  public void check() {
    if ((value() < lowerBound) || (value() > upperBound)) {
      throw new JMetalException(
          "Parameter "
              + name()
              + ": Invalid value: "
              + value()
              + ". Range: "
              + lowerBound
              + ", "
              + upperBound);
    }
  }

  /**
   * @return A list with the lower and upper bounds delimiting the valid values
   */
  public List<Double> getValidValues() {
    return List.of(lowerBound, upperBound);
  }

  @Override
  public String toString() {
    StringBuilder result =
        new StringBuilder("Name: "
            + name()
            + ": "
            + "Value: "
            + value()
            + ". Lower bound: "
            + lowerBound
            + ". Upper bound: "
            + upperBound);
    for (Parameter<?> parameter : globalParameters()) {
      result.append("\n -> ").append(parameter.toString());
    }
    for (Pair<String, Parameter<?>> parameter : specificParameters()) {
      result.append("\n  -> ").append(parameter.getRight().toString());
    }
    return result.toString();
  }
}
