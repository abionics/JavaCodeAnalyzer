package com.abionics.codeanalyzer;

import com.abionics.codeanalyzer.analyzer.Analyzer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Controller {
    @FXML private TextArea textArea;

    public static final String preload = "data/preload.txt";
    public static final String sources = "data/sources/";
    public static final String results = "data/results/";
    public static final String kulakovska = results + "kulakovska.txt";


    @FXML private void initialize() throws IOException {
        mkdirs(preload.substring(0, preload.indexOf('/')));
        var preloadPath = Paths.get(preload);
        if (Files.notExists(preloadPath)) Files.createFile(preloadPath);
        preload();
    }

    private void preload() throws IOException {
        var list = Files.readAllLines(Paths.get(preload));
        var value = String.join("\n", list);
        textArea.setText(value);
    }

    @FXML private void load() throws Exception {
        delete(sources);
        mkdirs(sources);
        var links = textArea.getText().split("\n");
        Loader.load(links);
    }

    @FXML private void analyze() throws Exception {
        delete(results);
        mkdirs(results);
        kulakovskaInit();

        var files = new File(sources).listFiles();
        if (files == null) throw new Exception("Main::launch: there is no files in source directory " + sources);
        int k = 0;
        for (var file : files) {
            System.out.println("\nProcessing " + (100 * k++ / files.length) + "% - " + file.getName());
            var analyzer = new Analyzer(file);
            analyzer.output();
            System.out.println("Analyzed " + (100 * k / files.length) + "% - " + file.getName());
        }
    }

    @FXML private void loadAndAnalyze() throws Exception {
        load();
        analyze();
    }

    private static void kulakovskaInit() throws IOException {
        var writer = new BufferedWriter(new FileWriter(kulakovska));
        writer.write("LOC\tNC\tANA\tANM\tANSM\tANGM\tANCM\tNinh\n");
        writer.close();
    }

    static void delete(String path) throws IOException {
        if (Files.exists(Paths.get(path))) FileUtils.forceDelete(new File(path));
    }

    public static void mkdirs(String path) {
        new File(path).mkdirs();
    }
}
