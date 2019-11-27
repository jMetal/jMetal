package org.uma.jmetal.operator.selection.impl;

import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class implementing the selection operator used in DE: a number of different solutions are
 * returned from a population. The number of solutions is requested in the class constructor (by
 * default, its value is 3), and they must be also different from the one indicated by an index
 * (typically, the current solution being processed by a DE algorithm). This current solution can
 * belong to the returned list if the {@link #selectCurrentSolution} variable is set to True when
 * invoking the {@link #setSelectCurrentSolution()} method; in this case, the current solution will
 * be the last one of the returned list.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DifferentialEvolutionSelection
    implements SelectionOperator<List<DoubleSolution>, List<DoubleSolution>> {
  private int currentSolutionIndex = Integer.MIN_VALUE;
  private BoundedRandomGenerator<Integer> randomGenerator;
  private int numberOfSolutionsToSelect;
  private boolean selectCurrentSolution;

  /** Constructor */
  public DifferentialEvolutionSelection() {
    this((a, b) -> JMetalRandom.getInstance().nextInt(a, b), 3, false);
  }

  /** Constructor */
  public DifferentialEvolutionSelection(
      int numberOfSolutionsToSelect, boolean selectCurrentSolution) {
    this(
        (a, b) -> JMetalRandom.getInstance().nextInt(a, b),
        numberOfSolutionsToSelect,
        selectCurrentSolution);
  }

  /** Constructor */
  public DifferentialEvolutionSelection(
      BoundedRandomGenerator<Integer> randomGenerator,
      int numberOfSolutionsToSelect,
      boolean selectCurrentSolution) {
    this.randomGenerator = randomGenerator;
    this.numberOfSolutionsToSelect = numberOfSolutionsToSelect;
    this.selectCurrentSolution = selectCurrentSolution;
  }

  public void setIndex(int index) {
    this.currentSolutionIndex = index;
  }

  /** Execute() method */
  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutionList) {
    Check.isNotNull(solutionList);
    Check.that(
        (currentSolutionIndex >= 0) && (currentSolutionIndex <= solutionList.size()),
        "Index value invalid: " + currentSolutionIndex);
    Check.that(
        solutionList.size() >= numberOfSolutionsToSelect,
        "The population has less than "
            + numberOfSolutionsToSelect
            + " solutions: "
            + solutionList.size());

    List<Integer> indexList = new ArrayList<>();

    int solutionsToSelect =
        selectCurrentSolution ? numberOfSolutionsToSelect - 1 : numberOfSolutionsToSelect;

    do {
      int index = randomGenerator.getRandomValue(0, solutionList.size() - 1);
      if (index != currentSolutionIndex && !indexList.contains(index)) {
        indexList.add(index);
      }
    } while (indexList.size() < solutionsToSelect);

    if (selectCurrentSolution) {
      indexList.add(currentSolutionIndex);
    }

    return indexList.stream().map(index -> solutionList.get(index)).collect(Collectors.toList());
  }
}
