package com.yilnz.surfing.core.selectors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSelector extends Selector {
    private Integer group;

    public RegexSelector(String selectPattern) {
        super(selectPattern);
    }

    public RegexSelector(String selectPattern, int group) {
        super(selectPattern);
        this.group = group;
    }

    @Override
    public List<String> selectList(String text) {
        Pattern pattern = Pattern.compile(this.selectPattern);
        Matcher matcher = pattern.matcher(text);
        List<String> groups = new ArrayList<>();
        while (matcher.find()) {
            final String group;
            if(this.group != null){
                group = matcher.group(this.group);
            }else{
                 group = matcher.group();
            }
            groups.add(group);
        }
        if (groups.size() == 0) {
            return null;
        }
        return groups;
    }
}
