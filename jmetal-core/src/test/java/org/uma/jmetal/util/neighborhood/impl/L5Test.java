package org.uma.jmetal.util.neighborhood.impl;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

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
      var rows = 1 ;
      var columns = 1 ;
      var neighborhood = new L5<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;

      var solution = mock(IntegerSolution.class) ;
    list.add(solution) ;

      var result = neighborhood.getNeighbors(list, 0) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItem(list.get(0))) ;
      var count = 0L;
      for (var s : result) {
          if (s == solution) {
              count++;
          }
      }
      assertEquals(4, count);
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
      var rows = 1 ;
      var columns = 2 ;
      var neighborhood = new L5<IntegerSolution>(rows, columns) ;

      List<IntegerSolution> list = new ArrayList<>(rows * columns);
      var bound = rows * columns;
      for (var i = 0; i < bound; i++) {
          var mock = mock(IntegerSolution.class);
          list.add(mock);
      }

      var result = neighborhood.getNeighbors(list, 0) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItems(list.get(0), list.get(1))) ;
      var count1 = 0L;
      for (var integerSolution : result) {
          if (integerSolution == list.get(0)) {
              count1++;
          }
      }
      assertEquals(2, count1);
      var count = 0L;
      for (var s : result) {
          if (s == list.get(1)) {
              count++;
          }
      }
      assertEquals(2, count);
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
      var rows = 1 ;
      var columns = 2 ;
      var neighborhood = new L5<IntegerSolution>(rows, columns) ;

      List<IntegerSolution> list = new ArrayList<>(rows * columns);
      var bound = rows * columns;
      for (var i = 0; i < bound; i++) {
          var mock = mock(IntegerSolution.class);
          list.add(mock);
      }

      var result = neighborhood.getNeighbors(list, 1) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItems(list.get(0), list.get(1))) ;
      var count1 = 0L;
      for (var integerSolution : result) {
          if (integerSolution == list.get(0)) {
              count1++;
          }
      }
      assertEquals(2, count1);
      var count = 0L;
      for (var s : result) {
          if (s == list.get(1)) {
              count++;
          }
      }
      assertEquals(2, count);
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
      var rows = 2 ;
      var columns = 2 ;
      var neighborhood = new L5<IntegerSolution>(rows, columns) ;

      List<IntegerSolution> list = new ArrayList<>(rows * columns);
      var bound = rows * columns;
      for (var i = 0; i < bound; i++) {
          var mock = mock(IntegerSolution.class);
          list.add(mock);
      }

      var result = neighborhood.getNeighbors(list, 0) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItems(list.get(1), list.get(2))) ;
    assertThat(result, not(hasItems(list.get(3), list.get(0)))) ;
      var count1 = 0L;
      for (var integerSolution : result) {
          if (integerSolution == list.get(1)) {
              count1++;
          }
      }
      assertEquals(2, count1);
      var count = 0L;
      for (var s : result) {
          if (s == list.get(2)) {
              count++;
          }
      }
      assertEquals(2, count);
  }
}
