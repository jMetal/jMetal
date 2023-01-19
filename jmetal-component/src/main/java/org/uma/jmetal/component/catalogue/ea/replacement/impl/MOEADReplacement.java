package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PopulationAndNeighborhoodSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

public class MOEADReplacement<S extends Solution<?>> implements Replacement<S> {

  private final PopulationAndNeighborhoodSelection<S> matingPoolSelection;
  private final WeightVectorNeighborhood<S> weightVectorNeighborhood;
  private final AggregationFunction aggregationFunction;
  private final SequenceGenerator<Integer> sequenceGenerator;
  private final int maximumNumberOfReplacedSolutions;
  private boolean normalize;

  private IdealPoint idealPoint = null;
  private NadirPoint nadirPoint = null;
  private NonDominatedSolutionListArchive<S> nonDominatedSolutionListArchive;
  private boolean firstReplacement = true;

  public MOEADReplacement(
      PopulationAndNeighborhoodSelection<S> matingPoolSelection,
      WeightVectorNeighborhood<S> weightVectorNeighborhood,
      AggregationFunction aggregationFunction,
      SequenceGenerator<Integer> sequenceGenerator,
      int maximumNumberOfReplacedSolutions,
      boolean normalize) {
    this.matingPoolSelection = matingPoolSelection;
    this.weightVectorNeighborhood = weightVectorNeighborhood;
    this.aggregationFunction = aggregationFunction;
    this.sequenceGenerator = sequenceGenerator;
    this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions;

    this.normalize = normalize;
  }



  @Override
  public List<S> replace(
      List<S> population, List<S> offspringPopulation) {
    S newSolution = offspringPopulation.get(0);

    updateIdealPoint(population, newSolution);
    updateNadirPoint(population, newSolution);

    Neighborhood.NeighborType neighborType = matingPoolSelection.getNeighborType();
    IntegerPermutationGenerator randomPermutation =
        new IntegerPermutationGenerator(
            neighborType.equals(Neighborhood.NeighborType.NEIGHBOR)
                ? weightVectorNeighborhood.neighborhoodSize()
                : population.size());

    int replacements = 0;

    for (int i = 0;
        i < randomPermutation.getSequenceLength()
            && (replacements < maximumNumberOfReplacedSolutions);
        i++) {
      int k;
      if (neighborType.equals(Neighborhood.NeighborType.NEIGHBOR)) {
        k =
            weightVectorNeighborhood
                .getNeighborhood()[sequenceGenerator.getValue()][randomPermutation.getValue()];
      } else {
        k = randomPermutation.getValue();
      }
      randomPermutation.generateNext();

      double f1;
      double f2;

      f1 =
          aggregationFunction.compute(
              population.get(k).objectives(), weightVectorNeighborhood.getWeightVector()[k],
              idealPoint, nadirPoint);
      f2 =
          aggregationFunction.compute(
              newSolution.objectives(), weightVectorNeighborhood.getWeightVector()[k], idealPoint,
              nadirPoint);

      if (f2 < f1) {
        population.set(k, (S) newSolution.copy());
        replacements++;
      }
    }

    sequenceGenerator.generateNext();
    return population;
  }

  private void updateIdealPoint(List<S> population, S newSolution) {
    if (firstReplacement) {
      idealPoint = new IdealPoint(population.get(0).objectives().length);
      if (normalize) {
        nonDominatedSolutionListArchive = new NonDominatedSolutionListArchive<>() ;
        nonDominatedSolutionListArchive.addAll(population);
        nonDominatedSolutionListArchive.add(newSolution);
      }
      firstReplacement = false;
    }
    idealPoint.update(newSolution.objectives());
  }

  private void updateNadirPoint(List<S> population, S newSolution) {
    if (normalize) {
      nadirPoint = new NadirPoint(population.get(0).objectives().length);
      nonDominatedSolutionListArchive.add(newSolution);
      for (S solution : nonDominatedSolutionListArchive.solutions()) {
        nadirPoint.update(solution.objectives());
      }
    }
  }
}

