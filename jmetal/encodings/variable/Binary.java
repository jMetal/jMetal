//  Binary.java
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

package jmetal.encodings.variable;

import jmetal.core.Variable;
import jmetal.util.PseudoRandom;

import java.util.BitSet;

/**
 * This class implements a generic binary string encodings.variable.It can be used as
 * a base class other binary string based classes (e.g., binary coded integer
 * or real variables).
 */ 
public class Binary extends Variable {
  
  /**
   * Stores the bits constituting the binary string. It is
   * implemented using a BitSet object
   */
  public BitSet bits_;
  
  /**
   * Store the length of the binary string
   */
  protected int numberOfBits_;
        
  /**
   * Default constructor.
   */
  public Binary() {       
  } //Binary
  
  /**
   *  Constructor
   *  @param numberOfBits Length of the bit string
   */
  public Binary(int numberOfBits){   
    numberOfBits_ = numberOfBits;

    bits_ = new BitSet(numberOfBits_);      
    for (int i = 0; i < numberOfBits_; i++){
      if (PseudoRandom.randDouble() < 0.5) {
        bits_.set(i,true);
      } else {
        bits_.set(i,false);                      
      }
    }
  } //Binary
  
  /**
   * Copy constructor.
   * @param variable The Binary encodings.variable to copy.
   */
  public Binary(Binary variable){
    numberOfBits_ = variable.numberOfBits_;
        
    bits_ = new BitSet(numberOfBits_);
    for (int i = 0; i < numberOfBits_; i++) {
      bits_.set(i,variable.bits_.get(i));      
    }
  } //Binary

  /**
   * This method is intended to be used in subclass of <code>Binary</code>, 
   * for examples the classes, <code>BinaryReal</code> and <code>BinaryInt<codes>. 
   * In this classes, the method allows to decode the 
   * value enconded in the binary string. As generic variables do not encode any
   * value, this method do noting 
   */
  public void decode() {
    //do nothing
  } //decode
  
  /** 
   * Creates an exact copy of a Binary object
   * @return An exact copy of the object.
   **/
  public Variable deepCopy() {
    return new Binary(this);
  } //deepCopy

  /**
   * Returns the length of the binary string.
   * @return The length
   */
  public int getNumberOfBits(){
    return numberOfBits_;
  } //getNumberOfBits
  
  /**
   * Returns the value of the ith bit.
   * @param bit The bit to retrieve
   * @return The ith bit
   */
  public boolean getIth(int bit){
    return bits_.get(bit);
  } //getNumberOfBits

  /**
   * Sets the value of the ith bit.
   * @param bit The bit to set
   */
  public void setIth(int bit, boolean value){
    bits_.set(bit, value) ;
  } //getNumberOfBits

  
 /**
  * Obtain the hamming distance between two binary strings
  * @param other The binary string to compare
  * @return The hamming distance
  */
  public int hammingDistance(Binary other) {
    int distance = 0;
    int i = 0;
    while (i < bits_.size()) {
      if (bits_.get(i) != other.bits_.get(i)) {
        distance++;
      }
      i++;
    }
    return distance;
  } // hammingDistance

 /**
  *  
  */
  public String toString() {
    String result ;
    
    result = "" ;
    for (int i = 0; i < numberOfBits_; i ++)
      if (bits_.get(i))
        result = result + "1" ;
      else
        result = result + "0" ;
        
    return result ;
  } // toString
} // Binary
