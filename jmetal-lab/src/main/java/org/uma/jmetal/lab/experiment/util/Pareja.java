package org.uma.jmetal.lab.experiment.util;

public class Pareja implements Comparable {

  private double indice;
  private double valor;
  private boolean minimizar;

  public Pareja() {}

  public Pareja(double i, double v, boolean minimizar) {
    indice = i;
    valor = v;
    this.minimizar = minimizar;
  }

  public double getIndice() {
    return indice;
  }

  public void setIndice(double indice) {
    this.indice = indice;
  }

  public double getValor() {
    return valor;
  }

  public void setValor(double valor) {
    this.valor = valor;
  }

  public int compareTo(Object o1) { // ordena por valor absoluto
    if (this.valor < ((Pareja) o1).valor)
      if (minimizar) return -1;
      else return 1;
    else if (this.valor > ((Pareja) o1).valor)
      if (minimizar) return 1;
      else return -1;
    else return 0;
  }
}
