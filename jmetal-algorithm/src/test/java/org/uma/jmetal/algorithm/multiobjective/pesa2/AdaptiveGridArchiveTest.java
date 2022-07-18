package org.uma.jmetal.algorithm.multiobjective.pesa2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.uma.jmetal.algorithm.multiobjective.pesa2.util.AdaptiveGridArchive;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

/**
 * Created by ajnebro on 16/11/16.
 */
public class AdaptiveGridArchiveTest {
  @Test
  public void shouldConstructorCreateAnArchiveWithTheRightCapacity() {

      var capacity = 100 ;
      var archive = new AdaptiveGridArchive<IntegerSolution>(100, 2, 2);

    assertEquals(capacity, archive.getMaxSize()) ;
  }

  @Test
  public void shouldConstructorCreateAnEmptyArchive() {

      var archive = new AdaptiveGridArchive<IntegerSolution>(100, 2, 2);

    assertEquals(0, archive.size()) ;
  }

  @Test
  public void shouldProneDoNothingIfTheArchiveIsEmpty() {

      var archive = new AdaptiveGridArchive<IntegerSolution>(4, 2, 2);
    archive.prune();

    assertEquals(0, archive.size()) ;
  }
}