package dev.invoice.authoring.controllers;

import dev.invoice.authoring.models.ApplicationResponse;
import dev.invoice.authoring.models.Draft;
import dev.invoice.authoring.repositories.AuthoringRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoice/authoring/v1")
public class DraftController {
    private final AuthoringRepository authoringRepository;

    public DraftController(AuthoringRepository authoringRepository) {
        this.authoringRepository = authoringRepository;
    }

    @PostMapping(path = "/drafts")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse create(@RequestBody final Draft request){

        var draft = authoringRepository.createDraft(request);

        return new ApplicationResponse<Draft>(draft);
    }

    @GetMapping("/pending")
    public ApplicationResponse findPendingByAuthor(@RequestParam(name = "filter.author") String author){
        var drafts = authoringRepository.findPendingDraftsByAuthor(author);

        return new ApplicationResponse<List<Draft>>(drafts);
    }
}
