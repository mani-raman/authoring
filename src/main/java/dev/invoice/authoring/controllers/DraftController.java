package dev.invoice.authoring.controllers;

import dev.invoice.authoring.models.ApplicationResponse;
import dev.invoice.authoring.models.Draft;
import dev.invoice.authoring.services.DraftService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice/authoring/v1")
public class DraftController {


    private final DraftService _draftService;

    public DraftController(DraftService draftService) {
        _draftService = draftService;
    }

    @PostMapping(path = "/drafts")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse create(@RequestBody final Draft request){
        return _draftService.create(request);
    }

    @GetMapping("/pending")
    public ApplicationResponse findPendingByAuthor(@RequestParam(name = "filter.author") String author){
        return _draftService.findPendingByAuthor(author);
    }
}
