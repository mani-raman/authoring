package dev.invoice.authoring.services;

import dev.invoice.authoring.models.ApplicationResponse;
import dev.invoice.authoring.models.Approval;
import dev.invoice.authoring.repositories.AuthoringRepository;
import org.springframework.stereotype.Component;

@Component
public class ApprovalService {
    private final AuthoringRepository _authoringRepository;

    public ApprovalService(AuthoringRepository authoringRepository){
        _authoringRepository = authoringRepository;
    }

    public ApplicationResponse create(Approval request){
        var response = _authoringRepository.requestApproval(request.actor(), request.draft().id());

        return new ApplicationResponse(response);
    }
}
