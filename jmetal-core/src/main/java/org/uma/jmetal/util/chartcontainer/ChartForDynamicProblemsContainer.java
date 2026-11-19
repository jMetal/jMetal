package org.uma.jmetal.util.chartcontainer;

import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class for configuring and displaying a XChart.
 *
 * <p>XChart is not thread-safe: once the chart is displayed, the series are added and removed in the
 * event dispatch thread of Swing, so that the algorithm, which updates the chart from its own
 * thread, does not change the series while Swing paints them.
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

    double[] xData = new double[]{0};
    double[] yData = new double[]{0};
    XYSeries frontChartSeries = this.frontChart.addSeries(this.name, xData, yData);
    frontChartSeries.setMarkerColor(Color.blue);

    this.charts.put("Front", this.frontChart);
  }

  public synchronized void setReferencePoint(List<List<Double>> referencePoint) {
    onTheEventDispatchThread(() -> addReferencePoints(referencePoint));
  }

  /** Replaces the reference points shown, whose number can change. */
  public synchronized void updateReferencePoint(List<List<Double>> referencePoint) {
    onTheEventDispatchThread(
        () -> {
          for (String name : referencePointName) {
            this.frontChart.removeSeries(name);
          }
          referencePointName.clear();
          addReferencePoints(referencePoint);
        });
  }

  private void addReferencePoints(List<List<Double>> referencePoint) {
    for (List<Double> point : referencePoint) {
      double rp1 = point.get(this.objective1);
      double rp2 = point.get(this.objective2);
      String name = "Reference Point [" + rp1 + ", " + rp2 + "]";
      if (!referencePointName.contains(name)) {
        referencePointName.add(name);
        XYSeries referencePointSeries =
            this.frontChart.addSeries(name, new double[] {rp1}, new double[] {rp2});
        referencePointSeries.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
        referencePointSeries.setMarkerColor(Color.green);
      }
    }
  }

  /** Sets the title of the chart of the front. */
  public void setFrontChartTitle(String title) {
    onTheEventDispatchThread(() -> this.frontChart.setTitle(title));
  }

  /**
   * Runs the change in the event dispatch thread if the chart is displayed, and waits for it;
   * otherwise, it runs it in the current thread.
   */
  private void onTheEventDispatchThread(Runnable change) {
    if (this.swingWrapper == null || SwingUtilities.isEventDispatchThread()) {
      change.run();
    } else {
      try {
        SwingUtilities.invokeAndWait(change);
      } catch (InterruptedException exception) {
        Thread.currentThread().interrupt();
      } catch (InvocationTargetException exception) {
        throw new JMetalException("The chart could not be updated", exception);
      }
    }
  }

  public void initChart() {
    this.swingWrapper = new SwingWrapper<XYChart>(new ArrayList<XYChart>(this.charts.values()));
    //this.swingWrapper.displayChartMatrix(this.name);
    this.swingWrapper.displayChartMatrix();
  }

  public void updateFrontCharts(List<S> solutionList) {
    if (this.frontChart != null) {
      double[] xData = this.getSolutionsForObjective(solutionList, this.objective1);
      double[] yData = this.getSolutionsForObjective(solutionList, this.objective2);
      onTheEventDispatchThread(
          () -> {
            XYSeries series = this.frontChart.addSeries("Front." + counter, xData, yData, null);
            series.setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
            counter++;
          });
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private double[] getSolutionsForObjective(List<S> solutionList, int objective) {
    double[] result = new double[solutionList.size()];
    for (int i = 0; i < solutionList.size(); i++) {
      result[i] = solutionList.get(i).objectives()[objective];
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

  public ChartForDynamicProblemsContainer<S> setName(String name) {
    this.name = name;
    return this;
  }

  public int getDelay() {
    return this.delay;
  }

  public ChartForDynamicProblemsContainer<S> setDelay(int delay) {
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
