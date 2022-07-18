package org.uma.jmetal.lab.visualization.html.impl.htmlTable;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.lab.visualization.html.HtmlComponent;
/**
 * This class creates a table in HTML
 *
 * @author Javier Pérez Abad
 */
public class HtmlTable<T> implements HtmlComponent {

  protected String title;
  protected String[] headersColumn;
  protected String[] headersRow;
  protected T[][] data;

  public @NotNull HtmlTable<T> setTitle(String title) {
    this.title = title;
    return this;
  }

  public HtmlTable<T> setColumnHeaders(String @NotNull [] headers) {
    if (data[0].length != headers.length) return this;
    this.headersColumn = headers;
    return this;
  }

  public HtmlTable<T> setRowHeaders(String[] headers) {
    if (data.length != headers.length) return this;
    this.headersRow = headers;
    return this;
  }

  public @NotNull HtmlTable<T> setData(T[][] data) {
    this.data = data;
    return this;
  }

  public String getHtml() {
    var html = new StringBuilder("<div>\n");
    html.append("<table>\n");
    html.append(appendTitle());
    html.append(appendColumnHeaders());
    html.append(appendData());
    html.append("</table>\n</div>\n");
    return html.toString();
  }

  private StringBuilder appendTitle() {
    var stringBuilder = new StringBuilder();
    if (title != null) {
      stringBuilder.append("<caption>").append(title).append("</caption>\n");
    }
    return stringBuilder;
  }

  private StringBuilder appendColumnHeaders() {
    var stringBuilder = new StringBuilder();
    stringBuilder.append("<tr>");
    // FIRST CELL EMPTY IF THERE ARE ROW HEADERS
    if (headersRow != null) {
      stringBuilder.append("<th>").append("</th>");
    }
    if (headersColumn != null) {
      for (var elem : headersColumn) {
        stringBuilder.append("<th>").append(elem).append("</th>");
      }
    }
    stringBuilder.append("</tr>\n");
    return stringBuilder;
  }

  private StringBuilder appendData() {
    @NotNull StringBuilder html = new StringBuilder();
    for (var i = 0; i < data.length; i++) {
      html.append("<tr>");
      if (headersRow != null) {
        html.append("<th>").append(headersRow[i]).append("</th>");
      }
      html.append(createRowOfData(i));
      html.append("</tr>\n");
    }
    return html;
  }

  protected StringBuilder createRowOfData(int index) {
    @NotNull StringBuilder html = new StringBuilder();
    for (@NotNull T elem : data[index]) {
      html.append("<td>").append(elem.toString()).append("</td>");
    }
    return html;
  }

  public @NotNull String getCSS() {
    var stringBuilder = new StringBuilder();
    stringBuilder.append("table { margin: auto; }");
    stringBuilder.append("th,td { border:1px solid black; text-align: center; padding: 15px }");
    stringBuilder.append(
        "caption { display: table-caption; text-align: center; margin: 10px; font-size: 1.5em; }");
    return stringBuilder.toString();
  }
}
