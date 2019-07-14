package org.uma.jmetal.operator.selection.impl;

import com.sun.tools.doclint.Checker;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing the selection operator used in DE: three different solutions are returned from
 * a population. The three solutions must be also different from the one indicated by an index (its
 * position in the list). As a consequence, the operator requires a solution list with at least for
 * elements.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class DifferentialEvolutionSelection
    implements SelectionOperator<List<DoubleSolution>, List<DoubleSolution>> {
  private int solutionListIndex = Integer.MIN_VALUE;
  private BoundedRandomGenerator<Integer> randomGenerator;
  private int numberOfSolutionsToSelect;

  /** Constructor */
  public DifferentialEvolutionSelection() {
    this(3);
  }

  /** Constructor */
  public DifferentialEvolutionSelection(int numberOfSolutionsToSelect) {
    this((a, b) -> JMetalRandom.getInstance().nextInt(a, b), numberOfSolutionsToSelect);
  }

  /** Constructor */
  public DifferentialEvolutionSelection(
      BoundedRandomGenerator<Integer> randomGenerator, int numberOfSolutionsToSelect) {
    this.randomGenerator = randomGenerator;
    this.numberOfSolutionsToSelect = numberOfSolutionsToSelect;
  }

  public void setIndex(int index) {
    this.solutionListIndex = index;
  }

  /** Execute() method */
  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutionList) {
    Check.isNotNull(solutionList);
    Check.isTrue(
        (solutionListIndex >= 0) && (solutionListIndex <= solutionList.size()),
        "Index value invalid: " + solutionListIndex);
    Check.isTrue(
        solutionList.size() >= numberOfSolutionsToSelect,
        "The population has less than " + numberOfSolutionsToSelect + " solution: " + solutionList.size());

    List<Integer> indexList = new ArrayList<>(numberOfSolutionsToSelect);

    do {
      int index = randomGenerator.getRandomValue(0, solutionList.size() - 1);
      if (index != solutionListIndex && !indexList.contains(index)) {
        indexList.add(index);
      }
    } while (indexList.size() < numberOfSolutionsToSelect);

    List<DoubleSolution> parents = new ArrayList<>();

    for (Integer index : indexList) {
      parents.add(solutionList.get(index));
    }

    return parents;
  }
}
