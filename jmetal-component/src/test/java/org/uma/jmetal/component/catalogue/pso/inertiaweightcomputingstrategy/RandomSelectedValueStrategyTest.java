package org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.RandomSelectedValueStrategy;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

class RandomSelectedValueStrategyTest {

  @Test
  void constructorCreatesANonNullObject() {
    var randomSelectedValueStrategy = new RandomSelectedValueStrategy(0.1, 0.5) ;
    assertNotNull(randomSelectedValueStrategy);
  }

  @Test
  void constructorCreatesAValidInstance() {
    var lowerBound = 0.1 ;
    var upperBound = 0.5 ;
    var randomSelectedValueStrategy = new RandomSelectedValueStrategy(lowerBound, upperBound) ;
    assertEquals(lowerBound, randomSelectedValueStrategy.getLowerBound());
    assertEquals(upperBound, randomSelectedValueStrategy.getUpperBound());
  }

  @Test
  void constructorWithAnUpperBoundLowerThanALowerBoundThrowsAnException() {
    var lowerBound = 0.3 ;
    var upperBound = 0.2 ;
    assertThrows(InvalidConditionException.class, () -> new RandomSelectedValueStrategy(lowerBound, upperBound)) ;
  }

  @Test
  void computeReturnsAValidValue() {
    var lowerBound = 0.1 ;
    var upperBound = 0.5 ;
    var randomSelectedValueStrategy = new RandomSelectedValueStrategy(lowerBound, upperBound) ;
    var weight = randomSelectedValueStrategy.compute() ;
    assertTrue((weight >= lowerBound) && (weight <= upperBound));
  }

  @Test
  void computeReturnsTheLowerBoundIfBothBoundsAreEquals() {
    var lowerBound = 0.2 ;
    var upperBound = 0.2 ;
    var randomSelectedValueStrategy = new RandomSelectedValueStrategy(lowerBound, upperBound) ;
    assertEquals(lowerBound, randomSelectedValueStrategy.compute());
  }
}