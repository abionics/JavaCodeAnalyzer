package com.abionics.codeanalyzer.analyzer;

import org.jetbrains.annotations.Contract;

import java.util.EnumSet;

import static com.abionics.codeanalyzer.analyzer.Type.*;

public enum Modifiers {
    PUBLIC(EnumSet.of(CLASS, INTERFACE, ENUM, METHOD, VARIABLE)),       // +++++
    PRIVATE(EnumSet.of(CLASS, INTERFACE, ENUM, METHOD, VARIABLE)),      // +++++
    PROTECTED(EnumSet.of(CLASS, INTERFACE, ENUM, METHOD, VARIABLE)),    // +++++
    STATIC(EnumSet.of(CLASS, INTERFACE, ENUM, METHOD, VARIABLE)),       // +++++
    FINAL(EnumSet.of(CLASS, METHOD, VARIABLE)),                         // +--++
    ABSTRACT(EnumSet.of(CLASS, INTERFACE, METHOD)),                     // ++-+-
    DEFAULT(EnumSet.of(METHOD)),                                        // ---+-
    SYNCHRONIZED(EnumSet.of(METHOD)),                                   // ---+-
    NATIVE(EnumSet.of(METHOD)),                                         // ---+-
    TRANSIENT(EnumSet.of(VARIABLE)),                                    // ----+
    VOLATILE(EnumSet.of(VARIABLE)),                                     // ----+
    STRICTFP(EnumSet.of(CLASS, INTERFACE, ENUM, METHOD));               // ++++-

    final EnumSet<Type> suits;

    @Contract(pure = true)
    Modifiers(EnumSet<Type> suits) {
        this.suits = suits;
    }
}
