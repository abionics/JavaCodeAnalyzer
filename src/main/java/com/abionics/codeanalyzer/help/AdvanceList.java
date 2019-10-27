package com.abionics.codeanalyzer.help;

import java.util.ArrayList;

public class AdvanceList<T> extends ArrayList<T> {
    public T last() {
        return get(size() - 1);
    }

    public int find(T item) {
        for (int i = 0; i < size(); i++)
            if (get(i).equals(item)) return i;
        return -1;
    }

    public T pop() {
        return remove(size() - 1);
    }
}
