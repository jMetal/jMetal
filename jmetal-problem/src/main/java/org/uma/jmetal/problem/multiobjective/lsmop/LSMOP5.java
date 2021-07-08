package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Sphere;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem LSMOP5
 */

public class LSMOP5 extends AbstractLSMOP5_8 {


    /**
     * Creates a default LSMOP5 problem (7 variables and 3 objectives)
     */
    public LSMOP5() {
        this(5, 300, 3);
    }

    /**
     * Creates a LSMOP5 problem instance
     *
     * @param nk Number of subcomponents in each variable group
     * @param numberOfVariables  Number of variables
     * @param numberOfObjectives Number of objective functions
     */


    public LSMOP5(int nk, int numberOfVariables, int numberOfObjectives) throws JMetalException {
        super(nk,numberOfVariables,numberOfObjectives);
        setName("LSMOP5");
    }

    @Override
    protected Function getOddFunction() {
        return new Sphere();
    }

    @Override
    protected Function getEvenFunction() {
        return new Sphere();
    }


}


