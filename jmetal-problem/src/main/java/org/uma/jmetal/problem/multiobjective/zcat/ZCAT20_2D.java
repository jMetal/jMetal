package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.Collections;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F20;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G3;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G6;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ZCAT20, defined in: "Challenging test problems for multi-and many-objective optimization",
 * DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT20_2D extends ZCAT20 {

  public ZCAT20_2D() {
    super(2, 30, true, 1, false, false);
  }
}

