package dev.invoice.authoring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.invoice.authoring.controllers.DraftController;
import dev.invoice.authoring.models.Draft;
import dev.invoice.authoring.models.Invoice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class AuthoringApplicationTests {

    @Autowired
    DraftController _draftController;


    @Test
    void create_draft_for_existing_invoice() {
        String author = UUID.randomUUID().toString();
        Invoice existingInvoice = getExistingInvoiceFromString();
        Draft draft = new Draft(UUID.randomUUID().toString(), author, existingInvoice);

        var newDraft = _draftController.create(draft);
        var pendingDrafts = _draftController.findPendingByAuthor(author);

        Assertions.assertEquals(existingInvoice.id(), newDraft.invoice().id());
        Assertions.assertEquals(1, pendingDrafts.stream().count());
    }

    @Test
    void create_draft_for_new_invoice(){
        String author = UUID.randomUUID().toString();
        Invoice newInvoice = getNewInvoiceFromString();
        Draft draft = new Draft(UUID.randomUUID().toString(), author, newInvoice);

        var newDraft = _draftController.create(draft);
        var pendingDrafts = _draftController.findPendingByAuthor(author);

        Assertions.assertEquals(newInvoice.id(), null);
        Assertions.assertFalse(newDraft.invoice().id() == null);
        Assertions.assertEquals(1, pendingDrafts.stream().count());
    }

    private Invoice getExistingInvoiceFromString() {
        String input = """
    {
        "id": "5059ab7f-d7a3-46c7-81d2-de8cd828ead5",
        "title": "PO ACME CORP WEC 91949",
        "description": "August Invoice for delivered goods",
        "payee": "228f4de6-59c3-413c-b038-4a81f809e468",
        "payer": "b5c75cf0-4c0e-486c-b014-7b1599397646",
        "items": [
            {
                "name": "Acme Giant Rubber Band",
                "quantity": 1,
                "cost": 19.99
            }
        ],
        "surcharges": []
    }
                            """;

        ObjectMapper mapper = new ObjectMapper();
        Invoice invoice;
        try {
            invoice = mapper.readValue(input, Invoice.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return invoice;
    }

    private Invoice getNewInvoiceFromString() {
        String input = """
  {
    "title": "PO ACME CORP WEC 91949",
    "description": "August Invoice for delivered goods",
    "payee": "228f4de6-59c3-413c-b038-4a81f809e468",
    "payer": "b5c75cf0-4c0e-486c-b014-7b1599397646",
    "items": [
      {
        "name": "Acme Explosive Tennis Balls",
        "quantity": 24,
        "cost": 10
      }
    ],
    "surcharges": []
  }
                            """;

        ObjectMapper mapper = new ObjectMapper();
        Invoice invoice;
        try {
            invoice = mapper.readValue(input, Invoice.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return invoice;
    }
}
