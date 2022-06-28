package component.catalogue.pso.velocityinitialization;

import java.util.List;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * TODO: description missing
 * @author Daniel Doblas
 * @author Antonio J. Nebro
 */
public interface VelocityInitialization {
  double[][] initialize(List<DoubleSolution> solutionList) ;
}
