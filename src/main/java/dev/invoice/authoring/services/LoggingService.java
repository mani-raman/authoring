package dev.invoice.authoring.services;

import org.springframework.stereotype.Component;

@Component
public class LoggingService {
    public void Write(String message) {
        System.out.println(message);
    }
}
