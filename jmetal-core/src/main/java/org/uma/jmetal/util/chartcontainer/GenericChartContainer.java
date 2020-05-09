package org.uma.jmetal.util.chartcontainer;

import org.knowm.xchart.*;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.front.impl.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    double[] xData = new double[] {0};
    double[] yData = new double[] {0};
    XYSeries frontChartSeries = this.frontChart.addSeries(this.name, xData, yData);
    frontChartSeries.setMarkerColor(Color.RED);

    this.charts.put("Front", this.frontChart);
  }

  public synchronized void setReferencePoint(List<List<Double>> referencePoint) {
    for (int i = 0; i < referencePoint.size(); i++) {
      double rp1 = referencePoint.get(i).get(this.objective1);
      double rp2 = referencePoint.get(i).get(this.objective2);

      referencePointName.add("Reference Point [" + rp1 + ", " + rp2 + "]");

      XYSeries referencePointSeries =
          this.frontChart.addSeries(
              referencePointName.get(i), new double[] {rp1}, new double[] {rp2});
      referencePointSeries.setMarkerColor(java.awt.Color.green);
    }
  }

  public synchronized void updateReferencePoint(List<List<Double>> referencePoint) {
    for (int i = 0; i < referencePoint.size(); i++) {
      double rp1 = referencePoint.get(i).get(this.objective1);
      double rp2 = referencePoint.get(i).get(this.objective2);

      this.frontChart.removeSeries(referencePointName.get(i));

      referencePointName.set(i, "Reference Point [" + rp1 + ", " + rp2 + "]");

      XYSeries referencePointSeries =
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

    double[] xData = new double[] {0};
    double[] yData = new double[] {0};

    XYSeries varChartSeries = this.varChart.addSeries(this.name, xData, yData);
    varChartSeries.setMarkerColor(Color.blue);

    this.charts.put("VAR", this.varChart);
  }

  public void initChart() {
    this.swingWrapper = new SwingWrapper<XYChart>(new ArrayList<XYChart>(this.charts.values()));
    //this.swingWrapper.displayChartMatrix(this.name);
    this.swingWrapper.displayChartMatrix().setTitle(this.name);
  }

  public void updateFrontCharts(List<S> solutionList) {
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
    XYChart indicatorChart = new XYChartBuilder().xAxisTitle("n").yAxisTitle(indicator).build();
    indicatorChart
        .getStyler()
        .setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter)
        .setMarkerSize(5);

    List<Integer> indicatorIterations = new ArrayList<Integer>();
    indicatorIterations.add(0);
    List<Double> indicatorValues = new ArrayList<Double>();
    indicatorValues.add(0.0);

    XYSeries indicatorSeries =
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
      for (int i = 0; i < this.charts.values().size(); i++) {
        this.swingWrapper.repaintChart(i);
      }
    } catch (IndexOutOfBoundsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void getReferenceFrontData(String fileName) throws FileNotFoundException {
    ArrayFront front = new ArrayFront(fileName);
    double[][] data = FrontUtils.convertFrontToArray(front);
    xReferenceFrontData = getObjectiveValues(data, objective1) ;
    yReferenceFrontData = getObjectiveValues(data, objective2) ;
  }

  private void displayReferenceFront() {
    XYSeries referenceFront = this.frontChart.addSeries("Reference Front", xReferenceFrontData, yReferenceFrontData);
    referenceFront.setMarkerColor(Color.blue);
  }

  private double[] getObjectiveValues(double[][] data, int obj) {
    double[] values = new double[data.length];
    for (int i = 0; i < data.length; i++) {
      values[i] = data[i][obj];
    }
    return values;
  }

  private double[] getSolutionsForObjective(List<S> solutionList, int objective) {
    double[] result = new double[solutionList.size()];
    for (int i = 0; i < solutionList.size(); i++) {
      result[i] = solutionList.get(i).getObjective(objective);
    }
    return result;
  }

  private double[] getVariableValues(List<DoubleSolution> solutionList, int variable) {
    double[] result = new double[solutionList.size()];
    for (int i = 0; i < solutionList.size(); i++) {
      result[i] = solutionList.get(i).getVariable(variable);
    }
    return result;
  }

  public void saveChart(String fileName, BitmapEncoder.BitmapFormat format) throws IOException {
    for (String chart : this.charts.keySet()) {
      BitmapEncoder.saveBitmap(this.charts.get(chart), fileName + "_" + chart, format);
    }
  }

  public String getName() {
    return this.name;
  }

  public GenericChartContainer<S> setName(String name) {
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
