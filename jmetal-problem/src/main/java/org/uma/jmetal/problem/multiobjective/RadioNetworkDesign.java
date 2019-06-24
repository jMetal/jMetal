/**
 * RadioNetworkDesign.java
 *
 * @author Rocio Garcia
 * @author Antonio J. Nebro
 * @version 1.0
 */
package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Class representing problem RadioNetworkDesign */
public class RadioNetworkDesign extends AbstractBinaryProblem {
  static final int MAXIMUM_UNCOVERAGE = 10;
  static final int MAXIMUM_ANTENNAE = 60;
  static final int GRID_SIZE_X = 287; // Artificial grid horizontal size.
  static final int GRID_SIZE_Y = 287; // Artificial grid vertical size.
  static final int GRID_SIZE = 82369; // Total grid size (287*287)
  static final int TRANS_NUMB = 59; // Number of transmiters used.
  static final int TRANS_TOTAL = 349; // Number of total transmiters.
  // 49 transmiters distributed regulary...
  // ... the rest is distributed randomly.
  static final int TRANS_TOTAL_EQUIDISTRIBUTED =
      225; // Number of total transmitters when distributed uniformly
  static final int BEAMS = 3; // Number of beams from a directive transmitter
  static final int PARAMETERS = 3;
  static final int ANTENNA_TYPES = 1; // 1- type: sectorial, 2-type: omnidirectional, 3 type: square
  static final int RANGE =
      22; // Transmiter cover range (square area), when >23'6 total coverage of the terrain is
  // achieved
  static final int RANGE_SQ = 20;
  static final int STD_COST = 5; // Normalized cost of antenna
  static final int COST_CIRC = 5; // Each type of antenna has a different cost
  static final int COST_SECT = 3;
  static final int COST_SQR = 5;

  static final double ALPHA =
      0.5; // Value of the interferenced area relative to the uninterferenced area
  // (always lesser or equal to 1)

  static final double TARGET_FITNESS =
      147.85; // Es el fitness que se ha de alcanzar para que termine
  protected List<Function<BinarySolution, Double>> constraints;
  // la ejecucion
  private int bits;
  private OverallConstraintViolation<BinarySolution> overallConstraintViolationDegree;
  private NumberOfViolatedConstraints<BinarySolution> numberOfViolatedConstraints;
  /** Constructor Create a new RadioNetwork problem instance */
  public RadioNetworkDesign(int numberOfBits) {
    setNumberOfVariables(1);
    setNumberOfObjectives(2);
    setNumberOfConstraints(2);
    setName("RadioNetworkDesign");

    bits = numberOfBits;
    overallConstraintViolationDegree = new OverallConstraintViolation<>();
    numberOfViolatedConstraints = new NumberOfViolatedConstraints<>();
    constraints = new ArrayList<>();
  } // RadioNetworkDesign

  public RadioNetworkDesign addConstraint(Function<BinarySolution, Double> constraint) {
    constraints.add(constraint);

    return this;
  }

