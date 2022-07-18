package org.uma.jmetal.problem.multiobjective.lsmop.functions;

import java.util.List;

public class Rosenbrock implements Function{
    @Override
    public Double evaluate(List<Double> x) {
        var res = 0.0;
        for (var i = 1; i < x.size(); i++) {
            var op1 = Math.pow(x.get(i-1),2.0);
            double op2 = x.get(i);
            var op3 = Math.pow(x.get(i-1)-1,2.0);
            var tmp = 100.0 *  Math.pow(op1-op2,2.0) + op3;
            res += tmp;
        }
        return res;
    }
}
