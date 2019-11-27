package org.uma.jmetal.problem.multiobjective.qom;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class representing problem Qom Simple hydrological model for the calculation of losses and
 * excesses
 */
public class Qom extends AbstractDoubleProblem {

  /**
   * Constructor. Creates a default instance of the Qom problem. is a Hydrological Model
   * Rainfall-Runoff Gustavo R. Zavala Antonio J. Nebro.
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */

  // selected objetive functions
  protected String file_;

  protected String[] selectedOF_;
  // amount evaluations
  protected int numberOfEval_;
  // maximum evaluations
  protected int test_;

  // Basin file
  protected String description_;
  protected String shapeName_; // Fast, Triangle, Trapeze, Rectangle, Oval, Slow
  protected double shape_; // coefficient of bsin shape, 0.5 to 2.5
  protected double area_; // km2
  protected double pervious_; // 0.0 to 1.0
  protected double CT_; // concentration time
  protected String dateIni_; // "yyyy/MM/dd hh:mm:00 tt"
  protected String dateFin_; // "yyyy/MM/dd hh:mm:00 tt"
  protected String dateIniPartial_; // "yyyy/MM/dd hh:mm:00 tt"
  protected String dateFinPartial_; // "yyyy/MM/dd hh:mm:00 tt"
  protected double Δt_; // hours decimal
  protected int regIni_;
  protected int regFin_;
  protected Boolean Percolation_is_base_flow_; // True or False
  protected double Added_direct_infiltration_; // True or False
  protected double Qoi_; // initial flow in impervious area (m3/s)
  protected double Qop_; // initial flow in pervious area (m3/s)
  protected double Qog_; // initial flow in ground water (m3/s)
  protected double Ro_; // humedad antecedente volume (mm)
  protected double So_; // humedad antecedente volume (mm)
  protected double Rmax_; // mm
  protected double Smax_; // mm
  protected double VC_; // mm/h
  protected double Ki_; // 1/s
  protected double Kp_; // 1/s
  protected double Kg_; // 1/s
  protected double Eq_;
  protected double Coefficient_to_increase_the_peaks_flow_ = 0.0;
  protected double Limits_to_increase_the_peaks_flow_ = 0.0;
  protected double Superficial_Storage_to_start_considering_runoff_ = 0.0;
  protected int ratio_between_evaporation_and_soil_moisture_ = 0;
  protected int varRmax_ = 0;
  protected int varSmax_ = 1;
  protected int varVC_ = 2;
  protected int varKi_ = 3;
  protected int varKp_ = 4;
  protected int varKg_ = 5;
  protected int varCT_ = 6;
  protected int varShape_ = 7;
  protected int varEq_ = 8;

  // conditions wet initial
  double Ri_1_;
  double Si_1_;
  double Di_1_;
  double EVTi_1_;
  // potential volumes
  double Dp_;
  // total volumes
  double sumVi_;
  double sumVp_;
  double sumVg_;
  double sumRunoffR_;
  double sumVixArea_;
  double sumVpxArea_;
  double sumVgxArea_;
  // moisture balance
  double Input_;
  double Evapotraspiration_;
  double SuperficialStore_;
  double SoilStore_;
  double StoreOrOutput_Deep_;
  double OutputSuperficial_;
  double MassBalance_;
  double MassBalanceError_;
  double Qpo_; // maximum streamflow for hydrograph observaded
  double QPc_; // maximum streamflow for hydrograph calculated
  double QPic_; // maximum streamflow for hydrograph calculated in impervious superfitial soil
  double QPpc_; // maximum streamflow for hydrograph calculated in pervious superfitial soil
  double QPgc_; // maximum streamflow for hydrograph calculated in growndwater
  double Qmo_; // minimum streamflow for hydrograph observaded
  double Qmc_; // minimum streamflow for hydrograph calculated
  double VHo_; // volume for hydrograph observaded
  double VHc_; // volume for hydrograph calculated
  double maxO_; // maximum observed steamflow

  String textOF_ = "";
  String optOF_ = "";
  double optRmax_ = 0;
  double optSmax_ = 0;
  double optVC_ = 0;
  double optKi_ = 0;
  double optKp_ = 0;
  double optKg_ = 0;
  double optCT_ = 0;
  double optShape_ = 0;
  double optEq_ = 0;

  // state variable for read VAR file
  int maxLine_, line_;
  // read variables in VAR jMetal file
  double[][] var_;
  // objetive function derivaded for validation test
  double[][] fd_;

  //  rainfall (constant)
  protected double[] Pconst_;

  public double getcconst_(int reg) {
    return Pconst_[reg];
  } // get Pconst

  //  rainfall accumulated (constant)
  protected double[] Paccum_;

  public double getPaccum_(int reg) {
    return Paccum_[reg];
  } // get Paccum

  //  rainfall
  protected double[] P_;

  public double getP_(int reg) {
    return P_[reg];
  } // get P

  //  potential evapotraspiration (constant)
  protected double[] PEVTconst_;

  public double getPEVTconst_(int reg) {
    return PEVTconst_[reg];
  } // get PEVTconst

  //  potential evapotraspiration
  protected double[] PEVT_;

  public double getPEVT_(int reg) {
    return PEVT_[reg];
  } // get PEVT

  //  total rainfall
  protected double[] EVT_;

  public double getEVT_(int reg) {
    return EVT_[reg];
  } // get EVT

  //  observed flow
  protected java.lang.Double[] Qo_;

  public java.lang.Double getQo_(int reg) {
    return Qo_[reg];
  } // get Qo

  //  calculated flow
  protected java.lang.Double[] Qc_;

  public java.lang.Double getQc_(int reg) {
    return Qc_[reg];
  } // get Qc

  //  observed depth
  protected double[] Ho_;

  public double getHo_(int reg) {
    return Ho_[reg];
  } // get Ho

  //  calculated depth
  protected double[] Hc_;

  public double getHc_(int reg) {
    return Hc_[reg];
  } // get Hc

  //  superficial storage
  protected double[] R_;

  public double getR_(int reg) {
    return R_[reg];
  } // get R

  //  soil storage
  protected double[] S_;

  public double getS_(int reg) {
    return S_[reg];
  } // get S

  //  deep storage
  protected double[] D_;

  public double getD_(int reg) {
    return D_[reg];
  } // get D

  //  impervious volume for time i
  protected double[] Vi_;

  public double getVi_(int reg) {
    return Vi_[reg];
  } // get Vi

  //  pervious volume for time i
  protected double[] Vp_;

  public double getVp_(int reg) {
    return Vp_[reg];
  } // get Vp

  //  ground water volume for time i
  protected double[] Vg_;

  public double getVg_(int reg) {
    return Vg_[reg];
  } // get Vg

  protected double[] RunoffR_;

  public double getRunoffR_(int reg) {
    return RunoffR_[reg];
  } // get RunoffR

  //  impervious flux for time i
  protected double[] Qi_;

  public double getQi_(int reg) {
    return Qi_[reg];
  } // get Qi

  //  pervious flux for time i
  protected double[] Qp_;

  public double getQp_(int reg) {
    return Qp_[reg];
  } // get Qp

  //  ground water flux for time i
  protected double[] Qg_;

  public double getQg_(int reg) {
    return Qg_[reg];
  } // get Qg

  //  impervious volume for time i
  protected double[] Vis_;

  public double getVis_(int reg) {
    return Vis_[reg];
  } // get Vis

  //  pervious volume for time i
  protected double[] Vps_;

  public double getVps_(int reg) {
    return Vps_[reg];
  } // get Vps

  //  ground water volume for time i
  protected double[] Vgs_;

  public double getVgs_(int reg) {
    return Vgs_[reg];
  } // get Vgs

  //  impervious flux for time i
  protected double[] Qis_;

  public double getQis_(int reg) {
    return Qis_[reg];
  } // get Qis

  //  pervious flux for time i
  protected double[] Qps_;

  public double getQps_(int reg) {
    return Qps_[reg];
  } // get Qps

  //  ground water flux for time i
  protected double[] Qgs_;

  public double getQgs_(int reg) {
    return Qgs_[reg];
  } // get Qgs

  //  volume of hydrograph observated
  protected double[] Vo_;

  public double getVo_(int reg) {
    return Vo_[reg];
  } // get Vo

  //  volume of hydrograph calculated
  protected double[] Vc_;

  public double getVc_(int reg) {
    return Vc_[reg];
  } // get Vc

  // sorted observed stramflow for duration curve
  protected Double[] QoD_;
  protected Double[] QfD_;
  protected double[] DurationT_ = {
    0.0, 0.05, 0.1, 0.2, 0.3, 0.5, 0.8, 1.0, 3.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0,
    45.0, 50.0, 55.0, 60.0, 65.0, 70.0, 75.0, 80.0, 85.0, 90.0, 95.0, 100.0
  };
  int DurationSeg_ = DurationT_.length;
  protected java.lang.Double[] DurationQo_ = new java.lang.Double[DurationSeg_];
  protected java.lang.Double[] DurationQe_ = new java.lang.Double[DurationSeg_];

  int peakPindx_ = 0;
  double peakP_;
  int peakPindxQ_ = 0;
  double peakPQ_ = 0;
  double peakPV_ = 0;
  double peakPT_ = 0;

  double peakQ_;
  int peakQindxP_ = 0;
  double peakQP_[] = new double[2];
  double peakQV_ = 0;
  double peakQT_ = 0;
  
  public Qom(String qomDataFile) {
    line_ = 0;
    Qom_Initialize(qomDataFile);
  }

  public void Qom_Initialize(String qomDataFile) {

    // name of class and problem
    setName("Qom");

    System.out.println("Qom a simple model of losses and excesses");
    System.out.println("Qom-Clark V2.0 (2017/07/14) a rainfall-runoff model");
    // read definition problem
    Qom_ReadProblems(qomDataFile);
    // dataset read files
    System.out.println("Problem file: " + file_);
    Qom_ReadFileBasin(file_ + ".basin");

    // shape_=ShapeCoefficient(shapeName_);
    // Fill lower and upper limits

    Qom_ReadLimitsVariable(file_ + ".var");
    Qom_ReadFileRain(file_ + ".rain");
    Qom_ReadFilePEVT(file_ + ".pevt");
    Qom_ReadFileQo(file_ + ".Qo");
    // Qom_ReadFileHo(file_ + ".Ho");
    // problem data print
    System.out.println("Optimization multi-objective: ");
    System.out.println("  Number of objective function: " + getNumberOfObjectives());
    System.out.println("  " + textOF_);
    System.out.println("  Number of Variables: " + getNumberOfVariables());
    System.out.println("  Number of Constraints: " + getNumberOfConstraints());

    System.out.println("Algorithm configuration: ");

    optCT_ = CT_;
    optShape_ = shape_;
    optEq_ = Eq_;
    numberOfEval_ = 0;
  } // end InitializeEBEs

