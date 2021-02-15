package org.uma.jmetal.util.distance;

import org.junit.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.distance.impl.CosineDistanceBetweenSolutionsInObjectiveSpace;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by ajnebro on 12/2/16.
 */
public class CosineDistanceBetweenSolutionsInObjectiveSpaceTest {
  private static final double EPSILON = 0.00000000001 ;

  @Test
  public void shouldIdenticalPointsHaveADistanceOfOne() {
    DoubleProblem problem = new DummyDoubleProblem(2, 2, 0) ;
    DoubleSolution idealPoint = problem.createSolution() ;
    idealPoint.objectives()[0] = 0.0 ;
    idealPoint.objectives()[1] = 0.0 ;

    DoubleSolution point1 = problem.createSolution() ;
    point1.objectives()[0] = 1.0 ;
    point1.objectives()[1] = 1.0 ;

    DoubleSolution point2 = problem.createSolution() ;
    point2.objectives()[0] = 1.0 ;
    point2.objectives()[1] = 1.0 ;

    CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>> distance =
        new CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>>(idealPoint) ;

    double receivedValue = distance.compute(point1, point2) ;
    assertEquals(1.0, receivedValue, EPSILON) ;
  }

  @Test
  public void shouldPointsInTheSameDirectionHaveADistanceOfOne() {
    DoubleProblem problem = new DummyDoubleProblem(2, 2, 0) ;
    DoubleSolution idealPoint = problem.createSolution() ;
    idealPoint.objectives()[0] = 0.0 ;
    idealPoint.objectives()[1] = 0.0 ;

    DoubleSolution point1 = problem.createSolution() ;
    point1.objectives()[0] = 1.0 ;
    point1.objectives()[1] = 1.0 ;

    DoubleSolution point2 = problem.createSolution() ;
    point2.objectives()[0] = 2.0 ;
    point2.objectives()[1] = 2.0 ;

    CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>> distance =
        new CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>>(idealPoint) ;

    double receivedValue = distance.compute(point1, point2) ;
    assertEquals(1.0, receivedValue, EPSILON) ;
  }

  @Test
  public void shouldTwoPerpendicularPointsHaveADistanceOfZero() {
    DoubleProblem problem = new DummyDoubleProblem(2, 2, 0) ;
    DoubleSolution idealPoint = problem.createSolution() ;
    idealPoint.objectives()[0] = 0.0 ;
    idealPoint.objectives()[1] = 0.0 ;

    DoubleSolution point1 = problem.createSolution() ;
    point1.objectives()[0] = 0.0 ;
    point1.objectives()[1] = 1.0 ;

    DoubleSolution point2 = problem.createSolution() ;
    point2.objectives()[0] = 1.0 ;
    point2.objectives()[1] = 0.0 ;


    CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>> distance =
        new CosineDistanceBetweenSolutionsInObjectiveSpace<Solution<?>>(idealPoint) ;

    double receivedValue = distance.compute(point1, point2) ;
    assertEquals(0.0, receivedValue, EPSILON) ;
  }
}