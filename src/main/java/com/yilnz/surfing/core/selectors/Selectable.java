package com.yilnz.surfing.core.selectors;

import com.sun.org.apache.bcel.internal.generic.Select;

import java.util.List;

public interface Selectable {

    String get();

    Selectable select(Selector selector);

    List<Selectable> nodes();
}
