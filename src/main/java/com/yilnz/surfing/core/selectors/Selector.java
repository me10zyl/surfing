package com.yilnz.surfing.core.selectors;

import java.util.List;

public abstract class Selector {
    protected String selectPattern;

    public Selector(String selectPattern) {
        this.selectPattern = selectPattern;
    }

    public abstract String select(String text);

    public abstract List<String> selectList(String text);
}
