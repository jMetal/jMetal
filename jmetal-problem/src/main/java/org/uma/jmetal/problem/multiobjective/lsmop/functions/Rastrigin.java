package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import java.util.List;

public class Rastrigin implements Function{
    @Override
    public Double evaluate(List<Double> x) {
        double res = 0.0;
        for (Double value : x) {
            double v = value;
            double v1 = Math.pow(v, 2.0) - 10 * Math.cos(2.0 * Math.PI * v) + 10.0;
            res += v1;
        }

        return res;
    }
}
