package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import java.util.List;

public class Ackley implements Function {
    @Override
    public Double evaluate(List<Double> x) {

        double operand1 = 0.0, sum1 = 0.0;
        double operand2 = 0.0, sum2 = 0.0;

        for (double value : x) {
            sum1 += Math.pow(value,2.0);
            sum2 += Math.cos(2*Math.PI*value);
        }
        operand1 = Math.exp(-0.2 * Math.sqrt(sum1/x.size()));
        operand2 = Math.exp(sum2 / x.size());

        return 20 - 20 * operand1 - operand2 + Math.exp(1.0);
    }
}
