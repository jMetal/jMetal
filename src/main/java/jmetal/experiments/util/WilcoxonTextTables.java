package jmetal.experiments.util ;

import jmetal.experiments.Experiment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Antonio J. Nebro on 17/02/14.
 */
public class WilcoxonTextTables implements iExperimentOutput {
  private Experiment experiment_ ;
  File outputDirectory_;
  String outputDirectoryName_ ;


  public WilcoxonTextTables(Experiment experiment) {
    experiment_ = experiment ;
    outputDirectory_ = null;
    outputDirectoryName_ = "" ;
  }

  @Override
  public void generate() {
//    String rDirectory = "R";
//    rDirectory = experiment_.experimentBaseDirectory_ + "/" + rDirectory;
//    System.out.println("R    : " + rDirectory);
//    outputDirectory_ = new File(rDirectory);
//    if (!outputDirectory_.exists()) {
//      new File(rDirectory).mkdirs();
//      System.out.println("Creating " + rDirectory + " directory");
//    }
    createOutputDirectory();

    try {
    for (int indicator = 0; indicator < experiment_.indicatorList_.length; indicator++) {
      System.out.println("Indicator: " + experiment_.indicatorList_[indicator]);
      String rFile = outputDirectoryName_ + "/"  + experiment_.indicatorList_[indicator] + ".Wilcoxon.R";
      String texFile = outputDirectoryName_ + "/" + experiment_.indicatorList_[indicator] + ".Wilcoxon.tex";

      FileWriter os = new FileWriter(rFile, false);
      String output = "write(\"\", \"" + texFile + "\",append=FALSE)";
      os.write(output + "\n");

      // Generate function latexHeader()
      String dataDirectory = experiment_.experimentBaseDirectory_ + "/data";
      os.write("resultDirectory<-\"" + dataDirectory + "\"" + "\n");
      output = "latexHeader <- function() {" + "\n" +
              "  write(\"\\\\documentclass{article}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\title{StandardStudy}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\usepackage{amssymb}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\usepackage[margin=0.6in]{geometry}\", \"" + texFile + "\", append=TRUE)" + "\n" +
      "  write(\"\\\\author{A.J.Nebro}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\begin{document}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\maketitle\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\section{Tables}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\\", \"" + texFile + "\", append=TRUE)" + "\n" + "}" + "\n";
      os.write(output + "\n");

      // Write function latexTableHeader
      String latexTableLabel = "";
      String latexTabularAlignment = "";
      String latexTableFirstLine = "";
      String latexTableCaption = "";

      latexTableCaption = "  write(\"\\\\caption{\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(problem, \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"." + experiment_.indicatorList_[indicator] + ".}\", \"" + texFile + "\", append=TRUE)" + "\n";
      latexTableLabel = "  write(\"\\\\label{Table:\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(problem, \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"." + experiment_.indicatorList_[indicator] + ".}\", \"" + texFile + "\", append=TRUE)" + "\n";
      latexTabularAlignment = "l";
      latexTableFirstLine = "\\\\hline ";

      for (int i = 1; i < experiment_.algorithmNameList_.length; i++) {
        latexTabularAlignment += "c";
        latexTableFirstLine += " & " + experiment_.algorithmNameList_[i];
      } // for
      //latexTableFirstLine += "\\\\\\\\\",\"" + texFile + "\", append=TRUE)" + "\n";
      latexTableFirstLine += "\\\\\\\\ \"";

      // Generate function latexTableHeader()
      output = "latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {" + "\n" +
              "  write(\"\\\\begin{table}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              latexTableCaption + "\n" +
              latexTableLabel + "\n" +
              "  write(\"\\\\centering\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\begin{scriptsize}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              //"  write(\"\\\\begin{tabular}{" + latexTabularAlignment + "}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\begin{tabular}{\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(tabularString, \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              //latexTableFirstLine +
              "  write(latexTableFirstLine, \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\hline \", \"" + texFile + "\", append=TRUE)" + "\n" + "}" + "\n";
      os.write(output + "\n");

      // Generate function latexTableTail()
      output = "latexTableTail <- function() { " + "\n" +
              "  write(\"\\\\hline\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\end{tabular}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\end{scriptsize}\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "  write(\"\\\\end{table}\", \"" + texFile + "\", append=TRUE)" + "\n" + "}" + "\n";
      os.write(output + "\n");

      // Generate function latexTail()
      output = "latexTail <- function() { " + "\n" +
              "  write(\"\\\\end{document}\", \"" + texFile + "\", append=TRUE)" + "\n" + "}" + "\n";
      os.write(output + "\n");

      if ((Boolean) experiment_.indicatorMinimize_.get(experiment_.indicatorList_[indicator])) {// minimize by default
        // Generate function printTableLine()
        output = "printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { " + "\n" +
                "  file1<-paste(resultDirectory, algorithm1, sep=\"/\")" + "\n" +
                "  file1<-paste(file1, problem, sep=\"/\")" + "\n" +
                "  file1<-paste(file1, indicator, sep=\"/\")" + "\n" +
                "  data1<-scan(file1)" + "\n" +
                "  file2<-paste(resultDirectory, algorithm2, sep=\"/\")" + "\n" +
                "  file2<-paste(file2, problem, sep=\"/\")" + "\n" +
                "  file2<-paste(file2, indicator, sep=\"/\")" + "\n" +
                "  data2<-scan(file2)" + "\n" +
                "  if (i == j) {" + "\n" +
                "    write(\"-- \", \"" + texFile + "\", append=TRUE)" + "\n" +
                "  }" + "\n" +
                "  else if (i < j) {" + "\n" +
                "    if (wilcox.test(data1, data2)$p.value <= 0.05) {" + "\n" +
                "      if (median(data1) <= median(data2)) {" + "\n" +
                "        write(\"$\\\\blacktriangle$\", \"" + texFile + "\", append=TRUE)" + "\n" +
                "      }" + "\n" +
                "      else {" + "\n" +
                "        write(\"$\\\\triangledown$\", \"" + texFile + "\", append=TRUE) " + "\n" +
                "      }" + "\n" +
                "    }" + "\n" +
                "    else {" + "\n" +
                "      write(\"--\", \"" + texFile + "\", append=TRUE) " + "\n" +
                "    }" + "\n" +
                "  }" + "\n" +
                "  else {" + "\n" +
                "    write(\" \", \"" + texFile + "\", append=TRUE)" + "\n" +
                "  }" + "\n" +
                "}" + "\n";
      } // if
      else {
        // Generate function printTableLine()
        output = "printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { " + "\n" +
                "  file1<-paste(resultDirectory, algorithm1, sep=\"/\")" + "\n" +
                "  file1<-paste(file1, problem, sep=\"/\")" + "\n" +
                "  file1<-paste(file1, indicator, sep=\"/\")" + "\n" +
                "  data1<-scan(file1)" + "\n" +
                "  file2<-paste(resultDirectory, algorithm2, sep=\"/\")" + "\n" +
                "  file2<-paste(file2, problem, sep=\"/\")" + "\n" +
                "  file2<-paste(file2, indicator, sep=\"/\")" + "\n" +
                "  data2<-scan(file2)" + "\n" +
                "  if (i == j) {" + "\n" +
                "    write(\"--\", \"" + texFile + "\", append=TRUE)" + "\n" +
                "  }" + "\n" +
                "  else if (i < j) {" + "\n" +
                "    if (wilcox.test(data1, data2)$p.value <= 0.05) {" + "\n" +
                "      if (median(data1) >= median(data2)) {" + "\n" +
                "        write(\"$\\\\blacktriangle$\", \"" + texFile + "\", append=TRUE)" + "\n" +
                "      }" + "\n" +
                "      else {" + "\n" +
                "        write(\"$\\\\triangledown$\", \"" + texFile + "\", append=TRUE) " + "\n" +
                "      }" + "\n" +
                "    }" + "\n" +
                "    else {" + "\n" +
                "      write(\"--\", \"" + texFile + "\", append=TRUE) " + "\n" +
                "    }" + "\n" +
                "  }" + "\n" +
                "  else {" + "\n" +
                "    write(\" \", \"" + texFile + "\", append=TRUE)" + "\n" +
                "  }" + "\n" +
                "}" + "\n";
      }
      os.write(output + "\n");

      // Start of the R script
      output = "### START OF SCRIPT ";
      os.write(output + "\n");

      String problemList = "problemList <-c(";
      String algorithmList = "algorithmList <-c(";

      for (int i = 0; i < (experiment_.problemList_.length - 1); i++) {
        problemList += "\"" + experiment_.problemList_[i] + "\", ";
      }
      problemList += "\"" + experiment_.problemList_[experiment_.problemList_.length - 1] + "\") ";

      for (int i = 0; i < (experiment_.algorithmNameList_.length - 1); i++) {
        algorithmList += "\"" + experiment_.algorithmNameList_[i] + "\", ";
      }
      algorithmList += "\"" + experiment_.algorithmNameList_[experiment_.algorithmNameList_.length - 1] + "\") ";

      latexTabularAlignment = "l";
      for (int i = 1; i < experiment_.algorithmNameList_.length; i++) {
        latexTabularAlignment += "c";
      } // for
      String tabularString = "tabularString <-c(" + "\""+ latexTabularAlignment + "\""+ ") " ;
      String tableFirstLine = "latexTableFirstLine <-c(" + "\"" + latexTableFirstLine + ") " ;

      output = "# Constants" + "\n" +
              problemList + "\n" +
              algorithmList + "\n" +
              tabularString + "\n" +
              tableFirstLine + "\n" +
              "indicator<-\"" + experiment_.indicatorList_[indicator] + "\"";
      os.write(output + "\n");


      output = "\n # Step 1.  Writes the latex header" + "\n" +
              "latexHeader()";
      os.write(output + "\n");

      // Generate tables per problem
      output = "# Step 2. Problem loop " + "\n" +
              "for (problem in problemList) {" + "\n" +
              "  latexTableHeader(problem,  tabularString, latexTableFirstLine)" + "\n\n" +
              "  indx = 0" + "\n" +
              "  for (i in algorithmList) {" + "\n" +
              "    if (i != \"" + experiment_.algorithmNameList_[experiment_.algorithmNameList_.length - 1]+ "\") {" + "\n" +
              "      write(i , \"" + texFile + "\", append=TRUE)" + "\n" +
              "      write(\" & \", \"" + texFile + "\", append=TRUE)" + "\n" +
              "      jndx = 0 " + "\n" +
              "      for (j in algorithmList) {" + "\n" +
              "        if (jndx != 0) {" + "\n" +
              "          if (indx != jndx) {" + "\n" +
              "            printTableLine(indicator, i, j, indx, jndx, problem)" + "\n" +
              "          }" + "\n" +
              "          else {" + "\n" +
              "            write(\"  \", \"" + texFile + "\", append=TRUE)" + "\n" +
              "          }" + "\n" +
              "          if (j != \"" + experiment_.algorithmNameList_[experiment_.algorithmNameList_.length - 1] + "\") {" + "\n" +
              "            write(\" & \", \"" + texFile + "\", append=TRUE)" + "\n" +
              "          }" + "\n" +
              "          else {" + "\n" +
              "            write(\" \\\\\\\\ \", \"" + texFile + "\", append=TRUE)" + "\n" +
              "          }" + "\n" +
              "        }" + "\n" +
              "        jndx = jndx + 1" + "\n" +
              "      }" + "\n" +
              "      indx = indx + 1" + "\n" +
              "    }" + "\n" +
              "  }" + "\n" + "\n" +
              "  latexTableTail()" + "\n" +
              "} # for problem" + "\n";
      os.write(output + "\n");

      // Generate full table
      problemList = "";
      for (String problem : experiment_.problemList_) {
        problemList += problem + " ";
      }
      // The tabular environment and the latexTableFirstLine encodings.variable must be redefined
      latexTabularAlignment = "| l | ";
      latexTableFirstLine = "\\\\hline \\\\multicolumn{1}{|c|}{}";
      for (int i = 1; i < experiment_.algorithmNameList_.length; i++) {
        for (String problem : experiment_.problemList_) {
          latexTabularAlignment += "p{0.15cm}  ";
          //latexTabularAlignment += "c ";
        } // for
        latexTableFirstLine += " & \\\\multicolumn{" + experiment_.problemList_.length + "}{c|}{" + experiment_.algorithmNameList_[i]+"}";
        latexTabularAlignment += " | " ;
      } // for
      latexTableFirstLine += " \\\\\\\\";

      tabularString = "tabularString <-c(" + "\""+ latexTabularAlignment + "\""+ ") " ;
      latexTableFirstLine = "latexTableFirstLine <-c(" + "\""+ latexTableFirstLine + "\""+ ") " ;

      output = tabularString;
      os.write(output + "\n" + "\n");
      output = latexTableFirstLine ;
      os.write(output + "\n" + "\n");

      output = "# Step 3. Problem loop " + "\n" +
              "latexTableHeader(\"" + problemList + "\", tabularString, latexTableFirstLine)" + "\n\n" +
              "indx = 0" + "\n" +
              "for (i in algorithmList) {" + "\n" +
              "  if (i != \"" + experiment_.algorithmNameList_[experiment_.algorithmNameList_.length - 1]+ "\") {" + "\n" +
              "    write(i , \"" + texFile + "\", append=TRUE)" + "\n" +
              "    write(\" & \", \"" + texFile + "\", append=TRUE)" + "\n" + "\n" +
              "    jndx = 0" + "\n" +
              "    for (j in algorithmList) {" + "\n" +
              "      for (problem in problemList) {" + "\n" +
              "        if (jndx != 0) {" + "\n" +
              "          if (i != j) {" + "\n" +
              "            printTableLine(indicator, i, j, indx, jndx, problem)" + "\n" +
              "          }" + "\n" +
              "          else {" + "\n" +
              "            write(\"  \", \"" + texFile + "\", append=TRUE)" + "\n" +
              "          } " + "\n" +
              "          if (problem == \"" + experiment_.problemList_[experiment_.problemList_.length - 1] + "\") {" + "\n" +
              "            if (j == \"" + experiment_.algorithmNameList_[experiment_.algorithmNameList_.length - 1] + "\") {" + "\n" +
              "              write(\" \\\\\\\\ \", \"" + texFile + "\", append=TRUE)" + "\n" +
              "            } " + "\n" +
              "            else {" + "\n" +
              "              write(\" & \", \"" + texFile + "\", append=TRUE)" + "\n" +
              "            }" + "\n" +
              "          }" + "\n" +
              "     else {" + "\n" +
              "    write(\"&\", \"" + texFile + "\", append=TRUE)" + "\n" +
              "     }" + "\n" +
              "        }" + "\n" +
              "      }" + "\n" +
              "      jndx = jndx + 1" + "\n" +
              "    }" + "\n" +
              "    indx = indx + 1" + "\n" +
              "  }" + "\n" +
              "} # for algorithm" + "\n" + "\n" +
              "  latexTableTail()" + "\n";

      os.write(output + "\n");

      // Generate end of file
      output = "#Step 3. Writes the end of latex file " + "\n" +
              "latexTail()" + "\n";
      os.write(output + "\n");


      os.close();
    } // for
    }
    catch (IOException e) {
       e.printStackTrace();
    }

  }

  private void createOutputDirectory() {
    outputDirectoryName_ = "R";
    outputDirectoryName_ = experiment_.experimentBaseDirectory_ + "/" + outputDirectoryName_;
    System.out.println("R    : " + outputDirectoryName_);
    outputDirectory_ = new File(outputDirectoryName_);
    if (!outputDirectory_.exists()) {
      new File(outputDirectoryName_).mkdirs();
      System.out.println("Creating " + outputDirectoryName_ + " directory");
    }
  }
}
