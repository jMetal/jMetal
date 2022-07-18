package org.uma.jmetal.problem.multiobjective.cre;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem CRE32. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class CRE32 extends AbstractDoubleProblem {

  /** Constructor */
  public CRE32() {
    setNumberOfObjectives(3);
    setNumberOfConstraints(9);
    setName("CRE32");

    var lowerLimit = List.of(150.0, 20.0, 13.0, 10.0, 14.0, 0.63);
    @NotNull List<Double> upperLimit = List.of(274.32, 32.31, 25.0, 11.71, 18.0, 0.75);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x_L = solution.variables().get(0);
    double x_B = solution.variables().get(1);
    double x_D = solution.variables().get(2);
    double x_T = solution.variables().get(3);
    double x_Vk = solution.variables().get(4);
    double x_CB = solution.variables().get(5);

    var displacement = 1.025 * x_L * x_B * x_T * x_CB;
    var V = 0.5144 * x_Vk;
    var g = 9.8065;
    var Fn = V / Math.pow(g * x_L, 0.5);
    var a = (4977.06 * x_CB * x_CB) - (8105.61 * x_CB) + 4456.51;
    var b = (-10847.2 * x_CB * x_CB) + (12817.0 * x_CB) - 6960.32;

    var power = (Math.pow(displacement, 2.0/3.0) * Math.pow(x_Vk, 3.0)) / (a + (b * Fn));
    var outfit_weight = 1.0 * Math.pow(x_L , 0.8) * Math.pow(x_B , 0.6) * Math.pow(x_D, 0.3) * Math.pow(x_CB, 0.1);
    var steel_weight = 0.034 * Math.pow(x_L ,1.7) * Math.pow(x_B ,0.7) * Math.pow(x_D ,0.4) * Math.pow(x_CB ,0.5);
    var machinery_weight = 0.17 * Math.pow(power, 0.9);
    var light_ship_weight = steel_weight + outfit_weight + machinery_weight;

    var ship_cost = 1.3 * ((2000.0 * Math.pow(steel_weight, 0.85))  + (3500.0 * outfit_weight) + (2400.0 * Math.pow(power, 0.8)));
    var capital_costs = 0.2 * ship_cost;

    var DWT = displacement - light_ship_weight;

    var running_costs = 40000.0 * Math.pow(DWT, 0.3);

    var round_trip_miles = 5000.0;
    var sea_days = (round_trip_miles / 24.0) * x_Vk;
    var handling_rate = 8000.0;

    var daily_consumption = ((0.19 * power * 24.0) / 1000.0) + 0.2;
    var fuel_price = 100.0;
    var fuel_cost = 1.05 * daily_consumption * sea_days * fuel_price;
    var port_cost = 6.3 * Math.pow(DWT, 0.8);

    var fuel_carried = daily_consumption * (sea_days + 5.0);
    var miscellaneous_DWT = 2.0 * Math.pow(DWT, 0.5);

    var cargo_DWT = DWT - fuel_carried - miscellaneous_DWT;
    var port_days = 2.0 * ((cargo_DWT / handling_rate) + 0.5);
    var RTPA = 350.0 / (sea_days + port_days);

    var voyage_costs = (fuel_cost + port_cost) * RTPA;
    var annual_costs = capital_costs + running_costs + voyage_costs;
    var annual_cargo = cargo_DWT * RTPA;

    solution.objectives()[0] = annual_costs / annual_cargo;
    solution.objectives()[1] = light_ship_weight;
    solution.objectives()[2] = -annual_cargo ;

    evaluateConstraints(solution, DWT, Fn);

    return solution;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution, double DWT, double Fn) {
    var constraint = new double[this.getNumberOfConstraints()];

    double x_L = solution.variables().get(0);
    double x_B = solution.variables().get(1);
    double x_D = solution.variables().get(2);
    double x_T = solution.variables().get(3);
    double x_Vk = solution.variables().get(4);
    double x_CB = solution.variables().get(5);

    constraint[0] = (x_L / x_B) - 6.0;
    constraint[1] = -(x_L / x_D) + 15.0;
    constraint[2] =  -(x_L / x_T) + 19.0;
    constraint[3] = 0.45 * Math.pow(DWT, 0.31) - x_T;
    constraint[4] = 0.7 * x_D + 0.7 - x_T;
    constraint[5] = 50000.0 - DWT;
    constraint[6] = DWT - 3000.0;
    constraint[7] = 0.32 - Fn;

    var KB = 0.53 * x_T;
    var BMT = ((0.085 * x_CB - 0.002) * x_B * x_B) / (x_T * x_CB);
    var KG = 1.0 + 0.52 * x_D;
    constraint[8] = (KB + BMT - KG) - (0.07 * x_B);

    for (var i = 0; i < getNumberOfConstraints(); i++) {
      if (constraint[i] < 0.0) {
        constraint[i] = -constraint[i];
      } else {
        constraint[i] = 0;
      }
    }

    for (var i = 0; i < getNumberOfConstraints(); i++) {
      solution.constraints()[i] = constraint[i];
    }
  }
}
