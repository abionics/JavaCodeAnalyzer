package com.abionics.codeanalyzer;

import com.abionics.codeanalyzer.analyzer.Analyzer;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static final String preload = "data/preload.txt";
    static final String sources = "data/sources/";
    public static final String results = "data/results/";

    private final static JTextArea area = new JTextArea();

    static {
        try {
            mkdirs(preload.substring(0, preload.indexOf('/')));
            var preloadPath = Paths.get(preload);
            if (Files.notExists(preloadPath)) Files.createFile(preloadPath);
            area.setFont(new Font(area.getFont().getName(), Font.PLAIN, 10));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void preload() throws IOException {
        var list = Files.readAllLines(Paths.get(preload));
        var value = String.join("\n", list);
        area.setText(value);
    }

    public static void main(String[] args) throws IOException {
        gui();
        preload();
    }

    private static void gui() {
        JFrame frame = new JFrame("Code Analyzer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 550);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.add(panel);

        JLabel title = new JLabel("Locations of projects:", SwingConstants.CENTER);
        JButton run = new JButton("Load and analyze");
        JButton load = new JButton("Load");
        JButton analyze = new JButton("Analyze");

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        area.setAlignmentX(Component.CENTER_ALIGNMENT);
        run.setAlignmentX(Component.CENTER_ALIGNMENT);
        load.setAlignmentX(Component.CENTER_ALIGNMENT);
        analyze.setAlignmentX(Component.CENTER_ALIGNMENT);

        run.addActionListener(e -> run());
        load.addActionListener(e -> load());
        analyze.addActionListener(e -> analyze());

        panel.add(title);
        panel.add(area);
        panel.add(run);
        panel.add(load);
        panel.add(analyze);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void run() {
        load();
        analyze();
    }

    private static void load() {
        try {
            delete(sources);
            mkdirs(sources);
            var links = area.getText().split("\n");
            Loader.load(links);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void analyze() {
        try {
            delete(results);
            mkdirs(results);
            var writer = new BufferedWriter(new FileWriter(results + "kulakovskaya.txt"));
            writer.write("LOC\tNC\tANA\tANM\tANSM\tANGM\tANCM\tNinh\n");
            writer.close();

            var files = new File(sources).listFiles();
            if (files == null) throw new Exception("Main::launch: there is no files in source directory " + sources);
            int k = 0;
            for (var file : files) {
                System.out.println("\nProcessing " + (100 * k++ / files.length) + "% - " + file.getName());
                var analyzer = new Analyzer(file);
                System.out.println("Analyzed " + (100 * k / files.length) + "% - " + file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    static void delete(String path) throws IOException {
        if (Files.exists(Paths.get(path))) FileUtils.forceDelete(new File(path));
    }

    public static void mkdirs(String path) {
        new File(path).mkdirs();
    }
}
