package org.uma.jmetal.component.algorithm.multiobjective;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

class SMSEMOABuilderDefaultRankingTest {
  @Test
  void shouldUseFastRankingByDefaultForThreeObjectiveSMSEMOA() throws NoSuchFieldException,
      IllegalAccessException {
    DoubleProblem problem = new FakeDoubleProblem(2, 3, 0);
    var builder =
        new SMSEMOABuilder<>(
            problem,
            10,
            new SBXCrossover(0.9, 20.0),
            new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0));

    assertTrue(FastNonDominatedSortRanking.class.isInstance(rankingField(builder)));
  }

  @Test
  void shouldUseMergeRankingByDefaultOutsideThreeObjectivesForSMSEMOA()
      throws NoSuchFieldException, IllegalAccessException {
    DoubleProblem problem = new FakeDoubleProblem(2, 2, 0);
    var builder =
        new SMSEMOABuilder<>(
            problem,
            10,
            new SBXCrossover(0.9, 20.0),
            new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0));

    assertTrue(MergeNonDominatedSortRanking.class.isInstance(rankingField(builder)));
  }

  @Test
  void shouldUseFastRankingByDefaultForThreeObjectiveSMSEMOADE() throws NoSuchFieldException,
      IllegalAccessException {
    DoubleProblem problem = new FakeDoubleProblem(2, 3, 0);
    var builder =
        new SMSEMOADEBuilder(
            problem,
            10,
            0.5,
            0.5,
            new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    assertTrue(FastNonDominatedSortRanking.class.isInstance(rankingField(builder)));
  }

  @Test
  void shouldUseMergeRankingByDefaultOutsideThreeObjectivesForSMSEMOADE()
      throws NoSuchFieldException, IllegalAccessException {
    DoubleProblem problem = new FakeDoubleProblem(2, 5, 0);
    var builder =
        new SMSEMOADEBuilder(
            problem,
            10,
            0.5,
            0.5,
            new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    assertTrue(MergeNonDominatedSortRanking.class.isInstance(rankingField(builder)));
  }

  private Object rankingField(Object builder) throws NoSuchFieldException, IllegalAccessException {
    Field rankingField = builder.getClass().getDeclaredField("ranking");
    rankingField.setAccessible(true);

    return rankingField.get(builder);
  }
}
