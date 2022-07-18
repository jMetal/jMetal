package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Rastrigin implements Function{
    @Override
    public Double evaluate(@NotNull List<Double> x) {
        var res = 0.0;
        for (var value : x) {
            double v = value;
            var v1 = Math.pow(v, 2.0) - 10 * Math.cos(2.0 * Math.PI * v) + 10.0;
            res += v1;
        }

        return res;
    }
}
