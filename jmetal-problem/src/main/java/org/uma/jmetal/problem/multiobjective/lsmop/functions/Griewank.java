package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Griewank implements Function {
    @Override
    public Double evaluate(@NotNull List<Double> x) {
        var res = 0.0;
        for (var value : x) {
            double v = value;
            var v1 = Math.pow(v, 2.0) / 4000.0;
            res += v1;
        }

        var aux = 1.0;
        for (var i = 1; i <= x.size(); i++) {
            var tmp = Math.sqrt(i);
            tmp = x.get(i-1) / tmp;
            tmp = Math.cos(tmp);
            aux *= tmp;
        }

        res = res - aux + 1;
        return res;
    }
}
