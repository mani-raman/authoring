package dev.invoice.authoring.repositories;

import dev.invoice.authoring.models.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AuthoringRepository {
    private final int _invoiceLimit = 1000;
    List<Draft> _drafts = new ArrayList<>();
    List<Invoice> _invoices = new ArrayList<>();
    List<Feedback> _feedbacks = new ArrayList<>();
    List<Approval> _approvals = new ArrayList<>();

    public void DraftRepository(){

        _invoices.add(getExistingInvoice());
        _invoices.add(getOverOneThousandInvoice());
    }

    public Draft createDraft(Draft input){
        Draft draft;

        if (input.invoice().id() == null)
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
        _drafts.add(draft);

        return draft;
    }

    public List<Draft> findPendingDraftsByAuthor(String author){
        var draftsByAuthor = _drafts.stream().filter(stream -> stream.author().equals(author)).toList();
        List<Draft> pendingDrafts = new ArrayList<>();

        for (Draft draft : draftsByAuthor){
            if (feedbacksForDraft(draft) == 0 && approvalsForDraft(draft) == 0) //no feedbacks or approvals.
                pendingDrafts.add(draft);
        }

        return pendingDrafts;
    }

    public Object requestApproval(String actor, Draft draft){

        if (approvalsForDraft(draft) > 0) return null; //already an approval

        if (draft.invoice().satisfiesApprovalLimit(_invoiceLimit)) return addApproval(actor, draft); //limit satisfies

        if (feedbacksForDraft(draft) == 0) return addFeedback(actor, draft); //no feedback over limit so needs 2 actors.

        if (feedbacksByADifferentActor(actor, draft) == 0) return addFeedback(actor, draft); //over limit so needs 2 different actors.

        return addApproval(actor, draft); // all conditions satisfied, so approve draft over limit.
    }

    private long feedbacksForDraft(Draft draft){
        return _feedbacks.stream().filter(f -> f.draft().id().equals(draft.id())).count();
    }
    private long approvalsForDraft(Draft draft){
        return _approvals.stream().filter(a -> a.draft().id().equals(draft.id())).count();
    }
    private long feedbacksByADifferentActor(String actor, Draft draft){
        return _feedbacks.stream().filter(f -> f.draft().id().equals(draft.id()) && !f.actor().equals(actor)).count();
    }
    private Feedback addFeedback(String actor, Draft draft){
        var feedback = new Feedback(actor, draft);

        _feedbacks.add(feedback);

        return feedback;
    }
    private Approval addApproval(String actor,Draft draft){
        var approval = new Approval(actor, draft);

        _approvals.add(approval);

        return approval;
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
