package dev.invoice.authoring.repositories;

import dev.invoice.authoring.models.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class AuthoringRepository {
    private final int INVOICE_LIMIT = 1000;
    List<Draft> _drafts = new ArrayList<>();
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
            if (!draftHasBeenApproved(draft)) //no feedbacks or approvals.
                pendingDrafts.add(draft);
        }

        return pendingDrafts;
    }

    public Object requestApproval(String actor, String draftId){

        var draft = findDraftById(draftId);

        if (draft == null) throw new NotFoundException();

        var approval = new Approval(actor, draft);

        _approvals.add(approval);

        return approval;
    }

    private boolean draftHasBeenApproved(Draft draft){
        var approvals = _approvals.stream().filter(a -> a.draft().id().equals(draft.id())).toList();

        if (draft.invoice().satisfiesApprovalLimit(INVOICE_LIMIT)) //invoice under limit
        {
            return approvals.size() > 0;
        }
        else
        {
            Map<String, Long> approvalsByActors = approvals.stream().collect(Collectors.groupingBy(Approval::actor, Collectors.counting()));

            return (long) approvalsByActors.entrySet().size() >= 2;
        }
    }
}
