package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.Collections;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F19;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G6;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ZCAT9, defined in: "Challenging test problems for multi-and many-objective optimization",
 * DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT19_2D extends ZCAT19 {


  public ZCAT19_2D() {
    super(2, 30, true, 1, false, false);
  }

  @Override
  public String name() {
    return "ZCAT19_2D";
  }
}

