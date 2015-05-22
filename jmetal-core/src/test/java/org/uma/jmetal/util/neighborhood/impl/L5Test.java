package org.uma.jmetal.util.neighborhood.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.neighborhood.Neighborhood;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by ajnebro on 21/5/15.
 */
public class L5Test {
  private Neighborhood neighborhood ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setup() {
    neighborhood = new L5() ;
  }

  @Test
  public void shouldGetNeighborsWithANullListOfSolutionsThrowAnException() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is null"));

    neighborhood.getNeighbors(null, 0) ;
  }

  @Test
  public void shouldGetNeighborsWithAnEmptyListOfSolutionsThrowAnException() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is empty"));

    List<IntegerSolution> list = new ArrayList<>() ;

    neighborhood.getNeighbors(list, 0) ;
  }

  @Test
  public void shouldGetNeighborsWithANegativeSolutionIndexThrowAnException() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution position value is negative: -1"));

    List<IntegerSolution> list = new ArrayList<>() ;
    list.add(mock(IntegerSolution.class));
    list.add(mock(IntegerSolution.class));
    list.add(mock(IntegerSolution.class));
    list.add(mock(IntegerSolution.class));

    neighborhood.getNeighbors(list, -1) ;
  }

  @Test
  public void shouldGetNeighborsWithASolutionIndexValueEqualToTheListSizeThrowAnException() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString(
        "The solution position value is equal or greater than the solution list size: 1"));

    List<IntegerSolution> list = new ArrayList<>() ;
    list.add(mock(IntegerSolution.class));

    neighborhood.getNeighbors(list, 1) ;
  }

  @Test
  public void shouldGetNeighborsWithASolutionIndexValueGreaterThanTheListSizeThrowAnException() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString(
        "The solution position value is equal or greater than the solution list size: 2"));

    List<IntegerSolution> list = new ArrayList<>() ;
    list.add(mock(IntegerSolution.class));

    neighborhood.getNeighbors(list, 2) ;
  }

  @Test
  public void shouldGetNeighborsWithSolutionListSizeNotSquareRootThrowAnException() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString(
        "The solution list size must have an exact square root: 3"));

    List<IntegerSolution> list = new ArrayList<>() ;
    list.add(mock(IntegerSolution.class));
    list.add(mock(IntegerSolution.class));
    list.add(mock(IntegerSolution.class));

    neighborhood.getNeighbors(list, 1) ;
  }

  /**
   * Case 1
   *
   * Solution list:
   * 0 1 2
   * 3 4 5
   * 6 7 8
   *
   * The solution location is 4, the neighborhood is 1, 3, 5, 7
   */
  @Test
  public void shouldGetNeighborsReturnFiveNeighborsCase1() {
    List<IntegerSolution> list = new ArrayList<>(9) ;
    for (int i = 0 ; i < 9; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 4) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItem(list.get(1))) ;
    assertThat(result, hasItem(list.get(3))) ;
    assertThat(result, hasItem(list.get(5))) ;
    assertThat(result, hasItem(list.get(7))) ;
  }

  /**
   * Case 2
   *
   * Solution list:
   * 0 1 2
   * 3 4 5
   * 6 7 8
   *
   * The solution location is 1, the neighborhood is 7, 0, 2, 4
   */
  @Test
  public void shouldGetNeighborsReturnFiveNeighborsCase2() {
    List<IntegerSolution> list = new ArrayList<>(9) ;
    for (int i = 0 ; i < 9; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 4) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItem(list.get(0))) ;
    assertThat(result, hasItem(list.get(7))) ;
    assertThat(result, hasItem(list.get(2))) ;
    assertThat(result, hasItem(list.get(4))) ;
  }

  /**
   * Case 3
   *
   * Solution list:
   * 0 1
   * 2 3
   *
   * The solution location is 0, the neighborhood is 2, 1, 2, 1
   */
  @Test
  public void shouldGetNeighborsReturnFiveNeighborsCase3() {
    List<IntegerSolution> list = new ArrayList<>(9) ;
    for (int i = 0 ; i < 4; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItem(list.get(2))) ;
    assertThat(result, hasItem(list.get(1))) ;
  }
}
