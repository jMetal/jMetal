package org.uma.jmetal.problem.multiobjective.MaF;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * Class representing problem MaF03, convex DTLZ3
 */
public class MaF03 extends AbstractDoubleProblem {

    private final String name;

    /**
     * Creates a MaF03 problem instance
     *
     * @param numberOfVariables Number of variables
     * @param numberOfObjectives Number of objective functions
     */
    public MaF03(Integer numberOfVariables,
            Integer numberOfObjectives) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setNumberOfConstraints(0);
        this.name = "MaF03";

        List<Double> lower = new ArrayList<>(getNumberOfVariables()), upper = new ArrayList<>(getNumberOfVariables());

        for (int var = 0; var < numberOfVariables; var++) {
            lower.add(0.0);
            upper.add(1.0);
        } //for

        setLowerLimit(lower);
        setUpperLimit(upper);

    }

    /**
     * Evaluates a solution
     *
     * @param solution The solution to evaluate
     */
    @Override
    public void evaluate(DoubleSolution solution) {

        int numberOfVariables_ = solution.getNumberOfVariables();
        int numberOfObjectives_ = solution.getNumberOfObjectives();

        double[] x = new double[numberOfVariables_];
        double[] f = new double[numberOfObjectives_];

        for (int i = 0; i < numberOfVariables_; i++) {
            x[i] = solution.getVariableValue(i);
        }

        double g = 0;
//	evaluate g
        for (int i = numberOfObjectives_ - 1; i < numberOfVariables_; i++) {
            g += (Math.pow(x[i] - 0.5, 2) - Math.cos(20 * Math.PI * (x[i] - 0.5)));
        }
        g = 100 * (numberOfVariables_ - numberOfObjectives_ + 1 + g);
        double subf1 = 1, subf3 = 1 + g;
//	evaluate fm,fm-1,...2,f1
        f[numberOfObjectives_ - 1] = Math.pow(Math.sin(Math.PI * x[0] / 2) * subf3, 2);
//	f=(subf1*subf2*subf3)^4
        for (int i = numberOfObjectives_ - 2; i > 0; i--) {
            subf1 *= Math.cos(Math.PI * x[numberOfObjectives_ - i - 2] / 2);
            f[i] = Math.pow(subf1 * Math.sin(Math.PI * x[numberOfObjectives_ - i - 1] / 2) * subf3, 4);
        }
        f[0] = Math.pow(subf1 * Math.cos(Math.PI * x[numberOfObjectives_ - 2] / 2) * subf3, 4);

        for (int i = 0; i < numberOfObjectives_; i++) {
            solution.setObjective(i, f[i]);
        }

    }

    @Override
    public String getName() {
        return name;
    }

}
