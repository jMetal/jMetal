package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.multiobjective.lsmop.functions.Ackley;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Rosenbrock;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Sphere;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.ArrayList;
import java.util.List;

public class LSMOP7 extends AbstractLSMOP5_8 {

    /**
     * Creates a default LSMOP7 problem (7 variables and 3 objectives)
     */
    public LSMOP7() {
        this(5, 300, 3);
    }

    /**
     * Creates a LSMOP7 problem instance
     *
     * @param nk Number of subcomponents in each variable group
     * @param numberOfVariables  Number of variables
     * @param numberOfObjectives Number of objective functions
     */


    public LSMOP7(int nk, int numberOfVariables, int numberOfObjectives) throws JMetalException {
        super(nk,numberOfVariables,numberOfObjectives);
        setName("LSMOP7");
    }

    @Override
    protected Function getOddFunction() {
        return new Ackley();
    }

    @Override
    protected Function getEvenFunction() {
        return new Rosenbrock();
    }
}
