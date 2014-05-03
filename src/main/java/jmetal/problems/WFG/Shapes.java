//  Shapes.java
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

/** 
* Class implementing shape functions for WFG benchmark
* Reference: Simon Huband, Luigi Barone, Lyndon While, Phil Hingston
*            A Scalable Multi-objective Test Problem Toolkit.
*            Evolutionary Multi-Criterion Optimization: 
*            Third International Conference, EMO 2005. 
*            Proceedings, volume 3410 of Lecture Notes in Computer Science
*/
public class Shapes {    
    
  /**
   * Calculate a linear shape
   */
  public float linear(float [] x, int m){        
    float  result = (float)1.0;        
    int M = x.length;        
    
    for (int i = 1; i <= M - m; i++)
      result *= x[i-1];
        
    if (m != 1)        
      result *= (1 - x[M - m]);
                
    return result;
  } // linear
    
  /**
   * Calculate a convex shape
   */
  public float convex(float [] x, int m){
    float result = (float)1.0;
    int M = x.length;
        
    for (int i = 1; i <= M - m; i++)
      result *= (1 - Math.cos(x[i-1] * Math.PI * 0.5));
                      
    if (m != 1)        
      result *= (1 - Math.sin(x[M - m] * Math.PI * 0.5));
        
        
    return result;
  } // convex
    
  /**
   * Calculate a concave shape
   */
  public float concave(float [] x, int m){
    float result = (float)1.0;
    int M = x.length;
        
    for (int i = 1; i <= M - m; i++)
      result *= Math.sin(x[i-1] * Math.PI * 0.5);
        
    if (m != 1)
      result *= Math.cos(x[M - m] * Math.PI * 0.5);
            
    return result;
  } // concave
    
  /**
   * Calculate a mixed shape
   */
  public float mixed(float [] x, int A, float alpha){
    float tmp;        
    tmp =(float) Math.cos((float)2.0 * A * (float)Math.PI * x[0] + (float)Math.PI * (float)0.5);
    tmp /= (2.0 * (float) A * Math.PI);
        
    return (float)Math.pow(((float)1.0 - x[0] - tmp),alpha);
  } // mixed
    
  /**
   *  Calculate a disc shape
   */
  public float disc(float [] x, int A, float alpha, float beta){
    float tmp;        
    tmp = (float)Math.cos((float)A * Math.pow(x[0], beta) * Math.PI);
        
    return (float)1.0 - (float)Math.pow(x[0],alpha) * (float)Math.pow(tmp,2.0);        
  } // disc
} // Shapes
