package test.metaheuristics.gde3;

import jmetal.core.Algorithm;
import jmetal.core.SolutionSet;
import jmetal.experiments.settings.GDE3_Settings;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Antonio on 12/06/14.
 */
public class GDE3Test {
    Algorithm algorithm_ ;

    @Test
    public void testNumberOfReturnedSolutionsInEasyProblem() throws IOException, ClassNotFoundException {
      algorithm_ = new GDE3_Settings("Kursawe").configure() ;

      SolutionSet solutionSet = algorithm_.execute() ;
    /*
    Rationale: the default problem is Kursawe, and usually GDE3; configured with standard
    settings return 100 solutions
     */
      assertTrue(solutionSet.size() >= 98) ;
    }
}
