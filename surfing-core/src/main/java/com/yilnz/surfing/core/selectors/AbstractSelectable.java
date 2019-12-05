package com.yilnz.surfing.core.selectors;

public abstract class AbstractSelectable implements Selectable {

    @Override
    public String toString() {
        return this.get();
    }

    @Override
    public Integer getInt() {
        return Integer.parseInt(this.get());}

}
