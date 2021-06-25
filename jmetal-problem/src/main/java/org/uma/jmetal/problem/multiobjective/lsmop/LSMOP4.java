package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.multiobjective.lsmop.functions.Ackley;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Griewank;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem LSMOP4
 */

public class LSMOP4 extends AbstractLSMOP1_4 {


    /**
     * Creates a default LSMOP4 problem (7 variables and 3 objectives)
     */
    public LSMOP4() {
        this(5, 300, 3);
    }

    /**
     * Creates a LSMOP4 problem instance
     *
     * @param nk Number of subcomponents in each variable group
     * @param numberOfVariables  Number of variables
     * @param numberOfObjectives Number of objective functions
     */


    public LSMOP4(int nk, int numberOfVariables, int numberOfObjectives) throws JMetalException {
        super(nk,numberOfVariables,numberOfObjectives);
        setName("LSMOP4");
    }

    @Override
    protected Function getOddFunction() {
        return new Ackley();
    }

    @Override
    protected Function getEvenFunction() {
        return new Griewank();
    }

}
