package com.yilnz.surfing.core.selectors;

public abstract class Selectors {

    public static Selector $(String selectPattern){
        return new CssSelector(selectPattern);
    }

    public static Selector $(String selectPattern, String attr){
        return new CssSelector(selectPattern, attr);
    }
    public static Selector $(String selectPattern, boolean containsTag){
        return new CssSelector(selectPattern, containsTag);
    }

    public static Selector regex(String selectPattern) {
        return new RegexSelector(selectPattern);
    }

    public static Selector jsonPath(String jsonPath) {
        return new JsonSelector(jsonPath);
    }

    public static Selector xpath(String xpath){
        return new XPathSelector(xpath);
    }
}
