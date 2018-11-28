package org.uma.jmetal.util.neighborhood.impl;

import org.junit.Test;
import org.uma.jmetal.solution.IntegerSolution;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by ajnebro on 26/5/15.
 */
public class L5Test {

  /**
   * Case 1
   *
   * Solution list:
   * 0
   *
   * The solution location is 0, the neighborhood is 0
   */
  @Test
  public void shouldGetNeighborsReturnFourNeighborsCase1() {
    int rows = 1 ;
    int columns = 1 ;
    L5<IntegerSolution> neighborhood = new L5<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;

    IntegerSolution solution = mock(IntegerSolution.class) ;
    list.add(solution) ;

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItem(list.get(0))) ;
    assertEquals(4, result.stream().filter(s -> s == solution).count());
  }

  /**
   * Case 2
   *
   * Solution list:
   * 0 1
   *
   * The solution location is 0, the neighborhood is 0, 1
   */
  @Test
  public void shouldGetNeighborsReturnFourNeighborsCase2() {
    int rows = 1 ;
    int columns = 2 ;
    L5<IntegerSolution> neighborhood = new L5<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItems(list.get(0), list.get(1))) ;
    assertEquals(2, result.stream().filter(s -> s == list.get(0)).count());
    assertEquals(2, result.stream().filter(s -> s == list.get(1)).count());
  }

  /**
   * Case 3
   *
   * Solution list:
   * 0 1
   *
   * The solution location is 1, the neighborhood is 0, 1
   */
  @Test
  public void shouldGetNeighborsReturnFourNeighborsCase3() {
    int rows = 1 ;
    int columns = 2 ;
    L5<IntegerSolution> neighborhood = new L5<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 1) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItems(list.get(0), list.get(1))) ;
    assertEquals(2, result.stream().filter(s -> s == list.get(0)).count());
    assertEquals(2, result.stream().filter(s -> s == list.get(1)).count());
  }

  /**
   * Case 4
   *
   * Solution list:
   * 0 1
   * 2 3
   *
   * The solution location is 0, the neighborhood is 1, 2
   */
  @Test
  public void shouldGetNeighborsReturnFourNeighborsCase4() {
    int rows = 2 ;
    int columns = 2 ;
    L5<IntegerSolution> neighborhood = new L5<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItems(list.get(1), list.get(2))) ;
    assertThat(result, not(hasItems(list.get(3), list.get(0)))) ;
    assertEquals(2, result.stream().filter(s -> s == list.get(1)).count());
    assertEquals(2, result.stream().filter(s -> s == list.get(2)).count());
  }
}
