package org.uma.jmetal.util.chartcontainer;

import java.awt.Color;
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

/**
 * Class for configuring and displaying a XChart.
 *
 * @author Jorge Rodriguez Ordonez
 */

public class ChartForDynamicProblemsContainer<S extends Solution<?>> {
  private Map<String, XYChart> charts;
  private XYChart frontChart;
  private XYChart varChart;
  private SwingWrapper<XYChart> swingWrapper;
  private String name;
  private int delay;
  private int objective1;
  private int objective2;
  private List<String> referencePointName;

  private int counter = 0;

  public ChartForDynamicProblemsContainer(String name) {
    this(name, 0);
  }

  public ChartForDynamicProblemsContainer(String name, int delay) {
    this.name = name;
    this.delay = delay;
    this.charts = new LinkedHashMap<String, XYChart>();
    referencePointName = new ArrayList<>();
  }

  public void setFrontChart(int objective1, int objective2) {
    this.objective1 = objective1;
    this.objective2 = objective2;
    this.frontChart = new XYChartBuilder().xAxisTitle("Objective " + this.objective1)
            .yAxisTitle("Objective " + this.objective2).build();
    this.frontChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter).setMarkerSize(5);

    var xData = new double[]{0};
    var yData = new double[]{0};
    var frontChartSeries = this.frontChart.addSeries(this.name, xData, yData);
    frontChartSeries.setMarkerColor(Color.blue);

    this.charts.put("Front", this.frontChart);
  }

  public synchronized void setReferencePoint(List<List<Double>> referencePoint) {
    for (var i = 0; i < referencePoint.size(); i++) {
      double rp1 = referencePoint.get(i).get(this.objective1);
      double rp2 = referencePoint.get(i).get(this.objective2);

      referencePointName.add("Reference Point [" + rp1 + ", " + rp2 + "]");

      var referencePointSeries = this.frontChart.addSeries(referencePointName.get(i),
              new double[]{rp1},
              new double[]{rp2});
      referencePointSeries.setMarkerColor(Color.green);
    }
  }

  public synchronized void updateReferencePoint(List<List<Double>> referencePoint) {
    for (var i = 0; i < referencePoint.size(); i++) {
      double rp1 = referencePoint.get(i).get(this.objective1);
      double rp2 = referencePoint.get(i).get(this.objective2);

      this.frontChart.removeSeries(referencePointName.get(i));

      referencePointName.set(i, "Reference Point [" + rp1 + ", " + rp2 + "]");

      var referencePointSeries = this.frontChart.addSeries(referencePointName.get(i),
              new double[]{rp1},
              new double[]{rp2});
      referencePointSeries.setMarkerColor(Color.green);
    }
  }

  public void initChart() {
    this.swingWrapper = new SwingWrapper<XYChart>(new ArrayList<XYChart>(this.charts.values()));
    //this.swingWrapper.displayChartMatrix(this.name);
    this.swingWrapper.displayChartMatrix();
  }

  public void updateFrontCharts(List<S> solutionList) {
    if (this.frontChart != null) {
      this.frontChart.addSeries("Front." + counter,
              this.getSolutionsForObjective(solutionList, this.objective1),
              this.getSolutionsForObjective(solutionList, this.objective2),
              null);
      counter++;
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
      for (var i = 0; i < this.charts.values().size(); i++) {
        this.swingWrapper.repaintChart(i);
      }
    } catch (IndexOutOfBoundsException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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

  public void saveChart(String fileName, BitmapEncoder.BitmapFormat format) throws IOException {
    for (var chart : this.charts.keySet()) {
      BitmapEncoder.saveBitmap(this.charts.get(chart), fileName + "_" + chart, format);
    }
  }

  public String getName() {
    return this.name;
  }

  public ChartForDynamicProblemsContainer<S> setName(String name) {
    this.name = name;
    return this;
  }

  public int getDelay() {
    return this.delay;
  }

  public @NotNull ChartForDynamicProblemsContainer<S> setDelay(int delay) {
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
