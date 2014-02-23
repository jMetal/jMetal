//  Transformations.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.problems.WFG;

import jmetal.util.Configuration;
import jmetal.util.JMException;

/**
 * Class implementing the basics transformations for WFG
 */
public class Transformations {
        
  /**
   * Stores a default epsilon value 
   */
  private static final float epsilon = (float)1.0e-10;
    
  /**
   * b_poly transformation
   * @throws JMException 
   */
  public float b_poly(float y, float alpha) throws JMException{
    if (!(alpha>0)) {
      
      Configuration.logger_.severe("WFG.Transformations.b_poly: Param alpha " +
          "must be > 0") ;
      Class cls = java.lang.String.class;
      String name = cls.getName(); 
      throw new JMException("Exception in " + name + ".b_poly()") ; 
    }
        
    return correct_to_01((float)StrictMath.pow(y,alpha));
  } //b_poly
    
  
  /**
   * b_flat transformation
   */
  public float b_flat(float y, float A, float B, float C){    
    float tmp1 = Math.min((float)0, (float)Math.floor(y - B))* A*(B-y)/B;
    float tmp2 = Math.min((float)0, (float)Math.floor(C - y))* (1 - A)*(y - C)/(1 - C);
        
    return correct_to_01(A + tmp1 - tmp2);
  }  // b_flat      
    
  /**
   * s_linear transformation
   */
  public float s_linear(float y, float A){
    return correct_to_01(Math.abs(y - A) /(float)Math.abs(Math.floor(A - y) + A));
  } // s_linear
    
  /**
   * s_decept transformation
   */
  public float s_decept(float y, float A, float B, float C){        
    float tmp, tmp1, tmp2;
        
    tmp1 = (float)Math.floor(y - A + B) * ((float)1.0 - C + (A - B)/B) / (A - B);
    tmp2 = (float)Math.floor(A + B - y) * ((float)1.0 - C + ((float)1.0 - A - B) / B) / ((float)1.0 - A - B);
        
    tmp = Math.abs(y - A) - B;
        
    return correct_to_01((float)1 + tmp * (tmp1 + tmp2 + (float)1.0/B));
  } // s_decept
    
  /**
   * s_multi transformation
   */
  public float s_multi(float y, int A, int B, float C){                
    float tmp1, tmp2;
        
    tmp1 = ((float)4.0 * A + (float)2.0) *
           (float)Math.PI * 
           ((float)0.5 - Math.abs(y - C) /((float)2.0 * ((float)Math.floor(C - y) + C)));
    tmp2 = (float)4.0 * B * 
           (float)StrictMath.pow(Math.abs(y - C) /((float)2.0 * ((float)Math.floor(C - y) + C))
                                 ,(float)2.0);
        
    return correct_to_01(((float)1.0 + (float)Math.cos(tmp1) + tmp2) / (B + (float)2.0));
  } //s_multi
    
  /**
   * r_sum transformation
   */
  public float r_sum(float [] y, float [] w){
    float tmp1 = (float)0.0, tmp2 =(float) 0.0;
    for (int i = 0; i < y.length; i++){
      tmp1 += y[i]*w[i];
      tmp2 += w[i];
    }
        
    return correct_to_01(tmp1 / tmp2);
  } // r_sum  
    
  /**
   * r_nonsep transformation
   */
  public float r_nonsep(float [] y, int A){               
    float tmp, denominator, numerator;
      
    tmp = (float)Math.ceil(A/(float)2.0);        
    denominator = y.length * tmp * ((float)1.0 + (float)2.0*A - (float)2.0*tmp)/A;        
    numerator = (float)0.0;
    for (int j = 0; j < y.length; j++){
      numerator += y[j];
      for (int k = 0; k <= A-2; k++){
        numerator += Math.abs( y[j] - y[( j+k+1 ) % y.length]);
      }
    }
        
    return correct_to_01(numerator/denominator);
  } // r_nonsep
    
  /**
   * b_param transformation
   */
  public float b_param(float y, float u, float A, float B, float C){
    float result, v, exp;
        
    v = A - ((float)1.0 - (float)2.0 * u) *
            Math.abs((float)Math.floor((float)0.5 - u) + A);
    exp = B + (C - B)*v;        
    result = (float)StrictMath.pow(y,exp);
        
    return correct_to_01(result);                  
  } // b_param
    
  /**
   */
  float correct_to_01(float a){    
    float min = (float)0.0;
    float max = (float)1.0;
    float min_epsilon = min - epsilon;
    float max_epsilon = max + epsilon;

    if (( a <= min && a >= min_epsilon ) || (a >= min && a <= min_epsilon)) {
      return min;        
    } else if (( a >= max && a <= max_epsilon ) || (a <= max && a >= max_epsilon)) {
      return max;        
    } else {
      return a;        
    }
  } // correct_to_01      
} // Transformations
