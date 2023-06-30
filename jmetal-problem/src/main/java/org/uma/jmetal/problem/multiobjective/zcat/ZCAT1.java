package org.uma.jmetal.problem.multiobjective.zcat;

import java.util.function.Function;
import org.uma.jmetal.problem.multiobjective.zcat.ffunction.F1;
import org.uma.jmetal.problem.multiobjective.zcat.gfunction.G0;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ZCAT1 extends ZCAT {

  private Function<double[], double[]> fFunction ;
  private Function<double[], double[]> gFunction ;

  public ZCAT1(int numberOfObjectives, int numberOfVariables, int complicatedParetoSet, int level,
      int bias, int imbalance) {
    super(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance);

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

