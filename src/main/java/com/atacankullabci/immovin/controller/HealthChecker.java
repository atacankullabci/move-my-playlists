package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.service.ObjectHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:4200")
public class HealthChecker {

    private final ObjectHandler objectHandler;

    public HealthChecker(ObjectHandler objectHandler) {
        this.objectHandler = objectHandler;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/map")
    public ResponseEntity<List<MediaContent>> mapper(@RequestBody String libraryFile) {
        System.out.println(libraryFile);
        return (ResponseEntity<List<MediaContent>>) objectHandler.getMediaContentList();
    }
}
