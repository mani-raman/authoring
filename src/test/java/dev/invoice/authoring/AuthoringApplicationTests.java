package dev.invoice.authoring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.invoice.authoring.controllers.*;
import dev.invoice.authoring.models.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
class AuthoringApplicationTests {

    @Autowired
    DraftController _draftController;
    @Autowired
    ApprovalController _approvalController;

    @Test
    void create_draft_for_existing_invoice() {
        String author = UUID.randomUUID().toString();
        Invoice existingInvoice = getExistingInvoiceFromString();
        Draft draft = new Draft(UUID.randomUUID().toString(), author, existingInvoice);

        var newDraft = (Draft) _draftController.create(draft).Response;
        var pendingDrafts = (List<Draft>)_draftController.findPendingByAuthor(author).Response;

        Assertions.assertEquals(existingInvoice.id(), newDraft.invoice().id());
        Assertions.assertEquals(1, (long) pendingDrafts.size());
    }

    @Test
    void create_draft_for_new_invoice(){
        String author = UUID.randomUUID().toString();
        Invoice newInvoice = getNewInvoiceFromString();
        Draft draft = new Draft(UUID.randomUUID().toString(), author, newInvoice);

        var newDraft = (Draft) _draftController.create(draft).Response;
        var pendingDrafts = (List<Draft>) _draftController.findPendingByAuthor(author).Response;

        Assertions.assertNull(newInvoice.id());
        Assertions.assertNotNull(newDraft.invoice().id());
        Assertions.assertEquals(1, (long) pendingDrafts.size());
    }

    @Test
    void approve_normal_draft(){
        String author = UUID.randomUUID().toString();
        Invoice newInvoice = getNewInvoiceFromString();
        Draft draft = new Draft(UUID.randomUUID().toString(), author, newInvoice);

        var newDraft = (Draft)_draftController.create(draft).Response;
        var pendingDrafts = (List<Draft>)_draftController.findPendingByAuthor(author).Response;

        Assertions.assertEquals(1, (long) pendingDrafts.size());

        String actor = UUID.randomUUID().toString();
        Approval response = (Approval) _approvalController.create(new Approval(actor, newDraft)).Response;

        Assertions.assertNotNull(response); //ensure there is an approval for normal draft.
        Assertions.assertEquals(response.actor(), actor);

        pendingDrafts = (List<Draft>) _draftController.findPendingByAuthor(author).Response;
        Assertions.assertEquals(0, (long) pendingDrafts.size());
    }

    @Test
    void approve_draft_over_limit(){
        String author = UUID.randomUUID().toString();
        Invoice dangerousInvoice = getDangerousInvoiceFromString();

        Assertions.assertFalse(dangerousInvoice.satisfiesApprovalLimit(1000));

        Draft draft = new Draft(UUID.randomUUID().toString(), author, dangerousInvoice);

        var newDraft = (Draft) _draftController.create(draft).Response;
        var pendingDrafts = (List<Draft>) _draftController.findPendingByAuthor(author).Response;

        Assertions.assertEquals(1, (long) pendingDrafts.size());

        String actor = UUID.randomUUID().toString();
        var response = _approvalController.create(new Approval(actor, newDraft)).Response;

        pendingDrafts = (List<Draft>) _draftController.findPendingByAuthor(author).Response; //Still pending after one approval.
        Assertions.assertEquals(1, (long) pendingDrafts.size());

        var differentActor = UUID.randomUUID().toString();
        response = _approvalController.create(new Approval(differentActor, newDraft)).Response; // create approval with a different Actor

        Approval approval = (Approval) response;
        Assertions.assertEquals(approval.actor(), differentActor);

        pendingDrafts = (List<Draft>) _draftController.findPendingByAuthor(author).Response;
        Assertions.assertEquals(0, (long) pendingDrafts.size()); //invoice now approved.
    }


    private Invoice getDangerousInvoiceFromString() {
        String input = """
{
    "id": "8ae7d8f3-2242-4237-bcc8-df654c502d45",
    "title": "PO ACME CORP WEC 91949",
    "description": "August Invoice for delivered goods",
    "payee": "228f4de6-59c3-413c-b038-4a81f809e468",
    "payer": "b5c75cf0-4c0e-486c-b014-7b1599397646",
    "items": [
      {
        "name": "Acme Giant Rubber Band",
        "quantity": 2,
        "cost": 19.99
      },
       {
        "name": "Acme Explosive Tennis Balls",
        "quantity": 48,
        "cost": 10
      }
    ],
    "surcharges": [
      {
        "name": "Priority Delivery Fee",
        "quantity": 50,
        "cost": 10
      },
      {
        "name": "Dangerous Material Fee",
        "quantity": 48,
        "cost": 5
      }
    ]
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
