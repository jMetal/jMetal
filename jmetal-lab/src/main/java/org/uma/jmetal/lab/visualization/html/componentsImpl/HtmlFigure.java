package org.uma.jmetal.lab.visualization.html.componentsImpl;

import org.uma.jmetal.lab.visualization.html.HtmlComponent;
import tech.tablesaw.plotly.components.Figure;

import java.util.Objects;

public class HtmlFigure implements HtmlComponent {

  private final Figure figure;

  public HtmlFigure(Figure figure) {
    this.figure = figure;
  }

  public String getHtml() {

    StringBuilder sb = new StringBuilder();

    sb.append("<div id='").append(hashCode()).append("' class='js-plotly-plot'>").append("</div>");

    sb.append(figure.asJavascript(Integer.toString(hashCode())));

    return sb.toString();
  }

  public String getCSS() {

    StringBuilder sb = new StringBuilder();

    sb.append(".svg-container { width: 80%, margin: auto} ");

    return sb.toString();
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
