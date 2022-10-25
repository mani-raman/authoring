package dev.invoice.authoring.controllers;

import dev.invoice.authoring.models.ApplicationResponse;
import dev.invoice.authoring.models.Draft;
import dev.invoice.authoring.repositories.AuthoringRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/invoice/authoring/v1/drafts")
public class DraftController {
    private final AuthoringRepository authoringRepository;

    public DraftController(AuthoringRepository authoringRepository) {
        this.authoringRepository = authoringRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse create(@RequestBody final Draft request){

        var draft = authoringRepository.createDraft(request);

        return new ApplicationResponse(draft, null, null, null);
    }

    @GetMapping("/pending")
    public ApplicationResponse findPendingByAuthor(@RequestParam(name = "filter.author") String author){
        var drafts = authoringRepository.findPendingDraftsByAuthor(author);

        return new ApplicationResponse(drafts, null, null, null);
    }
}
