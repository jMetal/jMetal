package org.uma.jmetal.util.neighborhood.impl;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

/**
 * Created by ajnebro on 26/5/15.
 */
class C9Test {

  /**
   * Case 1
   *
   * Solution list:
   * 0
   *
   * The solution location is 0, the neighborhood is 0
   */
  @Test
  void shouldGetNeighborsReturnFourNeighborsCase1() {
    int rows = 1 ;
    int columns = 1 ;
    C9<IntegerSolution> neighborhood = new C9<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0) ;
    Assertions.assertEquals(8, result.size());
    assertThat(result, hasItem(list.get(0))) ;
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
  void shouldGetNeighborsReturnFourNeighborsCase2() {
    int rows = 1 ;
    int columns = 2 ;
    C9<IntegerSolution> neighborhood = new C9<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0) ;
    Assertions.assertEquals(8, result.size());
    assertThat(result, hasItems(list.get(0), list.get(1))) ;
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
  void shouldGetNeighborsReturnFourNeighborsCase3() {
    int rows = 1 ;
    int columns = 2 ;
    C9<IntegerSolution> neighborhood = new C9<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 1) ;
    Assertions.assertEquals(8, result.size());
    assertThat(result, hasItems(list.get(0), list.get(1))) ;
  }

  /**
   * Case 4
   *
   * Solution list:
   * 0 1
   * 2 3
   *
   * The solution location is 0, the neighborhood is 1, 2, 3
   */
  @Test
  void shouldGetNeighborsReturnFourNeighborsCase4() {
    int rows = 2 ;
    int columns = 2 ;
    C9<IntegerSolution> neighborhood = new C9<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0) ;
    Assertions.assertEquals(8, result.size());
    assertThat(result, hasItems(list.get(1), list.get(2), list.get(3))) ;
    assertThat(result, not(hasItems(list.get(0)))) ;
  }

  /**
   * Case 5
   *
   * Solution list:
   * 0 1
   * 2 3
   *
   * The solution location is 1, the neighborhood is 0, 2, 3
   */
  @Test
  void shouldGetNeighborsReturnFourNeighborsCase5() {
    int rows = 2 ;
    int columns = 2 ;
    C9<IntegerSolution> neighborhood = new C9<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 1) ;
    Assertions.assertEquals(8, result.size());
    assertThat(result, hasItems(list.get(0), list.get(2), list.get(3))) ;
    assertThat(result, not(hasItems(list.get(1)))) ;
  }

  /**
   * Case 6
   *
   * Solution list:
   * 0 1
   * 2 3
   *
   * The solution location is 2, the neighborhood is 0, 1, 3
   */
  @Test
  void shouldGetNeighborsReturnFourNeighborsCase6() {
    int rows = 2 ;
    int columns = 2 ;
    C9<IntegerSolution> neighborhood = new C9<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 2) ;
    Assertions.assertEquals(8, result.size());
    assertThat(result, hasItems(list.get(0), list.get(1), list.get(3))) ;
    assertThat(result, not(hasItems(list.get(2)))) ;
  }

  /**
   * Case 7
   *
   * Solution list:
   * 0 1
   * 2 3
   *
   * The solution location is 3, the neighborhood is 0, 1, 2
   */
  @Test
  void shouldGetNeighborsReturnFourNeighborsCase7() {
    int rows = 2 ;
    int columns = 2 ;
    C9<IntegerSolution> neighborhood = new C9<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 3) ;
    Assertions.assertEquals(8, result.size());
    assertThat(result, hasItems(list.get(0), list.get(1), list.get(2))) ;
    assertThat(result, not(hasItems(list.get(3)))) ;
  }

  /**
   * Case 8
   *
   * Solution list:
   * 0 1 2 3
   * 4 5 6 7
   *
   * The solution location is 0, the neighborhood is 1, 4, 5, 3, 7
   */
  @Test
  void shouldGetNeighborsReturnFourNeighborsCase8() {
    int rows = 2 ;
    int columns = 4 ;
    C9<IntegerSolution> neighborhood = new C9<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0) ;
    Assertions.assertEquals(8, result.size());
    assertThat(result, hasItems(list.get(1), list.get(3), list.get(4), list.get(5), list.get(7))) ;
    assertThat(result, not(hasItems(list.get(2), list.get(6)))) ;
  }

  /**
   * Case 9
   *
   * Solution list:
   * 0 1 2 3
   * 4 5 6 7
   *
   * The solution location is 5, the neighborhood is 0, 1, 2, 4, 6
   */
  @Test
  void shouldGetNeighborsReturnFourNeighborsCase9() {
    int rows = 2 ;
    int columns = 4 ;
    C9<IntegerSolution> neighborhood = new C9<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 5) ;
    Assertions.assertEquals(8, result.size());
    assertThat(result, hasItems(list.get(0), list.get(1), list.get(2), list.get(4), list.get(6))) ;
    assertThat(result, not(hasItems(list.get(3), list.get(5), list.get(7)))) ;
  }

  /**
   * Case 10
   *
   * Solution list:
   * 0 1 2 3
   * 4 5 6 7
   * 8 9 10 11
   *
   * The solution location is 5, the neighborhood is 0, 1, 2, 4, 6, 8, 9
   */
  @Test
  void shouldGetNeighborsReturnFourNeighborsCase10() {
    int rows = 3 ;
    int columns = 4 ;
    C9<IntegerSolution> neighborhood = new C9<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 5) ;
    Assertions.assertEquals(8, result.size());
    assertThat(result, hasItems(list.get(0), list.get(1), list.get(2), list.get(4), list.get(6), list.get(8), list.get(9))) ;
    assertThat(result, not(hasItems(list.get(3), list.get(5), list.get(7), list.get(10), list.get(11)))) ;
  }

  /**
   * Case 11
   *
   * Solution list:
   * 0 1 2 3
   * 4 5 6 7
   * 8 9 10 11
   *
   * The solution location is 11, the neighborhood is 6, 7, 10, 3, 2, 8
   */
  @Test
  void shouldGetNeighborsReturnFourNeighborsCase11() {
    int rows = 3 ;
    int columns = 4 ;
    C9<IntegerSolution> neighborhood = new C9<IntegerSolution>(rows, columns) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 11) ;
    Assertions.assertEquals(8, result.size());
    assertThat(result, hasItems(list.get(2), list.get(3), list.get(6), list.get(7), list.get(8), list.get(10))) ;
    assertThat(result, not(hasItems(list.get(0), list.get(1), list.get(4), list.get(5), list.get(9), list.get(11)))) ;
  }
}
