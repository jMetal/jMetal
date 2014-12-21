package org.uma.jmetal.problem.multiobjective.ebes;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Created by Antonio J. Nebro on 11/07/14.
 */
public class EbesMutation implements MutationOperator<DoubleSolution> {
  Ebes ebes ;
  double mutationProbability ;
  private JMetalRandom randomGenerator ;

  /** Constructor */
  public EbesMutation(double mutationProbability) {
    try {
      ebes = new Ebes() ;
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    this.mutationProbability = mutationProbability ;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /** execute() method*/
  @Override
  public DoubleSolution execute(DoubleSolution solution) throws JMetalException {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(solution); ;

    return solution ;
  }

  private void doMutation(DoubleSolution solution) {
    for (int groupId = 0; groupId < ebes.getnumberOfGroupElements(); groupId++) {

      if (randomGenerator.nextDouble() <= mutationProbability) {
        switch (ebes.getGroupShape(groupId)) {
          case Ebes.CIRCLE:
            doCircleMutation(groupId, solution) ;
            break ;
          case Ebes.I_SINGLE:
            doISingleMutation(groupId, solution);
            break;
          case Ebes.HOLE_CIRCLE:
          case Ebes.RECTANGLE:
          case Ebes.HOLE_RECTANGLE:
          case Ebes.I_DOUBLE:
          case Ebes.H_SINGLE:
          case Ebes.H_DOUBLE:
          case Ebes.L_SINGLE:
          case Ebes.L_DOUBLE:
          case Ebes.T_SINGLE:
          case Ebes.T_DOUBLE:
            doGenericMutation(groupId, solution) ;
            break ;
          default:
            break ;
        }

        //doGenericMutation(groupId, solution);
      }
    }
  }

  private void doGenericMutation(int groupId, DoubleSolution solution) {
    int firstVariable = ebes.getVariablePosition(groupId) ;
    int numberOfVariables = 0 ;
    switch (ebes.getGroupShape(groupId)) {
      case Ebes.CIRCLE:
        numberOfVariables = 1 ;
        break ;
      case Ebes.HOLE_CIRCLE:
      case Ebes.RECTANGLE:
        numberOfVariables = 2 ;
        break ;
      case Ebes.I_SINGLE:
      case Ebes.HOLE_RECTANGLE:
      case Ebes.I_DOUBLE:
      case Ebes.H_SINGLE:
      case Ebes.H_DOUBLE:
      case Ebes.L_SINGLE:
      case Ebes.L_DOUBLE:
      case Ebes.T_SINGLE:
      case Ebes.T_DOUBLE:
        numberOfVariables = 4 ;
        break;
      default:
        break;
    }

    //System.out.print("first: " + ebes.getVariablePosition(groupId)) ;
    //System.out.print("  .Number: " + numberOfVariables) ;

    int variableMutate = randomGenerator.nextInt(firstVariable, firstVariable+numberOfVariables-1) ;
    //System.out.println("  .Var: " + variableMutate) ;

    mutateVariable(variableMutate, solution);
  }

  private void doCircleMutation(int groupId, DoubleSolution solution) {
    int variableIndex = ebes.getVariablePosition(groupId) ;
    mutateVariable(variableIndex, solution);
  }

  private void doISingleMutation(int groupId, DoubleSolution solution) {

    // hipótesis de carga
    int hi=0;

    if (ebes.StrainNxxMin_ != null && ebes.StrainNxxMax_ != null) {
      // alturas necearias por tres esfuerzos distintos
      double[] Y = {0.0, 0.0, 0.0};

      // área de la sección por tensión de compresión
      // participaciones de las tensiones normales referidas al esfuerzo axil
      double ratioStrainMinNxx = ebes.StrainNxxMin_[groupId][hi] / ebes.Groups_[groupId][ebes.COMPRESSION];
      // área de la sección por tensión de compresión
      double Ac = ebes.omegaMax_[groupId][hi] * ebes.NxxMin_[groupId][hi] / ebes.Groups_[groupId][ebes.COMPRESSION] * ratioStrainMinNxx;
      // participaciones de las tensiones normales referidas al esfuerzo axil
      double ratioStrainMaxNxx = ebes.StrainNxxMax_[groupId][hi] / ebes.Groups_[groupId][ebes.STRESS];
      // área de la sección por tensión de tracción
      double At = ebes.NxxMax_[groupId][hi] / ebes.Groups_[groupId][ebes.STRESS] * ratioStrainMaxNxx;
      // área máxima necesaria
      double A = Math.max(Ac, At);
      A *= 10000;
      // altura necesaria en función del area y del esfuerzo normal coincidente con el eje x
      Y[0] = ebes.Interpolation_I_Single_Y_func_Area_(A);

      // participaciones de las tensiones normales mínimas referidas al momento flector respecto al eje z
      double ratioStrainMinMxz = ebes.StrainMxzMin_[groupId][hi] / ebes.Groups_[groupId][ebes.COMPRESSION];
      // módulo resistente por tensión de compresión respecto al momento flector Mxz
      double Wzc = ebes.MxzMin_[groupId][hi] / ebes.Groups_[groupId][ebes.COMPRESSION] * ratioStrainMinMxz;
      // participaciones de las tensiones normales máximas referidas al momento flector respecto al eje z
      double ratioStrainMaxMxz = ebes.StrainMxzMax_[groupId][hi] / ebes.Groups_[groupId][ebes.STRESS];
      // módulo resistente por tensión de tracción respecto al momento flector Mxz
      double Wzt = ebes.MxzMax_[groupId][hi] / ebes.Groups_[groupId][ebes.STRESS] * ratioStrainMaxMxz;
      // módulo resistente máximo necesario
      double Wxz = Math.max(Wzc, Wzt);
      // conversión de unidades de medidas a cm3
      Wxz *= 1000000;
      // altura necesaria en función del módulo resistente y del momento flector respecto al eje z
      Y[1] = ebes.Interpolation_I_Single_Y_func_Wxz_(Wxz);

      // participaciones de las tensiones normales mínimas referidas al momento flector respecto al eje y
      double ratioStrainMinMxy = ebes.StrainMxyMin_[groupId][hi] / ebes.Groups_[groupId][ebes.COMPRESSION];
      // módulo resistente por tensión de compresión respecto al momento flector Mxz
      double Wyc = ebes.MxyMin_[groupId][hi] / ebes.Groups_[groupId][ebes.COMPRESSION] * ratioStrainMinMxy;
      // participaciones de las tensiones normales máximas referidas al momento flector respecto al eje y
      double ratioStrainMaxMxy = ebes.StrainMxyMax_[groupId][hi] / ebes.Groups_[groupId][ebes.STRESS];
      // módulo resistente por tensión de tracción respecto al momento flector Mxz
      double Wyt = ebes.MxyMax_[groupId][hi] / ebes.Groups_[groupId][ebes.STRESS] * ratioStrainMaxMxy;
      // módulo resistente máximo necesario
      double Wxy = Math.max(Wyc, Wyt);
      // conversión de unidades de medidas a cm3
      Wxy *= 1000000;
      // altura necesaria en función del módulo resistente y del momento flector respecto al eje z
      Y[2] = ebes.Interpolation_I_Single_Y_func_Wxy_(Wxy);

      // altura máxima necesaria
      double y = 0.0;
      for (int i = 1; i < Y.length; i++) {
        y = Math.max(y, Y[i]);
      }
      double z = ebes.Interpolation_I_Single_Z_func_Y_(y);
      double ey = ebes.Interpolation_I_Single_ey_func_Y_(y);
      double ez = ebes.Interpolation_I_Single_ez_func_Y_(y);

      int variableIndex = ebes.getVariablePosition(groupId);
      // conversión de unidades de medidas al sistema de cálculo
      y *= 0.001;
      if (y < solution.getLowerBound(variableIndex))
        y = solution.getLowerBound(variableIndex);

      if (y > solution.getUpperBound(variableIndex))
        y = solution.getUpperBound(variableIndex);

      z *= 0.001;
      ey *= 0.001;
      ez *= 0.001;

      solution.setVariableValue(variableIndex, y);
      solution.setVariableValue(variableIndex + 1, z);
      solution.setVariableValue(variableIndex + 2, ey);
      solution.setVariableValue(variableIndex + 3, ez);

      // double newValue = mutateVariable(variableIndex, solution);
    }
  }

  private void mutateVariable(int variableIndex, DoubleSolution solution) {
    double rnd, delta1, delta2, mutPow, deltaq;
    double y, yl, yu, val, xy;
    double distributionIndex = 20.0 ;

    y = solution.getVariableValue(variableIndex);
    yl = solution.getLowerBound(variableIndex);
    yu = solution.getUpperBound(variableIndex);
    delta1 = (y - yl) / (yu - yl);
    delta2 = (yu - y) / (yu - yl);
    rnd = randomGenerator.nextDouble();
    mutPow = 1.0 / (distributionIndex + 1.0);
    if (rnd <= 0.5) {
      xy = 1.0 - delta1;
      val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, (distributionIndex + 1.0)));
      deltaq = Math.pow(val, mutPow) - 1.0;
    } else {
      xy = 1.0 - delta2;
      val =
        2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, (distributionIndex + 1.0)));
      deltaq = 1.0 - (Math.pow(val, mutPow));
    }
    y = y + deltaq * (yu - yl);
    if (y < yl) {
      y = yl;
    }
    if (y > yu) {
      y = yu;
    }

    //return y ;
    solution.setVariableValue(variableIndex, y);

  }

