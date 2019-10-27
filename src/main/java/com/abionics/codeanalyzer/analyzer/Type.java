package com.abionics.codeanalyzer.analyzer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Type {
    CLASS, INTERFACE, ENUM, METHOD, VARIABLE;

    @NotNull
    @Contract(value = " -> new", pure = true)
    static Type[] classValues() {
        return new Type[]{CLASS, INTERFACE, ENUM};
    }
}
