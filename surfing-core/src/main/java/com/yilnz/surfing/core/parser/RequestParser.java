package com.yilnz.surfing.core.parser;

import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.header.generators.BulkHeaderGenerator;

import java.util.Scanner;

public class RequestParser {
    public static SurfHttpRequest parse(String rawText){
        final SurfHttpRequest surfHttpRequest = new SurfHttpRequest();
        String method = "GET";
        String url = "";
        int i = 0;
        StringBuilder header = new StringBuilder();
        StringBuilder body = new StringBuilder();
        Scanner scanner = new Scanner(rawText);
        boolean isBody = false;
        boolean isHeader = false;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if(isHeader){
                header.append(line).append("\n");
            }
            if(i == 0){
                final String[] split = line.split(" ");
                method = split[0];
                url = split[1];
                isHeader = true;
            }
            if(isBody){
                body.append(line);
            }
            if(line.equals("")){
                isBody = true;
                isHeader = false;
            }
            i++;
        }

        surfHttpRequest.setUrl(url);
        surfHttpRequest.setMethod(method);
        surfHttpRequest.setHeaderGenerator(new BulkHeaderGenerator(header.toString()));
        surfHttpRequest.setBody(body.toString());
        return surfHttpRequest;
    }
}
