package org.uma.jmetal.util.archive.impl;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.DummyDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.*;

class AdaptiveGridArchiveV2Test {
@Test
    public void test1() {
    AdaptiveGridArchive<DoubleSolution> archive1 = new AdaptiveGridArchive<>(100, 5, 2) ;
    AdaptiveGridArchiveV2<DoubleSolution> archive2 = new AdaptiveGridArchiveV2<>(100, 5, 2) ;

    assertEquals(5, archive1.getGrid().getBisections());

    DoubleProblem problem = new DummyDoubleProblem(2, 2, 0) ;

    DoubleSolution solution1 = problem.createSolution() ;
    DoubleSolution solution2 = problem.createSolution() ;
    DoubleSolution solution3 = problem.createSolution() ;
    DoubleSolution solution4 = problem.createSolution() ;

    solution1.objectives()[0] = 0.0 ;
    solution1.objectives()[1] = 1.0 ;

    solution2.objectives()[0] = 1.0 ;
    solution2.objectives()[1] = 2.0 ;

    solution3.objectives()[0] = 0.1 ;
    solution3.objectives()[1] = 0.9 ;

    solution4.objectives()[0] = 0.11 ;
    solution4.objectives()[1] = 0.89 ;

    archive1.archive.add(solution1) ;
    assertEquals(1, archive1.getSolutionList().size());

    archive1.archive.add(solution2) ;
    assertEquals(1, archive1.getSolutionList().size());
    assertEquals(solution1, archive1.getSolutionList().get(0));

    archive1.archive.add(solution3) ;
    assertEquals(2, archive1.getSolutionList().size());

    archive1.archive.add(solution4) ;
    assertEquals(3, archive1.getSolutionList().size());

}
    @Test
    public void test2() {
        AdaptiveGridArchiveV2<DoubleSolution> archive1 = new AdaptiveGridArchiveV2<>(100, 5, 2) ;

        DoubleProblem problem = new DummyDoubleProblem(2, 2, 0) ;

        DoubleSolution solution1 = problem.createSolution() ;
        DoubleSolution solution2 = problem.createSolution() ;
        DoubleSolution solution3 = problem.createSolution() ;
        DoubleSolution solution4 = problem.createSolution() ;

        solution1.objectives()[0] = 0.0 ;
        solution1.objectives()[1] = 1.0 ;

        solution2.objectives()[0] = 1.0 ;
        solution2.objectives()[1] = 2.0 ;

        solution3.objectives()[0] = 0.1 ;
        solution3.objectives()[1] = 0.9 ;

        solution4.objectives()[0] = 0.11 ;
        solution4.objectives()[1] = 0.89 ;

        archive1.archive.add(solution1) ;
        assertEquals(1, archive1.getSolutionList().size());

        archive1.archive.add(solution2) ;
        assertEquals(1, archive1.getSolutionList().size());
        assertEquals(solution1, archive1.getSolutionList().get(0));

        archive1.archive.add(solution3) ;
        assertEquals(2, archive1.getSolutionList().size());

        archive1.archive.add(solution4) ;
        assertEquals(3, archive1.getSolutionList().size());

    }

}