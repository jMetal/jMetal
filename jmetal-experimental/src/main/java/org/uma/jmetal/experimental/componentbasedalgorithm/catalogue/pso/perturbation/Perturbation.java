package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

public interface Perturbation {
  List<DoubleSolution> perturb(List<DoubleSolution> swarm) ;
}
