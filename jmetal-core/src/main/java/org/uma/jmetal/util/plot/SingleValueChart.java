package org.uma.jmetal.util.plot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.LegendPosition;

public class SingleValueChart {

  private final XYChart chart;
  private SwingWrapper<XYChart> swingWrapper ;
  private final String legend;

  private final List<Double> listOfXValues = new ArrayList<>() ;
  private final List<Double> listOfYValues = new ArrayList<>() ;

  private boolean firstUpdate = true ;
  private long delay = 1000 ;

  public SingleValueChart(String title, String xAxisTitle, String yAxisTitle, String legend) {
    chart = new XYChartBuilder()
        .width(800)
        .height(600)
        .title(title)
        .xAxisTitle(xAxisTitle)
        .yAxisTitle(yAxisTitle)
        .build();

    this.legend = legend ;

    chart.getStyler().setLegendPosition(LegendPosition.OutsideE);
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line) ;
    chart.getStyler().setCursorEnabled(true) ;
  }

  public void delay(long delay) {
    this.delay = delay ;
  }

  public void updateChart(double x, double y) {
    if (firstUpdate) {
      firstUpdate = false ;
      listOfXValues.add(x) ;
      listOfYValues.add(y) ;
      chart.addSeries(legend, listOfXValues, listOfYValues) ;

      swingWrapper = new SwingWrapper<>(chart);
      swingWrapper.displayChart();

    } else {
      try {
        TimeUnit.MILLISECONDS.sleep(delay);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      listOfXValues.add(x);
      listOfYValues.add(y);
      chart.updateXYSeries(legend, listOfXValues, listOfYValues, null);
      swingWrapper.repaintChart();
    }
  }
}
