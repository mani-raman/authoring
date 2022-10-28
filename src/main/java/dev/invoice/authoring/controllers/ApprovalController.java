package dev.invoice.authoring.controllers;

import dev.invoice.authoring.models.ApplicationResponse;
import dev.invoice.authoring.models.Approval;
import dev.invoice.authoring.services.ApprovalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice/authoring/v1")
public class ApprovalController {

    private final ApprovalService _approvalService;

    public ApprovalController(ApprovalService approvalService) {
        _approvalService = approvalService;
    }

    @PostMapping(path = "/approvals")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse<Approval> create(@RequestBody final Approval request){
        return _approvalService.create(request);
    }
}
