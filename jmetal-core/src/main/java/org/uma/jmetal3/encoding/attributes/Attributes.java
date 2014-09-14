package org.uma.jmetal3.encoding.attributes;

import java.util.HashMap;

/**
 * Created by antonio on 13/09/14.
 */
public abstract class Attributes {
  public HashMap<String, Object> map ;

  public Attributes() {
    map = new HashMap<String, Object>() ;
  }

  public Attributes(Attributes attr) {
    map = (HashMap<String, Object>)attr.map.clone() ;
  }

  public void setAttribute(String name, Object value) {
    map.put(name, value) ;
  }

  public Object getAttribute(String name) {
    return map.get(name) ;
  }

  public abstract Attributes copy() ;
}
