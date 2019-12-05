package com.yilnz.surfing.core.selectors;

import java.util.List;

public interface Selectable {

    String get();

    Selectable select(Selector selector);

    List<Selectable> nodes();

    Integer getInt();
}
