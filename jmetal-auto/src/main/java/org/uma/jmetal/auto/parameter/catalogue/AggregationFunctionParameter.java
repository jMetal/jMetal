package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.BooleanParameter;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.aggregationfunction.impl.ModifiedTschebyscheff;
import org.uma.jmetal.util.aggregationfunction.impl.PenaltyBoundaryIntersection;
import org.uma.jmetal.util.aggregationfunction.impl.Tschebyscheff;
import org.uma.jmetal.util.aggregationfunction.impl.WeightedSum;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class AggregationFunctionParameter extends CategoricalParameter {

  private boolean normalizedObjectives;

  public AggregationFunctionParameter(List<String> aggregationFunctions) {
    super("aggregationFunction", aggregationFunctions);

    normalizedObjectives = false;
  }

  public void normalizedObjectives(boolean normalizedObjectives) {
    this.normalizedObjectives = normalizedObjectives;
  }

  public AggregationFunction getParameter() {
    AggregationFunction aggregationFunction;

    BooleanParameter normalizeObjectivesParameter = ((BooleanParameter) findGlobalParameter(
        "normalizeObjectives"));
    boolean normalizeObjectives = normalizeObjectivesParameter.value();
    double epsilon = 0.00000001;

    /*
    if (normalizeObjectives) {
      var epsilonParameterForNormalizing = (RealParameter) normalizeObjectivesParameter.findSpecificParameter(
          "epsilonParameterForNormalizing") ;
      epsilon = epsilonParameterForNormalizing.value() ;
    }

*/


    switch (value()) {
      case "tschebyscheff":
        aggregationFunction = new Tschebyscheff(normalizedObjectives);
        break;
      case "modifiedTschebyscheff":
        aggregationFunction = new ModifiedTschebyscheff(normalizedObjectives);
        break;
      case "weightedSum":
        aggregationFunction = new WeightedSum(normalizedObjectives);
        break;
      case "penaltyBoundaryIntersection":
        double theta = (double) findSpecificParameter("pbiTheta").value();
        aggregationFunction = new PenaltyBoundaryIntersection(theta, normalizedObjectives);
        break;
      default:
        throw new JMetalException("Aggregation function does not exist: " + name());
    }

    if (normalizeObjectives) {
      aggregationFunction.epsilon(epsilon);
    }
    return aggregationFunction;
  }
}
