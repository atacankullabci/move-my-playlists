package com.atacankullabci.immovin.controller;

import com.atacankullabci.immovin.common.Client;
import com.atacankullabci.immovin.common.MediaContent;
import com.atacankullabci.immovin.common.User;
import com.atacankullabci.immovin.repository.ClientRepository;
import com.atacankullabci.immovin.repository.UserRepository;
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
    private ClientRepository clientRepository;
    private UserRepository userRepository;

    public FileController(ObjectHandler objectHandler, ClientRepository clientRepository, UserRepository userRepository) {
        this.objectHandler = objectHandler;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "/map", consumes = "multipart/form-data")
    public ResponseEntity<List<MediaContent>> mapper(@RequestParam("file") MultipartFile libraryFile,
                                                     @RequestHeader("client-ip") String ip,
                                                     @RequestHeader("username") String username,
                                                     @RequestHeader("external-url") String externalUrl) {
        List<MediaContent> mediaContentList = null;
        try {
            mediaContentList = objectHandler.getMediaContentList(libraryFile.getBytes());
            User user = this.userRepository.findByUsernameAndExternalUrl(username, externalUrl);
            user.setMediaContentList(mediaContentList);
            this.userRepository.save(user);
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
