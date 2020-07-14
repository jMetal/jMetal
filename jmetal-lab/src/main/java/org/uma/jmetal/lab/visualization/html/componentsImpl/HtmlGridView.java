package org.uma.jmetal.lab.visualization.html.componentsImpl;

import org.uma.jmetal.lab.visualization.html.HtmlComponent;

import java.util.LinkedList;
import java.util.List;

public class HtmlGridView implements HtmlComponent {

  private String title;

  public HtmlGridView(String title) {
    this.title = title;
  }

  public HtmlGridView() {
    this(null);
  }

  private final List<HtmlComponent> components = new LinkedList<>();

  public void addComponent(HtmlComponent component) {
    components.add(component);
  }

  @Override
  public String getHtml() {

    StringBuilder sb = new StringBuilder();

    if (title != null) {
      sb.append("<h2>").append(title).append("</h2>\n");
    }

    sb.append("<div class='grid-container'>\n");

    for (HtmlComponent component : components) {

      sb.append("<div class='grid-item'>\n");

      sb.append(component.getHtml());

      sb.append("</div>\n");
    }

    sb.append("</div>\n");

    return sb.toString();
  }

  @Override
  public String getCSS() {

    StringBuilder sb = new StringBuilder();

    sb.append("h2 { text-align: center; }");

    sb.append(".grid-container")
        .append(" { display: flex; flex-direction: row; flex-wrap: wrap; flex-shrink: 0; justify-content: space-evenly; align-items: center; }");

    sb.append(".grid-container .grid-item ").append("{ margin: 15px; }");

    for (HtmlComponent component : components) {

      sb.append(component.getCSS());
    }

    return sb.toString();
  }
}
