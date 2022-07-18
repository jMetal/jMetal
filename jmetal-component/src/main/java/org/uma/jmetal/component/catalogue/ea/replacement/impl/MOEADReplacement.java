package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PopulationAndNeighborhoodSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

public class MOEADReplacement<S extends Solution<?>> implements Replacement<S> {
  private final PopulationAndNeighborhoodSelection<S> matingPoolSelection;
  private final WeightVectorNeighborhood<S> weightVectorNeighborhood;
  private final AggregativeFunction aggregativeFunction;
  private final SequenceGenerator<Integer> sequenceGenerator;
  private final int maximumNumberOfReplacedSolutions;

  public MOEADReplacement(
      PopulationAndNeighborhoodSelection<S> matingPoolSelection,
      WeightVectorNeighborhood<S> weightVectorNeighborhood,
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
  public List<S> replace(
      List<S> population, List<S> offspringPopulation) {
    var newSolution = offspringPopulation.get(0);
    aggregativeFunction.update(newSolution.objectives());

    var neighborType = matingPoolSelection.getNeighborType();
    @NotNull IntegerPermutationGenerator randomPermutation =
        new IntegerPermutationGenerator(
            neighborType.equals(Neighborhood.NeighborType.NEIGHBOR)
                ? weightVectorNeighborhood.neighborhoodSize()
                : population.size());

    var replacements = 0;

    for (var i = 0;
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

      var f1 =
          aggregativeFunction.compute(
              population.get(k).objectives(), weightVectorNeighborhood.getWeightVector()[k]);
      var f2 =
          aggregativeFunction.compute(
              newSolution.objectives(), weightVectorNeighborhood.getWeightVector()[k]);

      if (f2 < f1) {
        population.set(k, (S) newSolution.copy());
        replacements++;
      }
    }

    sequenceGenerator.generateNext();
    return population;
  }
}
