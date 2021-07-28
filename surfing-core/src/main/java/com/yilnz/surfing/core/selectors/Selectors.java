package com.yilnz.surfing.core.selectors;

public abstract class Selectors {

    public static Selector $(String selectPattern){
        return new CssSelector(selectPattern);
    }

    public static Selector $(String selectPattern, String attr){
        return new CssSelector(selectPattern, attr);
    }

    public static Selector regex(String selectPattern) {
        return new RegexSelector(selectPattern);
    }

    public static Selector regex(String selectPattern, int group) {
        return new RegexSelector(selectPattern, group);
    }

    public static Selector jsonPath(String jsonPath) {
        return new JsonSelector(jsonPath);
    }

    public static Selector xpath(String xpath){
        return new XPathSelector(xpath);
    }

    public static Selector replace(String selectPattern, String replacement){
        return new ReplaceSelector(selectPattern, replacement);
    }
}
