package org.uma.jmetal3.encoding.attributes;

import java.util.HashMap;

/**
 * Created by antonio on 13/09/14.
 */
public abstract class AlgorithmAttributes {
  public HashMap<String, Object> map ;

  public AlgorithmAttributes() {
    map = new HashMap<String, Object>() ;
  }

  public AlgorithmAttributes(AlgorithmAttributes attr) {
    map = (HashMap<String, Object>)attr.map.clone() ;
  }

  public void setAttribute(String name, Object value) {
    map.put(name, value) ;
  }

  public Object getAttribute(String name) {
    return map.get(name) ;
  }

  public abstract AlgorithmAttributes copy() ;
}
