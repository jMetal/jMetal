package component.catalogue.pso.velocityupdate;

import java.util.List;
import component.catalogue.pso.globalbestselection.GlobalBestSelection;
import component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;

public interface VelocityUpdate {

  double[][] update(
      List<DoubleSolution> swarm,
      double[][] speed, DoubleSolution[] localBest,
      BoundedArchive<DoubleSolution> globalBest,
      GlobalBestSelection globalBestSelection,
      InertiaWeightComputingStrategy inertiaWeightComputingStrategy);
}
