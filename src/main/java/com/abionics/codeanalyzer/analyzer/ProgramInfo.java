package com.abionics.codeanalyzer.analyzer;

import com.abionics.codeanalyzer.help.AdvanceList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

class ProgramInfo {
    int files;
    int lines;
    int blank;

    private boolean backslash;
    private boolean quotes;
    private boolean comments;

    private StringBuilder word;
    private AdvanceList<String> words;
    private ArrayList<AdvanceList<String>> logicals;

    ArrayList<ClassInfo> classes;


    ProgramInfo() {
        reset();
    }

    private void reset() {
        files = 0;
        lines = 0;
        blank = 0;
        cleanup();
        classes = new ArrayList<>();
    }

    private void cleanup() {
        backslash = false;
        quotes = false;
        comments = false;

        word = new StringBuilder();
        words = new AdvanceList<>();
        logicals = new ArrayList<>();
    }

    void analyze(@NotNull List<String> code) throws Exception {
        cleanup();
        files++;
        for (String line : code) {
            line = line.trim();
            lines++;
            if (line.isEmpty()) {
                blank++;
                continue;
            }
            analyze(line);
        }
        analyze();
    }

    private void analyze(String line) throws Exception {
        if (backslash) throw new Exception("Analyzer::analyze: invalid backslash, line = " + lines);
        if (quotes) throw new Exception("Analyzer::analyze: all quotes must be closed, line = " + lines);
        word.setLength(0);
        split(line);
    }

    private void split(String line) {
        line += " ";
        Character last = '\n';
        for (Character letter : line.toCharArray()) {
            if (comments) {
                if (last == '*' && letter == '/') comments = false;
            } else if (letter == '\\') {
                backslash = !backslash;
            } else if (backslash) {
                backslash = false;
            } else if (letter == '\"') {
                quotes = !quotes;
                if (quotes) word();
            } else if (!quotes) {
                if (last == '/' && letter == '/') break;
                if (last == '/' && letter == '*') comments = true;
                if (letter == ' ' || !isIdentification(last) || !isIdentification(letter)) {
                    word();
                }
            }
            word.append(letter);
            last = letter;
        }
    }

    private void word() {
        String current = word.toString().trim();
        if (!current.isEmpty()) {
            words.add(current);
            if (current.equals(";") || current.equals("{") || current.equals("}")) {
                logicals.add((AdvanceList<String>) words.clone());
                words.clear();
            }
        }
        word.setLength(0);
    }

    private static boolean isIdentification(char letter) {
        return Character.isLetter(letter) || Character.isDigit(letter) || letter == '_';
    }

    private void analyze() throws Exception {
        int level = 0;
        AdvanceList tree = new AdvanceList();

        int q = -1;
        for (var words : logicals) {
//            System.out.println(words.toString());
            q++;
            String end = words.last();
            boolean isClass = tree.size() > 0 && tree.last() instanceof ClassInfo;
            switch (end) {
                case "{":
                    if (level == tree.size() && words.size() > 1) {
                        // class|interface|enum|method|none
                        Type type = null;
                        if (words.contains("class")) type = Type.CLASS;
                        if (words.contains("interface")) type = Type.INTERFACE;
                        if (words.contains("enum")) type = Type.ENUM;
                        if (type != null) {
                            int pos = words.size() - 1;
                            if (words.contains("extends")) pos = words.find("extends");
                            if (words.contains("implements")) pos = words.find("implements");
                            var name = words.get(pos - 1);
                            for (int i = 0; i < words.size(); i++)
                                if (words.get(i).equals("<")) {
                                    name = words.get(i - 1);
                                    break;
                                }
                            int ancestors = (pos == words.size() - 1) ? 0 : 1;
                            var clazz = new ClassInfo(name, type, getModifiers(words), ancestors + getCount(words));
                            classes.add(clazz);
                            tree.add(clazz);
                        } else if (isClass) {
                            // method
                            var clazz = (ClassInfo) tree.last();
                            int pos = words.find("(");
                            if (pos != -1) { // not lambda
                                var name = words.get(pos - 1);
                                boolean isThrows = words.contains("throws");
                                int endpos = words.size() - 1;
                                if (isThrows) endpos = words.find("throws");
                                int params = (words.find(")") - words.find("(") > 1) ? 1 : 0;
                                var method = new MethodInfo(name, getModifiers(words), params + getCount(words.subList(0, endpos)), isThrows);
                                clazz.addMethod(method);
                                tree.add(method);
                            }
                        }
                    }
                    level++;
                    break;
                case ";":
                    // variable
                    if (isClass && level == tree.size()) {
                        var clazz = (ClassInfo) tree.last();
                        int count = getCount(words) + 1;
                        for (int i = 0; i < count; i++) {
                            var variable = new VariableInfo(getModifiers(words));
                            clazz.addVariable(variable);
                        }
                    }
                    break;
                case "}":
                    if (level == tree.size()) tree.pop();
                    level--;
                    break;
                default: throw new Exception("Analyze::analyze: invalid end of logical (words), logical = " + q);
            }
        }

        if (level != 0) throw new Exception("Analyze::analyze: '{' or '}' exception, level = " + level);
    }

    @NotNull
    private EnumSet<Modifiers> getModifiers(@NotNull List<String> words) {
        EnumSet<Modifiers> modifiers = EnumSet.noneOf(Modifiers.class);
        for (var word : words) {
            if (isModifier(word)) {
                Modifiers modifier = Modifiers.valueOf(word.toUpperCase());
                modifiers.add(modifier);
            }
        }
        return modifiers;
    }

    private int getCount(@NotNull List<String> words) {
        int count = 0;
        for (var word : words) {
            count += word.chars().filter(c -> c == (int)',').count();
        }
        return count;
    }

    private boolean isModifier(String word) {
        for (Modifiers modifier : Modifiers.values())
            if (word.equals(modifier.name().toLowerCase()))
                return true;
        return false;
    }
}