private double quehacer(int groupId)
{
    double coefficient;
    if(Math.abs(ebes.getStrainMax(groupId,0)) > Math.abs(ebes.getStrainMin(groupId,0))) {
        if (ebes.getStrainMax(groupId, 0) < ebes.Groups_[groupId][ebes.STRESS]) {
            // reducir alto y ancho para auentar tensión
            // o reducir espesores
            coefficient=0.95;
        } else {
            // auemntar alto y ancho para reducir tensión
            // o aumentar espesores
            coefficient=1.05;
        }
    }
    else {
        if (ebes.getStrainMin(groupId,0) > ebes.Groups_[groupId][ebes.COMPRESSION]){
            // reducir alto y ancho para auentar tensión
            // o reducir espesores
            coefficient=0.95;
        }
        else{
            // auemntar altura para reducir tensión
            // o aumentar espesores
            coefficient=1.05;
        }
    }

    return coefficient;
}

  private void doRealMutation(double probability, DoubleSolution solution) {
    double rnd, delta1, delta2, mutPow, deltaq;
    double y, yl, yu, val, xy;
    double distributionIndex = 20.0 ;

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      if (randomGenerator.nextDouble() <= probability) {
        y = solution.getVariableValue(i);
        yl = solution.getLowerBound(i) ;
        yu = solution.getUpperBound(i) ;
        delta1 = (y - yl) / (yu - yl);
        delta2 = (yu - y) / (yu - yl);
        rnd = randomGenerator.nextDouble();
        mutPow = 1.0 / (distributionIndex + 1.0);
        if (rnd <= 0.5) {
          xy = 1.0 - delta1;
          val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, distributionIndex + 1.0));
          deltaq = Math.pow(val, mutPow) - 1.0;
        } else {
          xy = 1.0 - delta2;
          val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, distributionIndex + 1.0));
          deltaq = 1.0 - Math.pow(val, mutPow);
        }
        y = y + deltaq * (yu - yl);
        if (y < yl) {
          y = yl;
        }
        if (y > yu) {
          y = yu;
        }
        solution.setVariableValue(i, y);
      }
    }
  }
}

