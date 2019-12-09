package com.abionics.codeanalyzer.analyzer;

import org.jetbrains.annotations.Contract;

import java.util.EnumSet;

class MethodInfo {
    final String name;
    final EnumSet<Modifiers> modifiers;
    final int params;
    final boolean isThrows;


    @Contract(pure = true)
    MethodInfo(String name, EnumSet<Modifiers> modifiers, int params, boolean isThrows) {
        this.name = name;
        this.modifiers = modifiers;
        this.params = params;
        this.isThrows = isThrows;
    }
}
