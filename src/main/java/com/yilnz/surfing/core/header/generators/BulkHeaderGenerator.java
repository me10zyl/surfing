package com.yilnz.surfing.core.header.generators;

import java.util.HashMap;
import java.util.Map;

public class BulkHeaderGenerator implements HeaderGenerator {

    private String bulk;

    public BulkHeaderGenerator(String bulk) {
        this.bulk = bulk;
    }

    @Override
    public Map<? extends String, ? extends String> generateHeaders() {
        final String bulk = this.bulk;
        final String[] split = bulk.split("\n");
        Map<String, String> res = new HashMap<>();
        for (String s : split) {
            final String[] split1 = s.split(":");
            res.put(split1[0], split1[1]);
        }
        return res;
    }
}
