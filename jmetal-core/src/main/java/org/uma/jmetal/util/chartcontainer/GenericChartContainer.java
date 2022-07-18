package org.uma.jmetal.util.chartcontainer;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.legacy.front.util.FrontUtils;

/**
 * Class for configuring and displaying a XChart.
 *
 * @author Jorge Rodriguez Ordonez
 */
public class GenericChartContainer<S extends Solution<?>> {
  private Map<String, XYChart> charts;
  private XYChart frontChart;
  private XYChart varChart;
  private SwingWrapper<XYChart> swingWrapper;
  private String name;
  private int delay;
  private int objective1;
  private int objective2;
  private int variable1;
  private int variable2;
  private Map<String, List<Integer>> iterations;
  private Map<String, List<Double>> indicatorValues;
  private List<String> referencePointName;

  private double[] xReferenceFrontData ;
  private double[] yReferenceFrontData ;

  public GenericChartContainer(String name) {
    this(name, 0);
  }

  public GenericChartContainer(String name, int delay) {
    this.name = name;
    this.delay = delay;
    this.charts = new LinkedHashMap<String, XYChart>();
    this.iterations = new HashMap<String, List<Integer>>();
    this.indicatorValues = new HashMap<String, List<Double>>();
    referencePointName = new ArrayList<>();
  }

  public void setFrontChart(int objective1, int objective2) throws FileNotFoundException {
    this.setFrontChart(objective1, objective2, null);
  }

  public void setFrontChart(int objective1, int objective2, String referenceFrontFileName)
      throws FileNotFoundException {
    this.objective1 = objective1;
    this.objective2 = objective2;
    this.frontChart =
        new XYChartBuilder()
            .xAxisTitle("Objective " + this.objective1)
            .yAxisTitle("Objective " + this.objective2)
            .build();
    this.frontChart
        .getStyler()
        .setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter)
        .setMarkerSize(5);

    if (referenceFrontFileName != null) {
      getReferenceFrontData(referenceFrontFileName);
      displayReferenceFront();
    }

    var xData = new double[] {0};
    var yData = new double[] {0};
    var frontChartSeries = this.frontChart.addSeries(this.name, xData, yData);
    frontChartSeries.setMarkerColor(Color.RED);

