package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import java.util.List;

public class Sphere implements Function{
    @Override
    public Double evaluate(List<Double> x) {
        double res = 0.0;
        for (Double aDouble : x) {
            double pow = Math.pow(aDouble, 2.0);
            res += pow;
        }
        return res;

    }
}
