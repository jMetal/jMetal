package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem CrashEnergyManagementProblem (RCM21)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM21CrashEnergyManagementProblem extends AbstractDoubleProblem {

  public RCM21CrashEnergyManagementProblem() {
    numberOfObjectives(2);
    numberOfConstraints(4);
    name("CrashEnergyManagementProblem");

    List<Double> lowerLimit = Arrays.asList(1.3, 2.5, 1.3, 1.3, 1.3, 1.3);
    List<Double> upperLimit = Arrays.asList(1.7, 3.5, 1.7, 1.7, 1.7, 1.7);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);
    double x5 = solution.variables().get(4);
    double x6 = solution.variables().get(5);

    double f1 = 1.3667145844797
        - 0.00904459793976106 * x1 - 0.0016193573938033 * x2 - 0.00758531275221425 * x3
        - 0.00440727360327102 * x4 - 0.00572216860791644 * x5 - 0.00936039926190721 * x6
        + 2.62510221107328e-6 * (x1 * x1) + 4.92982681358861e-7 * (x2 * x2)
        + 2.25524989067108e-6 * (x3 * x3) + 1.84605439400301e-6 * (x4 * x4)
        + 2.17175358243416e-6 * (x5 * x5) + 3.90158043948054e-6 * (x6 * x6)
        + 4.55276994245781e-7 * x1 * x2 - 6.37013576290982e-7 * x1 * x3
        + 8.26736480446359e-7 * x1 * x4 + 5.66352809442276e-8 * x1 * x5
        - 3.20213897443278e-7 * x1 * x6 + 1.18015467772812e-8 * x2 * x3
        + 9.25820391546515e-8 * x2 * x4 - 1.05705364119837e-7 * x2 * x5
        - 4.74797783014687e-7 * x2 * x6 - 5.02319867013788e-7 * x3 * x4
        + 9.54284258085225e-7 * x3 * x5 + 1.80533309229454e-7 * x3 * x6
        - 1.07938022118477e-6 * x4 * x5 - 1.81370642220182e-7 * x4 * x6
        - 2.24238851688047e-7 * x5 * x6;

    double f2 = -1.19896668942683
        + 3.04107017009774 * x1 + 1.23535701600191 * x2 + 2.13882039381528 * x3
        + 2.33495178382303 * x4 + 2.68632494801975 * x5 + 3.43918953617606 * x6
        - 7.89144544980703e-4 * (x1 * x1) - 2.06085185698215e-4 * (x2 * x2)
        - 7.15269900037858e-4 * (x3 * x3) - 7.8449237573837e-4 * (x4 * x4)
        - 9.31396896237177e-4 * (x5 * x5) - 1.40826531972195e-3 * (x6 * x6)
        - 1.60434988248392e-4 * x1 * x2 + 2.0824655419411e-4 * x1 * x3
        - 3.0530659653553e-4 * x1 * x4 - 8.10145973591615e-5 * x1 * x5
        + 6.94728759651311e-5 * x1 * x6 + 1.18015467772812e-8 * x2 * x3
        + 9.25820391546515e-8 * x2 * x4 - 1.05705364119837e-7 * x2 * x5
        + 1.69935290196781e-4 * x2 * x6 + 2.32421829190088e-5 * x3 * x4
        - 2.0808624041163476e-4 * x3 * x5 + 1.75576341867273e-5 * x3 * x6
        + 2.68422081654044e-4 * x4 * x5 + 4.39852066801981e-5 * x4 * x6
        + 2.96785446021357e-5 * x5 * x6;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;

    double g1 = f1 - 5;
    double g2 = -f1;
    double g3 = f2 - 28;
    double g4 = -f2;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;
    solution.constraints()[2] = -g3;
    solution.constraints()[3] = -g4;

    return solution;
  }
}
