package com.yilnz.surfing.core.header.generators;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurlHeaderGenerator implements HeaderGenerator {
    private String curlString;
    public CurlHeaderGenerator(String s) {
        curlString = s;
    }

    public String getUrl(){
        final Matcher matcher = Pattern.compile("curl ([^ ]+)").matcher(curlString);
        if(matcher.find()){
            return matcher.group(1).replaceAll("^['\"]|['\"]$", "");
        }
        return null;
    }

    public String getBody(){
        final Matcher matcher = Pattern.compile("--data-binary \\$?['\"](.+)['\"]").matcher(curlString);
        if(matcher.find()){
            return matcher.group(1);
        }
        return null;
    }


    @Override
    public Map<? extends String, ? extends String> generateHeaders() {
        final Map<String, String> headers = new HashMap<>();
        final Matcher matcher = Pattern.compile("-H ['\"](.+?)\\s*:\\s*(.+?)['\"]").matcher(curlString);
        while (matcher.find()) {
            headers.put(matcher.group(1), matcher.group(2));
        }
        //System.out.println(headers);
        return headers;
    }
}
