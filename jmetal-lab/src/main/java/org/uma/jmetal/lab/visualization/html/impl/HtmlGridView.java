package org.uma.jmetal.lab.visualization.html.impl;

import org.uma.jmetal.lab.visualization.html.HtmlComponent;

import java.util.LinkedList;
import java.util.List;
/**
 * This class makes possible to group elements inside the HTML file using the flexbox technology.
 *
 * <p>It is composed of {@link HtmlComponent}.
 *
 * @author Javier Pérez Abad
 */
public class HtmlGridView implements HtmlComponent {

  private final List<HtmlComponent> components = new LinkedList<>();
  private String title;

  public HtmlGridView(String title) {
    this.title = title;
  }

  public HtmlGridView() {
    this(null);
  }

  public void addComponent(HtmlComponent component) {
    components.add(component);
  }

  @Override
  public String getHtml() {
    StringBuilder stringBuilder = new StringBuilder();
    if (title != null) {
      stringBuilder.append("<h2>").append(title).append("</h2>\n");
    }
    stringBuilder.append("<div class='grid-container'>\n");
    for (HtmlComponent component : components) {
      stringBuilder.append("<div class='grid-item'>\n");
      stringBuilder.append(component.getHtml());
      stringBuilder.append("</div>\n");
    }
    stringBuilder.append("</div>\n");
    return stringBuilder.toString();
  }

  @Override
  public String getCSS() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("h2 { text-align: center; }");
    stringBuilder.append(".grid-container")
        .append(
            " { display: flex; flex-direction: row; flex-wrap: wrap; flex-shrink: 0; justify-content: space-evenly; align-items: center; }");
    stringBuilder.append(".grid-container .grid-item ").append("{ margin: 15px; }");
    for (HtmlComponent component : components) {
      stringBuilder.append(component.getCSS());
    }
    return stringBuilder.toString();
  }
}
