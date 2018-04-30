package org.uma.jmetal.problem.multiobjective.MaF;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * Class representing problem MaF02, DTLZ2BZ
 */
public class MaF02 extends AbstractDoubleProblem {

    public static int const2;

    private final String name;

    /**
     * Creates a MaF02 problem instance
     *
     * @param numberOfVariables Number of variables
     * @param numberOfObjectives Number of objective functions
     */
    public MaF02(Integer numberOfVariables,
            Integer numberOfObjectives) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setNumberOfConstraints(0);
        this.name = "MaF02";

        const2 = (int) Math.floor((numberOfVariables - numberOfObjectives + 1) / (double) numberOfObjectives);

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

        double[] g = new double[numberOfObjectives_];
        double[] thet = new double[numberOfObjectives_ - 1];
        int lb, ub;
//	evaluate g,thet
        for (int i = 0; i < numberOfObjectives_ - 1; i++) {
            g[i] = 0;
            lb = numberOfObjectives_ + i * const2;
            ub = numberOfObjectives_ + (i + 1) * const2 - 1;
            for (int j = lb - 1; j < ub; j++) {
                g[i] += Math.pow(x[j] / 2 - 0.25, 2);
            }
            thet[i] = Math.PI / 2 * (x[i] / 2 + 0.25);
        }
        lb = numberOfObjectives_ + (numberOfObjectives_ - 1) * const2;
        ub = numberOfVariables_;
        for (int j = lb - 1; j < ub; j++) {
            g[numberOfObjectives_ - 1] += Math.pow(x[j] / 2 - 0.25, 2);
        }
//	evaluate fm,fm-1,...,2,f1
        f[numberOfObjectives_ - 1] = Math.sin(thet[0]) * (1 + g[numberOfObjectives_ - 1]);
        double subf1 = 1, subf2, subf3;
//	fi=cos(thet1)cos(thet2)...cos(thet[m-i])*sin(thet(m-i+1))*(1+g[i]),fi=subf1*subf2*subf3
        for (int i = numberOfObjectives_ - 2; i > 0; i--) {
            subf1 *= Math.cos(thet[numberOfObjectives_ - i - 2]);
            f[i] = subf1 * Math.sin(thet[numberOfObjectives_ - i - 1]) * (1 + g[i]);
        }
        f[0] = subf1 * Math.cos(thet[numberOfObjectives_ - 2]) * (1 + g[0]);

        for (int i = 0; i < numberOfObjectives_; i++) {
            solution.setObjective(i, f[i]);
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
