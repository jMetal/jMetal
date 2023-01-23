package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.aggregationfunction.impl.ModifiedTschebyscheff;
import org.uma.jmetal.util.aggregationfunction.impl.PenaltyBoundaryIntersection;
import org.uma.jmetal.util.aggregationfunction.impl.Tschebyscheff;
import org.uma.jmetal.util.aggregationfunction.impl.WeightedSum;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class AggregationFunctionParameter extends CategoricalParameter {
  private boolean normalizedObjectives ;
  public AggregationFunctionParameter(List<String> aggregationFunctions,  String[] args) {
    super("aggregationFunction", args, aggregationFunctions);
    normalizedObjectives = false ;
  }

  public void normalizedObjectives(boolean normalizedObjectives) {
    this.normalizedObjectives = normalizedObjectives ;
  }

  public AggregationFunction getParameter() {
    AggregationFunction result;

    switch (getValue()) {
      case "tschebyscheff":
        result =  new Tschebyscheff(normalizedObjectives) ;
        break;
      case "modifiedTschebyscheff":
        result =  new ModifiedTschebyscheff(normalizedObjectives) ;
        break;
      case "weightedSum":
        result = new WeightedSum(normalizedObjectives) ;
        break;
      case "penaltyBoundaryIntersection":
        double theta = (double) findSpecificParameter("pbiTheta").getValue();
        result = new PenaltyBoundaryIntersection(theta, normalizedObjectives) ;
        break;
      default:
        throw new JMetalException("Aggregation function does not exist: " + getName());
    }
    return result;
  }
}
