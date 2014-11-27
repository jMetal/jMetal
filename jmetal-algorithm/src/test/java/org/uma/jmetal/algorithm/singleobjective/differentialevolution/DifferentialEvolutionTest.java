package org.uma.jmetal.algorithm.singleobjective.differentialevolution;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.GenericDoubleSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Created by Antonio J. Nebro on 25/11/14.
 */
@RunWith(MockitoJUnitRunner.class)
public class DifferentialEvolutionTest {

    private DifferentialEvolution algorithm;

    private DoubleProblem problem;

    private int populationSize = 100;

    private int maxEvaluations = 25000;

    private static final double EPSILON = 0.000000000000001;

    @Mock
    private DifferentialEvolutionCrossover crossover;

    @Mock
    private DifferentialEvolutionSelection selection;

    @Mock
    private SequentialSolutionListEvaluator evaluator;

    @Before
    public void startup() {
        problem = Mockito.mock(Kursawe.class);
        algorithm = new DifferentialEvolution(problem, maxEvaluations, populationSize, crossover, selection, evaluator);

    }

    @Test
    public void shouldEvaluatePopulation() {
        List<DoubleSolution> population = Arrays.<DoubleSolution>asList(new GenericDoubleSolution(new DTLZ4()));

        List<DoubleSolution> expectedResult = Arrays.<DoubleSolution>asList(new GenericDoubleSolution(new DTLZ4()));

        Mockito.when(evaluator.evaluate(population, problem)).thenReturn(expectedResult);
        List<DoubleSolution> result = algorithm.evaluatePopulation(population);
        Mockito.verify(evaluator).evaluate(population, problem);

        Assert.assertEquals("Evaluation result is different from expected.", expectedResult, result);
    }

    @Test
    public void initProgress() {
        Integer expectedPopulationSize = 10;
        ReflectionTestUtils.setField(algorithm, "populationSize", expectedPopulationSize);

        algorithm.initProgress();
        assertEquals(expectedPopulationSize.intValue(), algorithm.getEvaluations());
    }

    @Test
    public void updateProgressFirstIteration() {

        algorithm.initProgress();
        algorithm.updateProgress();
        assertEquals(populationSize * 2, algorithm.getEvaluations());
    }

    @Test
    public void updateProgressSecondIteration() {
        algorithm.initProgress();
        algorithm.updateProgress();
        assertEquals(populationSize * 2, algorithm.getEvaluations());
        algorithm.updateProgress();
        assertEquals(populationSize * 3, algorithm.getEvaluations());

        algorithm.setEvaluations(20000);
        algorithm.updateProgress();
        assertEquals(20000 + populationSize, algorithm.getEvaluations());

    }

    @Test
    public void updateProgress() {
        algorithm.setEvaluations(20000);
        algorithm.updateProgress();
        assertEquals(20000 + populationSize, algorithm.getEvaluations());

    }

    @Test
    public void stoppingConditionNotReachedByOneEvaluation() {
        int value = maxEvaluations - 1;
        algorithm.setEvaluations(value);
        assertFalse(algorithm.isStoppingConditionReached());
    }

    @Test
    public void stoppingConditionReached() {
        algorithm.setEvaluations(maxEvaluations);
        assertTrue(algorithm.isStoppingConditionReached());

        algorithm.setEvaluations(maxEvaluations++);
        assertTrue(algorithm.isStoppingConditionReached());
    }

    @Test
    public void theInitialPopulationHasTheRightSize() {
        List<DoubleSolution> population = algorithm.createInitialPopulation();

        assertEquals(population.size(), populationSize);
    }

    @Test
    public void theInitialPopulationHasInstantiatedSolutions() {
        List<DoubleSolution> population = algorithm.createInitialPopulation();

        assertNotNull(population.get(0));
        assertNotNull(population.get(populationSize - 1));
        assertNotEquals(population.get(0), population.get(populationSize - 1));
    }

    @Test
    public void evaluationOfTheInitialPopulation() {
        List<DoubleSolution> population1 = algorithm.createInitialPopulation();
        List<DoubleSolution> population2 = algorithm.evaluatePopulation(population1);
        assertEquals(population1.size(), population2.size());

        assertEquals(population1.get(0), population2.get(0));
        assertNotEquals(population1.get(0), population2.get(1));
        assertEquals(population1.get(populationSize - 1), population2.get(populationSize - 1));
    }

    @Test
    public void selection() {
        List<DoubleSolution> population = algorithm.createInitialPopulation();

        List<DoubleSolution> offspringPopulation = algorithm.selection(population);
        assertEquals(population, offspringPopulation);
    }

    @Test
    public void reproductionReturnsTheSameNumberOfSolutions() {
        List<DoubleSolution> population = algorithm.createInitialPopulation();
        population = algorithm.evaluatePopulation(population);
        List<DoubleSolution> offspringPopulation = algorithm.reproduction(population);

        assertEquals(populationSize, offspringPopulation.size());
    }

    @Test
    public void replacementReturnsASolutionSetWithTheRightSize() {
        List<DoubleSolution> population = algorithm.createInitialPopulation();
        population = algorithm.evaluatePopulation(population);
        List<DoubleSolution> offspringPopulation = algorithm.reproduction(population);
        offspringPopulation = algorithm.evaluatePopulation(offspringPopulation);

        population = algorithm.replacement(population, offspringPopulation);
        assertEquals(populationSize, population.size());
    }

    @Test
    public void getResultReturnsTheBestIndividual() {
        List<DoubleSolution> population = algorithm.createInitialPopulation();
        algorithm.setPopulation(algorithm.evaluatePopulation(population));

        DoubleSolution solution = algorithm.getResult();
        Comparator<Solution> comparator = new ObjectiveComparator(0);
        assertEquals(0, comparator.compare(solution, algorithm.getPopulation().get(0)));
        assertEquals(-1, comparator.compare(solution, algorithm.getPopulation().get(1)));
        assertEquals(-1, comparator.compare(solution, algorithm.getPopulation().get(2)));
        assertEquals(-1, comparator.compare(solution, algorithm.getPopulation().get(populationSize - 1)));
    }

}
