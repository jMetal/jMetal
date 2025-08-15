package org.uma.jmetal.util.observer.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.knowm.xchart.XYChart;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;
import org.uma.jmetal.util.plot.FrontScatterPlot;

/**
 * An observer that displays the current Pareto front during the execution of a multi-objective
 * optimization algorithm. It visualizes the solutions in a 2D scatter plot, showing how the front
 * evolves over time.
 *
 * <p>This observer is typically used to monitor the progress of multi-objective optimization
 * algorithms by plotting the objective values of the current population. It can also display
 * reference fronts for comparison and filter out dominated solutions if needed.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * FrontPlotObserver<Solution<?>> observer = new FrontPlotObserver<>(
 *     "Front Plot", "Objective 1", "Objective 2", "Population", 100);
 * algorithm.getObservable().register(observer);
 * }</pre>
 *
 * @param <S> Type of the solutions being observed, must extend {@code Solution<?>}
 * @author Antonio J. Nebro
 */
public class FrontPlotObserver<S extends Solution<?>> implements Observer<Map<String, Object>> {
  private final FrontScatterPlot chart;
  private final int plotUpdateFrequency;
  private final String plotTitle;
  private boolean filterDominatedSolutions;

  /**
   * Creates a new FrontPlotObserver instance.
   *
   * @param title The title of the plot
   * @param xAxisTitle Title for the X-axis (typically first objective name)
   * @param yAxisTitle Title for the Y-axis (typically second objective name)
   * @param legend Legend text for the population points
   * @param plotUpdateFrequency How often to update the plot (in number of evaluations)
   * @throws IllegalArgumentException if plotUpdateFrequency is less than 1
   */
  public FrontPlotObserver(
      String title, String xAxisTitle, String yAxisTitle, String legend, int plotUpdateFrequency) {
    if (plotUpdateFrequency < 1) {
      throw new IllegalArgumentException("Update frequency must be at least 1");
    }
    chart = new FrontScatterPlot(title, xAxisTitle, yAxisTitle, legend);
    this.plotUpdateFrequency = plotUpdateFrequency;
    this.plotTitle = title;
    this.filterDominatedSolutions = false;
  }

  /**
   * Adds a reference front to the plot for comparison with the current population. The reference
   * front is typically a known Pareto-optimal front or another reference set of solutions.
   *
   * @param front 2D array where each row represents a solution in the reference front
   * @param frontName The name to display in the legend for this front
   * @throws IllegalArgumentException if front is null or empty
   */
  public void setFront(double[][] front, String frontName) {
    if (front == null || front.length == 0) {
      throw new IllegalArgumentException("Front cannot be null or empty");
    }
    chart.setFront(front, frontName);
  }

  /**
   * Adds a single point to the plot, which can be used to mark specific solutions or reference
   * points.
   *
   * @param x X-coordinate of the point (first objective value)
   * @param y Y-coordinate of the point (second objective value)
   * @param pointName The name to display for this point in the legend
   */
  public void setPoint(double x, double y, String pointName) {
    chart.addPoint(x, y, pointName);
  }

  /**
   * Enables or disables filtering of dominated solutions. When enabled, only non-dominated
   * solutions from the current population will be displayed.
   *
   * @param filterDominatedSolutions If true, only non-dominated solutions will be plotted
   */
  public void filterDominatedSolutions(boolean filterDominatedSolutions) {
    this.filterDominatedSolutions = filterDominatedSolutions;
  }

  /**
   * Updates the plot with the current population from the optimization algorithm. This method is
   * called automatically by the observed algorithm.
   *
   * @param observable The observable object (typically the optimization algorithm)
   * @param data Map containing the current state with the following expected keys: - "EVALUATIONS":
   *     Current number of evaluations (Integer) - "POPULATION": Current population of solutions
   *     (List<S>)
   * @throws ClassCastException if the data map contains values of unexpected types
   */
  @Override
  public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
    Integer evaluations = (Integer) data.get("EVALUATIONS");
    @SuppressWarnings("unchecked")
    List<S> population = (List<S>) data.get("POPULATION");

    if (evaluations != null && population != null) {
      if (evaluations % plotUpdateFrequency == 0) {
        List<S> displayPopulation = population;
        if (filterDominatedSolutions) {
          displayPopulation =
              new NonDominatedSolutionListArchive<S>().addAll(population).solutions();
        }
        List<Double> objective1 =
            displayPopulation.stream().map(s -> s.objectives()[0]).collect(Collectors.toList());
        List<Double> objective2 =
            displayPopulation.stream().map(s -> s.objectives()[1]).collect(Collectors.toList());
        chart.chartTitle(plotTitle + ". Evaluations: " + evaluations);
        chart.updateChart(objective1, objective2);
      }
    } else {
      JMetalLogger.logger.warning(
          getClass().getName()
              + " : insufficient for generating real time information."
              + " Either EVALUATIONS or POPULATION keys have not been registered yet by the algorithm");
    }
  }

  /**
   * Returns the underlying XYChart instance for advanced customization. Use this method to modify
   * chart properties not exposed by the FrontScatterPlot API.
   *
   * @return The XYChart instance used for plotting
   */
  public XYChart chart() {
    return chart.chart();
  }
}
