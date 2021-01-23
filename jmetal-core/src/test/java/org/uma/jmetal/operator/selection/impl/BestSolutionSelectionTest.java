package org.uma.jmetal.operator.selection.impl;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BestSolutionSelectionTest {
  @Test
  public void shouldConstructorCreateANonNullObject() {
    var operator = new BestSolutionSelection<>(new ObjectiveComparator<>(0));

    assertNotNull(operator);
  }
}
