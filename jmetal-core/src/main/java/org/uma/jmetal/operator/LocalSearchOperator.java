package org.uma.jmetal.operator;

import org.uma.jmetal.solution.Solution;

/**
 * Created by cbarba on 5/3/15.
 */
public interface LocalSearchOperator <Source extends Solution> extends Operator<Source, Source> {
    /**
     * Returns the number of evaluations made by the local search operator
     */
    public abstract int getEvaluations();
    /**
     * Sets a new <code>Object</code> parameter to the operator.
     * @param name The parameter name.
     * @param value Object representing the parameter.
     */
   // public void setParameter(String name, Object value);
    /**
     * Returns an object representing a parameter of the <code>Operator</code>
     * @param name The parameter name.
     * @return the parameter.
     */
    //public Object getParameter(String name);
}
