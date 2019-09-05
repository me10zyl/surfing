package com.yilnz.surfing.core.selectors;

import java.util.ArrayList;
import java.util.List;

public abstract class Selector {
    protected String selectPattern;
    protected String logicType;
    protected List<Selector> otherSelectors = new ArrayList<>();

    public String getLogicType() {
        return logicType;
    }

    public Selector(String selectPattern) {
        this.selectPattern = selectPattern;
    }

    public abstract String select(String text);

    public abstract List<String> selectList(String text);

    public List<Selector> getOtherSelectors() {
        return otherSelectors;
    }

    public Selector and(Selector selector){
        selector.logicType = "AND";
        otherSelectors.add(selector);
        if(selector.otherSelectors.size() > 0){
            throw new UnsupportedOperationException("不能重复嵌套逻辑操作!");
        }
        return this;
    }

    public Selector or(Selector selector) {
        selector.logicType = "OR";
        otherSelectors.add(selector);
        if(selector.otherSelectors.size() > 0){
            throw new UnsupportedOperationException("不能重复嵌套逻辑操作!");
        }
        return this;
    }
}
