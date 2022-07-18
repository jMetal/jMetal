package org.uma.jmetal.util.chartcontainer;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.legacy.front.util.FrontUtils;

/**
 * Class for configuring and displaying a char with a number of subpopulations and reference points. Designed to be
 * used with the SMPSORP.
 *
 * @author Antonio J. Nebro
 */

public class ChartContainerWithReferencePoints {
  private Map<String, XYChart> charts;
  private XYChart frontChart;
  private XYChart varChart;
  private SwingWrapper<XYChart> swingWrapper;
  private String name;
  private int delay;
  private int objective1;
  private int objective2;
  private List<String> referencePointName ;

  public ChartContainerWithReferencePoints(String name) {
    this(name, 0);
  }

  public ChartContainerWithReferencePoints(String name, int delay) {
    this.name = name;
    this.delay = delay;
    this.charts = new LinkedHashMap<String, XYChart>();
    this.referencePointName = new ArrayList<>() ;
  }

  public void setFrontChart(int objective1, int objective2) throws FileNotFoundException {
    this.setFrontChart(objective1, objective2, null);
  }

  public void setFrontChart(int objective1, int objective2, @Nullable String referenceFrontFileName) throws FileNotFoundException {
    this.objective1 = objective1;
    this.objective2 = objective2;
    this.frontChart = new XYChartBuilder().xAxisTitle("Objective " + this.objective1)
        .yAxisTitle("Objective " + this.objective2).build();
    this.frontChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter).setMarkerSize(5);

    if (referenceFrontFileName != null) {
      this.displayReferenceFront(referenceFrontFileName);
    }

    double @NotNull [] xData = new double[] { 0 };
    double @NotNull [] yData = new double[] { 0 };
    XYSeries frontChartSeries = this.frontChart.addSeries(this.name, xData, yData);
    frontChartSeries.setMarkerColor(Color.blue);

    this.charts.put("Front", this.frontChart);
  }

  public synchronized void setReferencePoint(List<List<Double>> referencePoint){
    for (int i = 0; i < referencePoint.size(); i++) {
      double rp1 = referencePoint.get(i).get(this.objective1);
      double rp2 = referencePoint.get(i).get(this.objective2);

      referencePointName.add("Reference Point [" + rp1 + ", " + rp2 + "]") ;

      XYSeries referencePointSeries = this.frontChart.addSeries(referencePointName.get(i),
          new double[] { rp1 },
          new double[] { rp2 });
      referencePointSeries.setMarkerColor(Color.green);
    }
  }

  public synchronized void updateReferencePoint(@NotNull List<List<Double>> referencePoint){
    for (int i = 0; i < referencePoint.size(); i++) {
      double rp1 = referencePoint.get(i).get(this.objective1);
      double rp2 = referencePoint.get(i).get(this.objective2);

      this.frontChart.removeSeries(referencePointName.get(i)) ;

      referencePointName.set(i, "Reference Point [" + rp1 + ", " + rp2 + "]") ;

      XYSeries referencePointSeries = this.frontChart.addSeries(referencePointName.get(i),
          new double[] { rp1 },
          new double[] { rp2 });
      referencePointSeries.setMarkerColor(Color.green);
    }
  }

  public void initChart() {
    this.swingWrapper = new SwingWrapper<XYChart>(new ArrayList<XYChart>(this.charts.values()));
    //this.swingWrapper.displayChartMatrix(this.name);
    this.swingWrapper.displayChartMatrix();
  }

  public void updateFrontCharts(@NotNull List<DoubleSolution> solutionList) {
    if (this.frontChart != null) {
      this.frontChart.updateXYSeries(this.name,
          this.getSolutionsForObjective(solutionList, this.objective1),
          this.getSolutionsForObjective(solutionList, this.objective2),
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

  public void repaint() {
    try {
      for (int i = 0; i < this.charts.values().size(); i++) {
        this.swingWrapper.repaintChart(i);
      }
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
  }

  private void displayFront(String name, @NotNull String fileName, int objective1, int objective2)
      throws FileNotFoundException {
    ArrayFront front = new ArrayFront(fileName, ",");
    double[][] data = FrontUtils.convertFrontToArray(front);
    double @NotNull [] xData = getObjectiveValues(data, objective1);
    double[] yData = getObjectiveValues(data, objective2);
    XYSeries referenceFront = this.frontChart.addSeries(name, xData, yData);
    referenceFront.setMarkerColor(Color.red);
  }

  private void displayReferenceFront(@NotNull String fileName) throws FileNotFoundException {
    this.displayReferenceFront(fileName, this.objective1, this.objective2);
  }

  private void displayReferenceFront(String fileName, int objective1, int objective2) throws FileNotFoundException {
    this.displayFront("Reference Front", fileName, objective1, objective2);
  }

  private double[] getObjectiveValues(double[][] data, int obj) {
    double @NotNull [] values = new double[10];
    int count = 0;
    for (double[] datum : data) {
      double v = datum[obj];
      if (values.length == count) values = Arrays.copyOf(values, count * 2);
      values[count++] = v;
    }
    values = Arrays.copyOfRange(values, 0, count);
    return values;
  }

  private double[] getSolutionsForObjective(List<DoubleSolution> solutionList, int objective) {
    double[] result = new double[10];
    int count = 0;
    for (@NotNull DoubleSolution doubleSolution : solutionList) {
      double v = doubleSolution.objectives()[objective];
      if (result.length == count) result = Arrays.copyOf(result, count * 2);
      result[count++] = v;
    }
    result = Arrays.copyOfRange(result, 0, count);
    return result;
  }

  public void saveChart(String fileName, @NotNull BitmapFormat format) throws IOException {
    for (String chart : this.charts.keySet()) {
      BitmapEncoder.saveBitmap(this.charts.get(chart), fileName + "_" + chart, format);
    }
  }

  public String getName() {
    return this.name;
  }

  public @NotNull ChartContainerWithReferencePoints setName(String name) {
    this.name = name;
    return this;
  }

  public int getDelay() {
    return this.delay;
  }

  public @NotNull ChartContainerWithReferencePoints setDelay(int delay) {
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
