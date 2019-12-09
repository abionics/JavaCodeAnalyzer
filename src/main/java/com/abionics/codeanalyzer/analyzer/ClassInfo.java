package com.abionics.codeanalyzer.analyzer;

import java.util.ArrayList;
import java.util.EnumSet;

class ClassInfo {
    final String name;
    final Type type;
    final EnumSet<Modifiers> modifiers;
    final int ancestors;
    final ArrayList<MethodInfo> methods;
    final ArrayList<VariableInfo> variables;
    int constructors;
    private boolean hasDefaultConstructor;


    ClassInfo(String name, Type type, EnumSet<Modifiers> modifiers, int ancestors) {
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
        this.ancestors = ancestors;
        methods = new ArrayList<>();
        variables = new ArrayList<>();
        constructors = 1;
        hasDefaultConstructor = true;
    }

    void addMethod(MethodInfo method) {
        methods.add(method);
        if (method.name.equals(name)) {
            constructors++;
            if (hasDefaultConstructor) {
                constructors--;
                hasDefaultConstructor = false;
            }
        }
    }

    void addVariable(VariableInfo variable) {
        variables.add(variable);
    }
}
