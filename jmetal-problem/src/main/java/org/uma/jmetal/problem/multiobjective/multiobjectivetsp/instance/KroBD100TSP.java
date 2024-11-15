package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import java.io.IOException;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.BiObjectiveTSP;

public class KroBD100TSP extends BiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public KroBD100TSP() throws IOException {
    super("resources/tspInstances/kroB100.tsp", "resources/tspInstances/kroAD100.tsp");
  }

  @Override
  public String name() {
    return "KroBD100TSP" ;
  }
}
