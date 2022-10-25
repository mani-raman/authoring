package dev.invoice.authoring.models;

import java.util.List;

public record Invoice(String id, String title, String description, String payee, String payer, List<Item> items, List<Surcharge> surcharges) {
    public boolean satisfiesApprovalLimit(int limit){
        float total = 0;
        for (Item item : items()) {
            total += (item.cost() * item.quantity());
        }

        for (Surcharge surcharge : surcharges()){
            total += (surcharge.cost() * surcharge.quantity());
        }

        return total <= limit;
    }
}
