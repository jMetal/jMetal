package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.impl.ConstantValueStrategy;

class ConstantValueStrategyTest {

  @Test
  public void shouldConstructorCreateANotNullObject() {
    ConstantValueStrategy constantValueStrategy = new ConstantValueStrategy(0.1);
    assertNotNull(constantValueStrategy);
  }

  @Test
  public void shouldConstructorCreateValidInstance() {
    double weight = 0.1 ;
    ConstantValueStrategy constantValueStrategy = new ConstantValueStrategy(weight);
    assertEquals(weight, constantValueStrategy.getInertiaWeightValue());
  }
}