package com.abionics.codeanalyzer.analyzer;

import org.jetbrains.annotations.Contract;

import java.util.EnumSet;

class VariableInfo {
    final EnumSet<Modifiers> modifiers;

    @Contract(pure = true)
    VariableInfo(EnumSet<Modifiers> modifiers) {
        this.modifiers = modifiers;
    }
}
