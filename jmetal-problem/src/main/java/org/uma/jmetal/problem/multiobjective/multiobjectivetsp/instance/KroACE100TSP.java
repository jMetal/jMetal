package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.MultiObjectiveTSP;

public class KroACE100TSP extends MultiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public KroACE100TSP() throws IOException {
    super(List.of("resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroC100.tsp", "resources/tspInstances/kroE100.tsp"));
  }

  @Override
  public String name() {
    return "KroACE100TSP" ;
  }
}
