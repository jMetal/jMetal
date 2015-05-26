package org.uma.jmetal.qualityindicator.hypervolume.impl;

import org.uma.jmetal.qualityindicator.hypervolume.Hypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;

import java.util.List;

/**
 * Created by ajnebro on 19/4/15.
 */
public class WFGHypervolume extends SimpleDescribedEntity implements Hypervolume {

  public WFGHypervolume() {
    super("HV", "Hypervolume quality indicator. WFG based implementation") ;
  }

  @Override 
  public double[] computeHypervolumeContribution(Front front) {
    return new double[0];
  }

  @Override public double execute(Front frontA, Front frontB) {
    return 0;
  }

  @Override
  public <S extends Solution<?>> double execute(List<S> frontA, List<S> frontB) {
    return 0;
  }

  @Override public String getName() {
    return super.getName() ;
  }

  @Override public String getDescription() {
    return super.getDescription() ;
  }
}
