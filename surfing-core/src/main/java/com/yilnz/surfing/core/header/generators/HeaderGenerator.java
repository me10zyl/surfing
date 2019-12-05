package com.yilnz.surfing.core.header.generators;

import java.util.Map;

public interface HeaderGenerator {
    Map<? extends String,? extends String> generateHeaders();
}
