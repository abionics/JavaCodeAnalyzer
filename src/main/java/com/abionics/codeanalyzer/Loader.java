package com.abionics.codeanalyzer;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static com.abionics.codeanalyzer.Controller.sources;

class Loader {
    static void load(@NotNull String[] links) throws Exception {
        int k = 0;
        for (var link : links) {
            System.out.println("\nLoading " + (100 * k++ / links.length) + "% - " + link);
            if (link.contains("github.com")) loadGithub(link);
            else if (Files.exists(new File(link).toPath())) loadLocal(link);
            else throw new Exception("Loader::load: undefined link " + link);
            System.out.println("Loaded " + (100 * k / links.length) + "%");
        }
    }

    private static void loadGithub(@NotNull String link) throws IOException {
        var parts = link.split("/");
        link = "https://api.github.com/repos/" + parts[3] + "/" + parts[4] + "/zipball/master";

        final String REPOS = "/repos/";
        int repos = link.indexOf(REPOS) + REPOS.length();
        int separator = link.indexOf('/', repos);
        int end = link.indexOf('/', separator + 1);
        String author = link.substring(repos, separator);
        String repo = link.substring(separator + 1, end);

        String title = author + "_" + repo;
        String zip = sources + title + "/archive.zip";
        String resource = sources + title;
        Controller.mkdirs(resource);

        System.out.println("\tLoading");
        FileUtils.copyURLToFile(new URL(link), new File(zip));
        System.out.println("\tUnzipping");
        unzip(zip, resource);
        Controller.delete(zip);
    }

    private static void loadLocal(@NotNull String link) throws IOException {
        System.out.println("\tLoading");
        File file = new File(link);
        FileUtils.copyDirectory(file, new File(sources + file.getName()));
    }

    private static void unzip(String source, String destination) throws ZipException {
        ZipFile file = new ZipFile(source);
        file.extractAll(destination);
    }
}
