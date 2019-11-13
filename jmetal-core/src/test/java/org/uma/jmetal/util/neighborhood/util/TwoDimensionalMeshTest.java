package org.uma.jmetal.util.neighborhood.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

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
public class TwoDimensionalMeshTest {

  private static final int [] north      = {-1,  0};
  private static final int [] south      = { 1 , 0};
  private static final int [] east       = { 0 , 1};
  private static final int [] west       = { 0 ,-1};

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldGetNeighborsWithANullListOfSolutionsThrowAnException() {
    TwoDimensionalMesh<Solution<?>> neighborhood = new TwoDimensionalMesh<Solution<?>>(3, 3, new int[][]{north, south, east, west}) ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is null"));

    neighborhood.getNeighbors(null, 0) ;
  }

  @Test
  public void shouldGetNeighborsWithAnEmptyListOfSolutionsThrowAnException() {
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(3, 3, new int[][]{north, south, east, west}) ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is empty"));

    List<IntegerSolution> list = new ArrayList<>() ;

    neighborhood.getNeighbors(list, 0) ;
  }

  @Test
  public void shouldGetNeighborsWithANegativeSolutionIndexThrowAnException() {
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(3, 3, new int[][]{north, south, east, west}) ;

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
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(1, 1, new int[][]{north, south, east, west}) ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString(
        "The solution position value 1 is equal or greater than the solution list size 1"));

    List<IntegerSolution> list = new ArrayList<>() ;
    list.add(mock(IntegerSolution.class));

    neighborhood.getNeighbors(list, 1) ;
  }

  @Test
  public void shouldGetNeighborsWithASolutionIndexValueGreaterThanTheListSizeThrowAnException() {
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(2, 2, new int[][]{north, south, east, west}) ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString(
        "The solution position value 5 is equal or greater than the solution list size 4"));

    List<IntegerSolution> list = new ArrayList<>() ;
    list.add(mock(IntegerSolution.class));
    list.add(mock(IntegerSolution.class));
    list.add(mock(IntegerSolution.class));
    list.add(mock(IntegerSolution.class));

    neighborhood.getNeighbors(list, 5) ;
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
  public void shouldGetNeighborsReturnFourNeighborsCase1() {
    int rows = 3 ;
    int columns = 3 ;
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(rows, columns, new int[][]{north, south, east, west}) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
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
  public void shouldGetNeighborsReturnFourNeighborsCase2() {
    int rows = 3 ;
    int columns = 3 ;
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(rows, columns, new int[][]{north, south, east, west}) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 1) ;
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
   * 0 1 2
   * 3 4 5
   * 6 7 8
   *
   * The solution location is 0, the neighborhood is 1, 2, 3, 6
   */
  @Test
  public void shouldGetNeighborsReturnFourNeighborsCase3() {
    int rows = 3 ;
    int columns = 3 ;
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(rows, columns, new int[][]{north, south, east, west}) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItem(list.get(1))) ;
    assertThat(result, hasItem(list.get(2))) ;
    assertThat(result, hasItem(list.get(3))) ;
    assertThat(result, hasItem(list.get(6))) ;
  }

  /**
   * Case 4
   *
   * Solution list:
   * 0 1 2
   * 3 4 5
   * 6 7 8
   *
   * The solution location is 2, the neighborhood is 1, 0, 5, 8
   */
  @Test
  public void shouldGetNeighborsReturnFourNeighborsCase4() {
    int rows = 3 ;
    int columns = 3 ;
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(rows, columns, new int[][]{north, south, east, west}) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 2) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItem(list.get(1))) ;
    assertThat(result, hasItem(list.get(0))) ;
    assertThat(result, hasItem(list.get(5))) ;
    assertThat(result, hasItem(list.get(8))) ;
  }

  /**
   * Case 5
   *
   * Solution list:
   * 0 1 2
   * 3 4 5
   * 6 7 8
   *
   * The solution location is 2, the neighborhood is 2, 6, 7, 5
   */
  @Test
  public void shouldGetNeighborsReturnFourNeighborsCase5() {
    int rows = 3 ;
    int columns = 3 ;
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(rows, columns, new int[][]{north, south, east, west}) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 8) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItem(list.get(2))) ;
    assertThat(result, hasItem(list.get(6))) ;
    assertThat(result, hasItem(list.get(7))) ;
    assertThat(result, hasItem(list.get(5))) ;
  }

  /**
   * Case 6
   *
   * Solution list:
   * 0 1 2
   * 3 4 5
   *
   * The solution location is 0, the neighborhood is 1, 3, 3, 2
   */
  @Test
  public void shouldGetNeighborsReturnFourNeighborsCase6() {
    int rows = 2 ;
    int columns = 3 ;
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(rows, columns, new int[][]{north, south, east, west}) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0);
    assertEquals(4, result.size()) ;
    assertThat(result, hasItem(list.get(1))) ;
    assertThat(result, hasItem(list.get(3))) ;
    assertThat(result, hasItem(list.get(2))) ;
  }

  /**
   * Case 7
   *
   * Solution list:
   * 0 1 2
   * 3 4 5
   *
   * The solution location is 3, the neighborhood is 0, 4, 5, 0
   */
  @Test
  public void shouldGetNeighborsReturnFourNeighborsCase7() {
    int rows = 2 ;
    int columns = 3 ;
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(rows, columns, new int[][]{north, south, east, west}) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 3) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItem(list.get(0))) ;
    assertThat(result, hasItem(list.get(4))) ;
    assertThat(result, hasItem(list.get(5))) ;
  }

  /**
   * Case 8
   *
   * Solution list:
   * 0 1
   * 2 3
   *
   * The solution location is 0, the neighborhood is 2, 1, 2, 1
   */
  @Test
  public void shouldGetNeighborsReturnFourNeighborsCase8() {
    int rows = 2 ;
    int columns = 2 ;
    TwoDimensionalMesh<IntegerSolution> neighborhood = new TwoDimensionalMesh<IntegerSolution>(rows, columns, new int[][]{north, south, east, west}) ;

    List<IntegerSolution> list = new ArrayList<>(rows*columns) ;
    for (int i = 0 ; i < rows*columns; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0) ;
    assertEquals(4, result.size()) ;
    assertThat(result, hasItem(list.get(2))) ;
    assertThat(result, hasItem(list.get(1))) ;
  }
}
