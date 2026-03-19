package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import java.io.IOException;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.BiObjectiveTSP;

public class KroAB100TSP extends BiObjectiveTSP {

  /** Creates a new MultiobjectiveTSP problem instance */
  public KroAB100TSP() throws IOException {
    super("resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroB100.tsp");
  }

  @Override
  public String name() {
    return "KroAB100TSP" ;
  }
}
