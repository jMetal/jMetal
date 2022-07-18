package org.uma.jmetal.auto.parameter;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
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

  public RealParameter(String name, String[] args, double lowerBound, double upperBound) {
    super(name, args);
    Check.that(lowerBound < upperBound, "The lower bound " + lowerBound + " "
        + "is not higher that the upper bound " + upperBound);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public RealParameter parse() {
    return (RealParameter) parse(Double::parseDouble);
  }

  @Override
  public void check() {
    if ((getValue() < lowerBound) || (getValue() > upperBound)) {
      throw new JMetalException(
          "Parameter "
              + getName()
              + ": Invalid value: "
              + getValue()
              + ". Range: "
              + lowerBound
              + ", "
              + upperBound);
    }
  }

  /**
   * @return A list with the lower and upper bounds delimiting the valid values
   */
  public @NotNull List<Double> getValidValues() {
    return List.of(lowerBound, upperBound);
  }

  @Override
  public String toString() {
    var result =
        new StringBuilder("Name: "
            + getName()
            + ": "
            + "Value: "
            + getValue()
            + ". Lower bound: "
            + lowerBound
            + ". Upper bound: "
            + upperBound);
    for (@NotNull Parameter<?> parameter : getGlobalParameters()) {
      result.append("\n -> ").append(parameter.toString());
    }
    for (var parameter : getSpecificParameters()) {
      result.append("\n  -> ").append(parameter.getRight().toString());
    }
    return result.toString();
  }
}
