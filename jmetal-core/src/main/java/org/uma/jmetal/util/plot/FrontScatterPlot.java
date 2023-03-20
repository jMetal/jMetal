package org.uma.jmetal.util.plot;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class FrontScatterPlot {
  private final XYChart chart;
  private SwingWrapper<XYChart> swingWrapper;
  private final String legend;
  private boolean firstUpdate = true;
  private long delay = 500 ;

  public FrontScatterPlot(String title, String xAxisTitle, String yAxisTitle, String legend) {
    chart = new XYChartBuilder()
        .width(800)
        .height(600)
        .title(title)
        .xAxisTitle(xAxisTitle)
        .yAxisTitle(yAxisTitle)
        .build();

    this.legend = legend;

    chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
    chart.getStyler().setMarkerSize(6);
    chart.getStyler().setShowWithinAreaPoint(true) ;
    chart.getStyler().setCursorEnabled(true) ;
  }

  private double[] objectiveValues(double[][] data, int obj) {
    return Arrays.stream(data).mapToDouble(v -> v[obj]).toArray();
  }

  public void delay(long delay) {
    this.delay = delay ;
  }

  public void setFront(double[][] front, String frontName) {
    double[] x = objectiveValues(front, 0);
    double[] y = objectiveValues(front, 1);
    XYSeries series = chart.addSeries(frontName, x, y);
    series.setMarkerColor(Color.blue);
    series.setMarker(SeriesMarkers.CIRCLE);
  }


  public void addPoint(double x, double y, String pointName) {
    XYSeries pointSeries = chart.addSeries(pointName,
        new double[]{x},
        new double[]{y});
    pointSeries.setMarker(SeriesMarkers.DIAMOND);
    pointSeries.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line) ;
  }

  public void chartTitle(String newTitle) {
    chart.setTitle(newTitle);
  }

  public void addPoint(double x, double y, String pointName, Color color) {
    XYSeries pointSeries = chart.addSeries(pointName,
        new double[]{x},
        new double[]{y});
    pointSeries.setMarker(SeriesMarkers.CIRCLE);
    pointSeries.setMarkerColor(color);
  }

  public void updatePoint(double x, double y, String pointName) {
    chart.updateXYSeries(pointName,
        new double[]{x},
        new double[]{y}, null);
  }

  public void updateChart(double[] x, double[] y) {
    this.updateChart(Arrays.stream(x).boxed().collect(Collectors.toList()),
        Arrays.stream(y).boxed().collect(Collectors.toList()));
  }

  public void updateChart(List<Double> x, List<Double> y) {
    if (firstUpdate) {
      swingWrapper = new SwingWrapper<>(chart);
      swingWrapper.displayChart();

      firstUpdate = false;
      var series = chart.addSeries(legend, x, y);
      series.setMarkerColor(Color.BLACK);
      series.setMarker(SeriesMarkers.CIRCLE);
    } else {
      try {
        TimeUnit.MILLISECONDS.sleep(delay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      chart.updateXYSeries(legend, x, y, null);
      swingWrapper.repaintChart();
    }
  }

  public XYChart chart()  {
    return chart ;
  }
}
