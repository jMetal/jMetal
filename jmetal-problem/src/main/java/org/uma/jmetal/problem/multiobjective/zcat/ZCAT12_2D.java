package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.Collections;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F12;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G10;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ZCAT12, defined in: "Challenging test problems for multi-and many-objective optimization",
 * DOI: https://doi.org/10.1016/j.swevo.2023.101350
 */
public class ZCAT12_2D extends ZCAT12 {


  public ZCAT12_2D() {
    super(2, 30, true, 1, false, false);
  }

  @Override
  public String name() {
    return "ZCAT12_2D";
  }
}

