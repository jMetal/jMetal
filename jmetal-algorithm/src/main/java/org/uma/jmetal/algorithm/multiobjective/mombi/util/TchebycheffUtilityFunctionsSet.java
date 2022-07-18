package org.uma.jmetal.algorithm.multiobjective.mombi.util;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * This class implements a set of utility functions based on the Tchebycheff aggregation approach
 *
 * @author Juan J. Durillo
 *     <p>ToDo List: + check the size of nadir and reference points are the correct ones + check
 *     that the function that needs to be evaluated is the correct one
 * @param <S>
 */
@SuppressWarnings("serial")
public class TchebycheffUtilityFunctionsSet<S extends Solution<?>>
    extends AbstractUtilityFunctionsSet<S> {
  private final List<Double> referencePoint;

  public TchebycheffUtilityFunctionsSet(String file_path, List<Double> referencePoint) {
    super(file_path);
    this.referencePoint = referencePoint;
  }

  public TchebycheffUtilityFunctionsSet(String file_path) {
    super(file_path);
    this.referencePoint = new ArrayList<>(this.getVectorSize());
    for (var i = 0; i < this.getVectorSize(); i++) this.referencePoint.add(0.0);
  }

  @Override
  public Double evaluate(S solution, int vector) {
    if ((vector < 0) || (vector >= this.getSize()))
      throw new JMetalException("Vector value " + vector + " invalid");

    var result = Double.NEGATIVE_INFINITY;
    var weightVector = this.getWeightVector(vector);
    for (var i = 0; i < weightVector.size(); i++)
      result =
          Math.max(
              result,
              weightVector.get(i)
                  * Math.abs(solution.objectives()[i] - this.referencePoint.get(i)));

    return result;
  }
}
