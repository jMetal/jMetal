package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import java.io.IOException;
import java.util.List;

import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.BiObjectiveTSP;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.MultiObjectiveTSP;

public class KroABC100TSP extends MultiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public KroABC100TSP() throws IOException {
    super(List.of("resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroB100.tsp", "resources/tspInstances/kroC100.tsp"));
  }

  @Override
  public String name() {
    return "KroABC100TSP" ;
  }
}
