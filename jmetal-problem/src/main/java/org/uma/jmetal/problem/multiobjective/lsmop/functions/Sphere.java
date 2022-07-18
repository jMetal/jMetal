package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Sphere implements Function{
    @Override
    public @NotNull Double evaluate(@NotNull List<Double> x) {
        double res = 0.0;
        for (Double aDouble : x) {
            double pow = Math.pow(aDouble, 2.0);
            res += pow;
        }
        return res;

    }
}
