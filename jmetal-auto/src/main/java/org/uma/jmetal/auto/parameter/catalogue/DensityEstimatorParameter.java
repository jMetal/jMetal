package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.auto.util.ranking.impl.DominanceRanking;
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
      case "CrowdingDistance":
        result = new CrowdingDistanceDensityEstimator<>() ;
        break;

      default:
        throw new RuntimeException("Density estimatorg does not exist: " + getName());
    }
    return result;
  }
}
