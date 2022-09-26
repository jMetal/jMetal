package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.PenaltyBoundaryIntersection;
import org.uma.jmetal.util.aggregativefunction.impl.Tschebyscheff;
import org.uma.jmetal.util.aggregativefunction.impl.WeightedSum;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class AggregativeFunctionParameter extends CategoricalParameter {
  public AggregativeFunctionParameter(List<String> aggregativeFunctions,  String[] args) {
    super("aggregativeFunction", args, aggregativeFunctions);
  }

  public AggregativeFunction getParameter() {
    AggregativeFunction result;

    switch (getValue()) {
      case "tschebyscheff":
        result =  new Tschebyscheff() ;
        break;
      case "weightedSum":
        result = new WeightedSum() ;
        break;
      case "penaltyBoundaryIntersection":
        double theta = (double) findSpecificParameter("pbiTheta").getValue();
        result = new PenaltyBoundaryIntersection(theta) ;
        break;
      default:
        throw new JMetalException("Aggregative function does not exist: " + getName());
    }
    return result;
  }
}
