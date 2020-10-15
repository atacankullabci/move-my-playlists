package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.service.ClientService;
import com.atacankullabci.immovin.service.ObjectHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "http://imovin.club")
public class FileController {

    private final ObjectHandler objectHandler;
    private ClientService clientService;

    public FileController(ObjectHandler objectHandler, ClientService clientService) {
        this.objectHandler = objectHandler;
        this.clientService = clientService;
    }

    @PostMapping(value = "/map", consumes = "multipart/form-data")
    public ResponseEntity<List<MediaContent>> mapper(@RequestParam("file") MultipartFile libraryFile) {
        List<MediaContent> mediaContentList = null;
        try {
            mediaContentList = objectHandler.getMediaContentList(libraryFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientService.getSpotifyJWTToken();
        return ResponseEntity.ok().body(mediaContentList);
    }
}
