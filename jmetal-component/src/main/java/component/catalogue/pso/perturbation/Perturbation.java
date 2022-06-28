package component.catalogue.pso.perturbation;

import java.util.List;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public interface Perturbation {
  List<DoubleSolution> perturb(List<DoubleSolution> swarm) ;
}
