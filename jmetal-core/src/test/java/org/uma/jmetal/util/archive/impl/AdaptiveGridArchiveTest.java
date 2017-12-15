package org.uma.jmetal.util.archive.impl;

import org.junit.Test;
import org.uma.jmetal.solution.IntegerSolution;

import static org.junit.Assert.assertEquals;

/**
 * Created by ajnebro on 16/11/16.
 */
public class AdaptiveGridArchiveTest {
  @Test
  public void shouldConstructorCreateAnArchiveWithTheRightCapacity() {
    AdaptiveGridArchive<IntegerSolution> archive ;

    int capacity = 100 ;
    archive = new AdaptiveGridArchive<>(100, 2, 2) ;

    assertEquals(capacity, archive.getMaxSize()) ;
  }

  @Test
  public void shouldConstructorCreateAnEmptyArchive() {
    AdaptiveGridArchive<IntegerSolution> archive ;

    archive = new AdaptiveGridArchive<>(100, 2, 2) ;

    assertEquals(0, archive.size()) ;
  }

  @Test
  public void shouldProneDoNothingIfTheArchiveIsEmpty() {
    AdaptiveGridArchive<IntegerSolution> archive ;

    archive = new AdaptiveGridArchive<>(4, 2, 2) ;
    archive.prune();

    assertEquals(0, archive.size()) ;
  }
}