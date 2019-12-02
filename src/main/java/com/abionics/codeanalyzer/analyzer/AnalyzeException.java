package com.abionics.codeanalyzer.analyzer;

import java.io.IOException;

class AnalyzeException extends IOException {
    AnalyzeException(String method, String message) {
        super(method + ": " + message);
    }
}
