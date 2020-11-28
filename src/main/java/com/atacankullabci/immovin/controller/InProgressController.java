package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.InProgressMap;
import com.atacankullabci.immovin.repository.InProgressMapRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://movemyplaylists.com", "https://movemyplaylists.com", "http://localhost:4200"})
public class InProgressController {

    private InProgressMapRepository inProgressMapRepository;

    public InProgressController(InProgressMapRepository inProgressMapRepository) {
        this.inProgressMapRepository = inProgressMapRepository;
    }

    @GetMapping("/progress")
    public ResponseEntity<Boolean> isUserInProgress(@RequestHeader("id") String id) {
        Optional<InProgressMap> inProgress = this.inProgressMapRepository.findById(id);
        if (inProgress.isPresent()) {
            return ResponseEntity.ok().body(inProgress.get().getInProgress());
        }
        return ResponseEntity.ok().body(false);
    }
}
