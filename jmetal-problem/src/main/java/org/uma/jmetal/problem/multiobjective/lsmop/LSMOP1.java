package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem LSMOP1
 */

public class LSMOP1 extends AbstractDoubleProblem {

    private int nk; // Number of subcomponents in each variable group
    private List<Integer> subLen; // Number of variables in each subcomponent
    private List<Integer> len;    // Cumulative sum of lengths of variable groups

    /**
     * Creates a default LSMOP1 problem (7 variables and 3 objectives)
     */
    public LSMOP1() {
        this(5, 300, 3);
    }

    /**
     * Creates a LSMOP1 problem instance
     *
     * @param nk Number of subcomponents in each variable group
     * @param numberOfVariables  Number of variables
     * @param numberOfObjectives Number of objective functions
     */


    public LSMOP1(int nk, int numberOfVariables, int numberOfObjectives) throws JMetalException {
        super();

        this.nk = nk;
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        setName("LSMOP1");

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

        double c =  3.8*0.1*(1-0.1);
        double sum = c;

        List<Double> c_list = new ArrayList<>(getNumberOfObjectives());
        c_list.add(c);
        for (int i = 0; i < getNumberOfObjectives()-1;i++) {
            c = 3.8 * c * (1.0 - c);
            c_list.add(c);
            sum += c;
        }

        this.subLen = new ArrayList<>(getNumberOfObjectives());
        for (int i = 0; i < getNumberOfObjectives(); i++)
        {
            int aux = (int) Math.floor(c_list.get(i) / sum * (getNumberOfVariables()-getNumberOfObjectives()+1)/this.nk);
            subLen.add(aux);
        }

        len = new ArrayList<>(subLen.size()+1);
        len.add(0);
        int cum = 0;
        for (int i = 0; i < getNumberOfObjectives();i++) {
            cum += subLen.get(i)* this.nk;
            this.len.add(cum);
        }
    }


    private double Sphere(List<Double> x) {
        double res = 0.0;
        for (int i = 0; i < x.size(); i++)
        {
            res += Math.pow(x.get(i),2.0);
        }
        return res;
    }


    private List<Double> evaluate(List<Double> variables) {
        double [] G = new double[getNumberOfObjectives()];

        for (int i = getNumberOfObjectives(); i <= getNumberOfVariables();i++) {
            double aux  = (1.0 + (double)i / (double) getNumberOfVariables()) * variables.get(i-1);
            aux         = aux - variables.get(0)*10;
            variables.set(i-1,aux);
        }

        for (int i = 0; i < getNumberOfObjectives(); i++) {
            G[i] = 0.0;
        }

        for (int i = 1; i <= getNumberOfObjectives(); i+=2) {
            for (int j = 1; j <= this.nk; j++) {

                List<Double> x = new ArrayList<>(getNumberOfVariables());
                for (int k = len.get(i-1) + getNumberOfObjectives() - 1 + (j-1) * subLen.get(i-1) +1;
                     k <= len.get(i-1)   + getNumberOfObjectives()  -1 +  j    * subLen.get(i-1);
                     k++) {
                    x.add(variables.get(k-1));
                }
                G[i-1] += Sphere(x);
            }
        }

        for (int i = 2; i <= getNumberOfObjectives(); i+=2) {
            for (int j = 1; j <= this.nk; j++) {

                List<Double> x = new ArrayList<>(getNumberOfVariables());

                for (int k = len.get(i-1) + getNumberOfObjectives() - 1 + (j-1) * subLen.get(i-1) +1;
                     k <= len.get(i-1)   + getNumberOfObjectives()  -1 +  j    * subLen.get(i-1);
                     k++) {
                    x.add(variables.get(k-1));
                }
                G[i-1] += Sphere(x);
            }
        }

        for (int i = 0; i < G.length; i++) {
            G[i] = G[i] / this.subLen.get(i) / this.nk;
        }

        List<Double> leftHand  = new ArrayList<>(getNumberOfObjectives());
        List<Double> rightHand = new ArrayList<>(getNumberOfObjectives());

        leftHand.add(1.0);
        for (int i = 1; i <= getNumberOfObjectives()-1;i++) {
            leftHand.add(variables.get(i-1));
        }
        double cum = 1.0;
        for (int i = 1; i <= getNumberOfObjectives();i++)
        {
            cum = cum * variables.get(i-1);
            variables.set(i-1,cum);
        }

        rightHand.add(1.0);
        for (int i = getNumberOfObjectives()-1; i >= 1; i--) {
            rightHand.add(1.0-variables.get(i-1));
        }
        List<Double> operand = new ArrayList<>();

        for (int i = 0 ; i < getNumberOfObjectives(); i++) {
            operand.add(leftHand.get(i)*rightHand.get(i));
        }

        List<Double> inverted = new ArrayList<>();
        for (int i = 0; i < getNumberOfObjectives(); i++) {
            inverted.add(leftHand.get(leftHand.size()-i-1));
        }

        List<Double> y = new ArrayList<>(getNumberOfObjectives());
        for (int i = 0 ; i < getNumberOfObjectives(); i++) {
            y.add((1.0+G[i]) * inverted.get(i));
        }
        return y;
    }


    @java.lang.Override
    public DoubleSolution evaluate(DoubleSolution solution) {
        List<Double> variables = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            variables.add(solution.variables().get(i));
        }
        List<Double> y = evaluate(variables);

        for (int i = 0; i < getNumberOfObjectives(); i++) {
            solution.objectives()[i] = y.get(i);
        }
        return solution;
    }

}
