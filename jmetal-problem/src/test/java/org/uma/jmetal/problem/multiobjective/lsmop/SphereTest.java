package org.uma.jmetal.problem.multiobjective.lsmop;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.multiobjective.lsmop.functions.Function;

class SphereTest {

  @Test
  void shouldEvaluateWorkProperly() {
    Function function = new org.uma.jmetal.problem.multiobjective.lsmop.functions.Sphere();
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
    DecimalFormat df = new DecimalFormat("#.#");
    // 285 is the value computed with the reference Matlab implementation
    assertEquals(df.format(285.0), df.format(result));

  }
}
