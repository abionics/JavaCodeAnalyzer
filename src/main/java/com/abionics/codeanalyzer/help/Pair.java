package com.abionics.codeanalyzer.help;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Pair<T1,T2> {
    public T1 first;
    public T2 second;
    @Contract(pure = true)
    public Pair(T1 _first, T2 _second) {
        first = _first;
        second = _second;
    }
    @Contract(pure = true)
    public Pair(@NotNull Pair<T1,T2> pair) {
        first = pair.first;
        second = pair.second;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var pair = (Pair) o;
        return first == pair.first && second == pair.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first) ^ Objects.hash(second);
    }

    @Override
    public String toString() {
        return "[" + first + ";" + second + "]";
    }
}
