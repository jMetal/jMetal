package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import java.util.List;

public class Sphere implements Function{
    @Override
    public Double evaluate(List<Double> x) {
        double res = x.stream().mapToDouble(aDouble -> Math.pow(aDouble, 2.0)).sum();
        return res;

    }
}
