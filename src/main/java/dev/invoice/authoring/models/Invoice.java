package dev.invoice.authoring.models;

import java.util.List;
import java.util.UUID;

public record Invoice(String id, String title, String description, String payee, String payer, List<Item> items, List<Surcharge> surcharges) {
}
