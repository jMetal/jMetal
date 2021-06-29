package org.uma.jmetal.algorithm.multiobjective.mosa.cooling;

/**
 * Interface representing cooling schemes for simulated annealing algorithms.
 *
 * The {@link #updateTemperature(double, int)} method takes the current temperature and update ii somehow by
 * possibly using as a parameter the current iteration number of the main loop of the algorithm.
 */

public interface CoolingScheme {
  double updateTemperature(double temperature, int iteration) ;
}
