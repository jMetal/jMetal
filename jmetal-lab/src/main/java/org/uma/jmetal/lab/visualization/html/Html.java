package org.uma.jmetal.lab.visualization.html;

import org.jetbrains.annotations.NotNull;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

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
    var file = createFileInDirectory();

    writeToFile(file);
  }

  public void show() {
    var file = createFileInDirectory();
    writeToFile(file);

    try {
      browse(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @NotNull File createFileInDirectory() {
    var path = Paths.get(PATH_FOLDER, title + ".html");

    try {
      Files.createDirectories(path.getParent());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return path.toFile();
  }

  private void writeToFile(@NotNull File outputFile) {
    @NotNull String output = createDocument();

    try (Writer writer =
                 new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {
      writer.write(output);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  private @NotNull String createDocument() {
    return "<!DOCTYPE html>\n" +
            "<html>\n" +
            createHead() +
            createBody() +
            "</html>";
  }

  private StringBuilder createHead() {
    var sb = new StringBuilder();

    sb.append("<head>\n");
    sb.append("<meta charset=\"UTF-8\">\n");

    sb.append("<title>").append(title).append("</title>\n");

    sb.append("<script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script>\n");
    sb.append("<script src=\"https://kit.fontawesome.com/a076d05399.js\"></script>");

    sb = addCSS(sb);

    return sb.append("</head>\n");
  }

  private StringBuilder createBody() {

    @NotNull StringBuilder sb = new StringBuilder();

    sb.append("<body>\n");

    sb.append("<h1>").append(title).append("</h1>");

    for (var component : components) {

      sb.append("<div class='component'>\n").append(component.getHtml()).append("</div>\n");
    }

    return sb.append("</body>\n");
  }

  private StringBuilder addCSS(StringBuilder sb) {

    sb.append("<style>\n");

    sb.append("html { min-width: 1024px; } \n");

    sb.append(" h1 {text-align: center} \n");

    var joiner = new StringJoiner("", ".component { margin : 1em auto 2em auto; width : 90%}\n", "");
      for (@NotNull HtmlComponent component : components) {
        var css = component.getCSS();
          joiner.add(css);
      }
      sb.append(joiner.toString());

    return sb.append("</style>\n");
  }

  private void browse(@NotNull File file) throws IOException {

    if (Desktop.isDesktopSupported()) {

      Desktop.getDesktop().browse(file.toURI());

    } else {

      throw new UnsupportedOperationException("Browser not supported.");
    }
  }
}
