package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializeVelocity;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

/**
 * TODO: description missing
 * @author Daniel Doblas
 * @author Antonio J. Nebro
 */
public interface InitializeVelocity {
  double[][] initialize(List<DoubleSolution> solutionList) ;
}
