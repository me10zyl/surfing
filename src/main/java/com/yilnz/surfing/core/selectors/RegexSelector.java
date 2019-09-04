package com.yilnz.surfing.core.selectors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSelector extends Selector {

    public RegexSelector(String selectPattern) {
        super(selectPattern);
    }

    @Override
    public String select(String text) {
        Pattern pattern = Pattern.compile(this.selectPattern);
        Matcher matcher = pattern.matcher(text);
        String group = null;
        while (matcher.find()) {
            group = matcher.group();
        }
        return group;
    }

    @Override
    public List<String> selectList(String text) {
        Pattern pattern = Pattern.compile(this.selectPattern);
        Matcher matcher = pattern.matcher(text);
        List<String> groups = new ArrayList<>();
        while (matcher.find()) {
            final String group = matcher.group();
            groups.add(group);
        }
        return groups;
    }
}
