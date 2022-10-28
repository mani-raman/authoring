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
    List<Feedback> _feedbacks = new ArrayList<>();
    List<Approval> _approvals = new ArrayList<>();

    public Draft createDraft(Draft input){
        Draft draft;

        if (input.invoice().id() == null)
            draft = new Draft(UUID.randomUUID().toString(), input.author(), input.invoice().createNew());
        else
            draft = new Draft(UUID.randomUUID().toString(), input.author(), input.invoice());

        _drafts.add(draft);

        return draft;
    }
    public Draft findDraftById(String id){
        return _drafts.stream().filter(d -> d.id().equals(id)).findFirst().orElse(null);
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
    public Object requestApproval(String actor, String draftId){

        var draft = findDraftById(draftId);

        if (draft == null) throw new NotFoundException();

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
}
