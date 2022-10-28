package dev.invoice.authoring.services;

import dev.invoice.authoring.models.ApplicationResponse;
import dev.invoice.authoring.models.Draft;
import dev.invoice.authoring.repositories.AuthoringRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DraftService {
    private final AuthoringRepository _authoringRepository;

    public DraftService(AuthoringRepository authoringRepository){

        this._authoringRepository = authoringRepository;
    }

    public ApplicationResponse<Draft> create(Draft request){
        var draft = _authoringRepository.createDraft(request);

        return new ApplicationResponse(draft);
    }

    public ApplicationResponse<List<Draft>> findPendingByAuthor(String author){
        var drafts = _authoringRepository.findPendingDraftsByAuthor(author);

        return new ApplicationResponse(drafts);
    }
}
