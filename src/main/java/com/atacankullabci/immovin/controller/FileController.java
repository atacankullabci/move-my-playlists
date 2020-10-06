package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.service.ObjectHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:4200")
public class FileController {

    private final ObjectHandler objectHandler;

    public FileController(ObjectHandler objectHandler) {
        this.objectHandler = objectHandler;
    }

    @PostMapping("/map")
    public ResponseEntity<List<MediaContent>> mapper(@RequestBody String libraryFile) {
        List<MediaContent> mediaContentList = objectHandler.getMediaContentList(libraryFile);
        return ResponseEntity.ok().body(mediaContentList);
    }
}
