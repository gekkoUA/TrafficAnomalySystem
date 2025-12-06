package com.traffic.analysis.service;

import org.springframework.stereotype.Component;

@Component
public class ConsoleLogger implements Logger {
    @Override
    public void log(String message) {
        System.out.println(message);
    }
}