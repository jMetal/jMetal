package org.uma.jmetal.component.replacement.impl;

import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.selection.impl.PopulationAndNeighborhoodMatingPoolSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

import java.util.List;

public class MOEADReplacement implements Replacement<DoubleSolution> {
  private final PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution> matingPoolSelection;
  private final WeightVectorNeighborhood<DoubleSolution> weightVectorNeighborhood;
  private final AggregativeFunction aggregativeFunction;
  private final SequenceGenerator<Integer> sequenceGenerator;
  private final int maximumNumberOfReplacedSolutions;

  public MOEADReplacement(
      PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution> matingPoolSelection,
      WeightVectorNeighborhood<DoubleSolution> weightVectorNeighborhood,
      AggregativeFunction aggregativeFunction,
      SequenceGenerator<Integer> sequenceGenerator,
      int maximumNumberOfReplacedSolutions) {
    this.matingPoolSelection = matingPoolSelection;
    this.weightVectorNeighborhood = weightVectorNeighborhood;
    this.aggregativeFunction = aggregativeFunction;
    this.sequenceGenerator = sequenceGenerator;
    this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions;
  }

  @Override
  public List<DoubleSolution> replace(
      List<DoubleSolution> population, List<DoubleSolution> offspringPopulation) {
    DoubleSolution newSolution = offspringPopulation.get(0);
    aggregativeFunction.update(newSolution.getObjectives());

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

      double f1 =
          aggregativeFunction.compute(
              population.get(k).getObjectives(), weightVectorNeighborhood.getWeightVector()[k]);
      double f2 =
          aggregativeFunction.compute(
              newSolution.getObjectives(), weightVectorNeighborhood.getWeightVector()[k]);

      if (f2 < f1) {
        population.set(k, (DoubleSolution) newSolution.copy());
        replacements++;
      }
    }

    sequenceGenerator.generateNext();
    return population;
  }
}
