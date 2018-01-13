package com.lmonkiewicz.spring.analyzer.domain.exception;

public class AnalyzerException extends RuntimeException {
    public AnalyzerException(String message, Throwable t) {
        super(message, t);
    }
}