  public void evaluate(BinarySolution solution) {
    BinarySet gen = solution.getVariableValue(0); // we have only a variable
    // DecisionVariables gen = individual.getDecisionVariables();

    int[] trans_location = {
      20, 20, 61, 20, 102, 20, 143, 20, 184, 20, 225, 20, 266, 20, 20, 61, 61, 61, 102, 61, 143, 61,
      184, 61, 225, 61, 266, 61, 20, 102, 61, 102, 102, 102, 143, 102, 184, 102, 225, 102, 266, 102,
      20, 143, 61, 143, 102, 143, 143, 143, 184, 143, 225, 143, 266, 143, 20, 184, 61, 184, 102,
      184, 143, 184, 184, 184, 225, 184, 266, 184, 20, 225, 61, 225, 102, 225, 143, 225, 184, 225,
      225, 225, 266, 225, 20, 266, 61, 266, 102, 266, 143, 266, 184, 266, 225, 266, 266, 266, 169,
      180, 180, 161, 160, 233, 57, 156, 158, 145, 138, 151, 160, 32, 165, 36, 228, 111, 251, 181,
      110, 130, 286, 19, 96, 183, 91, 133, 88, 74, 93, 56, 35, 273, 152, 198, 142, 181, 37, 117,
      271, 193, 49, 109, 104, 119, 110, 54, 58, 160, 135, 204, 220, 172, 4, 141, 160, 244, 210, 14,
      36, 37, 98, 3, 134, 226, 197, 109, 101, 17, 112, 230, 169, 126, 215, 44, 206, 27, 18, 90, 14,
      272, 40, 134, 160, 150, 58, 216, 170, 37, 252, 185, 246, 142, 154, 247, 180, 128, 188, 55,
      207, 201, 134, 15, 31, 111, 166, 34, 206, 116, 223, 261, 94, 48, 227, 179, 24, 250, 46, 26,
      159, 245, 279, 56, 235, 43, 195, 166, 165, 241, 203, 9, 190, 73, 20, 91, 25, 200, 211, 255,
      260, 199, 262, 66, 283, 120, 16, 76, 38, 112, 201, 172, 144, 1, 273, 282, 230, 285, 222, 240,
      70, 132, 240, 40, 202, 147, 35, 175, 24, 41, 4, 120, 88, 114, 23, 104, 274, 142, 83, 230, 281,
      265, 248, 58, 140, 42, 136, 185, 17, 2, 9, 42, 156, 115, 216, 32, 242, 104, 221, 224, 241,
      113, 224, 229, 261, 56, 96, 220, 79, 158, 137, 180, 104, 147, 273, 262, 182, 205, 40, 174, 4,
      69, 39, 230, 44, 115, 37, 31, 286, 62, 147, 240, 175, 84, 182, 150, 141, 279, 83, 221, 151,
      220, 114, 255, 81, 101, 231, 263, 20, 272, 150, 24, 55, 190, 255, 100, 18, 5, 131, 18, 68,
      278, 258, 244, 76, 154, 107, 218, 147, 191, 152, 11, 125, 267, 267, 206, 81, 211, 183, 101,
      197, 47, 126, 252, 237, 94, 65, 256, 100, 197, 274, 168, 188, 246, 126, 265, 114, 233, 196,
      261, 138, 61, 272, 264, 42, 252, 183, 123, 177, 80, 225, 88, 128, 64, 53, 79, 159, 119, 48,
      260, 29, 36, 142, 218, 282, 268, 196, 109, 215, 105, 84, 66, 167, 70, 43, 210, 36, 227, 47,
      213, 21, 272, 15, 149, 50, 68, 228, 210, 188, 277, 183, 218, 26, 38, 149, 22, 20, 58, 132,
      235, 164, 216, 14, 45, 286, 58, 255, 36, 286, 15, 249, 20, 1, 264, 170, 51, 46, 112, 262, 235,
      103, 158, 166, 129, 197, 28, 152, 217, 87, 284, 165, 251, 214, 180, 10, 214, 239, 265, 250,
      238, 281, 213, 259, 282, 191, 142, 47, 238, 255, 22, 186, 71, 180, 65, 201, 90, 94, 66, 21,
      181, 64, 186, 146, 278, 80, 156, 206, 32, 135, 170, 271, 129, 96, 243, 124, 0, 98, 171, 239,
      67, 193, 138, 138, 87, 204, 52, 178, 11, 118, 199, 193, 183, 99, 52, 174, 179, 209, 94, 212,
      58, 264, 196, 187, 73, 152, 25, 74, 251, 196, 26, 31, 103, 165, 170, 191, 82, 222, 82, 94, 54,
      282, 1, 237, 95, 54, 125, 275, 263, 219, 200, 34, 196, 110, 222, 270, 262, 247, 58, 227, 157,
      85, 259, 261, 250, 142, 165, 46, 78, 248, 141, 133, 243, 142, 83, 51, 196, 208, 39, 173, 141,
      240, 207, 51, 63, 143, 34, 39, 103, 93, 267, 260, 178, 240, 234, 142, 96, 113, 189, 174, 74,
      43, 20, 30, 185, 104, 82, 95, 26, 122, 268, 167, 76, 189, 218, 139, 45, 253, 179, 148, 59,
      160, 122, 238, 113, 70, 93, 209, 183, 282, 97, 257, 39, 117, 1, 224, 222, 84, 32, 248, 206,
      14, 128, 283, 203, 60, 136, 248, 26, 28, 109, 86, 188, 232, 37, 14, 15, 131, 224, 198, 127
    };

    int grid[] = new int[GRID_SIZE];
    int x, y, x1, y1;
    double cover_rate = 0.0;
    int used_trans;
    int covered_points;

    //  Initializing  the grid
    for (int k = 0; k < GRID_SIZE; k++) {
      grid[k] = 0;
    } // for

    used_trans = 0; // Updating covered points in the grid according ...
    covered_points = 0;

    // with transmiter locations and calculating ...
    for (int i = 0; i < gen.getBinarySetLength(); i++) {
      if (gen.get(i)) {
        used_trans++;
        x = trans_location[i * 2];
        y = trans_location[i * 2 + 1];
        for (x1 = x - RANGE_SQ; x1 <= x + RANGE_SQ; x1++) {
          for (y1 = y - RANGE_SQ; y1 <= y + RANGE_SQ; y1++) {
            if ((x1 >= 0) & (y1 >= 0) && (x1 < GRID_SIZE_X) && (y1 < GRID_SIZE_Y)) {
              grid[x1 * GRID_SIZE_X + y1]++;
              if (grid[x1 * GRID_SIZE_X + y1] == 1) {
                covered_points++;
              } // if
            } // if
          } // for
        } // for
      } // if
    } // for
    cover_rate = (100.0 * covered_points) / (GRID_SIZE);

    solution.setObjective(0, used_trans); // Number of antennae employed
    solution.setObjective(1, 100.0 - cover_rate); // Coverage rate unachieved
    //evaluateConstraints(solution) ;
    evaluateConstraintsSemantic(solution);

  }

