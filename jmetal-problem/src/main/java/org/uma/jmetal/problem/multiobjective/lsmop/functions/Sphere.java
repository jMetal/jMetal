package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import java.util.List;

public class Sphere implements Function{
    @Override
    public Double evaluate(List<Double> x) {
        double res = 0.0;
        for (int i = 0; i < x.size(); i++)
        {
            res += Math.pow(x.get(i),2.0);
        }
        return res;

    }
}
