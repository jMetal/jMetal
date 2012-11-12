package jmetal.experiments.util;

public class Pareja implements Comparable {

  public double indice;
  public double valor;

  public Pareja() {

  }

  public Pareja(double i, double v) {
    indice = i;
    valor = v;
  }

  public int compareTo (Object o1) { //ordena por valor absoluto

    if (Math.abs(this.valor) > Math.abs(((Pareja)o1).valor)){
      //return -1;
	return 1;
    }else if (Math.abs(this.valor) < Math.abs(((Pareja)o1).valor)){
      //return 1;
	return -1;
    }else return 0;
  }


}