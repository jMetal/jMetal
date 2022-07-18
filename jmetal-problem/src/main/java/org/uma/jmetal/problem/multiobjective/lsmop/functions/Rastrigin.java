package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import java.util.List;

public class Rastrigin implements Function{
    @Override
    public Double evaluate(List<Double> x) {
        double res = x.stream().mapToDouble(value -> value).map(value -> Math.pow(value, 2.0) - 10 * Math.cos(2.0 * Math.PI * value) + 10.0).sum();

        return res;
    }
}
