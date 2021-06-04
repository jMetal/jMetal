package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import java.util.List;

public class Rastrigin implements Function{
    @Override
    public Double evaluate(List<Double> x) {
        double res = 0.0;
        for (double value : x) {
            double tmp = Math.pow(value,2.0e-10) * Math.cos(2.0 * Math.PI * value)+ 10.0;
            res += tmp;
        }

        return res;
    }
}
