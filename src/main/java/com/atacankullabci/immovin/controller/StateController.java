package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.StateMap;
import com.atacankullabci.immovin.repository.StateMapRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://imovin.club", "https://imovin.club", "http://localhost:4200"})
public class StateController {

    private final StateMapRepository stateMapRepository;

    public StateController(StateMapRepository stateMapRepository) {
        this.stateMapRepository = stateMapRepository;
    }

    @GetMapping("/state")
    public ResponseEntity<String> getRandomState() {
        String uuid = UUID.randomUUID().toString();
        this.stateMapRepository.save(new StateMap(uuid, true));
        return ResponseEntity.ok(uuid);
    }
}