  /**
   * The constraints must be normalized in the form (x) >= 0. The method must calculate the overall
   * constraint violation and the number of violated constraints. * @param individual The individual
   * to be evaluated
   */
  public void evaluateConstraints(BinarySolution solution) {
    double[] constraintValue = new double[2];
    constraintValue[0] = MAXIMUM_UNCOVERAGE - solution.getObjective(1);
    constraintValue[1] = MAXIMUM_ANTENNAE - solution.getObjective(0);

    int violation = 0;
    double total = 0.0;
    for (int i = 0; i < constraintValue.length; i++) {
      if (constraintValue[i] < 0) {
        total += constraintValue[i];
        violation++;
      } // if
    }
    overallConstraintViolationDegree.setAttribute(solution, total);
    numberOfViolatedConstraints.setAttribute(solution, violation);
    evaluateConstraintsSemantic(solution);
  }

  private void evaluateConstraintsSemantic(BinarySolution solution) {
    if (!constraints.isEmpty()) {
      double overallConstraintViolation = 0.0;
      int violatedConstraints = 0;
      for (int i = 0; i < constraints.size(); i++) {
        double violationDegree = constraints.get(i).apply(solution);
        if (violationDegree < 0) {
          overallConstraintViolation += violationDegree;
          violatedConstraints++;
        }
      }
      Double auxOverall = overallConstraintViolationDegree.getAttribute(solution);
      if (auxOverall != null) {
        overallConstraintViolation += auxOverall;
      }
      Integer auxNumber = numberOfViolatedConstraints.getAttribute(solution);
      if (auxNumber != null) {
        violatedConstraints += auxNumber;
      }
      overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
      numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
    }
  }

  /**
   * Determines whether the solution tested is an optimum for the radio network design problem
   * instance. It is used to stop the execution of the solver once the optimum is found.
   *
   * @param solution The individual to be evaluated
   */
  public boolean isOptimum(BinarySolution solution) {
    if ((solution.getObjective(0) == 49) && (solution.getObjective(1) == 0)) return true;
    else return false;
  }

  @Override
  protected int getBitsPerVariable(int index) {
    if (index != 0) {
      throw new JMetalException("Problem RND has only a variable. Index = " + index);
    }
    return bits;
  }
} // RadioNetworkDesign
