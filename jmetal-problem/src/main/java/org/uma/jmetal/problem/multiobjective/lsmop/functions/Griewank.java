package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import java.util.List;

public class Griewank implements Function {
    @Override
    public Double evaluate(List<Double> x) {
        double res = x.stream().mapToDouble(value -> value).map(value -> Math.pow(value, 2.0) / 4000.0).sum();

        double aux = 1.0;
        for (int i = 1; i <= x.size(); i++) {
            double tmp = Math.sqrt(i);
            tmp = x.get(i-1) / tmp;
            tmp = Math.cos(tmp);
            aux *= tmp;
        }

        res = res - aux + 1;
        return res;
    }
}
