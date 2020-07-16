package org.uma.jmetal.lab.visualization.html;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
/**
 * This class creates the skeleton of a HTML file.
 *
 * The body of the HTML file is composed by {@link HtmlComponent} objects.
 *
 * @author Javier Pérez Abad
 */
public class Html {

  public static String PATH_FOLDER = "html";

  private String title;
  private final List<HtmlComponent> components = new LinkedList<>();

  public Html(String title) {
    this.title = title;
  }

  public Html() {
    this("output");
  }

  public void setPathFolder(String pathFolder) {
    PATH_FOLDER = pathFolder;
  }

  public void addComponent(HtmlComponent component) {
    components.add(component);
  }

  public void save() {
    File file = createFileInDirectory();

    writeToFile(file);
  }

  public void show() {
    File file = createFileInDirectory();
    writeToFile(file);

    try {
      browse(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  File createFileInDirectory() {
    Path path = Paths.get(PATH_FOLDER, title + ".html");

    try {
      Files.createDirectories(path.getParent());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return path.toFile();
  }

  private void writeToFile(File outputFile) {
    String output = createDocument();

    try (Writer writer =
                 new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {
      writer.write(output);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  private String createDocument() {
    return "<!DOCTYPE html>\n" +
            "<html>\n" +
            createHead() +
            createBody() +
            "</html>";
  }

  private StringBuilder createHead() {
    StringBuilder sb = new StringBuilder();

    sb.append("<head>\n");
    sb.append("<meta charset=\"UTF-8\">\n");

    sb.append("<title>").append(title).append("</title>\n");

    sb.append("<script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>\n");
    sb.append("<script src=\"https://kit.fontawesome.com/a076d05399.js\"></script>");

    sb = addCSS(sb);

    return sb.append("</head>\n");
  }

  private StringBuilder createBody() {

    StringBuilder sb = new StringBuilder();

    sb.append("<body>\n");

    sb.append("<h1>").append(title).append("</h1>");

    for (HtmlComponent component : components) {

      sb.append("<div class='component'>\n").append(component.getHtml()).append("</div>\n");
    }

    return sb.append("</body>\n");
  }

  private StringBuilder addCSS(StringBuilder sb) {

    sb.append("<style>\n");

    sb.append("html { min-width: 1024px; } \n");

    sb.append(" h1 {text-align: center} \n");

    sb.append(".component { margin : 1em auto 2em auto; width : 90%}\n");

    for (HtmlComponent component : components) {

      sb.append(component.getCSS());
    }

    return sb.append("</style>\n");
  }

  private void browse(File file) throws IOException {

    if (Desktop.isDesktopSupported()) {

      Desktop.getDesktop().browse(file.toURI());

    } else {

      throw new UnsupportedOperationException("Browser not supported.");
    }
  }
}
