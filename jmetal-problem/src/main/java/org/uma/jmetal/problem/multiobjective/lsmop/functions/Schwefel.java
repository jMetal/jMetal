package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import java.util.List;

public class Schwefel implements Function{
    @Override
    public Double evaluate(List<Double> x) {
        var res = Double.NEGATIVE_INFINITY;
        for (double value : x) {
            res = Math.max(Math.abs(value),res);
        }

        return res;
    }
}
