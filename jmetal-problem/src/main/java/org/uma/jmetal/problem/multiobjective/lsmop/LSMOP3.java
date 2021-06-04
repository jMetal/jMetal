package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Rastrigin;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Rosenbrock;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Sphere;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem LSMOP3
 */

public class LSMOP3 extends LSMOP {


    /**
     * Creates a default LSMOP1 problem (7 variables and 3 objectives)
     */
    public LSMOP3() {
        this(5, 300, 3);
    }

    /**
     * Creates a LSMOP1 problem instance
     *
     * @param nk Number of subcomponents in each variable group
     * @param numberOfVariables  Number of variables
     * @param numberOfObjectives Number of objective functions
     */


    public LSMOP3(int nk, int numberOfVariables, int numberOfObjectives) throws JMetalException {
        super(nk,numberOfVariables,numberOfObjectives);
        setName("LSMOP3");

        List<Double> lowerLimit = new ArrayList<Double>(getNumberOfVariables()) ;
        List<Double> upperLimit = new ArrayList<Double>(getNumberOfVariables()) ;

        for (int i = 0; i < getNumberOfObjectives()-1; i++) {
            lowerLimit.add(0.0);
            upperLimit.add(1.0);
        }

        for (int i = getNumberOfObjectives()-1; i < getNumberOfVariables(); i++) {
            lowerLimit.add(0.0);
            upperLimit.add(10.0);
        }

        setVariableBounds(lowerLimit, upperLimit);
    }


    @java.lang.Override
    public DoubleSolution evaluate(DoubleSolution solution) {
        List<Double> variables = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            variables.add(solution.variables().get(i));
        }
        List<Double> y = evaluate(variables,new Rastrigin(),new Rosenbrock());

        for (int i = 0; i < getNumberOfObjectives(); i++) {
            solution.objectives()[i] = y.get(i);
        }
        return solution;
    }

}
