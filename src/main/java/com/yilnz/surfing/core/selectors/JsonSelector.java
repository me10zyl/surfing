package com.yilnz.surfing.core.selectors;

import com.alibaba.fastjson.JSONPath;

import java.util.ArrayList;
import java.util.List;

public class JsonSelector extends Selector{

    public JsonSelector(String selectPattern) {
        super(selectPattern);
    }

    @Override
    public String select(String text) {
        String res = (String) JSONPath.read(text, selectPattern);
        return res;
    }

    @Override
    public List<String> selectList(String text) {
        final Object read = JSONPath.read(text, selectPattern);
        if(read instanceof List){
            return (List<String>) read;
        }
        List<String> arr = new ArrayList<>();
        arr.add((String) read);
        return arr;
    }
}
