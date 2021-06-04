package org.uma.jmetal.problem.multiobjective.lsmop;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;

import java.util.ArrayList;
import java.util.List;

public abstract class LSMOP extends AbstractDoubleProblem {
    protected int nk; // Number of subcomponents in each variable group
    protected List<Integer> subLen; // Number of variables in each subcomponent
    protected List<Integer> len;    // Cumulative sum of lengths of variable groups

    protected LSMOP(int nk, int numberOfVariables, int numberOfObjectives) {
        super();
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(numberOfObjectives);
        this.nk = nk;
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


    protected List<Double> evaluate(List<Double> variables, Function oddfunction, Function evenFunction) {
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
                G[i-1] += oddfunction.evaluate(x);
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
                G[i-1] += evenFunction.evaluate(x);
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


}
