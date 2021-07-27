package com.yilnz.surfing.core.selectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;

import java.util.ArrayList;
import java.util.List;

public class JsonSelector extends Selector{

    public JsonSelector(String selectPattern) {
        super(selectPattern);
    }

    @Override
    public List<String> selectList(String text) {
        final Object read = JSONPath.read(text, selectPattern);
        if (read == null) {
            return null;
        }
        if(read instanceof List){
            List<String> res = new ArrayList<>();
            final JSONArray jsonArray = (JSONArray) read;
            for(int i = 0; i < jsonArray.size(); i++){
                res.add(jsonArray.get(i).toString());
            }
            return res;
        }
        List<String> arr = new ArrayList<>();
        arr.add(String.valueOf(read));
        return arr;
    }
}