    this.charts.put("Front", this.frontChart);
  }

  public synchronized void setReferencePoint(List<List<Double>> referencePoint) {
    for (var i = 0; i < referencePoint.size(); i++) {
      double rp1 = referencePoint.get(i).get(this.objective1);
      double rp2 = referencePoint.get(i).get(this.objective2);

      referencePointName.add("Reference Point [" + rp1 + ", " + rp2 + "]");

      var referencePointSeries =
          this.frontChart.addSeries(
              referencePointName.get(i), new double[] {rp1}, new double[] {rp2});
      referencePointSeries.setMarkerColor(java.awt.Color.green);
    }
  }

  public synchronized void updateReferencePoint(List<List<Double>> referencePoint) {
    for (var i = 0; i < referencePoint.size(); i++) {
      double rp1 = referencePoint.get(i).get(this.objective1);
      double rp2 = referencePoint.get(i).get(this.objective2);

      this.frontChart.removeSeries(referencePointName.get(i));

      referencePointName.set(i, "Reference Point [" + rp1 + ", " + rp2 + "]");

      var referencePointSeries =
          this.frontChart.addSeries(
              referencePointName.get(i), new double[] {rp1}, new double[] {rp2});
      referencePointSeries.setMarkerColor(Color.green);
    }
  }

  public void setVarChart(int variable1, int variable2) {
    this.variable1 = variable1;
    this.variable2 = variable2;
    this.varChart =
        new XYChartBuilder()
            .xAxisTitle("Variable " + this.variable1)
            .yAxisTitle("Variable " + this.variable2)
            .build();
    this.varChart
        .getStyler()
        .setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter)
        .setMarkerSize(5);

    var xData = new double[] {0};
    var yData = new double[] {0};

    var varChartSeries = this.varChart.addSeries(this.name, xData, yData);
    varChartSeries.setMarkerColor(Color.blue);

    this.charts.put("VAR", this.varChart);
  }

  public void initChart() {
    this.swingWrapper = new SwingWrapper<XYChart>(new ArrayList<XYChart>(this.charts.values()));
    //this.swingWrapper.displayChartMatrix(this.name);
    this.swingWrapper.displayChartMatrix().setTitle(this.name);
  }

  public void updateFrontCharts(@NotNull List<S> solutionList) {
    if (this.frontChart != null) {
      this.frontChart.updateXYSeries(
          this.name,
          this.getSolutionsForObjective(solutionList, this.objective1),
          this.getSolutionsForObjective(solutionList, this.objective2),
          null);
    }

    if (this.varChart != null) {
      this.varChart.updateXYSeries(
          this.name,
          this.getVariableValues((List<DoubleSolution>) solutionList, this.variable1),
          this.getVariableValues((List<DoubleSolution>) solutionList, this.variable2),
          null);
    }
  }

  public void refreshCharts() {
    this.refreshCharts(this.delay);
  }

  public void refreshCharts(int delay) {
    if (delay > 0) {
      try {
        TimeUnit.MILLISECONDS.sleep(delay);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    this.repaint();
  }

  public void addIndicatorChart(String indicator) {
    var indicatorChart = new XYChartBuilder().xAxisTitle("n").yAxisTitle(indicator).build();
    indicatorChart
        .getStyler()
        .setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter)
        .setMarkerSize(5);

    @NotNull List<Integer> indicatorIterations = new ArrayList<Integer>();
    indicatorIterations.add(0);
    List<Double> indicatorValues = new ArrayList<Double>();
    indicatorValues.add(0.0);

    var indicatorSeries =
        indicatorChart.addSeries(this.name, indicatorIterations, indicatorValues);
    indicatorSeries.setMarkerColor(Color.blue);

    this.iterations.put(indicator, indicatorIterations);
    this.indicatorValues.put(indicator, indicatorValues);
    this.charts.put(indicator, indicatorChart);
  }

  public void removeIndicator(String indicator) {
    this.iterations.remove(indicator);
    this.indicatorValues.remove(indicator);
    this.charts.remove(indicator);
  }

  public void updateIndicatorChart(String indicator, Double value) {
    this.indicatorValues.get(indicator).add(value);
    this.iterations.get(indicator).add(this.indicatorValues.get(indicator).size());

    this.charts
        .get(indicator)
        .updateXYSeries(
            this.name, this.iterations.get(indicator), this.indicatorValues.get(indicator), null);
  }

  public void repaint() {
    try {
      for (var i = 0; i < this.charts.values().size(); i++) {
        this.swingWrapper.repaintChart(i);
      }
    } catch (IndexOutOfBoundsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void getReferenceFrontData(@NotNull String fileName) throws FileNotFoundException {
    @NotNull ArrayFront front = new ArrayFront(fileName);
    var data = FrontUtils.convertFrontToArray(front);
    xReferenceFrontData = getObjectiveValues(data, objective1) ;
    yReferenceFrontData = getObjectiveValues(data, objective2) ;
  }

  private void displayReferenceFront() {
    var referenceFront = this.frontChart.addSeries("Reference Front", xReferenceFrontData, yReferenceFrontData);
    referenceFront.setMarkerColor(Color.blue);
  }

  private double[] getObjectiveValues(double[][] data, int obj) {
    var values = new double[10];
    var count = 0;
      for (var datum : data) {
        var v = datum[obj];
          if (values.length == count) values = Arrays.copyOf(values, count * 2);
          values[count++] = v;
      }
      values = Arrays.copyOfRange(values, 0, count);
      return values;
  }

  private double[] getSolutionsForObjective(List<S> solutionList, int objective) {
    var result = new double[10];
    var count = 0;
      for (var s : solutionList) {
        var v = s.objectives()[objective];
          if (result.length == count) result = Arrays.copyOf(result, count * 2);
          result[count++] = v;
      }
      result = Arrays.copyOfRange(result, 0, count);
      return result;
  }

  private double[] getVariableValues(@NotNull List<DoubleSolution> solutionList, int variable) {
    var result = new double[10];
    var count = 0;
      for (var doubleSolution : solutionList) {
          double v = doubleSolution.variables().get(variable);
          if (result.length == count) result = Arrays.copyOf(result, count * 2);
          result[count++] = v;
      }
      result = Arrays.copyOfRange(result, 0, count);
      return result;
  }

  public void saveChart(String fileName, BitmapEncoder.BitmapFormat format) throws IOException {
    for (var chart : this.charts.keySet()) {
      BitmapEncoder.saveBitmap(this.charts.get(chart), fileName + "_" + chart, format);
    }
  }

  public String getName() {
    return this.name;
  }

  public @NotNull GenericChartContainer<S> setName(String name) {
    this.name = name;
    return this;
  }

  public int getDelay() {
    return this.delay;
  }

  public GenericChartContainer<S> setDelay(int delay) {
    this.delay = delay;
    return this;
  }

  public XYChart getFrontChart() {
    return this.frontChart;
  }

  public XYChart getVarChart() {
    return this.varChart;
  }

  public XYChart getChart(String chartName) {
    return this.charts.get(chartName);
  }
}
