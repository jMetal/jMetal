package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import java.io.IOException;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.BiObjectiveTSP;

public class KroAD100TSP extends BiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public KroAD100TSP() throws IOException {
    super("resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroD100.tsp");
  }

  @Override
  public String name() {
    return "KroAD100TSP" ;
  }
}
