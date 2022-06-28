package component.catalogue.pso.localbestinitialization;

import java.util.List;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * TODO: comment the interface
 *
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */
public interface LocalBestInitialization {
  DoubleSolution[] initialize(List<DoubleSolution> swarm) ;
}
