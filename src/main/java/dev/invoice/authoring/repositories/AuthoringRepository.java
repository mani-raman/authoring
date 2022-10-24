package dev.invoice.authoring.repositories;

import dev.invoice.authoring.models.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AuthoringRepository {
    List<Draft> drafts = new ArrayList<>();
    List<Invoice> invoices = new ArrayList<>();
    List<Feedback> feedbacks = new ArrayList<>();
    List<Approval> approvals = new ArrayList<>();

    public void DraftRepository(){

        invoices.add(getExistingInvoice());
        invoices.add(getOverOneThousandInvoice());
    }

    public Draft createDraft(Draft input){
        Invoice existingInvoice = findInvoiceById(input.invoice().id());
        Draft draft;

        if (existingInvoice == null)
        {
            draft = new Draft(
                    UUID.randomUUID().toString(),
                    input.author(),
                    new Invoice(
                            UUID.randomUUID().toString(),
                            input.invoice().title(),
                            input.invoice().description(),
                            input.invoice().payee(),
                            input.invoice().payer(),
                            input.invoice().items(),
                            input.invoice().surcharges())
            );

        }
        else {
            draft = new Draft(UUID.randomUUID().toString(), input.author(), input.invoice());
        }
        drafts.add(draft);

        return draft;
    }

    public Feedback createFeedback(String actor, Draft draft){
        var feedback = new Feedback(actor, draft);

        feedbacks.add(feedback);

        return feedback;
    }

    public Approval createApproval(String actor, Draft draft){
        var approval = new Approval(actor, draft);

        approvals.add(approval);

        return approval;
    }

    public List<Draft> findDraftById(String id){
        return drafts.stream().filter(stream -> stream.id().equals(id)).toList();
    }

    public List<Draft> findPendingDraftsByAuthor(String author){
        var draftsByAuthor = findDraftByAuthor(author);
        List<Draft> pendingDrafts = new ArrayList<>();

        for (Draft draft : draftsByAuthor){
            if (findAnyFeedbacksForDraft(draft).stream().count() > 0) continue;
            if (findAnyApprovalsForDraft(draft).stream().count() > 0) continue;

            pendingDrafts.add(draft);
        }

        return pendingDrafts;
    }


    private List<Draft> findDraftByAuthor(String author){
        return drafts.stream().filter(stream -> stream.author().equals(author)).toList();
    }

    private List<Feedback> findAnyFeedbacksForDraft(Draft draft){
        return feedbacks.stream().filter(f -> f.draft().id().equals(draft.id())).toList();
    }

    private List<Approval> findAnyApprovalsForDraft(Draft draft){
        return approvals.stream().filter(a -> a.draft().id().equals(draft.id())).toList();
    }


    private Invoice findInvoiceById(String id){
        return invoices.stream().filter(stream -> stream.id().equals(id)).findFirst().orElse(null);
    }
    private Invoice getExistingInvoice(){
        List<Item> items = new ArrayList<>();
        items.add(new Item("Acme Giant Rubber Band", 1, (float) 19.99));
        Invoice invoice = new Invoice(
                "497f6eca-6276-4993-bfeb-53cbbbba6f08",
                "PO ACME CORP WEC 91949",
                "August Invoice for delivered goods",
                "228f4de6-59c3-413c-b038-4a81f809e468",
                "b5c75cf0-4c0e-486c-b014-7b1599397646",
                items,
                new ArrayList<>());

        return invoice;
    }

    private Invoice getOverOneThousandInvoice(){
        List<Item> items = new ArrayList<>();
        items.add(new Item("Acme Giant Rubber Band", 2, (float) 19.99));
        items.add(new Item("Acme Explosive Tennis Balls", 48, (float) 10.00));

        List<Surcharge> surcharges = new ArrayList<>();
        surcharges.add(new Surcharge("Priority Delivery Fee", 50, (float) 10.00));
        surcharges.add(new Surcharge("Dangerous Material Fee", 48, (float) 5.00));

        Invoice invoice = new Invoice(
                "497f6eca-6276-4993-bfeb-53cbbbba6f08",
                "PO ACME CORP WEC 91949",
                "August Invoice for delivered goods",
                "228f4de6-59c3-413c-b038-4a81f809e468",
                "b5c75cf0-4c0e-486c-b014-7b1599397646",
                items,
                surcharges);

        return invoice;
    }
}
