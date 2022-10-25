package dev.invoice.authoring.controllers;

import dev.invoice.authoring.models.Approval;
import dev.invoice.authoring.repositories.AuthoringRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice/authoring/v1/approvals")
public class ApprovalController {

    private final AuthoringRepository authoringRepository;

    public ApprovalController(AuthoringRepository authoringRepository) {
        this.authoringRepository = authoringRepository;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Object create(@RequestBody final Approval request){
        return authoringRepository.requestApproval(request.actor(), request.draft());
    }
}
