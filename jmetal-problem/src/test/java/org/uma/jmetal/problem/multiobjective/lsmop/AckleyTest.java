package org.uma.jmetal.problem.multiobjective.lsmop;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AckleyTest {

    @Test
    public void shouldEvaluateWorkProperly() {
        Function function = new org.uma.jmetal.problem.multiobjective.lsmop.functions.Ackley();
        List<Double> x = new ArrayList<>(10);
        x.add(0.0);
        x.add(1.0);
        x.add(2.0);
        x.add(3.0);
        x.add(4.0);
        x.add(5.0);
        x.add(6.0);
        x.add(7.0);
        x.add(8.0);
        x.add(9.0);
        double result = function.evaluate(x);
        DecimalFormat df = new DecimalFormat("#.###");

        // 13.124 is the value computed with the reference Matlab implementation
        assertEquals(df.format(13.124), df.format(result));

    }
}


