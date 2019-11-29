package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.component.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.component.densityestimator.impl.KnnDensityEstimator;
import org.uma.jmetal.solution.Solution;

import java.util.List;
import java.util.function.Function;

public class DensityEstimatorParameter<S extends Solution<?>> extends CategoricalParameter<String> {
  public DensityEstimatorParameter(String name, String args[], List<String> validDensityEstimators) {
    super(name, args, validDensityEstimators);
  }

  public CategoricalParameter<String> parse() {
    setValue(on("--" + getName(), getArgs(), Function.identity()));

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
