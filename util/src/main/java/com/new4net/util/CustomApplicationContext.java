package com.new4net.util;

public class CustomApplicationContext extends Container {
    private final static CustomApplicationContext context = new CustomApplicationContext();
    private CustomApplicationContext(){}
    public static CustomApplicationContext getContext(){
        return context;
    }
}
