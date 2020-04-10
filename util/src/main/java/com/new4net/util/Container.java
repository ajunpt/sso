package com.new4net.util;

import java.util.HashMap;
import java.util.Map;

public class Container {
    Map<Object, Object> container = new HashMap<Object, Object>();
    public void addAttr(Object key,Object value){
        container.put(key,value);
    }
    public Object removeAttr(Object key){
        return container.remove(key);
    }
    public Object getAttr(Object key){
        return container.get(key);
    }
}