  public void evaluate(DoubleSolution solution) {

    int hi = 0;
    double[] fx = new double[getNumberOfObjectives()]; // functions

    if (test_ == -1 || test_ == 1) {
      if (test_ == -1 && line_ < maxLine_) // variables from VAR
      {
        System.out.println("Evaluation: " + line_);
        for (int j = 0; j < getNumberOfVariables(); j++) {
          solution.setVariable(j, var_[line_][j]);
        }
        if (Ro_ > solution.getVariable(varRmax_)) Ro_ = solution.getVariable(varRmax_);
        if (So_ > solution.getVariable(varSmax_)) So_ = solution.getVariable(varSmax_);
      } else if (test_ == 1) {
        // variables form file *.basin
        if (getNumberOfVariables() >= 6) {
          solution.setVariable(varRmax_, Rmax_);
          solution.setVariable(varSmax_, Smax_);
          solution.setVariable(varVC_, VC_);
          solution.setVariable(varKi_, Ki_);
          solution.setVariable(varKp_, Kp_);
          solution.setVariable(varKg_, Kg_);
        }
        if (getNumberOfVariables() >= 7) solution.setVariable(varCT_, CT_);
        if (getNumberOfVariables() >= 8) solution.setVariable(varShape_, shape_);
        if (getNumberOfVariables() >= 9) solution.setVariable(varEq_, Eq_);

        if (Ro_ > Rmax_ || So_ > Smax_) {
          System.out.println("Error: antecedent moisture conditions are greater than the");
          System.out.println("allowable maximum, check the values ​​of Ro, So and limit variables");
          System.exit(1);
        }
      }
    }

    Rmax_ = solution.getVariable(varRmax_);
    Smax_ = solution.getVariable(varSmax_);
    VC_ = solution.getVariable(varVC_); //
    Ki_ = solution.getVariable(varKi_); //
    Kp_ = solution.getVariable(varKp_); //
    Kg_ = solution.getVariable(varKg_); //
    if (getNumberOfVariables() >= 7) CT_ = solution.getVariable(varCT_);
    if (getNumberOfVariables() >= 8) shape_ = solution.getVariable(varShape_);
    if (getNumberOfVariables() >= 9) Eq_ = solution.getVariable(varEq_);

    // Method simple losses, a hydrological problem
    Qom_Calculus();
    optOF_ = "";

    // START OBJETIVES FUNCTION
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      if (selectedOF_[i].equals("NSE")) {
        fx[i] = FunctionNSE(Qo_, Qc_);
      } else if (selectedOF_[i].equals("NSE-V")) {
        // fx[i] = FunctionNSE(Vo_, Vc_);
      } else if (selectedOF_[i].equals("NSEO")) {
        fx[i] = FunctionNSEO(Qo_, Qc_);
      } else if (selectedOF_[i].equals("NSEO-V")) {
        // fx[i] = FunctionNSEO(Vo_, Vc_);
      } else if (selectedOF_[i].equals("R2")) {
        fx[i] = FunctionCDR2(Qo_, Qc_);
      } else if (selectedOF_[i].equals("MDV")) {
        fx[i] = Functions_Mahalanobis_Distance(Qo_, Qc_);
      } else if (selectedOF_[i].equals("PBIAS")) {
        fx[i] = FunctionPBIAS(Qo_, Qc_);
      } else if (selectedOF_[i].equals("RQV")) {
        fx[i] = FunctionRQV(Qo_, Qc_, Vo_, Vc_);
      } else if (selectedOF_[i].equals("ERQV")) {
        fx[i] = FunctionERQV(Qo_, Qc_, Vo_, Vc_);
      } else if (selectedOF_[i].equals("Module")) {
        fx[i] = FunctionModule(Qo_, Qc_);
      } else if (selectedOF_[i].equals("SE")) {
        fx[i] = FunctionSE(Qo_, Qc_);
      } else if (selectedOF_[i].equals("SIE")) {
        fx[i] = FunctionSIE(Qo_, Qc_);
      } else if (selectedOF_[i].equals("RSE")) {
        fx[i] = FunctionRSE(Qo_, Qc_);
      } else if (selectedOF_[i].equals("MSE")) {
        fx[i] = FunctionMSE(Qo_, Qc_);
      } else if (selectedOF_[i].equals("RMSE")) {
        fx[i] = FunctionRMSE(Qo_, Qc_);
      } else if (selectedOF_[i].equals("RSR")) {
        fx[i] = FunctionRSR(Qo_, Qc_);
      } else if (selectedOF_[i].equals("M4E")) {
        fx[i] = FunctionM4E(Qo_, Qc_);
      } else if (selectedOF_[i].equals("MSDE")) {
        fx[i] = FunctionMSDE(Qo_, Qc_);
      } else if (selectedOF_[i].equals("MLSE")) {
        fx[i] = FunctionMLSE(Qo_, Qc_);
      } else if (selectedOF_[i].equals("EMQQ")) {
        fx[i] = FunctionEMQQ(Qo_, Qc_);
      } else if (selectedOF_[i].equals("ERQQ")) {
        fx[i] = FunctionERQQ(Qo_, Qc_);
      } else if (selectedOF_[i].equals("MBE")) {
        double a = Paccum_[P_.length - 1];
        double b = (sumVg_ + sumVi_ + sumVp_);
        fx[i] = (1 - Math.min(a, b) / Math.max(a, b)) * 100;
      } else if (selectedOF_[i].equals("REMB")) {
        // Relative Error of the Mass Balance
        fx[i] = Math.abs(MassBalance_ / Input_) * 100;
      } else if (selectedOF_[i].equals("DC")) {
        // Relative Error of the Mass Balance
        Function_Duration_Curve(Qc_);
        fx[i] = FunctionPBIAS(DurationQo_, DurationQe_);
      } else {
        System.out.println("Error: the objective function " + i + " is not recognized");
        System.exit(1);
      }

      optOF_ += fx[i] + " ";

      solution.setObjective(i, fx[i]);
    }

    VHo_ = SumVolumeOfHydrograph(Vo_);
    VHc_ = SumVolumeOfHydrograph(Vc_);

