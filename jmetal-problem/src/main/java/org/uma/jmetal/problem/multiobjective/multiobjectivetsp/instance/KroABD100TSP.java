package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.MultiObjectiveTSP;

public class KroABD100TSP extends MultiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public KroABD100TSP() throws IOException {
    super(List.of("resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroB100.tsp", "resources/tspInstances/kroD100.tsp"));
  }

  @Override
  public String name() {
    return "KroABD100TSP" ;
  }
}
