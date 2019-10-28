package com.abionics.codeanalyzer.analyzer;

import com.abionics.codeanalyzer.Main;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import static com.abionics.codeanalyzer.Main.results;

public class Analyzer {
    private final String output;
    private final ProgramInfo program;

    public Analyzer(@NotNull File directory) throws Exception {
        String name = directory.getName();
        output = results + name + "/";
        program = new ProgramInfo(name);
        analyze(directory);
        output();
    }

    private void analyze(@NotNull File directory) throws Exception {
        var files = directory.listFiles();
        if (files == null) return;
        for (var file : files) {
            if (file.isDirectory()) analyze(file);
            else if (getExtension(file).equals("java")) {
                var path = file.getPath().substring(output.length());
                System.out.println("\t" + path.substring(path.indexOf('/') + 1));
                try {
                    var code = Files.readAllLines(file.toPath());
                    program.analyze(code);
                } catch (MalformedInputException e) {
                    System.out.println("[WARNING] File encoding does not supported (" + e.getMessage() + "), we try to use ISO_8859_1");
                    var code = Files.readAllLines(file.toPath(), StandardCharsets.ISO_8859_1);
                    program.analyze(code);
                }
            }
        }
    }

    private void output() throws IOException {
        Main.mkdirs(output);
        mainInfo();
        classesInfo();
        statisticInfo();
        kulakovskayaInfo();
    }

    private void mainInfo() throws IOException {
        var writer = new BufferedWriter(new FileWriter(output + "main.txt"));
        int files = program.files;
        int lines = program.lines;
        int nonblanks = lines - program.blank;
        writer.write("Total files: " + files + "\n");
        writer.write("Total lines: " + lines + " (" + lines / files + " per file)\n");
        writer.write("Total non-blank lines: " + nonblanks + " (" + 100 * nonblanks / lines +"%)\n");
        writer.close();
    }

    private void classesInfo() throws IOException {
        var writer = new BufferedWriter(new FileWriter(output + "classes.txt"));
        HashMap<Type, ArrayList<ClassInfo>> classes = new HashMap<>();
        for (var type : Type.classValues()) {
            classes.put(type, new ArrayList<>());
        }
        for (var clazz : program.classes) {
            var list = classes.get(clazz.type);
            list.add(clazz);
        }

        for (var type : Type.classValues()) {
            writer.write("\t\t\t\t\t---" + type + "---\n");
            for (var clazz : classes.get(type)) {
                writer.write(clazz.name + ":\n");
                writer.write("\t" + clazz.constructors + " constructors, " + clazz.methods.size() + " methods, " + clazz.variables.size() + " variables | " + clazz.ancestors + " ancestors |");
                for (var modifier : clazz.modifiers) {
                    writer.write(" " + modifier);
                }
                writer.write("\n");
                writer.write("\tMethods:\n");
                for (var method : clazz.methods) {
                    var throws_ = method.isThrows ? "| throws " : "";
                    writer.write("\t\t" + method.name + " | " + method.params + " params " + throws_ + "|");
                    for (var modifier : method.modifiers) {
                        writer.write(" " + modifier);
                    }
                    writer.write("\n");
                }
                writer.write("\tVariables:\n");
                for (var variable : clazz.variables) {
                    writer.write("\t\tvar");
                    for (var modifier : variable.modifiers) {
                        writer.write(" " + modifier);
                    }
                    writer.write("\n");
                }
                writer.write("\n");
            }
            writer.write("\n");
        }
        writer.close();
    }

    private void statisticInfo() throws IOException {
        var writer = new BufferedWriter(new FileWriter(output + "statistic.txt"));
        int[] counts = new int[Type.values().length];
        int[][] statistic = new int[Type.values().length][Modifiers.values().length];
        for (var clazz : program.classes) {
            counts[clazz.type.ordinal()]++;
            counts[Type.METHOD.ordinal()] += clazz.methods.size();
            counts[Type.VARIABLE.ordinal()] += clazz.variables.size();
            for (var modifier : clazz.modifiers)
                statistic[clazz.type.ordinal()][modifier.ordinal()]++;
            for (var method : clazz.methods)
                for (var modifier : method.modifiers)
                    statistic[Type.METHOD.ordinal()][modifier.ordinal()]++;
            for (var variable : clazz.variables)
                for (var modifier : variable.modifiers)
                    statistic[Type.VARIABLE.ordinal()][modifier.ordinal()]++;
        }

        for (var type : Type.values()) {
            writer.write(type + " (count = " + counts[type.ordinal()] + "):\n");
            for (var modifier : Modifiers.values())
                if (modifier.suits.contains(type)) {
                    int count = statistic[type.ordinal()][modifier.ordinal()];
                    if (count > 0) {
                        writer.write(modifier.toString().toLowerCase() + ": " + count);
                        writer.write(" (" + 100 * count / counts[type.ordinal()] + "%)\n");
                    }
                }
            writer.write("\n");
        }
        writer.close();
    }

    private void kulakovskayaInfo() throws IOException {
        var writer = new BufferedWriter(new FileWriter(results + "kulakovskaya.txt", true));
        int classes = program.classes.size();
        int variables = 0;
        int methods = 0;
        int gets = 0;
        int sets = 0;
        int constructors = 0;
        int ancestors = 0;
        for (var clazz : program.classes) {
            variables += clazz.variables.size();
            methods += clazz.methods.size();
            for (var method : clazz.methods) {
                var name = method.name;
                if (name.length() >= 3) {
                    if (method.name.substring(0, 3).equals("get")) gets++;
                    if (method.name.substring(0, 3).equals("set")) sets++;
                }
            }
            constructors += clazz.constructors;
            ancestors += clazz.ancestors;
        }
        writer.write(program.name + "\t");
        writer.write(program.lines + "\t");
        writer.write(classes + "\t");
        writer.write(round(variables, classes, 2) + "\t");
        writer.write(round(methods, classes, 2) + "\t");
        writer.write(round(gets, classes, 2) + "\t");
        writer.write(round(sets, classes, 2) + "\t");
        writer.write(round(constructors, classes, 2) + "\t");
        writer.write(ancestors + "\n");
        writer.close();
    }

    private static double round(int numerator, int denominator, int precision) {
        double value = (double) numerator / denominator;
        double power = (int) Math.pow(10, precision);
        return Math.round(value * power) / power;
    }

    @NotNull
    private static String getExtension(@NotNull File file) {
        var name = file.getName();
        int index = name.lastIndexOf('.');
        if (index < 0) return "";
        return name.substring(index + 1);
    }
}
