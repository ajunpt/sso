package com.new4net.util;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PageConverter<T,R> {
    public  Page<R> convert(Page<T>page,Function<T,R> function){

        List<T> modules= page.getList();
        List<R> moduleInfos = new ArrayList<>();
        Page<R> pageResult = new Page<>();
        pageResult.setList(moduleInfos);
        pageResult.setCount(page.getCount());
        pageResult.setPageSize(page.getPageSize());
        pageResult.setPageNo(page.getPageNo());

        if(modules!=null){
            for(T t:modules){
                if(t!=null){
                    moduleInfos.add(function.apply(t));
                }
            }
        }

        return pageResult;
    }
}
