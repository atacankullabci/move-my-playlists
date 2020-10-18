package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.Client;
import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.repository.ClientRepository;
import com.atacankullabci.immovin.service.ClientService;
import com.atacankullabci.immovin.service.ObjectHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://imovin.club", "http://localhost:4200"})
public class FileController {

    private final ObjectHandler objectHandler;
    private ClientService clientService;
    private ClientRepository clientRepository;

    public FileController(ObjectHandler objectHandler, ClientService clientService, ClientRepository clientRepository) {
        this.objectHandler = objectHandler;
        this.clientService = clientService;
        this.clientRepository = clientRepository;
    }

    @PostMapping(value = "/map", consumes = "multipart/form-data")
    public ResponseEntity<List<MediaContent>> mapper(@RequestParam("file") MultipartFile libraryFile,
                                                     @RequestHeader("client-ip") String ip) {
        List<MediaContent> mediaContentList = null;
        try {
            mediaContentList = objectHandler.getMediaContentList(libraryFile.getBytes());
            this.clientRepository.save(new Client(ip, Instant.now()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(mediaContentList);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Client>> getAllList() {
        return ResponseEntity.ok().body(this.clientRepository.findAll());
    }
}
