package org.uma.jmetal.problem.multiobjective;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.uma.jmetal.util.ConstraintHandling.isFeasible;
import static org.uma.jmetal.util.ConstraintHandling.overallConstraintViolationDegree;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

class SimpleIntegerProblemTest {
    @Test
    void givenAFeasibleSolutionWhenEvaluatingThenItDoesNotViolateConstraintsCaseA() {
        var problem = new SimpleIntegerProblem() ;
        IntegerSolution solution = problem.createSolution() ;

        solution.variables().set(0, 2) ;
        solution.variables().set(1, 2) ;

        problem.evaluate(solution) ;

        assertTrue(isFeasible(solution)) ;
    }

    @Test
    void givenAFeasibleSolutionWhenEvaluatingThenItDoesNotViolateConstraintsCaseB() {
        var problem = new SimpleIntegerProblem() ;
        var solution = problem.createSolution() ;

        solution.variables().set(0, 0) ;
        solution.variables().set(1, 10) ;

        problem.evaluate(solution) ;

        assertTrue(isFeasible(solution)) ;
    }

    @Test
    void givenAFeasibleSolutionWhenEvaluatingThenItDoesNotViolateConstraintsCaseC() {
        var problem = new SimpleIntegerProblem() ;
        var solution = problem.createSolution() ;

        solution.variables().set(0, 7) ;
        solution.variables().set(1, 4) ;

        problem.evaluate(solution) ;

        assertTrue(isFeasible(solution)) ;
    }

    @Test
    void givenANonFeasibleSolutionWhenEvaluatingThenItViolatesConstraintsCaseA() {
        var problem = new SimpleIntegerProblem() ;
        var solution = problem.createSolution() ;

        solution.variables().set(0, 0) ;
        solution.variables().set(1, 12) ;

        problem.evaluate(solution) ;

        assertFalse(isFeasible(solution)); ;
    }

    @Test
    void givenANonFeasibleSolutionWhenEvaluatingThenItViolatesConstraintsCaseB() {
        var problem = new SimpleIntegerProblem() ;
        var solution = problem.createSolution() ;

        solution.variables().set(0, 20) ;
        solution.variables().set(1, 4) ;

        problem.evaluate(solution) ;

        assertFalse(isFeasible(solution)); ;
    }

    @Test
    void givenANonFeasibleSolutionWhenEvaluatingThenViolationDegreeIsNotZero() {
        var problem = new SimpleIntegerProblem() ;
        var solution = problem.createSolution() ;

        solution.variables().set(0, 20) ;
        solution.variables().set(1, 4) ;

        problem.evaluate(solution) ;

        var r = overallConstraintViolationDegree(solution) ;
        assertFalse(isFeasible(solution)); ;
    }
}
