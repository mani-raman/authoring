package dev.invoice.authoring.controllers;

import dev.invoice.authoring.models.Draft;
import dev.invoice.authoring.repositories.AuthoringRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoice/authoring/v1/drafts")
public class DraftController {
    private final AuthoringRepository authoringRepository;

    public DraftController(AuthoringRepository authoringRepository) {
        this.authoringRepository = authoringRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Draft create(@RequestBody final Draft request){
        return authoringRepository.createDraft(request);
    }

    @GetMapping("/pending")
    public List<Draft> findPendingByAuthor(@RequestParam(name = "filter.author") String author){
        return authoringRepository.findPendingDraftsByAuthor(author);
    }
}
