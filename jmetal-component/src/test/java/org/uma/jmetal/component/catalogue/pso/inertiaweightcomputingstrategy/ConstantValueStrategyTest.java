package org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.ConstantValueStrategy;

class ConstantValueStrategyTest {

  @Test
  void constructorCreatesANotNullObject() {
    var constantValueStrategy = new ConstantValueStrategy(0.1);
    assertNotNull(constantValueStrategy);
  }

  @Test
  void constructorCreatesAValidInstance() {
    var weight = 0.1 ;
    var constantValueStrategy = new ConstantValueStrategy(weight);
    assertEquals(weight, constantValueStrategy.getInertiaWeightValue());
  }
}