package dev.invoice.authoring.models;

import java.util.UUID;

public class Id {
    private String id;

    public Id() {
        if (id == null){
            id = UUID.randomUUID().toString();
        }
    }
}
