package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.function.Function;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F1;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ZCAT3 extends ZCAT {

  private Function<double[], double[]> fFunction ;
  private Function<double[], double[]> gFunction ;

  public ZCAT3(int numberOfObjectives, int numberOfVariables) {
    super(numberOfObjectives, numberOfVariables, true, 1, true, false);

    int paretoSetDimension = numberOfObjectives - 1 ;

    fFunction = new F1(numberOfObjectives) ;
    gFunction = new G0(numberOfVariables, paretoSetDimension) ;


  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] normalizedVariables = zcatGetY(solution.variables()) ;
    double[] alpha = zcatGetAlpha(normalizedVariables, numberOfObjectives, fFunction) ;

    return null;
  }
}

