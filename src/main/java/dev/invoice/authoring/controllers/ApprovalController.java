package dev.invoice.authoring.controllers;

import dev.invoice.authoring.models.ApplicationResponse;
import dev.invoice.authoring.models.Approval;
import dev.invoice.authoring.models.Feedback;
import dev.invoice.authoring.repositories.AuthoringRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice/authoring/v1")
public class ApprovalController {

    private final AuthoringRepository authoringRepository;

    public ApprovalController(AuthoringRepository authoringRepository) {
        this.authoringRepository = authoringRepository;
    }

    @PostMapping(path = "/approvals")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse create(@RequestBody final Approval request){
        var response = authoringRepository.requestApproval(request.actor(), request.draft().id());

        if (response == null) return new ApplicationResponse<Approval>(null, "Draft already approved.");;

        if (response instanceof Approval){
            return new ApplicationResponse<Approval>(response, "This is an approval.");
        }

        if (response instanceof Feedback)
            return new ApplicationResponse<Feedback>(response, "This is an feedback!");

        return new ApplicationResponse(response);
    }
}