    numberOfEval_++;
    this.evaluateConstraints(solution);
  } // evaluate

  /**
   * Evaluates the constraint overhead of a solution
   *
   * @param solution The solution
   * @throws JMetalException
   */
  public void evaluateConstraints(DoubleSolution solution) {

    double[] constraint = new double[this.getNumberOfConstraints()];
    double[] fx = new double[getNumberOfObjectives()]; // functions
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      fx[i] = solution.getObjective(i);
    }

    // constraint No 1: Mass Balance
    double absoluteVolumeErr = MassBalance_;
    double observedVolume = Input_;
    double relativeVolumeErr = absoluteVolumeErr / observedVolume;
    constraint[0] = 0.30 - Math.abs(relativeVolumeErr); // < 0.25 -> 0.25%

    // constraint No 2: Nash-Sutclife Efficiency
    double NSE = FunctionNSE(Qo_, Qc_);
    constraint[1] = NSE - 0.70; // < at 0.85 of NSE

    // constraint No 3: ERQQ relative error of max and min streamflow
    double ERQQ = FunctionERQQ(Qo_, Qc_);
    constraint[2] = 0.30 - ERQQ; //

    // constraint No 3: relative error for volume derived hydrograph
    // < at 25.0->0.25%
    // x [Hm3] * 1000.0 [mm/m] / Area[km2]
    observedVolume = (VHo_ * 1000.0 / area_);
    if (Percolation_is_base_flow_ && true) {
      // absoluteVolumeErr= (StoreOrOutput_Deep_ + OutputSuperficial_) - observedVolume;
      absoluteVolumeErr = OutputSuperficial_ - observedVolume;
    } else {
      absoluteVolumeErr = OutputSuperficial_ - observedVolume;
    }
    // absoluteVolumeErr = OutputSuperficial_ - observedVolume;
    relativeVolumeErr = absoluteVolumeErr / observedVolume;
    constraint[3] = 0.15 - Math.abs(relativeVolumeErr); // < at 0.25->25%

    // error relativo del volumen genrardo por el hidrograma de Clark
    double RE = 0.0;
    if (VHo_ > 0) {
      RE = Math.abs((VHc_ - VHo_) / VHo_ * 100.0);
    } else {
      RE = 1e25;
    }

    constraint[4] = 0.15 - RE;

    constraint[5] = solution.getVariable(varKg_) - solution.getVariable(varKi_); // > 0
    constraint[6] = solution.getVariable(varKg_) - solution.getVariable(varKp_); // > 0
    if (VHo_ > 0) {
      Function_Duration_Curve(Qc_);
      constraint[7] = 20 - FunctionPBIAS(DurationQo_, DurationQe_); // > 0 is 20% or less
    } else {
      constraint[7] = 1e25;
    }

    double total = 0.0;
    String optOF = "";
    for (int i = 0; i < this.getNumberOfConstraints(); i++)
      if (constraint[i] < 0.0) {
        total += constraint[i];
      } else {
        // objective functions
        // decision variables
        optRmax_ = solution.getVariable(varRmax_);
        optSmax_ = solution.getVariable(varSmax_);
        optVC_ = solution.getVariable(varVC_);
        optKi_ = solution.getVariable(varKi_);
        optKp_ = solution.getVariable(varKp_);
        optKg_ = solution.getVariable(varKg_);
        if (getNumberOfVariables() >= 7) optCT_ = solution.getVariable(varCT_);
        if (getNumberOfVariables() >= 8) optShape_ = solution.getVariable(varShape_);
        if (getNumberOfVariables() >= 9) optEq_ = solution.getVariable(varEq_);

        // System.out.println("Evaluation: " + numberOfEval_) ;
        optOF = "";
        for (int j = 0; j < getNumberOfObjectives(); j++) {
          optOF += "f" + (j + 1) + ": " + solution.getObjective(j) + " ";
        }

        if ((numberOfEval_ % 1000) == 0) {
          System.out.println("Evaluation: " + numberOfEval_);
          System.out.println(optOF);
          // System.out.println("  Volumes hydrograph. O: " + VHo_ + " - C: " + VHc_ );
          System.out.println(" Rmax: " + optRmax_); // x[0]
          System.out.println(" Smax: " + optSmax_); // x[1]
          System.out.println(" VC: " + optVC_); // x[2]
          System.out.println(" Ki: " + optKi_); // x[3]
          System.out.println(" Kp: " + optKp_); // x[4]
          System.out.println(" Kg: " + optKg_); // x[5]
          System.out.println(" CT: " + optCT_); // x[6]
          System.out.println(" Shape : " + optShape_); // x[7]
          System.out.println(" Eq : " + optEq_); // x[8]
        }
      }

    // solution.setOverallConstraintViolation(total);
    // solution.setNumberOfViolatedConstraint(number);
    for (int i = 0; i < getNumberOfConstraints(); i++) {
      solution.setConstraint(i, constraint[i]);
    }

    if (test_ == 1 || test_ == -1) {
      optOF = "";
      for (int j = 0; j < getNumberOfObjectives(); j++) {
        optOF += "f" + (j + 1) + ": " + solution.getObjective(j) + " ";
      }
      System.out.println(optOF); // x[0]
      System.out.println(" Rmax: " + optRmax_); // x[0]
      System.out.println(" Smax: " + optSmax_); // x[1]
      System.out.println(" VC: " + optVC_); // x[2]
      System.out.println(" Ki: " + optKi_); // x[3]
      System.out.println(" Kp: " + optKp_); // x[4]
      System.out.println(" Kg: " + optKg_); // x[5]
      System.out.println(" CT: " + optCT_); // x[6]
      System.out.println(" Shape : " + optShape_); // x[7]
      System.out.println(" Eq : " + optEq_); // x[8]

      if (test_ == 1) {
        Qom_PrintArchSolutions();
        if (test_ == 1) System.exit(0);
      } else if (test_ == -1) {
        if (Ro_ > 0) {
          if (Ro_ > optRmax_) Ro_ = optRmax_ * 0.9;
        }
        if (So_ > 0) {
          if (So_ > optSmax_) So_ = optSmax_ * 0.9;
        }

        fd_[line_][0] = fx[0];
        fd_[line_][1] = fx[1];
        if (line_ >= maxLine_ - 1) {
          Qom_save_OF_validation();
          System.exit(0);
        }
        line_++;
      }
    } else {
      if (numberOfEval_ == test_) Toolkit.getDefaultToolkit().beep();
    }
  } // evaluateConstraints

  public void Qom_Calculus() throws JMetalException {

    // clear variables

    Qom_InitializeProblemVariables();

    // initials condition
    // previous storage
    Ri_1_ = Ro_;
    // lineal rate
    // double z =  Ri_1_ / Rmax_;
    // exponential rate
    double z = 1 - Math.pow(Math.E, -Ri_1_ / (Rmax_ - Ri_1_));
    double Sp = z * Smax_;
    Si_1_ = Math.max(Sp, So_);

    // lineal rate
    // double z2 = Si_1_ / Smax_;
    // exponential rate
    double z2 = 1 - Math.pow(Math.E, -Si_1_ / (Smax_ - Si_1_));

    Dp_ = z2 * VC_ * Δt_; // potential percolation
    Di_1_ = Math.min(Dp_, Si_1_);

    for (int i = 0; i < P_.length; i++) {

      R_[i] = Ri_1_;
      S_[i] = Si_1_;
      D_[i] = Di_1_;

      // superfitial storage por rain
      if (P_[i] >= (Rmax_ - R_[i])) {
        Superfitial_Storage_per_Rain_Excess(i);
      } else {
        Superfitial_Storage_per_Rain_Storage(i);
      }

      // potential evapotranspiration for water in superfitial storage
      if (R_[i] >= PEVT_[i]) {
        Superfitial_Losses_per_PEVT_Excess(i);
      } else {
        Superfitial_Losses_per_PEVT_Deficit(i);
      }

      // potential evapotranspiration for rain
      if (P_[i] >= PEVT_[i]) {
        Rain_Losses_per_PEVT_Excess(i);
      } else {
        Rain_Losses_per_PEVT_Deficit(i);
      }

      if (pervious_ > 0.0) {
        // soil storage for infiltration
        SoilStorage(i);

        // storage pur direct infiltration into soil
        if (Added_direct_infiltration_ > 0.0) {
          AddDirectInfiltrationExponential(i);
          // AddDirectInfiltrationLinear(i);
        }

        // deep flow in soil for percolation
        Percolation(i);
      }
      // soil evaporation
      if (ratio_between_evaporation_and_soil_moisture_ == 0) {
        // ratio lineal
        Soil_Moisture_Loss_By_Evapotraspiration_Linear(i);
      } else {
        // ratio exponential
        Soil_Moisture_Loss_By_Evapotraspiration_Exponential(i);
      }

      // separation of volumes
      SeparationVolumes(i);

      Ri_1_ = R_[i];
      Si_1_ = S_[i];
      Di_1_ = D_[i];
      EVTi_1_ = EVT_[i];
    }

    Runoff();

    MoistureBalance();
  }

  public void Superfitial_Storage_per_Rain_Excess(int i) {
    // rain is greater than the rest of the storage
    P_[i] = P_[i] - (Rmax_ - R_[i]);
    R_[i] = Rmax_;
  }

  public void Superfitial_Storage_per_Rain_Storage(int i) { // rain is less than the storage
    R_[i] += P_[i];
    P_[i] = 0.0;
  }

  public void Superfitial_Losses_per_PEVT_Excess(int i) {
    R_[i] -= PEVT_[i];
    EVT_[i] = PEVT_[i];
    PEVT_[i] = 0.0;
  }

  public void Superfitial_Losses_per_PEVT_Deficit(int i) {
    PEVT_[i] -= R_[i];
    EVT_[i] = R_[i];
    R_[i] = 0.0;
  }

  public void Rain_Losses_per_PEVT_Deficit(int i) {
    PEVT_[i] -= P_[i];
    EVT_[i] += P_[i];
    P_[i] = 0.0;
  }

  public void Rain_Losses_per_PEVT_Excess(int i) {
    // is P >= PEPT
    P_[i] -= PEVT_[i];
    EVT_[i] += PEVT_[i];
    PEVT_[i] = 0.0;
  }

  public void SoilStorage(int i) {
    // lineal rate
    // double z =  R_[i] / Rmax_;

    // exponential rate
    if (R_[i] > 0.0) {
      double aux = R_[i] / (Rmax_ - R_[i]);
      double z = 1 - Math.pow(Math.E, -aux);
      double Sp = z * Smax_;
      double Saux = Math.max(S_[i], Sp);
      double ΔS = Math.max(Saux - S_[i], 0.0);

      if (Saux + ΔS > Smax_) ΔS = Smax_ - Saux;

      if (R_[i] - ΔS > 0.0) {
        R_[i] -= ΔS;
        S_[i] = Math.min(S_[i] + ΔS, Smax_);
        ΔS = 0.0;
      } else {
        S_[i] = Math.min(S_[i] + R_[i], Smax_);
        ΔS = Math.max(ΔS - R_[i], 0.0);
        R_[i] = 0.0;
        // ΔS = Math.max(Smax_-S_[i], 0.0);
      }
    }
  }

  public void AddDirectInfiltrationExponential(int i) {

    if (P_[i] > 0.0 && S_[i] < Smax_) {
      double ΔS = Smax_ - S_[i];
      double aux = S_[i] / (Smax_ - S_[i]);
      double z = Math.pow(Math.E, -aux);
      double ΔP1 = P_[i] * z;
      double ΔP2 = P_[i] * Added_direct_infiltration_;
      double ΔP = Math.max(ΔP1, ΔP2);

      if (ΔP - ΔS > 0.0) {
        P_[i] -= ΔS;
        S_[i] = Math.min(S_[i] + ΔS, Smax_);
      } else {
        S_[i] = Math.min(S_[i] + ΔP, Smax_);
        P_[i] = Math.max(P_[i] - ΔP, 0.0);
      }
    }
  }

  public void AddDirectInfiltrationExponentialBAK(int i) {

    if (P_[i] > 0.0 && S_[i] < Smax_) {
      double ΔS = Smax_ - S_[i];
      double aux = S_[i] / (Smax_ - S_[i]);
      double z = Math.pow(Math.E, -aux);
      double ΔP1 = P_[i] * z;
      double ΔP2 = P_[i] * Added_direct_infiltration_;
      double ΔP = Math.max(ΔP1, ΔP2);

      if (ΔP - ΔS > 0.0) {
        P_[i] -= ΔS;
        S_[i] = Math.min(S_[i] + ΔS, Smax_);
      } else {
        S_[i] = Math.min(S_[i] + ΔP, Smax_);
        P_[i] = Math.max(P_[i] - ΔP, 0.0);
      }
    }
  }

  public void AddDirectInfiltrationLinear(int i) {

    if (P_[i] > 0.0 && S_[i] < Smax_) {
      double ΔS = Smax_ - S_[i];
      if (P_[i] * Added_direct_infiltration_ - ΔS > 0.0) {
        P_[i] -= ΔS;
        S_[i] = Math.min(S_[i] + ΔS, Smax_);
        ΔS = 0.0;
      } else {
        double aux;
        aux = P_[i] * Added_direct_infiltration_;
        S_[i] = Math.min(S_[i] + aux, Smax_);
        P_[i] = Math.max(P_[i] - aux, 0.0);
        ΔS -= aux;
      }
    }
  }

  public void Percolation(int i) {

    if (i > 0) {
      // exponential rate
      double aux = S_[i] / (Smax_ - S_[i]);
      double z = 1 - Math.exp(-aux); // ratio lineal Math.pow(2.7182,-aux)
      // double z = S_[i] / Smax_;
      Dp_ = z * VC_ * Δt_; // potential percolation

      if (S_[i] > Dp_) {
        D_[i] = Dp_;
        S_[i] -= Dp_;
      } else {
        D_[i] = S_[i];
        S_[i] = 0.0;
      }
    }
  }

  public void Soil_Moisture_Loss_By_Evapotraspiration_Exponential(int i) {
    // curve relationship between EVT and soil moisture

    if (PEVT_[i] > 0.0 && S_[i] > 0.0) {

      double aux = 0.0;
      double z = 0.0;

      if (PEVT_[i] > 0.0 && S_[i] > 0.0) {
        if (PEVT_[i] > S_[i])
          // soil moisture is less than the evapotranspiration or
          aux = S_[i] / (PEVT_[i] - S_[i]);
      } else {
        aux = PEVT_[i] / (S_[i] - PEVT_[i]);
      }
      z = 1 - Math.pow(2.7182, -aux); // 4*2.7182
      double ev = Math.max(z, 0.0);
      PEVT_[i] -= ev;
      EVT_[i] += ev;
      S_[i] = Math.max(S_[i] - ev, 0.0);
    }
  }

  public void Soil_Moisture_Loss_By_Evapotraspiration_Linear(int i) {

    if (PEVT_[i] > 0.0 && S_[i] > 0.0 && R_[i] == 0.0) {
      if (PEVT_[i] > S_[i]) {
        // soil moisture is less than the evapotranspiration
        PEVT_[i] = Math.max(PEVT_[i] - S_[i], 0.0);
        EVT_[i] += S_[i];
        S_[i] = 0.0;
      } else {
        // soil moisture is higher than the evapotranspiration
        EVT_[i] += PEVT_[i];
        S_[i] -= PEVT_[i];
        PEVT_[i] = 0.0;
      }
    }
  }

  public void SeparationVolumes(int i) {

    // water volume in impervious area
    double runoffPervious = 0.0;
    double runoffImpervious = 0.0;
    double excess = 0.0;

    if (R_[i]
        > Superficial_Storage_to_start_considering_runoff_
            * Rmax_) { // && (1.0 - Superficial_Storage_to_start_considering_runoff_) > 0.0
      runoffPervious = pervious_;
      runoffImpervious = 1.0 - pervious_;
      excess = Math.max((R_[i] - Superficial_Storage_to_start_considering_runoff_ * Rmax_), 0.0);
    }

    RunoffR_[i] = excess;
    // water volume runoff for impervious area
    Vi_[i] = P_[i] * (1.0 - pervious_) + runoffImpervious * excess;
    // water volume runoff in pervious area
    Vp_[i] = P_[i] * pervious_ + runoffPervious * excess;
    // deep groudwater volume
    Vg_[i] = D_[i];

    // R_[i] = Math.max(R_[i]-RunoffR_[i], 0);
    R_[i] = R_[i] - RunoffR_[i];
    // P_[i] += Math.min(RunoffR_[i], Pconst_[i]-RunoffR_[i]);
    P_[i] += RunoffR_[i];
  }

  public void Runoff() throws JMetalException {
    // surface spatial distribution coefficient applied to surface runoff volumes
    Vis_ = SyntheticHistogramArea(shape_, CT_, Δt_, Vi_);
    Vps_ = SyntheticHistogramArea(shape_, CT_, Δt_, Vp_);
    if (Percolation_is_base_flow_) {
      Vgs_ = SyntheticHistogramArea(shape_, CT_, Δt_, Vg_);
    }

    // overlandwater flux
    Qis_ = SimpleLinearReservoir(area_, Vis_, Ki_, Δt_, Qoi_);

    // groundwater layer
    Qps_ = SimpleLinearReservoir(area_, Vps_, Kp_, Δt_, Qop_);

    // deep water flow for the groundwater
    if (Percolation_is_base_flow_) {
      Qgs_ = SimpleLinearReservoir(area_, Vgs_, Kg_, Δt_, Qog_);
    }
    // total flux spread calculated
    for (int i = 0; i < Qis_.length; i++) {
      Qc_[i] = Qis_[i] + Qps_[i] + Qgs_[i];
    }

    Vo_ = VolumeOfHydrograph(Qo_);
    Vc_ = VolumeOfHydrograph(Qc_);
  }

  public void MoistureBalance() {
    Input_ = 0.0;
    Evapotraspiration_ = 0.0;
    sumVi_ = 0.0;
    sumVp_ = 0.0;
    sumVg_ = 0.0;
    sumRunoffR_ = 0.0;
    sumVixArea_ = 0.0;
    sumVpxArea_ = 0.0;
    sumVgxArea_ = 0.0;
    int numReg = Pconst_.length;
    for (int i = 0; i < numReg; i++) {
      Input_ += Pconst_[i];
      Evapotraspiration_ += EVT_[i];
      sumVi_ += Vi_[i];
      sumVp_ += Vp_[i];
      sumVg_ += Vg_[i];
      sumRunoffR_ += RunoffR_[i];
    }
    // m3 = 1[mm] * 1[m]/1.000[mm] * Area[Km2] * 1.000.000[m2]/1[km2]
    // Hm3 = V[mm] * 1[Hm]/100.000[mm] * Area[km2] * 100[Hm2]/1[km2]
    sumVixArea_ = sumVi_ * area_ / 1000.0;
    sumVpxArea_ = sumVp_ * area_ / 1000.0;
    sumVgxArea_ = sumVg_ * area_ / 1000.0;

    SuperficialStore_ = R_[numReg - 1];
    SoilStore_ = S_[numReg - 1];
    StoreOrOutput_Deep_ = sumVg_;
    OutputSuperficial_ = sumVi_ + sumVp_; // + sumRunoffR_;
    // estimated - observed
    MassBalance_ =
        -So_
            - Ro_
            - Input_
            + SuperficialStore_
            + SoilStore_
            + StoreOrOutput_Deep_
            + OutputSuperficial_
            + Evapotraspiration_;
    // mass balance errors
    MassBalanceError_ = MassBalance_ / Input_ * 100;
  }

  public double ShapeCoefficient(String shape) throws JMetalException {
    /// <summary>
    /// shape coefficient
    /// </summary>
    double n = 1.0;
    try {
      // coeficient shape number
      // n is watershed response, if n is less than 1 quick, if n is greater than 1 is slow
      shape = shapeName_.toUpperCase();
      if (shape.equals("FAST")) {
        n = 0.25;
      } else if (shape.equals("TRIANGLE")) {
        n = 0.5;
      } else if (shape.equals("TRAPEZE")) {
        n = 0.75;
      } else if (shape.equals("RECTANGLE")) {
        n = 1.0;
      } else if (shape.equals("OVAL")) {
        n = 1.25;
      } else if (shape.equals("DIAMOND")) {
        n = 1.50;
      } else if (shape.equals("SLOW")) {
        n = 1.75;
      } else {
        System.out.println("Error: Shape Area Coefficient not recognized ( " + shape + " )");
        System.exit(1);
      }
      ;
    } catch (Exception ex) {
      System.out.println("Shape Area Coefficient - Error: " + ex.getMessage());
    }

    return n;
  }

  public double[] SyntheticHistogramArea(double n, double CT, double Δt, double[] V)
      throws JMetalException {
    /// <summary>
    /// shape coefficient number
    /// </summary>
    /// <summary>
    /// CT concentration time (hour)
    /// </summary>
    /// <summary>
    ///  Δt step time (hour)
    // / </summary>
    /// <summary>
    ///  V excess volume (mm)
    // / </summary>

    /// <summary>
    /// cumulative area
    /// </summary>
    double[] Ac;
    /// <summary>
    /// area between two consecutive cumulative areas
    /// </summary>
    double[] f;
    /// <summary>
    /// number of subbasin subdivision for for distribution
    /// </summary>
    int No;

    double[] Vs = new double[V.length];
    try {
      // coeficient
      // double a = Math.pow(0.5, (1.0 - n)); // de prueba
      double a = Math.pow(0.5, n); // original

      // number division of cuenca
      No = (int) (CT / Δt);
      if (No < 1) {
        No = 1;
      }

      // determining the cumulative areas
      Ac = new double[No];
      double[] time = new double[No];
      double dt = 1.0 / No;
      for (int i = 0; i < No; i++) {
        time[i] = (i + 1) * dt;
      }
      // t: is this unitary concentration time
      // 1/No: is this step of time
      for (int i = 0; i < No; i++) {
        if (time[i] <= 0.5) {
          Ac[i] = a * Math.pow(time[i], n);
        } else {
          Ac[i] = 1.0 - a * Math.pow((1.0 - time[i]), n);
        }
      }

      // area between two consecutive cumulative areas
      f = new double[No];
      f[0] = Ac[0];
      for (int i = 1; i < No; i++) {
        f[i] = Ac[i] - Ac[i - 1];
      }

      // runoff volume spead in function of Synthetic Histogram Area
      // limit down
      double fc = 0.0f;

      // runoff volume spread
      for (int t = 0; t < V.length; t++) {
        for (int i = 1; i <= t; i++) {
          if (t - i < No) fc = f[t - i];
          else fc = 0.0;
          // ratio for spread and sum volume runoff imprevius
          Vs[t] += V[i] * fc;
          // Console.WriteLine("{0}({1},{2})={3}", t, i, t-i, Vs[t]);
        }
      }
    } catch (Exception ex) {
      System.out.println("SynteticHistogramAreas - Error: " + ex.getMessage());
    }

    return Vs;
  }

  public double[] SimpleLinearReservoir(double Area, double[] Vs, double K, double Δt, double Qo)
      throws JMetalException {
    /// spread flow undergoes a buffer pool of equivalent
    /// to a simple linear function.
    /// overland water using pervious and impervious areas and groundwater
    /// Area: area basin (km2)
    /// Vs: volume of the streamflow in (mm/h)
    /// Ki, Kp and Kg: average time (hour) of emptying the reservoir related to Vs
    /// Δt: step time (hours)
    /// Qo: initial flux (m3/s)
    /// Qs: streamflow spread (m3/s)

    double[] Qs = new double[Vs.length];
    double peakQ = 0;
    double peakV = 0;
    int count = 0;
    double coefExp = 1.0;

    try {
      Qs[0] = Qo;

      // conversion mm/h to m3/s for area catchement
      // streamflow [m3/s] = V[mm/h] * 1[h/3600s] * 1000000[m2/km2] * Area[Km2] * 1[m/1000mm]
      Double coefConvert = area_ * 1.0 / 3.6 / Δt_;

      if (K > 0.0) {
        Qs[0] = Qs[0] + (Vs[0] * coefConvert * (1.0f - Math.pow(Eq_, (-Δt / K))));

        for (int i = 1; i < Vs.length; i++) {

          // NUEVO inicio ------------
          // Chequea la alta intensidad y corta duracón de lluvia que provocan picos
          // de caudales importantes.

          // NUEVO inicio ------------
          // Chequea la alta intensidad y corta duracón de lluvia que provocan picos
          // de caudales importantes.
          // NUEVO fin ------------

          if (Coefficient_to_increase_the_peaks_flow_ == 0.0) {
            Qs[i] =
                Qs[i - 1] * Math.pow(Eq_, (-Δt / K))
                    + Vs[i] * coefConvert * (1.f - Math.pow(Eq_, (-Δt / K)));
          } else {
            double deltaT = 0;
            peakV = 0.0;
            count = 0;
            for (int j = i; j > 0; j--) {
              peakV += Pconst_[j];
              if (count * Δt <= 24.0) { // Pconst_[j] == 0 ||
                count++;
              } else {
                break;
              }
            }
            if (peakV >= Limits_to_increase_the_peaks_flow_) {
              coefExp = Eq_ * Coefficient_to_increase_the_peaks_flow_; // Math.E();
              Qs[i] =
                  Qs[i - 1] * Math.pow(Eq_, (-Δt / K))
                      + Vs[i] * coefConvert * (1.f - Math.pow(coefExp, (-Δt / K)));
            } else
              Qs[i] =
                  Qs[i - 1] * Math.pow(Eq_, (-Δt / K))
                      + Vs[i] * coefConvert * (1.f - Math.pow(Eq_, (-Δt / K)));
          }
        }
      }
    } catch (Exception ex) {
      System.out.println("Simple linear Reservoir - Error: " + ex.getMessage());
    }

    return Qs;
  }

  public void Qom_InitializeProblemVariables() throws JMetalException {

    // problem variables
    P_ = new double[regFin_ - regIni_ + 1];
    Paccum_ = new double[regFin_ - regIni_ + 1];
    PEVT_ = new double[regFin_ - regIni_ + 1];
    EVT_ = new double[regFin_ - regIni_ + 1];
    Qc_ = new java.lang.Double[regFin_ - regIni_ + 1];
    Hc_ = new double[regFin_ - regIni_ + 1];
    R_ = new double[regFin_ - regIni_ + 1];
    S_ = new double[regFin_ - regIni_ + 1];
    D_ = new double[regFin_ - regIni_ + 1];
    Vi_ = new double[regFin_ - regIni_ + 1];
    Vp_ = new double[regFin_ - regIni_ + 1];
    Vg_ = new double[regFin_ - regIni_ + 1];
    RunoffR_ = new double[regFin_ - regIni_ + 1];
    Qi_ = new double[regFin_ - regIni_ + 1];
    Qp_ = new double[regFin_ - regIni_ + 1];
    Qg_ = new double[regFin_ - regIni_ + 1];
    Vis_ = new double[regFin_ - regIni_ + 1];
    Vps_ = new double[regFin_ - regIni_ + 1];
    Vgs_ = new double[regFin_ - regIni_ + 1];
    Qis_ = new double[regFin_ - regIni_ + 1];
    Qps_ = new double[regFin_ - regIni_ + 1];
    Qgs_ = new double[regFin_ - regIni_ + 1];
    Vo_ = new double[regFin_ - regIni_];
    Vc_ = new double[regFin_ - regIni_];
    double Plast = 0.0;
    for (int i = 0; i < Pconst_.length; i++) {
      P_[i] = Pconst_[i];
      Paccum_[i] = Plast + P_[i];
      PEVT_[i] = PEVTconst_[i];

      Plast = Paccum_[i];
    }
  }

  public double FunctionNSE(java.lang.Double[] O, java.lang.Double[] E) {
    // function Efficiency Nash-Sutcliffe
    // O[k] : k-th observed data value of streamflow
    // E[k] : k-th estimated or forecasted value of streamflow
    int k = O.length;
    double Om = 0.0;

    // mean of the observed data
    for (int i = 0; i < k; i++) {
      Om += O[i];
    }
    Om = Om / k;

    double SSRes = 0.0;
    double SSTot = 0.0;
    double NSE = 0.0;
    for (int i = 0; i < k; i++) {
      // Sum of Squares of Residuals, also called the residual sum of squares
      SSRes += Math.pow((E[i] - O[i]), 2.0);
      // Total Sum of Squares (proportional to the sample variance)
      SSTot += Math.pow((O[i] - Om), 2.0);
    }

    if (SSTot > 0) {
      NSE = 1.0 - SSRes / SSTot;
    } else NSE = 1e25;

    return NSE;
  }

  public double FunctionNSEO(java.lang.Double[] O, java.lang.Double[] E) {
    // function Efficiency Nash-Sutcliffe
    // O[k] : k-th observed data value of streamflow
    // E[k] : k-th forecasted or estimated or calculated of value of streamflow (discharge)

    double NSE = 0.0;
    double NSEO = 1.0;
    NSE = FunctionNSE(O, E);
    NSEO = 1.0 - NSE;

    return NSEO;
  }

  public double FunctionPBIAS(java.lang.Double[] O, java.lang.Double[] E) {
    // function percent bias (PBIAS)
    // measures the average tendency of the estimated (simulated) data to be larger
    // or smaller than their observed counterparts
    // O[k] : k-th observed data value of streamflow
    // F[k] : k-th forecasted or estimated or calculated of value of streamflow (discharge)
    double PBIAS = 0.0;
    double SOMF = 0.0;
    double SO = 0.0;

    // register numbers
    int k = O.length;
    for (int i = 0; i < k; i++) {
      // if(test_>1) {
      //    if (maxO_ > ) E[i] *= 0.85;
      // }

      SOMF += (E[i] - O[i]);
      SO += O[i];
    }

    if (SO > 0) {
      PBIAS = SOMF / SO * 100.0;
    } else PBIAS = 1e25;

    return Math.abs(PBIAS);
  }

  public double FunctionSE(java.lang.Double[] O, java.lang.Double[] F) {
    // function Square Error
    // O[k] : k-th observed data value of streamflow
    // F[k] : k-th forecasted or estimated or calculated of value of streamflow (discharge)
    int k = O.length;
    double SE = 0.0;
    for (int i = 0; i < k; i++) {
      SE += Math.pow((F[i] - O[i]), 2.0);
    }
    return SE;
  }

  public double FunctionSIE(java.lang.Double[] O, java.lang.Double[] F) {
    // function Square Inverse Error
    // O[k] : k-th observed data value of streamflow
    // F[k] : k-th forecasted value of streamflow
    int k = O.length;
    double SIE = 0.0;
    for (int i = 0; i < k; i++) {
      if (O[i] != 0.0 && F[i] != 0.0 && (1.0 / F[i] - 1.0 / O[i]) > 0.0) {
        SIE += Math.pow((1.0 / F[i] - 1.0 / O[i]), 2.0);
      }
    }
    return SIE;
  }

  public double FunctionModule(java.lang.Double[] O, java.lang.Double[] F) {
    // function Module
    // O[k] : k-th observed data value of streamflow
    // F[k] : k-th forecasted value of streamflow
    int k = O.length;
    double M = 0.0;
    for (int i = 0; i < k; i++) {
      M += Math.abs(F[i] - O[i]);
    }
    return M;
  }

  public double FunctionRSE(java.lang.Double[] O, java.lang.Double[] F) {
    // function Relative Square Error
    // O[k] : k-th observed data value of streamflow
    // F[k] : k-th forecasted value of streamflow
    double RSE = 0.0;
    // register numbers
    int k = O.length;
    for (int i = 0; i < k; i++) {
      RSE += Math.pow((F[i] - O[i]) / O[i], 2.0);
    }
    return RSE;
  }

  public double FunctionMSE(java.lang.Double[] O, java.lang.Double[] F) {
    // function Mean Error Square-root
    // O[k] : k-th observed data value of streamflow
    // F[k] : k-th forecasted value of streamflow
    double MSE = 0.0;
    // register numbers
    int k = O.length;
    for (int i = 0; i < k; i++) {
      MSE += Math.sqrt(Math.abs(F[i] - O[i]));
    }
    MSE = MSE / k;
    return MSE;
  }

  public double FunctionRMSE(java.lang.Double[] O, java.lang.Double[] F) {
    // function Mean Error Square-root
    // O[k] : k-th observed data value of streamflow
    // F[k] : k-th forecasted value of streamflow
    // RMSE-observations standard deviation ratio
    // is one of the commonly used error index statistics
    // (Chu and Shirmohammadi, 2004; Singh et al., 2004;
    // Vasquez-Amábile and Engel, 2005). Although it is commonly
    // accepted that the lower the RMSE the better the model performance,
    // only Singh et al. (2004) have published a guideline
    // to qualify what is considered a low RMSE based on the observations
    // standard deviation.

    double RMSE = 0.0;

    // register numbers
    int k = O.length;
    for (int i = 0; i < k; i++) {
      RMSE += Math.pow(O[i] - F[i], 2.0);
    }
    RMSE = Math.abs(Math.sqrt(RMSE));

    return RMSE;
  }

  public double FunctionRSR(java.lang.Double[] O, java.lang.Double[] F) {
    // function Mean Error Square-root
    // O[k] : k-th observed data value of streamflow
    // F[k] : k-th forecasted value of streamflow
    // Based on the recommendation by Singh et al. (2004),
    // a model evaluation statistic, named the
    // RMSE-observations standard deviation ratio (RSR),
    // was developed. RSR standardizes RMSE using the
    // observations standard deviation, and it combines
    // both an error index and the additional information
    // recommended by Legates and McCabe (1999).
    // RSR is calculated as the ratio of the RMSE and
    // standard deviation of measured data.

    double RSR = 0.0;
    double RMSE = FunctionRMSE(O, F);
    double STDEVo = 0.0;
    double mean = 0.0;
    // register numbers
    int k = O.length;
    for (int i = 0; i < k; i++) {
      mean += O[i];
    }
    mean /= O.length;

    for (int i = 0; i < k; i++) {
      STDEVo += Math.pow(O[i] - mean, 2.0);
    }

    STDEVo = Math.abs(Math.sqrt(STDEVo));

    RSR = RMSE / STDEVo;

    return RSR;
  }

  public double FunctionRQV(
      java.lang.Double[] Qo, java.lang.Double[] Qf, double[] Vo, double[] Vf) {
    // function Ratio Streamflow Volume
    // Qo[k] : k-th observed data value of streamflow
    // Qf[k] : k-th forecasted value of streamflow
    // Vo[k] : l-th observed data value of streamflow
    // Vf[k] : l-th forecasted value of streamflow
    double RQV = 0.0;
    // register numbers
    int k = Qo.length;
    int l = Vo.length;

    double Qom = 0;
    double RQoc = 0;
    double RQom = 0.0;
    double Vom = 0.0;
    double RVoc = 0;
    double RVom = 0.0;

    for (int i = 0; i < k; i++) {
      Qom += Qo[i];
    }
    Qom = Qom / k;

    for (int j = 0; j < l; j++) {
      Vom += Vo[j];
    }
    Vom = Vom / l;

    for (int i = 0; i < k; i++) {
      RQoc += Math.pow((Qf[i] - Qo[i]), 2.0);
      RQom += Math.pow((Qo[i] - Qom), 2.0);
    }

    for (int j = 0; j < l; j++) {
      RVoc += Math.pow((Vf[j] - Vo[j]), 2.0);
      RVom += Math.pow((Vo[j] - Vom), 2.0);
    }
    double a = RQoc / RQom;
    double b = RVoc / RVom;
    RQV = 1 - b / a; //

    return RQV;
  }

  public double FunctionCDR2(java.lang.Double[] O, java.lang.Double[] E) {
    // function Coefficient of Determination
    // denoted R2 and pronounced R squared
    // O[k] : k-th observed data value of streamflow
    // E[k] : k-th estimated or forecasted value of streamflow
    int N = O.length; // total No of data set
    double SO = 0.0; // Sum of observed data
    double SE = 0.0; // sum of values estimated
    double SOE = 0.0;
    double SO2 = 0.0;
    double SE2 = 0.0;
    double R = 0.0;
    double R2 = 0.0;

    // mean of the observed data
    for (int i = 0; i < N; i++) {
      SO += O[i];
      SE += E[i];
      SOE += O[i] * E[i];
      SO2 += O[i] * O[i];
      SE2 += E[i] * E[i];
    }
    // Pearson’s correlation coefficient
    R =
        (N * SOE - SO * SE)
            / (Math.sqrt((N * SO2 - Math.pow(SO, 2.0)) * (N * SE2 - Math.pow(SE, 2.0))));
    // coefficient of determination
    R2 = R * R;

    return R2;
  }

  public double Functions_Mahalanobis_Distance(java.lang.Double[] O, java.lang.Double[] E) {
    // Mahalanobis Distance With Variance for estimated value respect to estimated data

    int N = O.length;
    double minQo = 1e25;
    double minQe = 1e25;
    double maxQo = -1e25;
    double maxQe = -1e25;

    double sumO = 0.0; //
    double sumE = 0.0; //
    double sumOxO = 0.0; //
    double sumExE = 0.0; //
    double sumOxE = 0.0; //
    double meanO = 0.0; // means Y distance
    double meanE = 0.0; // means Z distance
    double S2O = 0.0; // variance Y distance
    double S2E = 0.0; // variance Z distance
    double SO = 0.0; // variance Y distance
    double SE = 0.0; // variance Z distance
    double r = 0.0; // Pearson correlation
    double MD = 0.0; // mahalanobis distance

    for (int n = 0; n < N; n++) {
      if (minQo >= O[n]) {
        minQo = O[n];
        minQe = E[n];
      }

      if (maxQo <= O[n]) {
        maxQo = O[n];
        maxQe = E[n];
      }

      sumO += O[n];
      sumE += E[n];
      sumOxO += O[n] * O[n];
      sumExE += E[n] * E[n];
      sumOxE += O[n] * E[n];

      meanO += O[n];
      meanE += E[n];
    }

    // mean of the observed data and values estimated
    meanO /= N;
    meanE /= N;

    // variance
    for (int k = 0; k < N; k++) {
      S2O += Math.pow((O[k] - meanO), 2.0);
      S2E += Math.pow((E[k] - meanE), 2.0);
    }

    S2O /= N;
    S2E /= N;

    SO = Math.sqrt(S2O);
    SE = Math.sqrt(S2E);

    // Pearson’s correlation coefficient
    r =
        (N * sumOxE - sumO * sumE)
            / (Math.sqrt((N * sumOxO - Math.pow(sumO, 2.0)) * (N * sumExE - Math.pow(sumE, 2.0))));

    // Mahalanobis distance
    MD =
        Math.pow((maxQo - minQo), 2.0) / S2O
            + Math.pow((maxQe - minQe), 2.0) / S2E
            - 2.0 * r * (maxQo - minQo) * (maxQe - minQe) / (SO * SE);
    MD = Math.sqrt(1 / (1 - Math.pow(r, 2.0)) * MD);

    return MD;
  }

  public double FunctionERQV(
      java.lang.Double[] Qo, java.lang.Double[] Qf, double[] Vo, double[] Vf) {
    // function enhanced Ratio Streamflow and Volume
    // Qo[k] : k-th observed data value of streamflow
    // Qf[k] : k-th forecasted value of streamflow
    // Vo[k] : l-th observed data value of streamflow
    // Vf[k] : l-th forecasted value of streamflow
    double RQV = 0.0;

    // register numbers
    int k = Qo.length;
    int l = Vo.length;

    double Qom = 0;
    double RQoc = 0;
    double RQom = 0.0;
    double Vom = 0.0;
    double RVoc = 0;
    double RVom = 0.0;

    for (int i = 0; i < k; i++) {
      Qom += Qo[i];
    }
    Qom = Qom / k;

    for (int j = 0; j < l; j++) {
      Vom += Vo[j];
    }
    Vom = Vom / l;

    for (int i = 0; i < k; i++) {
      RQoc += Math.pow((Qf[i] - Qo[i]), 2.0);
      RQom += Math.pow((Qo[i] - Qom), 2.0);
    }

    for (int j = 0; j < l; j++) {
      RVoc += Math.pow((Vf[j] - Vo[j]), 2.0);
      RVom += Math.pow((Vo[j] - Vom), 2.0);
    }
    // RQV = Math.sqrt(RVoc / Vo.length) * RQom / RQoc;

    double m = 1.0;
    int p = (int) (RVoc * m);
    while (p < 1) {
      m *= 10.0;
      p = (int) (RVoc * m);
    }
    RQV = Math.sqrt(RVoc * m / Vo.length) * RQom / RQoc * 100.0;

    return RQV;
  }

  public double FunctionMLSE(java.lang.Double[] O, java.lang.Double[] F) {
    // function mean logarithmic squared error (MLSE)
    // O[k] : k-th observed data value of streamflow
    // F[k] : k-th forecasted value of streamflow
    double MSLE = 0.0;
    int k = O.length;
    for (int i = 0; i < k; i++) {
      if (O[i] != 0.0 && F[i] != 0.0) {
        MSLE += Math.pow(Math.log(F[i]) - Math.log(O[i]), 2.0);
      }
    }
    MSLE = MSLE / k;

    return MSLE;
  }

  public double FunctionMSDE(java.lang.Double[] O, java.lang.Double[] F) {
    // function mean squared derivative error (MSDE)
    // O[k] : k-th observed data value of streamflow
    // F[k] : k-th forecasted value of streamflow
    double MSDE = 0.0;
    int k = O.length;
    for (int i = 1; i < k; i++) {
      MSDE += Math.pow((F[i] - F[i - 1]) - (O[i] - O[i - 1]), 2.0);
    }
    MSDE = MSDE / (k - 1);

    return MSDE;
  }

  public double FunctionM4E(java.lang.Double[] O, java.lang.Double[] F) {
    // mean fourth-power error (M4E) (de Vos and Rientjes, 2007, 2008)
    // O[k] : k-th observed data value of streamflow
    // F[k] : k-th forecasted value of streamflow
    double M4E = 0.0;
    int k = O.length;
    for (int i = 1; i < k; i++) {
      M4E += Math.pow((F[i] - O[i]), 4.0);
    }
    M4E = M4E / k;

    return M4E;
  }

  public double FunctionEMQQ(java.lang.Double[] O, java.lang.Double[] F) {
    double EMQQ = 0.0;
    double minQo = 1.0E208;
    double minQf = 1.0E208;
    double maxQo = -1.0E208;
    double maxQf = -1.0E208;

    for (int n = 0; n < Qo_.length; n++) {
      if (minQo >= Qo_[n]) {
        minQo = Qo_[n];
        minQf = Qc_[n];
      }

      if (maxQo <= Qo_[n]) {
        maxQo = Qo_[n];
        maxQf = Qc_[n];
      }
    }
    double EQmin = Math.sqrt(Math.pow((minQf - minQo), 2.0));
    double EQmax = Math.sqrt(Math.pow((maxQf - maxQo), 2.0));
    EMQQ = EQmin + EQmax;

    return EMQQ;
  }

  public double FunctionERQQ(java.lang.Double[] O, java.lang.Double[] F) {
    // sumatoria de los errores relativos de los caudales máximos y mínimos
    // acfectados por un coeficiente de importancia

    double ERQQ = 0.0;
    double minQo = 1.0E208;
    double minQf = 1.0E208;
    double maxQo = -1.0E208;
    double maxQf = -1.0E208;

    for (int n = 0; n < Qo_.length; n++) {
      if (minQo >= Qo_[n]) {
        minQo = Qo_[n];
        minQf = Qc_[n];
      }

      if (maxQo <= Qo_[n]) {
        maxQo = Qo_[n];
        maxQf = Qc_[n];
      }
    }
    // double EQmin = Math.sqrt(Math.pow((minQf - minQo), 2.0));
    // double EQmax = Math.sqrt(Math.pow((maxQf - maxQo), 2.0));

    double EQmin = Math.abs(minQf - minQo);
    double EQmax = Math.abs(maxQf - maxQo);
    double div = (maxQo * 0.95 + minQo * 0.05);
    if (div > 0) {
      ERQQ = (EQmax * 0.95 + EQmin * 0.05) / (maxQo * 0.95 + minQo * 0.05);
    } else {
      ERQQ = 1e25;
    }

    return ERQQ;
  }

  public void Function_Duration_Curve(java.lang.Double[] F) {

    java.lang.Double[] QeD = new java.lang.Double[F.length];
    System.arraycopy(F, 0, QeD, 0, F.length);
    Arrays.sort(QeD, Collections.reverseOrder());

    DurationQe_[0] = QeD[0];
    for (int j = 1; j < DurationSeg_; j++) {
      for (int i = 0; i < regFin_ - regIni_ + 1; i++) {
        double Duration = (double) i / (double) (regFin_ - regIni_ + 1) * 100.0;

        if (Duration > DurationT_[j]) {
          DurationQe_[j] = QeD[i - 1];
          break;
        }
      }
    }
    DurationQe_[DurationQo_.length - 1] = QeD[QeD.length - 1];
    return;

    /* Integral cuadales de duración observados y estimados
    double areaDC=0.0;

    for(int n=1;n< F.length;n++) {
      // areaDC += Math.abs(((O[n-1]-F[n-1])+(O[n]-F[n]))/2.0*(Duration_[n]-Duration_[n-1]));
    }
    return areaDC;

     */
  }

  public double[] VolumeOfHydrograph(java.lang.Double[] f) {
    // f: function in m3/s
    // for volume convertUnit to Hm3 = X[m3/s]*Δt[h]*3600[s/h]*1[Hm3/1000000] = Δt * 0.36 / 100.0
    // for flows convertUnit (m3/s) = 1.0
    double convertUnit = Δt_ * 0.36 / 100.0;
    double[] fm = new double[f.length - 1];
    for (int j = 1; j < f.length; j++) {
      fm[j - 1] = (f[j - 1] + f[j]) / 2.0 * convertUnit;
    }
    return fm;
  }

  public double SumVolumeOfHydrograph(double[] f) {
    double area = 0.0;
    for (int j = 0; j < f.length; j++) {
      area += f[j];
    }

    return area;
  }

  public void MaxAndMinStreamflows() {
    Qpo_ = -1.0e208;
    QPc_ = -1.0e208;
    Qmo_ = 1.0e208;
    Qmc_ = 1.0e208;
    QPic_ = -1.0e208;
    QPpc_ = -1.0e208;
    QPgc_ = -1.0e208;
    for (int j = 1; j < Qo_.length; j++) {
      // maximun
      if (Qpo_ < Qo_[j]) Qpo_ = Qo_[j];
      if (QPc_ < Qc_[j]) QPc_ = Qc_[j];
      if (QPic_ < Qis_[j]) QPic_ = Qis_[j];
      if (QPpc_ < Qps_[j]) QPpc_ = Qps_[j];
      if (QPgc_ < Qgs_[j]) QPgc_ = Qgs_[j];

      // minimun
      if (Qmo_ > Qo_[j]) Qmo_ = Qo_[j];
      if (Qmc_ > Qc_[j]) Qmc_ = Qc_[j];
    }
  }

  public void Qom_ReadProblems(String fileName) throws JMetalException {

    char ch;
    int lineN = 0;
    String line = "";
    String var1 = "";
    String var2 = "";
    String fileLoad = fileName;
    int i = 0, j = 0;
    selectedOF_ = new String[2];

    try {
      InputStream inputStream = getClass().getResourceAsStream("/" + fileName);

      if (inputStream == null) {
        inputStream = new FileInputStream("Qom.txt");
      }

      InputStreamReader isr = new InputStreamReader(inputStream);
      BufferedReader br = new BufferedReader(isr);

      // create a File instance
      //java.io.File file = new java.io.File(fileName);
      // create a Scanner for the file
      Scanner input = new Scanner(br);
      // Head read
      lineN++;
      line = input.nextLine();
      lineN++;
      line = input.nextLine();
      lineN++;
      line = input.nextLine();

      // file read
      line = input.nextLine();
      j = 0;
      for (i = line.length() - 1; i >= 0; i--) {
        ch = line.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      test_ = Integer.valueOf(line.substring(j));

      // number of decision Variables
      int w = 0;
      for (i = j - 2; i >= 0; i--) {
        ch = line.charAt(i);
        if (ch == ' ') {
          w = i + 1;
          break;
        }
      }
      setNumberOfVariables(Integer.valueOf(line.substring(w, j - 1)));

      // number of objective function
      int v = 0;
      for (i = w - 2; i >= 0; i--) {
        ch = line.charAt(i);
        if (ch == ' ') {
          v = i + 1;
          break;
        }
      }
      setNumberOfObjectives(Integer.valueOf(line.substring(v, w - 1)));

      // number of constraint function
      setNumberOfConstraints(8); //

      selectedOF_ = new String[getNumberOfObjectives()];
      int m = 0;
      for (j = getNumberOfObjectives() - 1; j > -1; j--) {
        for (i = v - 2; i >= 0; i--) {
          ch = line.charAt(i);
          if (ch == ' ') {
            m = i + 1;
            break;
          }
        }
        selectedOF_[j] = line.substring(m, v - 1);
        v = m;
        m = 0;
      }

      textOF_ = "";
      for (i = 0; i < getNumberOfObjectives(); i++) {
        textOF_ += "f" + (i + 1) + ": " + selectedOF_[i] + " ";
      }

      file_ = line.substring(0, v - 1);

      // close the file
      input.close();

      // for validation at VAR jMetal file
      /*
      if (test_ == -1) {
        // count amount lines
        String directory = "C:/jMetal_5.0_Home/Qom/QomStudy/pareto_fronts/";
        fileLoad = directory + "ReferenceFrontAndVariableStudy-jMetal.txt";
        //java.io.File file = new java.io.File(fileName);
        //file = new java.io.File(fileLoad);
        Scanner s = new Scanner(br);
        maxLine_ = -1; // head
        do {
          String linea = s.nextLine();
          maxLine_++;
        } while (s.hasNextLine());
        // close the file
        input.close();

        // read variables in VAR jMetal file
        fd_ = new double[maxLine_][getNumberOfObjectives()];
        var_ = new double[maxLine_][getNumberOfVariables()];
        //file = new java.io.File(fileLoad);
        s = new Scanner(br);
        String linea = s.nextLine(); // head
        i = 0;
        do {
          linea = s.nextLine();
          String[] campos = linea.split(" ");
          for (String campo : campos) {
            int ind = 0;
            for (j = getNumberOfObjectives();
                j < (getNumberOfObjectives() + getNumberOfVariables());
                j++) {
              var_[i][ind] = Double.valueOf(campos[j]);
              ind++;
            }
          }
          i++;
        } while (s.hasNextLine());
        // close the file
        input.close();
      }
      */
    } catch (Exception ex) {
      System.out.println(
          "ERROR in file: " + fileLoad + " - (" + ex.getMessage() + ") in line No:" + lineN);
      System.exit(1);
    }
  }

  public void Qom_ReadFileBasin(String fileName) throws JMetalException {

    int i, j = 0, lineN = 0;
    char ch;
    String txt = "";

    try {
      // create a File instance
      java.io.File file = new java.io.File(fileName);
      // create a Scanner for the file
      Scanner input = new Scanner(file);
      // Read data from file

      lineN++;
      txt = input.nextLine();
      lineN++;
      txt = input.nextLine();
      lineN++;
      txt = input.nextLine();

      lineN++;
      // description
      txt = input.nextLine();
      for (i = 0; i < txt.length(); i++) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      description_ = txt.substring(j);

      lineN++;
      // shape
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      // shapeName_ = txt.substring(j);
      txt = txt.substring(j);
      shape_ = Double.valueOf(txt);

      lineN++;
      // number of bar groups
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      area_ = Double.valueOf(txt);

      lineN++;
      // pervious
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      pervious_ = Double.valueOf(txt);

      lineN++;
      // concentration time
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      CT_ = Double.valueOf(txt);

      lineN++;
      // initial date-time
      txt = input.nextLine();
      txt = txt.substring(txt.length() - 24);
      dateIni_ = txt;
      dateIniPartial_ = dateIni_;

      lineN++;
      // final date-time
      txt = input.nextLine();
      txt = txt.substring(txt.length() - 24);
      dateFin_ = txt;
      dateFinPartial_ = dateFin_;

      lineN++;
      // time step
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Δt_ = Double.valueOf(txt);

      lineN++;
      // initial register
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      regIni_ = Integer.valueOf(txt);

      lineN++;
      // final register
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      regFin_ = Integer.valueOf(txt);

      lineN++;
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Superficial_Storage_to_start_considering_runoff_ = Double.valueOf(txt);
      if (Superficial_Storage_to_start_considering_runoff_ < 0.0)
        Superficial_Storage_to_start_considering_runoff_ = 0.0;
      if (Superficial_Storage_to_start_considering_runoff_ > 1.0)
        Superficial_Storage_to_start_considering_runoff_ = 1.0;

      lineN++;
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Limits_to_increase_the_peaks_flow_ = Double.valueOf(txt);

      lineN++;
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Coefficient_to_increase_the_peaks_flow_ = Double.valueOf(txt);
      if (Coefficient_to_increase_the_peaks_flow_ <= 1.0)
        Coefficient_to_increase_the_peaks_flow_ = 1.0;

      lineN++;
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      ratio_between_evaporation_and_soil_moisture_ = Integer.valueOf(txt);
      if (ratio_between_evaporation_and_soil_moisture_ != 0
          || ratio_between_evaporation_and_soil_moisture_ != 1)
        ratio_between_evaporation_and_soil_moisture_ = 1;

      lineN++;
      // percolation Is Base Flow ?
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Added_direct_infiltration_ = Double.valueOf(txt);

      lineN++;
      // percolation Is Base Flow ?
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Percolation_is_base_flow_ = Boolean.valueOf(txt);

      lineN++;
      // initial flow in impervious soil
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Qoi_ = Double.valueOf(txt);

      lineN++;
      // initial flow in pervious soil
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Qop_ = Double.valueOf(txt);

      lineN++;
      // initial downwater
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Qog_ = Double.valueOf(txt);

      lineN++;
      // superficial storage initial (reservoir)
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Ro_ = Double.valueOf(txt);

      lineN++;
      // soil storage initial (reservoir)
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      So_ = Double.valueOf(txt);

      lineN++;
      // superficial storage maximum (reservoir)
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Rmax_ = Double.valueOf(txt);

      lineN++;
      // soil storage maximum
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Smax_ = Double.valueOf(txt);

      lineN++;
      // ratio ercolation
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      VC_ = Double.valueOf(txt);

      lineN++;
      // basin coeficient retention impervious soil
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Ki_ = Double.valueOf(txt);

      lineN++;
      // basin coeficient retention pervious soil
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Kp_ = Double.valueOf(txt);

      lineN++;
      // basin coeficient retention internal soil
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Kg_ = Double.valueOf(txt);

      lineN++;
      // exponente del reservorio lineal simple
      txt = input.nextLine();
      for (i = txt.length() - 1; i >= 0; i--) {
        ch = txt.charAt(i);
        if (ch == ' ') {
          j = i + 1;
          break;
        }
      }
      txt = txt.substring(j);
      Eq_ = Double.valueOf(txt);

      // clse the file
      input.close();
      System.out.println("*.basin file has been read");

    } catch (Exception ex) {
      throw new RuntimeException(
          "ERROR in file: " + fileName + " - (" + ex.getMessage() + ") in line No:" + lineN);
    }
  }

  public void Qom_ReadFileRain(String fileName) throws JMetalException {

    int i, j = 0, lineN = 0;
    char ch;
    String txt = "";

    double Paccum_1 = 0.0;
    try {
      // create a File instance
      java.io.File file = new java.io.File(fileName);
      // create a Scanner for the file
      Scanner input = new Scanner(file);
      // Read data from file

      lineN++;
      txt = input.nextLine();
      lineN++;
      txt = input.nextLine();
      lineN++;
      txt = input.nextLine();

      for (i = 0; i < regIni_ - 1; i++) {
        lineN++;
        txt = input.nextLine();
      }

      Pconst_ = new double[regFin_ - regIni_ + 1];
      Paccum_ = new double[regFin_ - regIni_ + 1];
      for (i = 0; i < regFin_ - regIni_; i++) {
        lineN++;
        txt = input.nextLine();
        Pconst_[i] = Double.valueOf(txt);
        Paccum_[i] = Paccum_1 + Pconst_[i];
        Paccum_1 = Pconst_[i];
      }
      input.close();
      System.out.println("*.rain file has been read");
    } catch (Exception ex) {
      System.out.println(
          "ERROR in file: " + fileName + " - (" + ex.getMessage() + ") in line No:" + lineN);
      System.exit(1);
    }
  }

  public void Qom_ReadFilePEVT(String fileName) throws JMetalException {

    int i, j = 0, lineN = 0;
    char ch;
    String txt = "";

    try {
      // create a File instance
      java.io.File file = new java.io.File(fileName);
      // create a Scanner for the file
      Scanner input = new Scanner(file);
      // Read data from file

      lineN++;
      txt = input.nextLine();
      lineN++;
      txt = input.nextLine();
      lineN++;
      txt = input.nextLine();

      for (i = 0; i < regIni_ - 1; i++) {
        lineN++;
        txt = input.nextLine();
      }

      PEVTconst_ = new double[regFin_ - regIni_ + 1];
      for (i = 0; i < regFin_ - regIni_ + 1; i++) {
        lineN++;
        txt = input.nextLine();
        PEVTconst_[i] = Double.valueOf(txt);
      }
      input.close();
      System.out.println("*.pevt file has been read");

    } catch (Exception ex) {
      System.out.println(
          "ERROR in file: " + fileName + " - (" + ex.getMessage() + ") in line No:" + lineN);
      System.exit(1);
    }
  }

  public void Qom_ReadFileQo(String fileName) throws JMetalException {

    maxO_ = 0; // maximum observed steamflow
    int i, j = 0, lineN = 0;
    char ch;
    String txt = "";

    try {
      // create a File instance
      File file = new File(fileName);
      // create a Scanner for the file
      Scanner input = new Scanner(file);
      // Read data from file

      lineN++;
      txt = input.nextLine();
      lineN++;
      txt = input.nextLine();
      lineN++;
      txt = input.nextLine();

      for (i = 0; i < regIni_ - 1; i++) {
        lineN++;
        txt = input.nextLine();
      }

      Qo_ = new java.lang.Double[regFin_ - regIni_ + 1];

      QoD_ = new Double[regFin_ - regIni_ + 1];
      double Duration_;

      for (i = 0; i < regFin_ - regIni_ + 1; i++) {
        lineN++;
        txt = input.nextLine();
        Qo_[i] = Double.valueOf(txt);
        if (maxO_ < Qo_[i]) maxO_ = Qo_[i];

        QoD_[i] = Qo_[i];
      }
      Arrays.sort(QoD_, Collections.reverseOrder());

      // array para curva de duracion caudales observados
      DurationQo_[0] = QoD_[0];
      for (j = 1; j < DurationSeg_; j++) {
        for (i = 0; i < regFin_ - regIni_ + 1; i++) {
          Duration_ = (double) i / (double) (regFin_ - regIni_ + 1) * 100.0;

          if (Duration_ > DurationT_[j]) {
            DurationQo_[j] = QoD_[i - 1];
            break;
          }
        }
      }

      DurationQo_[DurationSeg_ - 1] = QoD_[QoD_.length - 1];

      // first flow peak
      peakQP_[0] = QoD_[0];
      // second flow peak
      peakQP_[1] = QoD_[1];

      System.out.println("*.Qo file has been read");
      // clse the file
      input.close();

      // Determina la lluvia de máxima intensidad,
      // el caudal pico local y el volumen de de lluvia asociado.
      // Sirve para controlar y tomar decisiones en lluvias
      // de alta intensidad y corta duración que provocan picos
      // de caudales importantes.
      double auxVol = 0;
      double auxP = 0;
      double auxQ = 0;
      double auxT = 0;

      for (i = 0; i < Pconst_.length; i++) {
        if (Pconst_[i] == 0 && auxT > 48) {
          auxP = 0;
          auxVol = 0;
          auxQ = 0;
          auxT = 0;
        }
        auxT += Δt_;
        auxVol += Pconst_[i];

        if (peakP_ < Pconst_[i]) {
          peakP_ = Pconst_[i];
          peakPindx_ = i;
          peakPV_ = auxVol;
          peakPT_ = auxT;
          auxVol = 0;
          auxT = 0;

          peakPQ_ = 0.0;
          for (j = i; j < Qo_.length; j++) {
            if (peakPQ_ < Qo_[j]) {
              peakPQ_ = Qo_[j];
              peakPindxQ_ = j;
            }
            if (peakPQ_ > Qo_[j]) {
              break;
            }
          }
        }
      }

      // determina el caudal pico observado
      // y elvolumen de lluvia que lo ha generdo
      // peakQP_ = new double[2];
      peakQindxP_ = 0;

      for (i = 0; i < Qo_.length; i++) {
        if (peakQP_[0] < Qo_[i]) {
          peakQindxP_ = i;
        }
      }

      peakQT_ = 0;
      peakQV_ = 0;
      for (i = peakQindxP_; i > 0; i--) {
        peakQT_ += Δt_;
        peakQV_ += Pconst_[i];
        if (Pconst_[i] == 0 && peakQT_ > 48) {
          break;
        }
      }

    } catch (Exception ex) {
      System.out.println(
          "ERROR in file: " + fileName + " - (" + ex.getMessage() + ") in line No:" + lineN);
      System.exit(1);
    }
  }

  public void Qom_ReadFileHo(String fileName) throws JMetalException {

    int i, j = 0, lineN = 0;
    char ch;
    String txt = "";

    try {
      // create a File instance
      java.io.File file = new java.io.File(fileName);
      // create a Scanner for the file
      Scanner input = new Scanner(file);
      // Read data from file

      lineN++;
      txt = input.nextLine();
      lineN++;
      txt = input.nextLine();
      lineN++;
      txt = input.nextLine();

      for (i = 0; i < regIni_ - 1; i++) {
        lineN++;
        txt = input.nextLine();
      }

      Ho_ = new double[regFin_ - regIni_ + 1];
      for (i = 0; i < regFin_ - regIni_ + 1; i++) {
        lineN++;
        txt = input.nextLine();
        Ho_[i] = Double.valueOf(txt);
      }
      System.out.println("*.Ho file has been read");

      // clse the file
      input.close();
    } catch (Exception ex) {
      System.out.println(
          "ERROR in file: " + fileName + " - (" + ex.getMessage() + ") in line No:" + lineN);
      System.exit(1);
    }
  }

  public void Qom_ReadLimitsVariable(String fileName) throws JMetalException {
    char ch;
    String line = "";
    String var1 = "";
    String var2 = "";
    int i, j = 0;
    int lineN = 0;

    java.util.List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    java.util.List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    try {
      // create a File instance
      java.io.File file = new java.io.File(fileName);
      // create a Scanner for the file
      Scanner input = new Scanner(file);
      // Read data from file

      lineN++;
      line = input.nextLine();
      lineN++;
      line = input.nextLine();
      lineN++;
      line = input.nextLine();

      for (int k = 0; k < getNumberOfVariables(); k++) {
        lineN++;
        line = input.nextLine();
        j = 0;
        for (i = line.length() - 1; i >= 0; i--) {
          ch = line.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        var2 = line.substring(j);
        upperLimit.add(Double.valueOf(var2));

        int m = 0;
        for (i = j - 2; i >= 0; i--) {
          ch = line.charAt(i);
          if (ch == ' ') {
            m = i + 1;
            break;
          }
        }
        var1 = line.substring(m, j);
        lowerLimit.add(Double.valueOf(var1));
      }
      setVariableBounds(lowerLimit, upperLimit);
      System.out.println("*.var file has been read");
      // clse the fil e
      input.close();
    } catch (Exception ex) {
      System.out.println(
          "ERROR in file: " + fileName + " - (" + ex.getMessage() + ") in line No:" + lineN);
      System.exit(1);
    }
  }

  public void Qom_PrintArchSolutions() throws JMetalException {
    // print solution hydrograph
    try {

      MaxAndMinStreamflows();

      PrintStream ps = new PrintStream("Optimization.txt");
      ps.printf("Objective Functions");
      ps.println();
      ps.printf("Eval: " + numberOfEval_);
      ps.println();
      ps.printf(textOF_);
      ps.println();
      ps.printf(optOF_);
      ps.println();
      ps.println();
      ps.printf("Rmax (mm): %12.3f", optRmax_);
      ps.println();
      ps.printf("Smax (mm): %12.3f", optSmax_);
      ps.println();
      ps.printf("Pc (mm/h):   %12.3f", optVC_);
      ps.println();
      ps.printf("Ki    (h):   %12.3f", optKi_);
      ps.println();
      ps.printf("Kp    (h):   %12.3f", optKp_);
      ps.println();
      ps.printf("Kg    (h):   %12.3f", optKg_);
      ps.println();
      ps.printf("Eq      :   %12.3f", optEq_);
      ps.close();

      ps = new PrintStream("Hydrograph.txt"); // NEW 21/10/2016
      // yyyy/mm/dd hh:mm:ss a.m.
      int year, month, date, hour, minute, second;
      String tt;
      year = Integer.valueOf(dateIni_.substring(0, 4));
      month = Integer.valueOf(dateIni_.substring(6, 7));
      date = Integer.valueOf(dateIni_.substring(9, 10));
      hour = Integer.valueOf(dateIni_.substring(11, 13));
      minute = Integer.valueOf(dateIni_.substring(15, 16));
      second = Integer.valueOf(dateIni_.substring(17, 19));
      tt = dateIni_.substring(20, 24);
      if (tt != "") {
        if (hour == 12 && (tt.equals("a.m."))) {
          hour = 0;
        } else if (tt.equals("p.m.")) {
          hour += 12;
        }
      }

      Calendar cal = Calendar.getInstance();
      cal.set(year, month - 1, date, hour, minute, second);

      int nDate, nHour, nMinute;
      nDate = 0;
      if ((Δt_ % 24.0) == 0) nDate = (int) (Δt_ / 24.0);
      nHour = 0;
      if ((Δt_ * 60.0 % 60.0) == 0 && Δt_ < 24.0 && Δt_ >= 1.0) nHour = (int) Δt_;
      nMinute = 0;
      if (Δt_ < 1.0) nMinute = (int) (60.0 * Δt_);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

      // NEW 2016-10-21
      cal.add(Calendar.DATE, nDate);
      cal.add(Calendar.MINUTE, nMinute);
      for (int i = 0; i < regIni_; i++) {
        if (nMinute != 0) {
          cal.add(Calendar.MINUTE, nMinute * i - nMinute);
        } else if (nHour != 0) {
          cal.add(Calendar.HOUR, nHour * i);
        }
      } // Next i
      dateIniPartial_ = sdf.format(cal.getTime());

      // ps.printf("Date Time(h)  Qo (m3/s)   Qc (m3/s)"); ps.println();
      for (int i = 0; i < Qo_.length; i++) {

        ps.printf(
            sdf.format(cal.getTime()) + "%12.3f %10.3f %10.3f %10.3f",
            i * Δt_,
            Qo_[i],
            Qc_[i],
            Qgs_[i]);
        ps.println();

        dateFinPartial_ = sdf.format(cal.getTime());

        cal.add(Calendar.DATE, nDate);
        cal.add(Calendar.HOUR, nHour);
        cal.add(Calendar.MINUTE, nMinute);
      } // Next i

      ps.close();

      double RE = 0.0;
      double RE2 = 0.0;
      ps = new PrintStream("Balance.txt");
      ps.printf(file_);
      ps.println();
      ps.printf(description_);
      ps.println();
      ps.printf("Qom-Clark");
      ps.println();
      double stepT = (double) ((int) (this.Δt_ * 100.0) / 100.0);
      // ps.printf("Period for " + this.dateIni_ + " to " +  this.dateFin_  + " step " + stepT+
      // "h"); ps.println();
      ps.printf(
          "Period for "
              + this.dateIniPartial_
              + " to "
              + this.dateFinPartial_
              + " step "
              + stepT
              + "h");
      ps.println();

      ps.printf("--------------------------------------------------");
      ps.println();
      ps.printf("Mass Balance of Qom in mm (Hm3)");
      ps.println();
      ps.printf("Rain:                         %8.2f(%8.3f)", Input_, Input_ / 1000.0 * area_);
      ps.println();
      ps.printf("Initial Superficial Storage:  %8.2f(%8.3f)", Ro_, Ro_ / 1000.0 * area_);
      ps.println();
      ps.printf("Initial Soil Storage:         %8.2f(%8.3f)", So_, So_ / 1000.0 * area_);
      ps.println();
      ps.printf(
          "EVT:                                %8.2f(%8.3f)",
          Evapotraspiration_, Evapotraspiration_ / 1000.0 * area_);
      ps.println();
      ps.printf(
          "Superficial Storage:                %8.2f(%8.3f)",
          SuperficialStore_, SuperficialStore_ / 1000.0 * area_);
      ps.println();
      ps.printf(
          "Soil Storage:                       %8.2f(%8.3f)",
          SoilStore_, SoilStore_ / 1000.0 * area_);
      ps.println();
      double TotalFlow = 0.0;
      if (Percolation_is_base_flow_) {
        // TotalFlow =OutputSuperficial_+StoreOrOutput_Deep_;
        TotalFlow = OutputSuperficial_; // +StoreOrOutput_Deep_;
        ps.printf(
            "Groundwater Runoff:                 %8.2f(%8.3f)",
            StoreOrOutput_Deep_, StoreOrOutput_Deep_ / 1000.0 * area_);
        ps.println();
      } else {
        TotalFlow = OutputSuperficial_;
        ps.printf(
            "Deep Storage:                       %8.2f(%8.3f)",
            StoreOrOutput_Deep_, StoreOrOutput_Deep_ / 1000.0 * area_);
        ps.println();
      }
      ps.printf(
          "Superficial Runoff:                 %8.2f(%8.3f)",
          OutputSuperficial_, OutputSuperficial_ / 1000.0 * area_);
      ps.println();
      double totalVomumeEstimated =
          Evapotraspiration_
              + SuperficialStore_
              + SoilStore_
              + StoreOrOutput_Deep_
              + OutputSuperficial_;
      ps.printf(
          "Total volume estimated:       %8.2f(%8.3f)",
          totalVomumeEstimated, totalVomumeEstimated / 1000.0 * area_);
      ps.println();
      ps.printf(
          "Balance AE:                   %8.2f(%8.3f)",
          MassBalance_, MassBalance_ / 1000.0 * area_);
      ps.println();
      ps.printf("Relative Error (RE):          %8.2f", MassBalanceError_);
      ps.println();
      ps.printf("Total Volume Runoff Hm3 (lts)");
      ps.println();
      ps.printf("  in pervious superficial     %8.4f(%8.3fx10^6)", sumVpxArea_, sumVpxArea_ * 1000);
      ps.println();
      ps.printf("  in impervious superficial   %8.4f(%8.3fx10^6)", sumVixArea_, sumVixArea_ * 1000);
      ps.println();
      ps.printf("  off growndwater             %8.4f(%8.3fx10^6)", sumVgxArea_, sumVgxArea_ * 1000);
      ps.println();
      ps.printf("Corff. Runoff                 %8.3f", OutputSuperficial_ / Input_);
      ps.println();
      ps.printf("--------------------------------------------------");
      ps.println();
      ps.printf("Comparison of water volumes mm (Hm3)");
      ps.println();
      ps.printf(" Observed hydrograph mm (Hm3):%8.2f(%8.3f)", VHo_ * 1000.0 / area_, VHo_);
      ps.println();
      ps.printf(" Estimated for Clark mm (Hm3):%8.2f(%8.3f)", VHc_ * 1000.0 / area_, VHc_);
      ps.println();
      if (VHo_ > 0) {
        // error= (1.0-Math.min(VHc_,VHo_)/Math.max(VHo_,VHc_))*100.0;
        RE = (VHc_ - VHo_) / VHo_ * 100.0;
        ps.printf("  Relative Error (RE):        %8.2f", RE);
        ps.println();
      } else {
        ps.printf("  Relative Error (RE):");
        ps.println();
      }
      RE = (-1 + (TotalFlow / 1000.0 * area_) / VHo_) * 100.0; // +DepFlow
      ps.printf(
          " Estimated for Qom mm (Hm3)   %8.2f(%8.3f)", TotalFlow, TotalFlow / 1000.0 * area_);
      ps.println();
      ps.printf("   Relative Error (RE):       %8.2f", RE);
      ps.println();
      ps.printf("--------------------------------------------------");
      ps.println();
      // RE= (Qmc_ - Qmo_) / Qmo_ * 100.0;
      // ps.printf("Relative error (RE)%%:            %8.2f", RE); ps.println();
      ps.printf("Hydrograph of Clark");
      ps.println();
      ps.printf("Estimated (m3/s):             %8.3f", QPc_);
      ps.println();
      ps.printf("     Superfitial impervious:        %8.3f", QPic_);
      ps.println();
      ps.printf("     Superfitial pervious:          %8.3f", QPpc_);
      ps.println();
      ps.printf("     Groundwater:                   %8.3f", QPgc_);
      ps.println();
      ps.printf("Flow peak of Hydrograph");
      ps.println();
      ps.printf("Observed maximum (m3/s):      %8.2f", peakQP_[0]);
      ps.println(); // Qpo_
      ps.printf("Observed second  (m3/s):      %8.2f", peakQP_[1]);
      ps.println();
      // RE= (Qmc_ - Qmo_) / Qmo_ * 100.0;
      if (Qpo_ > 0) {
        // RE= (1.0-Math.min(Qpo_,QPc_)/Math.max(Qpo_,QPc_))*100.0;
        RE = (QPc_ - peakQP_[0]) / peakQP_[0] * 100.0;
        ps.printf("Relative error max peak (RE): %8.2f", RE);
        ps.println();
        RE2 = (QPc_ - peakQP_[1]) / peakQP_[1] * 100.0;
        ps.printf("RE second max peak:           %8.2f", RE2);
        ps.println();
      } else {
        ps.printf("Relative error (RE):");
        ps.println();
      }
      ps.printf("Minimum flowrates");
      ps.println();
      ps.printf("Observed (m3/s):              %8.2f", Qmo_);
      ps.println();
      ps.printf("Estimated (m3/s):             %8.2f", Qmc_);
      ps.println();
      ps.printf("-----------------------------------------");
      ps.println();
      ps.printf("Precipitation maximum intensity");
      ps.println();
      ps.printf("rain max. intensity:            %8.2f", peakP_);
      ps.println();
      ps.printf("index for rain max. intensity:    %d", peakPindx_);
      ps.println();
      ps.printf("Accumulated rain (mm)           %8.2f", peakPV_);
      ps.println();
      ps.printf("Accumulated time (h)            %8.2f", peakPT_);
      ps.println();
      ps.printf("local peak Q (m3/s)             %8.2f", peakPQ_);
      ps.println();
      ps.printf("index local peak Q (m3/s):        %d", peakPindxQ_);
      ps.println();
      ps.printf("Precipitation for Q with max. peak");
      ps.println();
      ps.printf("index for max. peak:              %d", peakQindxP_);
      ps.println();
      ps.printf("Accum. rain for Q max. peak (mm)%8.2f", peakQV_);
      ps.println();
      ps.printf("Accum. time for Q max. peak (h) %8.2f", peakQT_);
      ps.println();
      ps.printf("-----------------------------------------");
      ps.println();
      double sumQo = 0;
      for (int contador = 0; contador < Qo_.length; contador++) {
        sumQo += Qo_[contador];
      }
      if (sumQo > 0.0) {
        ps.printf("Calibration indicators");
        ps.println();
        double R2 = FunctionCDR2(Qo_, Qc_);
        double R = Math.sqrt(R2);
        ps.printf("Pearson’s correlation coefficient: (R): %8.4f", R);
        ps.println();
        ps.printf("Determination coefficient (R2):         %8.4f", R2);
        ps.println();
        double NSE = FunctionNSE(Qo_, Qc_);
        ps.printf("Nash-Sutcliffe Efficiency (NSE):        %8.4f", NSE);
        ps.println();
        double PBIAS = FunctionPBIAS(Qo_, Qc_);
        ps.printf("Percent bias (PBIAS):                   %8.4f", PBIAS);
        ps.println();
        // NSE for Duration Curve
        double DC = FunctionPBIAS(DurationQo_, DurationQe_);
        ps.printf("Duration Curve: RE bwtween Qo and Qe:   %8.4f", DC);
        ps.println();
        double RSR = FunctionRSR(Qo_, Qc_);
        ps.printf("Observations standard deviation ratio");
        ps.println();
        ps.printf("for root mean square error (RSR):       %8.4f", RSR);
        double ERQQ = FunctionERQQ(Qo_, Qc_);
        ps.println();
        ps.printf("Relative weighted error between the sum");
        ps.println();
        ps.printf("of the maximum and minimum flows (ERQQ):%8.4f", ERQQ);
      } else {
        ps.printf("Pearson’s correlation coefficient (R):");
        ps.println();
        ps.printf("Determination coefficient (R2):");
        ps.println();
        ps.printf("Nash-Sutcliffe Efficiency (NSE):");
        ps.println();
        ps.printf("Percent bias (PBIAS):");
        ps.println();
        ps.printf("Observations standard deviation ratio");
        ps.println();
        ps.printf("for root mean square error (RSR):");
        ps.println();
      }
      ps.close();

      // volume for hydrograph observaded and hudrograph calculated
      ps = new PrintStream("Volumes.txt");
      // ps.printf("Time(h)  Rain (mm)  Losses(mm) Excess(mm)  R(mm)  S(mm)   D(mm)"); ps.println();
      for (int i = 0; i < Pconst_.length; i++) {
        ps.printf(
            "%10.3f %8.2f %8.2f %8.2f %8.2f %8.2f %8.3f %8.3f",
            i * Δt_, Pconst_[i], (Pconst_[i] - P_[i]), P_[i], R_[i], S_[i], D_[i], EVT_[i]);
        ps.println();
      } // Next i
      ps.close();

      // volume for hydrograph observaded and hudrograph calculated
      ps = new PrintStream("Volumes Total of Hydrograph.txt");
      // ps.printf("Time(h)  Vo (m3)   Vc (m3)"); ps.println();
      for (int i = 0; i < Vo_.length; i++) {
        ps.format("%10.3f %10.6f %10.6f", i * Δt_, Vo_[i], Vc_[i]);
        ps.println();
      } // Next i
      ps.close();

      System.out.println("Solution files saved");
      Toolkit.getDefaultToolkit().beep();

    } // i
    catch (Exception ex) {
      System.out.println("ERROR in save file Hydrograph.txt: (" + ex.getMessage() + ")");
    }
  }

  public void Qom_save_OF_validation() throws JMetalException {
    // print solution hydrograph
    try {
      // PrintStream ps = new PrintStream("Optimization.txt");
      PrintStream ps = new PrintStream(file_);
      // f1 f2
      for (int i = 0; i < maxLine_; i++) {
        ps.format(fd_[i][0] + " " + fd_[i][1]);
        ps.println();
      } // Next i
      ps.close();
      System.out.println("saved function objective derived");
      Toolkit.getDefaultToolkit().beep();
    } catch (Exception ex) {
      System.out.println("ERROR in save file SolutionsSort.txt: (" + ex.getMessage() + ")");
    }
  }
}
