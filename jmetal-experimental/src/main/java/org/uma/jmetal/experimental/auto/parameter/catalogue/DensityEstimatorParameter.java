package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.KnnDensityEstimator;

public class DensityEstimatorParameter<S extends Solution<?>> extends CategoricalParameter {
  public DensityEstimatorParameter(String name, String[] args, List<String> validDensityEstimators) {
    super(name, args, validDensityEstimators);
  }

  public DensityEstimator<S> getParameter() {
    DensityEstimator<S> result ;
    switch (getValue()) {
      case "crowdingDistance":
        result = new CrowdingDistanceDensityEstimator<>() ;
        break;
      case "knn":
        result = new KnnDensityEstimator<>(1) ;
        break;
      //case "hypervolumeContribution":
        //result = new Hyper
        default:
        throw new RuntimeException("Density estimator does not exist: " + getName());
    }
    return result;
  }
}
