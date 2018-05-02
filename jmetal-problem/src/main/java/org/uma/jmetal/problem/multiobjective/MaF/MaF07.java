package org.uma.jmetal.problem.multiobjective.MaF;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * Class representing problem MaF07
 */
public class MaF07 extends AbstractDoubleProblem {

    private final String name;

    /**
     * Creates a MaF07 problem instance
     *
     * @param numberOfVariables Number of variables
     * @param numberOfObjectives Number of objective functions
     */
    public MaF07(Integer numberOfVariables,
            Integer numberOfObjectives) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setNumberOfConstraints(0);
        this.name = "MaF07";

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

//	evaluate g,h
        double g = 0, h = 0, sub1;
        for (int i = numberOfObjectives_ - 1; i < numberOfVariables_; i++) {
            g += x[i];
        }
        g = 1 + 9 * g / (numberOfVariables_ - numberOfObjectives_ + 1);
        sub1 = 1 + g;
        for (int i = 0; i < numberOfObjectives_ - 1; i++) {
            h += (x[i] * (1 + Math.sin(3 * Math.PI * x[i])) / sub1);
        }
        h = numberOfObjectives_ - h;
//	evaluate f1,...,m-1,m
        for (int i = 0; i < numberOfObjectives_; i++) {
            f[i] = x[i];
        }
        f[numberOfObjectives_ - 1] = h * sub1;

        for (int i = 0; i < numberOfObjectives_; i++) {
            solution.setObjective(i, f[i]);
        }

    }

    @Override
    public String getName() {
        return name;
    }
}
