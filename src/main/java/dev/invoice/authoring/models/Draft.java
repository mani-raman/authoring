package dev.invoice.authoring.models;

import java.util.UUID;

public record Draft(
        String id,
        String author,
        Invoice invoice
) {
}
