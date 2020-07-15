package org.uma.jmetal.lab.visualization.html.impl;

import org.uma.jmetal.lab.visualization.html.HtmlComponent;
import tech.tablesaw.plotly.components.Figure;

import java.util.Objects;
/**
 * This class provides a wrapper to include figures from Tablesaw into the HTML file.
 *
 * @author Javier Pérez Abad
 */
public class HtmlFigure implements HtmlComponent {

  private final Figure figure;

  public HtmlFigure(Figure figure) {
    this.figure = figure;
  }

  public String getHtml() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("<div id='")
        .append(hashCode())
        .append("' class='js-plotly-plot'>")
        .append("</div>");
    stringBuilder.append(figure.asJavascript(Integer.toString(hashCode())));

    return stringBuilder.toString();
  }

  public String getCSS() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(".svg-container { width: 80%, margin: auto} ");
    return stringBuilder.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HtmlFigure that = (HtmlFigure) o;
    return Objects.equals(figure, that.figure);
  }

  @Override
  public int hashCode() {
    return Objects.hash(figure);
  }
}
